package com.assignment.paralleldownload.client;

import com.assignment.paralleldownload.infra.Logs;
import com.assignment.paralleldownload.util.ClientConst;

public class ClientServiceDriver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(null == args || args.length < 2) {
			Logs.logError("Provide input for Client Service");
			return;
		}
		ClientService cs = new ClientService(
				args[0] + "/" + ClientConst.PROP_FILE_CS);
		cs.download(args[1]);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
