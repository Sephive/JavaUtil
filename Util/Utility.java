package cn.com.postel.da.server.util;

public class Utility
	{
		private Utility()
		{
		}
		
		public static String decodeIPAddress(byte[] bytIpAddress)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = bytIpAddress.length - 1; i >= 0; i--)
			{
				sb.append(bytIpAddress[i] & 0xff).append(".");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
		
		public static String toHexString(int value)
		{
			String str = Integer.toHexString(value);
			int p = 4 - str.length();
			for (int i = 0; i < p; i++)
			{
				str = "0" + str;
			}
			return "0x" + str.toUpperCase();
		}
		
		/**
		 * 
		 * @param hex
		 *            0x开头的十六进制数据，必须是在int范围
		 * @return
		 */
		public static int intFromHexStr(String hex)
		{
			Integer val = Integer.decode(hex);
			return val.intValue();
		}
		
		public static int reverseInt(int val)
		{
			byte[] bytes = new byte[] { (byte) (val & 0x000000ff),
										(byte) ((val & 0x0000ff00) >> 8),
										(byte) ((val & 0x00ff0000) >> 16), (byte) (val >> 24) };
			int ret = bytes[0] << 24;
			ret = ret | bytes[1] << 16;
			ret = ret | bytes[2] << 8;
			ret = ret | bytes[3];
			return ret;
		}
		
		/**
		 * 
		 * @param bytes
		 * @param mode
		 *            true 高位在前 false 高位在后
		 * @return
		 */
		public static int bytes2Int(byte[] bytes, boolean mode)
		{
			if (bytes.length > 4) throw new IllegalArgumentException("bytes长度超出int范围");
			int ret = 0;
			int tmp = 0;
			for (int i = 0; i < bytes.length; i++)
			{
				if (mode)
				{
					ret = ret + (tmp | bytes[i]) << 8 * (bytes.length - i - 1);
				}
				else
				{
					ret = ret + ((tmp | bytes[i]) << 8 * i);
				}
			}
			return ret;
		}
		
		/**
		 * 返回int类型 低位在前
		 * 
		 * @param bytes
		 * @return
		 */
		public static int bytes2Int(byte[] bytes)
		{
			return bytes2Int(bytes, false);
		}
		
		public static void main(String[] args)
		{
			System.out.println(":" + reverseInt(131072));
		}
	}
