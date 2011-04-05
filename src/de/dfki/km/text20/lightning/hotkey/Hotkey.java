/*
 * Hotkey.java
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
package de.dfki.km.text20.lightning.hotkey;

import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;
import de.dfki.km.text20.lightning.worker.clickto.FixationEvaluator;
import de.dfki.km.text20.lightning.worker.evaluationmode.PrecisionEvaluator;

/**
 * This is the hotkey management which is designed as a singleton. The hotkeys
 * are linked with thier functions or removed from the listener by this class.
 * 
 * @author Christoph Käding
 * 
 */
public class Hotkey implements HotkeyListener {

    /** instance of singleton */
    private static Hotkey instance = null;

    /** the active hotkey to start the mouse click */
    private HotkeyContainer actionHotkey;

    /** the status hotkey to enable/disable the tool */
    private HotkeyContainer statusHotkey;

    /** temporary stored action hotkey */
    private HotkeyContainer tmpActionHotkey;

    /** temporary stored status hotkey */
    private HotkeyContainer tmpStatusHotkey;

    /** a list of all available hotkeys */
    private ArrayList<HotkeyContainer> hotkeys;

    /** global used properties */
    private Properties properties;

    /** this is necessary for move the mouse and click in normal mode */
    FixationEvaluator fixationEvaluator;

    /** this is needed for evaluation mode */
    private PrecisionEvaluator precisionEvaluator;

    /** singleton instance of the main class */
    private MainClass main;

    /**
     * indicates status of evaluation step true = catch fixation false = catch
     * mouse position
     */
    private boolean evaluationStatus;

    /** */
    private Hotkey(FixationEvaluator fixationEvaluator,
                   PrecisionEvaluator precisionEvaluator) {
        this.fixationEvaluator = fixationEvaluator;
        this.precisionEvaluator = precisionEvaluator;
        this.properties = MainClass.getInstance().getProperties();
        this.main = MainClass.getInstance();
        this.evaluationStatus = true;
        this.initHotkeys();
        this.tmpActionHotkey = null;
        this.tmpStatusHotkey = null;

        if ((this.properties.getActionHotkey() != null) && (this.properties.getStatusHotkey() != null)) {
            // if already hotekeys are stored in the properties, these will be
            // used as current hotkeys
            this.actionHotkey = this.properties.getActionHotkey();
            this.statusHotkey = this.properties.getStatusHotkey();

        } else {
            // otherwise the default hotkeys were set and stored to properties
            this.getCurrentHotkey(1, true);
            this.properties.setActionHotkey(this.actionHotkey);
            this.getCurrentHotkey(2, true);
            this.properties.setStatusHotkey(this.statusHotkey);
        }

        // add hotkeys to listener
        if (this.actionHotkey.getButtonCode() > 0) {
            JIntellitype.getInstance().registerHotKey(1, this.actionHotkey.getModificator(), this.actionHotkey.getButtonCode());
        } else {
            JIntellitype.getInstance().registerHotKey(1, this.actionHotkey.getButtonString());
        }
        if (this.statusHotkey.getButtonCode() > 0) {
            JIntellitype.getInstance().registerHotKey(2, this.statusHotkey.getModificator(), this.statusHotkey.getButtonCode());
        } else {
            JIntellitype.getInstance().registerHotKey(2, this.statusHotkey.getButtonString());
        }

        // enable hotkey listener
        JIntellitype.getInstance().addHotKeyListener(this);
    }

    /**
     * Returns the singleton.
     * 
     * @return instance the only instance of Hotkey
     */
    public static Hotkey getInstance() {
        return instance;
    }

    /**
     * Initializes the hotkeys. This method should be used before every call of
     * any hotkey function.
     * 
     * @param fixationEvaluator
     * @param precisionEvaluator
     */
    public static void init(FixationEvaluator fixationEvaluator,
                            PrecisionEvaluator precisionEvaluator) {
        instance = new Hotkey(fixationEvaluator, precisionEvaluator);
    }

    /**
     * Needed method of JIntellyType which is called when a registered hotkey is
     * typed.
     */
    @Override
    public void onHotKey(int keyCode) {
        switch (keyCode) {

        // action hotkey
        case 1:
            // check if the tool is activated
            if (!this.main.isActivated()) return;

            // increase use count
            this.main.gerReminder().addUse();
            
            // check if trackingdevice provides correct data and if the
            // initializing was successful
            if (!this.main.isTrackingValid() || !this.main.isAllFine()) {
                this.main.playError();
                return;
            }

            // decide which mode
            if (this.main.isNormalMode()) {
                // if the hotkey is typed, the stored fixation will be evaluated
                if (this.fixationEvaluator.evaluateLocation()) this.main.playDing();
                else
                    this.main.playError();
                break;
            }

            if (this.evaluationStatus) {

                // store last fixation point
                if (this.precisionEvaluator.storeFixation()) {
                    this.main.showTrayMessage("Evaluation: fixation position recognized, now place the mouse to the point you look at and press " + this.getCurrentHotkey(1, true) + " again...");
                    this.evaluationStatus = !(this.evaluationStatus);
                    break;
                }

                // indicate error
                this.main.playError();
                this.main.showTrayMessage("Evaluation: --WARNING-- failure in recognizing fixation position, please try again...");
                break;
            }

            if (!this.evaluationStatus) {
                // set mouse position which is associated with the last stored
                // fixation
                if (this.precisionEvaluator.setMousePosition(MouseInfo.getPointerInfo().getLocation())) {

                    // indicate success
                    this.main.playDing();
                    this.main.showTrayMessage("Evaluation: mouse position recognized, now look at the next point and press " + this.getCurrentHotkey(1, true) + " again...");
                } else {

                    // indicate failure
                    this.main.playError();
                    this.main.showTrayMessage("Evaluation: --WARNING-- mouse position was out of dimension or processing was already in progress! now look at the next point and press " + this.getCurrentHotkey(1, true) + " again...");
                }

                // toggle status
                this.evaluationStatus = !(this.evaluationStatus);
                break;
            }

            break;
        // status hotkey
        case 2:
            // change status
            this.main.toggleStatus();
            break;
        default:
            return;
        }
    }

