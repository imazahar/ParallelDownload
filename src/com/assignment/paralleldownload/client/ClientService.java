package com.assignment.paralleldownload.client;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.assignment.paralleldownload.infra.BaseServiceSocketHandler;
import com.assignment.paralleldownload.infra.Logs;
import com.assignment.paralleldownload.util.ClientConst;
import com.assignment.paralleldownload.util.ConfigHandler;
import com.assignment.paralleldownload.util.PropFileHandler;
import com.assignment.paralleldownload.util.ServiceUtlity;
import com.assignment.paralleldownload.util.Utility;

public class ClientService extends BaseServiceSocketHandler {

	private final ConfigHandler propHandler;
	private String[] sbService = null;

	public ClientService(String path) {
		propHandler = new PropFileHandler(path);
		try {
			sbService = propHandler.get(ClientConst.SB_SERVICE).split(ClientConst.DELIM);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void download(String file) {
		String result = getFileOffsets(file);
		parallelDownload(file, result);

	}

	/**
	 * @param file
	 * @param result
	 */
	protected void parallelDownload(String file, String result) {
		try {
			List<PairOffset> list = parseOffsetInfo(result);
			ExecutorService service = Executors.newFixedThreadPool(list.size());
			for (int i = 0; i < list.size(); i++) {
				String input = file + ClientConst.DELIM + list.get(i).start + ClientConst.DELIM + list.get(i).end;
				service.execute(new Downloader(input, propHandler.get(ClientConst.WRITE_FILE_DIR),
						file, sbService,
						Integer.parseInt(propHandler.get(ClientConst.DOWNLOAD_RETRY)), i + 1));
			}
			service.shutdown();
			while (!service.isTerminated()) {
			}
			if (service.isTerminated()) {
				FileMerger filemerge = new FileMerger();
				filemerge
						.mergeFiles(
								propHandler.get(ClientConst.WRITE_FILE_DIR)
										+ ClientConst.TEMP_FOLDER + file,
								propHandler.get(ClientConst.WRITE_FILE_DIR) + "/" + file);
			}

		} catch (Exception e) {
			// TBD-Log Error
			Logs.logError("Error occurred in ClientService while doing parallel download");
		}
	}

	/**
	 * @param result
	 * @return
	 */
	protected List<PairOffset> parseOffsetInfo(String result) {
		String arr[] = result.split(ClientConst.EQUAL);
		String[] arroffset = arr[1].split(ClientConst.PIPE);
		List<PairOffset> list = new LinkedList<PairOffset>();
		for (int i = 0; i < arroffset.length; i++) {
			String[] arrpair = arroffset[i].split(ClientConst.DELIM);
			list.add(new PairOffset(Long.parseLong(arrpair[0]), Long.parseLong(arrpair[1])));
		}
		return list;
	}

	/**
	 * @param file
	 * @return
	 */
	protected String getFileOffsets(String file) {
		String serviceConnect = "";
		serviceConnect = ServiceUtlity.getConnectString(sbService[0], Integer.parseInt(sbService[1]),
				ClientConst.FS_SERVICE);
		String result = "";
		Logs.logInfo("file server service connect string = " + serviceConnect);
		if (!serviceConnect.isEmpty()) {

			try {
				String input = file;
				String chunkSize = propHandler.get( ClientConst.CLIENT_CHUNK_SIZE);
				if (Utility.isValidString(chunkSize)) {
					input += ClientConst.DELIM + chunkSize;
				}
				result = callService(ClientConst.FILE_OFFEST_INFO, input, serviceConnect, "ClientService", true);
				Logs.logInfo("file server service daa = " + result);
			} catch (Exception e) {
				// TBD-Log Error
				Logs.logError("Error occurred in ClientService");
			}

		} else {
			// TBD-Log Error
			Logs.logError("Service Discovery of file service returns no connect string");
		}
		return result;
	}
}
