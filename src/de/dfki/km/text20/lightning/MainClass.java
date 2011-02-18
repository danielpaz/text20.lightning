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

import java.io.File;

import javax.swing.UIManager;

import com.melloware.jintellitype.JIntellitype;

import de.dfki.km.text20.lightning.gui.TraySymbol;
import de.dfki.km.text20.lightning.plugins.MethodManager;
import de.dfki.km.text20.lightning.worker.FixationCatcher;
import de.dfki.km.text20.lightning.worker.FixationEvaluator;
import de.dfki.km.text20.lightning.worker.PrecisionTrainer;

/**
 * Main entry point.
 * 
 * @author Christoph Kaeding
 */
public class MainClass {

    /** */
    private static String outputPath;
    
    /** shows if the program should react on hotkeys */
    private static boolean isActivated;
    
    /** shows if normal mode or trainings mode is activated */
    private static boolean isNormalMode;
    
    /** shows if screenshots etc are written to the outputpath */
    private static boolean showImages;
    
    /** dimension for the screenshots */
    private static int dimension;
    
    /** pluginmanager for the different methods */
    private static MethodManager methodManager;
    
    /** */
    private static TraySymbol trayIcon;

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
        
        
        isActivated = true;
        isNormalMode = true;
        showImages = false;
        dimension = 100;
        methodManager = new MethodManager();
        trayIcon = new TraySymbol(methodManager);

        outputPath = "./output";

        // Create output path
        try {
            new File(outputPath).mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // TODO: What does this do?
        FixationEvaluator fixationEvaluator = new FixationEvaluator(methodManager);
        PrecisionTrainer precisionTrainer = new PrecisionTrainer(methodManager);
        FixationCatcher fixationCatcher = new FixationCatcher(fixationEvaluator, precisionTrainer);
        fixationCatcher.startCatching();
    }

    /**
     * Indicates if ...? 
     * 
     * @return ???
     */
    public static boolean isActivated() {
        return isActivated;
    }

    /**
     * Toggles what ... ? 
     */
    public static void toggleStatus() {
        if (isActivated) {
            System.out.println("Status changed to: inactive");
            trayIcon.showMessage("status", "tool is now deactivated");
            trayIcon.setDeactivatedIcon();
            isActivated = false;
        } else {
            System.out.println("Status changed to: active");
            trayIcon.showMessage("status", "tool is now activated");
            trayIcon.setActivatedIcon();
            isActivated = true;
        }
    }

    /**
     * Shuts down the application
     */
    public static void exit() {
        JIntellitype.getInstance().cleanUp();
        trayIcon.remove();
        methodManager.getPluginManager().shutdown();
        System.out.println("Session closed.");
        System.exit(0);
    }

    /**
     * Returns true if ... ?
     * 
     * @return ?? 
     */
    public static boolean isNormalMode() {
        return isNormalMode;
    }

    
    /**
     * Toggles mode to ... ?
     */
    public static void toggleMode() {
        if (isNormalMode) {
            System.out.println("Modus changed to: training");
            trayIcon.showMessage("modus", "trainings mode activated");
            isNormalMode = false;
        } else {
            System.out.println("Modus changed to: normal");
            trayIcon.showMessage("modus", "normal mode activated");
            isNormalMode = true;
        }
    }

    
    //// TODO: COMMENT PROPERLY FROM THIS LINE ON ....
    
    
    /** */
    public static boolean showImages() {
        return showImages;
    }

    /** */
    public static void setShowImages(boolean choice) {
        showImages = choice;
    }

    /** */
    public static int getDimension() {
        return dimension;
    }

    /** */
    public static void setDimension(int choosedDimension) {
        dimension = choosedDimension;
    }

    /** */
    public static String getOutputPath() {
        return outputPath;
    }

    /** */
    public static void setOutputPath(String choosedOutputPath) {
        try {
            File outputDir = new File(choosedOutputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            } else {
                if (outputDir.isDirectory()) {
                    outputPath = choosedOutputPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showTrayMessage(String caption, String text) {
        trayIcon.showMessage(caption, text);
    }
}
