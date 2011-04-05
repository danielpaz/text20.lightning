/*
 * PrecisionEvaluator.java
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
 *
 */
package de.dfki.km.text20.lightning.worker.evaluationmode;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;

/**
 * The precision evaluator is used in evaluation mode. Here the collected data is
 * handled.
 * 
 * @author Christoph Käding
 * 
 */
public class PrecisionEvaluator {

	/** last fixation point */
	private Point fixationTmp;

	/** stored fixation point */
	private Point fixation;

	/** associated mouse position which shows the real target */
	private Point mousePosition;

	/** offset between fixation point and mouse position */
	private Point mousePoint;

	/** robot for creating screenshots */
	private Robot robot;

	/** actual time */
	private long timestamp;

	/** screenshot of the target area */
	private BufferedImage screenShot;

	/** a list of all catched data */
	private ArrayList<StorageContainer> allData;

	/** global used properties */
	private Properties properties;

	/** name of current registered user */
	private String user;

	/** indicates if the mouseposition was anytime out of dimension */
	private boolean warning;

	/**
	 * size of the pupils 0 = left 1 = right
	 */
	private float[] pupils;

	/**
	 * temporary size of the pupils 0 = left 1 = right
	 */
	private float[] pupilsTmp;

	/** indicates if already any processing is in progress */
	private boolean isProcessing;

