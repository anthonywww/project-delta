package com.github.anthonywww.projectdelta.network.packets;

import java.io.UnsupportedEncodingException;

import com.github.anthonywww.projectdelta.network.Packet;
import com.github.anthonywww.projectdelta.network.PacketHeader;

public class B100DisconnectPacket extends Packet {
	
	private final String message;
	
	public B100DisconnectPacket(byte[] data) throws UnsupportedEncodingException {
		super(PacketHeader.DISCONNECT, data);
		
		if (data != null) {
			message = new String(data, Packet.CHARSET);
		} else {
			message = null;
		}
		
		this.setValid();
	}
	
	public String getDisconnectMessage() {
		return message;
	}
	
}
