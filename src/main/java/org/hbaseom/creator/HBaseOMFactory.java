package org.hbaseom.creator;

import static org.hbaseom.client.meta.reflect.Reflector.forName;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hbaseom.client.HBaseOM;
import org.hbaseom.client.meta.EntityDescriptor;
import org.hbaseom.client.translators.util.CollectionTypes;
import org.hbaseom.creator.config.HConfiguration;
import org.hbaseom.creator.om.HBaseOMCreator;
import org.hbaseom.creator.types.HBaseOMType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Factory class to create {@link HBaseOM} instance
 * 
 * @author levan
 * 
 */
public final class HBaseOMFactory {

	private static volatile HBaseOM hBaseClient;

	private static Set<String> unitNames;

	public static List<EntityDescriptor> entities;

	public static StringBuffer configUrlBuffer;

	private static String unitName;

	public static String configAddress;

	private static final String HBASE = "hbase";

	private static final String UNIT_NAME = "unit_name";

	public static final String META_INF = "META-INF/";

	private static final String WEB_INF = "WEB-INF/";

	public static final String HBASE_CLIENT_FILE = "hbase-client.xml";

	private static HBaseOMCreator creator;

	static {
		CollectionTypes.setCollectionTypes();
	}

	/**
	 * Private constructor to avoid to creation of new object
	 */
	private HBaseOMFactory() {
	}

	/**
	 * Appends existing configuration files paths string by new the passed one
	 * 
	 * @param value
	 */
	public static void setConfigAddress(String value) {
		StringBuffer buffer = new StringBuffer(value);
		if (!value.endsWith("/")) {
			buffer.append("/");
		}
		configAddress = buffer.toString();
	}

	public static HBaseOM getHBaseClient() {
		return hBaseClient;
	}

	private static Set<String> getUnitNames() {
		if (unitNames == null) {
			unitNames = new HashSet<String>();
		}
		return unitNames;
	}

	private static Element getWorkingHBaseNode(Document document, String name) {

		Element workingHbaseNode = null;
		/*
		 * Find unit or set default working xml node
		 */
		if (name != null) {
			NodeList hbaseNodes = document.getElementsByTagName(HBASE);
			for (int i = 0; i < hbaseNodes.getLength(); i++) {
				Element thisElement = (Element) hbaseNodes.item(i);
				if (thisElement.getAttribute(UNIT_NAME).equals(name)) {
					workingHbaseNode = thisElement;
				}
			}
		} else {
			NodeList hbaseNodes = document.getElementsByTagName(HBASE);
			for (int i = 0; i < hbaseNodes.getLength(); i++) {
				Element hbaseNode = (Element) hbaseNodes.item(i);
				String unitNameToSave = hbaseNode.getAttribute(UNIT_NAME);
				getUnitNames().add(unitNameToSave);

			}
			workingHbaseNode = (Element) document.getElementsByTagName(HBASE)
					.item(0);
		}

		return workingHbaseNode;
	}

	/**
	 * Add properties to {@link EntityDescriptor} from configuration file
	 * 
	 * @param rawClasses
	 */
	private static void addToEntities(NodeList rawClasses) {
		for (int i = 0; i < rawClasses.getLength(); i++) {
			if (rawClasses.item(i).getNodeName().equals("class")) {
				Element clazz = (Element) rawClasses.item(i);
				EntityDescriptor entityDescriptor = new EntityDescriptor(
						forName(clazz.getTextContent().trim(), true, Thread
								.currentThread().getContextClassLoader()),
						clazz.getAttribute("autocreate").equals("true"));
				String maxVersionsAttr = clazz.getAttribute("maxversions");
				if (maxVersionsAttr.length() > 0) {
					entityDescriptor.setMaxVersions(Integer
							.valueOf(maxVersionsAttr));
				}
				String getMaxVersionsAttr = clazz
						.getAttribute("getmaxversions");
				if (getMaxVersionsAttr.length() > 0) {
					entityDescriptor.setGetMaxVersions(Integer
							.valueOf(getMaxVersionsAttr));
				}
				String scanMaxVersionsAttr = clazz
						.getAttribute("scanmaxversions");
				if (scanMaxVersionsAttr.length() > 0) {
					entityDescriptor.setScanMaxVersions(Integer
							.valueOf(scanMaxVersionsAttr));
				}
				String scanCacheAttr = clazz.getAttribute("scancache");
				if (scanCacheAttr.length() > 0) {
					entityDescriptor.setScanCache(Integer
							.valueOf(scanCacheAttr));
				}
				entities.add(entityDescriptor);
			}
		}
	}

