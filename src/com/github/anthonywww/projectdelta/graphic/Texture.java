package com.github.anthonywww.projectdelta.graphic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture {

	private int id;
	private int width;
	private int height;

	public Texture(String filename) {

		try {
			/*
			 * bufferedImage = ImageIO.read(new File("./resources/textures/" +
			 * filename)); //bufferedImage =
			 * ImageIO.read(this.getClass().getResourceAsStream(
			 * "/resources/textures/" + filename)); width =
			 * bufferedImage.getWidth(); height = bufferedImage.getHeight();
			 * 
			 * 
			 * int[] pixels = new int[width * height * 4]; pixels =
			 * bufferedImage.getRGB(0, 0, width, height, null, 0, width);
			 * 
			 * ByteBuffer pixelBuffer = BufferUtils.createByteBuffer(width *
			 * height * 4);
			 * 
			 * for (int i=0; i<height; i++) { for (int j=0; j<width; i++) { int
			 * pixel = pixels[i*width + 4]; pixelBuffer.put((byte) ((pixel >>
			 * 16) & 0xFF)); // RED pixelBuffer.put((byte) ((pixel >> 8) &
			 * 0xFF)); // GREEN pixelBuffer.put((byte) ((pixel) & 0xFF)); //
			 * BLUE pixelBuffer.put((byte) ((pixel >> 24) & 0xFF)); // ALPHA } }
			 * 
			 * pixelBuffer.flip();
			 */

			InputStream is = this.getClass().getResourceAsStream("/textures/" + filename);
			
			if (is == null) {
				throw new IOException("Texture resource not found!");
			}
			
			PNGDecoder decoder = new PNGDecoder(is);

			width = decoder.getWidth();
			height = decoder.getHeight();

			ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buffer, decoder.getWidth() * 4, Format.RGBA);
			buffer.flip();
			
			is.close();

			id = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

			// Tell OpenGL how to unpack the RGBA bytes. Each component is 1
			// byte size
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

			// Pixelate/sharp edges
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}

	public void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void delete() {
		GL11.glDeleteTextures(id);
	}

}
