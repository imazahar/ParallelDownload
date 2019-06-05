package com.assignment.paralleldownload.infra;

import java.net.Socket;

public interface FileDataHandler {

	default void fileChunksOffsetInfo(String name, Socket clientSocket){
		
	}

	default void fileReader(String name, long start, long end, Socket clientSocket) {
		
	}

}