	/**
	 * creates the precision evaluator
	 */
	public PrecisionEvaluator() {

		// initialize variables
		this.fixation = new Point();
		this.fixationTmp = new Point();
		this.mousePoint = new Point();
		this.allData = new ArrayList<StorageContainer>();
		this.properties = MainClass.getInstance().getProperties();
		this.warning = false;
		this.pupils = new float[2];
		this.isProcessing = false;

		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			MainClass.getInstance().exit();
		}
	}

	/**
	 * stores fixation and pupil sizes
	 * 
	 * @param fixation
	 * @param pupils
	 *            0 = left 1 = right
	 */
	public void setFixationPoint(Point fixation, float[] pupils) {
		this.fixationTmp = fixation;
		this.pupilsTmp = pupils;
	}

	/**
	 * stores last fixation so that it can be used to create a evaluation step
	 * 
	 * @return true if successful 
	 */
	public boolean storeFixation() {
		if (this.fixationTmp == null || this.isProcessing)
			return false;
		this.isProcessing = true;
		this.timestamp = System.currentTimeMillis();
		this.fixation = new Point(this.fixationTmp);
		this.pupils = new float[2];
		this.pupils[0] = this.pupilsTmp[0];
		this.pupils[1] = this.pupilsTmp[1];
		this.fixationTmp = null;
		this.isProcessing = false;
		return true;
	}

	/**
	 * sets the mouseposition to associate it with the stored fixation
	 * 
	 * @param mousePosition
	 * @return true if the position is valid
	 */
	@SuppressWarnings("boxing")
	public boolean setMousePosition(Point mousePosition) {
		if (this.fixation == null || this.isProcessing)
			return false;
		this.isProcessing = true;
		this.user = MainClass.getInstance().getEvaluationSettings()[0];
		this.mousePosition = mousePosition;
		Rectangle screenShotRect = new Rectangle(
				-this.properties.getDimension() / 2,
				-this.properties.getDimension() / 2, Toolkit
						.getDefaultToolkit().getScreenSize().width
						+ this.properties.getDimension(), Toolkit
						.getDefaultToolkit().getScreenSize().height
						+ this.properties.getDimension());
		this.screenShot = this.robot.createScreenCapture(screenShotRect);

		// calculate offset
		this.mousePoint.setLocation(this.mousePosition.x - this.fixation.x
				+ this.properties.getDimension() / 2, this.mousePosition.y
				- this.fixation.y + this.properties.getDimension() / 2);

		// collect data
		this.allData.add(new StorageContainer(new Long(this.timestamp),
				new Point(this.fixation), new Point(this.mousePosition),
				this.pupils));

		// write image
		try {
			File outputfile = new File("./evaluation/data/" + this.user + "/"
					+ this.user + "_" + this.timestamp + ".png");
			outputfile.mkdirs();
			ImageIO.write(this.screenShot, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// update logfile
		String logString = new String("Evaluation - Timestamp: " + this.timestamp
				+ ", Fixation: (" + this.fixation.x + "," + this.fixation.y
				+ "), Mouseposition: (" + this.mousePosition.x + ","
				+ this.mousePosition.y + "), Dimension: "
				+ this.properties.getDimension());
		System.out.println(logString);
		MainClass.getInstance().getChannel().status(logString);
		MainClass.getInstance().addToStatistic(logString);

		// indicate error
		if ((this.mousePoint.x > this.properties.getDimension())
				|| (this.mousePoint.y > this.properties.getDimension())) {
			this.warning = true;

			// reset status
			this.isProcessing = false;

			// return failure
			return false;
		}

		// reset status
		this.isProcessing = false;

		// return success
		return true;
	}

	/**
	 * called when evaluation ends
	 * writes evaluation data into a file
	 */
	public void leaveEvaluation() {
		// only write file if there is some data
		if (this.allData.size() == 0)
			return;

		// create file
		File logfile = new File("./evaluation/data/" + this.user + "/"
				+ this.user + "_" + System.currentTimeMillis() + ".xml");

		try {
			// Create an XML stream writer
			FileOutputStream outputStream = new FileOutputStream(logfile);
			XMLStreamWriter writer = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(outputStream, "UTF-8");

			// Write XML prologue
			writer.writeStartDocument();
			writer.writeCharacters("\r\n");

			// TODO: add a dtd, encodingtag, namespace, ...

			// set identifier
			writer.writeComment("Project Lightning (Desktop) - evaluation data");
			writer.writeCharacters("\r\n");

			// start with root element
			writer.writeStartElement("alldata");
			writer.writeCharacters("\r\n");

			// write screen brightness
			writer.writeCharacters("\t");
			writer.writeStartElement("screenBrightness");
			writer.writeCharacters(""
					+ MainClass.getInstance().getEvaluationSettings()[1]);
			writer.writeEndElement();
			writer.writeCharacters("\r\n");

			// write setting brightness
			writer.writeCharacters("\t");
			writer.writeStartElement("settingBrightness");
			writer.writeCharacters(""
					+ MainClass.getInstance().getEvaluationSettings()[2]);
			writer.writeEndElement();
			writer.writeCharacters("\r\n");

			// write dimension
			writer.writeCharacters("\t");
			writer.writeStartElement("dimension");
			writer.writeCharacters("" + this.properties.getDimension());
			writer.writeEndElement();
			writer.writeCharacters("\r\n");

			// if mouseposition was anytime out of dimension
			if (this.warning) {
				writer.writeCharacters("\t");
				writer.writeComment("mouseposition was anytime out of dimension");
				writer.writeCharacters("\r\n");
				this.warning = false;
			}

			// run through all data
			for (StorageContainer data : this.allData) {

				// write datatag
				writer.writeCharacters("\t");
				writer.writeStartElement("step");
				writer.writeCharacters("\r\n");

				// write timestamp
				writer.writeCharacters("\t\t");
				writer.writeStartElement("timestamp");
				writer.writeCharacters("" + data.getTimestamp());
				writer.writeEndElement();
				writer.writeCharacters("\r\n");

				// write fixation
				writer.writeCharacters("\t\t");
				writer.writeStartElement("fixation");
				writer.writeCharacters("\r\n");
				writer.writeCharacters("\t\t\t");
				writer.writeStartElement("x");
				writer.writeCharacters("" + data.getFixation().x);
				writer.writeEndElement();
				writer.writeCharacters("\r\n");
				writer.writeCharacters("\t\t\t");
				writer.writeStartElement("y");
				writer.writeCharacters("" + data.getFixation().y);
				writer.writeEndElement();
				writer.writeCharacters("\r\n");
				writer.writeCharacters("\t\t");
				writer.writeEndElement();
				writer.writeCharacters("\r\n");

				// write mouse position
				writer.writeCharacters("\t\t");
				writer.writeStartElement("mouseposition");
				writer.writeCharacters("\r\n");
				writer.writeCharacters("\t\t\t");
				writer.writeStartElement("x");
				writer.writeCharacters("" + data.getMousePoint().x);
				writer.writeEndElement();
				writer.writeCharacters("\r\n");
				writer.writeCharacters("\t\t\t");
				writer.writeStartElement("y");
				writer.writeCharacters("" + data.getMousePoint().y);
				writer.writeEndElement();
				writer.writeCharacters("\r\n");
				writer.writeCharacters("\t\t");
				writer.writeEndElement();
				writer.writeCharacters("\r\n");

				// write pupil size
				writer.writeCharacters("\t\t");
				writer.writeStartElement("pupils");
				writer.writeCharacters("\r\n");
				writer.writeCharacters("\t\t\t");
				writer.writeStartElement("left");
				writer.writeCharacters("" + data.getPupils()[0]);
				writer.writeEndElement();
				writer.writeCharacters("\r\n");
				writer.writeCharacters("\t\t\t");
				writer.writeStartElement("right");
				writer.writeCharacters("" + data.getPupils()[1]);
				writer.writeEndElement();
				writer.writeCharacters("\r\n");
				writer.writeCharacters("\t\t");
				writer.writeEndElement();
				writer.writeCharacters("\r\n");

				// close datatag
				writer.writeCharacters("\t");
				writer.writeEndElement();
				writer.writeCharacters("\r\n");
			}

			// Write document end. This closes all open structures
			writer.writeEndDocument();

			// Close the writer to flush the output
			outputStream.close();
			writer.close();

		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// try to write file
		/*
		 * try { ObjectOutputStream outputStream = new ObjectOutputStream(new
		 * FileOutputStream(logfile)); for (StorageContainer temp :
		 * this.allData) { outputStream.writeObject(temp); }
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 */

		// reset data
		this.allData.clear();
	}
}
