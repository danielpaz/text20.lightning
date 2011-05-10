/*
 * ContentPanelTextHint.java
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
 */
package de.dfki.km.text20.lightning.components.evaluationmode.gui;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class ContentPanelTextHint extends ContentPanelText {

    /**
     * @param hint 
     */
    public ContentPanelTextHint(String hint) {
        this.textPaneContent.setBounds(new Rectangle(new Dimension(this.getBounds().width, this.getBounds().height)));
        this.textPaneContent.setContentType("text/html");
        this.textPaneContent.setText(hint);
        this.textPaneContent.setEditable(false);
    }
}
