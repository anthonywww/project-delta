package com.github.anthonywww.projectdelta.graphic;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class TexturedModel {
	
	public static final int DIMENSIONS = 2;
	private int drawCount;
	private int vertexId;
	private int textureId;
	private int indexId;
	
	public TexturedModel(float[] vertices, float[] texcoords, int[] indices) {
		drawCount = indices.length;
		
		
		// Generate a VBO ID, bind to vboId, set vertex data
		vertexId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createFloatBuffer(vertices), GL15.GL_STATIC_DRAW);
		
		
		// Generate a VBO ID, bind to textureId, set texcoord data, unbind
		textureId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createFloatBuffer(texcoords), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		
		// Generate a VBO ID, bind to indices
		indexId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexId);
		IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
		buffer.put(indices);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER,	0);
		
	}
	
	
	public void render() {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		
		// Bind buffers
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexId);
		GL11.glVertexPointer(DIMENSIONS, GL11.GL_FLOAT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureId);
		
		// Set texture pointer
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		
		// Bind indices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexId);
		GL11.glDrawElements(GL11.GL_TRIANGLES, drawCount, GL11.GL_UNSIGNED_INT, 0);
		
		// Unbind indices and buffers
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}
	
	
	private FloatBuffer createFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	
	
	
	
}
