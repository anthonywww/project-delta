package com.github.anthonywww.projectdelta;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

import com.github.anthonywww.projectdelta.audio.AudioManager;
import com.github.anthonywww.projectdelta.graphic.DisplayOld;
import com.github.anthonywww.projectdelta.network.Client;

public class ProjectDelta {

	public static final String NAME = "Project Delta";
	public static final String VERSION = "0.2.4";
	private static ProjectDelta instance;
	
	private boolean running;
	private int locationX;
	private int locationY;
	private Client client;
	private DisplayOld display;
	private AudioManager audioManager;

	public ProjectDelta(int x, int y) {
		// If already running
		if (instance != null) {
			return;
		}
		
		// Close STDIN
		try {System.in.close();} catch (IOException e) {}
		
		instance = this;
		running = true;
		
		this.locationX = x;
		this.locationY = y;
		
		System.out.println(x + ", " + y);
		
		String server = null;
		int port = 0;
		
		// Load file containing remote server info
		try {
			Properties prop = new Properties();
			prop.load(ProjectDelta.class.getResourceAsStream("/client.conf"));
			
			if (!prop.containsKey("server")) {
				System.err.println("Missing 'server' key-value pair in 'client.conf'");
				System.exit(1);
			}
			
			if (!prop.containsKey("port")) {
				System.err.println("Missing 'port' key-value pair in 'client.conf'");
				System.exit(1);
			}
			
			server = prop.getProperty("server");
			try {
				port = Integer.parseInt(prop.getProperty("port"));
			} catch (NumberFormatException e) {
				System.err.println("The 'port' key-value in 'client.conf' must be an integer from 0 to 65535.");
				System.exit(1);
			}
			
		} catch (NullPointerException | IOException e) {
			System.err.println("Failed to load configuration file 'client.conf'");
			System.exit(1);
		}
		
		// Begin connection to remote server
		try {
			client = new Client(new InetSocketAddress(server, port));
			client.connect();
		} catch (IOException | IllegalStateException e) {
			System.err.println("Failed to connect to remote server");
			System.exit(1);
		}
		
		// Create audio manager
		audioManager = new AudioManager();
		
//		audioManager.load("test/600hz.ogg", "test/600hz");
//		audioManager.load("test/codelyoko_xana_battle2.ogg", "test/codelyoko_xana_battle2");
		audioManager.load("test/ocarina.ogg", "test/ocarina");
		audioManager.load("test/piano.ogg", "test/piano");
		audioManager.load("test/smooth.ogg", "test/smooth");
		audioManager.load("test/string_orchestra_bg.ogg", "test/string_orchestra_bg.ogg");
//		audioManager.load("test/subdigitals_planet_net.ogg", "test/subdigitals_planet_net");
		audioManager.load("test/test1.ogg", "test/test1");
		audioManager.load("test/test2.ogg", "test/test2");
		audioManager.load("test/overture.ogg", "test/overture");
		
		audioManager.load("program/hopes_and_dreams_v3.ogg", "program/hopes_and_dreams_v3");
		audioManager.load("program/1_StringOrchestra.ogg", "program/1_StringOrchestra");
		audioManager.load("program/10_Drums.ogg", "program/10_Drums");
		audioManager.load("program/11_Ocarina.ogg", "program/11_Ocarina");
		audioManager.load("program/12_ElectricGuitar.ogg", "program/12_ElectricGuitar");
		audioManager.load("program/13_ElectricGuitar.ogg", "program/13_ElectricGuitar");
		audioManager.load("program/14_BassGuitar.ogg", "program/14_BassGuitar");
		audioManager.load("program/15_ReverseCymbal.ogg", "program/15_ReverseCymbal");
		audioManager.load("program/2_StringOrchestraBG.ogg", "program/2_StringOrchestraBG");
		audioManager.load("program/3_SquareWave.ogg", "program/3_SquareWave");
		audioManager.load("program/4_Piano.ogg", "program/4_Piano");
		audioManager.load("program/6_Fiddle.ogg", "program/6_Fiddle");
		audioManager.load("program/7_Fiddle.ogg", "program/7_Fiddle");
		audioManager.load("program/8_Fiddle.ogg", "program/8_Fiddle");
		audioManager.load("program/9_Bells.ogg", "program/9_Bells");
		
		System.out.println("Audio loaded");
		
		// Create display component
		display = new DisplayOld();
		//display.start();
		
		//audioManager.playById("test/smooth");
		//audioManager.playById("subdigitals_planet_net.ogg");
		
		// Loop
		while (running) {
			
			display.update();
			
			//System.err.println("running");
		}
		
		display.stop();
		terminate();
		System.exit(0);
	}
	
	private void terminate() {
		display.shutdown();
		client.disconnect();
		audioManager.shutdown();
	}
	
	public int getX() {
		return locationX;
	}
	
	public int getY() {
		return locationY;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public AudioManager getAudioManager() {
		return audioManager;
	}
	
	public DisplayOld getDisplay() {
		return display;
	}
	
	public static ProjectDelta getInstance() {
		return instance;
	}

}
