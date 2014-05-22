/**
 * 系统常用方法
 * @author zhangyunpeng
 */
package cn.com.postel.da.client.business;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.RECT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

import cn.com.postel.da.client.business.netmgr.NetWork;
import cn.com.postel.da.client.business.netmgr.NetcellNode;
import cn.com.postel.da.client.business.netmgr.SubNetWork;
import cn.com.postel.da.client.business.netmgr.TreeManage;
import cn.com.postel.da.client.common.ITreeNode;
import cn.com.postel.da.client.common.ResGetClass;
import cn.com.postel.da.client.common.SWTResourceManager;
import cn.com.postel.da.server.modules.common.data.Constants;
import cn.com.postel.da.server.modules.common.data.DClient;
import cn.com.postel.da.server.modules.common.data.DConstantCode;
import cn.com.postel.da.server.modules.common.data.DNetCell;
import cn.com.postel.da.server.modules.common.data.DNetCellBoard;
import cn.com.postel.da.server.modules.common.data.DNetCellBoardType;
import cn.com.postel.da.server.modules.common.data.DNetCellType;
import cn.com.postel.da.server.modules.common.data.DOperationObjects;
import cn.com.postel.da.server.modules.common.data.DViewFilterConfig;
import cn.com.postel.da.server.util.CLogger;
import cn.com.postel.da.server.util.RandomGUID;

