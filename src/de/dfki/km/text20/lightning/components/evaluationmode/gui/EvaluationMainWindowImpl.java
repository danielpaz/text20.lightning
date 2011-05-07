/*
 * EvaluationMainWindowImpl.java
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import de.dfki.km.text20.lightning.components.evaluationmode.CoordinatesXMLParser;
import de.dfki.km.text20.lightning.components.evaluationmode.PrecisionEvaluator;

/**
 * @author Christoph Käding
 *
 */
@SuppressWarnings("serial")
public class EvaluationMainWindowImpl extends EvaluationMainWindow implements
        ActionListener, WindowListener {

    /** currrent evaluator */
    private PrecisionEvaluator evaluator;

    /** cofigurations panel */
    private ContentPanelZeroImpl configPanel;

    /** current evaluation step */
    private int step;

    /** maximal number of steps */
    private int maxStep;

    /** indicates if the current warper should be evaluated */
    private boolean evaluateWarper;

    /** indicates if precision data should be collected */
    private boolean evaluatePrecision;

    /** indicates if the current detector should be evaluated */
    private boolean evaluateDetector;

    /** path to current prepared coordinate directory */
    private String pathCoordinates;

    /** path to current prepared text directory */
    private String pathText;

    /** list of all prepared coordinates inside of the screenshots, offset from gui have to be added */
    private ArrayList<Point> preparedCoordinates;

    /** list of all prepared text positions which should be highlighted */
    private ArrayList<Point> preparedText;

    /** indicates that the coordinate file is valid */
    private boolean coordinatesValid;

    /** indicates that the text file is valid */
    private boolean textValid;

    /** image which is shown in the JPanel */
    private BufferedImage image;

    /** JLabel which contains the image */
    private JLabel label;

    /** graphic to draw stuff into the screenshots */
    private Graphics2D graphics;

    /**
     * @param evaluator 
     * 
     */
    public EvaluationMainWindowImpl(PrecisionEvaluator evaluator) {
        // initialize variables
        this.configPanel = new ContentPanelZeroImpl();
        this.evaluator = evaluator;
        this.step = 0;
        this.preparedCoordinates = new ArrayList<Point>();
        this.preparedText = new ArrayList<Point>();
        this.coordinatesValid = true;
        this.textValid = true;

        // add 1st panel
        this.panelContent.add(this.configPanel);

        // add action listeners
        this.buttonNext.addActionListener(this);
        this.buttonCancel.addActionListener(this);
        this.addWindowListener(this);

        // set visible
        this.setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.buttonNext) {
            this.buttonNextActionPerformed();
        } else if (event.getSource() == this.buttonCancel) {
            this.buttonCancelActionPerformed();
        }
    }

    /** 
     * fired if the next bitton is clicked
     * navigates through evaluation
     */
    private void buttonNextActionPerformed() {
        // first step
        if (this.step == 0) {
            // set evaluation steps
            this.evaluateDetector = this.configPanel.isEvaluateDetector();
            this.evaluatePrecision = this.configPanel.isEvaluatePrecision();
            this.evaluateWarper = this.configPanel.isEvaluateWarp();

            // test files if necessary
            if (this.evaluatePrecision) {

                // initialize variables
                this.pathCoordinates = this.configPanel.getPathCoordinates();
                CoordinatesXMLParser coordinatesXMLParser = new CoordinatesXMLParser();
                File coordinateFile = new File(this.pathCoordinates);

                // change path from xml-file to directory
                this.pathCoordinates = this.pathCoordinates.replace("PreparedCoordinates.xml", "");

                // validity check
                if (coordinateFile.exists()) {
                    if (coordinatesXMLParser.isValid(coordinateFile)) {
                        this.preparedCoordinates = coordinatesXMLParser.readFile(coordinateFile);

                        // indicate that all is fine
                        this.coordinatesValid = true;

                        // set max size
                        this.maxStep = this.preparedCoordinates.size() + 1;

                    } else {
                        this.coordinatesValid = false;
                    }
                } else {
                    this.coordinatesValid = false;
                }
            }

            // check if all stuff is correct
            if (this.coordinatesValid && this.textValid && (this.maxStep > 1)) {

                // prepare next step
                this.step++;
                this.labelDescription.setText("");

                // show description png
                if (this.evaluatePrecision) {

                    // paint image to JPanel
                    try {
                        this.image = ImageIO.read(EvaluationMainWindowImpl.class.getResourceAsStream("../resources/PrecisionEvaluationDescription.png"));
                        this.setImage();
                    } catch (IOException e) {
                        this.labelDescription.setText(e.toString());
                        e.printStackTrace();
                    }

                } else if (this.evaluateDetector || this.evaluateWarper) {
                    // TODO: add text description
                }

            } else {

                // indicate failure
                if (!this.coordinatesValid) {
                    this.labelDescription.setText("PreparedCoordinates.xml is not recognized or is not valid.");
                    System.out.println("PreparedCoordinates.xml is not recognized or is not valid.");
                } else if (!this.textValid) {
                    this.labelDescription.setText("PreparedText.xml is not recognized or is not valid.");
                    System.out.println("PreparedText.xml is not recognized or is not valid.");
                }
            }

            return;

            // next steps, if precision evaluation is enabled, +1 because of description png
        } else if ((this.step > 0) && (this.step < this.preparedCoordinates.size() + 1) && this.evaluatePrecision) {

            // paint image to JPanel
            try {
                this.image = ImageIO.read(new File(this.pathCoordinates + "Image" + (this.step - 1) + ".png"));
                this.setImage();
                this.updateImage(this.preparedCoordinates.get(this.step - 1));
            } catch (IOException e) {
                e.printStackTrace();
                this.labelDescription.setText(e.toString());
            }

            // step forward
            this.step++;

            return;

            // precision evaluation done
        } else if (this.step == this.preparedCoordinates.size() + 1) {

            if (!this.evaluateDetector) {

                // if detectors shouldn't be evaluated, the description isn't needed
                this.step++;

                if (!this.evaluateWarper) {

                    // if the warper shouldn't also not evaluated, its description and the description of normal use is also not needed
                    this.step++;
                    this.step++;
                }
            } else {

                if (!this.evaluateWarper) {

                    // if warpers shouldn't be evaluated, the description isn't needed
                    this.step++;
                }
            }

        }
        if (this.step == (this.preparedCoordinates.size() + 1 + this.preparedText.size() * 3 + 3)) {
            // paint image to JPanel
            try {
                this.image = ImageIO.read(EvaluationMainWindowImpl.class.getResourceAsStream("../resources/Finished.png"));
                this.setImage();
            } catch (IOException e) {
                this.labelDescription.setText(e.toString());
                e.printStackTrace();
            }

            // step forward
            this.buttonNext.setText("Exit");
            this.step++;

            return;

        }

        // last step
        this.exit();
    }

    /**
     * fired if the cancel button is clicked
     * closes evaluation
     */
    private void buttonCancelActionPerformed() {
        this.exit();
    }

    /**
     * exits evaluation
     */
    public void exit() {
        this.dispose();
    }

    /**
     * sets current image inside the JPanel
     */
    private void setImage() {
        this.panelContent.removeAll();
        this.panelContent.remove(this.configPanel);
        this.label = new JLabel(new ImageIcon(this.image));
        this.label.setSize(this.image.getWidth(), this.image.getHeight());
        this.panelContent.add(this.label);
        // FIXME: why does repaint not work?
        this.dispose();
        this.setVisible(true);
        this.panelButtonPanel.repaint();
    }

    /**
     * updates current image inside the JPanel with some notification stuff
     */
    private void updateImage(Point point) {
        this.label = new JLabel(new ImageIcon(this.image));
        this.label.setSize(this.image.getWidth(), this.image.getHeight());
        SwingUtilities.convertPointToScreen(point, this.label);
        this.graphics = this.image.createGraphics();
        this.graphics.setColor(Color.BLUE);
        this.graphics.drawOval(point.x - 5, point.y - 5, 10, 10);
        this.panelContent.removeAll();
        this.panelContent.remove(this.configPanel);
        this.label = new JLabel(new ImageIcon(this.image));
        this.label.setSize(this.image.getWidth(), this.image.getHeight());
        this.panelContent.add(this.label);
        this.panelContent.repaint();
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
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent e) {
        this.exit();
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