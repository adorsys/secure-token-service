package de.adorsys.sts.cryptoutils.envutils;

import org.apache.commons.lang3.StringUtils;

public class EnvProperties {
    public static String getEnvOrSysProp(String propName, boolean optional) {
		String propValue = System.getenv(propName);

		if(StringUtils.isBlank(propValue))propValue = System.getProperty(propName);
		
		if(StringUtils.isBlank(propValue)) {
			if (optional)return null;
			throw new IllegalStateException("Missing Environmen property " + propName);
		}
		return propValue;
	}

	public static String getEnvOrSysProp(String propName, String defaultValue) {
		String propValue = System.getenv(propName);
		
		if(StringUtils.isBlank(propValue))propValue = System.getProperty(propName);
		
		if(StringUtils.isBlank(propValue))return defaultValue;
		
		return propValue;
	}
}
