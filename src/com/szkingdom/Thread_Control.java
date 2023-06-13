package com.szkingdom;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import com.szkingdom.a.XMLUtil;

public class Thread_Control {

	/**
	 * @param args
	 */
	private static org.dom4j.Document luceneConfig;
	
	private static int  threadcount = 1;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ThreadGroup group_rzrq = new ThreadGroup("ThreadGroup_Rzrq");
		
		luceneConfig = XMLUtil.loadFile("FileConfig.xml");//用于配置数据库参数的
		
		List nodelist = null;
		Iterator it = null;
		
		nodelist = luceneConfig.selectNodes("//root/threadcount");
		it = nodelist.iterator();
		while(it.hasNext())
		{
			Element pathelement = (Element)it.next();
			threadcount = Integer.parseInt((String)pathelement.getText());
		}
		
		while(true)
		{
			System.out.println(group_rzrq.activeCount());
			
			Thread group1 = new Thread(group_rzrq,new com.szkingdom.a.TestFileXML(),"threadms");
			System.out.println(group_rzrq.activeCount());
		}
	
	}

}
