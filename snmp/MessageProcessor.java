/**
 * FileName: MessageProcessor.java 
 * CreateBy: suzhengkun 
 * CreateDate:Jun 13, 2007
 */
package cn.com.postel.da.server.comm.snmp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.snmp4j.MutablePDU;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.asn1.BEROutputStream;
import org.snmp4j.asn1.BER.MutableByte;
import org.snmp4j.mp.StateReference;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;

public class MessageProcessor
	{
		public final static int PROCESS_OK = 0;
		
		public static final int MPv1 = 0;
		
		public final static int ProcessingModel_V1 = 0;

		public int prepareOutgoingMessage(Address transportAddress,int maxMessageSize, int messageProcessingModel, int securityModel,byte[] securityName, 
			PDU pdu, boolean expectResponse,Address destTransportAddress, BEROutputStream outgoingMessage) throws IOException
		{
			OctetString community = new OctetString(securityName);
			Integer32 version = new Integer32(messageProcessingModel);
			int length = pdu.getBERLength();
			length += community.getBERLength();
			length += version.getBERLength();
			ByteBuffer buf = ByteBuffer.allocate(length + BER.getBERLengthOfLength(length) + 1);
			outgoingMessage.setBuffer(buf);

			BER.encodeHeader(outgoingMessage, BER.SEQUENCE, length);
			version.encodeBER(outgoingMessage);
			
			community.encodeBER(outgoingMessage);
			pdu.encodeBER(outgoingMessage);
			
			return PROCESS_OK;	
		}

		/**
		 * 对SNMP数据进行编码
		 * 
		 * @param maxMessageSize 最大消息长度
		 * @param communityName 组织名称 如 public
		 * @param pdu 消息体
		 * @param outgoingMessage 输出的消息内容
		 * @return  处理结果
		 * @throws IOException
		 */
		public int prepareOutgoingMessage(int maxMessageSize, int snmpVersion,byte[] communityName, PDU pdu, BEROutputStream outgoingMessage) throws IOException
		{
			OctetString community = new OctetString(communityName);
			Integer32 version = new Integer32(snmpVersion);
			int length = pdu.getBERLength();
			length += community.getBERLength();
			length += version.getBERLength();
			ByteBuffer buf = ByteBuffer.allocate(length + BER.getBERLengthOfLength(length) + 1);
			outgoingMessage.setBuffer(buf);

			BER.encodeHeader(outgoingMessage, BER.SEQUENCE, length);
			version.encodeBER(outgoingMessage);
			
			community.encodeBER(outgoingMessage);
			pdu.encodeBER(outgoingMessage);
			return PROCESS_OK;
		}
		
		
		public int prepareResponseMessage(int messageProcessingModel,int maxMessageSize, int securityModel, byte[] securityName,PDU pdu, 
			int maxSizeResponseScopedPDU,StateReference stateReference, StatusInformation statusInformation,BEROutputStream outgoingMessage) throws IOException
		{
			return prepareOutgoingMessage(stateReference.getAddress(),maxMessageSize, messageProcessingModel, securityModel,
				securityName, pdu, false, null, outgoingMessage);
		}
		
		/**
		 * 处理接收到的数据包SNMPV1 版本
		 * 
		 * @param wholeMsg 接收到的数据
		 * @param securityName 团体名称 "public"
		 * @param pdu
		 * @return
		 * @throws IOException
		 */
		public int prepareDataElements(BERInputStream wholeMsg,OctetString securityName, MutablePDU pdu) throws IOException
		{
			MutableByte mutableByte = new MutableByte();
			int length = BER.decodeHeader(wholeMsg, mutableByte);
			int startPos = (int) wholeMsg.getPosition();
			if (mutableByte.getValue() != BER.SEQUENCE)
			{
				String txt = "SNMPv1 PDU must start with a SEQUENCE";
				throw new IOException(txt);
			}
			Integer32 version = new Integer32();
			version.decodeBER(wholeMsg);
			
			securityName.decodeBER(wholeMsg);
			PDUv1 v1PDU = new PDUv1();
			pdu.setPdu(v1PDU);
			v1PDU.decodeBER(wholeMsg);
			
			BER.checkSequenceLength(length, (int) wholeMsg.getPosition()- startPos, v1PDU);
			
			return PROCESS_OK;	
		}
	}
