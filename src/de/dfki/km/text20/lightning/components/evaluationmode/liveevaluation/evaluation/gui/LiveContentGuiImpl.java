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

import static net.jcores.jre.CoreKeeper.$;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.View;

import de.dfki.km.text20.lightning.hotkey.Hotkey;

/**
 * shows current text and highlights given currentWord
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
    private String currentWord;

    /** */
    private String nextWord;

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

    /** */
    private boolean inWord;

    /** */
    private boolean aTyped;

    /** */
    private boolean lTyped;

    /** */
    private boolean wTyped;

    /** */
    private boolean pTyped;

    /** */
    private ArrayList<Integer> buttons;

    /** */
    private long hitTime;

    /**
     * @param evalMainWin 
     */
    public LiveContentGuiImpl(LiveStepGuiImpl evalMainWin) {
        // initialize variables
        this.mainWindow = evalMainWin;
        this.autoUpdate = false;
        this.inWord = false;
        this.aTyped = false;
        this.lTyped = false;
        this.wTyped = false;
        this.pTyped = false;
        this.hitTime = 0;
        this.currentWord = this.mainWindow.getWord();
        this.nextWord = this.mainWindow.getNextWord();
        this.formerCarretPosition = this.mainWindow.getCarretPosition();
        this.buttons = new ArrayList<Integer>();
        int preferredWidth;
        int preferredHeight;
        View view;

        // reset hotkey count
        Hotkey.getInstance().resetEvaluationCount();

        // add key listener
        this.textPaneContent.addKeyListener(new KeyListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access", "boxing" })
            @Override
            public void keyPressed(KeyEvent event) {
                buttons.add(event.getKeyCode());
                if ((event.getKeyCode() == KeyEvent.VK_A) && inWord) {
                    aTyped = true;
                    //                    System.out.println("a typed");
                } else if ((event.getKeyCode() == KeyEvent.VK_L) && inWord) {
                    lTyped = true;
                    //                    System.out.println("l typed");
                } else if ((event.getKeyCode() == KeyEvent.VK_W) && inWord) {
                    wTyped = true;
                    //                    System.out.println("w typed");
                } else if ((event.getKeyCode() == KeyEvent.VK_P) && inWord) {
                    pTyped = true;
                    //                    System.out.println("p typed");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });

        // initialize document listener
        this.documentListener = new DocumentListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (!inWord) reloadText();
                    }
                });
            }

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void insertUpdate(DocumentEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (!inWord) reloadText();
                    }
                });
            }

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void changedUpdate(DocumentEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (!inWord) reloadText();
                    }
                });
            }
        };
        
        // set timestamp
        this.timestamp = System.currentTimeMillis();

        // initialize textpane
        this.textPaneContent.setContentType("text/html");
        this.textPaneContent.addCaretListener(this);
        this.reloadText();
        this.textPaneContent.setCaretPosition(this.formerCarretPosition);

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

        // show current currentWord
        System.out.println("current currentWord: " + this.currentWord);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
     */
    @Override
    public void caretUpdate(CaretEvent event) {
        String actualWord = "";
        int caretIndex = this.textPaneContent.getCaretPosition();

        // test if a or l was typed inside the text
        if (this.inWord)
            if (this.aTyped && this.lTyped && this.wTyped && this.pTyped) {
                try {
                    long neededTime = System.currentTimeMillis() - this.timestamp;
                    Rectangle formerCaretRectangle = this.textPaneContent.getUI().modelToView(this.textPaneContent, this.formerCarretPosition);
                    this.formerCarretPoint = new Point(formerCaretRectangle.x + formerCaretRectangle.width / 2, formerCaretRectangle.y + formerCaretRectangle.height / 2);
                    SwingUtilities.convertPointToScreen(this.formerCarretPoint, this.textPaneContent);
                    Rectangle currentCaretRectangle = this.textPaneContent.getUI().modelToView(this.textPaneContent, caretIndex);
                    Point caretPoint = new Point(currentCaretRectangle.x + currentCaretRectangle.width / 2, currentCaretRectangle.y + currentCaretRectangle.height / 2);
                    SwingUtilities.convertPointToScreen(caretPoint, this.textPaneContent);
                    this.mainWindow.stepDone(caretPoint.distance(this.formerCarretPoint), this.hitTime, neededTime, this.textPaneContent.getCaretPosition(), this.buttons, Hotkey.getInstance().getEvaluationCount());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            } else
                return;

        // build current currentWord
        try {
            for (int i = caretIndex - 1; i > 0; i--) {
                if (Character.isLetter(this.textPaneContent.getDocument().getText(i, 1).toCharArray()[0])) {
                    actualWord = this.textPaneContent.getDocument().getText(i, 1) + actualWord;
                } else
                    break;
            }
            for (int i = caretIndex; i < this.textPaneContent.getDocument().getLength(); i++) {
                if (Character.isLetter(this.textPaneContent.getDocument().getText(i, 1).toCharArray()[0])) {
                    actualWord = actualWord + this.textPaneContent.getDocument().getText(i, 1);
                } else
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        System.out.println("|" + currentWord + "|");

        if (actualWord.equals(this.currentWord)) {
            this.hitTime = System.currentTimeMillis() - this.timestamp;
            this.inWord = true;
            this.reloadText();
            //            System.out.println("in word");
        } else {
            this.inWord = false;
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
        this.currentCarretPosition = this.textPaneContent.getCaretPosition();

        if (this.inWord) {
            this.textPaneContent.setText($(this.mainWindow.getTextFile()).text().join().replace(this.currentWord, "<font size=\"5\" color=\"red\"><b>" + this.currentWord + "</b></font>").replace(this.nextWord, "<font size=\"5\" color=\"green\"><b>" + this.nextWord + "</b></font>"));
        } else {
            this.textPaneContent.setText($(this.mainWindow.getTextFile()).text().join().replace(this.currentWord, "<font size=\"5\" color=\"red\"><b>" + this.currentWord + "</b></font>"));
            this.currentCarretPosition = Math.max(this.currentCarretPosition - 1, 0);
        }

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
        int carretPos = this.textPaneContent.getCaretPosition();
        this.textPaneContent.getDocument().removeDocumentListener(this.documentListener);
        if (this.inWord) this.textPaneContent.setText($(this.mainWindow.getTextFile()).text().join().replace(this.currentWord, "<font size=\"5\" color=\"red\"><b>" + this.currentWord + "</b></font>").replace(this.nextWord, "<font size=\"5\" color=\"green\"><b>" + this.nextWord + "</b></font>"));
        else
            this.textPaneContent.setText($(this.mainWindow.getTextFile()).text().join().replace(this.currentWord, "<font size=\"5\" color=\"red\"><b>" + this.currentWord + "</b></font>"));
        this.textPaneContent.setCaretPosition(carretPos);
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
