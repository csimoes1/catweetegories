package org.simoes.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * 
 * Utility class for manipulating Files in Java
 * that I have found useful.
 * 
 * @author Chris Simoes
 */
public class FileUtil {
    // error logging variables //
    static Logger log = Logger.getLogger(FileUtil.class.getName());
    private static final String METHOD_writeFile = "writeFile(): ";
    private static final String METHOD_deleteFiles = "deleteFiles(): ";
    private static final String METHOD_runProgram = "runProgram(): ";
    private static final String METHOD_readInputStream = "readInputStream(): ";
    private static final String STARTED = "started.";
    // end of error logging variables //

    /**
     *  Writes the entire contents of the InputStream to a file named the
     *  filename passed in.
     * @param inputStream - the inputStream to be written to file
     * @param filename - the file that the inputStream will be written to
     * @return File - a File object representing the file we just created, or
     * null if there was an error.
     */
    public static File writeFile(InputStream inputStream, String filename)
        throws IOException {
        log.finest(METHOD_writeFile + STARTED);
        File result = null;
        int byteCount = -1;
        byte[] data = new byte[1024];
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        BufferedOutputStream bos =
            new BufferedOutputStream(new FileOutputStream(filename));
        while ((byteCount = bis.read(data, 0, 1024)) > -1) {
            bos.write(data, 0, byteCount);
        }
        bos.flush();

        if (null != bos) {
            try {
                bos.close();
            } catch (IOException e) {
            }
        }
        if (null != bis) {
            try {
                bis.close();
            } catch (IOException e) {
            }
        }
        result = new File(filename);
        return result;
    }

    /**
     *  Writes the String data to a file named filename.
     * @param data - A String that will be written to file
     * @param filename - the file that the inputStream will be written to
     * @return File - a File object representing the file we just created, or
     * null if there was an error.
     */
    public static File writeFile(String data, String filename)
        throws IOException {
        File result = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
        result = writeFile(bais, filename);
        if (null != bais) {
            try {
                bais.close();
            } catch (IOException e) {
            }
        }
        return result;
    }

    /**
     * Writes the byte[] data to a file named filename.
     * @param data - A byte[] that will be written to file
     * @param filename - the file that the inputStream will be written to
     * @return File - a File object representing the file we just created, or
     * null if there was an error.
     */
    public static File writeFile(byte[] data, String filename)
        throws IOException {
        File result = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        result = writeFile(bais, filename);
        if (null != bais) {
            try {
                bais.close();
            } catch (IOException e) {
            }
        }
        return result;
    }

    /**
     *  Deletes the files passed in the String array.  Basically it calls
     * the delete method in the File object.
     * @param filenames - the files to be deleted
     */
    public static void deleteFiles(String[] filenames) {
        for (int i = 0; i < filenames.length; i++) {
            deleteFile(filenames[i]);
        }
    }

    /**
     *  Deletes the file passed in.  Basically it calls
     * the delete method in the File object.
     * @param filename - the file to be deleted
     */
    public static void deleteFile(String filename) {
        File file = new File(filename);
        try {
            if (file.delete()) {
                log.finest(
                    METHOD_deleteFiles
                        + filename
                        + " was successfully deleted");
            } else {
                log.severe(
                    METHOD_deleteFiles + "Error deleting \"" + filename + "\"");
            }
        } catch (SecurityException e) {
            log.severe(
                METHOD_deleteFiles + "Error deleting \"" + filename + "\"");
            log.severe(METHOD_deleteFiles + e);
            e.printStackTrace();
        }
    }

    /**
     *  Deletes the file passed in.  Basically it calls
     * the delete method in the File object.
     * @param filename - the file to be deleted
     */
    public static void deleteFile(File filename) {
        try {
            if (filename.delete()) {
                log.finest(
                    METHOD_deleteFiles
                        + filename
                        + " was successfully deleted");
            } else {
                log.severe(
                    METHOD_deleteFiles + "Error deleting \"" + filename + "\"");
            }
        } catch (SecurityException e) {
            log.severe(
                METHOD_deleteFiles + "Error deleting \"" + filename + "\"");
            log.severe(METHOD_deleteFiles + e);
            e.printStackTrace();
        }
    }

