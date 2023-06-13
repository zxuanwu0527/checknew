package com.szkingdom.submission.datacheck.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang.StringUtils;

public abstract class PropertyUtil {
	private static final String DEFAULT_PROPERTY = "zqgswt";
	private static final String PROPERTY_SUFIX = ".properties";
	private static final Map<String, PropertiesConfiguration> CONFIGURATIONS = new HashMap<String, PropertiesConfiguration>();

	public static void load(String propertyFile){
		PropertiesConfiguration config = CONFIGURATIONS.get(propertyFile);
		if (config == null) {
			try {
				config = new PropertiesConfiguration(propertyFile);
//				config.setReloadingStrategy(new FileChangedReloadingStrategy());
				CONFIGURATIONS.put(DEFAULT_PROPERTY, config);
			} catch (ConfigurationException e) {
				throw new RuntimeException("cannot find property file  : "+ propertyFile, e);
			}
		}
	}
	
	private static PropertiesConfiguration getInstance(String name) {
		String fileName = getPropertyName(name);
		PropertiesConfiguration config = CONFIGURATIONS.get(fileName);
		if (config == null) {
			try {
				config = new PropertiesConfiguration(fileName);
				config.setReloadingStrategy(new FileChangedReloadingStrategy());
				CONFIGURATIONS.put(fileName, config);
			} catch (ConfigurationException e) {
				throw new RuntimeException("cannot find property file for : "+ name, e);
			}
		}
		return config;
	}

	private static String getPropertyName(String name) {
		if (StringUtils.isBlank(name)) {
			name = DEFAULT_PROPERTY;
		}
		return name.endsWith(PROPERTY_SUFIX) ? name : (name += PROPERTY_SUFIX);
	}

	public static PropertiesConfiguration getInstance() {
		return getInstance(null);
	}

	public static String get(String key) {
		return getInstance(DEFAULT_PROPERTY).getString(key);
	}

	public static List<Object> getList(String key) {
		return getInstance(DEFAULT_PROPERTY).getList(key);
	}
	
	public static String get(String key, String propertyInstance) {
		return getInstance(propertyInstance).getString(key);
	}

	public static List<Object> getList(String key, String propertyInstance) {
		return getInstance(propertyInstance).getList(key);
	}
	
	public static void main(String[] args) {
		String sjrq = get("sjrq", "mastloadconf/rptProc.properties");
		System.out.println(sjrq);
		List<Object> procs = getList("proc.group2.proc", "mastloadconf/rptProc.properties");
		System.out.println(procs);
	}
		
}
