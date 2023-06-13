package com.szkingdom.submission.datacheck;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.szkingdom.a.XMLUtil;
import com.szkingdom.submission.datacheck.po.CheckFile;
import com.szkingdom.submission.datacheck.po.TaskQueueStatus;

public class ObjectFactory {
	private static Logger logger = Logger.getLogger(ObjectFactory.class);
	private static ConcurrentHashMap<String, Object> beanMap  = new ConcurrentHashMap<String, Object>();;
	//配置文件上次更新时间
	private static AtomicLong lastUpdateTime = new AtomicLong(0);
	
	//任务队列
	private static ConcurrentLinkedQueue<CheckFile> preTaskQueue = new ConcurrentLinkedQueue<CheckFile>();
	private static ConcurrentLinkedQueue<CheckFile> runingTaskQueue = new ConcurrentLinkedQueue<CheckFile>();
	//可用的程序
	private static Set<String> availableProcessors = new HashSet<String>();
	private static Set<String> unvailableProcessors = new HashSet<String>();
	//子任务超时时间
//	private static final int CONNECT_OUT_TIME = 5000;
	//存放消息：任务数
	private static ConcurrentHashMap<String, TaskQueueStatus>  taskStatusPool = new ConcurrentHashMap<String, TaskQueueStatus>();

	
	private static ObjectFactory of = new ObjectFactory();
	/**
	 * 用于执行1509个存储过程，初始化中心库记录
	 */
	static ExecutorService procServicePool ;
	private void init(){
		logger.info("init applicatinContext");
		//设置上次更新时间
		File configFile = new File("conf/applicationContext.xml");
		lastUpdateTime.set(configFile.lastModified());
		loadBeans(configFile);
		ThreadParam tp = (ThreadParam) beanMap.get("threaParam");
		procServicePool = Executors.newFixedThreadPool(tp.getProcThreadNum());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadBeans(File configFile){
		//读取配置文件
		Document datasource = XMLUtil.loadFile(configFile);
		List<Element> beans = datasource.selectNodes("//beans/bean");
		if(beans != null && beans.size() > 0){
			for(int i = 0; i < beans.size(); i++){
				Element bean = beans.get(i);
				String beanId = bean.attribute("id").getValue();
				String beanName = bean.attribute("class").getValue();
				try {
					Class beanClass = Class.forName(beanName);
					//获取对应class的信息
				       java.beans.BeanInfo info = java.beans.Introspector.getBeanInfo(beanClass);
				       //获取其属性描述
				       java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
				        
					//创建对象
				      Object beanInstance = beanClass.newInstance();
				      
				      List<Element> properties = bean.selectNodes("property");
				      if(properties != null && properties.size() > 0){
				      	for(int proIndex = 0; proIndex < properties.size(); proIndex++){
				      		String propertyName = properties.get(proIndex).attributeValue("name");
				      		Element property = properties.get(proIndex);
				      		for (int k = 0; k < pd.length; k++) {
						      	Method mSet = pd[k].getWriteMethod();
						      	if(null != mSet){
							    		String name = pd[k].getName();
//									System.out.println("WriteMethod:" + mSet.getName()+": " + name  + ":" + pd[k].getPropertyType().getName());
							    		//属性名是否匹配
							    		if(propertyName.equalsIgnoreCase(name)){
							    			String propertyType = pd[k].getPropertyType().getName();
							    			executeSetMethod(mSet, property, propertyType, beanInstance);
							    			break;
							    		}
						      	}
						        }
				      	}
				      }
				      beanMap.put(beanId, beanInstance);
				} catch (Exception e) {
					logger.error("error!", e);
				}
			}
		}
	}
	/**
	 * 重新加载bean对象
	 * @return 重新加载：true 没有重新加载：false
	 */
	public boolean reloadBeans(){
		File configFile = new File("applicationContext.xml");
		long lastModifiedTime = configFile.lastModified();
		if(lastModifiedTime > lastUpdateTime.get()){
			lastUpdateTime.set(lastModifiedTime);
			loadBeans(configFile);
			return true;
		}
		return false;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void executeSetMethod(Method mSet, Element property, String type, Object beanInstance) throws Exception{
			if(type.equals("int")){
				String propertyValue = property.attributeValue("value").trim();
				int value = Integer.parseInt(propertyValue);
				mSet.invoke(beanInstance, value);
			}else if(type.equalsIgnoreCase("boolean")){
				String propertyValue = property.attributeValue("value").trim();
				boolean value = Boolean.parseBoolean(propertyValue);
				mSet.invoke(beanInstance, value);
			}else if(type.equalsIgnoreCase("java.lang.String")){
				String propertyValue = property.attributeValue("value").trim();
				mSet.invoke(beanInstance, propertyValue);
			}else if(type.equalsIgnoreCase("java.util.Map")){
				List<Element> mapEles = property.selectNodes("map[1]");
				if(mapEles != null && mapEles.size() >0){
					Element map = mapEles.get(0);
					List<Element> entries = map.selectNodes("entry");
					if(entries != null && entries.size() > 0){
						Map valueMap = new HashMap();
						for(int entryCount = 0; entryCount < entries.size(); entryCount++){
							String key = entries.get(entryCount).attributeValue("key");
							String value = entries.get(entryCount).attributeValue("value");
							valueMap.put(key, value);
						}
						mSet.invoke(beanInstance, valueMap);
					}
				}
			}else if(type.equalsIgnoreCase("java.util.List")){
				List<Element> listEles = property.selectNodes("list[1]");
				if(listEles != null && listEles.size() > 0){
					Element list = listEles.get(0);
					List<Element> values = list.selectNodes("value");
					if(values != null && values.size() > 0){
						List valueList = new ArrayList();
						for(int listCount = 0; listCount < values.size(); listCount++){
							String value = values.get(listCount).getText();
							valueList.add(value);
						}
						mSet.invoke(beanInstance, valueList);
					}
				}
			}else if(StringUtils.isBlank(property.attributeValue("value"))){
				//value为空，则认为指向了对象
				String beanId = property.attributeValue("ref").trim();
				if(StringUtils.isBlank(beanId)){
					throw new Exception("Object with id:" + beanId + " should have a value of ref object!");
				}
				Object o = beanMap.get(beanId);
				if(o == null){
					throw new Exception("Object with id:" + beanId + " not found!");
				}
				mSet.invoke(beanInstance, o);
			}
	}
	public synchronized Connection getConnection() throws SQLException{
		ComboPooledDataSource datasource = (ComboPooledDataSource)beanMap.get("dataSource");
		if(datasource == null){
			throw new SQLException("datasource is null");
		}
	      return datasource.getConnection();
	}
	
	public Object getBean(String beanName){
		return beanMap.get(beanName);
	}
	public Object setBean(String beanName, Object obj){
		return beanMap.put(beanName, obj);
	}
	
	public static ObjectFactory instance(){
		return of;
	}
	
	private ObjectFactory() {
		init();
	}

	public void addPreTask(CheckFile task){
		preTaskQueue.add(task);
	}
	public void addRunningTask(CheckFile cf){
		runingTaskQueue.add(cf);
	}
	public final ConcurrentLinkedQueue<CheckFile> getPreTaskQueue() {
		return preTaskQueue;
	}

	public final ConcurrentLinkedQueue<CheckFile> getRuningTaskQueue() {
		return runingTaskQueue;
	}

	public synchronized void addProcessor(String processorinfo){
		availableProcessors.add(processorinfo);
		if(unvailableProcessors.contains(processorinfo)){
			unvailableProcessors.remove(processorinfo);
		}
	}
	public synchronized void removeProcessor(String processorinfo){
		if(availableProcessors.contains(processorinfo)){
			availableProcessors.remove(processorinfo);
		}
		unvailableProcessors.add(processorinfo);
	}
	
	public  final Set<String> getAvailableProcessors() {
		return availableProcessors;
	}

	public  final Set<String> getUnvailableProcessors() {
		return unvailableProcessors;
	}

	public  final ConcurrentHashMap<String, TaskQueueStatus> getTaskStatusPool() {
		return taskStatusPool;
	}
	public  final void putTaskStatus2Pool(String key, TaskQueueStatus tqs) {
		taskStatusPool.put(key, tqs);
	}

	public static void main(String[] args){
		ObjectFactory of = new ObjectFactory();
		of.init();
		Object obj = of.getBean("dataSource");
		System.out.println(obj);
		DataCheckProperty dcp = (DataCheckProperty) ObjectFactory.instance().getBean("dataCheckProperty");
		System.out.println(dcp);
		ThreadParam tp = (ThreadParam)ObjectFactory.instance().getBean("threaParam");
		System.out.println(tp);
	}
}
