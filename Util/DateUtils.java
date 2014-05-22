package cn.com.postel.da.server.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class DateUtils
	{
		
		
		/**
		 * util.date ת���� sql.date
		 * @param date
		 * @return
		 */
		public static java.sql.Date convert2SqlDate(java.util.Date date)
		{
			Assert.notNull(date);
			java.sql.Date sdate = new java.sql.Date(date.getTime());
			
			return sdate;
			
		}
		
		
		
		/**
		 * sql.Date ת���� util.Date
		 * @param date
		 * @return
		 */
		public static java.util.Date convert2UtilDate(java.sql.Date date)
		{
			Assert.notNull(date);
			java.util.Date udate = new java.util.Date(date.getTime());
			
			return udate;
		}
		
		
		
		/**
		 * ����ת����String
		 * @param date sql SQL��������
		 * @param pattern ���ڸ�ʽ �� "yyyy-MM-dd HH:mm:ss.SSS"
		 * @return
		 */
		public static String sqlDate2Str(java.sql.Date date, String pattern)
		{
			Assert.notNull(date);
			return utilDate2Str(convert2UtilDate(date), pattern);
		}
		
		
		
		/**
		 * ����ת����String
		 * @param date Util��������
		 * @param pattern ���ڸ�ʽ �� "yyyy-MM-dd HH:mm:ss.SSS"
		 * @return
		 */
		public static String utilDate2Str(java.util.Date date, String pattern)
		{
			Assert.notNull(date, "Date must not null!");
			Assert.notNull(pattern, "pattern must not null");
			SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.SIMPLIFIED_CHINESE);
			return df.format(date);
			
		}
		
		
		public static String currentDate()
		{
			return utilDate2Str(new java.util.Date(), "yyyy-MM-dd HH:mm:ss");
		}
		
		
		
		/**
		 * strת����sqldate
		 * @param strdate
		 * @param pattern
		 * @return
		 * @throws ParseException
		 */
		public static java.sql.Date str2SqlDate(String strdate, String pattern)
			throws ParseException
		{
			return convert2SqlDate(str2UtilDate(strdate, pattern));
		}
		
		
		
		/**
		 * strת����utilDate
		 * @param strdate
		 * @param pattern
		 * @return
		 * @throws ParseException
		 */
		public static java.util.Date str2UtilDate(String strdate, String pattern)
			throws ParseException
		{
			Assert.notNull(strdate, "StrDate must not null!");
			if (pattern == null)
			{
				pattern = "yyyy-MM-dd HH:mm:ss";
			}
			
			SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.SIMPLIFIED_CHINESE);
			
			return df.parse(strdate);
			
		}
	}
