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
package de.dfki.km.text20.lightning.tools;

import java.util.ArrayList;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

import de.dfki.km.text20.lightning.MainClass;

/**
 * This is the hotkey management which is designed as a singleton. The hotkeys are linked with thier functions or removed from the listener by this class.
 * 
 * @author Christoph Käding
 * 
 */
public class Hotkey implements HotkeyListener {

    /** instance of singleton */
    private static Hotkey instance = null;

    /** indicates if the action hotkey is typed */
    private boolean isHotKeyTyped;

    /** the active hotkey to start the mouse click */
    private HotkeyContainer actionHotkey;

    /** the status hotkey to enable/disable the tool */
    private HotkeyContainer statusHotkey;

    /** a list of all available hotkeys */
    private ArrayList<HotkeyContainer> hotkeys;

    /** */
    private Hotkey() {

        this.isHotKeyTyped = false;
        this.initHotkeys();

        if ((MainClass.getProperties().getActionHotkey() != null) && (MainClass.getProperties().getStatusHotkey() != null)) {
            // if already hotekeys are stored in the properties, these will be used as current hotkeys
            this.actionHotkey = MainClass.getProperties().getActionHotkey();
            this.statusHotkey = MainClass.getProperties().getStatusHotkey();

        } else {
            // otherwise the default hotkeys were set and stored to properties
            this.actionHotkey = this.hotkeys.get(0);
            MainClass.getProperties().setActionHotkey(this.actionHotkey);
            this.statusHotkey = this.hotkeys.get(1);
            MainClass.getProperties().setStatusHotkey(this.statusHotkey);
        }

        // add hotkeys to listener
        JIntellitype.getInstance().registerHotKey(1, this.actionHotkey.modificator, this.actionHotkey.button);
        JIntellitype.getInstance().registerHotKey(2, this.statusHotkey.modificator, this.statusHotkey.button);
        JIntellitype.getInstance().addHotKeyListener(this);
    }

    /**
     * Returns the singleton. This method should be used before every call of any hotkey function.   
     * 
     * @return instance 
     * the only instance of Hotkey
     */
    public static Hotkey getInstance() {
        if (instance == null) {
            instance = new Hotkey();
        }
        return instance;
    }

    /**
     * Indicates if the action hotkey is typed.
     * 
     * @return isHotKeyTyped
     */
    public boolean isTyped() {
        return this.isHotKeyTyped;
    }

    /**
     * If the hotkey is typed and the algorithm which have to be processed after is finished, the boolean have to be reseted with this function.
     */
    public void resetHotkeyTyped() {
        this.isHotKeyTyped = false;
    }

    /**
     * Needed method of JIntellyType which is called when a registered hotkey is typed.
     */
    @Override
    public void onHotKey(int keyCode) {
        switch (keyCode) {

        // action hotkey
        case 1:
            this.isHotKeyTyped = true;
            break;

        // status hotkey
        case 2:
            // change status
            MainClass.toggleStatus();

            // necessary if status is changed in trainings mode 
            this.isHotKeyTyped = false;
            break;
        default:
            return;
        }
    }

    /**
     * Registers the given hotkey to a specific function.
     * 
     * encoding of index:
     * 1 = action hotkey
     * 2 = status hotkey
     * 
     * @param index
     * @param hotkey
     */
    public void setHotkey(int index, HotkeyContainer hotkey) {
        switch (index) {

        // action hotkey
        case 1:
            this.actionHotkey = hotkey;
            MainClass.getProperties().setActionHotkey(this.actionHotkey);
            System.out.println("action hotkey changed: " + hotkey);
            break;

        // status hotkey
        case 2:
            this.statusHotkey = hotkey;
            MainClass.getProperties().setStatusHotkey(this.statusHotkey);
            System.out.println("status hotkey changed: " + hotkey);
            break;
        default:
            return;
        }

        // change listener
        JIntellitype.getInstance().unregisterHotKey(index);
        JIntellitype.getInstance().registerHotKey(index, hotkey.modificator, hotkey.button);
    }

    /**
     * Returns the current hotkey for a given function.
     * 
     * encoding of index:
     * 1 = action hotkey
     * 2 = status hotkey
     * 
     * @param index
     * @return the specific hotkey container
     */
    public HotkeyContainer getCurrentHotkey(int index) {
        switch (index) {

        // action hotkey 
        case 1:
            return this.actionHotkey;

            // satus hotkey
        case 2:
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
     * Creates the list of available hotkys.
     */
    //TODO: increase number of hotkeys
    private void initHotkeys() {
        this.hotkeys = new ArrayList<HotkeyContainer>();
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_WIN, 'A', "WIN + A"));
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_WIN, 'C', "WIN + C"));
        this.hotkeys.add(new HotkeyContainer(JIntellitypeConstants.MOD_WIN, 'H', "WIN + H"));
    }
}
