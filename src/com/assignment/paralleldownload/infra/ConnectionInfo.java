package com.assignment.paralleldownload.infra;

import com.assignment.paralleldownload.util.ServiceConstants;

public class ConnectionInfo {
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(ip);
		builder.append(ServiceConstants.DELIM);
		builder.append(port);
		return builder.toString();
	}

	private final String ip;
	private final String port;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ConnectionInfo)) {
			return false;
		}
		ConnectionInfo other = (ConnectionInfo) obj;
		if (ip == null) {
			if (other.ip != null) {
				return false;
			}
		} else if (!ip.equals(other.ip)) {
			return false;
		}
		if (port == null) {
			if (other.port != null) {
				return false;
			}
		} else if (!port.equals(other.port)) {
			return false;
		}
		return true;
	}

	public ConnectionInfo(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}

}
