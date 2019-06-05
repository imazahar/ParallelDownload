package com.assignment.paralleldownload.util;

import java.io.IOException;

public interface ConfigHandler {

	void set(String key, String value);

	String get(String key) throws IOException;

}