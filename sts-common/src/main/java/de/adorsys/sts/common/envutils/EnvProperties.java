package de.adorsys.sts.common.envutils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvProperties {

    public String getEnvOrSysProp(String propName, boolean optional) {
		String propValue = System.getenv(propName);

		if(isBlank(propValue))propValue = System.getProperty(propName);
		
		if(isBlank(propValue)) {
			if (optional)return null;
			throw new IllegalStateException("Missing Environment property " + propName);
		}
		return propValue;
	}

	public String getEnvOrSysProp(String propName, String defaultValue) {
		String propValue = System.getenv(propName);
		
		if(isBlank(propValue))propValue = System.getProperty(propName);
		
		if(isBlank(propValue))return defaultValue;
		
		return propValue;
	}
	
	private boolean isBlank(String value) {
    	return null == value || "".equals(value) || "".equals(value.trim());
	}
}
