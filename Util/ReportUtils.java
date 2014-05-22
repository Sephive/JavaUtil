package cn.com.postel.da.client.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * �����ߣ� ������е����ݵ���
 * 
 * @author dzhang
 * 
 */
public class ReportUtils
	{
		
		private static final Pattern PDFSuffix = Pattern.compile(".*\\.pdf$",
																	Pattern.CASE_INSENSITIVE);
		
		
		private static final Pattern XLSSuffix = Pattern.compile(".*\\.xls$",
																	Pattern.CASE_INSENSITIVE);
		
		
		private static final Pattern DOCSuffix = Pattern.compile(".*\\.doc$",
																	Pattern.CASE_INSENSITIVE);
		
		
		private static final Pattern HTMLSuffix = Pattern.compile(".*\\.html$",
																	Pattern.CASE_INSENSITIVE);
		
		
		private static final Pattern PPTSuffix = Pattern.compile(".*\\.ppt$",
																	Pattern.CASE_INSENSITIVE);
		
		
		
		public static void main(String[] args)
		{
			List<Map<String, String>> dataset = new ArrayList<Map<String, String>>();
			
			String[] selectedColumnNames = { "����", "����", "����", "����", "��ͨ��", "�׻�" };
			
			for (int i = 0; i < 100; i++)
			{
				Map<String, String> data = new HashMap<String, String>();
				
				for (int j = 0; j < selectedColumnNames.length; j++)
				{
					String headerName = selectedColumnNames[j];
					data.put(headerName, (System.currentTimeMillis() / 1000) + " ʱ��");
				}
				dataset.add(data);
			}
			
			saveAndOpenReport(dataset, selectedColumnNames, "c:\\hell.xls", "δ����", true);
		}
		
		
		public static void saveTableAndOpenReport(Table table, String filename, String title,
			boolean open)
		{
			String[] selectedColumnNames = getColumnNames(table);
			List<Map<String, String>> dataset = getDataset(table, selectedColumnNames);
			saveAndOpenReport(dataset, selectedColumnNames, filename, title, open);
		}
		
		
		
		/**
		 * ����ͷ����ת��Ϊ��ͷ���ڵ�����λ��
		 * 
		 * @param table
		 *            ��
		 * @param selectedColumnNames
		 *            �Ѿ�ѡ�������б�ͷ
		 * 
		 * @return ��ѡ���ı�ͷ������λ��
		 */
		public static int[] convertTableHeaderName2Index(final Table table,
			final String[] selectedColumnNames)
		{
			
			final int[] indecies = new int[selectedColumnNames.length];
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					TableColumn[] columns = table.getColumns();
					for (int i = 0; i < selectedColumnNames.length; i++)
					{
						for (int j = 0; j < columns.length; j++)
						{
							TableColumn column = columns[j];
							
							if (column.getText().equals(selectedColumnNames[i]))
							{
								indecies[i] = j;
								break;
							}
						}
						
					}
				}
			});
			return indecies;
		}
		
		
		
		/**
		 * �����б�������ת��Ϊ Map �б���ֵ�ֱ�Ϊ��ͷ�Ͷ�Ӧ��ÿһ�еĵ�Ԫ��ֵ
		 * 
		 * @param table
		 *            ��ת���ı�
		 * @param selectedColumnNames
		 *            ѡ���ı�ͷ
		 * @return ѡ���е����ݼ�
		 */
		public static List<Map<String, String>> getDataset(final Table table,
			final String[] selectedColumnNames)
		{
			final List<Map<String, String>> dataset = new ArrayList<Map<String, String>>();
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					int[] indecies = convertTableHeaderName2Index(table, selectedColumnNames);
					TableItem[] items = table.getItems();
					for (int i = 0; i < items.length; i++)
					{
						TableItem item = items[i];
						
						Map<String, String> data = new HashMap<String, String>();
						for (int j = 0; j < indecies.length; j++)
						{
							String value = item.getText(indecies[j]);
							data.put(selectedColumnNames[j], value);
						}
						
						dataset.add(data);
					}
				}
			});
			return dataset;
		}
		
		
		public static List<Map<Integer, String>> getDataset(Table table, int[] indecies)
		{
			List<Map<Integer, String>> dataset = new ArrayList<Map<Integer, String>>();
			
			TableItem[] items = table.getItems();
			for (int i = 0; i < items.length; i++)
			{
				TableItem item = items[i];
				
				Map<Integer, String> data = new HashMap<Integer, String>();
				for (int j = 0; j < indecies.length; j++)
				{
					String value = item.getText(indecies[j]);
					data.put(indecies[j], value);
				}
				
				dataset.add(data);
			}
			return dataset;
		}
		
		
		public static String[] getColumnNames(final Table table)
		{
			final String[] headerNames = new String[table.getColumnCount()];
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					TableColumn[] columns = table.getColumns();
					for (int i = 0; i < columns.length; i++)
					{
						TableColumn column = columns[i];
						headerNames[i] = column.getText();
					}
				}
			});	
			return headerNames;
		}
		
		
		
		/**
		 * ��������ݵ���
		 * 
		 * @param dataset
		 *            ����Դ
		 * @param selectedColumnNames
		 *            �����б�
		 * @param filename
		 *            �ļ���
		 * @param title
		 *            ���ݵı�����
		 * @param open
		 *            �Ƿ���ļ�
		 */
		public static void saveAndOpenReport(List<Map<String, String>> dataset,
			String[] selectedColumnNames, String filename, String title, boolean open)
		{
			
			File file = new File(filename);
			filename = file.getAbsolutePath();
			
			if (PDFSuffix.matcher(filename).matches())
			{
				doSavePDF(dataset, selectedColumnNames, filename, title);
			}
			else if (XLSSuffix.matcher(filename).matches())
			{
				doSaveExcel(dataset, selectedColumnNames, filename, title);
			}
			else if (DOCSuffix.matcher(filename).matches())
			{
				doSaveWord(dataset, selectedColumnNames, filename, title);
			}
			else if (PPTSuffix.matcher(filename).matches())
			{
				doSavePowerPoint(dataset, selectedColumnNames, filename, title);
			}
			else if (HTMLSuffix.matcher(filename).matches())
			{
				doSaveHTML(dataset, selectedColumnNames, filename, title);
				
			}
			else
			{
				throw new RuntimeException("δ֪�ļ�����: " + filename);
			}
			
			if (open)
			{
				try
				{
					// filename = filename.replaceAll(" ", "\" \"");
					filename = "\"" + filename + "\"";
					Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler " + filename);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		
		
		/**
		 * ��������ݵ���ΪHTML�ļ�
		 * 
		 * @param dataset
		 *            ����Դ
		 * @param selectedColumnNames
		 *            �����б�
		 * @param filename
		 *            �ļ���
		 * @param title
		 *            ���ݵı�����
		 */
		public static void doSaveHTML(List<Map<String, String>> dataset,
			String[] selectedColumnNames, String filename, String title)
		{
			
		}
		
		
		
		/**
		 * ��������ݵ���ΪPowerPoint�ļ�
		 * 
		 * @param dataset
		 *            ����Դ
		 * @param selectedColumnNames
		 *            �����б�
		 * @param filename
		 *            �ļ���
		 * @param title
		 *            ���ݵı�����
		 */
		public static void doSavePowerPoint(List<Map<String, String>> dataset,
			String[] selectedColumnNames, String filename, String title)
		{
			
		}
		
		
		
		/**
		 * ��������ݵ���ΪWord�ļ�
		 * 
		 * @param dataset
		 *            ����Դ
		 * @param headerNames
		 *            �����б�
		 * @param selectedColumnNames
		 *            �ļ���
		 * @param title
		 *            ���ݵı�����
		 */
		public static void doSaveWord(List<Map<String, String>> dataset, String[] headerNames,
			String selectedColumnNames, String title)
		{
			
		}
		
		
		
		/**
		 * ��������ݵ���ΪExcel�ļ�
		 * 
		 * @param dataset
		 *            ����Դ
		 * @param selectedColumnNames
		 *            �����б�
		 * @param filename
		 *            �ļ���
		 * @param title
		 *            ���ݵı�����
		 */
		public static void doSaveExcel(List<Map<String, String>> dataset,
			String[] selectedColumnNames, String filename, String title)
		{
			HSSFWorkbook book = new HSSFWorkbook();
			HSSFSheet sheet = book.createSheet(title);
			sheet.setDefaultColumnWidth((short) (30));
			
			HSSFPalette palette = book.getCustomPalette();
			palette.setColorAtIndex((short) 9, (byte) 220, (byte) 230, (byte) 241);
			palette.setColorAtIndex((short) 10, (byte) 79, (byte) 129, (byte) 189);
			palette.setColorAtIndex((short) 11, (byte) 255, (byte) 255, (byte) 255);
			palette.setColorAtIndex((short) 12, (byte) 85, (byte) 135, (byte) 195);
			
			HSSFFont headerFont = book.createFont();
			HSSFFont cellFont = book.createFont();
			cellFont.setFontHeightInPoints((short) 14);
			cellFont.setFontName("����");
			headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			headerFont.setColor((short) 11);
			// headerFont.setFontHeight((short) 300);
			headerFont.setFontName("����");
			headerFont.setFontHeightInPoints((short) 16);
			
			HSSFCellStyle headerStyle = book.createCellStyle();
			headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			headerStyle.setFillForegroundColor((short) 12);
			// headerStyle.setFillBackgroundColor((short) 10);
			headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headerStyle.setFont(headerFont);
			headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			headerStyle.setBottomBorderColor((short) 10);
			headerStyle.setTopBorderColor((short) 10);
			headerStyle.setLeftBorderColor((short) 10);
			headerStyle.setRightBorderColor((short) 10);
			headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			
			HSSFRow header = sheet.createRow(0);
			header.setHeightInPoints(25f);
			for (int i = 0; i < selectedColumnNames.length; i++)
			{
				sheet.autoSizeColumn((short) i);
				String headerName = selectedColumnNames[i];
				HSSFCell cell = header.createCell((short) i);
				HSSFRichTextString value = new HSSFRichTextString(headerName);
				cell.setCellStyle(headerStyle);
				value.applyFont(headerFont);
				cell.setCellValue(value);
			}
			
			HSSFCellStyle oddRowCellStyle = book.createCellStyle();
			oddRowCellStyle.setFont(cellFont);
			oddRowCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			oddRowCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			oddRowCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			oddRowCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			oddRowCellStyle.setBottomBorderColor((short) 10);
			oddRowCellStyle.setTopBorderColor((short) 10);
			oddRowCellStyle.setLeftBorderColor((short) 10);
			oddRowCellStyle.setRightBorderColor((short) 10);
			
			HSSFCellStyle evenRowCellStyle = book.createCellStyle();
			evenRowCellStyle.setFont(cellFont);
			evenRowCellStyle.setFillForegroundColor((short) 9);
			evenRowCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			evenRowCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			evenRowCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			evenRowCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			evenRowCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			evenRowCellStyle.setBottomBorderColor((short) 10);
			evenRowCellStyle.setTopBorderColor((short) 10);
			evenRowCellStyle.setLeftBorderColor((short) 10);
			evenRowCellStyle.setRightBorderColor((short) 10);
			
			for (int i = 0; i < dataset.size(); i++)
			{
				Map<String, String> data = dataset.get(i);
				HSSFRow row = sheet.createRow(1 + i);// ��һ���Ѿ����˱�����
				row.setHeightInPoints(22f);
				
				for (int j = 0; j < selectedColumnNames.length; j++)
				{
					HSSFCell cell = row.createCell((short) j);
					
					
					// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					if (i % 2 == 0) cell.setCellStyle(evenRowCellStyle);
					else cell.setCellStyle(oddRowCellStyle);
					
					HSSFRichTextString value = new HSSFRichTextString(data
						.get(selectedColumnNames[j]));
					cell.setCellValue(value);
				}
			}
			
			try
			{
				FileOutputStream out = new FileOutputStream(filename);
				book.write(out);
				out.flush();
				out.close();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		/**
		 * ��������ݵ���ΪPDF�ļ�
		 * 
		 * @param dataset
		 *            ����Դ
		 * @param selectedColumnNames
		 *            �����б�
		 * @param filename
		 *            �ļ���
		 * @param title
		 *            ���ݵı�����
		 */
		public static void doSavePDF(List<Map<String, String>> dataset,
			String[] selectedColumnNames, String filename, String title)
		{
			
		}
	}
