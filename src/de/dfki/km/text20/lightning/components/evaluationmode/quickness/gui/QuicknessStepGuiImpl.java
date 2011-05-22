/*
 * QuicknessStepGuiImpl.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.quickness.gui;

import static net.jcores.CoreKeeper.$;

import java.awt.Color;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.hotkey.Hotkey;

/**
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class QuicknessStepGuiImpl extends QuicknessStepGui {

    /** */
    private int carretPosition;

    /** */
    private int steps;

    /** */
    private boolean startWithDetector;

    /** */
    private HashMap<String, ArrayList<String>> data;

    /** */
    private int formerWordIndexOne;

    /** */
    private int formerWordIndexTwo;

    /** */
    private int currentWordIndex;

    /** */
    private String currentWord;

    /** */
    private String currentFile;

    /** */
    private String currentMethod;

    private int currentStep;

    private int currentFileIndex;

    private QuicknessContentGuiImpl contentWindow;

    /**
     * @param steps
     * @param startWithDetector
     * @param data
     */
    public QuicknessStepGuiImpl(int steps, boolean startWithDetector,
                                HashMap<String, ArrayList<String>> data) {
        // initialize variables
        this.steps = steps;
        this.startWithDetector = startWithDetector;
        this.data = data;
        this.currentFileIndex = 0;

        // set color
        this.panelContent.setOpaque(true);
        this.panelContent.setBackground(Color.WHITE);

        // add window listener
        this.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void ancestorResized(HierarchyEvent e) {
                if (contentWindow != null) contentWindow.resize();
            }

            @Override
            public void ancestorMoved(HierarchyEvent e) {
            }
        });

        // set maximized
        //        this.setExtendedState(Frame.MAXIMIZED_BOTH);

        // set size
        this.setBounds(280, 0, 1000, 1000);

        // set visible
        this.setVisible(true);

        // initial file entry
        $(MainClass.getInstance().getEvaluationSettings()[1] + "/Quickness.log").file().append("Session: " + new SimpleDateFormat("dd.MM.yyyy', 'HH:mm:ss").format(new Date()) + "\r\n");

        // run through files
        this.nextFile();
    }

    /**
     * step through next file
     */
    private void nextFile() {
        // check if done
        if (this.currentFileIndex >= this.data.keySet().size()) {

            // show thank you
            this.panelContent.removeAll();
            try {
                this.panelContent.add(new JLabel(new ImageIcon(ImageIO.read(QuicknessStepGuiImpl.class.getResourceAsStream("../resources/Finish.png")))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.refresh();

            // last file entry
            $(MainClass.getInstance().getEvaluationSettings()[1] + "/Quickness.log").file().append("\r\n");

            return;
        }

        // initialize next file
        this.currentStep = 0;
        this.currentFile = new ArrayList<String>(this.data.keySet()).get(this.currentFileIndex);
        this.currentFileIndex++;

        // get current file and step through file
        this.stepForward();
    }

    /**
     * steps through current file and indicates highlighting
     * 
     * @param file
     */
    @SuppressWarnings("boxing")
    private void stepForward() {

        // first step
        if (this.currentStep == 0) {

            // reset variables
            this.carretPosition = 1;
            this.formerWordIndexOne = -1;
            this.formerWordIndexTwo = -1;
            this.currentWordIndex = -1;

            if (this.startWithDetector) {

                // enable detector
                Hotkey.getInstance().setBlockHotkeys(false);
                MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().start();
                this.currentMethod = MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().getInformation().getDisplayName();

                // show dialog and react on answer
                if (JOptionPane.showOptionDialog(null, "Ready to start DetectorQuicknessEvaluation?", "Quickness", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Yes", "No" }, 0) != 0) {
                    this.dispose();
                    return;
                }

            } else {

                // set method
                this.currentMethod = "Normal";

                // show dialog and react on answer
                if (JOptionPane.showOptionDialog(null, "Ready to start NormalQuicknessEvaluation?", "Quickness", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Yes", "No" }, 0) != 0) {
                    this.dispose();
                    return;
                }
            }

            // first set is done, switch do other mode
        } else if (this.currentStep == (this.steps)) {

            // reset variables
            this.carretPosition = 1;
            this.formerWordIndexOne = -1;
            this.formerWordIndexTwo = -1;
            this.currentWordIndex = -1;

            if (this.startWithDetector) {
                // stop detector
                Hotkey.getInstance().setBlockHotkeys(true);
                MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().stop();
                this.currentMethod = "Normal";

                // show dialog and react on answer
                if (JOptionPane.showOptionDialog(null, "Ready to start NormalQuicknessEvaluation?", "Quickness", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Yes", "No" }, 0) != 0) {
                    this.dispose();
                    return;
                }

            } else {

                // enable detector
                Hotkey.getInstance().setBlockHotkeys(false);
                MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().start();
                this.currentMethod = MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().getInformation().getDisplayName();

                // show dialog and react on answer
                if (JOptionPane.showOptionDialog(null, "Ready to start DetectorQuicknessEvaluation?", "Quickness", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Yes", "No" }, 0) != 0) {
                    this.dispose();
                    return;
                }
            }

            // both mode are done
        } else if (this.currentStep == (this.steps * 2)) {

            // stop detector
            Hotkey.getInstance().setBlockHotkeys(true);
            MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().stop();

            this.nextFile();
            return;
        }

        // choose word randomized 
        // TODO: add sth that catches this.data.get(this.currentFile).size() == 2
        while ((this.formerWordIndexOne == this.currentWordIndex) || (this.formerWordIndexTwo == this.currentWordIndex))
            this.currentWordIndex = Integer.parseInt($.range(0, this.data.get(this.currentFile).size()).random(1).string().join());
        this.formerWordIndexTwo = this.formerWordIndexOne;
        this.formerWordIndexOne = this.currentWordIndex;

        // set current word and current file
        this.currentWord = this.data.get(this.currentFile).get(this.currentWordIndex);

        // add text
        this.panelContent.removeAll();
        this.contentWindow = new QuicknessContentGuiImpl(this);
        this.panelContent.add(this.contentWindow);
        this.refresh();

        // step forward
        this.currentStep++;
    }

    /**
     * refreshes ui
     * 
     * TODO: change method
     */
    private void refresh() {
        this.panelContent.setVisible(false);
        this.panelContent.setVisible(true);
    }

    /**
     * called if the carret is inside highlighted text
     * 
     * @param distance
     * @param time
     * @param currentCarretPosition
     */
    public void stepDone(double distance, long time, int currentCarretPosition) {
        // set caret position
        this.carretPosition = currentCarretPosition;

        // reset window
        this.contentWindow = null;

        // update file
        $(MainClass.getInstance().getEvaluationSettings()[1] + "/Quickness.log").file().append("- " + this.currentMethod + ": file = " + this.currentFile.substring(this.currentFile.lastIndexOf(File.separator) + 1, this.currentFile.lastIndexOf(".")) + ", word = " + this.currentWord + ", distance = " + ((double) Math.round(distance * 100) / 100) + " Pixel, time = " + time + " ms\r\n");

        // continue
        this.stepForward();
    }

    /**
     * @return the carretPosition
     */
    public int getCarretPosition() {
        return this.carretPosition;
    }

    /**
     * @return current text file
     */
    public File getTextFile() {
        return new File(this.currentFile);
    }

    /**
     * @return current word
     */
    public String getWord() {
        return this.currentWord;
    }
}
