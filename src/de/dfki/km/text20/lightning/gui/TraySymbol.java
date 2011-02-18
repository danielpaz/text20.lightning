package de.dfki.km.text20.lightning.gui;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;

import de.dfki.km.text20.lightning.plugins.MethodManager;

/**
 * 
 * @author Christoph Kaeding
 *
 */
public class TraySymbol {

    /** */
    //TODO: delete
    private String iconPath;
    /** */
    private String activeIconPath;
    /** */
    private String inactiveIconPath;
    /** */
    private TrayIcon trayIcon;
    /** */
    private SystemTray systemTray;
    /** */
    private MethodManager methodManager;

    /** 
     * creates the taskicon 
     */
    public TraySymbol(MethodManager manager) {
        //        this.iconPath = "D:" + File.separator + "Daten" + File.separator + "DFKI" + File.separator + "Workspace" + File.separator + "Click2Sight" + File.separator + "dependencies";
        this.iconPath = ".";

        this.methodManager = manager;

        this.activeIconPath = this.iconPath + File.separator + "TrayIconActive.gif";
        this.inactiveIconPath = this.iconPath + File.separator + "TrayIconInactive.gif";
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

    /** */
    public boolean setActivatedIcon() {
        if (SystemTray.isSupported()) {
            this.trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(this.activeIconPath));
        } else {
            return false;
        }
        return true;
    }

    /** */
    public boolean setDeactivatedIcon() {
        if (SystemTray.isSupported()) {
            this.trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(this.inactiveIconPath));
        } else {
            return false;
        }
        return true;
    }

    /** */
    public void remove() {
        this.systemTray.remove(this.trayIcon);
    }

    /**
     * shows info messages at the taskicon
     */
    public void showMessage(String caption, String text) {
        this.trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
    }
}