@SuppressWarnings("unchecked")
public class ClientCommon
	{
		private ClientCommon()
		{
			
		}
		
		public static ImageRegistry imageRegistry = new ImageRegistry();
		
		/**
		 * 默认背景色
		 */
		public static final Color DefaultBackgroundColor = new Color(Display.getDefault(),
			ConstantDefine.ViewFormColor.R, ConstantDefine.ViewFormColor.G,
			ConstantDefine.ViewFormColor.B);
		
		/**
		 * 表格默认行高
		 */
		public static final int DefaultTableHeight = 23;
		
		/**
		 * 弹出窗口居中
		 * 
		 * @param shell
		 */
		public static void setCenter(Shell shell)
		{
			Rectangle rtg = shell.getMonitor().getClientArea();
			int width = rtg.width;
			int height = rtg.height;
			int x = shell.getSize().x;
			int y = shell.getSize().y;
			Point p = new Point((width - x) / 2, (height - y) / 2);
			shell.setLocation(p);
		}
		
		/**
		 * 设置Shell相对某个Shell居中，如果参照shell为null，则相对屏幕居中
		 * 
		 * @param relative
		 *            参照shell
		 * @param self
		 *            待设置居中的shell
		 */
		public static void setLocationRelative(Shell relative, Shell self)
		{
			if (relative == null)
			{
				setCenter(self);
				return;
			}
			
			Rectangle relativeRect = relative.getBounds();
			Rectangle area = self.getBounds();
			
			int xOffset = (relativeRect.width - area.width) / 2;
			int yOffset = (relativeRect.height - area.height) / 2;
			
			Point location = new Point(Math.max(0, xOffset + relativeRect.x),
				Math.max(0, yOffset + relativeRect.y));
			self.setLocation(location);
			
		}
		
		/**
		 * 给指定的表格的指定列绘制操作按钮
		 * 
		 * @param gc
		 *            系统传递过来的画笔
		 * @param table
		 *            表格
		 * @param columnIndcies
		 *            列索引
		 */
		public static void drawButtonColumn(GC gc, Table table, int... columnIndcies)
		{
			TableItem[] items = table.getItems();
			
			for (int i = 0; i < items.length; i++)
			{
				TableItem item = items[i];
				
				for (int j = 0; j < columnIndcies.length; j++)
				{
					int columnIndex = columnIndcies[j];
					Rectangle rect = item.getBounds(columnIndex);
					String text = item.getText(columnIndex);
					rect.width = rect.width - 3;
					rect.x = rect.x + 1;
					rect.height = rect.height - 3;
					rect.y = rect.y + 1;
					gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
					// gc.fillRoundRectangle(rect.x + 1, rect.y + 1,
					// rect.width - 2,
					// rect.height - 2, 5, 5);
					gc.fillRectangle(rect);
					gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					gc.drawRectangle(rect);
					// gc.drawRoundRectangle(rect.x + 1, rect.y + 1,
					// rect.width - 2,
					// rect.height - 2, 5, 5);
					gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
					int offset = gc.getFontMetrics().getAverageCharWidth() * text.length() * 2;
					int height = gc.getFontMetrics().getHeight();
					// gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
					gc.drawString(	text, rect.x + (rect.width - offset) / 2,
									rect.y + (rect.height - height) / 2);
				}
			}
		}
		
		/**
		 * 设置指定表格，在指定的点，鼠标变成手型
		 * 
		 * @param position
		 *            坐标位置，一般为鼠标所在的位置
		 * @param table
		 *            表格
		 * @param columnIndcies
		 *            要变成手型的列
		 */
		public static void setCellHandCursorAtPoint(Point position, Table table,
			int... columnIndcies)
		{
			TableItem item = table.getItem(position);
			if (item == null)
			{
				table.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
				
				return;
			}
			for (int i : columnIndcies)
			{
				Rectangle execRect = item.getBounds(i);
				
				if (execRect.contains(position))
				{
					table.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
					break;
				}
				else table.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
			}
			
		}
		
		/**
		 * 系统时间和日期
		 * 
		 * @return
		 */
		public static String getCurTimeDateByString()
		{
			
			String time;
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			time = formater.format(new Date());
			
			return time;
		}
		
		/**
		 * 让所有表格的列自动适应内容宽度
		 * 
		 * @param table
		 *            待调整的表格
		 */
		public static void packAllColumn(Table table)
		{
			if (table != null)
			{
				TableColumn[] columns = table.getColumns();
				for (TableColumn column : columns)
				{
					column.pack();
				}
			}
		}
		
		/**
		 * 系统时间
		 * 
		 * @return
		 */
		public static String getCurClockDateByString()
		{
			
			String time;
			SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
			
			time = formater.format(new Date());
			
			return time;
		}
		
		/**
		 * 时间转秒
		 * 
		 * @param datetime
		 * @return
		 */
		public static int timeToSec(Date datetime)
		{
			int result = 0;
			long l = datetime.getTime() / 1000;
			result = (int) l;
			return result;
		}
		
		/**
		 * 秒转时间
		 * 
		 * @param time
		 * @return
		 */
		public static Date timeToSec(int time)
		{
			long ltime = (long) time;
			return new Date(ltime * 1000);
			
		}
		
		/**
		 * 系统IP地址
		 * 
		 * @return
		 */
		public static String getCurLocalIPAddress()
		{
			InetAddress myIPaddress = null;
			try
			{
				myIPaddress = InetAddress.getLocalHost();
			}
			catch (UnknownHostException e)
			{
				return "";
			}
			
			return myIPaddress.getHostAddress();
			
		}
		
		/**
		 * 系统主机名
		 * 
		 * @return
		 */
		public static String getCurLocalName()
		{
			InetAddress myIPaddress = null;
			try
			{
				myIPaddress = InetAddress.getLocalHost();
			}
			catch (UnknownHostException e)
			{
				return "";
			}
			
			return myIPaddress.getHostName();
			
		}
		
		/**
		 * 子网ID找子网名称
		 * 
		 * @param SubID
		 * @return
		 */
		public static String getSubNetWorkName(String SubID)
		{
			TreeItem item = TreeManage.getInstance().getCurTreeItem(SubID);
			if (item != null)
			{
				return item.getText();
			}
			else return "";
		}
		
		/**
		 * 网元图标
		 * 
		 * @param netcell
		 * @return
		 */
		public static Image getNetCellIcon(DNetCell netcell)
		{
			Image icon = null;
			ResGetClass resGetClass = ResGetClass.getInstance();
			switch (netcell.getStatus())
			{
			case Constants.NetCellStatuID.NORMAL:
				if (netcell.getNodeType() == 0)
				{
					if (netcell.getNetGateType() == 0)
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM)// ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-30
						{
							icon = resGetClass.treeImage[ConstantDefine.TreeImage.M_MSAPZHENGCHANG];
						}
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						{
							icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDN];
						}
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G)
						{
							icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GN];
						}
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02)
						{
							icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSN];
						}
						
					}
					else
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.NORMALMSAP02];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE) 
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDN];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GN];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSN];
					}
					
				}
				else
				{
					icon = resGetClass.treeImage[ConstantDefine.TreeImage.NORMALREMOTE];
				}
				break;
			case Constants.NetCellStatuID.ALARM:
				if (netcell.getNodeType() == 0)
				{
					if (netcell.getNetGateType() == 0)
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.M_MSAPGAOJING];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE) 
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDA];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GA];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSA];
					}
					else
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ALARMMSAP02];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDA];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GA];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSA];
					}
					
				}
				else
				{
					icon = resGetClass.treeImage[ConstantDefine.TreeImage.ALARMREMOTE];
				}
				break;
			case Constants.NetCellStatuID.FAIL:
				if (netcell.getNodeType() == 0)
				{
					if (netcell.getNetGateType() == 0)
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.M_MSAPZHONGDUAN];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDF];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GF];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSF];
					}
					else
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.FAILMSAP02];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE) icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDF];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GF];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSF];
					}
					
				}
				else
				{
					icon = resGetClass.treeImage[ConstantDefine.TreeImage.FAILREMOTE];
				}
				break;
			case Constants.NetCellStatuID.UNWATCH:
				if (netcell.getNodeType() == 0)
				{
					if (netcell.getNetGateType() == 0)
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.M_MSAPWEIJIERU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSU];
					}
					else
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.UNCONMSAP02];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE// ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSU];
					}
					
				}
				else
				{
					icon = resGetClass.treeImage[ConstantDefine.TreeImage.UNCONREMOTE];
				}
				break;
			case Constants.NetCellStatuID.ELLCONFIG:
				if (netcell.getNodeType() == 0)
				{
					if (netcell.getNetGateType() == 0)
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.M_MSAPPEIZHICUOWU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSU];
					}
					else
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ERRORMSAP02];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSU];
					}
					
				}
				else
				{
					icon = resGetClass.treeImage[ConstantDefine.TreeImage.ERRORREMOTE];
				}
				break;
			case Constants.NetCellStatuID.INITSTATUS:
				if (netcell.getNodeType() == 0)
				{
					if (netcell.getNetGateType() == 0)
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.M_MSAPCHUSHI];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDI];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GI];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSI];
					}
					else
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.MSAP02I];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDI];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GI];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSI];
					}
					
				}
				else
				{
					icon = resGetClass.treeImage[ConstantDefine.TreeImage.REMOTEI];
				}
				break;
			case Constants.NetCellStatuID.COMRESUM:
				if (netcell.getNodeType() == 0)
				{
					if (netcell.getNetGateType() == 0)
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.M_MSAPHUIFU];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDCOMFAILR];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GCOMFAILR];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSCOMFAILR];
					}
					else
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.MSAP02COMFAILR];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDCOMFAILR];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GCOMFAILR];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSCOMFAILR];
					}
					
				}
				else
				{
					icon = resGetClass.treeImage[ConstantDefine.TreeImage.REMOTECOMFAILR];
				}
				break;
			
			default:
				if (netcell.getNodeType() == 0)
				{
					if (netcell.getNetGateType() == 0)
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.M_MSAPZHENGCHANG];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE// ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE) 
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDN];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GN];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSN];
					}
					else
					{
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM) // ADD
																									// BY
																									// ZOUXIANG
																									// 2012-3-30
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.NORMALMSAP02];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE // ADD
																								// BY
																								// ZOUXIANG
																								// 2012-3-28
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM // 2012-8-8
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_ECS2CS25M1_FXFE)
						icon = resGetClass.treeImage[ConstantDefine.TreeImage.ECPACDN];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G) icon = resGetClass.treeImage[ConstantDefine.TreeImage.GF806GN];
						if (netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS
							|| netcell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02) icon = resGetClass.treeImage[ConstantDefine.TreeImage.S03GSN];
					}
					
				}
				else
				{
					icon = resGetClass.treeImage[ConstantDefine.TreeImage.NORMALREMOTE];
				}
				break;
			}
			
			if (netcell.getNetCellTypeId() == 10000)// 虚拟网元
			{
				icon = resGetClass.treeImage[ConstantDefine.TreeImage.VirtualNE];
			}
			
			Image iconimg = new Image(Display.getCurrent(), ConstantDefine.TopoLogoSize.WIDTH,
				ConstantDefine.TopoLogoSize.HIGHT);
			
			GC gc = new GC(iconimg);
			if (icon != null) gc.drawImage(icon, 0, 0);
			
			int iStartX = 0;
			if (netcell.getLoopStatus() != Constants.LoopType.NOLOOP)
			{
				gc.drawImage(resGetClass.treeImage[ConstantDefine.TreeImage.STATELOOP], iStartX, 0);
			}
			
			iStartX += 11;
			if (netcell.getMaskStatusID() == Constants.MASKTYPE.MASK)
			{
				gc.drawImage(resGetClass.treeImage[ConstantDefine.TreeImage.STATEMASK], iStartX, 0);
			}
			
			iStartX += 11;
			if (netcell.getBiChanged() == Constants.TOPOCHANGE.CHANGE)
			{
				gc.drawImage(	resGetClass.treeImage[ConstantDefine.TreeImage.STATETOPOCHANGE],
								iStartX, 0);
			}
			gc.dispose();
			
			Image clarity = setImageClarity(iconimg);
			iconimg.dispose();
			iconimg = null;
			
			return clarity;
			
		}
		
		/**
		 * 使图片透明
		 * 
		 * @param image
		 * @return
		 */
		public static Image setImageClarity(Image image)
		{
			
			final ImageData imageData = image.getImageData();
			
			try
			{
				int dataLength = imageData.data.length;
				if (imageData.data != null)
				{
					// iImageType 判断图片是16色的
					// 还是高于16色，当是16色时候data区用2个字节表示一个象素点,高于16色均是4个
					int iImageType = imageData.bytesPerLine / imageData.width;
					if (iImageType == 2)
					{
						for (int j = 0; j < imageData.height; j++)
						{
							for (int i = 0; i < imageData.width; i++)
							{
								
								imageData.setAlpha(i, j, 255);
								if (((j * imageData.height + i) * iImageType) < dataLength
									&& ((j * imageData.height + i) * iImageType + 1) < dataLength)
								{
									if (imageData.data[(j * imageData.height + i) * iImageType] == -1
										&& imageData.data[(j * imageData.height + i) * iImageType
															+ 1] == 127)
									{
										imageData.setAlpha(i, j, 0);
									}
								}
								
							}
						}
					}
					else
					{
						for (int j = 0; j < imageData.height; j++)
						{
							for (int i = 0; i < imageData.width; i++)
							{
								
								imageData.setAlpha(i, j, 255);
								if (((j * imageData.height + i) * iImageType) < dataLength
									&& ((j * imageData.height + i) * iImageType + 1) < dataLength
									&& ((j * imageData.height + i) * iImageType + 2) < dataLength)
								
								{
									if (imageData.data[(j * imageData.height + i) * iImageType] == -1
										&& imageData.data[(j * imageData.height + i) * iImageType
															+ 1] == -1
										&& imageData.data[(j * imageData.height + i) * iImageType
															+ 2] == -1)
									{
										imageData.setAlpha(i, j, 0);
									}
								}
								
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				CLogger.getLogger(ClientCommon.class).error("图标透明方法异常！");
				return image;
			}
			
			Image result = new Image(Display.getCurrent(), imageData);
			return result;
		}
		
		/**
		 * 根据单盘信息获得树节点文字
		 * 
		 * @param dBoard
		 * @param item
		 */
		public static void initBoardItem(final DNetCellBoard dBoard, TreeItem item)
		{
			ResGetClass resget = ResGetClass.getInstance();
			Image boardImage;
			
			switch (dBoard.getBoardStatus())
			{
			case Constants.BoardStatusID.NORMAL:
				boardImage = resget.netcellmangerImages[ConstantDefine.NetcellMangerImage.NORMALBOARDITEM];
				break;
			case Constants.BoardStatusID.ALARM:
				boardImage = resget.netcellmangerImages[ConstantDefine.NetcellMangerImage.ALARMBOARDITEM];
				break;
			case Constants.BoardStatusID.ILLEGITIPLUS:
			case Constants.BoardStatusID.ILLEGITIPLUCK:
			case Constants.BoardStatusID.ERRCODE:
				boardImage = resget.netcellmangerImages[ConstantDefine.NetcellMangerImage.WRONGBOARDITEM];
				break;
			default:
				boardImage = resget.netcellmangerImages[ConstantDefine.NetcellMangerImage.NORMALBOARDITEM];
				break;
			}
			
			Image iconimg = new Image(Display.getCurrent(), ConstantDefine.TopoLogoSize.WIDTH,
				ConstantDefine.TopoLogoSize.HIGHT);
			
			GC gc = new GC(iconimg);
			if (boardImage != null) gc.drawImage(boardImage, 0, 0);
			
			if (dBoard.getBoardStatus() != Constants.BoardStatusID.ILLEGITIPLUS
				&& dBoard.getBoardStatus() != Constants.BoardStatusID.ILLEGITIPLUCK
				&& dBoard.getBoardStatus() != Constants.BoardStatusID.ERRCODE)
			{
				int iStartX = 0;
				if (dBoard.getLoopStatus() != Constants.LoopType.NOLOOP)
				{
					gc.drawImage(resget.treeImage[ConstantDefine.TreeImage.STATELOOP], iStartX, 0);
				}
				iStartX += 11;
				
				if (dBoard.getMaskStatus() == Constants.MASKTYPE.MASK)
				{
					gc.drawImage(resget.treeImage[ConstantDefine.TreeImage.STATEMASK], iStartX, 0);
				}
				
				iStartX += 11;
				
				if (dBoard.getModuleStatus() == Constants.ModuleType.UNENABLE)
				{
					gc.drawImage(	resget.treeImage[ConstantDefine.TreeImage.MODULEENABLE],
									iStartX,
									0);
				}
			}
			if (gc != null) gc.dispose();
			
			Image clarity = setImageClarity(iconimg);
			if (iconimg != null)
			{
				iconimg.dispose();
				iconimg = null;
			}
			
			item.setImage(clarity);
			item.setText(dBoard.getSlotNo() + "#槽道" + " - " + dBoard.getBoardName());
		}
		
		/**
		 * 单盘状态描述
		 * 
		 * @param board
		 * @return
		 */
		public static String getBoardStatusDes(DNetCellBoard board)
		{
			String strBoardStatus = "";
			switch (board.getBoardStatus())
			{
			case Constants.BoardStatusID.NORMAL:
				strBoardStatus += "正常";
				break;
			case Constants.BoardStatusID.ALARM:
				strBoardStatus += "告警";
				break;
			case Constants.BoardStatusID.ILLEGITIPLUS:
				strBoardStatus += "非法插盘";
				break;
			case Constants.BoardStatusID.ILLEGITIPLUCK:
				strBoardStatus += "非法拔盘";
				break;
			case Constants.BoardStatusID.ERRCODE:
				strBoardStatus += "插错盘";
				break;
			default:
				strBoardStatus += "正常";
				break;
			}
			return strBoardStatus;
		}
		
		/**
		 * 使字符串不为NULL值
		 * 
		 * @param str
		 * @return
		 */
		public static String assectStringNoNull(String str)
		{
			if (str == null) return "";
			return str;
		}
		
		/**
		 * 检查IP地址是否合法
		 * 
		 * @param ipaddress
		 * @return NULL 不合法
		 */
		public static String checkIP(String ipaddress)
		{
			Pattern IP_PATTERN = Pattern
				.compile("(\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b)|((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:\\*?))|((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){2}(?:\\*\\.\\*?))|((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)(?:\\*\\.\\*\\.\\*?))");
			
			String result = ipaddress;
			
			String[] subips = result.split("\\.");
			
			if (subips.length != 4) return null;
			for (int i = 0; i < subips.length; i++)
			{
				if (subips[i] == null || subips[i].equals("")) return null;
			}
			
			if (!IP_PATTERN.matcher(result).matches())
			{
				return null;
			}
			else return result;
			
		}
		
		/**
		 * 检查子网掩码是否有效
		 * 
		 * @param ipaddress
		 * @return
		 */
		public static boolean checkMask(String begintipaddress, String endipaddress,
			String maskaddress)
		{
			boolean result = true;
			Pattern IP_PATTERN = Pattern
				.compile("((24[08]|25[245]|224|192|128|0)\\.){3}(24[08]|25[245]|224|192|128|0)");
			
			String maskAddresstemp = maskaddress;
			
			String[] submakss = maskAddresstemp.split("\\.");
			
			if (submakss.length != 4) return false;
			for (int i = 0; i < submakss.length; i++)
			{
				if (submakss[i] == null || submakss[i].equals("")) return false;
			}
			
			if (!IP_PATTERN.matcher(maskAddresstemp).matches()) { return false; }
			String beginipaddresstemp = begintipaddress;
			String endipaddresstemp = endipaddress;
			
			String[] subbeginips = beginipaddresstemp.split("\\.");
			String[] subendips = endipaddresstemp.split("\\.");
			
			if (subbeginips.length != 4) return false;
			if (subendips.length != 4) return false;
			int[] intbeginips = new int[subbeginips.length];
			int[] intendips = new int[subbeginips.length];
			int[] intmakss = new int[subbeginips.length];
			for (int i = 0; i < subbeginips.length; i++)
			{
				intbeginips[i] = Integer.valueOf(subbeginips[i]).intValue();
				intendips[i] = Integer.valueOf(subendips[i]).intValue();
				intmakss[i] = Integer.valueOf(submakss[i]).intValue();
			}
			for (int i = 0; i < intmakss.length; i++)
			{
				if (i < intmakss.length - 1)
				{
					if (intmakss[i] < intmakss[i + 1]) { return false; }
				}
				
				if (intmakss[i] != 255)
				{
					boolean flag = true;
					for (int j = i + 1; j < subbeginips.length - i; i++)
					{
						if (intmakss[j] != 0)
						{
							flag = false;
							break;
						}
						
					}
					if (!flag) return false;
				}
			}
			
			return result;
		}
		
		/**
		 * 检查输入的IP地址范围的合法性
		 * 
		 * @param ipaddress
		 * @return
		 */
		public static boolean checkIpRange(String begintipaddress, String endipaddress,
			String maskAddress)
		{
			boolean result = true;
			String beginipaddresstemp = begintipaddress;
			String endipaddresstemp = endipaddress;
			String maskAddresstemp = maskAddress;
			
			String[] subbeginips = beginipaddresstemp.split("\\.");
			String[] subendips = endipaddresstemp.split("\\.");
			String[] submakss = maskAddresstemp.split("\\.");
			if (subbeginips.length != 4) return false;
			if (subendips.length != 4) return false;
			if (submakss.length != 4) return false;
			int[] tempbeginint = new int[subbeginips.length];
			int[] tempendint = new int[subendips.length];
			int[] intbeginips = new int[subbeginips.length];
			int[] intendips = new int[subendips.length];
			int[] intmakss = new int[submakss.length];
			for (int i = 0; i < subbeginips.length; i++)
			{
				intbeginips[i] = Integer.valueOf(subbeginips[i]).intValue();
				intendips[i] = Integer.valueOf(subendips[i]).intValue();
				intmakss[i] = Integer.valueOf(submakss[i]).intValue();
			}
			for (int i = 0; i < subbeginips.length; i++)
			{
				tempbeginint[i] = intbeginips[i] & intmakss[i];
				tempendint[i] = intendips[i] & intmakss[i];
			}
			for (int i = 0; i < subbeginips.length; i++)
			{
				if (tempbeginint[i] != tempendint[i]) { return false; }
			}
			
			return result;
			
		}
		
		/**
		 * 比较IP地址大小，相等返回：0, beginIPaddress大于endIPaddress返回:1，
		 * beginIPaddress小于endIPaddress返回:-1;
		 * 
		 * @param beginIPaddress
		 * @param endIPaddress
		 * @return
		 */
		public static int compareIP(String beginIPaddress, String endIPaddress)
		{
			int compareresult = 0;
			String[] beginsubips = beginIPaddress.split("\\.");
			
			String[] endsubips = endIPaddress.split("\\.");
			
			for (int i = 0; i < 4; i++)
			{
				Integer argument1 = Integer.valueOf(beginsubips[i]);
				Integer argument2 = Integer.valueOf(endsubips[i]);
				int iresult = argument1.compareTo(argument2);// beginsubips[i]<endsubips[i]
																// 返回值<0
				if (iresult != 0)
				{
					if (iresult > 0)
					{
						compareresult = 1;
					}
					else
					{
						compareresult = -1;
					}
					break;
				}
			}
			return compareresult;
		}
		
		/**
		 * 检查端口号是否合法
		 * 
		 * @param port
		 * @return -1 不合法
		 */
		public static int checkPort(String port)
		{
			Integer iport = null;
			try
			{
				iport = new Integer(port);
			}
			catch (Exception e)
			{
				return -1;
			}
			
			if (iport.compareTo(new Integer(0)) < 0 && iport.compareTo(new Integer(65535)) > 0) { return -1; }
			return iport.intValue();
		}
		
		/**
		 * -1 不合法
		 * 
		 * @param extendID
		 * @return 检查硬件开关地址是否合法
		 */
		public static int checkExtendID(String extendID)
		{
			Integer iextendID = null;
			try
			{
				iextendID = new Integer(extendID);
				if (iextendID.intValue() == -1) iextendID = new Integer(0);
			}
			catch (Exception e)
			{
				return -1;
			}
			
			if (iextendID.compareTo(new Integer(0)) < 0
				&& iextendID.compareTo(new Integer(255)) > 0) { return -1; }
			return iextendID.intValue();
		}
		
		/**
		 * 验证服务器连接是否正常 -1：不政策，0:正常，1：未知
		 * 
		 * @return
		 */
		public static int checkSevConnect()
		{
			for (int i = 0; i < ConstantDefine.FailConnectToSev.COUNT; i++)
			{
				DClient dClient = CurClient.getInstance().getDCurClient();
				
				String clientID;
				if (dClient == null || dClient.getClientID() == null)
				{
					clientID = RandomGUID.getGUID();
				}
				else
				{
					clientID = dClient.getClientID();
				}
				
				String result = GetSetServiceData.checkSevConnect(clientID);
				
				if (result != null && result.equals(clientID)) return 0;
				if (result != null && result.equals(Constants.UNKNOWN)) return 1;
			}
			return -1;
		}
		
		/**
		 * 按照slotno顺序排序
		 * 
		 * @param boards
		 * @return
		 */
		public static DNetCellBoard[] boardSort(DNetCellBoard[] boards)
		{
			DNetCellBoard tempboard;
			if (boards != null && boards.length > 0)
			{
				int length = boards.length;
				for (int i = 0; i < length - 1; i++)
				{
					for (int j = i + 1; j < length; j++)
					{
						if (boards[i].getSlotNo() > boards[j].getSlotNo())
						{
							tempboard = boards[i];
							boards[i] = boards[j];
							boards[j] = tempboard;
						}
					}
				}
			}
			return boards;
		}
		
		/**
		 * 将空盘排至最后
		 * 
		 * @param boards
		 * @return
		 */
		public static void SortCanSelectBoard(List boardList)
		{
			DNetCellBoardType tempboard;
			DNetCellBoardType cloneboard = null;
			if (boardList != null && boardList.size() > 0)
			{
				for (int i = 0; i < boardList.size(); i++)
				{
					tempboard = (DNetCellBoardType) boardList.get(i);
					if ((tempboard.getBoardType() == Constants.DevBoardType.B_MSAP_EMPTY && tempboard
						.getNetCellTypeID() == Constants.NetCellType.DEV_ECP_ACD)
						|| (tempboard.getBoardType() == Constants.DevBoardType.B_MSAP_EMPTY && tempboard
							.getNetCellTypeID() == Constants.NetCellType.DEV_ECP_ACDE)
						|| (tempboard.getBoardType() == Constants.DevBoardType.B_MSAP_EMPTY && tempboard
							.getNetCellTypeID() == Constants.NetCellType.DEV_ECP_ACDM)
						|| // add by wangxu 2012-05-07
						(tempboard.getBoardType() == Constants.DevBoardType.B_MSAP_EMPTY && tempboard
							.getNetCellTypeID() == Constants.NetCellType.DEV_MSAP)
						|| (tempboard.getBoardType() == Constants.DevBoardType.B_MSAP_EMPTY && tempboard
							.getNetCellTypeID() == Constants.NetCellType.DEV_MSAP02M)
						|| (tempboard.getBoardType() == Constants.DevBoardType.B_MSAP_EMPTY && tempboard
							.getNetCellTypeID() == Constants.NetCellType.DEV_MSAP02A)
						|| (tempboard.getBoardType() == Constants.DevBoardType.B_06G_EMPTY && tempboard
							.getNetCellTypeID() == Constants.NetCellType.DEV_GF806G)
						|| (tempboard.getBoardType() == Constants.DevBoardType.B_S03GS2_EMPTY && tempboard
							.getNetCellTypeID() == Constants.NetCellType.DEV_S03GS_02))
					{
						try
						{
							cloneboard = (DNetCellBoardType) tempboard.clone();
						}
						catch (CloneNotSupportedException e)
						{
							e.printStackTrace();
						}
						boardList.add(cloneboard);
						boardList.remove(i);
						break;
					}
				}
			}
		}
		
		/**
		 * 获得有效单盘
		 * 
		 * @param boards
		 * @return
		 */
		public static DNetCellBoard[] boardFilter(DNetCellBoard[] boards)
		{
			List newboardlist = new ArrayList();
			if (boards != null && boards.length > 0)
			{
				int length = boards.length;
				for (int i = 0; i < length; i++)
				{
					if (boards[i].getBoardType() == Constants.DevBoardType.B_MSAP_EMPTY
						|| boards[i].getBoardType() == Constants.DevBoardType.B_06G_EMPTY
						|| boards[i].getBoardType() == Constants.DevBoardType.B_MSAP_PWR
						|| boards[i].getBoardType() == Constants.DevBoardType.B_06G_PWB
						|| boards[i].getBoardType() == Constants.DevBoardType.B_06G_JK)
					{
						continue;
					}
					newboardlist.add(boards[i]);
				}
			}
			DNetCellBoard[] newboards = new DNetCellBoard[newboardlist.size()];
			newboards = (DNetCellBoard[]) newboardlist.toArray(newboards);
			return newboards;
		}
		
		/**
		 * 取得客户端所有的子网 数据类型 subnetwork 建议取到的数据进行克隆再运用
		 * 
		 * @return
		 */
		public static List getAllSubNetWork()
		{
			ArrayList lstSubNetWork = new ArrayList();
			
			NetWork netWork = NetWork.getInstance();
			SubNetWork subNetWork = netWork.getSubNetWork();
			
			for (int i = 0; i < subNetWork.getListSubNetWork().size(); i++)
			{
				SubNetWork sub = (SubNetWork) subNetWork.getListSubNetWork().get(i);
				
				ArrayList lstOneSub = (ArrayList) getDownSubNetWork(sub, new ArrayList());
				
				for (int j = 0; j < lstOneSub.size(); j++)
				{
					lstSubNetWork.add(lstOneSub.get(j));
				}
			}
			
			return lstSubNetWork;
		}
		
		/**
		 * 取得一个子网下的所有子网 包括自身 建议取到的数据进行克隆再运用 递归算法
		 * 
		 * @param sub
		 * @param esitSubLst
		 * @return
		 */
		public static List getDownSubNetWork(SubNetWork sub, List esitSubLst)
		{
			List result = new ArrayList();
			
			result.add(sub);
			for (int i = 0; i < esitSubLst.size(); i++)
			{
				result.add(esitSubLst.get(i));
			}
			
			for (int i = 0; i < sub.getListSubNetWork().size(); i++)
			{
				SubNetWork subWork = (SubNetWork) sub.getListSubNetWork().get(i);
				
				result = getDownSubNetWork(subWork, result);
			}
			
			return result;
		}
		
		/**
		 * 取得客户端所有的网关网元 数据类型 netCellNode 建议取到的数据进行克隆再运用
		 * 
		 * @return
		 */
		public static List getAllGateWayNetCell()
		{
			List lstAllNetCell = new ArrayList();
			
			NetWork netWork = NetWork.getInstance();
			SubNetWork subNetWorkAll = netWork.getSubNetWork();
			
			for (int i = 0; i < subNetWorkAll.getListSubNetCell().size(); i++)
			{
				lstAllNetCell.add(subNetWorkAll.getListSubNetCell().get(i));
			}
			
			List lstAllSub = getAllSubNetWork();
			for (int i = 0; i < lstAllSub.size(); i++)
			{
				SubNetWork subNetWork = (SubNetWork) lstAllSub.get(i);
				
				for (int j = 0; j < subNetWork.getListSubNetCell().size(); j++)
				{
					NetcellNode netcellNode = (NetcellNode) subNetWork.getListSubNetCell().get(j);
					
					lstAllNetCell.add(netcellNode);
				}
			}
			
			return lstAllNetCell;
		}
		
		/**
		 * 取得客户端所有的网元,包括了远端网元 数据类型 netCellNode 建议取到的数据进行克隆再运用
		 * 
		 * @return
		 */
		public static List getAllNetCell()
		{
			List lstAllNetCell = new ArrayList();
			
			NetWork netWork = NetWork.getInstance();
			SubNetWork subNetWorkAll = netWork.getSubNetWork();
			
			NetcellNode netcellNode;
			for (int i = 0; i < subNetWorkAll.getListSubNetCell().size(); i++)
			{
				netcellNode = (NetcellNode) subNetWorkAll.getListSubNetCell().get(i);
				lstAllNetCell.add(netcellNode);
				
				for (int j = 0; j < netcellNode.listRemoteNetCell.size(); j++)
				{
					NetcellNode rmtNetCellNode = (NetcellNode) netcellNode.listRemoteNetCell.get(j);
					lstAllNetCell.add(rmtNetCellNode);
				}
			}
			
			List lstAllSub = getAllSubNetWork();
			for (int i = 0; i < lstAllSub.size(); i++)
			{
				SubNetWork subNetWork = (SubNetWork) lstAllSub.get(i);
				
				for (int j = 0; j < subNetWork.getListSubNetCell().size(); j++)
				{
					netcellNode = (NetcellNode) subNetWork.getListSubNetCell().get(j);
					lstAllNetCell.add(netcellNode);
					
					for (int k = 0; k < netcellNode.listRemoteNetCell.size(); k++)
					{
						NetcellNode rmtNetCellNode = (NetcellNode) netcellNode.listRemoteNetCell
							.get(k);
						lstAllNetCell.add(rmtNetCellNode);
					}
				}
			}
			
			return lstAllNetCell;
		}
		
		/**
		 * 是否能显示该网元类型
		 * 
		 * @param netCellType
		 * @param lstViewFilter
		 * @return
		 */
		public static boolean canDrawNetCell(int netCellType, List lstViewFilter)
		{
			if (lstViewFilter == null) return true;
			for (int i = 0; i < lstViewFilter.size(); i++)
			{
				DViewFilterConfig dViewFilterConfig = (DViewFilterConfig) lstViewFilter.get(i);
				
				if (dViewFilterConfig.getNetCellTypeID() == netCellType)
				{
					if (dViewFilterConfig.getIsShow() == 1) return true;
					else return false;
				}
			}
			return true;
		}
		
		/**
		 * 过滤掉所有非图象资源文件
		 * 
		 * @param existFiles
		 * @return
		 */
		public static File[] getSubnetFiles(File[] existFiles)
		{
			ArrayList resultLst = new ArrayList();
			for (int i = 0; i < existFiles.length; i++)
			{
				String filesName = existFiles[i].getName();
				if (filesName.endsWith(".bmp") || filesName.endsWith(".jpg")
					|| filesName.endsWith(".jpeg") || filesName.endsWith(".png")
					|| filesName.endsWith(".jpe") || filesName.endsWith(".jfif")
					|| filesName.endsWith(".gif") || filesName.endsWith(".tif")
					|| filesName.endsWith(".tiff") || filesName.endsWith(".ico")
					|| filesName.endsWith(".BMP") || filesName.endsWith(".JPG")
					|| filesName.endsWith(".JPEG") || filesName.endsWith(".PNG")
					|| filesName.endsWith(".JPE") || filesName.endsWith(".JFIF")
					|| filesName.endsWith(".GIF") || filesName.endsWith(".TIF")
					|| filesName.endsWith(".TIFF") || filesName.endsWith(".ICO")) resultLst
					.add(existFiles[i]);
			}
			
			File[] result = new File[resultLst.size()];
			result = (File[]) resultLst.toArray(result);
			
			return result;
			
		}
		
		/**
		 * 画纯色图片
		 * 
		 * @param height
		 * @param width
		 * @param color
		 * @return
		 */
		public static Image drawImage(int width, int height, Color color)
		{
			Image result = new Image(Display.getCurrent(), width, height);
			GC gc = new GC(result);
			gc.setBackground(color);
			gc.fillRectangle(0, 0, width, height);
			gc.dispose();
			
			return result;
		}
		
		/**
		 * 点击CheckBoxTreeViewer响应事件
		 * 
		 * @param checkboxTreeViewer
		 * @param item
		 */
		public static void checkTreeBoxState(CheckboxTreeViewer checkboxTreeViewer, ITreeNode item)
		{
			if (checkboxTreeViewer.getChecked(item))
			{
				checkboxTreeViewer.setGrayChecked(item, false);
				checkboxTreeViewer.setChecked(item, true);
				dealwithCheckedChildNode(item, checkboxTreeViewer);
				ITreeNode parent = (ITreeNode) item.getParent();
				if (parent != null) dealwithParentNode(parent, checkboxTreeViewer);
			}
			else
			{
				checkboxTreeViewer.setGrayChecked(item, false);
				checkboxTreeViewer.setChecked(item, false);
				dealwithUnCheckedChildNode(item, checkboxTreeViewer);
				ITreeNode parent = (ITreeNode) item.getParent();
				if (parent != null) dealwithParentNode(parent, checkboxTreeViewer);
			}
		}
		
		private static void dealwithParentNode(ITreeNode item, CheckboxTreeViewer checkboxTreeViewer)
		{
			
			boolean iCheckFlag = false;
			boolean iGrayFlag = false;
			boolean iUnCheckFlag = false;
			ArrayList lstChild = item.getChildren();
			for (int i = 0; i < lstChild.size(); i++)
			{
				if (checkboxTreeViewer.getGrayed(lstChild.get(i)))
				{
					iGrayFlag = true;
				}
				else if (checkboxTreeViewer.getChecked(lstChild.get(i)))
				{
					iCheckFlag = true;
				}
				else
				{
					iUnCheckFlag = true;
				}
			}
			// 全不选
			if (!iCheckFlag && !iGrayFlag)
			{
				checkboxTreeViewer.setGrayChecked(item, false);
				checkboxTreeViewer.setChecked(item, false);
			}
			// 全选
			else if (!iGrayFlag && !iUnCheckFlag)
			{
				checkboxTreeViewer.setGrayChecked(item, false);
				checkboxTreeViewer.setChecked(item, true);
			}
			// 部分选中
			else
			{
				checkboxTreeViewer.setChecked(item, false);
				checkboxTreeViewer.setGrayChecked(item, true);
			}
			ITreeNode parent = (ITreeNode) item.getParent();
			if (parent != null) dealwithParentNode(parent, checkboxTreeViewer);
			
		}
		
		public static void dealwithCheckedChildNode(ITreeNode item,
			CheckboxTreeViewer checkboxTreeViewer)
		{
			ArrayList childlist = item.getChildren();
			if (childlist != null && childlist.size() > 0)
			{
				for (int i = 0; i < childlist.size(); i++)
				{
					checkboxTreeViewer.setGrayChecked(childlist.get(i), false);
					checkboxTreeViewer.setChecked(childlist.get(i), true);
					ITreeNode temp = (ITreeNode) childlist.get(i);
					dealwithCheckedChildNode(temp, checkboxTreeViewer);
				}
			}
		}
		
		public static void dealwithUnCheckedChildNode(ITreeNode item,
			CheckboxTreeViewer checkboxTreeViewer)
		{
			ArrayList childlist = item.getChildren();
			if (childlist != null && childlist.size() > 0)
			{
				for (int i = 0; i < childlist.size(); i++)
				{
					checkboxTreeViewer.setGrayChecked(childlist.get(i), false);
					checkboxTreeViewer.setChecked(childlist.get(i), false);
					ITreeNode temp = (ITreeNode) childlist.get(i);
					dealwithUnCheckedChildNode(temp, checkboxTreeViewer);
				}
			}
		}
		
		/**
		 * 初始化checkboxTreeViewer
		 * 
		 * @param checkboxTreeViewer
		 * @param elements
		 *            选择的对象
		 */
		public static void initCheckboxTreeViewer(CheckboxTreeViewer checkboxTreeViewer,
			ITreeNode[] elements)
		{
			List parentList = new ArrayList();
			boolean iParentFlag = false;
			if (elements != null && elements.length > 0)
			{
				for (int i = 0; i < elements.length; i++)
				{
					ITreeNode item = (ITreeNode) elements[i];
					if (checkboxTreeViewer.getChecked(item))
					{
						checkboxTreeViewer.setGrayChecked(item, false);
						checkboxTreeViewer.setChecked(item, true);
						dealwithCheckedChildNode(item, checkboxTreeViewer);
						ITreeNode parent = (ITreeNode) item.getParent();
						iParentFlag = false;
						if (parent != null)
						{
							if (parentList != null && parentList.size() > 0)
							{
								for (int j = 0; j < parentList.size(); j++)
								{
									ITreeNode temp = (ITreeNode) parentList.get(j);
									if (parent.getName().equals(temp.getName()))
									{
										iParentFlag = true;
										break;
									}
								}
								if (!iParentFlag)
								{
									parentList.add(parent);
								}
							}
							else parentList.add(parent);
						}
					}
					else
					{
						checkboxTreeViewer.setGrayChecked(item, false);
						checkboxTreeViewer.setChecked(item, false);
					}
				}
				if (parentList != null && parentList.size() > 0)
				{
					for (int m = 0; m < parentList.size(); m++)
					{
						ITreeNode parent = (ITreeNode) parentList.get(m);
						dealwithParentNode(parent, checkboxTreeViewer);
					}
				}
				
			}
		}
		
		/**
		 * tableViewer 序号列排序
		 * 
		 * @param tableViewer
		 */
		public static void sortIndexColumn(TableViewer tableViewer)
		{
			TableItem[] items = tableViewer.getTable().getItems();
			if (items != null && items.length > 0)
			{
				for (int i = 0; i < items.length; i++)
				{
					items[i].setText(0, String.valueOf(i + 1));
				}
			}
			tableViewer.getTable().redraw();
		}
		
		public static int returnProductID(DNetCell netcell)
		{
			int iProductID = 0;
			TreeManage treeManage = TreeManage.getInstance();
			NetcellNode netcellnode = treeManage.getGateWayNetCell(netcell.getId());
			if (netcellnode.dNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP
				|| netcellnode.dNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02M
				|| netcellnode.dNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02A
				|| netcellnode.dNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_MSAP02AM)
			{
				iProductID = ConstantDefine.ProductType.MSAP;
			}
			else if (netcellnode.dNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G)
			{
				iProductID = ConstantDefine.ProductType.PDH;
			}
			else if (netcellnode.dNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACD
						|| netcellnode.dNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDE
						|| netcellnode.dNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_ECP_ACDM)
			{
				iProductID = ConstantDefine.ProductType.ECPACD;
			}
			else if (netcellnode.dNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS)
			{
				iProductID = ConstantDefine.ProductType.S03GS;
			}
			else if (netcellnode.dNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_S03GS_02)
			{
				iProductID = ConstantDefine.ProductType.S03GS02;
			}
			else
			{
				iProductID = ConstantDefine.ProductType.DEFAULT;
			}
			return iProductID;
		}
		
		// 导出EXCEL 创建单元格
		@SuppressWarnings("deprecation")
		public static void createCell(HSSFWorkbook wb, HSSFRow row, short column, short align,
			String value)
		{
			HSSFCell cell = row.createCell(column);
			cell.setCellValue(value);
			
			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(align);
			cell.setCellStyle(cellStyle);
		}
		
		/**
		 * 根据ID查询变量描述
		 */
		public static String getDescById(int constantCodeID)
		{
			
			String str = "";
			ConstCode conCod = ConstCode.getInstance();
			List constList = conCod.getConstList();
			if (constList != null)
			{
				for (int i = 0; i < constList.size(); i++)
				{
					DConstantCode temp = (DConstantCode) constList.get(i);
					
					if (temp.getConstantCodeID() == constantCodeID)
					{
						str = temp.getValueDesc();
						break;
					}
					
				}
			}
			return str;
		}
		
		/**
		 * 根据ID查询变量的Value值
		 */
		public static int getConstantValue(int constantCodeID)
		{
			int value = -1;
			ConstCode conCod = ConstCode.getInstance();
			List constList = conCod.getConstList();
			if (constList != null)
			{
				for (int i = 0; i < constList.size(); i++)
				{
					DConstantCode temp = (DConstantCode) constList.get(i);
					
					if (temp.getConstantCodeID() == constantCodeID)
					{
						value = temp.getValue();
						break;
					}
					
				}
			}
			return value;
		}
		
		/**
		 * 根据变量的描述名称，变量类型，网元id找value
		 * 
		 */
		public static int getConstantValue(int netcellTypeId, int constantType, String descName)
		{
			int value = -1;
			ConstCode conCod = ConstCode.getInstance();
			List constList = conCod.getConstList();
			if (constList != null)
			{
				for (int i = 0; i < constList.size(); i++)
				{
					DConstantCode temp = (DConstantCode) constList.get(i);
					
					if ((temp.getValueDesc().equals(descName))
						&& (temp.getNetCellTypeID() == netcellTypeId)
						&& (temp.getConstantType() == constantType))
					{
						value = temp.getValue();
						break;
					}
					
				}
			}
			return value;
		}
		
		/**
		 * 根据变量类型，变量value，网元类型Id找变量Id
		 */
		public static int getConstantId(int netcellTypeId, int constantType, int value)
		{
			int id = -1;
			ConstCode conCod = ConstCode.getInstance();
			List constList = conCod.getConstList();
			if (constList != null)
			{
				for (int i = 0; i < constList.size(); i++)
				{
					DConstantCode temp = (DConstantCode) constList.get(i);
					
					if ((temp.getNetCellTypeID() == netcellTypeId)
						&& (temp.getConstantType() == constantType) && (temp.getValue() == value))
					{
						id = temp.getConstantCodeID();
						break;
					}
					
				}
			}
			return id;
		}
		
		/**
		 * 根据变量类型，网元类型Id找一系列变量Id
		 */
		public static DConstantCode[] getSeriesConstCode(int netcellTypeId, int constantType)
		{
			ArrayList<DConstantCode> lstid = new ArrayList<DConstantCode>();
			ConstCode conCod = ConstCode.getInstance();
			List constList = conCod.getConstList();
			if (constList != null)
			{
				for (int i = 0; i < constList.size(); i++)
				{
					DConstantCode temp = (DConstantCode) constList.get(i);
					
					if ((temp.getNetCellTypeID() == netcellTypeId)
						&& (temp.getConstantType() == constantType))
					{
						lstid.add(temp);
					}
					
				}
			}
			DConstantCode[] code = new DConstantCode[lstid.size()];
			code = lstid.toArray(code);
			return code;
		}
		
		/**
		 * @deprecated
		 * @param tableViewer
		 * @param iColIndex
		 */
		public static void setColColor(TableViewer tableViewer, int iColIndex)
		{
			TableItem[] items = tableViewer.getTable().getItems();
			if (items != null)
			{
				for (int i = 0; i < items.length; i++)
				{
					items[i].setForeground(iColIndex, SWTResourceManager.getColor(0, 0, 225));
				}
			}
			
		}
		
		/**
		 * 获得EcpAcd远端面板文字信息提示
		 * 
		 * @param netcellnode
		 * @param doprObj
		 * @return
		 */
		public static String getEcpAcdRemoteInfoTxt(NetcellNode netcellNode,
			DOperationObjects doprObj)
		{
			String result = "";
			result = "该网元为 " + "＂" + netcellNode.localNetCellName + " - " + doprObj.getSlotNo()
						+ "#槽道 - " + doprObj.getNetCellBoardTypeName() + " - "
						+ doprObj.getOperObjectTypeName() + "-" + doprObj.getOperObjectIndex()
						+ "＂  的远端设备";
			return result;
		}
		
		/**
		 * 获得Msap远端面板文字信息提示
		 * 
		 * @param netcellnode
		 * @param doprObj
		 * @return
		 */
		public static String getMsapRemoteInfoTxt(NetcellNode netcellNode, DOperationObjects doprObj)
		{
			String result = "";
			result = "该网元为 " + "＂" + netcellNode.localNetCellName + " - " + doprObj.getSlotNo()
						+ "#槽道 - " + doprObj.getNetCellBoardTypeName() + " - "
						+ doprObj.getOperObjectTypeName() + "-" + doprObj.getOperObjectIndex()
						+ "＂  的远端设备";
			return result;
		}
		
		/**
		 * 获得Gf806g远端面板文字信息提示
		 * 
		 * @param netcellnode
		 * @param doprObj
		 * @return
		 */
		public static String getGf806gRemoteInfoTxt(NetcellNode netcellNode,
			DOperationObjects doprObj)
		{
			String result = "";
			result = "该网元为 " + "＂" + netcellNode.localNetCellName + " - " + doprObj.getSlotNo()
						+ "#槽道 - " + doprObj.getNetCellBoardTypeName() + " - "
						+ doprObj.getOperObjectTypeName() + "-" + doprObj.getOperObjectIndex()
						+ "＂  的远端设备";
			return result;
		}
		
		/**
		 * 获得S03gs远端面板文字信息提示
		 * 
		 * @param netcellNode
		 * @param doprObj
		 * @return
		 */
		public static String getS03gsRemoteInfoTxt(NetcellNode netcellNode,
			DOperationObjects doprObj, DNetCellType netCellType)
		{
			String result = "";
			result = "该网元为 " + "＂" + netcellNode.localNetCellName + " - "
						+ netCellType.getNetCellTypeName() + " - "
						+ doprObj.getOperObjectTypeName() + "-" + doprObj.getOperObjectIndex()
						+ "＂  的远端设备";
			return result;
		}
		
		/**
		 * 获得S03gs2远端面板文字信息提示
		 * 
		 * @param netcellnode
		 * @param doprObj
		 * @return
		 */
		public static String getS03gs2RemoteInfoTxt(NetcellNode netcellNode,
			DOperationObjects doprObj)
		{
			String result = "";
			result = "该网元为 " + "＂" + netcellNode.localNetCellName + " - " + doprObj.getSlotNo()
						+ "#槽道 - " + doprObj.getNetCellBoardTypeName() + " - "
						+ doprObj.getOperObjectTypeName() + "-" + doprObj.getOperObjectIndex()
						+ "＂  的远端设备";
			return result;
		}
		
		/**
		 * 对网元进行排序，一级远端在前，二级远端在后。
		 * 
		 * @param netcellList
		 * @return
		 */
		public static List ArrayNetCellType(List<DNetCell> netcellList)
		{
			
			if (netcellList == null) return null;
			
			List<DNetCell> sortedList = new ArrayList<DNetCell>();
			
			List<DNetCell> netCell2J = new ArrayList<DNetCell>();
			for (int i = 0; i < netcellList.size(); i++)
			{
				DNetCell tempnetcell = (DNetCell) netcellList.get(i);
				if (tempnetcell.getParentNetCellID() == null)
				{
					sortedList.add(tempnetcell);
				}
				else
				{
					if (tempnetcell.getLocalNetCellID().equals(tempnetcell.getParentNetCellID()))
					{
						sortedList.add(tempnetcell);
					}
					else
					{
						netCell2J.add(tempnetcell);
					}
				}
				
			}
			if (netCell2J != null && !netCell2J.isEmpty())
			{
				
				DNetCell[] netCell2Js = new DNetCell[netCell2J.size()];
				netCell2J.toArray(netCell2Js);
				
				for (int i = 0; i < netCell2Js.length; i++)
				{
					sortedList.add(netCell2Js[i]);
				}
			}
			
			netcellList.clear();
			for (int i = 0; i < sortedList.size(); i++)
			{
				netcellList.add(sortedList.get(i));
			}
			return netcellList;
		}
		
		public static ArrayList ArrayNetCellNodeType(ArrayList<NetcellNode> netcellList)
		{
			
			if (netcellList == null) return null;
			
			List<NetcellNode> sortedList = new ArrayList<NetcellNode>();
			
			List<NetcellNode> netCell2J = new ArrayList<NetcellNode>();
			for (int i = 0; i < netcellList.size(); i++)
			{
				NetcellNode tempnetcell = (NetcellNode) netcellList.get(i);
				if (tempnetcell.dNetCell.getParentNetCellID() == null)
				{
					sortedList.add(tempnetcell);
				}
				else
				{
					if (tempnetcell.dNetCell.getLocalNetCellID().equals(tempnetcell.dNetCell
																			.getParentNetCellID()))
					{
						sortedList.add(tempnetcell);
					}
					else
					{
						netCell2J.add(tempnetcell);
					}
				}
				
			}
			if (netCell2J != null && !netCell2J.isEmpty())
			{
				
				NetcellNode[] netCell2Js = new NetcellNode[netCell2J.size()];
				netCell2J.toArray(netCell2Js);
				
				for (int i = 0; i < netCell2Js.length; i++)
				{
					sortedList.add(netCell2Js[i]);
				}
			}
			
			netcellList.clear();
			for (int i = 0; i < sortedList.size(); i++)
			{
				netcellList.add(sortedList.get(i));
			}
			return netcellList;
		}
		
		public static Rectangle RandomCoordinate(int r, int handle, int parentX, int parentY)
		{
			int x = 0, y = 0;
			RECT rect = new RECT();
			OS.GetClientRect(handle, rect);
			int xPos = rect.left, yPos = rect.top;
			int width = rect.right - rect.left;
			int height = rect.bottom - rect.top;
			Rectangle clientRect = new Rectangle(xPos, yPos, width, height);
			
			int i = (int) (Math.random() * 360);// i为角度
			double radian = i * (Math.PI / 180);// radian为弧度
			x = (int) (r * Math.cos(radian)) + parentX;
			y = (int) (r * Math.sin(radian)) + parentY;
			
			x = x < clientRect.x ? clientRect.x : x;
			y = y < clientRect.y ? clientRect.y : y;
			x = x > clientRect.width ? clientRect.width : x;
			y = y > clientRect.height ? clientRect.height : y;
			
			Rectangle newRect = new Rectangle(x, y, 22, 22);
			return newRect;
		}
		
		public static Cursor ChangeCursor(Display display, ImageData imageData)
		{
			int hCursor = OS.LoadCursor(0, SWT.CURSOR_SIZENESW);
			Cursor cursor = new Cursor(display, imageData, 0, 0);
			OS.SetCursor(hCursor);
			return cursor;
		}
		
		/**
		 * 排列图标 add by lg 2010-12-24
		 * 
		 * @param netCells
		 *            待排列网元数组
		 * @param gateNetCell
		 *            网关型网元
		 * @param iStyle
		 *            0:矩形 1:圆形
		 * @return
		 */
		public static DNetCell[] arrayNetCells(DNetCell[] netCells, DNetCell gateNetCell, int iStyle)
		{
			// 如果远端网元为空
			if (netCells == null || netCells.length == 0) { return null; }
			
			DNetCell[] retNetCells = new DNetCell[netCells.length];
			switch (iStyle)
			{
			case ConstantDefine.SortType.Rect:
			{
				//
				// int[] arrayY = new int[17];
				// int x = 10,y = 50;
				// Hashtable<String,DNetCell> ht = new
				// Hashtable<String,DNetCell>();
				//
				// for (int i = 0; i < netCells.length; i++) {
				// DNetCell cell = netCells[i];
				// int slot = cell.getLocalSlotNo();
				//
				// if(slot > 0)
				// {
				// x = slot * 80 - 70;
				// y = (80 + arrayY[slot]);
				// arrayY[slot] = y;
				// }
				// else
				// {
				// if(i>0)
				// {
				// y += 80;
				// //i为10的倍数时分开排列
				// if(i%10 == 0)
				// {
				// x += 80;
				// y = 70;
				// }
				// }
				// }
				//
				// cell.setCoordinateX(x);
				// cell.setCoordinateY(y);
				// retNetCells[i] = cell;
				// }
				// // 若为06g则判断
				// if(gateNetCell.getNetCellTypeId() ==
				// Constants.NetCellType.DEV_GF806G)
				// {
				// return retNetCells;
				// }
				//
				//
				// // 得到一级远端
				// for(int i=0; i < retNetCells.length; i++)
				// {
				// DNetCell cell = retNetCells[i];
				// if(cell.getLocalNetCellID().equals(cell.getParentNetCellID()))
				// {
				// ht.put(cell.getId(), cell);
				// }
				// }
				//
				// for(int i=0; i < retNetCells.length; i++)
				// {
				// DNetCell cell = retNetCells[i];
				//
				// //定义二级远端坐标
				// if(!cell.getLocalNetCellID().equals(cell.getParentNetCellID()))
				// {
				// if(ht.get(cell.getParentNetCellID()) != null)
				// {
				// DNetCell netcell = ht.get(cell.getParentNetCellID());
				// if(cell.getParentNetCellID().equals(netcell.getId()))
				// {
				// cell.setCoordinateX(netcell.getCoordinateX() + 28);
				// cell.setCoordinateY(netcell.getCoordinateY() + 20);
				// }
				// }
				// }
				// }
				
				retNetCells = Rect_arrayNetCells(netCells, gateNetCell);
				
			}
				break;
			case ConstantDefine.SortType.Round:
			{
				int x = 0, y = 0;
				int x0 = gateNetCell.getCoordinateX();
				int y0 = gateNetCell.getCoordinateY();
				Hashtable<String, DNetCell> ht = new Hashtable<String, DNetCell>();
				List list = new ArrayList();
				List allList = new ArrayList();
				Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
				
				// 定义半径
				int R = 230;
				int Ri = 320;
				int count = 0;
				
				// 得到一,二级远端
				for (int i = 0; i < netCells.length; i++)
				{
					DNetCell cell = netCells[i];
					if (cell.getLocalNetCellID().equals(cell.getParentNetCellID())
						|| cell.getLocalNetCellTypeID() == Constants.NetCellType.DEV_GF806G)
					// 06g远端父网元id为空，故判断网元类型
					{
						list.add(cell);
						count++; // 得到一级远端个数
					}
					else
					{
						allList.add(cell);
					}
				}
				double r = (Math.PI * 2) / count;
				DNetCell[] netCellss = new DNetCell[list.size()];
				list.toArray(netCellss);
				
				// 排列一级远端
				for (int i = 0; i < netCellss.length; i++)
				{
					DNetCell cell = netCellss[i];
					
					x = (int) (R * Math.cos(r * i)) + x0;
					y = (int) (R * Math.sin(r * i)) + y0;
					
					// 传递i
					hashtable.put(cell.getId(), i);
					cell.setCoordinateX(x);
					cell.setCoordinateY(y);
					retNetCells[i] = cell;
					ht.put(cell.getId(), cell);
					allList.add(cell);
				}
				// 若为06g则判断
				if (gateNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G)
				{
					
				}
				else
				{
					// 得到一级远端排列后的所有远端
					DNetCell[] dNetCells = new DNetCell[allList.size()];
					allList.toArray(dNetCells);
					
					for (int i = 0; i < dNetCells.length; i++)
					{
						DNetCell cell = dNetCells[i];
						
						// 定义二级远端坐标
						if (!cell.getLocalNetCellID().equals(cell.getParentNetCellID()))
						{
							if (ht.get(cell.getParentNetCellID()) != null)
							{
								DNetCell netcell = ht.get(cell.getParentNetCellID());
								if (cell.getParentNetCellID().equals(netcell.getId()))
								{
									// 得到夹角的倍数
									if (hashtable.containsKey(netcell.getId()))
									{
										int j = hashtable.get(netcell.getId());
										x = (int) (Ri * Math.cos(r * j)) + x0;
										y = (int) (Ri * Math.sin(r * j)) + y0;
										cell.setCoordinateX(x);
										cell.setCoordinateY(y);
									}
								}
							}
						}
						
						retNetCells[i] = cell;
					}
				}
				
				// 判断图标越界
				int x2 = 100;
				int y2 = 100;
				for (int i = 0; i < netCellss.length; i++)
				{
					DNetCell cell = netCellss[i];
					
					if (cell.getCoordinateX() - x0 < 10 && cell.getCoordinateY() < 100)
					{
						y2 = cell.getCoordinateY();
					}
					if (cell.getCoordinateY() - y0 < 10 && cell.getCoordinateX() < 100)
					{
						x2 = cell.getCoordinateX();
					}
					
				}
				
				// 判断图标越界
				if (x2 < 100)
				{
					x0 += Math.abs(x2 - 100);
					gateNetCell.setCoordinateX(x0);
					retNetCells = arrayNetCells(netCells, gateNetCell, 1);
					gateNetCell.setCoordinateX(x0 - Math.abs(x2 - 100));
				}
				if (y2 < 100)
				{
					y0 += Math.abs(y2 - 100);
					gateNetCell.setCoordinateY(y0);
					retNetCells = arrayNetCells(netCells, gateNetCell, 1);
					gateNetCell.setCoordinateY(y0 - Math.abs(y2 - 100));
				}
				
			}
				break;
			default:
				retNetCells = netCells;
				break;
			}
			return retNetCells;
		}
		
		/**
		 * 排列图标 add by lg 2010-12-24
		 * 
		 * @param netCells
		 * @param gateNetCell
		 * @param iStyle
		 * @return
		 */
		public static DNetCell[] Rect_arrayNetCells(DNetCell[] netCells, DNetCell gateNetCell)
		{
			DNetCell[] retNetCells = new DNetCell[netCells.length];
			
			int x = 0, y = 0;
			int x0 = gateNetCell.getCoordinateX();
			int y0 = gateNetCell.getCoordinateY();
			Hashtable<String, DNetCell> ht = new Hashtable<String, DNetCell>();
			List list = new ArrayList();
			List allList = new ArrayList();
			
			// 分别得到一二级远端
			for (int i = 0; i < netCells.length; i++)
			{
				DNetCell cell = netCells[i];
				if (cell.getLocalNetCellID().equals(cell.getParentNetCellID())
					|| cell.getLocalNetCellTypeID() == Constants.NetCellType.DEV_GF806G)
				// 06g远端父网元id为空，故判断网元类型
				{
					list.add(cell);
				}
				else
				{
					allList.add(cell);
				}
			}
			
			DNetCell[] dNetCells = new DNetCell[list.size()];
			list.toArray(dNetCells);
			// 遍历一级远端
			int n = dNetCells.length;
			int xx = 0;
			int yy = 0;
			// 分四部分
			int c1 = (int) Math.ceil(n / 4.0);
			int c2 = (int) Math.ceil(n / 2.0);
			int c3 = (int) Math.ceil(3 * n / 4.0);
			
			if (n > 0 && n <= 66)
			{
				for (int i = 0; i < dNetCells.length; i++)
				{
					DNetCell cell = dNetCells[i];
					if (n == 1)
					{
						x = x0;
						y = y0 - 180;
					}
					if (n == 2)
					{
						if (i == 0)
						{
							x = x0;
							y = y0 + 180;
						}
						else
						{
							x = x0;
							y = y0 - 180;
						}
					}
					if (n >= 3)
					{
						if (i < c1)
						{
							x = x0 - (i + 1) * 85;
							y = (y0 - 180);
						}
						else if (i >= c1 && i < c2)
						{
							x = x0 + (i + 1 - c1) * 85;
							y = (y0 - 180);
							
						}
						else if (i >= c2 && i < c3)
						{
							x = x0 - (i + 1 - c2) * 85;
							y = (y0 + 180);
							
						}
						else
						{
							x = x0 + (i + 1 - c3) * 85;
							y = (y0 + 180);
							
						}
					}
					
					cell.setCoordinateX(x);
					cell.setCoordinateY(y);
					ht.put(cell.getId(), cell);
					allList.add(cell);
					retNetCells[i] = cell;
					// 判断图标越界
					if (i == c1 - 1)
					{
						xx = cell.getCoordinateX();
					}
					yy = y0 - 180;
				}
				
				// 若为06g则判断
				if (gateNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G)
				{
				}
				else
				{
					// 得到一级远端排列后的所有远端
					DNetCell[] dNetCellss = new DNetCell[allList.size()];
					allList.toArray(dNetCellss);
					
					for (int i = 0; i < dNetCellss.length; i++)
					{
						DNetCell cell = dNetCellss[i];
						
						// 定义二级远端坐标
						if (!cell.getLocalNetCellID().equals(cell.getParentNetCellID()))
						{
							if (ht.get(cell.getParentNetCellID()) != null)
							{
								DNetCell netcell = ht.get(cell.getParentNetCellID());
								if (cell.getParentNetCellID().equals(netcell.getId()))
								{
									cell.setCoordinateX(netcell.getCoordinateX());
									if (netcell.getCoordinateY() > y0)
									{
										cell.setCoordinateY(netcell.getCoordinateY() + 80);
									}
									else
									{
										cell.setCoordinateY(netcell.getCoordinateY() - 80);
									}
								}
							}
						}
						
						retNetCells[i] = cell;
					}
				}
				// 判断图标越界
				if (xx < 10)
				{
					x0 += Math.abs(xx - 10);
					gateNetCell.setCoordinateX(x0);
					retNetCells = Rect_arrayNetCells(netCells, gateNetCell);
					gateNetCell.setCoordinateX(x0 - Math.abs(xx - 10));
				}
				if (yy - 70 < 10)
				{
					y0 += Math.abs(yy - 10 - 70);
					gateNetCell.setCoordinateY(y0);
					retNetCells = Rect_arrayNetCells(netCells, gateNetCell);
					gateNetCell.setCoordinateY(y0 - Math.abs(yy - 10 - 70));
				}
			}
			else
			{
				// 右边越界
				int x01 = 10;
				int y01 = 100;
				int count = 30;
				
				Hashtable<String, DNetCell> ht1 = new Hashtable<String, DNetCell>();
				
				for (int i = 0; i < dNetCells.length; i++)
				{
					DNetCell cell = dNetCells[i];
					
					// if(i<count)
					// {
					// cell.setCoordinateX(x01 + i*90);
					// cell.setCoordinateY(y01);
					// }
					// if(i>=count)
					{
						int h = i / count;// 第几行
						int m = i % count;// 第几列
						cell.setCoordinateX(x01 + m * 95);
						cell.setCoordinateY(y01 + 200 * h);
					}
					
					retNetCells[i] = cell;
					ht1.put(cell.getId(), cell);
				}
				
				// 若为06g则判断
				if (gateNetCell.getNetCellTypeId() == Constants.NetCellType.DEV_GF806G)
				{
				}
				else
				{
					// 得到一级远端排列后的所有远端
					DNetCell[] dNetCellss = new DNetCell[allList.size()];
					allList.toArray(dNetCellss);
					
					for (int i = 0; i < dNetCellss.length; i++)
					{
						DNetCell cell = dNetCellss[i];
						
						// 定义二级远端坐标
						if (!cell.getLocalNetCellID().equals(cell.getParentNetCellID()))
						{
							if (ht1.get(cell.getParentNetCellID()) != null)
							{
								DNetCell netcell = ht1.get(cell.getParentNetCellID());
								if (cell.getParentNetCellID().equals(netcell.getId()))
								{
									cell.setCoordinateX(netcell.getCoordinateX());
									
									cell.setCoordinateY(netcell.getCoordinateY() + 60);
									
								}
							}
						}
						
						retNetCells[i] = cell;
					}
				}
				
			}
			
			return retNetCells;
		}
		
	}