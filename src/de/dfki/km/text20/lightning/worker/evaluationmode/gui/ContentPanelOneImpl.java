/*
 * ContentPanelOneImpl.java
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
package de.dfki.km.text20.lightning.worker.evaluationmode.gui;

import static net.jcores.CoreKeeper.$;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import de.dfki.km.text20.lightning.worker.evaluationmode.PrecisionEvaluator;

/**
 * @author Christoph Käding
 *
 */
@SuppressWarnings("serial")
public class ContentPanelOneImpl extends ContentPanelOne implements ActionListener,
        CaretListener {

    private StyledDocument doc;

    private Style red;
    
    private PrecisionEvaluator evaluator;

    /**
     * 
     */
    public ContentPanelOneImpl(PrecisionEvaluator evaluator) {
        this.textPaneContent.setContentType("text/html");
        this.textPaneContent.setText($(ContentPanelOneImpl.class.getResourceAsStream("../resources/test.html")).text().join(""));

        this.doc = this.textPaneContent.getStyledDocument();
        this.red = this.textPaneContent.addStyle("Red", null);
        StyleConstants.setForeground(this.red, Color.red);

        this.evaluator = evaluator;
        
        this.buttonStart.addActionListener(this);
        this.textPaneContent.addCaretListener(this);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.buttonStart) {
            this.buttonStartActionPerformed();
        }
    }

    private void buttonStartActionPerformed() {
        this.doc.setCharacterAttributes(0, 5, this.textPaneContent.getStyle("Red"), true);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
     */
    @Override
    public void caretUpdate(CaretEvent e) {
        try {
            // width from rectangles are always 0?
            
            Rectangle rectangleCaret = this.textPaneContent.getUI().modelToView(this.textPaneContent, this.textPaneContent.getCaretPosition());
            Point caretPosition = new Point(rectangleCaret.x + rectangleCaret.width / 2, rectangleCaret.y + rectangleCaret.height / 2);
            SwingUtilities.convertPointToScreen(caretPosition, this.textPaneContent);

            Rectangle rectangleChar = this.textPaneContent.getUI().modelToView(this.textPaneContent, 1);
            Point charPosition = new Point(rectangleChar.x + rectangleChar.width / 2, rectangleChar.y + rectangleChar.height / 2);
            SwingUtilities.convertPointToScreen(charPosition, this.textPaneContent);

            System.out.println(this.textPaneContent.getCaretPosition() + " " + caretPosition + " " + charPosition);
            System.out.println(rectangleCaret + " " + rectangleChar);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

}
