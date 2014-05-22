/**
 * 文件：DefaultTrapHandler.java 
 * 功能：
 * 创建时间:Mar 20, 2008
 */
package cn.com.postel.da.server.comm.snmp;

import cn.com.postel.da.server.task.TrapRquest;

/**
 * Trap消息的默认处理类
 * @author suzhengkun
 *
 */
public class DefaultTrapHandler implements ITrapHandler
	{
		public int handleTrapPDU(TrapRquest pdu)
		{
			return 0;
		}
		
	}
