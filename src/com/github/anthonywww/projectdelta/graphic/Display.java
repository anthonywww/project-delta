package com.github.anthonywww.projectdelta.graphic;

import java.nio.IntBuffer;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.github.anthonywww.projectdelta.ProjectDelta;

public class Display {

	private long window;

	public Display() {
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

		GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		
        int width = vidMode.width();
        int height = vidMode.height();
		
		// Create the window
		window = GLFW.glfwCreateWindow(width, height, ProjectDelta.NAME, GLFW.glfwGetPrimaryMonitor(), MemoryUtil.NULL);

		if (window == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Get the thread stack and push a new frame
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			GLFW.glfwGetWindowSize(window, pWidth, pHeight);
		}

		// Make the OpenGL context current
		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
		GLFW.glfwSetWindowShouldClose(window, false);
		
		// Enable v-sync
		GLFW.glfwSwapInterval(1);
		
		
		GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW.GLFW_KEY_F12 && action == GLFW.GLFW_RELEASE) {
				ProjectDelta.getInstance().setRunning(false);
			}
		});
		
		GLFW.glfwSetWindowFocusCallback(window, (window, focused) -> {
			if (window == this.window) {
				GLFW.glfwFocusWindow(window);
			}
		});
		
		GLFW.glfwSetWindowRefreshCallback(window, (window)-> {
			GLFW.glfwFocusWindow(window);
			GLFW.glfwShowWindow(window);
		});
		
		GLFW.glfwSetWindowCloseCallback(window, (window) -> {
			if (window == this.window) {
				GLFW.glfwSetWindowShouldClose(window, false);
			}
		});
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		
		GLFW.glfwSetWindowTitle(window, " ");
		
		// Make the window visible
		GLFW.glfwShowWindow(window);

		// Set the clear color
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		
		
		
		
		
		
		
		
		
		
		
		// Enable textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		
		
		float[] vertices = new float[] {
				// Top Right Triangle
				-0.5f, 0.5f,    // TOP LEFT     (0)
				0.5f, 0.5f,     // TOP RIGHT    (1)
				0.5f, -0.5f,    // BOTTOM RIGHT (2)
				-0.5f, -0.5f,   // BOTTOM LEFT  (3)
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
		
		TexturedModel model = new TexturedModel(vertices, texcoords, indices);
		
		// FIXME: Fix textures
		Texture tex = new Texture("mpc.png");
		
		Shader shader = new Shader("shader_vertex.glsl", "shader_fragment.glsl");
		
		
		while (ProjectDelta.getInstance().isRunning()) {
			GLFW.glfwPollEvents();
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			
			
			shader.bind();
			tex.bind();
			model.render();
			tex.unbind();
			shader.unbind();
			
			// Swap color buffer
			GLFW.glfwSwapBuffers(window);
		}
		
		
		
		
		
		
		
		// Free the window callbacks and destroy the window
		GLFW.glfwDestroyWindow(window);
		Callbacks.glfwFreeCallbacks(window);
		GLFW.glfwSetErrorCallback(null).free();
		
		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
	}
	
	
	
}
