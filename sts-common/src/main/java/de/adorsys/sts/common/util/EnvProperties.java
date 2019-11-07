package de.adorsys.sts.common.util;

public final class EnvProperties {

    public static String getEnvOrSysProp(String propName, boolean optional) {
		String propValue = System.getenv(propName);

		if(isBlank(propValue))propValue = System.getProperty(propName);
		
		if(isBlank(propValue)) {
			if (optional)return null;
			throw new IllegalStateException("Missing Environmen property " + propName);
		}
		return propValue;
	}

	public static String getEnvOrSysProp(String propName, String defaultValue) {
		String propValue = System.getenv(propName);
		
		if(isBlank(propValue))propValue = System.getProperty(propName);
		
		if(isBlank(propValue))return defaultValue;
		
		return propValue;
	}

	private static boolean isBlank(String value) {
    	return null == value || value.isEmpty();
	}
}
