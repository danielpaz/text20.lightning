/*
 * ScreenshotViewer.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.precision.screenshotviewer;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import de.dfki.km.text20.lightning.components.evaluationmode.precision.screenshotviewer.gui.ScreenshotViewerGUI;
import de.dfki.km.text20.lightning.components.evaluationmode.precision.screenshotviewer.worker.DataXMLParser;

/**
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class ScreenshotViewer extends ScreenshotViewerGUI implements ActionListener {

    /** */
    private static ScreenshotViewer main;
    
    /**
     * main entry point
     * 
     * @param args
     */
    public static void main(String[] args) {
        //JCoresScript.SCRIPT("Viewer", args).pack();
        
        // Set global look and feel. 
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Unable to load native look and feel.\n");
        }

        // initialize instance
        ScreenshotViewer.getInstance().init();

        // indicate start
        System.out.println("initialize done.");
    }

    /**
     * @return singleton instance
     */
    public static ScreenshotViewer getInstance() {
        if (main == null) {
            main = new ScreenshotViewer();
        }
        return main;
    }

    /**
     * initializes instance
     */
    private void init() {
        // initialize
        this.buttonSelect.addActionListener(this);
        this.labelDescription.setText("");

        // set fullscreen
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        
        // show
        this.setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if(event.getSource() != this.buttonSelect) return;
        
        // open chooser
        JFileChooser chooser = initChooser();
        chooser.showOpenDialog(null); 
    }

    /**
     * initializes filechooser
     * 
     * @return
     */
    private JFileChooser initChooser() {
        JFileChooser fileChooser = new JFileChooser() {
            
            // react on selection
            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            public void approveSelection() {
                super.approveSelection();

                // get file
                if(new DataXMLParser().isValid(this.getSelectedFile()))
                       drawImage(this.getSelectedFile());
                else labelDescription.setText(this.getSelectedFile().getName() + " not valid!");
            }
        };

        // set behavior of this chooser
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(new FileFilter() {

            // filter string, only files with this extension and directories
            // will be shown
            private String extension = ".xml";

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

        // return the created chooser
        return fileChooser;
    }

    /**
     * visualizes image
     * 
     * @param xmlFile
     */
    private void drawImage(File xmlFile){
        // initialize variables
        JLabel paintLabel;
        DataXMLParser dataParser = new DataXMLParser();
        
        // create graphics
        BufferedImage notificationImage = dataParser.getRelatedImage(xmlFile);
        Graphics2D graphics = notificationImage.createGraphics();

        // paint marks
        for (Rectangle mark : dataParser.readFile(xmlFile)) {
            graphics.setColor(new Color(0, 0, 255, 255));
            graphics.drawRect(mark.x, mark.y, mark.width, mark.height);
            graphics.setColor(new Color(0, 0, 255, 64));
            graphics.fillRect(mark.x, mark.y, mark.width, mark.height);
        }

        // update image
        this.panelContent.removeAll();
        paintLabel = new JLabel(new ImageIcon(notificationImage));
        paintLabel.setSize(notificationImage.getWidth(), notificationImage.getHeight());
        this.panelContent.add(paintLabel);
        this.panelContent.repaint();

        // update label
        this.labelDescription.setText(xmlFile.getName() + " visualized. Amount of marks: " + dataParser.count(xmlFile));
        System.out.println(xmlFile.getName() + " visualized.");
    }
}
