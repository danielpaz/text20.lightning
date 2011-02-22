/*
 * TraySymbol.java
 *
 * Copyright (c) 2011, Christoph K�ding, DFKI. All rights reserved.
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
package de.dfki.km.text20.lightning.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;

import de.dfki.km.text20.lightning.plugins.MethodManager;

/**
 * The icon which is shown in the system tray.
 * 
 * @author Christoph K�ding
 *
 */
public class TraySymbol {

    /** */
    //TODO: delete me
    private String iconPath;
    
    /** path to the active icon gif */
    private String activeIconPath;
    
    /** path to the inactive icon gif*/
    private String inactiveIconPath;
    
    /** */
    private TrayIcon trayIcon;
    
    /** */
    private SystemTray systemTray;
    
    /** necessary to show and activate plugins by the gui*/
    private MethodManager methodManager;

    /** 
     * creates the taskicon 
     * 
     * @param manager 
     * necessary to show and activate plugins by the gui
     */
    public TraySymbol(MethodManager manager) {
        
        // TODO: delete me
        this.iconPath = "D:" + File.separator + "Daten" + File.separator + "DFKI" + File.separator + "Workspace" + File.separator + "Click2Sight" + File.separator + "dependencies";
        //        this.iconPath = ".";

        this.methodManager = manager;
        this.activeIconPath = this.iconPath + File.separator + "TrayIconActive.gif";
        this.inactiveIconPath = this.iconPath + File.separator + "TrayIconInactive.gif";
        
        // initialize tray icon and add it to tray
        this.trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(this.activeIconPath), "Click2Sight");
        this.trayIcon.setImageAutoSize(true);

        if (SystemTray.isSupported()) {
            this.systemTray = SystemTray.getSystemTray();
            try {
                this.trayIcon.setPopupMenu(TrayMenu.getInstance(this.methodManager));
                this.systemTray.add(this.trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * shows the 'active' symbol in the tray
     * 
     * @return true if it was successful
     */
    public boolean setActivatedIcon() {
        if (SystemTray.isSupported()) {
             = Toolkit.getDefaultToolkit().getImage(TraySymbol.class.getResource("TrayIconActive.gif"));
            
            this.trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(this.activeIconPath));
        } else {
            return false;
        }
        return true;
    }

    /**
     * shows the 'inactive' symbol in the tray
     * 
     * @return true if it was successful
     */
    public boolean setDeactivatedIcon() {
        if (SystemTray.isSupported()) {
            this.trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(this.inactiveIconPath));
        } else {
            return false;
        }
        return true;
    }

    /** 
     * removes the trayicon 
     */
    public void remove() {
        this.systemTray.remove(this.trayIcon);
    }

    /**
     * shows info messages at the taskicon
     * @param caption 
     * @param text 
     */
    public void showMessage(String caption, String text) {
        this.trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
    }
}
