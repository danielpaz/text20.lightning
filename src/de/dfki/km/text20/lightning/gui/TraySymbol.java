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
import java.awt.Image;
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

    /** */
    private Image evaluation;

    /** */
    private Image active;

    /** */
    private Image deactivated;

    /**
     * creates the taskicon
     * 
     * @return true if successful
     */
    public boolean init() {
        if (!System.getProperty("os.name").toLowerCase().contains("win") || !SystemTray.isSupported()) {
            System.out.println("Project Lightning (Desktop) doesn't support " + System.getProperty("os.name") + ".");
            return false;
        }

        // initialize images
        this.active = Toolkit.getDefaultToolkit().getImage(TraySymbol.class.getResource("ActiveNormal.gif"));
        this.deactivated = Toolkit.getDefaultToolkit().getImage(TraySymbol.class.getResource("Inactive.gif"));
        this.evaluation = Toolkit.getDefaultToolkit().getImage(TraySymbol.class.getResource("ActiveEvaluation.gif"));

        // initialize tray icon and add it to tray
        this.trayIcon = new TrayIcon(this.active, "Project Lightning (Desktop)");
        this.systemTray = SystemTray.getSystemTray();

        try {
            this.systemTray.add(this.trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * adds popupmenu to tray
     */
    public void setPopUpMenu() {
        this.trayIcon.setPopupMenu(TrayMenu.getInstance());
    }

    /**
     * shows the 'active' symbol in the tray
     * 
     * @param normal
     *            indicates in which mode the tool is
     */
    public void setActivatedIcon(boolean normal) {
        if (normal) this.trayIcon.setImage(this.active);
        else
            this.trayIcon.setImage(this.evaluation);
    }

    /**
     * shows the 'inactive' symbol in the tray
     */
    public void setDeactivatedIcon() {
        this.trayIcon.setImage(this.deactivated);
    }

    /**
     * removes the trayicon
     */
    public void remove() {
        this.systemTray.remove(this.trayIcon);
    }

    /**
     * shows info messages at the taskicon
     * 
     * @param caption
     * @param text
     */
    public void showMessage(String caption, String text) {
        this.trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
    }
}
