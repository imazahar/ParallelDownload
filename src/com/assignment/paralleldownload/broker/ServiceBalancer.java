package com.assignment.paralleldownload.broker;

import java.util.*;

import com.assignment.paralleldownload.infra.ConnectionInfo;
import com.assignment.paralleldownload.infra.Logs;
import com.assignment.paralleldownload.util.ServiceConstants;
import com.assignment.paralleldownload.util.Utility;

public class ServiceBalancer {
	private Map<String, LinkedHashSet<ConnectionInfo>> serviceList = new HashMap<String, LinkedHashSet<ConnectionInfo>>();

	public void addService(String name, String connectString) {
		synchronized (this) {
			if (!Utility.isValidString(name) || !Utility.isValidString(connectString)) {
				return;
			}
			String [] arr = connectString.split(ServiceConstants.DELIM);
			Logs.logInfo("add Service " + arr[0] + arr[1]);
			
			ConnectionInfo ci = new ConnectionInfo(arr[0], arr[1]);
			if (serviceList.containsKey(name)) {
				serviceList.get(name).add(ci);
			} else {
				LinkedHashSet<ConnectionInfo> set = new LinkedHashSet<ConnectionInfo>();
				set.add(ci);
				serviceList.put(name, set);
			}
		}
	}

	public String getService(String name) {
		synchronized (this) {
			if (!Utility.isValidString(name)) {
				return "";
			} else {
				if (serviceList.containsKey(name)) {
					LinkedHashSet<ConnectionInfo> set = serviceList.get(name);
					Iterator<ConnectionInfo> it = set.iterator();
					ConnectionInfo retCi = null;
					while(it.hasNext()) {
						retCi = it.next();
					}
					set.remove(retCi);
					set.add(retCi);
					return retCi.toString();
				}
				return "";
			}
		}
	}
	
	public LinkedHashSet<String> getServices(String name){
		return (LinkedHashSet<String>) serviceList.get(name).clone();
	}
}
