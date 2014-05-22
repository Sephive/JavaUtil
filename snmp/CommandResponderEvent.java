package cn.com.postel.da.server.comm.snmp;

import java.net.InetAddress;
import java.util.EventObject;

import org.snmp4j.PDU;
import org.snmp4j.mp.PduHandle;

import cn.com.postel.da.server.comm.DAServerConnection;

public class CommandResponderEvent extends EventObject
	{
		private static final long serialVersionUID = 5367388455349845092L;
		
		private InetAddress peerAddress;
		
		private PDU pdu;
		
		private transient DAServerConnection connection;
		
		private PduHandle handle;
		
		private byte[] securityName;
		
		private boolean processed;
		
		private int maxSizeResponsePDU;
		
		/**
		 * ¹¹Ôìº¯Êý
		 * 
		 * @param source
		 * @param peerAddress
		 * @param pdu
		 * @param connection
		 * @param handle
		 * @param securityName
		 * @param maxSizeResponsePDU
		 */
		public CommandResponderEvent(Object source, InetAddress peerAddress, PDU pdu, DAServerConnection connection, PduHandle handle, byte[] securityName, int maxSizeResponsePDU)
		{
			super(source);
			this.peerAddress = peerAddress;
			this.pdu = pdu;
			this.connection = connection;
			this.handle = handle;
			this.securityName = securityName;
			this.maxSizeResponsePDU = maxSizeResponsePDU;
		}

		public DAServerConnection getConnection()
		{
			return connection;
		}

		public void setConnection(DAServerConnection connection)
		{
			this.connection = connection;
		}

		public PduHandle getHandle()
		{
			return handle;
		}

		public void setHandle(PduHandle handle)
		{
			this.handle = handle;
		}

		public int getMaxSizeResponsePDU()
		{
			return maxSizeResponsePDU;
		}

		public void setMaxSizeResponsePDU(int maxSizeResponsePDU)
		{
			this.maxSizeResponsePDU = maxSizeResponsePDU;
		}

		public PDU getPDU()
		{
			return pdu;
		}

		public void setPDU(PDU pdu)
		{
			this.pdu = pdu;
		}

		public InetAddress getPeerAddress()
		{
			return peerAddress;
		}

		public void setPeerAddress(InetAddress peerAddress)
		{
			this.peerAddress = peerAddress;
		}

		public boolean isProcessed()
		{
			return processed;
		}

		public void setProcessed(boolean processed)
		{
			this.processed = processed;
		}

		public byte[] getSecurityName()
		{
			return securityName;
		}

		public void setSecurityName(byte[] securityName)
		{
			this.securityName = securityName;
		}
		
	}
