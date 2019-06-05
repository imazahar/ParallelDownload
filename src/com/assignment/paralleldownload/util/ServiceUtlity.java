package com.assignment.paralleldownload.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.assignment.paralleldownload.infra.Logs;

public class ServiceUtlity {
	public static String getConnectString(String serviceHost, Integer servicePort, String service) {
		Logs.logInfo("Get Connect String of service = " + service);
		Socket socket = null;
		String serviceConnect = "";

		PrintWriter out = null;
		BufferedReader in = null;
		try {
			socket = new Socket(serviceHost, servicePort);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(ServiceConstants.SERVICE_DISCOVERY + "=" + service);
			serviceConnect = in.readLine();
			Logs.logInfo("Connect String of " + service  + " " +  serviceConnect);
			in.close();
			out.close();
			socket.close();

		} catch (Exception e) {
			// TBD-Log Error
			Logs.logError("Error in getting connect string for service = " + service); 
			return "";
		}
		return serviceConnect;
	}
}
