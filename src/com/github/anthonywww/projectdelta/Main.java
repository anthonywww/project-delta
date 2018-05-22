package com.github.anthonywww.projectdelta;

import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
	
	public static void main(String[] args) {
		int x = -1;
		int y = -1;
		
		if (args.length > 0) {
			
			if (args.length == 2) {
				try {
					x = Integer.parseInt(args[0]);
					y = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {}
			}
			
			if (x >= 0 && y >= 0) {
				new ProjectDelta(x, y);
				return;
			}
			
			File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			System.err.println("Usage: " + file.getName() + ".jar <x> <y>");
			System.exit(1);
		} else {
			try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
			new JFrameInit();
		}
	}
	
}
