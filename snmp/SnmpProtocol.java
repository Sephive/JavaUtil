/**
 * 文件：SnmpProtocol.java 
 * 功能：
 * 创建时间:Dec 6, 2007
 */
package cn.com.postel.da.server.comm.snmp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.snmp4j.DefaultTimeoutModel;
import org.snmp4j.MutablePDU;
import org.snmp4j.PDU;
import org.snmp4j.TimeoutModel;
import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.asn1.BEROutputStream;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.PduHandle;
import org.snmp4j.mp.PduHandleCallback;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import cn.com.postel.da.server.comm.ConnectionTransListener;
import cn.com.postel.da.server.comm.DAServerConnection;
import cn.com.postel.da.server.exception.MessageException;
import cn.com.postel.da.server.util.CLogger;

/**
 * Snmp协议处理
 * 
 * @author suzhengkun
 * 
 */
public class SnmpProtocol implements ConnectionTransListener
	{
		private final static CLogger log = CLogger.getLogger("SnmpProtocol");
		private int snmpVersion = 0;
		private int nextTransactionID = new Random().nextInt(Integer.MAX_VALUE - 2) + 1;
		private static Timer timer = new Timer("snmpProtocol定时线程", true);
		private Hashtable pendingRequests = new Hashtable(5);
		private TimeoutModel timeoutModel = new DefaultTimeoutModel();
		private ReportHandler reportHandler = new ReportHandler();
		
		public SnmpProtocol(DAServerConnection connection)
		{
		}
		
		public int getSnmpVersion()
		{
			return this.snmpVersion;
		}
		
		/**
		 * 发送数据包
		 * 
		 * @param request 数据请求
		 * @param connection 通信连接
		 * @return 响应数据包
		 * @throws IOException
		 */
		public SnmpResponse sendPdu(SnmpRequest request, DAServerConnection connection) throws IOException
		{
			PDU pdu = request.getPdu();
			if (!pdu.isConfirmedPdu())// 是否需要接收响应
			{
				sendMessage(pdu, request.getMaxRequestMessageSize(), request.getSnmpVersion(),
							request.getCommunityName(), connection, null);
				return null;
			}
			connection.addTransListener(this);
			SyncResponseListener syncResponse = new SyncResponseListener();
			PendingRequest retryRequest = null;
			synchronized (syncResponse)
			{
				PduHandle handle = null;
				PendingRequest pendingRequest = new PendingRequest(syncResponse, request, pdu,request, connection);
				handle = sendMessage(pdu, request.getMaxRequestMessageSize(), request
					.getSnmpVersion(), request.getCommunityName(), connection, pendingRequest);
				try
				{
					syncResponse.wait();
					retryRequest = (PendingRequest) pendingRequests.remove(handle);
					if (log.isDebugEnabled())
					{
						log.debug("Removed pending request with handle: " + handle);
					}
					pendingRequest.setFinished();
					pendingRequest.cancel();
				}
				catch (InterruptedException iex)
				{
					// log.warn(iex);
					// ignore
				}
			}
			if (retryRequest != null)
			{
				synchronized (retryRequest)
				{
					retryRequest.setFinished();
					retryRequest.cancel();
				}
			}
			connection.removeTransListener(this);
			return syncResponse.response;
		}
		
		public void processMessage(DAServerConnection source, SocketAddress incomingAddr,
				ByteBuffer wholeMessage)
		{
			processMessage(source, incomingAddr, new BERInputStream(wholeMessage));
		}
		
		/**
		 * 
		 * @param source
		 * @param incomingAddr
		 * @param wholeMessage
		 */
		public void processMessage(DAServerConnection source, SocketAddress incomingAddr,
				BERInputStream wholeMessage)
		{
			if (!wholeMessage.markSupported())
			{
				String txt = "Message stream must support marks";
				log.error(txt);
				throw new IllegalArgumentException(txt);
			}
			try
			{
				wholeMessage.mark(16);
				BER.MutableByte type = new BER.MutableByte();
				BER.decodeHeader(wholeMessage, type, false);
				if (type.getValue() != BER.SEQUENCE)
				{
					log.error("ASN.1 parse error (message is not a sequence)");
				}
				Integer32 version = new Integer32();
				version.decodeBER(wholeMessage);
				wholeMessage.reset(); // 复位指针
				MessageProcessor mp = new MessageProcessor();
				OctetString securityName = new OctetString();
				MutablePDU resPdu = new MutablePDU();
				int status = mp.prepareDataElements(wholeMessage, securityName, resPdu);
				if (status == MessageProcessor.PROCESS_OK)
				{
					PduHandle handle = new PduHandle();
					handle.setTransactionID(resPdu.getPdu().getRequestID().getValue());
					PDU response = resPdu.getPdu();
					// 判断返回的PDU类型
					if (response.getType() == PDU.RESPONSE)
					{
						PendingRequest request;
						if (log.isDebugEnabled())
						{
							log.debug("Looking up pending request with handle " + handle);
						}
						request = (PendingRequest) pendingRequests.get(handle);
						if (request == null)
						{
							if (log.isDebugEnabled())
							{
								log.debug("不能找到该响应对应的请求句柄，丢弃响应包" + handle);
							}
							// 丢弃包是否要通知？
						}
						else
						{
							if (log.isDebugEnabled())
							{
								log.debug("校验响应数据成功，返回响应数据:");
								log.debug("PDU:" + resPdu.getPdu());
							}
							request.listener.onResponse(new ResponseEvent(this, null, request.pdu,resPdu.getPdu(), request.userObject));
						}
					}
					else if (response.getType() == PDU.REPORT)
					{
						reportHandler.processReport(handle, new CommandResponderEvent(this, null,
							response, source, handle, securityName.getValue(), 65536));
					}
					else
					{
						log.error("未知类型！！");
					}
				}
			}
			catch (Exception ex)
			{
				log.error(ex);
			}
		}
		
		/**
		 * 
		 * @param pdu
		 * @param target
		 * @param connection
		 * @param callback
		 * @return
		 * @throws MessageException
		 */
		protected PduHandle sendMessage(PDU pdu, SnmpRequest target, DASnmpConnection connection,
				PduHandleCallback callback) throws MessageException
		{
			return sendMessage(pdu, target.getMaxRequestMessageSize(), target.getSnmpVersion(),
								target.getCommunityName(), connection, callback);
		}
		
		/**
		 * 发送数据包
		 * 
		 * @param pdu 数据单元
		 * @param maxMessageSize 消息最大长度
		 * @param snmpVersion snmp版本
		 * @param communityName 团体名 OctString
		 * @param connection 通信连接
		 * @param callback 回调接口
		 * @return
		 * @throws IOException
		 */
		protected PduHandle sendMessage(PDU pdu, int maxMessageSize, int snmpVersion,
				byte[] communityName, DAServerConnection connection, PduHandleCallback callback)
				throws MessageException
		{
			if (connection == null)
			{
				log.error("连接未创建，Connection is null");
				throw new MessageException("Connection is null");
			}
			else if (pdu.isConfirmedPdu())
			{
				if (!checkListening4ConfirmedPDU(connection))
				{
					log.error("ConfirmedPDU 需要启动监听");
					throw new MessageException("Connection 连接未启动监听 Connection is not lisetening");
				}
			}
			PduHandle pduHandle;
			if ((pdu.getRequestID().getValue() == 0) && (pdu.getType() != PDU.RESPONSE))
			{
				pduHandle = createPduHandle();
			}
			else
			{
				pduHandle = new PduHandle(pdu.getRequestID().getValue());
			}
			pdu.setRequestID(new Integer32(pduHandle.getTransactionID()));
			try
			{
				BEROutputStream outgoingMessage = new BEROutputStream();
				MessageProcessor mp = new MessageProcessor();
				int status = mp.prepareOutgoingMessage(maxMessageSize, snmpVersion, communityName,
														pdu, outgoingMessage);
				if (status == MessageProcessor.PROCESS_OK)
				{
					if (callback != null)
					{
						callback.pduHandleAssigned(pduHandle, pdu);
					}
					byte[] messageBytes = outgoingMessage.getBuffer().array();
					if (connection != null)
					{
						connection.sendMessage(messageBytes);
					}
				}
				else
				{
					throw new MessageException("处理数据包编码出错，错误码:" + status);
				}
			}
			catch (IOException ex)
			{
				log.error("发送数据包错误：", ex);
				throw new MessageException(ex.getMessage(), ex);
			}
			return pduHandle;
		}
		
		/**
		 * 检查支持ConfirmedPdu 的连接是否启动了监听 ConfirmedPDU 即需要目标机器回应的PDU类型 启用连接时，可调用
		 * <code>connection.listen()</code>方法
		 * @param connection
		 * @return
		 */
		protected boolean checkListening4ConfirmedPDU(DAServerConnection connection)
		{
			return (connection != null && connection.isListening());
		}
		
		public synchronized int getNextRequestID()
		{
			int nextID = nextTransactionID++;
			if (nextID <= 0)
			{
				nextID = 1;
				nextTransactionID = 2;
			}
			return nextID;
		}
		
		protected PduHandle createPduHandle()
		{
			return new PduHandle(getNextRequestID());
		}
		
		static class SyncResponseListener implements ResponseListener
			{
				private SnmpResponse response = null;
				
				public synchronized void onResponse(ResponseEvent event)
				{
					this.response = new SnmpResponse(event.getRequest(), event.getResponse(), event
						.getUserObject(), event.getError());
					this.notify();
				}
				
				public SnmpResponse getResponse()
				{
					return response;
				}
			}
		
		class PendingRequest extends TimerTask implements PduHandleCallback
			{
				private PduHandle key;
				protected int retryCount;
				protected ResponseListener listener;
				protected Object userObject;
				protected PDU pdu;
				private int requestStatus = 0;
				private int maxRequestStatus = 2;
				private volatile boolean finished = false;
				protected SnmpRequest target;
				protected DAServerConnection connection;
				
				public PendingRequest(ResponseListener listener, Object userObject, PDU pdu,
					SnmpRequest target, DAServerConnection connection)
				{
					this.listener = listener;
					this.pdu = pdu;
					this.userObject = userObject;
					this.target = target;
					this.connection = connection;
					this.retryCount = target.getRetrytimes();
				}
				
				public PendingRequest(PendingRequest other)
				{
					this.userObject = other.userObject;
					this.listener = other.listener;
					this.pdu = other.pdu;
					this.target = other.target;
					this.requestStatus = other.requestStatus;
					this.retryCount = other.retryCount - 1;
					this.connection = other.connection;
				}
				
				public void run()
				{
					Thread.currentThread().setName("PendingRequest");
					try
					{
						if ((!finished) && (retryCount > 0))
						{
							try
							{
								PendingRequest nextRetry = new PendingRequest(this);
								if (log.isDebugEnabled())
								{
									log.debug("尝试重新发送数据包，尝试：" + (target.getRetrytimes() - retryCount));
								}
								sendMessage(pdu, target.getMaxRequestMessageSize(), target.getSnmpVersion(), target.getCommunityName(), connection,nextRetry);
							}
							catch (MessageException ex)
							{
								finished = true;
								log.error("发送数据包出错" + ex.getMessage());
								if (log.isDebugEnabled())
								{
									log.debug(ex);
								}
								// 通知响应监听事件
								listener.onResponse(new ResponseEvent(SnmpProtocol.this, null, pdu,null, userObject, ex));
							}
						}
						else if (!finished)
						{
							finished = true;
							pendingRequests.remove(key);
							// 通知响应监听事件
							listener.onResponse(new ResponseEvent(SnmpProtocol.this, null, pdu,null, userObject, new RetryTimeOutException("接收数据失败")));
						}
					}
					catch (RuntimeException ex)
					{
						log.error("系统运行时异常");
						log.error(ex);
						throw ex;
					}
					catch (Error er)
					{
						log.error("系统致命错误");
						log.error(er);
						throw er;
					}
				}
				
				/**
				 * 设置完成标记
				 * 
				 * @return
				 */
				public synchronized boolean setFinished()
				{
					boolean currentState = finished;
					this.finished = true;
					return currentState;
				}
				
				public void setMaxRepuestStatus(int maxRepuestStatus)
				{
					this.maxRequestStatus = maxRepuestStatus;
				}
				
				public int getMaxRepuestStatus()
				{
					return maxRequestStatus;
				}
				
				protected void registerRequest(PduHandle handle)
				{
				}
				
				public void pduHandleAssigned(PduHandle handle, Object pdu)
				{
					if (key == null)
					{
						key = handle;
						pendingRequests.put(handle, this);
						registerRequest(handle);
						long delay = timeoutModel.getRetryTimeout(target.getRetrytimes() - this.retryCount, target.getRetrytimes(), 
						                                          target.getRetrydelay() + target.getTimeout());
						if (!finished)
						{
							try
							{
								timer.schedule(this, delay);
							}
							catch (IllegalStateException e)
							{
								log.error("发送命令出错,Timer线程失效，重新初始化Timer");
								timer = null;
								synchronized (SnmpProtocol.class)
								{
									if (timer == null)
									{
										timer = new Timer(true);
									}
								}
								try
								{
									timer.schedule(this, delay);
								}
								catch (IllegalStateException e2)
								{
									log.error("发送命令出错，重新初始化Timer仍不成功！");
								}
							}
						}
					}
				}
			}
		
		class ReportHandler
			{
				public void processReport(PduHandle handle, CommandResponderEvent e)
				{
					PDU pdu = e.getPDU();
					log.debug("Searching pending request with handle" + handle);
					PendingRequest request = (PendingRequest) pendingRequests.get(handle);
					if (request == null)
					{
						log.warn("Unmatched report PDU received from " + e.getPeerAddress());
						return;
					}
					if (pdu.size() == 0)
					{
						log.error("Illegal report PDU received from " + e.getPeerAddress()
									+ " missing report variable binding");
						return;
					}
					VariableBinding vb = pdu.get(0);
					if (vb == null)
					{
						log.error("Received illegal REPORT PDU from " + e.getPeerAddress());
						return;
					}
					OID firstOID = vb.getOid();
					boolean resend = false;
					if (request.requestStatus < request.maxRequestStatus)
					{
						switch (request.requestStatus)
						{
							case 0:
								if (SnmpConstants.usmStatsUnknownEngineIDs.equals(firstOID))
								{
									resend = true;
								}
								else if (SnmpConstants.usmStatsNotInTimeWindows.equals(firstOID))
								{
									request.requestStatus++;
									resend = true;
								}
								break;
							case 1:
								if (SnmpConstants.usmStatsNotInTimeWindows.equals(firstOID))
								{
									resend = true;
								}
								break;
						}
					}
					if (resend)
					{
						log.debug("Send new request after report.");
						request.requestStatus++;
						try
						{
							PduHandle resentHandle = sendMessage(request.pdu, request.target,(DASnmpConnection) e.getConnection(), null);

							request.key = resentHandle;
						}
						catch (MessageException iox)
						{
							log.error("Failed to send message to " + request.target+ ": " + iox.getMessage());
							return;
						}
					}
					else
					{
						boolean intime;
						synchronized (request)
						{
							intime = request.cancel();
						}
						pendingRequests.remove(handle);
						if (intime)
						{
							ResponseEvent event = new ResponseEvent(this, null, request.pdu, pdu,
								request.userObject);
							request.listener.onResponse(event);
						}
						else
						{
							if (log.isInfoEnabled())
							{
								log.info("Received late report from " + e.getPeerAddress() + " with request ID " + pdu.getRequestID());
							}
						}
					}
				}
			}
	}
