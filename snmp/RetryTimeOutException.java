/**
 * 文件：RetryTimeOutException.java 
 * 功能：
 * 创建时间:Apr 2, 2008
 */
package cn.com.postel.da.server.comm.snmp;

import cn.com.postel.da.server.exception.MessageException;

/**
 * @author suzhengkun
 *
 */
public class RetryTimeOutException extends MessageException
	{
		private static final long serialVersionUID = -2097220772120249895L;

		/**
		 *	构造函数
		 */
		public RetryTimeOutException()
		{
		}
		
		/**
		 * 构造函数
		 * 
		 * @param message
		 */
		public RetryTimeOutException(String message)
		{
			super(message);
		}
		
		/**
		 * 构造函数
		 * 
		 * @param message
		 * @param cause
		 */
		public RetryTimeOutException(String message, Throwable cause)
		{
			super(message, cause);
		}
		
	}
