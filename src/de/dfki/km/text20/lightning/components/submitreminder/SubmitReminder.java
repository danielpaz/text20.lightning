package de.dfki.km.text20.lightning.components.submitreminder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;

/**
 * This class manages reminder for the surveys and the upload of the statistics.
 * 
 * @author Christoph Käding
 * 
 */
public class SubmitReminder {

    /** timer which clocks the mouse position tracking */
    private Timer timer;

    /** timestamp from the programmstart */
    private long timestamp;

    /** global used properties */
    private Properties properties;

    /** threshold for reminder */
    private int useCountThres;

    /** threshold for reminder */
    private double upTimeThresh;

    /** creates new reminder object and initializes variables */
    public SubmitReminder() {
        // initialize variables
        // TODO: set useful values
        this.upTimeThresh = 10;
        this.useCountThres = 100;
        this.timestamp = System.currentTimeMillis();
        this.properties = MainClass.getInstance().getProperties();
        this.timer = new Timer((2 * 60 * 1000), new ActionListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void actionPerformed(ActionEvent arg0) {
                showReminder();
                timer.stop();
            }
        });
    }

    /**
     * increases the number of hotkey uses in properties,
     * also checks if the threshold is reached
     */
    public void addUse() {
        this.properties.incrementUseCount();
        if ((this.properties.getUseCount() > this.useCountThres) && this.properties.isFirstSubmitted() && !this.properties.isSecondSubmitted())
            this.showReminder();
    }

    /**
     * initializes and starts the reminder
     */
    public void init() {
        // check for 1st use
        if (!this.properties.isFirstSubmitted()) {
            this.timer.start();
            System.out.println("reminder started...");
            System.out.println();
        } else

        // check execution duration and uses
        if (this.properties.isFirstSubmitted() && !this.properties.isSecondSubmitted() && ((this.properties.getUpTime() > this.upTimeThresh) || this.properties.getUseCount() > this.useCountThres)) {
            this.timer.start();
            System.out.println("reminder started...");
            System.out.println();
        }
    }

    /**
     * indicates that the tool is closed, updates properties
     */
    public void close() {
        double time = (double) (System.currentTimeMillis() - this.timestamp) / (1000 * 60 * 60);
        this.properties.addToUpTime(time);
    }

    /**
     * initiates the reminder window and reacts depending on what part should be shown 
     */
    private void showReminder() {
        // initialize variables
        final Object[] options = { "Yes", "No, thanks" };
        int choice = -1;
        try {
            // first survey part
            if (!this.properties.isFirstSubmitted()) {

                // show dialog
                choice = JOptionPane.showOptionDialog(null, "Sorry to interrupt you and we only ask once. Would you like to participate " + "in a usability survey about this tool?", "Text 2.0 Lightning Survey", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                // evaluate answer, if yes...  
                if (choice == 0) {
                    //  ... skip first part and ...
                    this.properties.setFirstSubmitted(true);

                    // ... open browser
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://www.google.de");
                }

                // if no ...
                else if (choice == 1) {

                    // ... cancel second part and ...
                    this.properties.setSecondSubmitted(true);

                    // ... first part
                    this.properties.setFirstSubmitted(true);
                }

                // second survey part
            } else {
                // show dialog
                choice = JOptionPane.showOptionDialog(null, "Sorry to interrupt you a second time. Would you like to participate " + "in the second part of the survey about this tool?", "Text 2.0 Lightning Survey", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                // evaluate answer, if yes ... 
                if (choice == 0) {

                    // ... skip second part, ...
                    this.properties.setSecondSubmitted(true);

                    // ... open url and ...
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://www.google.de");

                    // ...  publish statistics
                    MainClass.getInstance().publishStatistics();
                }

                // if no ...
                else if (choice == 1) {
                    
                    // ... skip second part
                    this.properties.setSecondSubmitted(true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            MainClass.getInstance().showTrayMessage("-- ERROR -- an error occures while opening the browser. Please Try again later.");
        }
    }

    /**
     * cancels the survey
     */
    public void cancelSurvey() {
        // cancel first survey
        if (!this.properties.isFirstSubmitted()) this.properties.setFirstSubmitted(true);

        // cancel second survey
        else
            this.properties.setSecondSubmitted(true);
    }

    /**
     * provides the text for the gui
     * 
     * @return the text
     */
    public String getText() {
        // for the first survey
        if (!this.properties.isFirstSubmitted()) return "blubbblubbblubb";

        // for the second survey
        return "blablabla";
    }
}
