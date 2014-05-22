/**
 * @author zhangyunpeng
 * 控制读写本地文件的操作 
 * 该类控制读写本地文件，每项属性需要有一个唯一个KEY定义在ConstantDefine里面，其他相关内容用#隔开，根据KEY来插入和读取
 */
package cn.com.postel.da.client.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;



import cn.com.postel.da.client.business.ConstantDefine;

@SuppressWarnings("unchecked")
public class FileAccess
	{
		private String fileName,fileHead;
		
	    private Hashtable hash = new Hashtable();
	    
	    public FileAccess(String name)
	    {
	    	fileName = name;
	    	
	    }

	    public String getFileName()
	    {
	       return fileName;
	    }
	    
	    /**
	     * 设置文件属性
	     * @param key 属性关键字
	     * @param fileprop 属性
	     */
	    public void setProp(String key,FileProp fileprop)
	    {
	    	if(hash.get(key) == null)
	    		hash.put(key, fileprop);
	    }
	    
	    /**
	     * @return 文件是否存在
	     */
	    public boolean fileExist()
	    {
	    	File file=new File(fileName);
	    	if(!file.exists())
		    {
		        return false;
		    }
	    	return true;
	    }
	    /*
	    将文件内容返回到一个字符串里去
	    如果有错误返回 null
	    */
	    private String getAllContent()
	    {
	      String content="";
	      File file=new File(fileName);

	      if(!file.exists())
	      {
	        return content;
	      }
	      else if(file!=null)
	      {
	        try
	        {
	          BufferedReader reader=new BufferedReader(new FileReader(file));
	          String inLine=reader.readLine();

	          while(inLine !=null)
	          {
	            content+=(inLine+System.getProperty("line.separator"));
	            inLine=reader.readLine();
	          }

	          return content;

	        }
	        catch(IOException e)
	        {
	          return content;
	        }
	      }
	      else
	      {
	        return content;
	      }

	   }

	    /**
	     *  对文件写入新的内容writemod的方式有2种
	    	0：如果文件存在，新的内容追加到文件。否则创建一个新文件 注意！！只能一行一行的写 否则返回错误
	    	1: 如果文件存在，覆盖旧文件的内容。否则创建一个新文件
	     * @param strInputContent
	     * @param iType
	     * @throws IOException
	     */
	    public void Write(String strInputContent,int iType) throws IOException
	    {
	    	 if(strInputContent.split(System.getProperty("line.separator")).length != 1)
	    	 {
	    		 System.err.println("写文件时只能一行一行插入！");
	    		 return;
	    	 }  
	    	 
	    	 String[] conts = strInputContent.split("#");
	    	 String key = conts[0];
	    	 
	    	 if(strInputContent == null || key == null)
	    	 {
	    		 System.err.println("输入数据有错误！");
	    		 return;
	    	 }
	    	 FileProp fileProp = (FileProp)hash.get(key);
	    	 if(fileProp == null)
	    	 {
	    		 System.err.println("没有设置信息属性！");
	    		 return;	 
	    	 }
	    	 boolean fileExist = fileExist();
	         File fso=new File(fileName);

	         String strContent=new String("");

	         FileAccess objFRead=new FileAccess(fileName);
             String oldContent=new String("");
             oldContent=objFRead.getAllContent();
	         
	         BufferedWriter writer=new BufferedWriter(new FileWriter(fso));

	         boolean head = false;
	         
		     String[] exist = oldContent.split(System.getProperty("line.separator"));
		     if(!exist[0].equals(System.getProperty("line.separator")))
		        head = true;	
	         
	        	 
	         
	         int iSameKeyCount = 0;
	         switch (iType)
	         {
	           case ConstantDefine.FileWriteType.ADD:    //追加新的内容
	        	   
	        	   String newContent = "";
	        	   int iStartLine = 0;

	        	   
	        	   int lastLineIndex = 0;
	        	   boolean whetherOver = false;
	        	   
	        	   for(int i=0;i<exist.length;i++)
	        	   {
	        		   
	        		   String[] curlineExist = exist[i].split("#");
	        		   if(curlineExist.length<=1)
	        			   continue;
	        			   
	        		   if(curlineExist[0].equals(key))
	        		   {
	        			   if(iStartLine == 0)
	        			   {
	        				   if(curlineExist[1].equals(""))
	        				   {
	        					   exist[i] = fileProp.getName()+"#"+fileProp.getDescrp()+System.getProperty("line.separator");
	        				   }   
	        			   } 
	        			   
	        			   iStartLine = i;
	        			   iSameKeyCount++;
	        		   }
	        		   //如果超出最大行数 则覆盖
	        		   if(fileProp.getMaxLine() != 0)
		        	   {
			        	   if(iSameKeyCount == fileProp.getMaxLine()+1)
			        	   {
			        			exist[i-(fileProp.getMaxLine()-1)] = strInputContent;
			        			whetherOver = true;
			        			break;
			        	   }
		        	   }
	        		   
	        		   //如果没有超出
	        		   String[] lastlineExist = exist[lastLineIndex].split("#");
	        		   if(lastlineExist[0].equals(key))
        			   {
	        			   //如果是结尾
	        			   if(i == exist.length-1)
	        			   {
	        				   iStartLine = i; 
	        				   break;
	        			   }   
	        			   //在中间
	        			   if(!lastlineExist[0].equals(curlineExist[0]))
	        			   {
	        				   iStartLine = lastLineIndex;    
	        				   break;
	        			   }
        			   }
	        		   
	        		   lastLineIndex = i;
	        	   } 
	        	   
	        	   if(!whetherOver)
	        	   {
	        		    if(iSameKeyCount == 0)
	        		    {
	        		    	//如果还没有该项属性 在末尾添加该项属性
	        		    	String add = System.getProperty("line.separator")+System.getProperty("line.separator") + fileProp.getName() + "#" + fileProp.getDescrp();
	        		    	add += System.getProperty("line.separator") + strInputContent;
	        		    	exist[exist.length-1] = exist[exist.length-1] + add;
	        		   
	        		    }
	        		    else
	        		    {
	        		    	exist[iStartLine] += System.getProperty("line.separator");
	        		    	exist[iStartLine] += strInputContent;
	        		    }
	        		    
				         
	        	   }
	        	   
	        	   newContent = "";
	        	   for(int i=0;i<exist.length;i++)
	        	   {
	        		   newContent += exist[i]; 
	        		   newContent += System.getProperty("line.separator");
	        	   }
	        	   
	               strContent=newContent;
	               break;
	           case ConstantDefine.FileWriteType.OVERLY:    //新内容直接覆盖旧内容 说明该属性只有一行！
	           default:
	        	   int overlyLine = 0;
	        	   for(int i=0;i<exist.length;i++)
	        	   {
	        		   
	        		   String[] curlineExist = exist[i].split("#");
	        		   if(curlineExist.length<=1)
	        			   continue;
	        		   if(curlineExist[0].equals(key))
	        		   {
	        			   if(iSameKeyCount == 0)
	        			   {
	        				   if(curlineExist[1].equals(""))
	        				   {
	        					   exist[i] = fileProp.getName()+"#"+fileProp.getDescrp()+System.getProperty("line.separator");
	        				   } 
	        				   overlyLine = i;
	        			   } 
	        			   
	        			   iSameKeyCount++;
	        		   }
	        	   }
	               
	        	   if(iSameKeyCount == 0)
	        	   {
	        		   //没有这项属性
	        		   String add = System.getProperty("line.separator")+System.getProperty("line.separator") + fileProp.getName() + "#" + fileProp.getDescrp();
       		    	   add += System.getProperty("line.separator") + strInputContent;
       		    	   exist[exist.length-1] = exist[exist.length-1] + add;
	        	   }
	        	   else
	        	   {
	        		   exist[overlyLine+1] = strInputContent;   
	        		   
	        	   }
	        	   
	        	   
		           newContent = "";
	        	   for(int i=0;i<exist.length;i++)
	        	   {
	        		   newContent += exist[i]; 
	        		   newContent += System.getProperty("line.separator");
	        	   }
	        	   
	               strContent=newContent;
	               break;
	         }
	         
	         String writeStr = "";
	         
	         
	         if(!fileExist || !head)
	        	 writeStr = fileHead + System.getProperty("line.separator") + System.getProperty("line.separator") + strContent;
	         else
	        	 writeStr = strContent;
	         
	         writer.write(writeStr);

	         writer.close();

	    }

		public void setFileHead(String fileHead)
		{
			this.fileHead = fileHead;
		}

		/**
		 * 取得本地内容
		 * @param key
		 * @return 内容数组 length为内容条数 没有返回String[0]
		 */
		public String[] getFileDataFormKey(String key)
		{
			String[] result;
			
			int start = 0,end = 0;
			FileAccess objFRead=new FileAccess(fileName);
            String content=new String("");
            content=objFRead.getAllContent();
            
            String[] exist = content.split(System.getProperty("line.separator"));
			
            for(int i=0;i<exist.length;i++)
            {
            	String[] curlineExist = exist[i].split("#");
     		    if(curlineExist.length<=1)
     			   continue;
     		    if(curlineExist[0].equals(key))
     		    {
     		    	if(start == 0)
     		    		start = i;
     		    	
	    		    if(i == exist.length || exist[i].split("#").length <1)
	     		    {
	     		    	break;
	     		    }	
	     		    
	     		    end = i;
            	}
     		    else
     		    {
     		    	if(start>0)
     		    		break;
     		    }
            }
            if(start == 0 ||  end == 0)
            	return new String[0];
            
            result = new String[end-start];
            for(int i=start+1;i<=end;i++)
            {
            	result[i-start-1] = exist[i]; 
            }
			return result;
		}
		/**
		 * 完全清除某项内容
		 * @param key
		 */
		public void clearOneContent(String key) throws IOException
		{
			if(!fileExist())
				return;
	       
	        FileAccess objFRead = new FileAccess(fileName);
	        
	        String oldContent=new String("");
            oldContent=objFRead.getAllContent();
            
            
            File fso=new File(fileName);
	        BufferedWriter writer = new BufferedWriter(new FileWriter(fso));
	        
            String newContent = "";
            
            String[] exist = oldContent.split(System.getProperty("line.separator"));
            
            for(int i=0;i<exist.length;i++)
            {
            	String[] strs = exist[i].split("#");
            	if(strs.length>1 && strs[0].equals(key))
            	{
            		continue;
            	}
            	newContent += exist[i]+System.getProperty("line.separator");
            }
             
            writer.write(newContent);
			writer.close();
			
		}
	}
