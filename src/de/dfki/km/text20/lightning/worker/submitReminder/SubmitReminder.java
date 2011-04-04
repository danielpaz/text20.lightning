package de.dfki.km.text20.lightning.worker.submitReminder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;

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

	public void addUse() {
		this.properties.incrementUseCount();
		if ((this.properties.getUseCount() > this.useCountThres)
				&& !this.properties.isSubmitted())
			this.showReminder();
	}

	/**
	 * starts the timer
	 */
	public void init() {
		// initialize variables
		this.timestamp = System.currentTimeMillis();

		// check execution duration
		if (!this.properties.isSubmitted()
				&& ((this.properties.getUpTime() > this.upTimeThresh) || this.properties
						.getUseCount() > this.useCountThres))
			this.timer.start();

		// check for 1st use
		if ((this.properties.getUpTime() == 0)
				&& (this.properties.getUseCount() == 0))
			this.timer.start();
	}

	/**
	 * indicates that the tool is closed, updates properties
	 */
	public void close() {
		double time = (System.currentTimeMillis() - this.timestamp)
				/ (1000 * 60 * 60);
		this.properties.addToUpTime(time);
	}

	private void showReminder() {
		System.out.println("remind");
		MainClass.getInstance().showTrayMessage("remindermsg");
	}
}
