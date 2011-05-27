/*
 * LiveContentGuiImpl.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation.gui;

import static net.jcores.CoreKeeper.$;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.View;

/**
 * shows current text and highlights given word
 * gives time distance and carretpostion back
 * 
 * TODO: handle words which are part of other words
 *  
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class LiveContentGuiImpl extends LiveContentGui implements CaretListener {

    /** */
    private long timestamp;

    /** */
    private String word;

    /** */
    private LiveStepGuiImpl mainWindow;

    /** */
    private int formerCarretPosition;

    /** */
    private Point formerCarretPoint;

    /** */
    private boolean autoUpdate;

    /** */
    private int currentCarretPosition;

    /** */
    private DocumentListener documentListener;

    /**
     * @param evalMainWin 
     */
    public LiveContentGuiImpl(LiveStepGuiImpl evalMainWin) {
        // initialize variables
        this.mainWindow = evalMainWin;
        this.autoUpdate = false;
        this.word = this.mainWindow.getWord();
        this.formerCarretPosition = this.mainWindow.getCarretPosition();
        int preferredWidth;
        int preferredHeight;
        View view;

        // initialize document listener
        this.documentListener = new DocumentListener() {

            @SuppressWarnings("synthetic-access")
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        reloadText();
                    }
                });
            }

            @SuppressWarnings("synthetic-access")
            @Override
            public void insertUpdate(DocumentEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        reloadText();
                    }
                });
            }

            @SuppressWarnings("synthetic-access")
            @Override
            public void changedUpdate(DocumentEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        reloadText();
                    }
                });
            }
        };

        // initialize textpane
        this.textPaneContent.setContentType("text/html");
        this.textPaneContent.setText($(this.mainWindow.getTextFile()).text().join().replace(this.word, "<font size=\"5\" color=\"red\"><b>" + this.word + "</b></font>"));
        this.textPaneContent.setCaretPosition(this.formerCarretPosition);
        this.textPaneContent.addCaretListener(this);
        this.textPaneContent.getDocument().addDocumentListener(this.documentListener);

        // resize
        preferredWidth = this.mainWindow.getSize().width;
        view = this.textPaneContent.getUI().getRootView(this.textPaneContent);
        view.setSize(preferredWidth - 50, Integer.MAX_VALUE);
        preferredHeight = (int) view.getPreferredSpan(View.Y_AXIS);
        this.textPaneContent.setPreferredSize(new Dimension(preferredWidth - 50, preferredHeight + 50));

        // request focus
        SwingUtilities.invokeLater(new Runnable() {

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void run() {
                textPaneContent.requestFocus();
            }
        });

        this.timestamp = System.currentTimeMillis();

        // show current word
        System.out.println("current word: " + this.word);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
     */
    @Override
    public void caretUpdate(CaretEvent event) {
        String currentWord = "";
        int caretIndex = this.textPaneContent.getCaretPosition();

        // build current word
        try {
            for (int i = caretIndex - 1; i > 0; i--) {
                if (Character.isLetter(this.textPaneContent.getDocument().getText(i, 1).toCharArray()[0])) {
                    currentWord = this.textPaneContent.getDocument().getText(i, 1) + currentWord;
                } else
                    break;
            }
            for (int i = caretIndex; i < this.textPaneContent.getDocument().getLength(); i++) {
                if (Character.isLetter(this.textPaneContent.getDocument().getText(i, 1).toCharArray()[0])) {
                    currentWord = currentWord + this.textPaneContent.getDocument().getText(i, 1);
                } else
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        System.out.println("|" + currentWord + "|");

        if (currentWord.equals(this.word)) {
            try {
                long neededTime = System.currentTimeMillis() - this.timestamp;
                Rectangle formerCaretRectangle = this.textPaneContent.getUI().modelToView(this.textPaneContent, this.formerCarretPosition);
                this.formerCarretPoint = new Point(formerCaretRectangle.x + formerCaretRectangle.width / 2, formerCaretRectangle.y + formerCaretRectangle.height / 2);
                SwingUtilities.convertPointToScreen(this.formerCarretPoint, this.textPaneContent);
                Rectangle currentCaretRectangle = this.textPaneContent.getUI().modelToView(this.textPaneContent, caretIndex);
                Point caretPoint = new Point(currentCaretRectangle.x + currentCaretRectangle.width / 2, currentCaretRectangle.y + currentCaretRectangle.height / 2);
                SwingUtilities.convertPointToScreen(caretPoint, this.textPaneContent);
                this.mainWindow.stepDone(caretPoint.distance(this.formerCarretPoint), neededTime, this.textPaneContent.getCaretPosition());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * reloads text if something is changed
     */
    private void reloadText() {
        // test if autochange occurs call
        if (this.autoUpdate) return;

        // update text
        this.autoUpdate = true;
        this.textPaneContent.getDocument().removeDocumentListener(this.documentListener);
        this.currentCarretPosition = this.textPaneContent.getCaretPosition() - 1;
        this.textPaneContent.setText($(this.mainWindow.getTextFile()).text().join().replace(this.word, "<font size=\"5\" color=\"red\"><b>" + this.word + "</b></font>"));
        this.textPaneContent.setCaretPosition(this.currentCarretPosition);
        this.textPaneContent.getDocument().addDocumentListener(this.documentListener);
        this.textPaneContent.setVisible(false);
        this.textPaneContent.setVisible(true);

        // request focus
        SwingUtilities.invokeLater(new Runnable() {

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void run() {
                textPaneContent.requestFocus();
            }
        });

        this.autoUpdate = false;
    }

    /**
     * resizes text pane
     */
    public void resize() {
        // initialize variables
        int preferredWidth;
        int preferredHeight;
        View view;

        // test if autochange occurs call
        if (this.autoUpdate) return;

        // update text
        this.autoUpdate = true;
        this.textPaneContent.getDocument().removeDocumentListener(this.documentListener);
        this.textPaneContent.setText($(this.mainWindow.getTextFile()).text().join().replace(this.word, "<font size=\"5\" color=\"red\"><b>" + this.word + "</b></font>"));
        this.textPaneContent.setCaretPosition(1);
        this.textPaneContent.getDocument().addDocumentListener(this.documentListener);
        this.textPaneContent.setVisible(false);
        this.textPaneContent.setVisible(true);

        // resize
        preferredWidth = this.mainWindow.getSize().width;
        view = this.textPaneContent.getUI().getRootView(this.textPaneContent);
        view.setSize(preferredWidth - 50, Integer.MAX_VALUE);
        preferredHeight = (int) view.getPreferredSpan(View.Y_AXIS);
        this.textPaneContent.setPreferredSize(new Dimension(preferredWidth - 50, preferredHeight + 50));

        // request focus
        SwingUtilities.invokeLater(new Runnable() {

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void run() {
                textPaneContent.requestFocus();
            }
        });

        this.autoUpdate = false;
    }
}
