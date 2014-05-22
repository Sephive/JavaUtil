package cn.com.epatch.server.comm;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * 连接数据监听接口
 *
 */
public interface ConnectionTransListener
	{
		/**
		 * 
		 * @param source    	源通信连接
		 * @param incomingAddr	消息来源地址
		 * @param wholeMessage 	消息内容
		 */
		 void processMessage(DAServerConnection source,SocketAddress incomingAddr,ByteBuffer wholeMessage);
	}
