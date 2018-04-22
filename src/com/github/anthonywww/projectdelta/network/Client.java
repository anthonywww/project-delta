package com.github.anthonywww.projectdelta.network;

import java.net.InetSocketAddress;

public class Client {
	
	private ClientListener listener;
	private InetSocketAddress address;
	
	public Client(InetSocketAddress address) {
		this.address = address;
		
		
	}
	
	public String getServerAddress() {
		return address.getHostString();
	}
	
	public int getServerPort() {
		return address.getPort();
	}
	
	private class ClientListener implements Runnable {
		@Override
		public void run() {
			
		}
	}
	
}
