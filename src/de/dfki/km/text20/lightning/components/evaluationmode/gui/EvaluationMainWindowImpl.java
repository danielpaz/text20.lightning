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

import static net.jcores.CoreKeeper.$;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.components.evaluationmode.CoordinatesXMLParser;
import de.dfki.km.text20.lightning.components.evaluationmode.PrecisionEvaluator;
import de.dfki.km.text20.lightning.components.evaluationmode.WordXMLParser;

/**
 * super quick and dirty evaluation mode main window
 * TODO: refactor this, HotKey integration, PrecisionEvaluator integration and EvaluatorWorker integration
 * 
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class EvaluationMainWindowImpl extends EvaluationMainWindow implements
        ActionListener, WindowListener {

    /** currrent evaluator */
    private PrecisionEvaluator evaluator;

    /** cofigurations panel */
    private ContentPanelConfigImpl configPanel;

    /** current evaluation step */
    private int step;

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
    private ArrayList<String> preparedText;

    /** indicates that the coordinate file is valid */
    private boolean coordinatesValid;

    /** indicates that the text file is valid */
    private boolean textValid;

    /** image which is shown in the JPanel */
    private BufferedImage image;

    /** JLabel which contains the image */
    private JLabel label;

    /** for temporary use, for example to show notifications */
    private Point pointTmp;

    /** timer to draw notifications */
    private Timer notificationTimer;

    /** a list of already selected indexes for preparedText-array */
    private ArrayList<Integer> alreadySelected;

    /** current index of text array*/
    private int textNumber;

    /** indicates if the next button should react */
    private boolean ready;

    /** number of normal text evaluation steps */
    private int sizeNormal;

    /** number of warp text evaluation steps */
    private int sizeWarp;

    /** number of detector text evaluation steps */
    private int sizeDetector;

    /** 
     * indicates which status the currrent shown text has
     * true = highlighted
     * false = not highlighted
     */
    private boolean textStatus;

    /** current word for search */
    private String word;

    /** current used textfile */
    private File textFile;

    /**
     * @param evaluator 
     * 
     */
    @SuppressWarnings("boxing")
    public EvaluationMainWindowImpl(PrecisionEvaluator evaluator) {
        // initialize variables
        this.configPanel = new ContentPanelConfigImpl();
        this.evaluator = evaluator;
        this.step = 0;
        this.preparedCoordinates = new ArrayList<Point>();
        this.preparedText = new ArrayList<String>();
        this.coordinatesValid = true;
        this.textValid = true;
        this.alreadySelected = new ArrayList<Integer>();
        this.alreadySelected.add(-1);
        this.textStatus = false;
        this.ready = true;
        this.labelDescription.setText("Initializing done.");

        // add 1st panel
        this.panelContent.add(this.configPanel);

        // add action listeners
        this.buttonNext.addActionListener(this);
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
        }
    }

    /** 
     * fired if the next bitton is clicked
     * navigates through evaluation
     */
    @SuppressWarnings("boxing")
    private void buttonNextActionPerformed() {

        if (!this.ready) {
            System.out.println("Please confirm current step.");
            this.labelDescription.setText("Please confirm current step.");
            return;
        }

        // reset label
        this.labelDescription.setText("");

        // first step
        if (this.step == 0) {
            // set evaluation steps
            this.evaluateDetector = this.configPanel.isEvaluateDetector();
            this.evaluatePrecision = this.configPanel.isEvaluatePrecision();
            this.evaluateWarper = this.configPanel.isEvaluateWarp();
            this.textValid = true;
            this.coordinatesValid = true;

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

                        // set to get first screenshot
                        this.evaluator.setStepRecognized(true);

                        // set content panel for translation of coordinates
                        this.evaluator.setContentPanel(this.panelContent);

                    } else {
                        this.coordinatesValid = false;
                    }
                } else {
                    this.coordinatesValid = false;
                }
            }

            if (this.evaluateDetector || this.evaluateWarper) {
                this.pathText = this.configPanel.getPathText();
                WordXMLParser wordXMLParser = new WordXMLParser();
                File wordFile = new File(this.pathText);

                // change path from xml-file to directory
                this.pathText = this.pathText.replace("PreparedText.xml", "");

                // validity check
                if (wordFile.exists()) {
                    if (wordXMLParser.isValid(wordFile)) {
                        this.preparedText = wordXMLParser.readFile(wordFile);

                        // indicate that all is fine
                        this.textValid = true;

                        // set to get first screenshot
                        this.evaluator.setStepRecognized(true);

                    } else {
                        this.textValid = false;
                    }
                } else {
                    this.textValid = false;
                }
            }

            // check if all stuff is correct
            if (this.coordinatesValid && this.textValid && ((this.preparedCoordinates.size()) > 0 || (this.preparedText.size() > 2))) {

                // prepare next step
                this.step++;
                this.labelDescription.setText("");

                // calculate sizes
                if (this.evaluateDetector && this.evaluateWarper) {
                    this.sizeDetector = this.preparedText.size() / 3;
                    this.sizeWarp = this.preparedText.size() / 3;
                    this.sizeNormal = this.preparedText.size() - this.sizeDetector - this.sizeWarp;
                } else if (this.evaluateDetector && !this.evaluateWarper) {
                    this.sizeDetector = this.preparedText.size() / 2;
                    this.sizeWarp = 0;
                    this.sizeNormal = this.preparedText.size() - this.sizeDetector;
                } else if (!this.evaluateDetector && this.evaluateWarper) {
                    this.sizeDetector = 0;
                    this.sizeWarp = this.preparedText.size() / 2;
                    this.sizeNormal = this.preparedText.size() - this.sizeWarp;
                } else if (!this.evaluateDetector && !this.evaluateWarper) {
                    this.sizeDetector = 0;
                    this.sizeWarp = 0;
                    this.sizeNormal = 0;
                }

                System.out.println("nomral = " + this.sizeNormal);
                System.out.println("detector = " + this.sizeDetector);
                System.out.println("warp = " + this.sizeWarp);

                // show description png
                if (this.evaluatePrecision) {

                    // paint image to JPanel
                    try {
                        this.image = ImageIO.read(EvaluationMainWindowImpl.class.getResourceAsStream("../resources/DescriptionPrecision.png"));
                        this.setImage();
                    } catch (IOException e) {
                        this.labelDescription.setText(e.toString());
                        e.printStackTrace();
                    }
                    return;
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

                return;
            }
        }

        // start pupil stream if is needed
        if ((this.step == 1) && this.evaluatePrecision) {
            this.evaluator.startPupilStream();
            this.evaluator.setEvaluationStep(0);
        }

        // next steps, if precision evaluation is enabled, +1 because of description png
        if ((this.step > 0) && (this.step < this.preparedCoordinates.size() + 1) && this.evaluatePrecision) {

            // check if ready for next step
            if (!this.evaluator.isStepRecognized()) {
                this.labelDescription.setText("Please confirm the current related point.");
                return;
            }

            // paint image to JPanel
            try {
                this.image = ImageIO.read(new File(this.pathCoordinates + "Image" + (this.step - 1) + ".png"));
                this.setImage();
                this.updateImage(this.preparedCoordinates.get(this.step - 1));
            } catch (IOException e) {
                e.printStackTrace();
                this.labelDescription.setText(e.toString());
            }

            // set evaluator
            this.evaluator.setPreparedPoint(this.preparedCoordinates.get(this.step - 1));
            this.evaluator.setStepRecognized(false);

            // step forward
            this.step++;

            return;
        }

        // precision evaluation done
        if (this.step == this.preparedCoordinates.size() + 1) {

            this.evaluator.setBlockHotkeys(true);

            if (!this.evaluateDetector && !this.evaluateWarper) {

                // if normal text should not be evaluated skip its description
                this.step++;
                this.step++;
                this.step++;
            }

            // flush collected data
            if (this.evaluatePrecision) this.evaluator.leaveEvaluation();

            // show description if some more evaluation should be done
            if (this.evaluateDetector || this.evaluateWarper) {
                try {
                    this.image = ImageIO.read(EvaluationMainWindowImpl.class.getResourceAsStream("../resources/DescriptionNormal.png"));
                    this.setImage();
                } catch (IOException e) {
                    this.labelDescription.setText(e.toString());
                    e.printStackTrace();
                }

                // set needed variables to output time file 
                if (!this.evaluatePrecision) this.evaluator.initTimeFile();

                // step forward
                this.evaluator.setEvaluationStep(1);
                this.step++;
                return;
            }
        }

        // text, normal 
        if ((this.step > (this.preparedCoordinates.size() + 1)) && (this.evaluateDetector || this.evaluateWarper) && (this.step < (this.preparedCoordinates.size() + 1 + this.sizeNormal + 1))) {

            if (!this.textStatus) {
                // generate position
                this.textNumber = -1;
                while (this.alreadySelected.contains(0 + this.textNumber))
                    this.textNumber = Integer.parseInt($.range(0, this.preparedText.size()).random(1).string().join());
                this.alreadySelected.add(0 + this.textNumber);

                // add text
                this.word = this.preparedText.get(this.textNumber);
                this.panelContent.removeAll();
                this.panelContent.add(new ContentPanelTextHint("Your word is '" + this.word + "'."));
                this.setVisible(false);
                this.setVisible(true);

                // step forward
                this.textStatus = true;
                return;
            }

            // add text
            this.labelDescription.setText("Your word is '" + this.word + "'.");
            this.textFile = new File(this.pathText + "Text" + this.textNumber + ".html");
            this.panelContent.removeAll();
            this.panelContent.add(new ContentPanelTextImpl(this));
            this.setVisible(false);
            this.setVisible(true);

            // step forward
            this.textStatus = false;
            this.ready = false;
            this.step++;
            return;
        }

        // text, description detector
        if ((this.step == (this.preparedCoordinates.size() + 1 + this.sizeNormal + 1)) && (this.evaluateDetector)) {

            // reset 
            //            this.alreadySelected.clear();
            //            this.alreadySelected.add(-1);
            this.textStatus = false;

            // show description
            try {
                this.image = ImageIO.read(EvaluationMainWindowImpl.class.getResourceAsStream("../resources/DescriptionDetector.png"));
                this.setImage();
            } catch (IOException e) {
                this.labelDescription.setText(e.toString());
                e.printStackTrace();
            }

            // activate detector
            MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().start();

            // step forward
            this.evaluator.setEvaluationStep(2);
            this.step++;
            return;
        }

        // text, detector
        if ((this.step > (this.preparedCoordinates.size() + 1 + this.sizeNormal + 1)) && (this.evaluateDetector) && (this.step < (this.preparedCoordinates.size() + 1 + this.sizeNormal + this.sizeDetector + 2))) {

            if (!this.textStatus) {
                // generate position
                this.textNumber = -1;
                while (this.alreadySelected.contains(0 + this.textNumber))
                    this.textNumber = Integer.parseInt($.range(0, this.preparedText.size()).random(1).string().join());
                this.alreadySelected.add(0 + this.textNumber);

                // add text
                this.word = this.preparedText.get(this.textNumber);
                this.panelContent.removeAll();
                this.panelContent.add(new ContentPanelTextHint("Your word is '" + this.word + "'."));
                this.setVisible(false);
                this.setVisible(true);

                // step forward
                this.textStatus = true;
                return;
            }

            // add text
            this.labelDescription.setText("Your word is '" + this.word + "'.");
            this.textFile = new File(this.pathText + "Text" + this.textNumber + ".html");
            this.panelContent.removeAll();
            this.panelContent.add(new ContentPanelTextImpl(this));
            this.setVisible(false);
            this.setVisible(true);
            this.evaluator.setBlockHotkeys(false);

            // step forward
            this.textStatus = false;
            this.ready = false;
            this.step++;
            return;
        }

        // overstep detector
        if ((this.step == (this.preparedCoordinates.size() + 1 + this.sizeNormal + 1)) && this.evaluateWarper && !this.evaluateDetector) {
            this.step = this.step + 1;

        }

        // overstep warper
        if ((this.step == (this.preparedCoordinates.size() + 1 + this.sizeNormal + this.sizeDetector + 2)) && !this.evaluateWarper && this.evaluateDetector) {
            this.step = this.step + 1;

        }

        // text, description warper
        if ((this.step == (this.preparedCoordinates.size() + 1 + this.sizeNormal + this.sizeDetector + 2)) && this.evaluateWarper) {

            // reset list
            //            this.alreadySelected.clear();
            //            this.alreadySelected.add(-1);
            this.textStatus = false;

            try {
                this.image = ImageIO.read(EvaluationMainWindowImpl.class.getResourceAsStream("../resources/DescriptionWarp.png"));
                this.setImage();
            } catch (IOException e) {
                this.labelDescription.setText(e.toString());
                e.printStackTrace();
            }

            // activate/deactive plugins
            if (this.evaluateDetector)
                MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().stop();
            MainClass.getInstance().startWarp();

            // step forward
            this.evaluator.setEvaluationStep(3);
            this.step++;
            return;
        }

        // text, warper
        if ((this.step > (this.preparedCoordinates.size() + 1 + this.sizeNormal + this.sizeDetector + 2)) && (this.evaluateWarper) && (this.step < (this.preparedCoordinates.size() + 1 + this.sizeNormal + this.sizeDetector + this.sizeWarp + 3))) {

            if (!this.textStatus) {
                // generate position
                this.textNumber = -1;
                while (this.alreadySelected.contains(0 + this.textNumber))
                    this.textNumber = Integer.parseInt($.range(0, this.preparedText.size()).random(1).string().join());
                this.alreadySelected.add(0 + this.textNumber);

                // add text
                this.word = this.preparedText.get(this.textNumber);
                this.panelContent.removeAll();
                this.panelContent.add(new ContentPanelTextHint("Your word is '" + this.word + "'."));
                this.setVisible(false);
                this.setVisible(true);

                // step forward
                this.textStatus = true;
                return;
            }

            // add text
            this.labelDescription.setText("Your word is '" + this.word + "'.");
            this.textFile = new File(this.pathText + "Text" + this.textNumber + ".html");
            this.panelContent.removeAll();
            this.panelContent.add(new ContentPanelTextImpl(this));
            this.setVisible(false);
            this.setVisible(true);

            // step forward
            this.textStatus = false;
            this.ready = false;
            this.step++;
            return;
        }

        // show finish
        if (this.step == (this.preparedCoordinates.size() + 1 + this.sizeNormal + this.sizeDetector + this.sizeWarp + 3)) {
            // paint image to JPanel
            try {
                this.image = ImageIO.read(EvaluationMainWindowImpl.class.getResourceAsStream("../resources/Finished.png"));
                this.setImage();
            } catch (IOException e) {
                this.labelDescription.setText(e.toString());
                e.printStackTrace();
            }

            // deactivate plugins
            if (this.evaluateDetector && !this.evaluateWarper)
                MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector().stop();
            if (this.evaluateWarper) MainClass.getInstance().stopWarp();

            // step forward
            this.buttonNext.setText("Exit");
            this.step++;

            return;
        }

        // last step
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
        this.label = new JLabel(new ImageIcon(this.image));
        this.label.setSize(this.image.getWidth(), this.image.getHeight());
        this.panelContent.add(this.label);
        // FIXME: why does repaint not work?
        this.setVisible(false);
        this.setVisible(true);
        this.buttonNext.repaint();
        this.labelDescription.repaint();
    }

    /**
     * updates current image inside the JPanel with some notification stuff
     * 
     * @param point which should be shown
     */
    private void updateImage(Point point) {
        // initialize timer, calculation is necessary because repaint of large scaled images will need more time than repaint of small ones
        // TODO: test calculation with more different image sizes
        this.notificationTimer = new Timer(Math.max((4000000 / (this.image.getWidth() * this.image.getHeight())), 50), new ActionListener() {

            private BufferedImage notificationImage;

            private Graphics2D graphics;

            private int size = 100;

            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            @Override
            public void actionPerformed(ActionEvent arg0) {

                // create graphics
                this.notificationImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
                this.notificationImage.setData(image.getData());
                this.graphics = this.notificationImage.createGraphics();

                // block hotkeys
                evaluator.setBlockHotkeys(true);

                // remove old image
                panelContent.removeAll();

                // draw into image
                this.graphics.setColor(new Color(0, 0, 255, 255));
                this.graphics.drawOval(pointTmp.x - this.size / 2, pointTmp.y - this.size / 2, this.size, this.size);
                this.graphics.setColor(new Color(0, 0, 255, 64));
                this.graphics.fillOval(pointTmp.x - this.size / 2, pointTmp.y - this.size / 2, this.size, this.size);

                // draw image to panel
                label = new JLabel(new ImageIcon(this.notificationImage));
                label.setSize(this.notificationImage.getWidth(), this.notificationImage.getHeight());
                panelContent.add(label);

                // label.repaint() or panelContent.repaint() moves image in the upper left corner... WTF why?
                label.setVisible(false);
                label.setVisible(true);

                // decrement size
                this.size = this.size - 5;

                if (size <= 0) {
                    // stop timer
                    notificationTimer.stop();

                    // enable hotkeys
                    evaluator.setBlockHotkeys(false);

                    // draw original picture
                    panelContent.removeAll();
                    label = new JLabel(new ImageIcon(image));
                    label.setSize(image.getWidth(), image.getHeight());
                    panelContent.add(label);

                    // label.repaint() or panelContent.repaint() moves image in the upper left corner... WTF why?
                    label.setVisible(false);
                    label.setVisible(true);
                }
            }
        });

        // initialize point
        this.pointTmp = point;

        // start timer
        this.notificationTimer.start();
    }

    /**
     * @return current word for search
     */
    public String getWord() {
        return this.word;
    }

    /**
     * @return current textfile
     */
    public File getTextFile() {
        return this.textFile;
    }

    /**
     * @param distance 
     * @param time
     */
    public void addToTimeFile(double distance, long time) {
        this.evaluator.setBlockHotkeys(true);
        this.evaluator.addToTimeFile("distance: " + distance + " Pixel, time: " + time + " ms");
        this.ready = true;
        this.labelDescription.setText("OK");
        System.out.println("OK");
    }

    /**
     * @return the screen coordinates of the center of the next button
     */
    public Point getButtonNextCenter() {
        Point center = new Point(this.buttonNext.getBounds().x + this.buttonNext.getBounds().width / 2, this.buttonNext.getBounds().y + this.buttonNext.getBounds().height / 2);
        SwingUtilities.convertPointToScreen(center, this.buttonNext);
        return center;
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
