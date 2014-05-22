package cn.com.postel.da.server.comm.snmp;

import java.io.IOException;

import cn.com.postel.da.server.comm.DAServerUDPConnection;
import cn.com.postel.da.server.core.DevProfile;

public class DASnmpConnection extends DAServerUDPConnection
	{
		public DASnmpConnection(DevProfile profile)throws IOException
		{
			super(profile);		
		}		
	}
