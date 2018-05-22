package com.github.anthonywww.projectdelta.graphic;

public class RendererOld {
	
	private TexturedModel model;
	private Texture texture;
	private Shader shader;
	
	public RendererOld() {
		
		float mult = 0.8f;
		
		float[] vertices = new float[] {
				// Top Right Triangle
				-1.0f*mult, 1.0f*mult,    // TOP LEFT     (0)
				1.0f*mult, 1.0f*mult,     // TOP RIGHT    (1)
				1.0f*mult, -1.0f*mult,    // BOTTOM RIGHT (2)
				-1.0f*mult, -1.0f*mult,   // BOTTOM LEFT  (3)
		};
		
		float[] texcoords = new float[] {
				0, 0,
				1, 0,
				1, 1,
				0, 1,
		};
		
		int[] indices = new int[] {
				0, 1, 2,
				2, 3, 0
		};
		
		model = new TexturedModel(vertices, texcoords, indices);
		texture = new Texture("xana_transparent.png");
		shader = new Shader("shader_vertex.glsl", "shader_fragment.glsl");
	}
	
	public void render() {
		
		shader.bind();
		texture.bind();
		model.render();
		texture.unbind();
		shader.unbind();
		
	}
	
}
