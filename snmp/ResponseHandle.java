/**
 * FileName:  ResponseHandle.java
 * CreateBy:  suzhengkun
 * CreateDate:Jun 21, 2007
 */
package cn.com.postel.da.server.comm.snmp;

import org.snmp4j.MutablePDU;
import org.snmp4j.PDU;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;

import cn.com.postel.da.server.util.CLogger;

public class ResponseHandle
	{
		public static final int RESULT_ERROR = -1;
		
		public static final int RESULT_SUCCESS = 0;
		
		private SnmpResponse response = null;
		
		private CLogger log = CLogger.getLogger("ResponseHandle");
		
		private PDU pdu = null;
		
		private int requestID;
		
		private int result = 0;
		
		public int getResult()
		{
			return result;
		}
		
		public void setResult(int result)
		{
			this.result = result;
		}
		
		public ResponseHandle(SnmpResponse response, int requestID)
		{
			this.requestID = requestID;
			this.response = response;
			process();
		}
		
		public SnmpResponse getResponse()
		{
			return this.response;
		}
		
		public int getRequestID()
		{
			return this.requestID;
		}
		
		protected void process()
		{
			BERInputStream bis = response.getIutputStream();
		}
		
		public PDU getPDU()
		{
			return this.pdu;
		}
		
		public PDU processMessage(Address incomingAddress,BERInputStream wholeMessage)
		{
			PDU retpdu = null;
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
					System.err.println("ASN.1 parse error (message is not a sequence");
					log.error("ASN.1 parse error (message is not a sequence)");
					this.result = RESULT_ERROR;
				}
				Integer32 version = new Integer32();
				version.decodeBER(wholeMessage);
				
				// ¥¶¿ÌœÏ”¶
				wholeMessage.reset();
			}
			catch (Exception ex)
			{
				log.error(ex);
				this.result = RESULT_ERROR;
				if (SNMP4JSettings.isFowardRuntimeExceptions()) { throw new RuntimeException(
					ex); }
			}
			catch (OutOfMemoryError oex)
			{
				log.error(oex);
				this.result = RESULT_ERROR;
			}
			return retpdu;
		}
	}
