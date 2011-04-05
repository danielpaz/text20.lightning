/*
 * ReminderWindowImpl.java
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
package de.dfki.km.text20.lightning.worker.submitReminder.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import de.dfki.km.text20.lightning.worker.submitReminder.SubmitReminder;

/**
 * @author Christoph Käding
 *
 */
public class ReminderWindowImpl extends ReminderWindow implements ActionListener,
        WindowListener {

    /** current used reminder */
    private SubmitReminder reminder;

    /**
     * creates a new reminder window, initializes its variables and shows it
     * 
     * @param reminder
     */
    public ReminderWindowImpl(SubmitReminder reminder) {
        // initialize variables
        this.reminder = reminder;
        
        // add action listeners
        this.buttonYes.addActionListener(this);
        this.buttonNo.addActionListener(this);
        this.mainFrame.addWindowListener(this);
        
        // set label text
        this.labelText.setText(this.reminder.getText());
        
        // show the window
        this.mainFrame.setVisible(true);
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
        this.reminder.cancelSurvey();
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

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        // if yes is clicked, start the survey
        if (event.getSource() == this.buttonYes) this.reminder.startSurvey();

        // if no is clicked, cancel it
        if (event.getSource() == this.buttonNo) this.reminder.cancelSurvey();
        
        // close the window
        this.mainFrame.dispose();
    }

}
