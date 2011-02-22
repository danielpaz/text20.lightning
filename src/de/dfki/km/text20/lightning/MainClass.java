/*
 * MainClass.java
 *
 * Copyright (c) 2011, Christoph Käding, DFKI. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 *
 */
package de.dfki.km.text20.lightning;

import javax.swing.UIManager;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;

import com.melloware.jintellitype.JIntellitype;

import de.dfki.km.text20.lightning.gui.TraySymbol;
import de.dfki.km.text20.lightning.plugins.MethodManager;
import de.dfki.km.text20.lightning.worker.FixationCatcher;
import de.dfki.km.text20.lightning.worker.FixationEvaluator;
import de.dfki.km.text20.lightning.worker.PrecisionTrainer;

/**
 * Main entry point.
 * 
 * @author Christoph Käding
 */
public class MainClass {

    /** indicates if the program should react on hotkeys */
    private static boolean isActivated;

    /** indicates if normal mode or trainings mode is activated */
    private static boolean isNormalMode;

    /** pluginmanager for the different methods */
    private static MethodManager methodManager;

    /** icon which is shown in the system tray */
    private static TraySymbol trayIcon;

    /** global pluinmanager which handles all plugins */
    private static PluginManager pluginManager;

    /** global properties of the tool which will be stored between session*/
    private static Properties properties;

    /**
     * Starts the main application.
     * 
     * @param args
     */
    public static void main(String[] args) {

        // Set global look and feel. 
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Unable to load native look and feel");
        }

        // initialize static variables
        properties = new Properties();
        isActivated = true;
        isNormalMode = true;
        pluginManager = PluginManagerFactory.createPluginManager();
        methodManager = new MethodManager();
        trayIcon = new TraySymbol(methodManager);

        // Creates classes which are needed for the two parts (clicking and training) of this tool.
        FixationEvaluator fixationEvaluator = new FixationEvaluator(methodManager);
        PrecisionTrainer precisionTrainer = new PrecisionTrainer(methodManager);

        // main component which listen on trackingevents
        FixationCatcher fixationCatcher = new FixationCatcher(fixationEvaluator, precisionTrainer);

        // start listening
        fixationCatcher.startCatching();
    }

    /**
     * Indicates if the program should react on hotkeys. 
     * 
     * @return true is activated
     */
    public static boolean isActivated() {
        return isActivated;
    }

    /**
     * Toggles between active and inactive status.
     */
    public static void toggleStatus() {
        if (isActivated) {
            
            // show change in tray and console
            System.out.println("Status changed to: inactive");
            trayIcon.showMessage("status", "tool is now deactivated");
            trayIcon.setDeactivatedIcon();
            
            // change status
            isActivated = false;

        } else {
            
            // show change in tray and console
            System.out.println("Status changed to: active");
            trayIcon.showMessage("status", "tool is now activated");
            trayIcon.setActivatedIcon();
            
            // change status
            isActivated = true;
        }
    }

    /**
     * Shuts down the application
     */
    public static void exit() {
        
        // store properties to a file
        properties.writeProperties();
        
        // deactivate the hotkeys
        JIntellitype.getInstance().cleanUp();
        
        // removes icon from system tray
        trayIcon.remove();
        
        // disables plugins
        pluginManager.shutdown();
        
        System.out.println("Session closed.");
        
        // close the tool
        System.exit(0);
    }

    /**
     * Indicates if the tool is in normal or trainings mode.
     * 
     * @return true if is in normal mode 
     */
    public static boolean isNormalMode() {
        return isNormalMode;
    }

    /**
     * Toggles mode between normal an trainings mode.
     */
    public static void toggleMode() {
        if (isNormalMode) {
            
            // shows change in tray and console
            System.out.println("Modus changed to: training");
            trayIcon.showMessage("modus", "trainings mode activated");
            
            // change mode
            isNormalMode = false;
            
        } else {
            
            // shows change in tray and console
            System.out.println("Modus changed to: normal");
            trayIcon.showMessage("modus", "normal mode activated");
            
            // change mode
            isNormalMode = true;
        }
    }
    
    /**
     * shows the given text as popup on the tray icon
     * 
     * @param caption
     * @param text
     */
    public static void showTrayMessage(String caption, String text) {
        trayIcon.showMessage(caption, text);
    }

    /**
     * returns global used pluginmanager, because only one of them should be used at the same time
     * 
     * @return pluginManager
     */
    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * returns the actual properties which includes all configurations which can be changed in the gui
     * 
     * @return current properties object
     */
    public static Properties getProperties() {
        return properties;
    }
}
