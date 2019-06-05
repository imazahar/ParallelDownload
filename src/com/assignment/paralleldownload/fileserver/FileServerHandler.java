package com.assignment.paralleldownload.fileserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.assignment.paralleldownload.infra.BaseServiceSocketHandler;
import com.assignment.paralleldownload.infra.FileDataHandler;
import com.assignment.paralleldownload.infra.Logs;
import com.assignment.paralleldownload.util.ConfigHandler;
import com.assignment.paralleldownload.util.ServiceConstants;

public class FileServerHandler extends BaseServiceSocketHandler implements Runnable {
	private final Socket clientSocket;
	private final ConfigHandler propFileHandler;
	private final FileDataHandler fdHandler;

	public FileServerHandler(final ConfigHandler propFileHandler, final Socket socket) {
		this.clientSocket = socket;
		this.propFileHandler = propFileHandler;
		this.fdHandler = new FileCharDataHandler(propFileHandler);
	}

	/**
	 * @param serverSocket
	 */
	private void processSocketData() {
		String data = null;
		try {
			// TBD-Log Info
			data = readSocketData();
			String[] dataarr = data.split(ServiceConstants.EQUAL);
			// 1 read=<filename>:<start>:<end>
			// 1 info=<filename>
			if (2 > dataarr.length) {
				// TBD-Log Error
				Logs.logInfo("File Server Data Format is incorrect = " + data);
				writeOnSocket(this.clientSocket, "error", ServiceConstants.FS_SERVICE, true, true);
			} else {
				Logs.logInfo("File Server input Data  = " + data);
				switch (dataarr[0]) {
				case ServiceConstants.FILE_READ:
					String[] arr = dataarr[1].split(ServiceConstants.DELIM);
					fdHandler.fileReader(arr[0], Long.parseLong(arr[1]), Long.parseLong(arr[2]), this.clientSocket);
					break;
				case ServiceConstants.FILE_OFFEST_INFO:
					fdHandler.fileChunksOffsetInfo(dataarr[1], this.clientSocket);
					break;
				default:
					// TBD-Log Error
					Logs.logError(
							"This method is not handled by " + ServiceConstants.FS_SERVICE + " = " + dataarr[0]);
					writeOnSocket(this.clientSocket, "error", ServiceConstants.FS_SERVICE, true, true);
					break;
				}
			}
		} catch (IOException e) {
			// TBD-Log Error
			Logs.logError("Error in reading from File Server socket with data = " + data);
		} finally {
			try {
				this.clientSocket.close();
			} catch (IOException e) {
				// TBD-Log Error
				Logs.logError("Error in closing File Server socket");
			}
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	protected String readSocketData() throws IOException {
		String data;
		BufferedReader input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		data = input.readLine();
		return data;
	}


	public void run() {
		processSocketData();
	}
}
