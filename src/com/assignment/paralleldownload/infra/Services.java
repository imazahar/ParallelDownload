package com.assignment.paralleldownload.infra;

public interface Services {

	default String getSmPort() {
		return "";
	}

	default boolean isServerClosed() {
		return false;
	}

	default void shutDown() {
		
	}

}