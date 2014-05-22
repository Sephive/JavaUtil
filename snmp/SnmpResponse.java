package cn.com.postel.da.server.comm.snmp;

import org.snmp4j.PDU;
import org.snmp4j.asn1.BERInputStream;

public class SnmpResponse 
	{
		private PDU request;
		
		private PDU response;
		
		private Object userObject;
		
		private Exception error;
		
		private BERInputStream bos ;

		/**
		 * ¹¹Ôìº¯Êý
		 * 
		 * @param request
		 * @param response
		 * @param userObject
		 * @param error
		 */
		public SnmpResponse(PDU request,PDU response,Object userObject,Exception error)
		{
			setRequest(request);
			setResponse(response);
			setUserObject(userObject);
			setError(error);
		}

		public BERInputStream getIutputStream()
		{
			return this.bos;
		}

		public Exception getError()
		{
			return error;
		}

		public void setError(Exception error)
		{
			this.error = error;
		}

		public PDU getRequest()
		{
			return request;
		}

		public void setRequest(PDU request)
		{
			this.request = request;
		}

		public PDU getResponse()
		{
			return response;
		}

		public void setResponse(PDU response)
		{
			this.response = response;
		}

		public Object getUserObject()
		{
			return userObject;
		}

		public void setUserObject(Object userObject)
		{
			this.userObject = userObject;
		}
	}
