/**
 * @author zhangyunpeng
 * ���ƶ�д�����ļ��Ĳ��� 
 * �������ƶ�д�����ļ���ÿ��������Ҫ��һ��Ψһ��KEY������ConstantDefine���棬��������������#����������KEY�������Ͷ�ȡ
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
public class FileAccess{
	
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
	     * �����ļ�����
	     * @param key ���Թؼ���
	     * @param fileprop ����
	     */
	    public void setProp(String key,FileProp fileprop)
	    {
	    	if(hash.get(key) == null)
	    		hash.put(key, fileprop);
	    }
	    
	    /**
	     * @return �ļ��Ƿ�����
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
	    ���ļ����ݷ��ص�һ���ַ�����ȥ
	    �����д��󷵻� null
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
	     *  ���ļ�д���µ�����writemod�ķ�ʽ��2��
	    	0�������ļ����ڣ��µ�����׷�ӵ��ļ������򴴽�һ�����ļ� ע�⣡��ֻ��һ��һ�е�д ���򷵻ش���
	    	1: �����ļ����ڣ����Ǿ��ļ������ݡ����򴴽�һ�����ļ�
	     * @param strInputContent
	     * @param iType
	     * @throws IOException
	     */
	    public void Write(String strInputContent,int iType) throws IOException
	    {
	    	 if(strInputContent.split(System.getProperty("line.separator")).length != 1)
	    	 {
	    		 System.err.println("д�ļ�ʱֻ��һ��һ�в��룡");
	    		 return;
	    	 }  
	    	 
	    	 String[] conts = strInputContent.split("#");
	    	 String key = conts[0];
	    	 
	    	 if(strInputContent == null || key == null)
	    	 {
	    		 System.err.println("���������д�����");
	    		 return;
	    	 }
	    	 FileProp fileProp = (FileProp)hash.get(key);
	    	 if(fileProp == null)
	    	 {
	    		 System.err.println("û��������Ϣ���ԣ�");
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
	           case ConstantDefine.FileWriteType.ADD:    //׷���µ�����
	        	   
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
	        		   //���������������� �򸲸�
	        		   if(fileProp.getMaxLine() != 0)
		        	   {
			        	   if(iSameKeyCount == fileProp.getMaxLine()+1)
			        	   {
			        			exist[i-(fileProp.getMaxLine()-1)] = strInputContent;
			        			whetherOver = true;
			        			break;
			        	   }
		        	   }
	        		   
	        		   //����û�г���
	        		   String[] lastlineExist = exist[lastLineIndex].split("#");
	        		   if(lastlineExist[0].equals(key))
        			   {
	        			   //�����ǽ�β
	        			   if(i == exist.length-1)
	        			   {
	        				   iStartLine = i; 
	        				   break;
	        			   }   
	        			   //���м�
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
	        		    	//������û�и������� ��ĩβ���Ӹ�������
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
	           case ConstantDefine.FileWriteType.OVERLY:    //������ֱ�Ӹ��Ǿ����� ˵��������ֻ��һ�У�
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
	        		   //û����������
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
		 * ȡ�ñ�������
		 * @param key
		 * @return �������� lengthΪ�������� û�з���String[0]
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
		 * ��ȫ����ĳ������
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
