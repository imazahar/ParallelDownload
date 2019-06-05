package com.assignment.paralleldownload.fileserver;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.assignment.paralleldownload.infra.BaseServiceSocketHandler;
import com.assignment.paralleldownload.infra.FileDataHandler;
import com.assignment.paralleldownload.infra.Logs;
import com.assignment.paralleldownload.util.ConfigHandler;
import com.assignment.paralleldownload.util.ServiceConstants;

public class FileCharDataHandler extends BaseServiceSocketHandler implements FileDataHandler {
	private final ConfigHandler propFileHandler;

	public FileCharDataHandler(final ConfigHandler propFileHandler) {
		this.propFileHandler = propFileHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.assignment.paralleldownload.fileserver.FileDataHandler#
	 * fileChunksOffsetInfo(java.lang.String, java.net.Socket)
	 */
	@Override
	public void fileChunksOffsetInfo(final String name, final Socket clientSocket) {
		StringBuilder sb = new StringBuilder();
		File file = null;
		String[] arr = name.split(ServiceConstants.DELIM);
		long data = 0;
		try {
			if (arr.length == 1) {
				file = new File(propFileHandler.get(ServiceConstants.FILE_DIR) + "/" + name);
			} else if (arr.length == 2) {
				file = new File(propFileHandler.get(ServiceConstants.FILE_DIR) + "/" + arr[0]);
				data = Long.parseLong(arr[1]);
			}
		} catch (IOException e) {
			sb.append("error in loading file");
			Logs.logError("Error in getting chunk info of file " + name);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (data == 0) {
				data = Long.parseLong(propFileHandler.get(ServiceConstants.FILE_CHUNK_SIZE));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logs.logInfo("chunksize = " + data);
		long index = 0;

		List<Long> offsets = new LinkedList<Long>();
		offsets.add(index);

		long count = file.length() / data;
		count--;

		while (count >= 0) {
			index += data;
			offsets.add(index);
			count--;
		}

		count = file.length() % data;
		if (count > 0) {
			offsets.add(file.length());
		}

		Logs.logInfo("offset-done size = " + offsets.size());

		sb.append("info=");
		for (int i = 0; i < offsets.size() - 1; i++) {
			long start = offsets.get(i);
			long end = offsets.get(i + 1);
			sb.append(start);
			sb.append(ServiceConstants.DELIM);
			sb.append(end);
			if (i < offsets.size() - 2) {
				sb.append(ServiceConstants.PIPE);
			}
		}

		Logs.logInfo("offset info = " + sb.toString());

		try {
			writeOnSocket(clientSocket, sb.toString(), ServiceConstants.FS_SERVICE, true, true);
		} catch (IOException e) {
			// TBD : deal with exception
			Logs.logError("Socket error in file chunk info writting");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.assignment.paralleldownload.fileserver.FileDataHandler#fileReader(java.
	 * lang.String, long, long, java.net.Socket)
	 */
	@Override
	public void fileReader(String name, long start, long end, final Socket clientSocket) {
		try {
			RandomAccessFile raf = new RandomAccessFile(
					propFileHandler.get(ServiceConstants.FILE_DIR) + "/" + name, "r");
			raf.seek(start);
			StringBuilder sb = new StringBuilder();
			int count = 0;
			long MAX_DATA_SIZE = Long
					.parseLong(propFileHandler.get(ServiceConstants.DATA_PACKET_SIZE));
			;
			long tend = raf.getFilePointer();
			Logs.logInfo("tend 1 = " + tend);
			int data = 0;
			while (tend < end) {
				count = 0;
				sb.append("data=");
				while (count < MAX_DATA_SIZE && count < (end - start)) {
					data = raf.read();
					if (-1 == data) {
						break;
					} else {
						sb.append((char) data);
					}
					count++;
				}
				tend = raf.getFilePointer();
				sb.append(":start=" + start);
				sb.append(":end=" + tend);
				start = tend;
				if (tend < end) {
					sb.append(ServiceConstants.PIPE);
					writeOnSocket(clientSocket, sb.toString(), ServiceConstants.FS_SERVICE, false, false);
				} else {
					writeOnSocket(clientSocket, sb.toString(), ServiceConstants.FS_SERVICE, false, true);
				}

			}

			raf.close();
		} catch (IOException e) {
			Logs.logError("File Character Write error");
		}
	}

}
