package com.assignment.paralleldownload.infra;

import java.io.IOException;
import java.net.Socket;

public interface ServiceSocketHandler {

	default String callService(String method, String data, String serviceConnect, String callingService, boolean bline) throws Exception{
		return "";
	}
	default void writeOnSocket(Socket socket, String data, String callingService, boolean bline, boolean close) throws IOException {
		
	}
}
