/*
 * TraySymbol.java
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
package de.dfki.km.text20.lightning.gui;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

/**
 * The icon which is shown in the system tray.
 * 
 * @author Christoph Käding
 *
 */
public class TraySymbol {
    
    /** */
    private TrayIcon trayIcon;
    
    /** */
    private SystemTray systemTray;

    /** 
     * creates the taskicon 
     * 
     * @param manager 
     * necessary to show and activate plugins by the gui
     */
    public TraySymbol() {
                
        // initialize tray icon and add it to tray
        this.trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(TraySymbol.class.getResource("../resources/TrayIconActive.gif")), "Project Lightning (Desktop)");
        this.trayIcon.setImageAutoSize(true);
        
        if (SystemTray.isSupported()) {
            this.systemTray = SystemTray.getSystemTray();
            try {
                this.trayIcon.setPopupMenu(TrayMenu.getInstance());
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
            this.trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(TraySymbol.class.getResource("../resources/TrayIconActive.gif")));
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
            this.trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(TraySymbol.class.getResource("../resources/TrayIconInactive.gif")));
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
