package com.assignment.paralleldownload.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.assignment.paralleldownload.infra.BaseServiceSocketHandler;
import com.assignment.paralleldownload.infra.Logs;
import com.assignment.paralleldownload.util.ConfigHandler;
import com.assignment.paralleldownload.util.ServiceConstants;

public class ServiceBrokerHandler extends BaseServiceSocketHandler implements Runnable {
	private final Socket clientSocket;
	private final ConfigHandler propFileHandler;
	private final ServiceBalancer sbalancer;

	public ServiceBrokerHandler(final ConfigHandler propFileHandler, final ServiceBalancer sbalancer, final Socket socket) {
		this.clientSocket = socket;
		this.propFileHandler = propFileHandler;
		this.sbalancer = sbalancer;
	}

	/**
	 * @param serverSocket
	 */
	private void processSocketData() {
		String data = null;
		try {
			// TBD-Log Info
			Logs.logInfo(ServiceConstants.SB_SERVICE + " Process Socket Data");
			BufferedReader input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			data = input.readLine();
			String[] dataarr = data.split(ServiceConstants.EQUAL);
			// e.data=discover=servicename.data=register=<servicename>=host:port
			if (2 > dataarr.length) {
				// TBD-Log Error
				Logs.logInfo("Service Broker Data Format is incorrect = " + data);
				writeOnSocket(this.clientSocket, "error", ServiceConstants.SB_SERVICE);
			} else {
				Logs.logInfo("Service Manager Data  = " + data);
				switch (dataarr[0]) {
				case ServiceConstants.SERVICE_REGISTRY:
					Logs.logInfo("register " + dataarr[1] + " " +  dataarr[2]);
					sbalancer.addService(dataarr[1], dataarr[2]);
					break;
				case ServiceConstants.SERVICE_DISCOVERY:
					Logs.logInfo("discover " + dataarr[1]);
					writeOnSocket(this.clientSocket, dataarr[1], ServiceConstants.SB_SERVICE);
					break;
				default:
					// TBD-Log Error
					Logs.logInfo("This method is not handled by " + ServiceConstants.SB_SERVICE + " = " + dataarr[0]);
					writeOnSocket(this.clientSocket, "error", ServiceConstants.SB_SERVICE);
					break;
				}
			}
		} catch (IOException e) {
			// TBD-Log Error
			Logs.logError("Error in reading from service broker socket with data = " + data);
		} finally {
			try {
				this.clientSocket.close();
			} catch (IOException e) {
				// TBD-Log Error
				Logs.logError("Error in closing service broker socket");
			}
		}
	}

	/**
	 * @param socket
	 * @param data
	 * @param service
	 * @throws IOException
	 */
	public void writeOnSocket(Socket socket, String data, String service) throws IOException {
		// TBD-Log Info
		Logs.logInfo("writeOnSocket = " + data);
		PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
		if (data.compareToIgnoreCase("error") == 0) {
			output.println(data);
			output.close();
			return;
		}
		switch (data) {
		case ServiceConstants.FS_SERVICE:
			try {
				Logs.logInfo(sbalancer.getService(data));
				output.println(sbalancer.getService(data));
				output.close();
			} catch (Exception e) {
				// TBD-Log Error
				Logs.logError("Error in writting on socket by " + service);
			}
			break;
		}
	}

	public void run() {
		processSocketData();
	}
}
