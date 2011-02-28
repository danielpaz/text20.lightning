/*
 * HotkeyContainer.java
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

import java.io.Serializable;

import org.simpleframework.xml.Attribute;

/**
 * Container which stores all needed information for one hotkey.
 * For documentation see: http://melloware.com/products/jintellitype/index.html
 * For valid windows keycodes see e.g.: http://www.sybase.com/detail?id=47760 
 * 
 * @author Christoph Käding
 * 
 */
@SuppressWarnings("serial")
public class HotkeyContainer implements Serializable {

    /** Integer constant of the modificator key, this is an jIntelliType own constant*/
    @Attribute
    public int modificator;
    
    /** Integer constant of the hotkey button, this seems to be normal ASCII code */
    @Attribute
    public int buttonCode;
    
    /** String of the used key combination */
    @Attribute
    public String buttonString;
    
    /** the description of the hotkey which is shown in the comboboxes of the gui */
    @Attribute
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
        this.buttonCode = button;
        this.buttonString = "";
        this.description = description;
    }
    
    /**
     * creates a new hotkey with all needed components
     * 
     * @param buttonString
     * @param description
     */
    public HotkeyContainer(String buttonString, String description){
        this.modificator = -1;
        this.buttonCode = -1;
        this.buttonString = buttonString;
        this.description = description;
    }
    
    /** 
     * to show the description in the comboboxes
     */
    @Override
    public String toString() {
        return this.description;
    }
}
