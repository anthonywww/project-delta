package com.github.anthonywww.projectdelta.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.anthonywww.projectdelta.ProjectDelta;
import com.github.anthonywww.projectdelta.utils.ByteUtils;

public class Client {
	
	public static final String DELIMITER = "|";
	private InetSocketAddress address;
	private ExecutorService service;
	private Socket socket;
	private ByteBuffer writeBuffer;
	
	private boolean authenticated;
	
	public Client(InetSocketAddress address) {
		this.address = address;
		this.service = Executors.newSingleThreadExecutor();
		this.writeBuffer = ByteBuffer.allocate(256);
		this.authenticated = false;
	}
	
	public void connect() throws IOException {
		socket = new Socket();
		socket.connect(address, 3000);
		
		service.execute(() -> {
			// Initiate handshake
			send(PacketHeader.HANDSHAKE, generateHandshakeDetails().getBytes());
			
			while (ProjectDelta.getInstance().isRunning() && socket.isConnected()) {
				// Attempt to read off the channel
				int numRead = -1;
				
				if (socket == null) {
					return;
				}
				
				byte[] data = new byte[this.writeBuffer.capacity()];
				
				try {
					numRead = socket.getInputStream().read(data, 0, data.length);
				} catch (IOException e) {
					// The remote peer forcibly closed the connection, cancel the selection key and close the channel.
					System.err.println("Forcefully closed connection");
					disconnect();
					return;
				}

				// Remote client shut the socket down. Do the same from our end and cancel the channel.
				if (numRead == -1) {
					System.err.println("Closed connection");
					disconnect();
					return;
				}
				
				PacketHeader header = null;
				byte[] payload = null;
				String headerString = "UNKNOWN";
				String payloadString = "";
				
				for (PacketHeader h : PacketHeader.values()) {
					if (data[0] == h.getValue()) {
						header = h;
						headerString = h.name();
						break;
					}
				}
				
				if (data.length > 1) {
					payload = Arrays.copyOfRange(data, 1, data.length);
					payloadString = new String(payload, Charset.forName(Packet.CHARSET)).trim();
				}
				
				System.out.println("Packet from server: [" + headerString + "] " + payloadString);
				
				if (header == PacketHeader.DISCONNECT) {
					System.out.println("Disconnect requested from server: " + payloadString);
					System.exit(0);
				}
				
				if (header == PacketHeader.HANDSHAKE_ACK) {
					System.out.println("Got handshake acknowledgement: " + payloadString);
					authenticated = true;
				}
				
				if (authenticated) {
					
					if (header == PacketHeader.SYNC) {
						//long serverTime = ByteUtils.bytesToLong(payload);
						
						send(PacketHeader.SYNC_ACK, ByteUtils.longToBytes(System.currentTimeMillis()));
						System.out.println("Got sync: " + payload);
					}
					
					// Display show
					if (header == PacketHeader.DISPLAY_SHOW) {
						ProjectDelta.getInstance().getDisplay().start();
						continue;
					}
					
					if (header == PacketHeader.DISPLAY_HIDE) {
						ProjectDelta.getInstance().getDisplay().stop();
						continue;
					}
					
					// Audio play
					if (header == PacketHeader.AUDIO_PLAY) {
						ProjectDelta.getInstance().getAudioManager().playById(payloadString);
						send(PacketHeader.AUDIO_PLAY, payloadString.getBytes());
						continue;
					}
					
					// Audio load
					if (header == PacketHeader.AUDIO_LOAD) {
						ProjectDelta.getInstance().getAudioManager().load(payloadString + ".ogg", payloadString);
						send(PacketHeader.AUDIO_LOAD_ACK, payloadString.getBytes());
						continue;
					}
					
					// Audio pause
					if (header == PacketHeader.AUDIO_PAUSE) {
						ProjectDelta.getInstance().getAudioManager().pauseById(payloadString);
						send(PacketHeader.AUDIO_PAUSE, payloadString.getBytes());
						continue;
					}
					
					// Audio stop
					if (header == PacketHeader.AUDIO_STOP) {
						ProjectDelta.getInstance().getAudioManager().stopById(payloadString);
						send(PacketHeader.AUDIO_STOP, payloadString.getBytes());
						continue;
					}
					
				}
				
			}
			
			ProjectDelta.getInstance().setRunning(false);
		});
	}
	
	public synchronized void send(PacketHeader header, byte[] payload) {
		if (socket == null) {
			System.err.println("Attemted to send a packet, however the Channel is null.");
			return;
		}
		if (!socket.isConnected()) {
			System.err.println("Attemted to send a [" + header.name() + "] packet, however the Channel is closed.");
			return;
		}
		
		// Clear the write buffer to be ready for more data
		writeBuffer.clear();
		writeBuffer.put(new byte[writeBuffer.capacity()]);
		writeBuffer.clear();
		
		// Put data
		writeBuffer.put(header.getValue());
		
		// A payload was provided provided
		if (payload != null) {
			try {
				writeBuffer.put(payload);
			} catch (BufferOverflowException e) {
				// BUFFER OVERFLOW, trying to put too much data into a small buffer
				e.printStackTrace();
			}
		}
		
		// Change byte order to network byte-order
		writeBuffer.order(ByteOrder.BIG_ENDIAN);
		
		// Change to read mode
		writeBuffer.flip();
		
		// While there's still data in the writeBuffer to pull from
//		while (writeBuffer.hasRemaining() && socket.isConnected()) {
//		    try {
//				socket.getOutputStream().write(writeBuffer.array());
//			} catch (IOException e) {
//				try {
//					socket.close();
//				} catch (IOException e2) {
//					e2.printStackTrace();
//					break;
//				}
//				e.printStackTrace();
//				break;
//			}
//		}
		
		try {
			if (!socket.isOutputShutdown() && !socket.isClosed()) {
				socket.getOutputStream().write(writeBuffer.array());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Change to write mode
		writeBuffer.flip();
	}
	
	public void disconnect() {
		if (socket != null) {
			if (!socket.isClosed() && socket.isConnected() && !socket.isOutputShutdown()) {
				send(PacketHeader.DISCONNECT, null);
			}
		}
		
		service.shutdownNow();
		try {socket.close();} catch (IOException e) {};
		ProjectDelta.getInstance().setRunning(false);
	}
	
	public String getServerAddress() {
		return address.getHostString();
	}
	
	public int getServerPort() {
		return address.getPort();
	}
	
	private String generateHandshakeDetails() {
		String[] parts = new String[10];
		
		parts[0] = ProjectDelta.NAME;
		parts[1] = ProjectDelta.VERSION;
		parts[2] = ProjectDelta.getInstance().getX() + "";
		parts[3] = ProjectDelta.getInstance().getY() + "";
		//parts[4] = ProjectDelta.getInstance().getDisplay().getWidth();
		//parts[5] = ProjectDelta.getInstance().getDisplay().getHeight();
		parts[4] = "1920";
		parts[5] = "1080";
		parts[6] = System.getProperty("os.name") + " " + System.getProperty("os.version");
		parts[7] = "? (" + Runtime.getRuntime().availableProcessors() + " cores)";
		//parts[8] = ProjectDelta.getInstance().getDisplay().getGPU();
		parts[8] = "igfx";
		parts[9] = (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "";
		
		return String.join(DELIMITER, parts);
	}
	
}
