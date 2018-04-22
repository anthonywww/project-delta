package com.github.anthonywww.projectdelta;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.anthonywww.projectdelta.graphic.Display;
import com.github.anthonywww.projectdelta.network.Client;

public class ProjectDelta {

	public static final String NAME = "Project Delta";
	public static final String VERSION = "0.1.0";
	private static ProjectDelta instance;
	
	private Client client;
	private boolean running;

	public ProjectDelta(String[] cliParams) {
		
		// If already running
		if (instance != null) {
			return;
		}
		
		// Close STDIN
		try {System.in.close();} catch (IOException e) {}
		
		instance = this;
		running = true;
		
		
		String server = null;
		int port = 0;
		
		// Load file containing remote server info
		try {
			Properties prop = new Properties();
			prop.load(ProjectDelta.class.getResourceAsStream("/server.conf"));
			
			if (!prop.containsKey("server")) {
				System.err.println("Missing 'server' key-value pair in 'server.conf'");
				System.exit(1);
			}
			
			if (!prop.containsKey("port")) {
				System.err.println("Missing 'port' key-value pair in 'server.conf'");
				System.exit(1);
			}
			
			server = prop.getProperty("server");
			try {
				port = Integer.parseInt(prop.getProperty("port"));
			} catch (NumberFormatException e) {
				System.err.println("The 'port' key-value in 'server.conf' must be an integer from 0 to 65535.");
				System.exit(1);
			}
			
		} catch (NullPointerException | IOException e) {
			System.err.println("Failed to load configuration file 'server.conf'");
			System.exit(1);
		}
		
		
		// Begin connection to remote server
		try {
			client = new Client(new InetSocketAddress(server, port));
		} catch (IllegalStateException e) {
			System.err.println("Failed to connect to remote server!");
			System.exit(1);
		}
		
		Display disp = new Display();
		
		init();
		
		while (running) {
			update();
		}
		
		terminate();
	}
	
	
	private void init() {
		
		
	}
	
	
	private void update() {
		
		
	}
	
	
	private void render() {
		
	}
	
	
	private void terminate() {
		
		
		
		System.exit(0);
	}
	
	public void windowsKill(String process) {
		if (System.getProperty("os.name").startsWith("Windows")) {
			Executors.newSingleThreadExecutor().submit(() -> {
				try {
					Runtime.getRuntime().exec("taskkill /F /IM " + process).waitFor(3000, TimeUnit.MILLISECONDS);
				} catch (Throwable t) {}
			});
		}
	}
	
	public void restoreWindowsExplorer() {
		try {
			Runtime.getRuntime().exec("explorer.exe");
		} catch (Throwable e) {}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public static ProjectDelta getInstance() {
		return instance;
	}

}
