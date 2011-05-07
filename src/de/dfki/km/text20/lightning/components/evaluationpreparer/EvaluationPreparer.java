/*
 * EvaluationPreparer.java
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
package de.dfki.km.text20.lightning.components.evaluationpreparer;

import static net.jcores.CoreKeeper.$;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.components.evaluationpreparer.gui.PreparerWindow;

/**
 * prepares screenshots for precision evaluation
 * 
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class EvaluationPreparer extends PreparerWindow implements ActionListener,
        HotkeyListener, WindowListener {

    /** singleton instance */
    private static EvaluationPreparer main;

    /** file chooser */
    private JFileChooser chooser;

    /** current output path */
    private String path;

    /** 
     * indicates status
     * 
     * false = start
     * true = finished
     */
    private boolean status;

    /** current step number */
    private int step;

    /** list of screenshots */
    private ArrayList<BufferedImage> screenShots;

    /** list of coordinates */
    private ArrayList<Point> coordinates;

    /** dimension x */
    private int dimX;

    /** moving offset x */
    private int tmpX;

    /** moving offset y */
    private int tmpY;

    /** dimension y */
    private int dimY;

    /** used to capture screenshots */
    private Robot robot;

    /** current mouse point */
    private Point mousePoint;

    /** timestamp from the start of the session */
    private long timeStamp;

    /**
     * main entry point
     * 
     * @param args
     */
    public static void main(String[] args) {
        // Set global look and feel. 
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Unable to load native look and feel.\n");
        }

        // initialize instance
        EvaluationPreparer.getInstance().init();
    }

    /**
     * @return singleton instance
     */
    public static EvaluationPreparer getInstance() {
        if (main == null) {
            main = new EvaluationPreparer();
        }
        return main;
    }

    /**
     * initializes instance
     */
    @SuppressWarnings("boxing")
    private void init() {
        // check dll
        if (!this.checkDll()) System.exit(0);

        // initialize variables
        this.timeStamp = System.currentTimeMillis();
        this.path = new File(".").getAbsolutePath();
        this.status = false;
        this.step = 0;
        this.mousePoint = new Point();
        this.screenShots = new ArrayList<BufferedImage>();
        this.coordinates = new ArrayList<Point>();
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // add listeners
        this.buttonSelect.addActionListener(this);
        this.buttonStart.addActionListener(this);
        JIntellitype.getInstance().addHotKeyListener(this);

        // initialize gui elements
        this.spinnerAmount.setValue(2);
        this.spinnerDimensionX.setModel(new SpinnerNumberModel(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, 1));
        this.spinnerDimensionY.setModel(new SpinnerNumberModel(0, 0, Toolkit.getDefaultToolkit().getScreenSize().height, 1));
        this.textFieldPath.setText(this.path);

        // initialize file chooser
        this.chooser = new JFileChooser() {
            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            public void approveSelection() {
                if (getSelectedFile().isFile()) return;
                super.approveSelection();
                textFieldPath.setText(this.getSelectedFile().getAbsolutePath());
                path = this.getSelectedFile().getAbsolutePath();
            }
        };
        this.chooser.setMultiSelectionEnabled(false);
        this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        System.out.println("Initialize done.");
        System.out.println();

        // set visible
        this.setVisible(true);
    }

    /* (non-Javadoc)
     * @see com.melloware.jintellitype.HotkeyListener#onHotKey(int)
     */
    @Override
    public void onHotKey(int keyCode) {
        if (keyCode != 1) return;

        this.mousePoint.setLocation(MouseInfo.getPointerInfo().getLocation());

        if ((Integer.parseInt(this.spinnerDimensionX.getValue().toString()) != 0) && (Integer.parseInt(this.spinnerDimensionY.getValue().toString()) != 0)) {

            // calculate tmpX
            this.tmpX = 0;
            if (this.dimX / 2 + this.mousePoint.x > Toolkit.getDefaultToolkit().getScreenSize().width) {
                this.tmpX = Toolkit.getDefaultToolkit().getScreenSize().width - (this.dimX / 2 + this.mousePoint.x);
            } else if (this.mousePoint.x - this.dimX / 2 < 0) {
                this.tmpX = Math.abs(this.mousePoint.x - this.dimX / 2);
            }

            // calculate tmpY
            this.tmpY = 0;
            if (this.dimY / 2 + this.mousePoint.y > Toolkit.getDefaultToolkit().getScreenSize().height) {
                this.tmpY = Toolkit.getDefaultToolkit().getScreenSize().height - (this.dimY / 2 + this.mousePoint.y);
            } else if (this.mousePoint.y - this.dimY / 2 < 0) {
                this.tmpY = Math.abs(this.mousePoint.y - this.dimY / 2);
            }

            // capture screen
            Rectangle screenShotRect = new Rectangle(this.mousePoint.x - this.dimX / 2 + this.tmpX, this.mousePoint.y - this.dimY / 2 + this.tmpY, this.dimX, this.dimY);
            this.screenShots.add(this.robot.createScreenCapture(screenShotRect));

            // capture coordinates
            this.coordinates.add(new Point(this.mousePoint.x - (this.mousePoint.x - this.dimX / 2) - this.tmpX, this.mousePoint.y - (this.mousePoint.y - this.dimY / 2) - this.tmpY));
        } else {
            // capture screen
            Rectangle screenShotRect = new Rectangle(0, 0, this.dimX, this.dimY);
            this.screenShots.add(this.robot.createScreenCapture(screenShotRect));

            // capture coordinates
            this.coordinates.add(new Point(MouseInfo.getPointerInfo().getLocation()));
        }

        this.step++;
        System.out.println("screenshots captured: " + this.step);
        if (this.step == Integer.parseInt(this.spinnerAmount.getValue().toString())) {
            this.labelAmount.setEnabled(false);
            this.labelDimensionX.setEnabled(false);
            this.labelDimensionY.setEnabled(false);
            this.labelPath.setEnabled(false);
            this.spinnerAmount.setEnabled(false);
            this.spinnerDimensionX.setEnabled(false);
            this.spinnerDimensionY.setEnabled(false);
            this.buttonSelect.setEnabled(false);
            this.textFieldPath.setEnabled(false);
            this.buttonStart.setText("Exit");
            this.status = true;
            System.out.println();
            System.out.println("Capturing finished.");
            this.setVisible(true);
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.buttonSelect) {
            this.buttonSelectActionPerformed();
        } else if (event.getSource() == this.buttonStart) {
            this.buttonStartActionPerformed();
        }
    }

    /**
     * fired if button start is clicked
     * starts screenshot capturing
     */
    private void buttonStartActionPerformed() {
        if (!this.status) {
            // close window
            this.dispose();

            // set dimensions
            if (Integer.parseInt(this.spinnerDimensionX.getValue().toString()) == 0) {
                this.dimX = Toolkit.getDefaultToolkit().getScreenSize().width;
            } else {
                this.dimX = Integer.parseInt(this.spinnerDimensionX.getValue().toString());
            }
            if (Integer.parseInt(this.spinnerDimensionY.getValue().toString()) == 0) {
                this.dimY = Toolkit.getDefaultToolkit().getScreenSize().height;
            } else {
                this.dimY = Integer.parseInt(this.spinnerDimensionY.getValue().toString());
            }

            // register hotkey
            JIntellitype.getInstance().registerHotKey(1, "F7");
        } else {

            // deregister hotkey
            JIntellitype.getInstance().unregisterHotKey(1);

            // start closing
            this.exit();
        }
    }

    /**
     * fired if button select is clicked
     * opens file chooser
     */
    private void buttonSelectActionPerformed() {
        this.chooser.showOpenDialog(this);
    }

    /**
     * writes results and closes programm
     */
    private void exit() {
        // create directory
        new File(this.path + "/evaluation/prepared screenshots/Pack_" + this.timeStamp).mkdirs();

        // write xml
        try {
            // Create an XML stream writer
            FileOutputStream outputStream = new FileOutputStream(new File(this.path + "/evaluation/prepared screenshots/Pack_" + this.timeStamp + "/PreparedCoordinates.xml"));
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");

            // Write XML prologue
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\r\n");

            // set identifier
            writer.writeComment("Project Lightning (Desktop) - prepared coordinates");
            writer.writeCharacters("\r\n");

            // start with root element
            writer.writeStartElement("alldata");
            writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi:noNamespaceSchemaLocation", "PreparedCoordinatesPattern.xsd");
            writer.writeCharacters("\r\n");

            // write images
            for (int i = 0; i < this.screenShots.size(); i++) {
                // TODO: debugging
                Graphics2D graphic = this.screenShots.get(i).createGraphics();
                graphic.setFont(graphic.getFont().deriveFont(5));
                graphic.setColor(new Color(255, 0, 0, 255));
                graphic.drawOval(this.coordinates.get(i).x - 5, this.coordinates.get(i).y - 5, 10, 10);
                graphic.setColor(new Color(255, 0, 0, 32));
                graphic.fillOval(this.coordinates.get(i).x - 5, this.coordinates.get(i).y - 5, 10, 10);

                // write image
                try {
                    ImageIO.write(this.screenShots.get(i), "png", new File(this.path + "/evaluation/prepared screenshots/Pack_" + this.timeStamp + "/Image" + i + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // write coordinates
                writer.writeCharacters("\t");
                writer.writeStartElement("coordinate");
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t");
                writer.writeStartElement("number");
                writer.writeCharacters("" + i);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t");
                writer.writeStartElement("x");
                writer.writeCharacters("" + this.coordinates.get(i).x);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t");
                writer.writeStartElement("y");
                writer.writeCharacters("" + this.coordinates.get(i).y);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t");
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
            }

            // close alldata
            writer.writeEndElement();

            // Close the writer to flush the output
            outputStream.close();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // unzip xsd
        $(EvaluationPreparer.class.getResourceAsStream("resources/PreparedCoordinatesPattern.zip")).zipstream().unzip(new File(this.path + "/evaluation/prepared screenshots/Pack_" + this.timeStamp).getAbsolutePath() + "/");

        // close
        System.out.println();
        System.out.println("All data are written to " + new File(this.path + "/evaluation/prepared screenshots/Pack_" + this.timeStamp).getAbsolutePath() + ". Session closed.");
        System.exit(0);
    }

    /**
     * checks JIntellitype.dll
     *
     * @return true if it is there or the copy was successful
     */
    private boolean checkDll() {

        // TODO: test these for XP, Win7 32Bit, ... works fine for Win7 64Bit
        File destination = new File("JIntellitype.dll");

        // check if it is already the
        if (!destination.exists()) {

            // try to unzip it to "."
            $(MainClass.class.getResourceAsStream("resources/JIntellitype.zip")).zipstream().unzip(".");

            if (!destination.exists()) {
                // Display an error message
                System.out.println("Initializing failed. The DLL 'JIntellitype.dll' could not be copied to " + new File(".").getAbsolutePath() + ".");

                // return not successful
                return false;
            }
        }

        // return successful
        return true;
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosed(WindowEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent arg0) {
        this.exit();
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    @Override
    public void windowOpened(WindowEvent arg0) {
    }
}
