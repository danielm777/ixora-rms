package com.ixora.common.ui;

import java.awt.EventQueue;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;

/**
 * Application initializer. It manages the splash screen and it invokes the 
 * application initialization code.
 * @author Daniel Moraru
 */
public class AppInitializer {
	private static final AppLogger logger = AppLoggerFactory.getLogger(AppInitializer.class);
	
	public interface Callback {
		void initialize() throws Throwable;
	}

	private static AppSplashScreen fSplashScreen;

	private AppInitializer() {
		super();
	}
	
	/**
	 * Main.
	 * @param args
	 */
	public static void initialize(Callback callback) {
		try {
			showSplashScreen();
			callback.initialize();
		    EventQueue.invokeLater(new Runnable() {
		        public void run(){
		            fSplashScreen.dispose();
		            fSplashScreen = null;
		        }
		    });
		} catch(Throwable e) {
			logger.error(e);
			System.exit(1);
		}
	}

	/**
	 * Show a simple graphical splash screen, as a quick preliminary to the main
	 * screen.
	 */
	private static void showSplashScreen() {
		fSplashScreen = new AppSplashScreen("/splash.gif");
		fSplashScreen.splash();
	}
}
