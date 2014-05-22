/**
 * �ļ���StringUtils.java 
 * ���ܣ�
 * ����ʱ��:Dec 12, 2007
 */
package cn.com.postel.da.server.util;

/**
 * �ַ���������
 * 
 * @author suzhengkun
 * 
 */
public abstract class StringUtils
	{
		private static final String FOLDER_SEPARATOR = "/";
		
		
		private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
		
		
		private static final String TOP_PATH = "..";
		
		
		private static final String CURRENT_PATH = ".";
		
		
		private static final char EXTENSION_SEPARATOR = '.';
		
		
		
		public static boolean hasLength(String str)
		{
			return (str != null && str.length() > 0);
		}
		
		
		
		/**
		 * �ж��Ƿ�����Ч����
		 * <p>
		 * 
		 * <pre>
		 *    StringUtils.hasText(null) = false
		 *    StringUtils.hasText(&quot;&quot;) = false
		 *    StringUtils.hasText(&quot; &quot;) = false
		 *    StringUtils.hasText(&quot;12345&quot;) = true
		 *    StringUtils.hasText(&quot; 12345 &quot;) = true
		 * </pre>
		 * 
		 * @param str
		 * @return
		 */
		public static boolean hasText(String str)
		{
			if (!hasLength(str)) { return false; }
			int strLen = str.length();
			for (int i = 0; i < strLen; i++)
			{
				if (!Character.isWhitespace(str.charAt(i))) { return true; }
			}
			return false;
		}
		
		
		
		/**
		 * �ж��Ƿ��пո�
		 * 
		 * @param str
		 * @return
		 */
		public static boolean containsWhitespace(String str)
		{
			if (!hasLength(str)) { return false; }
			int strLen = str.length();
			for (int i = 0; i < strLen; i++)
			{
				if (Character.isWhitespace(str.charAt(i))) { return true; }
			}
			return false;
		}
		
		
		
		/**
		 * ȥ��ͷβ�ո�
		 * 
		 * @param str
		 * @return
		 */
		public static String trimWhitespace(String str)
		{
			if (!hasLength(str)) { return str; }
			StringBuffer buf = new StringBuffer(str);
			while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0)))
			{
				buf.deleteCharAt(0);
			}
			while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1)))
			{
				buf.deleteCharAt(buf.length() - 1);
			}
			return buf.toString();
		}
		
		
		
		/**
		 * ȥ��ͷ�ո�
		 * 
		 * @param str
		 * @return
		 */
		public static String trimLeadingWhitespace(String str)
		{
			if (!hasLength(str)) { return str; }
			StringBuffer buf = new StringBuffer(str);
			while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0)))
			{
				buf.deleteCharAt(0);
			}
			return buf.toString();
		}
		
		
		
		/**
		 * ȥ��β�ո�
		 * 
		 * @param str
		 * @return
		 */
		public static String trimTrailingWhitespace(String str)
		{
			if (!hasLength(str)) { return str; }
			StringBuffer buf = new StringBuffer(str);
			while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1)))
			{
				buf.deleteCharAt(buf.length() - 1);
			}
			return buf.toString();
		}
		
		
		
		/**
		 * ȥ�����пո񣬰����ַ����м�Ŀո�
		 * 
		 * @param str
		 * @return
		 */
		public static String trimAllWhitespace(String str)
		{
			if (!hasLength(str)) { return str; }
			StringBuffer buf = new StringBuffer(str);
			int index = 0;
			while (buf.length() > index)
			{
				if (Character.isWhitespace(buf.charAt(index)))
				{
					buf.deleteCharAt(index);
				}
				else
				{
					index++;
				}
			}
			return buf.toString();
		}
		
		
		
		/**
		 * �ַ���ƥ�俪ʼ���֣����Դ�Сд
		 * 
		 * @param str
		 * @param prefix
		 * @return
		 */
		public static boolean startsWithIgnoreCase(String str, String prefix)
		{
			if (str == null || prefix == null) { return false; }
			if (str.startsWith(prefix)) { return true; }
			if (str.length() < prefix.length()) { return false; }
			String lcStr = str.substring(0, prefix.length()).toLowerCase();
			String lcPrefix = prefix.toLowerCase();
			return lcStr.equals(lcPrefix);
		}
		
		
		
		/**
		 * �ַ���ƥ���β���֣����Դ�Сд
		 * 
		 * @param str
		 * @param suffix
		 * @return
		 */
		public static boolean endsWithIgnoreCase(String str, String suffix)
		{
			if (str == null || suffix == null) { return false; }
			if (str.endsWith(suffix)) { return true; }
			if (str.length() < suffix.length()) { return false; }
			
			String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
			String lcSuffix = suffix.toLowerCase();
			return lcStr.equals(lcSuffix);
		}
		
		
		
		/**
		 * ���ַ���������
		 * 
		 * @param str
		 * @return
		 */
		public static String quote(String str)
		{
			return (str != null ? "'" + str + "'" : null);
		}
		
		
		
		/**
		 * ���ַ��������ţ�������ַ���
		 * 
		 * @param obj
		 * @return
		 */
		public static Object quoteIfString(Object obj)
		{
			return (obj instanceof String ? quote((String) obj) : obj);
		}
		
		/**
		 * ȥ���޶��� ���� "this.name.is.qualified" --> qualified
		 * 
		 * @param qualifiedName
		 * @return
		 */
		public static String unqualify(String qualifiedName)
		{
			return unqualify(qualifiedName, '.');
		}
		
		/**
		 * ȥ��ָ���޶����ŵ��޶��� 
		 * ���磺 "this:name:is:qualified" -> qualified
		 * 
		 * @param qualifiedName
		 * @param separator
		 * @return
		 */
		public static String unqualify(String qualifiedName, char separator)
		{
			return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
		}
		
		/**
		 * ת������ĸ��д
		 * ����
		 * user -> User
		 * @param str
		 * @return
		 */
		public static String capitalize(String str)
		{
			return changeFirstCharacterCase(str, true);
		}
		
		public static String uncapitalize(String str)
		{
			return changeFirstCharacterCase(str, false);
		}
		
		
		private static String changeFirstCharacterCase(String str, boolean capitalize)
		{
			if (str == null || str.length() == 0) { return str; }
			StringBuffer buf = new StringBuffer(str.length());
			if (capitalize)
			{
				buf.append(Character.toUpperCase(str.charAt(0)));
			}
			else
			{
				buf.append(Character.toLowerCase(str.charAt(0)));
			}
			buf.append(str.substring(1));
			return buf.toString();
		}
		
		/**
		 * ���ļ�·���л���ļ�����
		 * @param path
		 * @return
		 */
		public static String getFilename(String path) {
			if (path == null) {
				return null;
			}
			int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
			return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
		}
		/**
		 * ���ļ�ȫ·������ļ���չ��
		 * @param path
		 * @return
		 */
		public static String getFilenameExtension(String path) {
			if (path == null) {
				return null;
			}
			int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
			return (sepIndex != -1 ? path.substring(sepIndex + 1) : null);
		}
		
		/**
		 * ȥ���ļ���չ��
		 * ���磺
		 * <pre>
		 * "/usr/tmp/file2.txt" ->"/usr/tmp/file2"
		 * </pre>
		 * @param path
		 * @return
		 */
		public static String stripFilenameExtension(String path) {
			if (path == null) {
				return null;
			}
			int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
			return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
		}
		
		
		public static String[] split(String toSplit, String delimiter) {
			if (!hasLength(toSplit) || !hasLength(delimiter)) {
				return null;
			}
			int offset = toSplit.indexOf(delimiter);
			if (offset < 0) {
				return null;
			}
			String beforeDelimiter = toSplit.substring(0, offset);
			String afterDelimiter = toSplit.substring(offset + delimiter.length());
			return new String[] {beforeDelimiter, afterDelimiter};
		}
		
	}
