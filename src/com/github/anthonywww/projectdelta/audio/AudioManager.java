package com.github.anthonywww.projectdelta.audio;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.libraries.LibraryJOAL;
import paulscode.sound.libraries.LibraryJavaSound;

public class AudioManager {

	private SoundSystem soundSystem;
	
	public AudioManager() {
		try {
			SoundSystemConfig.addLibrary(LibraryJOAL.class);
			SoundSystemConfig.addLibrary(LibraryJavaSound.class);
			SoundSystemConfig.setCodec("ogg", CodecJOgg.class);
			//SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			soundSystem = new SoundSystem();
		} catch (SoundSystemException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void load(String path, String id) {
		soundSystem.loadSound(AudioManager.class.getResource("/audio/" + path), "ogg");
		soundSystem.newStreamingSource(true, id, AudioManager.class.getResource("/audio/" + path), "ogg", false, 0, 0, 0, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
	}
	
	public synchronized void playById(String sourceId) {
		soundSystem.play(sourceId);
	}
	
	public synchronized void setVolume(String sourceId, float value) {
		soundSystem.setVolume(sourceId, value);
	}
	
	public synchronized void setPitch(String sourceId, float value) {
		soundSystem.setPitch(sourceId, value);
	}
	
	public synchronized void pauseById(String sourceId) {
		soundSystem.pause(sourceId);
	}
	
	public synchronized void stopById(String sourceId) {
		soundSystem.stop(sourceId);
	}
	
	public synchronized float getVolume(String sourceId) {
		return soundSystem.getVolume(sourceId);
	}
	
	public synchronized float getPitch(String sourceId) {
		return soundSystem.getPitch(sourceId);
	}
	
	public synchronized void shutdown() {
		if (soundSystem != null) {
			soundSystem.cleanup();
		}
	}
	
	
}