	/**
	 * Reads properties of {@link HBaseOM} from configuration files from passed
	 * stream and unit name, reads and loads {@link HBaseOMCreator}
	 * implementation from configuration file
	 * 
	 * @param {@link InputStream} client
	 * @param {@link String} name
	 * @throws IOException
	 */
	public static void hbaseProperties(InputStream client, String name)
			throws IOException {
		/*
		 * Read and parse xml configuration
		 */
		configUrlBuffer = new StringBuffer(configAddress);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document document;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(client);
		} catch (ParserConfigurationException ex) {
			throw new IOException(ex);
		} catch (SAXException ex) {
			throw new IOException(ex);
		}
		Element workingHbaseNode = getWorkingHBaseNode(document, name);

		if (workingHbaseNode == null) {
			throw new IllegalArgumentException(
					"Unit was not found in configuration");
		}

		/**
		 * Read creator
		 */
		NodeList creatorNameList = workingHbaseNode
				.getElementsByTagName("hbaseom-provider");
		if (creatorNameList == null) {
			throw new IllegalArgumentException(
					HConfiguration.PROVIDER_NOT_FOUND_ERROR);
		}

		String creatorName = ((Element) creatorNameList.item(0))
				.getTextContent();
		try {
			@SuppressWarnings("unchecked")
			Class<HBaseOMCreator> creatorClass = (Class<HBaseOMCreator>) Class
					.forName(creatorName);
			creator = creatorClass.newInstance();
			creator.provide();
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException(
					HConfiguration.PROVIDER_INITIALIZATION_ERROR, ex);
		} catch (InstantiationException ex) {
			throw new IllegalArgumentException(
					HConfiguration.PROVIDER_INITIALIZATION_ERROR, ex);
		} catch (IllegalAccessException ex) {
			throw new IllegalArgumentException(
					HConfiguration.PROVIDER_INITIALIZATION_ERROR, ex);
		}

