package com.assignment.paralleldownload.broker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.assignment.paralleldownload.infra.Logs;
import com.assignment.paralleldownload.infra.Services;
import com.assignment.paralleldownload.util.ConfigHandler;
import com.assignment.paralleldownload.util.PropFileHandler;
import com.assignment.paralleldownload.util.ServiceConstants;
import com.assignment.paralleldownload.util.Utility;

public class ServiceBroker implements Services {

	/**
	 * 
	 */
	private String psPort = "";
	private ConfigHandler propHandler = null;
	private ServiceBalancer sbalancer = null;
	private ExecutorService clientProcessingThreadPool = null;
	private ServerSocket serverSocket = null;
	private AtomicBoolean isClosed = null;

	public ServiceBroker(String info) {
		propHandler = new PropFileHandler(info);
		isClosed = new AtomicBoolean(false);
		sbalancer = new ServiceBalancer();
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.assignment.paralleldownload.broker.Services#getSmPort()
	 */
	@Override
	public String getSmPort() {
		return psPort;
	}

	private void setSmPort(String smPort) {
		this.psPort = smPort;
	}

	protected void init() {
		setConfig();
		acceptClientSocket();
	}

	/**
	 * 
	 */
	protected void setConfig() {
		try {
			setSmPort(propHandler.get(ServiceConstants.PORT));

			String data = propHandler.get(ServiceConstants.FS_SERVICE);
			if (Utility.isValidString(data)) {
				String arr[] = data.split(";");
				for (int i = 0; i < arr.length; i++) {
					sbalancer.addService(ServiceConstants.FS_SERVICE, arr[i]);
				}
			}

			clientProcessingThreadPool = Executors.newFixedThreadPool(
					Integer.parseInt(propHandler.get(ServiceConstants.THREAD_POOL_SIZE)));
		} catch (IOException e) {
			// TBD-Log Error
			Logs.logError("Error in reading Service Broker port from Service Broker Properties File");
		}
	}

	/**
	 * 
	 */
	protected void acceptClientSocket() {
		try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(getSmPort()))) {
			// TBD-Log Info
			Logs.logInfo("Service Broker is running...");
			// TBD: Provide mechanism to shutdown service from outside
			while (!isServerClosed()) {
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					// TBD-Log Error
					Logs.logError("Error in accepting client connection");
				}
				clientProcessingThreadPool
						.execute(new ServiceBrokerHandler(propHandler, sbalancer, clientSocket));
			}

		} catch (IOException e) {
			// TBD-Log Error
			Logs.logError("Error in starting Service Broker");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.assignment.paralleldownload.broker.Services#isServerClosed()
	 */
	@Override
	public boolean isServerClosed() {
		return isClosed.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.assignment.paralleldownload.broker.Services#shutDown()
	 */
	@Override
	public synchronized void shutDown() {
		isClosed.set(true);
		try {
			if (null != serverSocket) {
				serverSocket.close();
			}
		} catch (IOException e) {
			// TBD-Log Error
			Logs.logError("Error in closing Service Broker socket");
		}
	}

	public static void main(String[] args) {

		if (null == args || args.length < 1) {
			Logs.logError("Provide input for service broker service");
			return;
		}

		Services sm = new ServiceBroker(args[0] + "/" + ServiceConstants.PROP_FILE_SB);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					Logs.logInfo("Shutting down Service Broker");
					if (!sm.isServerClosed()) {
						sm.shutDown();
					}

				} catch (InterruptedException e) {
					// TBD-Log Error
					Logs.logError("Error in shutting down Service Broker");
				}
			}
		});

		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
