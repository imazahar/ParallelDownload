package com.assignment.paralleldownload.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.assignment.paralleldownload.infra.Logs;
import com.assignment.paralleldownload.util.ClientConst;

public class FileMerger {

	private final Map<Integer, Path> listFiles;

	public FileMerger() {
		listFiles = new TreeMap<Integer, Path>();
	}

	public void mergeFiles(String path, String target) {
		getTempFileList(path);
		mergeContent(target);
		deleteTempFiles(path);
	}

	/**
	 * @param path
	 */
	protected void getTempFileList(String path) {
		try (Stream<Path> paths = Files.walk(Paths.get(path))) {
			paths.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					try {
						String[] arr = filePath.getFileName().toString().split(ClientConst.CLIENT_TEMP_FILE_DELIM);
						listFiles.put(Integer.parseInt(arr[0]), filePath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Logs.logError("Error in getting temp file list");
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logs.logError("Error in getting temp file list 2");
			e.printStackTrace();
		}
	}

	protected void mergeContent(String target) {
		Logs.logInfo(target);
		File tmpDir = new File(target);
		listFiles.forEach((key, path) -> {
			try {
				if (tmpDir.exists()) {
					Files.write(Paths.get(target), Files.readAllBytes(path), StandardOpenOption.APPEND);
				} else {
					Files.write(Paths.get(target), Files.readAllBytes(path));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Logs.logError("Error in reading temp file = " + path);
				e.printStackTrace();
			}
		});
		
	}

	/**
	 * 
	 */
	protected void deleteTempFiles(String dpath) {
		listFiles.forEach((key, path) -> {
			try {
				Files.delete(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Logs.logError("Error in deleting temp file = " + path);
				e.printStackTrace();
			}
		});
		try {
			Files.delete(Paths.get(dpath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logs.logError("Error in deleting temp file folder = " + dpath);
			e.printStackTrace();
		}
	}

}