		/*
		 * Read entities
		 */
		NodeList rawClasses = workingHbaseNode
				.getElementsByTagName("hbase_classes").item(0).getChildNodes();
		entities = new ArrayList<EntityDescriptor>();
		addToEntities(rawClasses);
		/*
		 * Read hbase-site file address
		 */
		NodeList rawProperties = ((Element) workingHbaseNode
				.getElementsByTagName("hbase-site").item(0))
				.getElementsByTagName("property");
		for (int i = 0; i < rawProperties.getLength(); i++) {
			Element property = (Element) rawProperties.item(i);
			if (property.getAttribute("name").equals("hbase_client")) {
				configUrlBuffer.append(property.getTextContent().trim());
				break;
			}
		}
	}

	/**
	 * Finds unit names from configuration files
	 * 
	 * @param {@link URL} url
	 * @param {@link DocumentBuilder} builder
	 * @throws IOException
	 */
	private static List<String> getUnitNames(URL url, DocumentBuilder builder)
			throws SAXException, IOException {
		Document document = builder.parse(url.openStream());
		NodeList hbaseNodes = document.getElementsByTagName(HBASE);
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < hbaseNodes.getLength(); i++) {
			Element thisElement = (Element) hbaseNodes.item(i);
			String unitNameToSave = thisElement.getAttribute(UNIT_NAME);
			if (!unitNameToSave.isEmpty()) {
				result.add(unitNameToSave);
			}
		}
		return result;
	}

	/**
	 * Finds units from configuration files
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * 
	 */
	public static List<String> getAvailableUnits()
			throws ParserConfigurationException, SAXException, IOException {
		/*
		 * Read and parse xml configuration
		 */
		if (configAddress == null || configAddress.isEmpty()) {
			configUrlBuffer = new StringBuffer(META_INF);
		} else {
			configUrlBuffer = new StringBuffer(configAddress);
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringBuffer buffer = new StringBuffer(configUrlBuffer);
		buffer.append(HBASE_CLIENT_FILE);
		URL url = HBaseOMFactory.class.getClassLoader().getResource(
				buffer.toString());
		return getUnitNames(url, builder);
	}

	/**
	 * Finds units from configuration files from ServletContext
	 * 
	 * @param context
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static List<String> getAvailableUnits(ServletContext context)
			throws ParserConfigurationException, SAXException, IOException {
		/*
		 * Read and parse xml configuration
		 */
		if (configAddress == null || configAddress.isEmpty()) {
			configUrlBuffer = new StringBuffer(WEB_INF);
		} else {
			configUrlBuffer = new StringBuffer(configAddress);
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringBuffer buffer = new StringBuffer(configUrlBuffer);
		buffer.append(HBASE_CLIENT_FILE);
		URL url = context.getResource(buffer.toString());
		return getUnitNames(url, builder);
	}

	/**
	 * Reads properties of {@link HBaseOM} from configuration files from passed
	 * {@link URL}
	 * 
	 * @param url
	 * @throws IOException
	 */
	private static void hbaseProperties(URL url) throws IOException {
		hbaseProperties(url.openStream(), null);
	}

	/**
	 * Creates {@link HBaseOM} instance for standalone applications using
	 * "META-INF/hbase-client.xml" parameters file.
	 * 
	 * @param name
	 * @param autoFlush
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createStand(String name, boolean autoFlush)
			throws IOException {
		if (configAddress == null || configAddress.isEmpty()) {
			setConfigAddress(META_INF);
		}
		StringBuffer buffer = new StringBuffer(configAddress);
		buffer.append(HBASE_CLIENT_FILE);
		URL url = HBaseOMFactory.class.getClassLoader().getResource(
				buffer.toString());
		hbaseProperties(url.openStream(), name);
		return creator.createHBaseClient(HBaseOMType.STAND, autoFlush);
	}

	/**
	 * Creates {@link HBaseOM} instance for standalone applications using
	 * "META-INF/hbase-client.xml" from passed {@link ClassLoader} parameters
	 * file.
	 * 
	 * @param name
	 * @param loader
	 * @param autoFlush
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createStand(String name, ClassLoader loader,
			boolean autoFlush) throws IOException {
		if (configAddress == null || configAddress.isEmpty()) {
			setConfigAddress(META_INF);
		}
		StringBuffer buffer = new StringBuffer(configAddress);
		buffer.append(HBASE_CLIENT_FILE);
		Enumeration<URL> urls = loader.getResources(buffer.toString());
		URL url = null;
		boolean found = false;
		while (urls.hasMoreElements() && !found) {
			url = urls.nextElement();
			try {
				hbaseProperties(url.openStream(), name);
				found = true;
			} catch (IllegalArgumentException ex) {
				if (!ex.getMessage().contains(
						"Unit was not found in configuration")) {
					throw ex;
				}
			}
		}
		if (!found) {
			throw new IllegalArgumentException(
					"Unit was not found in configuration");
		}
		URL siteURL = new URL(url.getProtocol(), url.getHost(), url.getFile()
				.replace(HBASE_CLIENT_FILE, "hbase-site.xml"));
		return creator.createHBaseClient(HBaseOMType.STAND_URL, autoFlush,
				siteURL);
	}

	/**
	 * Creates {@link HBaseOM} instance for standalone applications using
	 * "META-INF/hbase-client.xml" parameters file.
	 * 
	 * @param name
	 *            unit name
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createStand(String name) throws IOException {
		return createStand(name, false);
	}

	/**
	 * Creates {@link HBaseOM} instance for standalone applications using
	 * "META-INF/hbase-client.xml" from passed ClassLoader parameters file.
	 * 
	 * @param {@link String} name
	 * @param {@link ClassLoader} loader
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createStand(String name, ClassLoader loader)
			throws IOException {
		return createStand(name, loader, false);
	}

	/**
	 * Create {@link HBaseOM} for standalone application with default parameters
	 * 
	 * @throws IOException
	 */
	public static HBaseOM createStand() throws IOException {
		return createStand(null, false);
	}

	/**
	 * Create {@link HBaseOM} for application server by passed name sets auto
	 * flush false
	 * 
	 * 
	 * @param {@link String} name
	 * @param {@link boolean} autoFlush
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createApp(String name, boolean autoFlush)
			throws IOException {
		if (configAddress == null || configAddress.isEmpty()) {
			setConfigAddress(META_INF);
		}
		URL url = HBaseOMFactory.class.getClassLoader().getResource(
				new StringBuffer(configAddress).append(HBASE_CLIENT_FILE)
						.toString());

		hbaseProperties(url.openStream(), name);

		return creator.createHBaseClient(HBaseOMType.STAND, autoFlush);
	}

	/**
	 * Create {@link HBaseOM} for application server by passed name sets auto
	 * flush false by default
	 * 
	 * @param {@link String} name
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createApp(String name) throws IOException {
		return createApp(name, false);
	}

	/**
	 * Create {@link HBaseOM} for application server with passed from
	 * configuration files from passed streams and sets auto flush
	 * 
	 * @param {@link InputStream} client
	 * @param {@link InputStream} site
	 * @param {@link String} name
	 * @param {@link boolean} autoFlush
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createApp(InputStream client, InputStream site,
			String name, boolean autoFlush) throws IOException {
		hbaseProperties(client, name);
		return creator.createHBaseClient(HBaseOMType.APP, autoFlush);
	}

	/**
	 * Create {@link HBaseOM} for web application with passed unit name and sets
	 * auto flush
	 * 
	 * @param {@link String} name
	 * @param {@link boolean} autoFlush
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createWeb(String name, boolean autoFlush)
			throws IOException {
		if (configAddress == null || configAddress.isEmpty()) {
			setConfigAddress(WEB_INF);
		}
		URL url = HBaseOMFactory.class.getClassLoader().getResource(
				new StringBuffer(configAddress).append(HBASE_CLIENT_FILE)
						.toString());
		hbaseProperties(url.openStream(), name);
		return creator.createHBaseClient(HBaseOMType.WEB, autoFlush);
	}

	/**
	 * Create {@link HBaseOM} for web application with passed unit name and sets
	 * auto flush false by default
	 * 
	 * @param {@link String} name
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createWeb(String name) throws IOException {
		return createWeb(name, false);
	}

	/**
	 * 
	 * @param {@link String} name
	 * @param {@link boolean} autoFlush
	 * @param {@link URL} ... url
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createWeb(String name, boolean autoFlush, URL... url)
			throws IOException {
		hbaseProperties(url[0].openStream(), name);
		return creator.createHBaseClient(HBaseOMType.WEB, autoFlush,
				url[url.length - 1]);
	}

	/**
	 * Create {@link HBaseOM} for web application with passed unit name and
	 * {@link URL}s
	 * 
	 * @param {@link String} name
	 * @param {@link URL} ... url
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createWeb(String name, URL... url) throws IOException {
		return createWeb(name, false, url);
	}

	/**
	 * Create {@link HBaseOM} for web application with passed unit name from
	 * configuration files by the {@link URL}s from {@link ServletContext} and
	 * sets auto flush
	 * 
	 * @param {@link String} name
	 * @param {@link ServletContext} context
	 * 
	 * @param {@link boolean} autoFlush
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createWeb(String name, ServletContext context,
			boolean autoFlush) throws IOException {
		if (configAddress == null || configAddress.isEmpty()) {
			setConfigAddress(WEB_INF);
		}
		URL url = context.getResource(new StringBuffer(configAddress).append(
				HBASE_CLIENT_FILE).toString());
		hbaseProperties(url.openStream(), name);
		URL urlSite = context.getResource(configUrlBuffer.toString());
		StringBuffer stringBuffer = new StringBuffer("hbase:");
		stringBuffer.append(name);
		try {
			return creator.createHBaseClient(HBaseOMType.WEB, autoFlush,
					urlSite);
		} finally {
			context.setAttribute(stringBuffer.toString(), hBaseClient);
		}
	}

	/**
	 * Create {@link HBaseOM} for web application from configuration files by
	 * the {@link URL}s from {@link ServletContext}
	 * 
	 * @param {@link String} name
	 * @param {@link ServletContext} context
	 * 
	 * @return {@link HBaseOM}
	 * @throws IOException
	 * @throws {@link Exception}
	 */
	public static HBaseOM createWeb(String name, ServletContext context)
			throws IOException {
		return createWeb(name, context, false);
	}

	/**
	 * Create {@link HBaseOM} for web application from configuration files by
	 * the {@link URL}s from {@link ServletContext} and sets auto flush
	 * 
	 * @param {@link ServletContext} context
	 * @param {@link boolean} autoFlush
	 * @return {@link HBaseOM}
	 * @throws IOException
	 */
	public static HBaseOM createWeb(ServletContext context, boolean autoFlush)
			throws IOException {
		if (configAddress == null || configAddress.isEmpty()) {
			setConfigAddress(WEB_INF);
		}
		StringBuffer configBuffer = new StringBuffer(configAddress);
		configBuffer.append(HBASE_CLIENT_FILE);
		URL url = context.getResource(configBuffer.toString());
		hbaseProperties(url);
		URL urlSite = context.getResource(configUrlBuffer.toString());
		if (unitName == null || unitName.length() == 0) {
			throw new IOException(
					"unit-name is not specified in hbase-client.xml");
		}
		StringBuffer stringBuffer = new StringBuffer("hbase:");
		stringBuffer.append(unitName);
		try {
			return creator.createHBaseClient(HBaseOMType.WEB, autoFlush,
					urlSite);
		} finally {
			context.setAttribute(stringBuffer.toString(), hBaseClient);
		}
	}

/**
     * Create {@link HBaseOM} for web application from configuration files by the
     * {@link URL}s from {@link ServletContext{@link 
     * 
     * @param {@link ServletContext}
     *            context
     * @return {@link HBaseOM}
 * @throws IOException 
     */
	public static HBaseOM createWeb(ServletContext context) throws IOException {
		return createWeb(context, false);
	}

	/**
	 * Gets or creates {@link HBaseOM} instance from {@link ServletContext} by
	 * passed unit name and sets auto flush
	 * 
	 * @param {@link ServletContext} context
	 * @param {@link String} name
	 * @param {@link boolean} autoFlush
	 * @return {@link HBaseOM} instance
	 * @throws IOException
	 */
	public static HBaseOM getHBaseClient(ServletContext context, String name,
			boolean autoFlush) throws IOException {
		StringBuffer stringBuffer = new StringBuffer("hbase:");
		stringBuffer.append(name);

		HBaseOM createdHBaseClient = (HBaseOM) context
				.getAttribute(stringBuffer.toString());
		if (createdHBaseClient == null) {
			createdHBaseClient = createWeb(name, context, autoFlush);
		}
		return createdHBaseClient;
	}

	/**
	 * Gets or creates {@link HBaseOM} instance from {@link ServletContext}.
	 * 
	 * @param {@link ServletContext} context
	 * @param {@link String} name
	 * @return {@link HBaseOM} instance
	 * @throws IOException
	 */
	public static HBaseOM getHBaseClient(ServletContext context, String name)
			throws IOException {
		return getHBaseClient(context, name, false);
	}
}
