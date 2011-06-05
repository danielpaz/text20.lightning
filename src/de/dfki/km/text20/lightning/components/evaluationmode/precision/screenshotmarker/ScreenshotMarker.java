/*
 * ScreenshotMarker.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.precision.screenshotmarker;

import static net.jcores.CoreKeeper.$;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import de.dfki.km.text20.lightning.components.evaluationmode.precision.screenshotmarker.gui.MarkerWindow;

/**
 * Utility to mark point in a screenshot and store its coordinates to a xml file.
 * 
 * FIXME: something strange with scrollbars 
 *  - if the image is smaller than the screen, there is an offset (seems to be the distance between scrollpane and image border)
 *  - horizontal scrollbar doesn't resize if window is not big enough
 * 
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class ScreenshotMarker extends MarkerWindow implements MouseListener,
        ActionListener, WindowListener {

    /** singleton instance */
    static private ScreenshotMarker main;

    /** chooser for screenshot files */
    private JFileChooser chooser;

    /** current chosen file */
    private File screenshotFile;

    /** label to draw the screenshots into it */
    private JLabel paintLabel;

    /** the current image */
    private BufferedImage image;

    /** list of marked rectangles */
    private ArrayList<Rectangle> markedRectangles;

    /** */
    private Point tmp;

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
        ScreenshotMarker.getInstance().init();

        // indicate start
        System.out.println("initialize done.");
    }

    /**
     * @return singleton instance
     */
    public static ScreenshotMarker getInstance() {
        if (main == null) {
            main = new ScreenshotMarker();
        }
        return main;
    }

    /**
     * initializes instance
     */
    private void init() {
        // deactivate gui elements
        this.buttonSave.setEnabled(false);
        this.buttonRemove.setEnabled(false);
        this.comboBoxType.setEnabled(false);
        this.labelDescription.setText("");

        // init combobox
        this.comboBoxType.addItem("Text");
        this.comboBoxType.addItem("Code");
        this.comboBoxType.addItem("Icons");

        // initialize variables
        this.markedRectangles = new ArrayList<Rectangle>();

        // add listener
        this.buttonSave.addActionListener(this);
        this.buttonSelect.addActionListener(this);
        this.buttonRemove.addActionListener(this);
        this.panelContent.addMouseListener(this);
        this.addWindowListener(this);

        // initialize file chooser
        this.chooser = new JFileChooser() {
            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            public void approveSelection() {
                super.approveSelection();
                try {
                    // set file
                    screenshotFile = this.getSelectedFile();
                    image = ImageIO.read(screenshotFile);

                    // show image
                    panelContent.removeAll();
                    paintLabel = new JLabel(new ImageIcon(image));
                    paintLabel.setSize(image.getWidth(), image.getHeight());
                    panelContent.add(paintLabel);
                    panelContent.repaint();

                    // indicate success
                    System.out.println();
                    System.out.println("Image '" + screenshotFile.getAbsolutePath() + "' loaded.");
                    System.out.println();
                    labelDescription.setText("Image '" + screenshotFile.getAbsolutePath() + "' loaded.");

                } catch (IOException e) {
                    e.printStackTrace();
                    labelDescription.setText(e.toString());
                }
            }
        };

        // set behavior of this chooser
        this.chooser.setMultiSelectionEnabled(false);
        this.chooser.setAcceptAllFileFilterUsed(false);
        this.chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.chooser.setFileFilter(new FileFilter() {

            // filter string, only files with this extension and directories will be shown
            private String extension = ".png";

            @Override
            public String getDescription() {
                return this.extension;
            }

            // set filter
            @Override
            public boolean accept(File file) {
                if (file == null) return false;

                if (file.isDirectory()) return true;

                return file.getName().toLowerCase().endsWith(this.extension);
            }
        });

        // set fullscreen
        this.setExtendedState(Frame.MAXIMIZED_BOTH);

        // set visible
        this.setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.buttonSave) {
            this.buttonSaveActionPerformed();
        } else if (event.getSource() == this.buttonSelect) {
            this.buttonSelectActionPerformed();
        } else if (event.getSource() == this.buttonRemove) {
            this.buttonRemoveActionPerformed();
        }
    }

    private void buttonSelectActionPerformed() {
        this.chooser.showOpenDialog(this);
        this.markedRectangles.clear();
    }

    private void buttonSaveActionPerformed() {
        // deactivate button
        this.buttonRemove.setEnabled(false);
        this.buttonSave.setEnabled(false);
        this.comboBoxType.setEnabled(false);

        // create file
        String name = this.screenshotFile.getName().substring(0, this.screenshotFile.getName().toLowerCase().lastIndexOf(".png"));
        File logfile = new File(this.screenshotFile.getAbsolutePath().substring(0, this.screenshotFile.getAbsolutePath().lastIndexOf(File.separator) + 1) + "Marks_" + this.comboBoxType.getSelectedItem() + "_" + name + "_" + System.currentTimeMillis() + ".xml");
        File xsd = new File(this.screenshotFile.getAbsolutePath().substring(0, this.screenshotFile.getAbsolutePath().lastIndexOf(File.separator) + 1) + "MarksPattern.xsd");

        try {
            // Create an XML stream writer
            FileOutputStream outputStream = new FileOutputStream(logfile);
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");

            // Write XML prologue
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\r\n");

            // set identifier
            writer.writeComment("Project Lightning (Desktop) - marks data");
            writer.writeCharacters("\r\n");

            // start with root element
            writer.writeStartElement("alldata");
            writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi:noNamespaceSchemaLocation", "DataPattern.xsd");
            writer.writeCharacters("\r\n");

            // write filename
            writer.writeCharacters("\t");
            writer.writeStartElement("file");
            writer.writeCharacters("" + this.screenshotFile.getName());
            writer.writeEndElement();
            writer.writeCharacters("\r\n");

            // write count
            writer.writeCharacters("\t");
            writer.writeStartElement("amount");
            writer.writeCharacters("" + this.markedRectangles.size());
            writer.writeEndElement();
            writer.writeCharacters("\r\n");

            // write marks
            writer.writeCharacters("\t");
            writer.writeStartElement("marks");
            writer.writeCharacters("\r\n");

            // run through all data
            for (Rectangle mark : this.markedRectangles) {

                // open mark
                writer.writeCharacters("\t\t");
                writer.writeStartElement("rectangle");
                writer.writeCharacters("\r\n");

                writer.writeCharacters("\t\t\t");
                writer.writeStartElement("x");
                writer.writeCharacters("" + mark.x);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t\t");
                writer.writeStartElement("y");
                writer.writeCharacters("" + mark.y);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t\t");
                writer.writeStartElement("width");
                writer.writeCharacters("" + mark.width);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t\t");
                writer.writeStartElement("height");
                writer.writeCharacters("" + mark.height);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");

                // close mark
                writer.writeCharacters("\t\t");
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
            }

            // Write document end. This closes all open structures
            writer.writeEndDocument();

            // Close the writer to flush the output
            outputStream.close();
            writer.close();

            // unzip xsd if not already there
            if (!xsd.exists())
                $(ScreenshotMarker.class.getResourceAsStream("resources/MarksPattern.zip")).zipstream().unzip(xsd.getAbsolutePath().substring(0, xsd.getAbsolutePath().lastIndexOf(File.separator) + 1));

            //indicate success
            System.out.println();
            System.out.println("Data stored to '" + logfile.getAbsolutePath() + "'.");
            this.labelDescription.setText("Data stored to '" + logfile.getAbsolutePath() + "'.");
            
            // clear marks
            this.markedRectangles.clear();

        } catch (Exception e) {
            e.printStackTrace();
            this.labelDescription.setText(e.toString());
        }
    }

    private void buttonRemoveActionPerformed() {
        // remove last point
        this.markedRectangles.remove(this.markedRectangles.size() - 1);

        // disable elements if needed
        if (this.markedRectangles.size() == 0) {
            this.buttonRemove.setEnabled(false);
            this.buttonSave.setEnabled(false);
            this.comboBoxType.setEnabled(false);
        }

        // update image
        this.updateImage();
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent event) {
        // add mousepoint to map
        this.tmp = new Point(event.getXOnScreen(), event.getYOnScreen());
        SwingUtilities.convertPointFromScreen(this.tmp, this.paintLabel);
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent event) {
        // check tmp
        if (this.tmp == null) return;

        // enable gui elements
        this.buttonRemove.setEnabled(true);
        this.buttonSave.setEnabled(true);
        this.comboBoxType.setEnabled(true);

        // add mousepoint to map
        Point mousePoint = new Point(event.getXOnScreen(), event.getYOnScreen());
        SwingUtilities.convertPointFromScreen(mousePoint, this.paintLabel);

        // create variables
        int x;
        int y;
        int width;
        int height;

        // set horizontal
        if (this.tmp.x < mousePoint.x) {
            x = this.tmp.x;
            width = mousePoint.x - this.tmp.x;
        } else {
            x = mousePoint.x;
            width = this.tmp.x - mousePoint.x;
        }

        // set vertical
        if (this.tmp.y < mousePoint.y) {
            y = this.tmp.y;
            height = mousePoint.y - this.tmp.y;
        } else {
            y = mousePoint.y;
            height = this.tmp.y - mousePoint.y;
        }

        this.markedRectangles.add(new Rectangle(x, y, width, height));

        // reset tmp
        this.tmp = null;

        // update image
        this.updateImage();
    }

    private void updateImage() {
        // create graphics
        BufferedImage notificationImage = new BufferedImage(this.image.getWidth(), this.image.getHeight(), this.image.getType());
        notificationImage.setData(this.image.getData());
        Graphics2D graphics = notificationImage.createGraphics();

        // paint marks
        for (Rectangle mark : this.markedRectangles) {
            graphics.setColor(new Color(0, 0, 255, 255));
            graphics.drawRect(mark.x, mark.y, mark.width, mark.height);
            graphics.setColor(new Color(0, 0, 255, 64));
            graphics.fillRect(mark.x, mark.y, mark.width, mark.height);
        }

        // update image
        this.panelContent.removeAll();
        this.paintLabel = new JLabel(new ImageIcon(notificationImage));
        this.paintLabel.setSize(notificationImage.getWidth(), notificationImage.getHeight());
        this.panelContent.add(this.paintLabel);
        this.panelContent.repaint();

        // update label
        this.labelDescription.setText("marks: " + this.markedRectangles.size());
        System.out.println("marks: " + this.markedRectangles.size());
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent event) {
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
        // indicate close
        System.out.println();
        System.out.println("Closed.");

        // exit
        System.exit(0);
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
