/*
 * 该类操作注册表
 */
package cn.com.postel.da.client.common;

import java.util.prefs.Preferences;

public class Regedit
	{
		Preferences prefsdemo = Preferences.userRoot().node("/com/postel/da/client");
		
		
		private String LastUserName = "";
		
		
		private String LastPassword = "";
		
		
		
		public String getLastPassword()
		{
			return LastPassword;
		}
		
		
		public void setLastPassword(String lastPassword)
		{
			LastPassword = lastPassword;
		}
		
		
		public String getLastUserName()
		{
			return LastUserName;
		}
		
		
		public void setLastUserName(String lastUserName)
		{
			LastUserName = lastUserName;
		}
		
		
		public void writeLastUserName()
		{
			try
			{
				
				prefsdemo.put("LastUserName", LastUserName);
				
			}
			catch (Exception e)
			{
				System.err.println("无法写入注册表" + e);
			}
		}
		
		
		public void writelastPassword()
		{
			
			try
			{
				prefsdemo.put("LastPassword", LastPassword);
				
			}
			catch (Exception e)
			{
				System.err.println("无法写入注册表" + e);
			}
		}
		
		
		public void readLastUserName()
		{
			try
			{
				
				setLastUserName(prefsdemo.get("LastUserName", ""));
				
			}
			catch (Exception e)
			{
				System.err.println("无法读入注册表" + e);
			}
		}
		
		
		public void readlastPassword()
		{
			
			try
			{
				
				setLastPassword(prefsdemo.get("LastPassword", ""));
				
			}
			catch (Exception e)
			{
				System.err.println("无法读入注册表" + e);
				
			}
		}
		
		
		public static void main(String[] args)
		{
			Regedit regedit = new Regedit();
			regedit.setLastPassword("111111");
			regedit.setLastUserName("syw");
			regedit.writelastPassword();
			regedit.writeLastUserName();
		}
	}
