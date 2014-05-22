/**
 * 文件：TrapHandlerFactory.java 
 * 功能：
 * 创建时间:Mar 20, 2008
 */
package cn.com.postel.da.server.comm.snmp;

import java.util.Hashtable;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import cn.com.postel.da.server.bussiness.ServiceFactory;
import cn.com.postel.da.server.dao.ServerSystemDao;
import cn.com.postel.da.server.util.CLogger;
import cn.com.postel.da.server.util.ClassUtils;

/**
 * @author suzhengkun
 * 
 */
public class TrapHandlerFactory
	{
		private static final Hashtable<String, ITrapHandler>	oidTable		= new Hashtable<String, ITrapHandler>();
		
		private static final DefaultTrapHandler					DEFAULT_HANDLER	= new DefaultTrapHandler();
		
		private static final CLogger log = CLogger.getLogger(TrapHandlerFactory.class);
		
		public static ITrapHandler createTrapHandler(PDU trapPDU)
		{
			ITrapHandler handler = DEFAULT_HANDLER;
			
			if (trapPDU.size() == 0)
			{
				return null;
			}
			// 根据PDU中的OID类型创建对应的TrapHandler实现类
			VariableBinding vb = trapPDU.get(0);
			String oid = vb.getOid().toString();
//			System.err.println("oid = " + oid);
			if (oidTable.containsKey(oid))
			{
				return oidTable.get(oid);
			}
			
			ServerSystemDao dao = (ServerSystemDao) ServiceFactory.getService("serverSystemDao");
			
			String clsName = dao.getImplTrapHandlerByOID(oid);
			System.err.println("clsName = " + clsName);
			if (clsName == null)
			{
				oidTable.put(oid, DEFAULT_HANDLER);
				return DEFAULT_HANDLER;
			}
			
			try
			{
				Class cls = ClassUtils.forName(clsName);
				handler = (ITrapHandler) cls.newInstance();
				
			}
			catch (ClassNotFoundException e)
			{
				log.error("获取TrapHandler类["+ clsName+"]出错，未找到该类");
			}
			catch (LinkageError e)
			{
				log.error("获取TrapHandler类["+ clsName+"]出错，LinkageError错误");
			}
			catch (InstantiationException e)
			{
				log.error("获取TrapHandler类["+ clsName+"]出错，InstantiationException错误");
			}
			catch (IllegalAccessException e)
			{
				log.error("获取TrapHandler类["+ clsName+"]出错，IllegalAccessException错误");
			}
			finally
			{
				oidTable.put(oid, handler);
			}
			
			return handler;
		}
	}
