/*
 * LiveTrainingGuiImpl.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.View;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation.LiveMode;
import de.dfki.km.text20.lightning.hotkey.Hotkey;

/**
 * super quick and dirty implementation of trainings mode
 * 
 * TODO: integrate this into the normal mode
 * 
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class LiveTrainingGuiImpl extends LiveTrainingGui implements ActionListener,
        WindowListener, CaretListener {

    /** */
    private LiveMode liveMode;

    /** */
    private boolean autoChange;

    /** */
    private int formerWordIndexOne;

    /** */
    private int formerWordIndexTwo;

    /** */
    private int currentWordIndex;

    /** */
    private String currentWord;

    /** */
    private int nextWordIndex;

    /** */
    private String nextWord;

    /** */
    private HashMap<String, ArrayList<String>> data;

    /** */
    private boolean autoUpdate;

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
    private String file;

    /**
     * @param liveMode 
     * @param detectorIndex 
     * @param data 
     */
    public LiveTrainingGuiImpl(LiveMode liveMode, int detectorIndex,
                               HashMap<String, ArrayList<String>> data) {
        // initialize variables
        this.liveMode = liveMode;
        this.formerWordIndexOne = -1;
        this.formerWordIndexTwo = -1;
        this.currentWordIndex = -1;
        this.data = data;
        this.file = this.data.keySet().iterator().next();
        this.nextWordIndex = Integer.parseInt($.range(0, this.data.get(this.file).size()).random(1).string().join());
        this.nextWord = this.data.get(this.file).get(this.nextWordIndex);
        this.autoUpdate = false;
        this.inWord = false;
        this.aTyped = false;
        this.lTyped = false;
        this.wTyped = false;
        this.pTyped = false;

        // add listener
        this.buttonReady.addActionListener(this);
        this.addWindowListener(this);

        // enable detector
        Hotkey.getInstance().setBlockHotkeys(false);
        MainClass.getInstance().getInternalPluginManager().setCurrentSaliencyDetector(detectorIndex);
        MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().start();

        // add key listener
        this.textPaneContent.addKeyListener(new KeyListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void keyPressed(KeyEvent event) {
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

        // choose word
        this.chooseWord();

        // initialize textpane
        this.textPaneContent.setContentType("text/html");
        this.textPaneContent.addCaretListener(this);
        this.reloadText();

        // request focus
        SwingUtilities.invokeLater(new Runnable() {

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void run() {
                textPaneContent.requestFocus();
            }
        });

        // set color, size and visible
        this.getContentPane().setBackground(Color.WHITE);
        //        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setBounds(280, 0, 1000, 1000);
        this.setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.buttonReady) {
            this.autoChange = true;
            this.dispose();
            MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().stop();
            Hotkey.getInstance().setBlockHotkeys(true);
            this.liveMode.startEvaluation();
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
     */
    @Override
    public void caretUpdate(CaretEvent event) {
        String actualWord = "";
        int caretIndex = this.textPaneContent.getCaretPosition();

        // test if a, w, p and l was typed inside the text
        if (this.inWord) if (this.aTyped && this.lTyped && this.wTyped && this.pTyped) {

            // reset
            this.inWord = false;
            this.aTyped = false;
            this.lTyped = false;
            this.wTyped = false;
            this.pTyped = false;

            // choose next word
            this.chooseWord();

            // reload text
            SwingUtilities.invokeLater(new Runnable() {

                @SuppressWarnings("synthetic-access")
                @Override
                public void run() {
                    reloadText();
                }
            });
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
            this.inWord = true;
            this.reloadText();
            //            System.out.println("in word");
        } else {
            this.inWord = false;
        }
    }

    /**
     * choose word randomized
     */
    private void chooseWord() {
        // TODO: add sth that catches this.data.get(this.currentFile).size() <= 5
        this.currentWordIndex = this.nextWordIndex;
        while ((this.formerWordIndexOne == this.nextWordIndex) || (this.formerWordIndexTwo == this.nextWordIndex) || (this.currentWordIndex == this.nextWordIndex))
            this.nextWordIndex = Integer.parseInt($.range(0, this.data.get(this.file).size()).random(1).string().join());
        this.formerWordIndexTwo = this.formerWordIndexOne;
        this.formerWordIndexOne = this.currentWordIndex;

        // set current word and current file
        this.currentWord = this.nextWord;
        this.nextWord = this.data.get(this.file).get(this.nextWordIndex);

        System.out.println("current word: " + this.currentWord);
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
        int carretPos = this.textPaneContent.getCaretPosition();

        if (this.inWord) {
            this.textPaneContent.setText($(this.file).file().text().join().replace(this.currentWord, "<font size=\"5\" color=\"red\"><b>" + this.currentWord + "</b></font>").replace(this.nextWord, "<font size=\"5\" color=\"green\"><b>" + this.nextWord + "</b></font>"));
        } else {
            this.textPaneContent.setText($(this.file).file().text().join().replace(this.currentWord, "<font size=\"5\" color=\"red\"><b>" + this.currentWord + "</b></font>"));
            carretPos = Math.max(carretPos - 1, 0);
        }

        this.textPaneContent.setCaretPosition(carretPos);
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
        if (this.inWord) this.textPaneContent.setText($(this.file).file().text().join().replace(this.currentWord, "<font size=\"5\" color=\"red\"><b>" + this.currentWord + "</b></font>").replace(this.nextWord, "<font size=\"5\" color=\"green\"><b>" + this.nextWord + "</b></font>"));
        else
            this.textPaneContent.setText($(this.file).file().text().join().replace(this.currentWord, "<font size=\"5\" color=\"red\"><b>" + this.currentWord + "</b></font>"));
        this.textPaneContent.setCaretPosition(carretPos);
        this.textPaneContent.getDocument().addDocumentListener(this.documentListener);
        this.textPaneContent.setVisible(false);
        this.textPaneContent.setVisible(true);

        // resize
        preferredWidth = this.getSize().width;
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

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent arg0) {
        if (this.autoChange) return;
        MainClass.getInstance().toggleMode();
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowActivated(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosed(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowIconified(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    @Override
    public void windowOpened(WindowEvent e) {
    }
}
