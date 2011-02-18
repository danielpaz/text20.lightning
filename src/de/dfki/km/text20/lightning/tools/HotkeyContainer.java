package de.dfki.km.text20.lightning.tools;

/**
 * 
 * @author Christoph Kaeding
 * 
 * Container which stores all needed information for one hotkey.
 */
public class HotkeyContainer {

    /** Integer constant of the modificator key */
    public int modificator;
    /** Integer constant of the hotkey button */
    public int button;
    /** the description of the hotkey which is shown in the comboboxes of the gui */
    public String description;

    /**
     * creates a new hotkey with all needed components
     * 
     * @param modificator
     * @param button
     * @param description
     */
    public HotkeyContainer(int modificator, int button, String description) {
        this.modificator = modificator;
        this.button = button;
        this.description = description;
    }
    
    /** */
    @Override
    public String toString() {
        return this.description;
    }
}
