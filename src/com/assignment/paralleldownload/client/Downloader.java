package com.assignment.paralleldownload.client;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.assignment.paralleldownload.infra.BaseServiceSocketHandler;
import com.assignment.paralleldownload.infra.Logs;
import com.assignment.paralleldownload.util.ClientConst;
import com.assignment.paralleldownload.util.ServiceUtlity;

public class Downloader extends BaseServiceSocketHandler implements Runnable {

	private final String input;
	private final String[] sbService;
	private final int chunkId;
	private final String downloadDir;
	private final String file;
	private int retry;

	public Downloader(String input, String downloadDir, String file, String[] sbService, int retry, int chunkId) {
		this.input = input;
		this.sbService = sbService;
		this.retry = retry;
		this.chunkId = chunkId;
		this.downloadDir = downloadDir;
		this.file = file;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (retry > 0) {
			String serviceConnect = ServiceUtlity.getConnectString(sbService[0], Integer.parseInt(sbService[1]),
					ClientConst.FS_SERVICE);
			String result = null;
			if (!serviceConnect.isEmpty()) {
				try {
					result = callService(ClientConst.FILE_READ, this.input, serviceConnect, "ClientService Downloader",
							false);
					if (!(result.compareToIgnoreCase("error") == 0 || null == result)) {
						String[] arrdata = result.split(ClientConst.PIPE);
						StringBuilder sb = new StringBuilder();
						Path path = Paths.get(downloadDir + "/temp/" + this.file);
						Files.createDirectories(path);
						for(int i = 0;i<arrdata.length;i++) {
							sb.append(arrdata[i].split(ClientConst.EQUAL)[1].split(ClientConst.DELIM)[0]);
						}
						Logs.logInfo("Downloaded data = " + sb.toString() + " chunk id = " + chunkId);
						Files.write(Paths.get(downloadDir + ClientConst.TEMP_FOLDER + this.file + "/" + chunkId + ClientConst.CLIENT_TEMP_FILE_DELIM + this.file), sb.toString().getBytes());
						break;
					}
					retry--;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Logs.logError("Downloader has thrown error");
					e.printStackTrace();
				}
			} else {
				// TBD-Log Error
				Logs.logError("Service Discovery of file service returns no connect string in Downloader");
			}
		}
	}

}
