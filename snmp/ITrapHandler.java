/**
 * 文件：ITrapHandler.java 
 * 功能：
 * 创建时间:Mar 20, 2008
 */
package cn.com.postel.da.server.comm.snmp;

import cn.com.postel.da.server.task.TrapRquest;

/**
 * Trap包处理接口
 * @author suzhengkun
 */
public interface ITrapHandler
	{
		public int handleTrapPDU(TrapRquest request);
	}
