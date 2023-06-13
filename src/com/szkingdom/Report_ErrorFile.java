package com.szkingdom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jxl.Workbook;
import jxl.write.Alignment;
import jxl.write.Border;
import jxl.write.BorderLineStyle;
import jxl.write.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;

import com.szkingdom.a.XMLUtil;

public class Report_ErrorFile {

	/**
	 * @param args
	 */
	private static Logger logger = Logger.getLogger(Report_ErrorFile.class);
	private static org.dom4j.Document luceneConfig =null;
	private static String   driverclass = null;
	private static String   url = null;
	private static String   userid = null;
	private static String   password = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * ����������ȯ�̵Ľ����ļ����÷���ֻ��������δ���������ݿ⣬���ڽ��ܲ��ɹ�
	 *����ȯ�̱��͵��ļ���*.list���������Ĵ�������excel�ļ���
	 *ע�⣺��������Ĵ�����Ϣ�����ݼ�����ɴ�����Ϣ��ͬ���Ǵ�����д���ģ������ڴ洢������
	 *ƴ�յġ�
	 **/
	
	//�������ű�һ������ϸ����罨�ģ���һ���ǽ���У���
	public static void createResultFile(String  zqgsdm,String sjrq,String milltime,String information,boolean flag)
	{

		
		if(luceneConfig == null)
		{
			luceneConfig = XMLUtil.loadFile("FileConfig.xml");
			Node node =  luceneConfig.selectSingleNode("//root/driverclass");
			driverclass = node.getText();
			
			node =  luceneConfig.selectSingleNode("//root/url");
			url  = node.getText();
			
			node = luceneConfig.selectSingleNode("//root/userid");
			userid = node.getText();
			
			node = luceneConfig.selectSingleNode("//root/password");
			password = node.getText();
			
		}
		
		
		List nodelist = null;
		Iterator it = null;
		String reportfile ="";
		nodelist = luceneConfig.selectNodes("//root/reportfile");
		it = nodelist.iterator();
		while(it.hasNext())
		{
			Element pathelement = (Element)it.next();
			reportfile = (String)pathelement.getText();
		}
		
		CreateFile(reportfile+zqgsdm+"/");
		Date   report_file_date = new Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File  report_file = null;
    	try {
			
			
		    logger.debug("*********************************");
			logger.debug("***********��ʼ���ɱ����ļ�***********");
			logger.debug("*********************************");
			/*��ǰ��ϵͳ��ǰʱ��Ϊ�����ļ�����
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
            Date   report_file_date = new Date();
			String mdate = sdf.format(report_file_date);
			*/		
			if(flag)
			{
				
				report_file = new File(reportfile+zqgsdm+"/"+zqgsdm+"_"+sjrq+"_SUCCESS.txt");
				if(report_file.exists())
				{
					report_file = null;
					report_file = new File( reportfile+zqgsdm+"/"+zqgsdm+"_"+sjrq+"_SUCCESS_"+report_file_date.getTime()+".txt");
				}
			}
			else 
			{
				report_file = new File(reportfile+zqgsdm+"/"+zqgsdm+"_"+sjrq+"_FAILURE.xls");
				if(report_file.exists())
				{
					report_file = null;
					report_file = new File( reportfile+zqgsdm+"/"+zqgsdm+"_"+sjrq+"_FAILURE_"+report_file_date.getTime()+".xls");
				}
			}
			
			System.out.println(report_file);
			if(flag)
			{
				FileOutputStream fos = new FileOutputStream(report_file);				
				BufferedWriter bw   = new BufferedWriter(new OutputStreamWriter(fos));
				String s =zqgsdm+"  "+sjrq+"�ϱ������Ѿ�ͨ������ɹ���";
				bw.write(s+"\n");
				bw.close();
				fos.close();
				
			}
			else
			{
				WritableWorkbook book= Workbook.createWorkbook(report_file); 
				//������Ϊ"���󱨸�"�Ĺ���������0��ʾ���ǵ�һҳ 
	            WritableSheet sheet=book.createSheet("���󱨸�",0); 
	            WritableFont font = new WritableFont(WritableFont.TAHOMA, 9, WritableFont.BOLD);// ��������   
				font.setColour(Colour.BLACK);
				// ������ɫ   
	            WritableCellFormat wc = new WritableCellFormat(font);   
	            wc.setAlignment(Alignment.CENTRE); // ���þ���  
	            wc.setBorder(Border.ALL, BorderLineStyle.THIN); // ���ñ߿���   
	            //wc.setBackground(jxl.format.Colour.BLUE_GREY); // ���õ�Ԫ��ı�����ɫ   
	             
	            Label lable  = null;
	            
	            //���ñ�ͷ
	            
	            lable = new Label(0,0,"��������",wc);
	            sheet.addCell(lable); 
	            //sheet.mergeCells(arg0, arg1, arg2, arg3)
	            
	            lable = new Label(1,0,"����",wc);
	            sheet.addCell(lable);
	            
	            //��д������Ϣ
	            lable = new Label(0,1,"000000",wc);
	            sheet.addCell(lable); 
	            
	            sheet.setColumnView(1, information.length()*2);
	            System.out.println(sheet.getColumnWidth(1));
	            lable = new Label(1,1,information,wc);
	            sheet.addCell(lable);
	            
	           
	            book.write();
	            book.close();
	            
	          
			}
			
			//��������־��Ϣ
			Connection conn = null;
			PreparedStatement stm = null;
			String sql1 = null;	
			String sql2 = null;	
			
			Class.forName(driverclass);			
			conn = DriverManager.getConnection(url, userid, password);		
		    
			sql1 = "insert into rzrq.T_SJZX_RZRQ_JKSJ_JHJG@db_push(SJRQ,ZQGSDM,JYRQ,CHECK_RTN,JYJG,TZBZ,sfsb) " +
		    		"values(?,?,?,?,?,?,?)";
			stm = conn.prepareStatement(sql1);
			stm.setString(1, sjrq);
			stm.setString(2, zqgsdm);
			stm.setString(3,sdf.format(report_file_date));
			stm.setLong(4,Long.parseLong(milltime));
			stm.setString(5, "02");
			stm.setString(6, "01");
			stm.setString(7, "01");
			stm.execute();	    
			stm.close();
			stm = null;
			
			//������ؼ�����Ϣ����ϸ��
			sql2 = "insert into krcs.t_krms_check_jksj_jksbcwxx@db_push(SJRQ,ZQGSDM,JCRQ,CHECK_RTNX,CWXX) " +
    		"values(?,?,?,?,?)";
			stm = conn.prepareStatement(sql2);
			stm.setString(1, sjrq);
			stm.setString(2, zqgsdm);
			stm.setString(3,sdf.format(report_file_date));
			stm.setLong(4,Long.parseLong(milltime));
			stm.setString(5,information);
			stm.execute();	    
			stm.close();
			stm = null;
			
			conn.close();
			conn = null;
			
			//���ɼ�����󱨸���fdepƽ̨�ϵ�ok��־
			if(report_file!=null)
			{
	            File okfile = new File(report_file.getPath()+".ok");
				
				
				
				try {
					FileOutputStream fis = new FileOutputStream(okfile);
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					logger.debug("������OK�ļ�ʱ�������ļ�ʧ��!");
					LogUtils.debugStackTrace(e.getStackTrace());
				}
			}
	            
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.debug("�����ɼ��鱨��ʱ,�ļ�"+report_file.getName()+"û���ҵ�!");
			LogUtils.debugStackTrace(e.getStackTrace());
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("�����ɼ��鱨��ʱ,д"+report_file.getName()+"�ļ�����!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			logger.debug("�����ɼ��鱨��ʱ,д"+report_file.getName()+"�ļ�����!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
			logger.debug("��д��־ʱ,���ݿ�����ʧ��!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.debug("��д��־ʱ,�������ݿ�ʧ��!");
			LogUtils.debugStackTrace(e.getStackTrace());
		}
	}
	
	/**
	    * ���ڴ����ļ��У���������ڣ��򴴽����ļ���
	    * @param path 
	    */
	static  void   CreateFile(String   path)
	{ 
		
        //ѭ�������ļ���
		String s = path;
		String m =""; 
		int j = path.indexOf("/");
	    for(;j>0;)
		{
			
			m = path.substring(0, j+1);
			File   file   =   new   File(m);    
		    if(file.isDirectory()){       
		       System.out.println("the   dir   is   exits");       
		    }else{       
		       file.mkdir();       
		       System.out.println("have   made   a   dir   ��"   );       
		    }
			s = s.replaceFirst("/", ".");
				
			j = s.indexOf('/');
			
		}
          
	}    

}
