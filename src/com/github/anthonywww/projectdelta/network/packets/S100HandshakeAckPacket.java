package com.github.anthonywww.projectdelta.network.packets;

import com.github.anthonywww.projectdelta.network.Packet;
import com.github.anthonywww.projectdelta.network.PacketHeader;

public class S100HandshakeAckPacket extends Packet {
	
	private String name;
	private String version;
	private String operatingSystem;
	
	public S100HandshakeAckPacket(byte[] data) {
		super(PacketHeader.HANDSHAKE_ACK, data);
		
	}
	
	
	
}
