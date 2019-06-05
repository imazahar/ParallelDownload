package com.assignment.paralleldownload.util;

public final class ServiceConstants {
	//File Consts
	public final static String PROP_FILE_SB = "sb.properties";
	public final static String PROP_FILE_FS = "fs.properties";
	
	
	//File Data Consts
	
	//Service hosts and ports
	public final static String PORT = "port";
	public final static String FILE_DIR = "filedir";
	public final static String FILE_CHUNK_SIZE = "filechunksize";
	public final static String DATA_PACKET_SIZE = "datapacketsize";
	public final static String HOST = "host";
	
	//Services
	public final static String SB_SERVICE = "servicebroker";
	public final static String FS_SERVICE = "fileserverservice";
	
	//Informations
	public final static String THREAD_POOL_SIZE = "clientprocessingthreadpool";
	public final static String INPUT_FILE = "input";
	public final static String OUTPUT_FILE = "output";
	
	//Method Consts
	public final static String SERVICE_DISCOVERY = "discover";
	public final static String SERVICE_REGISTRY = "register";
	public final static String FILE_READ = "read";
	public final static String FILE_OFFEST_INFO = "offsetinfo";
	
	public final static String DELIM = ":";
	public final static String PIPE = "&";
	public final static String SPACE = " ";
	public final static String EQUAL = "=";
}
