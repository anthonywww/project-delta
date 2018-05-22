package com.github.anthonywww.projectdelta.graphic;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.nio.IntBuffer;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.github.anthonywww.projectdelta.ProjectDelta;

public class DisplayOld {

	private long window;
	private int width;
	private int height;
	private TexturedModel model;
	private Texture tex;
	private Shader shader;
	private ScheduledExecutorService scheduledExecutor;
	private ScheduledExecutorService halExecutor;
	private boolean display;
	private Robot hal;

	public DisplayOld() {
		display = false;
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = (int) screenSize.getWidth();
		this.height = (int) screenSize.getHeight();
		
		try {
			hal = new Robot();
		} catch (AWTException e) {}
		
		halExecutor = Executors.newSingleThreadScheduledExecutor();
		
		double lastMouseX = MouseInfo.getPointerInfo().getLocation().getX();
		double lastMouseY = MouseInfo.getPointerInfo().getLocation().getY();
		
		halExecutor.scheduleAtFixedRate(() -> {
			double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
			double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
			if (lastMouseX == mouseX && lastMouseY == mouseY) {
				Random rnd = new Random();
				hal.mouseMove(rnd.nextInt(800), rnd.nextInt(600));
			}
		}, 0L, (1000 * 60) * 5, TimeUnit.MILLISECONDS);
	}

	public void start() {
		if (display) {
			return;
		}
		GLFWErrorCallback.createPrint(System.err).set();
		this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

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
			if (key == GLFW.GLFW_KEY_PERIOD && action == GLFW.GLFW_RELEASE) {
				ProjectDelta.getInstance().setRunning(false);
			}
		});

		GLFW.glfwSetWindowFocusCallback(window, (window, focused) -> {
			if (window == this.window) {
				GLFW.glfwFocusWindow(window);
			}
		});

		GLFW.glfwSetWindowRefreshCallback(window, (window) -> {
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

		GLFW.glfwHideWindow(window);

		float mult = 0.8f;

		float[] vertices = new float[] {
				// Top Right Triangle
				-1.0f * mult, 1.0f * mult, // TOP LEFT (0)
				1.0f * mult, 1.0f * mult, // TOP RIGHT (1)
				1.0f * mult, -1.0f * mult, // BOTTOM RIGHT (2)
				-1.0f * mult, -1.0f * mult, // BOTTOM LEFT (3)
		};

		float[] texcoords = new float[] { 0, 0, 1, 0, 1, 1, 0, 1, };

		int[] indices = new int[] { 0, 1, 2, 2, 3, 0 };

		model = new TexturedModel(vertices, texcoords, indices);

		tex = new Texture("xana_transparent.png");

		shader = new Shader("shader_vertex2.glsl", "shader_fragment2.glsl");
		
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
		GL11.glViewport(0, 0, width, height);

		if (System.getProperty("os.name").startsWith("Windows")) {
			scheduledExecutor.scheduleAtFixedRate(() -> {
				try {
					//Runtime.getRuntime().exec("taskkill /F /IM explorer.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					//Runtime.getRuntime().exec("taskkill /F /IM taskmgr.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					//Runtime.getRuntime().exec("taskkill /F /IM SearchUI.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
				} catch (Throwable t) {}
			}, 0L, 1000L, TimeUnit.MILLISECONDS);
		}
		
		display = true;
	}

	public void update() {
		if (!display) {
			return;
		}
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

	public void stop() {
		if (!display) {
			return;
		}
		scheduledExecutor.shutdownNow();
		
		display = false;
		
		if (System.getProperty("os.name").startsWith("Windows")) {
			/*
			try {
				Runtime.getRuntime().exec("explorer.exe").waitFor(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException | IOException e) {}
			*/
		}
		
		GLFW.glfwHideWindow(window);
		GLFW.glfwDestroyWindow(window);
		Callbacks.glfwFreeCallbacks(window);
		
		// Free the window callbacks and destroy the window
		GLFW.glfwSetErrorCallback(null).free();

		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
	}

	public void shutdown() {
		halExecutor.shutdownNow();
		if (display) {
			stop();
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

}
