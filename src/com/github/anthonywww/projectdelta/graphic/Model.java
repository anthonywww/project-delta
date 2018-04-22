package com.github.anthonywww.projectdelta.graphic;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class Model {
	
	public static final int DIMENSIONS = 2;
	private int drawCount;
	private int vboId;
	
	public Model(float[] vertices) {
		drawCount = vertices.length / DIMENSIONS;
		
		// Bind, set the vertex data, un-bind
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createFloatBuffer(vertices), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	
	public void render() {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		
		GL11.glVertexPointer(DIMENSIONS, GL11.GL_FLOAT, 0, 0);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, drawCount);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}
	
	
	private FloatBuffer createFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	
}
