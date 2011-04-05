package de.dfki.km.text20.lightning.worker.submitReminder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Timer;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;
import de.dfki.km.text20.lightning.worker.submitReminder.gui.ReminderWindowImpl;

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
        this.upTimeThresh = 0;
        this.useCountThres = 0;
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
        if ((this.properties.getUseCount() > this.useCountThres) && !this.properties.isFirstSubmitted())
            this.showReminder();
    }

    /**
     * initializes and starts the reminder
     */
    public void init() {
        // initialize variables
        this.timestamp = System.currentTimeMillis();

        // check for 1st use
        if (!this.properties.isFirstSubmitted()) this.timer.start();
        else

        // check execution duration
        if (!this.properties.isSecondSubmitted() && ((this.properties.getUpTime() > this.upTimeThresh) || this.properties.getUseCount() > this.useCountThres))
            this.timer.start();
    }

    /**
     * indicates that the tool is closed, updates properties
     */
    public void close() {
        double time = (double)(System.currentTimeMillis() - this.timestamp) / (1000 * 60 * 60);
        this.properties.addToUpTime(time);
    }

    /**
     * initiates the reminder window
     */
    @SuppressWarnings("unused")
    private void showReminder() {
        new ReminderWindowImpl(this);
    }

    /**
     * starts the survey and uploads the data
     */
    public void startSurvey() {
        try {
            // for the first survey
            if (!this.properties.isFirstSubmitted()) {
                // TODO: add useful url
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://www.google.de");
                this.properties.setFirstSubmitted(true);

                // for the second survey
            } else {
                // TODO: add useful url
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://www.google.de");
                MainClass.getInstance().publishStatistics();
                this.properties.setSecondSubmitted(true);
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
