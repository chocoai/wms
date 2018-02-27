package com.xyy.http.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServiceManager {
	private static String config = "service_config.xml";
	private static ServiceManager _instance = null;
	private ConcurrentMap<String, String> _serviceMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, Service> _serviceInstanceMap = new ConcurrentHashMap<String, Service>();

	public static ServiceManager getInstance() {
		if (_instance == null) {
			synchronized (ServiceManager.class) {
				if (_instance == null) {
					_instance = new ServiceManager();
				}
			}
		}
		return _instance;
	}

	private ServiceManager() {
		this.init();
	}

	private void init() {
		//return (Service) Class.forName(className).newInstance();
		Properties properties = new Properties();
		InputStream in = this.getClass().getResourceAsStream(config);
		try {
			properties.loadFromXML(in);
			Set<String> names = properties.stringPropertyNames();
			for (String name : names) {
				String clazz=properties.getProperty(name);
				try{
					Service s_instance=(Service) Class.forName(clazz).newInstance();
					if(s_instance!=null){
						this._serviceMap.put(name, clazz);
						this._serviceInstanceMap.put(name, s_instance);
					}
				}catch (Exception e) {
					e.printStackTrace();
					continue;
				} 
				
				
			}

		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public String getServiceClassName(String servcie) {
		return this._serviceMap.get(servcie);
	}

	
	
		
	
	
	
	public Service getService(String servcie) {
		if(!this._serviceInstanceMap.containsKey(servcie)){
			return null;
		}		
		return this._serviceInstanceMap.get(servcie);	
	}
	

	

	public static void main(String[] args) {
		ServiceManager.getInstance();
	}
}