    /**
     *  Runs the program passed in.  It the returns the "return code"
     *  of the program.  0 usually means normal termination.  The
     *  System.out of the program is stored in the ByteArrayOutputStream
     *  that is passsed in.
     */
    public static int runProgram(String program, ByteArrayOutputStream baos) {
        int result = -1;
        InputStream is = null;
        try {
            Process proc = Runtime.getRuntime().exec(program);
            is = proc.getInputStream();
            readInputStream(is, baos);
            result = proc.waitFor();
        } catch (InterruptedException e) {
            log.severe(METHOD_runProgram + e);
            e.printStackTrace();
        } catch (IOException e) {
            log.severe(METHOD_runProgram + e);
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                } // we tried
            }
        }
        return result;
    }

    /**
     *  Converts the InputStream to the returned String.
     */
    public static String inputStream2String(InputStream is) {
        String result = new String();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        readInputStream(is, baos);
        result = baos.toString();
        if (null != baos) {
            try {
                baos.close();
            } catch (IOException e) {
            }
        }
        return result;
    }

    /**
     *  Reads the InputStream using specified encoding.
     */
    public static String readInputStream(InputStream is, String encoding) {
    	StringBuffer result = new StringBuffer();
    	
        int data = -1;
        InputStreamReader isr = null;
        
        try {
        	isr = new InputStreamReader(is, encoding);
        	data = isr.read();
            while (-1 != data && 0 != data) {
            	char c = (char)data;
            	result.append(c);
            	data = isr.read();
            }
        } catch (IOException e) {
            log.severe(METHOD_readInputStream + e);
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     *  Reads the InputStream into the ByteArrayOutputStream.
     *  Wrapper that has a more descriptive name than readInputStream.
     */
    public static ByteArrayOutputStream inputStream2ByteArray(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        readInputStream(is, baos);
        return baos;
    }

    /**
     *  Reads the InputStream into the ByteArrayOutputStream.
     */
    public static void readInputStream(InputStream is, ByteArrayOutputStream baos) {
        int data = -1;
        try {
            while (-1 != (data = is.read())) {
                baos.write(data);
            }
        } catch (IOException e) {
            log.severe(METHOD_readInputStream + e);
            e.printStackTrace();
        }
    }
    
    /**
     *  Reads the number of bytes specified to into the ByteArrayOutputStream.
     */
    public static void readInputStream(InputStream is, ByteArrayOutputStream baos, int size) {
        int data = -1;
        int count = 0;
        try {
            while (count < size && -1 != (data = is.read())) {
                baos.write(data);
                count++;
            }
        } catch (IOException e) {
            log.severe(METHOD_readInputStream + e);
            e.printStackTrace();
        }
    }

    
    /**
     * Loads the property file.  Searches the CLASSPATH for the propertiesFile passed in.
     * @param propertiesFile the file name of the properties file, the CLASSPATH
     *      will be searched for a file with this name
     * @throws IOException if there is a problem reading properties file
     */
    public static Properties loadProperties(String propertiesFile) throws java.io.IOException {
        // create and load default properties
        Properties properties = new Properties();
        InputStream in = null;
        try {
        	File propsFile = getFile(propertiesFile);
        	in = new FileInputStream(propsFile);
//            in = FileUtil.class.getResourceAsStream("/" + propertiesFile);
            
//            if (in == null) {
//                throw new java.util.MissingResourceException(
//						"Can't find property file " + propertiesFile,
//						FileUtil.class.getName(), propertiesFile);
//            }
            properties.load(in);
        } finally {
            if(null != in) {
                try {
                    in.close();
                } catch(IOException e) {} //ignored on purpose
            }
        }
        
        return properties;
    }    

    
    public static boolean isReadable(String file) {
        boolean result = false;
        File f = new File(file);
        if(f.canRead()) {
            result = true;
        }
        return result;
    }
    
    /**
     * Returns a File object for the file name passed in.  It searches
     * the class path for the file.  Returns null if we could not find the file.
     * @param file
     * @return
     */
    public static File getFile(String file) {
        File result = null;
        if(null != file && file.length() != 0) {
            URL url = FileUtil.class.getResource(file);
            if(url == null) { // try prepending "/"
            	url = FileUtil.class.getResource("/" + file);
            }
            
            if(null != url) {
                try {
                	result = new File(url.toURI());
                } catch(URISyntaxException e) {
                    // ignore and return null
                }
            } else { // try getting absolute file path
            	File f = new File(file);
            	if(f.exists()) {
            		result = f;
            	}
            }
        }
        return result;
    }
    
    public static boolean fileExists(String file) {
    	return (null == FileUtil.getFile(file)) ? false : true;
    }

    /**
     * Returns the timestamp on the file representing the last time it was changed,
     * or it returns 0 if there was a problem.
     * @param file
     * @return
     */
    public static long getLastModifiedDate(String file) {
    	long result = 0;
        File f = getFile(file);
        if(null != f) {
        	result = f.lastModified();
        }
        return result;
    }
}