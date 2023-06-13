package com.szkingdom;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.szkingdom.a.XMLUtil;

/**
 * �ļ�У�����������
 * @author tanlun
 */
public class XMLFileMain{
    
	private static final Logger log = Logger.getLogger(XMLFileMain.class);
	//private static final long DELAY_TIME = 1*1000;
	//private static final long PERIOD_TIME = 3600*1000;
	private static org.dom4j.Document luceneConfig_a;
	private static org.dom4j.Document luceneConfig_b;
	private static org.dom4j.Document luceneConfig_c;
	
	private static boolean  group1_flag = true;
	private static boolean  group2_flag = true;
	private static boolean  group3_flag = true;

	public static void main(String[] args)
	{
		log.info("�ļ�У������������...");
		//ConfigParse cfg = new ConfigParse();
		
		//long delaytime = cfg.getDelayTime();
		//System.out.println(delaytime);
		//long periodtime = cfg.getPeriodTime();
		//System.out.println(periodtime);
		
		//if (delaytime <= 0) delaytime = DELAY_TIME;
		//if (periodtime <= 0) periodtime = PERIOD_TIME;
		//if (log.isDebugEnabled())
		//log.debug(ConfigParse.getDbconfig());
		
		/*FileTask filetask = new FileTask();
		Timer timer = new Timer(); 
		timer.schedule(filetask, 1000, 3600*1000*10);*/
		luceneConfig_a = XMLUtil.loadFile("a/FileConfig.xml");//�����������ݿ������
		luceneConfig_b = XMLUtil.loadFile("b/FileConfig.xml");//�����������ݿ������
		luceneConfig_c = XMLUtil.loadFile("c/FileConfig.xml");//�����������ݿ������

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
		
	}
}
