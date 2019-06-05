package com.assignment.paralleldownload.fileserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.assignment.paralleldownload.infra.BaseServiceSocketHandler;
import com.assignment.paralleldownload.infra.Logs;
import com.assignment.paralleldownload.infra.Services;
import com.assignment.paralleldownload.util.ConfigHandler;
import com.assignment.paralleldownload.util.PropFileHandler;
import com.assignment.paralleldownload.util.ServiceConstants;

public class FileServerService extends BaseServiceSocketHandler implements Services {

	/**
	 * 
	 */
	private String psPort = "";
	private ConfigHandler propHandler = null;
	private ExecutorService clientProcessingThreadPool = null;
	private ServerSocket serverSocket = null;
	private AtomicBoolean isClosed = null;
	private String sbService = null;

	public FileServerService(String info) {
		propHandler = new PropFileHandler(info);
		isClosed = new AtomicBoolean(false);
		init();
	}

	public String getSmPort() {
		return psPort;
	}

	private void setSmPort(String smPort) {
		this.psPort = smPort;
	}

	private void init() {
		setConfig();

		acceptClientSocket();

	}

	/**
	 * 
	 */
	public void acceptClientSocket() {
		try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(getSmPort()))) {
			// TBD-Log Info
			Logs.logInfo("File Server Service is running...");
			// TBD: Provide mechanism to shutdown service from outside
			while (!isServerClosed()) {
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					// TBD-Log Error
					Logs.logError("Error in accepting client connection");
				}
				clientProcessingThreadPool.execute(new FileServerHandler(propHandler, clientSocket));
			}

		} catch (IOException e) {
			// TBD-Log Error
			Logs.logError("Error in starting  File Server Service");
		}
	}

	/**
	 * 
	 */
	public void setConfig() {
		try {
			setSmPort(propHandler.get(ServiceConstants.PORT));

			clientProcessingThreadPool = Executors.newFixedThreadPool(
					Integer.parseInt(propHandler.get(ServiceConstants.THREAD_POOL_SIZE)));

			sbService = propHandler.get(ServiceConstants.SB_SERVICE);
			register();
		} catch (IOException e) {
			// TBD-Log Error
			Logs.logError("Error in reading File Server Service port from File Server Service Properties File");
		}
	}

	/**
	 * @throws UnknownHostException
	 */
	protected void register() throws UnknownHostException {
		Logs.logInfo(InetAddress.getLocalHost().getHostAddress() + ServiceConstants.DELIM + psPort);
		try {
			String result = callService(
					ServiceConstants.SERVICE_REGISTRY, ServiceConstants.FS_SERVICE + "="
							+ InetAddress.getLocalHost().getHostAddress() + ServiceConstants.DELIM + psPort,
					sbService, "File Server Service Register", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logs.logError("Error in registring service");
		}
	}

	public boolean isServerClosed() {
		return isClosed.get();
	}

	public synchronized void shutDown() {
		isClosed.set(true);
		try {
			if (null != serverSocket) {
				serverSocket.close();
			}
		} catch (IOException e) {
			// TBD-Log Error
			Logs.logError("Error in closing  File Server Service socket");
		}
	}

	public static void main(String[] args) {

		if (null == args || args.length < 1) {
			Logs.logError("Provide input for File Server Service");
			return;
		}
		FileServerService sm = new FileServerService(args[0] + "/" + ServiceConstants.PROP_FILE_FS);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					Logs.logInfo("Shutting down File Server Service");
					if (!sm.isServerClosed()) {
						sm.shutDown();
					}

				} catch (InterruptedException e) {
					// TBD-Log Error
					Logs.logError("Error in shutting down File Server Service");
				}
			}
		});

	}

}
