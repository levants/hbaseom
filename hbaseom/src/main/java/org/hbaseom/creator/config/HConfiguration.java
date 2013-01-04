package org.hbaseom.creator.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hbaseom.creator.om.HBaseOMCreator;

/**
 * Container class to read and cache HBase client configuration
 * 
 * @author levan
 * 
 */
public final class HConfiguration {

	private static final Map<String, Properties> CONFIGURATION = new HashMap<String, Properties>();

	/**
	 * Default connection pool size
	 */
	public static final int DEFAULT_POOL_SIZE = 10;

	/**
	 * Default recursion depth for entity relations
	 */
	public static final int DEFAULT_RECURSION_DEPTH = 10;

	/**
	 * {@link HBaseOMCreator} not provided error
	 */
	public static final String PROVIDER_NOT_FOUND_ERROR = "Could not find HBaseOM provider name in configuration";

	/**
	 * {@link HBaseOMCreator} not initialized error
	 */
	public static final String PROVIDER_INITIALIZATION_ERROR = "Could not initialize HBaseOM provider from configuration";

	private HConfiguration() {
		throw new AssertionError("Can not instatiate object");
	}

	public static void addProperty(String unitName, String name, String property) {
		Properties properties = CONFIGURATION.get(unitName);
		if (properties == null) {
			properties = new Properties();
			CONFIGURATION.put(unitName, properties);
		}
		properties.put(name, property);
	}

	private static String getProperty(String unitName, String name) {
		Object property = CONFIGURATION.get(unitName).get(name);
		if (property == null) {
			return null;
		} else {
			return (String) property;
		}
	}

	public static String getString(String unitName, String name) {
		return getProperty(unitName, name);
	}

	public static Short getShort(String unitName, String name) {
		String property = getProperty(unitName, name);
		if (property == null) {
			return null;
		} else {
			return Short.valueOf(property);
		}
	}

	public static Integer getInt(String unitName, String name) {
		String property = getProperty(unitName, name);
		if (property == null) {
			return null;
		} else {
			return Integer.valueOf(property);
		}
	}

	public static Long getLong(String unitName, String name) {
		String property = getProperty(unitName, name);
		if (property == null) {
			return null;
		} else {
			return Long.valueOf(property);
		}
	}

	public static Float getFloat(String unitName, String name) {
		String property = getProperty(unitName, name);
		if (property == null) {
			return null;
		} else {
			return Float.valueOf(property);
		}
	}

	public static Double getDouble(String unitName, String name) {
		String property = getProperty(unitName, name);
		if (property == null) {
			return null;
		} else {
			return Double.valueOf(property);
		}
	}

	public static Byte getByte(String unitName, String name) {
		String property = getProperty(unitName, name);
		if (property == null) {
			return null;
		} else {
			return Byte.valueOf(property);
		}
	}
}
