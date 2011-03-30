/*
 * TrayMenu.java
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

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.dfki.km.text20.lightning.MainClass;

/**
 * A singleton of the menu which is shown by a click on the tray icon.
 * 
 * @author Christoph Käding
 *
 */
public class TrayMenu {

    /** the instance of the menu */
    private static PopupMenu menu = null;

    /** 
     * returns the singleton of the tray menu
     * 
     * @return menu
     *  
     */
    public static PopupMenu getInstance() {

        if (menu == null) {
            menu = new PopupMenu();

            // adds the title to the menu
            final MenuItem title = new MenuItem("Project Lightning (Desktop)");
            menu.add(title);
            menu.addSeparator();

            // adds configuration to the menu
            final MenuItem configMenu = new MenuItem("Configuration");
            configMenu.addActionListener(new ActionListener() {

                // the gui is builded here, because it is only there for configuration and this wont be done often
                @SuppressWarnings("unused")
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ConfigWindowImpl();
                }
            });
            menu.add(configMenu);

            // adds change state button to the menu
            final MenuItem changeState = new MenuItem("Disable");
            changeState.addActionListener(new ActionListener() {

                // depending on the current state, the label for the next state is printed and the state is changed 
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainClass.getInstance().toggleStatus();
                    if (MainClass.getInstance().isActivated()) {
                        changeState.setLabel("Disable");
                    } else {
                        changeState.setLabel("Enable");
                    }
                }
            });
            menu.add(changeState);

            // adds change mode to the menu
            final MenuItem changeMode = new MenuItem("Change Modus: Evaluation");
            changeMode.addActionListener(new ActionListener() {

                // depending on the current mode, the label for the next mode is printed and the modus is changed 
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainClass.getInstance().toggleMode();
                    if (MainClass.getInstance().isNormalMode()) {
                        changeMode.setLabel("Change Modus: Evaluation");
                    } else {
                        changeMode.setLabel("Change Modus: Normal");
                    }
                }
            });
            menu.add(changeMode);

            // adds exit to the menu
            final MenuItem exit = new MenuItem("Exit");
            exit.addActionListener(new ActionListener() {

                // calls exit method of the main class
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainClass.getInstance().exit();
                }
            });
            menu.add(exit);
        }
        return menu;
    }
}
