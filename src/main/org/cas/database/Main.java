package org.cas.database;

import javax.swing.UIManager;

import org.cas.database.business.Application;

public class Main {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Application();
	}
}
