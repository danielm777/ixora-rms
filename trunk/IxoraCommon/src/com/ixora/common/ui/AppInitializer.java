package com.ixora.common.ui;

import java.awt.EventQueue;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.IxoraCommonModule;
import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.preferences.PreferencesComponent;
import com.ixora.common.ui.preferences.PreferencesConfiguration;
import com.ixora.common.update.UpdateMgr;

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
	 * @param component The name of the main component in the application.
	 * @param callback The application initialization callback.
	 */
	public static void initialize(String component, Callback callback) {
		try {
			showSplashScreen();
			// register this module with the update manager             
			UpdateMgr.registerModule(new IxoraCommonModule());
			// init the message repository using the name of the default component
    		MessageRepository.initialize(component);
			// register preferences configuration if needed
			if(PreferencesConfiguration.get() == null) {
				ConfigurationMgr.registerConfiguration(PreferencesComponent.NAME, new PreferencesConfiguration());
			}
			// callback to application initialization
			callback.initialize();
			// dispose of the splash screen
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
		fSplashScreen = new AppSplashScreen("splash.gif");
		fSplashScreen.splash();
	}
}
