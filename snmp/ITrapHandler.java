/**
 * �ļ���ITrapHandler.java 
 * ���ܣ�
 * ����ʱ��:Mar 20, 2008
 */
package cn.com.postel.da.server.comm.snmp;

import cn.com.postel.da.server.task.TrapRquest;

/**
 * Trap������ӿ�
 * @author suzhengkun
 */
public interface ITrapHandler
	{
		public int handleTrapPDU(TrapRquest request);
	}
