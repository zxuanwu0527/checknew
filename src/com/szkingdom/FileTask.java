package com.szkingdom;

import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.szkingdom.a.XMLUtil;


//import com.szkingdom.cms.dao.impl.CmsNewsDAOImpl;

/**
 * 定时执行文件检测程序
 * 
 * @author tanlun
 */
public class FileTask extends TimerTask {
	private static final Logger log = Logger.getLogger(FileTask.class);

	private static org.dom4j.Document luceneConfig_a;
	private static org.dom4j.Document luceneConfig_b;
	private static org.dom4j.Document luceneConfig_c;
	
	private static boolean  group1_flag = true;
	private static boolean  group2_flag = true;
	private static boolean  group3_flag = true;
	
	
	public void run()
	{
		
		//Date start = new Date();
		
		luceneConfig_a = XMLUtil.loadFile("a/FileConfig.xml");//用于配置数据库参数的
		luceneConfig_b = XMLUtil.loadFile("b/FileConfig.xml");//用于配置数据库参数的
		luceneConfig_c = XMLUtil.loadFile("c/FileConfig.xml");//用于配置数据库参数的

		List nodelist = null;
		Iterator it = null;
		
		nodelist = luceneConfig_a.selectNodes("//root/start");
		it = nodelist.iterator();
		while(it.hasNext())
		{
			Element pathelement = (Element)it.next();
			group1_flag = Boolean.valueOf(((String)pathelement.getText()).toLowerCase()).booleanValue();
		}
		log.info(group1_flag+"");
		
		nodelist = luceneConfig_b.selectNodes("//root/start");
		it = nodelist.iterator();
		while(it.hasNext())
		{
			Element pathelement = (Element)it.next();
			group2_flag = Boolean.valueOf(((String)pathelement.getText()).toLowerCase()).booleanValue();
		}
		log.info(group2_flag+"");
		
		nodelist = luceneConfig_c.selectNodes("//root/start");
		it = nodelist.iterator();
		while(it.hasNext())
		{
			Element pathelement = (Element)it.next();
			group3_flag = Boolean.valueOf(((String)pathelement.getText()).toLowerCase()).booleanValue();
		}
		log.info(group3_flag+"");
		
		if(group1_flag)
		{
			
			Thread group1 = new Thread(new com.szkingdom.a.TestFileXML());
			
			group1.start();
		}
		
		if(group2_flag)
		{
			Thread group2 = new Thread(new com.szkingdom.b.TestFileXML());
			
			group2.start();
		}

		if(group3_flag)
		{
			Thread group3 = new Thread(new com.szkingdom.c.TestFileXML());
			  
			group3.start();
		}
		
		/*
		Date end = new Date();
		log.debug("start at "+start.getTime());
		log.debug("end   at "+end.getTime());yan
		log.debug(end.getTime() - start.getTime() + " total milliseconds");	
		*/	
	}
	
}
