package org.simoes.util;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.simoes.classify.Classifier;


/**
 * 
 * Provides user with access to the properties file.
 * 
 * @author Chris Simoes
 */
public class ConfigResources {
    static Logger log = Logger.getLogger(ConfigResources.class.getName());

    private static boolean startup = true; // set to true until init() completes
    private static File configFile;     
    private static PropertyResourceBundle propertyBundle;
    private static PropertyResourceBundle resourceBundle;
        
    //public static final String PROPS_DIR = "src" + File.separator;
    public static final String PROP_FILE_NAME = "default.properties";
    public static final String MSG_CAT_FILE_NAME = "MessageResources.properties";
    public static final String DATA_DIR_PREFIX = "data.dir.prefix";
//    public static final String LOG4J_TOMCAT = "log4j.tomcat.file";
//    public static final String LOG4J_JUNIT = "log4j.junit.file";
    public static final String LOG4J_TOMCAT = "log4j.tomcat.properties";
    public static final String LOG4J_JUNIT = "log4j.properties";
    

    /**
     * Initializes our application.  ConfigResources.init() must be called 
     * as the application starts
     *
     */
    public static void init() {
    	ConfigResources.init(PROP_FILE_NAME);
    }        

    public static void init(String propertyFile) {
    	System.out.println("ConfigResources.init() BEGAN");
    	if(startup) { // initialize everything at startup
        	System.out.println("ConfigResources.init() 1");
	        propertyBundle = loadProperties(propertyFile);
	        // working dir is only used for parser, use catalina.home for tomcat
        	System.out.println("ConfigResources.init() 2");
	        initLog4J(); // setup log4J

        	System.out.println("ConfigResources.init() 3");
//	        resourceBundle = loadProperties(MSG_CAT_FILE_NAME); // load message bundle
//	        MailUtil.init();
        	
        	initMyApplicationResources();

	        // set startup to false
	        startup = false;
    	} else { // only reload property files on subsequent calls
//	        propertyBundle = loadProperties(propertyFile);
	        // working dir is only used for parser, use catalina.home for tomcat
    	}
    }

    /**
     * This initializes objects used by Tomcat, but not by parser.
     * This needs to be called after regular init()
     *
     */
    public static void initTomcat() {
//        StaticSpace.init();
        // initialize Tasks
//        initTasks();
    }
    
    /**
     * Returns this property as an Int or throws a runtime exception
     * if we could not Parse the Number
     * @param key
     * @return
     */
    public static int getPropertyInt(String key) {
        String prop = propertyBundle.getString(key); 
        return Integer.parseInt(prop); 
    }

    public static String getProperty(String key) {
        return propertyBundle.getString(key);
    }
    
    public static String getMessage(String key) {
        return resourceBundle.getString(key);
    }

    public static String getConfigFileName() {
        return configFile.getAbsolutePath();
    }
    
    /**
	 * Returns site like http://www.boatshare.com or http://www.simoes2.org
	 * @return
	 */
	public static String getRootHost() {
		String siteName = ConfigResources.getProperty(Constants.PROP_HOST_NAME);
		
		return "http://" + siteName;
	}
	
	public static boolean isTweetsCachedInSession() {
		return Boolean.parseBoolean(getProperty("tweets.session.cache"));
	}
	
	/*
	 * Private Methods
	 */
	
    private static PropertyResourceBundle loadProperties(String propertiesFile)
    {
        PropertyResourceBundle result = null;
        InputStream inputStream = ConfigResources.class.getResourceAsStream("/" + propertiesFile);
        if (null != inputStream) {
            try {
                result = new PropertyResourceBundle(inputStream);
            } catch (java.io.IOException e) {
                log.severe("Caught IOException while reading property file " + propertiesFile);
                throw new Error("Can't find property file: " + propertiesFile);
            }
            try {
                inputStream.close();
            } catch (java.io.IOException e) {
                log.warning("Caught IOException while closing InputStream connected to property file:" + e.getMessage());
            }
        } else {
            throw new Error("Can't find property file: " + propertiesFile);
        }
        return result;
    }
    

    private static void initLog4J() {
    	final String METHOD = "initLog4J(): ";
    	System.out.println(METHOD + " STARTED");
    	// if this isn't tomcat initialize log4j
    	String file = LOG4J_TOMCAT; //this works because the "conf" directory is marked as a source directory
    	if(StringUtils.isBlank(System.getProperty("catalina.home"))) {
    		file = LOG4J_JUNIT;
    	}

		// if the log4j.file is not set, then no point in trying
	    if(file != null) {
	    	PropertyConfigurator.configureAndWatch(file, 5000);
			log.warning(METHOD + "Log4j has been initialized.");
			// Log some system settings
			log.warning(METHOD + "Tomcat TotalMemory=" + Runtime.getRuntime().totalMemory());
	    } else {
	    	throw new Error(METHOD + "log4j file could not be found!");
	    }

//        outputJVMSystemProperties();
    	System.out.println(METHOD + " FINISHED");
    }
    
    private static void initMyApplicationResources() {
    	Classifier.init();
    }
    
    private static void outputJVMSystemProperties() {
		log.info("JVM SYSTEM PROPERTIES START");
		Properties props = System.getProperties();
		Set<Object> keys = props.keySet();
		for (Object key : keys) {
			String value = (String) props.get(key);
			log.info(key + "=" + value);
		}
		log.info("JVM SYSTEM PROPERTIES END");
    }

}
