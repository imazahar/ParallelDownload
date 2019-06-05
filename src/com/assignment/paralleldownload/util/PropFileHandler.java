package com.assignment.paralleldownload.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.assignment.paralleldownload.infra.Logs;

public class PropFileHandler implements ConfigHandler {

	private final String configInfo;

	public PropFileHandler(String info) {
		this.configInfo = info;
	}

	/* (non-Javadoc)
	 * @see com.assignment.paralleldownload.util.ConfigHandler#set(java.lang.String, java.lang.String)
	 */
	@Override
	public void set(String key, String value) {

		synchronized (this) {
			Properties prop = new Properties();
			OutputStream output = null;
			FileInputStream input = null;

			try {

				input = new FileInputStream(configInfo);
				prop.load(input);
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						Logs.logError("Error in closing input Properties File = " + configInfo);
						throw e;
					}
				}
				output = new FileOutputStream(configInfo);
				prop.setProperty(key, value);
				prop.store(output, null);

			} catch (IOException io) {
				// TBD-Log Error
				Logs.logError("Error in reading Properties File = " + configInfo);
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						// TBD-Log Error
						Logs.logError("Error in closing output Properties File = " + configInfo);
					}
				}

			}
		}
	}

	/* (non-Javadoc)
	 * @see com.assignment.paralleldownload.util.ConfigHandler#get(java.lang.String)
	 */
	@Override
	public String get(String key) throws IOException {
		synchronized (this) {
			Properties prop = new Properties();
			FileInputStream input = null;
			String data = "";
			try {

				input = new FileInputStream(configInfo);
				prop.load(input);
				data = prop.getProperty(key);

			} catch (IOException io) {
				// TBD-Log Error
				Logs.logError("Error in reading Properties File = " + configInfo);
				throw io;
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						Logs.logError("Error in closing Properties File = " + configInfo);
						throw e;
					}
				}

			}
			return data;
		}
	}

}
