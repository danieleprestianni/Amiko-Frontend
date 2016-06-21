package io.amiko.app.doctorsapp.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigLoader {

	private Properties configFile = new Properties();
	private static final Logger logger = Logger.getLogger(ConfigLoader.class);
	private static ConfigLoader instance = null;
	
	private ConfigLoader(){
		InputStream is = getClass().getResourceAsStream("/config.properties");
		configFile = new Properties();
		if(is!=null){
			try {
				configFile.load(is);
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}else{
			logger.error("Property file bluegiga.properties not found");
		}
	}
	
	public static ConfigLoader getInstance(){
		if(instance==null){
			instance = new ConfigLoader();
		}
		return instance;
	}
	
	public String getProperty(String key){
		return this.configFile.getProperty(key);
	}
	
}
