package com.github.anthonywww.projectdelta.graphic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader {
	
	private int program;
	private int vertexShader;
	private int fragmentShader;
	
	public Shader(String vertexShaderFile, String fragmentShaderFile) {
		program = GL20.glCreateProgram();
		
		// Load and compile vertex shader
		vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(vertexShader, readFile(vertexShaderFile));
		GL20.glCompileShader(vertexShader);
		if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
			System.err.println("#### GL ERROR ####");
			System.err.println("Failed to compile Vertex Shader");
			System.err.println(GL20.glGetShaderInfoLog(vertexShader));
		}
		
		// Load and compile fragment shader
		fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(fragmentShader, readFile(fragmentShaderFile));
		GL20.glCompileShader(fragmentShader);
		if (GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
			System.err.println("#### GL ERROR ####");
			System.err.println("Failed to compile Fragment Shader");
			System.err.println(GL20.glGetShaderInfoLog(fragmentShader));
		}
		
		
		// Attach the compiled shaders to the program
		GL20.glAttachShader(program, vertexShader);
		GL20.glAttachShader(program, fragmentShader);
		
		// Bind attributes
		GL20.glBindAttribLocation(program, 0, "verticies");
		GL20.glBindAttribLocation(program, 1, "texcoords");
		
		
		GL20.glLinkProgram(program);
		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
			System.err.println("#### GL ERROR ####");
			System.err.println("Failed to link program");
			System.err.println(GL20.glGetProgramInfoLog(program));
		}
		
		GL20.glValidateProgram(program);
		if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) != GL11.GL_TRUE) {
			System.err.println("#### GL ERROR ####");
			System.err.println("Failed to validate program");
			System.err.println(GL20.glGetProgramInfoLog(program));
		}
		
	}
	
	
	
	public void bind() {
		GL20.glUseProgram(program);
	}
	
	
	
	public void unbind() {
		GL20.glUseProgram(0);
	}
	
	
	
	private String readFile(String filename) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/shaders/" + filename)));
			String line;
			
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
}
