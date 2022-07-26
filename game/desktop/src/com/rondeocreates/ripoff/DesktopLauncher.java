package com.rondeocreates.ripoff;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
//import com.rondeocreates.ripoff.Game;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Rip Off");
		config.setWindowSizeLimits( 812, 375, 9999, 9999);
		new Lwjgl3Application(new Game(), config);
	}
}
