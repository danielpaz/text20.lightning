package de.dfki.km.text20.lightning.tools;

import java.util.ArrayList;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import de.dfki.km.text20.lightning.MainClass;

/**
 * 
 * @author Christoph Kaeding
 * 
 * This is the hotkey management which is designed as a kind of singleton. 
 */
public class Hotkey implements HotkeyListener {

    /** instance of singleton */
    private static Hotkey instance = null;
    /** */
    private static boolean isHotKeyTyped;
    /** the active hotkey to start the mouse click */
    private static HotkeyContainer actionHotkey;
    /** the active hotkey to enable/disable the tool */
    private static HotkeyContainer statusHotkey;
    /** a list of all available hotkeys */
    private static ArrayList<HotkeyContainer> hotkeys;

    /** */
    private Hotkey() {
        isHotKeyTyped = false;
        this.initHotkeys();
        actionHotkey = hotkeys.get(0);
        statusHotkey = hotkeys.get(1);
        JIntellitype.getInstance().registerHotKey(1, actionHotkey.modificator, actionHotkey.button);
        JIntellitype.getInstance().registerHotKey(2, statusHotkey.modificator, statusHotkey.button);
        JIntellitype.getInstance().addHotKeyListener(this);
    }

    /**
     * Returns the singleton. This method should be used before every call of any hotkey function.   
     * 
     * @return
     */
    private static Hotkey getInstance() {
        if (instance == null) {
            instance = new Hotkey();
        }
        return instance;
    }

    /**
     * Shows if the hotkey is type.
     * 
     * @return
     */
    public static boolean isTyped() {
        return Hotkey.getInstance().isHotKeyTyped;
    }

    /**
     * If the hotkey is typed and the algorithm which have to be processed after is finished, the boolean is reseted with this function.
     */
    public static void resetHotkeyTyped() {
        Hotkey.getInstance().isHotKeyTyped = false;
    }

    /**
     * Needed method of JIntellyType which is called when a registered hotkey is typed.
     */
    @Override
    public void onHotKey(int keyCode) {
        switch (keyCode) {
        case 1:
            Hotkey.getInstance().isHotKeyTyped = true;
            break;
        case 2:
            MainClass.toggleStatus();
            Hotkey.getInstance().isHotKeyTyped = false;
            break;
        default:
            return;
        }
    }

    /**
     * Registers an new hotkey to a specific function.
     * 
     * encoding of index:
     * 1 = action hotkey
     * 2 = status hotkey
     * 
     * @param index
     * @param hotkey
     */
    public static void setHotkey(int index, HotkeyContainer hotkey) {
        switch (index) {
        case 1:
            Hotkey.getInstance().actionHotkey = hotkey;
            System.out.println("action hotkey changed: " + hotkey);
            break;
        case 2:
            Hotkey.getInstance().statusHotkey = hotkey;
            System.out.println("status hotkey changed: " + hotkey);
            break;
        default:
            return;
        }
        JIntellitype.getInstance().unregisterHotKey(index);
        JIntellitype.getInstance().registerHotKey(index, hotkey.modificator, hotkey.button);
    }

    /**
     * returns the current hotkey for a given function.
     * 
     * encoding of index:
     * 1 = action hotkey
     * 2 = status hotkey
     * 
     * @param index
     * @return
     */
    public static HotkeyContainer getCurrentHotkey(int index) {
        switch (index) {
        case 1:
            return Hotkey.getInstance().actionHotkey;
        case 2:
            return Hotkey.getInstance().statusHotkey;
        default:
            return null;
        }
    }

    /**
     * Returns a list of all available hotkeys.
     * 
     * @return
     */
    public static ArrayList<HotkeyContainer> getHotkeys() {
        return Hotkey.getInstance().hotkeys;
    }

    /**
     * Creates the list of available hotkys.
     */
    //TODO: increase number of hotkeys
    private void initHotkeys() {
        hotkeys = new ArrayList<HotkeyContainer>();
        hotkeys.add(new HotkeyContainer(JIntellitype.MOD_WIN, (int) 'A', "WIN + A"));
        hotkeys.add(new HotkeyContainer(JIntellitype.MOD_WIN, (int) 'C', "WIN + C"));
        hotkeys.add(new HotkeyContainer(JIntellitype.MOD_WIN, (int) 'H', "WIN + H"));
    }
}
