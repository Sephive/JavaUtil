package cn.com.postel.da.server.comm.snmp;

import org.snmp4j.PDU;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OctetString;

import cn.com.postel.da.server.comm.Request;

public class SnmpRequest extends Request
	{
		
		private PDU pdu = null;
		
		public static final String DEFAULT_COMMUNITY_NAME = "public";
		public static final byte[] DEFAULT_COMMUNITY_BNAME =  new OctetString("public").getValue();
		
		private int maxRequestMessageSize = 65535;
		
		private byte[] communityName = DEFAULT_COMMUNITY_BNAME;
		
		private int snmpVersion = SnmpConstants.version1;
		
		public byte[] getCommunityName()
		{
			return communityName;
		}

		public void setCommunityName(byte[] communityName)
		{
			this.communityName = communityName;
		}

		public int getMaxRequestMessageSize()
		{
			return maxRequestMessageSize;
		}

		public void setMaxRequestMessageSize(int maxRequestMessageSize)
		{
			this.maxRequestMessageSize = maxRequestMessageSize;
		}

		public int getSnmpVersion()
		{
			return snmpVersion;
		}

		public void setSnmpVersion(int snmpVersion)
		{
			this.snmpVersion = snmpVersion;
		}

		protected void encode()
		{
		}

		public PDU getPdu()
		{
			return pdu;
		}

		public void setPdu(PDU pdu)
		{
			this.pdu = pdu;
		}	
	}
