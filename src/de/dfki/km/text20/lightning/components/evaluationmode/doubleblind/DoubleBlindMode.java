/*
 * DoubleBlindMode.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.doubleblind;

import static net.jcores.CoreKeeper.$;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.gui.DoubleBlindGuiImpl;
import de.dfki.km.text20.lightning.hotkey.Hotkey;

/**
 * double blind evaluation mode
 * at first only normal mode is activated (standard mouse keyboard), after a choosed period of time a of the given two randomized choosed algorithm is available 
 * and after this the other one 
 * 
 * @author Christoph Käding
 */
public class DoubleBlindMode {

    /** */
    private int time;

    /** id of algorithm one */
    private int one;

    /** id of algorithm two */
    private int two;

    /** current id */
    private int current;

    /** */
    private int step;

    /** */
    private Timer timer;

    /**
     * builds instance and opens gui
     */
    @SuppressWarnings("unused")
    public DoubleBlindMode() {
        new DoubleBlindGuiImpl(this);
    }

    /**
     * starts double blind mode
     */
    @SuppressWarnings("boxing")
    public void start() {
        System.out.println();
        this.timer = new Timer(this.time * 60 * 1000, new ActionListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void actionPerformed(ActionEvent e) {
                // initialize variables
                final Object[] options = { "Yes", "No" };
                int choice = -1;

                // stop timer
                timer.stop();

                // test if last step
                if (step != 2) {

                    System.out.println("Step " + (step + 1) + " done.");

                    // show dialog
                    choice = JOptionPane.showOptionDialog(null, "Step " + (step + 1) + " done. Continue with next step?", "Double Blind", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                    // evaluate answer, if yes...  
                    if (choice == 0) nextStep();

                } else {

                    // stop detector
                    MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().stop();

                    // block hotkeys
                    Hotkey.getInstance().setBlockHotkeys(true);

                    // update file
                    $(MainClass.getInstance().getEvaluationSettings()[1] + "/DoubleBlind.log").file().append("Session: " + new SimpleDateFormat("dd.MM.yyyy ', ' HH:mm:ss ").format(new Date()) + "\r\n");
                    $(MainClass.getInstance().getEvaluationSettings()[1] + "/DoubleBlind.log").file().append("- algorithm one: " + MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(one).getInformation().getDisplayName() + "\r\n");
                    $(MainClass.getInstance().getEvaluationSettings()[1] + "/DoubleBlind.log").file().append("- algorithm two: " + MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(two).getInformation().getDisplayName() + "\r\n\r\n");

                    System.out.println("Step " + (step + 1) + " done.");

                    // indicate finish
                    JOptionPane.showOptionDialog(null, "Double Blind finished.", "Double Blind", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "OK" }, 0);

                    System.out.println();
                }
            }
        });

        // choose algorithm randomized
        this.current = Integer.parseInt($.range(1, 2).random(1).string().join());
        if (this.current == 1) {
            this.current = this.one;
        } else {
            this.current = this.two;
        }

        // show dialog and react on answer
        if (JOptionPane.showOptionDialog(null, "Ready to start?", "Double Blind", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Yes", "No" }, 0) == 0)
            this.timer.start();
    }

    private void nextStep() {
        // step forward
        this.step++;

        // unlock hotkeys
        Hotkey.getInstance().setBlockHotkeys(false);

        // react on current step
        if (this.step == 1) {

            // set detector
            MainClass.getInstance().getInternalPluginManager().setCurrentSaliencyDetector(this.current);

            // start detector
            MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().start();

        } else {

            // set the other algorithm
            if (this.current == this.one) {
                this.current = this.two;
            } else {
                this.current = this.one;
            }

            // stop detector
            MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().stop();

            // set detector
            MainClass.getInstance().getInternalPluginManager().setCurrentSaliencyDetector(this.current);

            // start detector
            MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().start();
        }

        // restart timer
        this.timer.start();
    }

    /**
     * @param time the time to set
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * @param one the one to set
     */
    public void setOne(int one) {
        this.one = one;
    }

    /**
     * @param two the two to set
     */
    public void setTwo(int two) {
        this.two = two;
    }
}
