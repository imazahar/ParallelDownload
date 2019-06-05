package com.assignment.paralleldownload.infra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.assignment.paralleldownload.util.ServiceConstants;

public class BaseServiceSocketHandler implements ServiceSocketHandler {

	@Override
	/**
	 * @param dataarr
	 * @throws IOException
	 */
	public void writeOnSocket(Socket socket, String data, String callingService, boolean bline, boolean close)
			throws IOException {
		try {
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			if (bline) {
				output.println(data);
			} else {
				output.print(data);
			}
			Logs.logInfo("writeOnSocket = " + data);
			if (close) {
				output.close();
			}
		} catch (IOException e) {
			// TBD-Log Error
			Logs.logError("Error in writting on socket by " + callingService);
			throw e;
		}
	}

	/**
	 * @param method
	 * @param data
	 * @param serviceConnect
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public String callService(String method, String data, String serviceConnect, String callingService, boolean bline)
			throws Exception {
		Socket socket;
		PrintWriter ds;
		BufferedReader in;
		String[] hostport;
		hostport = serviceConnect.split(ServiceConstants.DELIM);
		String result = "";
		StringBuilder sb = new StringBuilder();
		try {
			socket = new Socket(hostport[0], Integer.parseInt(hostport[1]));
			ds = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			ds.println(method + "=" + data);

			if (bline) {
				result = in.readLine();
			} else {
				char[] buffer = new char[1024];
				int read;
				while ((read = in.read(buffer)) != -1) {
					sb.append(buffer);
					for(int i =0;i<buffer.length;i++) {
						buffer[i] = '\0';
					}
				}
				result = sb.toString();
			}

			/*
			 * while (!in.ready()) { }
			 */

			ds.close();
			in.close();
			socket.close();
			// TBD-Log Info
			/*System.out.println("Response of calling service " + serviceConnect + " by " + callingService
					+ " response = " + result);*/
		} catch (Exception e) {
			// TBD-Log Error
			Logs.logError("Error in calling service " + serviceConnect);
			throw e;
		}

		return result;
	}

}
