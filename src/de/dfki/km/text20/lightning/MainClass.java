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

import static net.jcores.CoreKeeper.$;

import java.io.File;

import javax.swing.UIManager;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.diagnosis.local.Diagnosis;
import net.xeoh.plugins.diagnosis.local.DiagnosisChannel;

import com.melloware.jintellitype.JIntellitype;

import de.dfki.km.text20.lightning.diagnosis.channels.tracing.LightningTracer;
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
    private boolean isActivated;

    /** indicates if normal mode or trainings mode is activated */
    private boolean isNormalMode;

    /** pluginmanager for the different methods */
    private MethodManager methodManager;

    /** icon which is shown in the system tray */
    private TraySymbol trayIcon;

    /** global pluinmanager which handles all plugins */
    private PluginManager pluginManager;

    /** global properties of the tool which will be stored between session*/
    private Properties properties;

    /** indicates if the JIntellytype.dll is there */
    private boolean dllStatus;

    /** logging channel */
    private DiagnosisChannel<String> channel;

    /** instance of mainclass */
    private static MainClass main;

    /**
     * creates a new instance of the mainclass and initializes it
     * 
     * @param args
     */
    public static void main(String[] args) {
        MainClass.getInstance().init();
    }

    /**
     * all needed stuff is initialized here
     * also the application starts 
     */
    private void init() {
        // Set global look and feel. 
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Unable to load native look and feel.\n");
        }

        // set logging properties
        final JSPFProperties props = new JSPFProperties();
        props.setProperty(Diagnosis.class, "recording.enabled", "true");
        props.setProperty(Diagnosis.class, "recording.file", "diagnosis.record");
        props.setProperty(Diagnosis.class, "recording.format", "java/serialization");
        props.setProperty(Diagnosis.class, "analysis.stacktraces.enabled", "true");
        props.setProperty(Diagnosis.class, "analysis.stacktraces.depth", "10000");

        // check dll
        this.dllStatus = this.checkDll();

        // initialize variables
        this.pluginManager = PluginManagerFactory.createPluginManager(props);
        this.channel = this.pluginManager.getPlugin(Diagnosis.class).channel(LightningTracer.class);
        this.methodManager = new MethodManager(this.pluginManager);
        this.trayIcon = new TraySymbol(this.methodManager);
        this.properties = new Properties();
        this.isActivated = true;
        this.isNormalMode = true;

        // indicate start
        System.out.println("\nSession started.\n");
        this.channel.status("Session started.");

        // Creates classes which are needed for the two parts (clicking and training) of this tool.
        FixationEvaluator fixationEvaluator = new FixationEvaluator(this.methodManager);
        PrecisionTrainer precisionTrainer = new PrecisionTrainer(this.methodManager);

        // main component which listen on trackingevents
        FixationCatcher fixationCatcher = new FixationCatcher(fixationEvaluator, precisionTrainer);

        // check if all things are fine
        if (fixationCatcher.getStatus() && this.dllStatus) {

            // start listening
            fixationCatcher.startCatching();

            // indicate success
            this.showTrayMessage("Initializing successful.");
            System.out.println("\nInitializing successful.\n");
            this.channel.status("Initializing successful.");
        }
    }

    /**
     * gives an object of the mainclass back
     * 
     * @return a singleton instance
     */
    public static MainClass getInstance() {
        if (main == null) {
            main = new MainClass();
        }
        return main;
    }

    /**
     * provides the logging channel
     * 
     * @return the diagnosis channel
     */
    public DiagnosisChannel<String> getChannel() {
        return this.channel;
    }

    /**
     * Indicates if the program should react on hotkeys. 
     * 
     * @return true is activated
     */
    public boolean isActivated() {
        return this.isActivated;
    }

    /**
     * Toggles between active and inactive status.
     */
    public void toggleStatus() {
        if (this.isActivated) {

            // show change in tray
            this.showTrayMessage("Status: tool is now deactivated");
            this.trayIcon.setDeactivatedIcon();

            // change status
            this.isActivated = false;

        } else {

            // show change in tray
            this.showTrayMessage("Status: tool is now activated");
            this.trayIcon.setActivatedIcon();

            // change status
            this.isActivated = true;
        }
    }

    /**
     * Shuts down the application
     */
    public void exit() {

        // store properties to a file
        this.properties.writeProperties();

        // deactivate the hotkeys
        if (this.dllStatus) JIntellitype.getInstance().cleanUp();

        // removes icon from system tray
        this.trayIcon.remove();

        // disables plugins
        this.pluginManager.shutdown();

        this.channel.status("Session closed.");
        System.out.println("\nSession closed.");

        // close the tool
        System.exit(0);
    }

    /**
     * Indicates if the tool is in normal or trainings mode.
     * 
     * @return true if is in normal mode 
     */
    public boolean isNormalMode() {
        return this.isNormalMode;
    }

    /**
     * Toggles mode between normal an trainings mode.
     */
    public void toggleMode() {
        if (this.isNormalMode) {

            // shows change in tray and console
            this.showTrayMessage("Modus: trainings mode activated");

            // change mode
            this.isNormalMode = false;

        } else {

            // shows change in tray and console
            this.showTrayMessage("Modus: normal mode activated");

            // change mode
            this.isNormalMode = true;
        }
    }

    /**
     * shows the given text as popup on the tray icon
     * 
     * @param caption
     * @param text
     */
    public void showTrayMessage(String text) {
        this.trayIcon.showMessage("Project Lightning (Desktop)", text);
    }

    /**
     * returns global used pluginmanager, because only one of them should be used at the same time
     * 
     * @return pluginManager
     */
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    /**
     * returns the actual properties which includes all configurations which can be changed in the gui
     * 
     * @return current properties object
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Checks if the required JIntellitype.dll is placed in the windows directory.
     * If it is not there it tries to unzip it into the System32 directory.
     * If this fails, a message is displayed and the dll is unziped into the "." directory.
     * 
     * @return true if it is there or the copy was successful 
     */
    // FIXME: works only with admin rights
    private boolean checkDll() {

        // create target file
        File destination = new File(System.getenv("SYSTEMROOT") + "/System32/JIntellitype.dll");

        // check if it is already the
        if (!destination.exists()) {

            System.out.println("JIntellytype.dll was not found.");

            try {
                // try to unzip it to the windows directory
                $(MainClass.class.getResourceAsStream("JIntellitype.zip")).zipstream().unzip(".");

                if (destination.exists()) {
                    System.out.println("... but is now placed.\n");
                    // return successful
                    return true;
                }

            } catch (Exception e) {
                // try to unzip it to "."
                $(MainClass.class.getResourceAsStream("JIntellitype.zip")).zipstream().unzip(".");

                // indicate error 
                String msg = new String("Initializing failed. A necessary DLL-file could not be copied into your " + destination.getParent() + " directory. Please do it by yourself or run Project Lightning (Desktop) with granted administration rights.");
                this.showTrayMessage(msg);
                System.out.println("\n" + msg + "\n\n");
                this.channel.status(msg);

                // return not successful
                return false;
            }

        }

        // return successful
        return true;
    }
}
