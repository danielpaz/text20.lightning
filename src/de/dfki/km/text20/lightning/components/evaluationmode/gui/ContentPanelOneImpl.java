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
package de.dfki.km.text20.lightning.components.evaluationmode.gui;

import static net.jcores.CoreKeeper.$;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

/**
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class ContentPanelOneImpl extends ContentPanelOne implements CaretListener {

    private long timestamp;

    private Point area;

    private EvaluationMainWindowImpl mainWindow;

    /**
     * @param evalMainWin 
     */
    public ContentPanelOneImpl(EvaluationMainWindowImpl evalMainWin) {
        this.mainWindow = evalMainWin;
        this.textPaneContent.setBounds(new Rectangle(new Dimension(this.getBounds().width, this.getBounds().height)));
        this.textPaneContent.setContentType("text/html");
        this.textPaneContent.setText($(this.mainWindow.getTextFile()).text().join(""));
        this.textPaneContent.addCaretListener(this);
        //        this.textPaneContent.setEditable(false);
        this.area = this.mainWindow.getArea();
        this.timestamp = System.currentTimeMillis();
    }

    /* (non-Javadoc)
     * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
     */
    @Override
    public void caretUpdate(CaretEvent event) {
        //        System.out.println("index: " + this.textPaneContent.getCaretPosition());
        if ((this.textPaneContent.getCaretPosition() >= this.area.x) && (this.textPaneContent.getCaretPosition() <= this.area.y + 1)) {
            try {
                long neededTime = System.currentTimeMillis() - this.timestamp;
                Rectangle caretRectangle = this.textPaneContent.getUI().modelToView(this.textPaneContent, this.textPaneContent.getCaretPosition());
                Point caretPoint = new Point(caretRectangle.x + caretRectangle.width / 2, caretRectangle.y + caretRectangle.height / 2);
                SwingUtilities.convertPointToScreen(caretPoint, this.textPaneContent);
                this.mainWindow.addToTimeFile(caretPoint.distance(this.mainWindow.getButtonNextCenter()), neededTime);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
}
