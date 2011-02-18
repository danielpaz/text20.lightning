package de.dfki.km.click2sight.gui;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.dfki.km.click2sight.MainClass;
import de.dfki.km.click2sight.plugins.MethodManager;
import de.dfki.km.click2sight.tools.Tools;

/**
 * 
 * @author Christoph Kaeding
 *
 * a singleton of the tray menu
 */
public class TrayMenu {

    /** */    
    private static PopupMenu menu = null;

    /** 
     * returns the singleton of the tray menu
     */    
    public static PopupMenu getInstance(MethodManager manager) {
        if (menu == null) {
            menu = new PopupMenu();
            final MethodManager methodManager = manager;

            final MenuItem title = new MenuItem("Click2Sight");
            menu.add(title);
            menu.addSeparator();

            final MenuItem configMenu = new MenuItem("Configuration");
            configMenu.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ConfigWindow configWindow = new ConfigWindow(methodManager);
                }
            });
            menu.add(configMenu);

            final MenuItem changeState = new MenuItem("Disable");
            changeState.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    MainClass.changeStatus();
                    if (MainClass.isActivated()) {
                        changeState.setLabel("Disable");
                    } else {
                        changeState.setLabel("Enable");
                    }
                }
            });
            menu.add(changeState);

            final MenuItem changeMode = new MenuItem("Change Modus: Training");
            changeMode.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    MainClass.changeMode();
                    if (MainClass.isNormalMode()) {
                        changeMode.setLabel("Change Modus: Training");
                    } else {
                        changeMode.setLabel("Change Modus: Normal");
                    }
                }
            });
            menu.add(changeMode);

            final MenuItem exit = new MenuItem("Exit");
            exit.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    MainClass.exit();
                }
            });
            menu.add(exit);
        }
        return menu;
    }
}
