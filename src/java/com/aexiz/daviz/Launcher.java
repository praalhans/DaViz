package com.aexiz.daviz;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.aexiz.daviz.ui.ControlFrame;

public final class Launcher {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.put("swing.boldMetal", Boolean.FALSE);
					MetalLookAndFeel.setCurrentTheme(new DavizTheme());
					UIManager.setLookAndFeel(new MetalLookAndFeel());
					ControlFrame.launch();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
	}
	
}