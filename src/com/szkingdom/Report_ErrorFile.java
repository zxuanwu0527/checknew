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
	 * 用于生成与券商的交互文件，该方法只生成数据未到备用数据库，由于解密不成功
	 *或者券商报送的文件和*.list不符产生的错误，生成excel文件。
	 *注意：这个方法的错误信息更数据检查生成错误信息不同，是传过来写死的，不是在存储过程中
	 *拼凑的。
	 **/
	
	//插人两张表，一个是明细表（彪哥建的），一个是交互校验表。
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
			logger.debug("***********开始生成报告文件***********");
			logger.debug("*********************************");
			/*以前用系统当前时间为交互文件命名
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
				String s =zqgsdm+"  "+sjrq+"上报数据已经通过检验成功。";
				bw.write(s+"\n");
				bw.close();
				fos.close();
				
			}
			else
			{
				WritableWorkbook book= Workbook.createWorkbook(report_file); 
				//生成名为"错误报告"的工作表，参数0表示这是第一页 
	            WritableSheet sheet=book.createSheet("错误报告",0); 
	            WritableFont font = new WritableFont(WritableFont.TAHOMA, 9, WritableFont.BOLD);// 定义字体   
				font.setColour(Colour.BLACK);
				// 字体颜色   
	            WritableCellFormat wc = new WritableCellFormat(font);   
	            wc.setAlignment(Alignment.CENTRE); // 设置居中  
	            wc.setBorder(Border.ALL, BorderLineStyle.THIN); // 设置边框线   
	            //wc.setBackground(jxl.format.Colour.BLUE_GREY); // 设置单元格的背景颜色   
	             
	            Label lable  = null;
	            
	            //设置表头
	            
	            lable = new Label(0,0,"检验名称",wc);
	            sheet.addCell(lable); 
	            //sheet.mergeCells(arg0, arg1, arg2, arg3)
	            
	            lable = new Label(1,0,"描述",wc);
	            sheet.addCell(lable);
	            
	            //编写错误信息
	            lable = new Label(0,1,"000000",wc);
	            sheet.addCell(lable); 
	            
	            sheet.setColumnView(1, information.length()*2);
	            System.out.println(sheet.getColumnWidth(1));
	            lable = new Label(1,1,information,wc);
	            sheet.addCell(lable);
	            
	           
	            book.write();
	            book.close();
	            
	          
			}
			
			//处理交互日志信息
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
			
			//插入相关检验信息到明细表
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
			
			//生成检验错误报告在fdep平台上的ok标志
			if(report_file!=null)
			{
	            File okfile = new File(report_file.getPath()+".ok");
				
				
				
				try {
					FileOutputStream fis = new FileOutputStream(okfile);
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					logger.debug("在生成OK文件时，创建文件失败!");
					LogUtils.debugStackTrace(e.getStackTrace());
				}
			}
	            
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.debug("在生成检验报告时,文件"+report_file.getName()+"没有找到!");
			LogUtils.debugStackTrace(e.getStackTrace());
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("在生成检验报告时,写"+report_file.getName()+"文件出错!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			logger.debug("在生成检验报告时,写"+report_file.getName()+"文件出错!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
			logger.debug("在写日志时,数据库连接失败!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.debug("在写日志时,操作数据库失败!");
			LogUtils.debugStackTrace(e.getStackTrace());
		}
	}
	
	/**
	    * 用于创建文件夹，如果不存在，则创建该文件夹
	    * @param path 
	    */
	static  void   CreateFile(String   path)
	{ 
		
        //循环创建文件夹
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
		       System.out.println("have   made   a   dir   ！"   );       
		    }
			s = s.replaceFirst("/", ".");
				
			j = s.indexOf('/');
			
		}
          
	}    

}
