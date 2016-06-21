package io.amiko.app.utils.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

	public Properties load(String propertiesResourceName) throws IOException,FileNotFoundException{
		InputStream is = getClass().getResourceAsStream(propertiesResourceName+".properties");
		Properties props = new Properties();
		if(is!=null){
			props.load(is);
		}else{
			throw new FileNotFoundException("Property file " + propertiesResourceName+".properties not found");
		}
		return props;
	}
}