    /**
     * Registers the given hotkey to a specific function.
     * 
     * encoding of index: 1 = action hotkey 2 = status hotkey
     * 
     * store means that the given hotkey will be stored in properties, 
     * otherwise it will only be stored temporary
     * 
     * @param index
     * @param hotkey
     * @param store 
     */
    public void setHotkey(int index, HotkeyContainer hotkey, boolean store) {
        switch (index) {

        // action hotkey
        case 1:
            if (store) {
                this.actionHotkey = hotkey;
                this.properties.setActionHotkey(this.actionHotkey);
            } else
                this.tmpActionHotkey = hotkey;
            break;

        // status hotkey
        case 2:
            if (store) {
                this.statusHotkey = hotkey;
                this.properties.setStatusHotkey(this.statusHotkey);
            } else
                this.tmpStatusHotkey = hotkey;
            break;
        default:
            return;
        }

        // don't change listener if the key isn't stored
        if (!store) return;

        // change listener
        JIntellitype.getInstance().unregisterHotKey(index);
        if (hotkey.getButtonCode() > 0) {
            JIntellitype.getInstance().registerHotKey(index, hotkey.getModificator(), hotkey.getButtonCode());
        } else {
            JIntellitype.getInstance().registerHotKey(index, hotkey.getButtonString());
        }
    }

    /**
     * Returns the current hotkey for a given function.
     * 
     * encoding of index: 1 = action hotkey 2 = status hotkey
     * 
     * stored selects the properties or the temporary hotkey
     * 
     * @param index
     * @param stored 
     * @return the specific hotkey container
     */
    public HotkeyContainer getCurrentHotkey(int index, boolean stored) {
        switch (index) {

        // action hotkey
        case 1:
            // return temporary action hotkey
            if (!stored) {
                if (this.tmpActionHotkey != null) return this.tmpActionHotkey;
                if (this.actionHotkey != null) return this.actionHotkey;
                return this.hotkeys.get(0);
            }

            // set default if it is needed
            if ((this.actionHotkey == null) || this.properties.getActionHotkey() == null)
                this.actionHotkey = this.hotkeys.get(0);
            return this.actionHotkey;

            // satus hotkey
        case 2:
            // return temporary status hotkey
            if (!stored) {
                if (this.tmpStatusHotkey != null) return this.tmpStatusHotkey;
                if (this.statusHotkey != null) return this.statusHotkey;
                return this.hotkeys.get(1);
            }

            // set default if it is needed
            if ((this.statusHotkey == null) || this.properties.getStatusHotkey() == null)
                this.statusHotkey = this.hotkeys.get(1);
            return this.statusHotkey;

            // theoretical never reached
        default:
            return null;
        }
    }

    /**
     * Returns a list of all available hotkeys.
     * 
     * @return hotkeys
     */
    public ArrayList<HotkeyContainer> getHotkeys() {
        return this.hotkeys;
    }

    /**
     * resets the temporary stored keys
     */
    public void resetTmpKeys() {
        this.tmpActionHotkey = null;
        this.tmpStatusHotkey = null;
    }
    
    /**
     * Creates the list of available hotkys.
     */
    // TODO: increase number of hotkeys
    private void initHotkeys() {
        this.hotkeys = new ArrayList<HotkeyContainer>();
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_WIN, KeyEvent.VK_A, "WIN + A"));
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_WIN, KeyEvent.VK_C, "WIN + C"));
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_WIN, KeyEvent.VK_H, "WIN + H"));
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_CONTROL, KeyEvent.VK_LEFT, "CTRL + LEFT"));
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_CONTROL, KeyEvent.VK_UP, "CTRL + UP"));
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_CONTROL, KeyEvent.VK_RIGHT, "CTRL + RIGHT"));
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_CONTROL, KeyEvent.VK_DOWN, "CTRL + DOWN"));
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_CONTROL, KeyEvent.VK_TAB, "CTRL + TAB"));
        this.hotkeys.add(new HotkeyContainer(0, KeyEvent.VK_NUMPAD0, "Numpad 0"));
        this.hotkeys.add(new HotkeyContainer(0, KeyEvent.VK_NUMPAD5, "Numpad 5"));
        this.hotkeys.add(new HotkeyContainer("F7", "F7"));
        this.hotkeys.add(new HotkeyContainer("F8", "F8"));
        this.hotkeys.add(new HotkeyContainer("F9", "F9"));
        this.hotkeys.add(new HotkeyContainer("F12", "F12"));

        // do not work... don't know why?!?
        // this.hotkeys.add(new HotkeyContainer("^", "^"));
        // this.hotkeys.add(new HotkeyContainer("<", "<"));
        // this.hotkeys.add(new
        // HotkeyContainer(JIntellitypeConstants.MOD_CONTROL, KeyEvent.VK_MINUS,
        // "CTRL + -"));
        // this.hotkeys.add(new
        // HotkeyContainer(JIntellitypeConstants.MOD_CONTROL,
        // KeyEvent.VK_PERIOD, "CTRL + ."));
        // this.hotkeys.add(new HotkeyContainer("CTRL + /", "CTRL + /"));
    }
}
