package com.assignment.paralleldownload.util;

public class Utility {
	public static boolean isValidString(String data) {
		if(null == data || data.isEmpty()) {
			return false;
		}
		return true;
	}
}
