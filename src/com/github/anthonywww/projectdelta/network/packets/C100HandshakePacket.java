package com.github.anthonywww.projectdelta.network.packets;

import java.io.UnsupportedEncodingException;

import com.github.anthonywww.projectdelta.network.Packet;
import com.github.anthonywww.projectdelta.network.PacketHeader;

public class C100HandshakePacket extends Packet {
	
	private String clientName;
	private String clientVersion;
	private int locationX;
	private int locationY;
	private int screenWidth;
	private int screenHeight;
	private String operatingSystem;
	private String cpu;
	private String gpu;
	private int memory;
	
	public C100HandshakePacket(byte[] data) throws UnsupportedEncodingException {
		super(PacketHeader.HANDSHAKE, data);
		
		
		String rawStringData = new String(data, Packet.CHARSET);
		String[] stringData = rawStringData.split(Packet.DELIMITER);
		
		
		this.setValid();
	}
	
	
	
	
}
