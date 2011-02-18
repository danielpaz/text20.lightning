package de.dfki.km.click2sight.worker;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.color.ColorSpace;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import de.dfki.km.click2sight.MainClass;
import de.dfki.km.click2sight.plugins.MethodManager;
import de.dfki.km.click2sight.tools.Tools;

public class FixationEvaluator {

    /** Point of fixation which is provided by the eyetracker */
    private Point fixation;
    /** previous mouse position */
    private Point location;
    /** calculated offset from fixation point to processed target */
    private Point offset;
    /** */
    private Robot robot;
    /** */
    private ColorConvertOp colorConverterGray;
    /** */
    private ColorConvertOp colorConverterRGB;
    /** */
    private long timestamp;
    /** */
    private BufferedImage screenShot;
    /** */
    private BufferedImage derivatedScreenShot;
    /** */
    private Graphics2D graphic;
    /** */
    private MethodManager methodManager;

    /** */
    public FixationEvaluator(MethodManager manager) {
        this.methodManager = manager;
        this.colorConverterGray = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        this.colorConverterRGB = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null);
        this.fixation = new Point();
        this.location = new Point();
        this.offset = new Point();

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            MainClass.exit();
        }
    }

    /**
     * sets the fixation point which is provided by the eyetracker
     */
    public void setFixationPoint(Point fixation) {
        this.fixation = fixation;
    }

    /**
     * makes a screenshot with choosed dimension around the fixation point an starts the processing of it. At the end a mouseclick were taken to the calculated point.
     */
    public void evaluateLocation() {
        this.location = MouseInfo.getPointerInfo().getLocation();

        Rectangle screenShotRect = new Rectangle(this.fixation.x - MainClass.getDimension() / 2, this.fixation.y - MainClass.getDimension() / 2, MainClass.getDimension(), MainClass.getDimension());
        this.screenShot = this.robot.createScreenCapture(screenShotRect);
        this.screenShot = this.colorConverterGray.filter(this.screenShot, null);

        this.timestamp = System.currentTimeMillis();

        this.derivatedScreenShot = this.methodManager.getCurrentPositionFinder().derivate(this.screenShot);
        this.offset = this.methodManager.getCurrentPositionFinder().analyse(this.derivatedScreenShot);

        if (MainClass.showImages()) {
            this.drawImages();
        }

        String logString = "Normal - Timestamp: " + this.timestamp + ", Fixation: (" + this.fixation.x + "," + this.fixation.y + "), Offset: (" + this.offset.x + "," + this.offset.y + "), Dimension: " + MainClass.getDimension() + System.getProperty("line.separator");
        Tools.updateLogFile(logString);
        System.out.println("click to: (" + (this.fixation.x + this.offset.x) + "," + (this.fixation.y + this.offset.y) + ")");

        this.robot.mouseMove(this.fixation.x + this.offset.x, this.fixation.y + this.offset.y);
        this.robot.mousePress(InputEvent.BUTTON1_MASK);
        this.robot.mouseRelease(InputEvent.BUTTON1_MASK);
        this.robot.mouseMove(this.location.x, this.location.y);
    }

    /**
     * draws the images of the screenshot and the derivated screenshot to the outputdirectory
     */
    private void drawImages() {
        this.screenShot = this.colorConverterRGB.filter(this.screenShot, null);
        this.graphic = this.screenShot.createGraphics();
        this.graphic.setColor(new Color(0, 255, 255, 255));
        this.graphic.drawOval(MainClass.getDimension() / 2 - 5, MainClass.getDimension() / 2 - 5, 10, 10);
        this.graphic.setColor(new Color(0, 255, 255, 64));
        this.graphic.fillOval(MainClass.getDimension() / 2 - 5, MainClass.getDimension() / 2 - 5, 10, 10);
        Tools.writeImage(this.screenShot, this.timestamp + "_original.png");

        this.derivatedScreenShot = this.colorConverterRGB.filter(this.derivatedScreenShot, null);
        this.graphic = this.derivatedScreenShot.createGraphics();
        this.graphic.setColor(new Color(255, 255, 0, 255));
        this.graphic.drawOval(MainClass.getDimension() / 2 + this.offset.x - 5, MainClass.getDimension() / 2 + this.offset.y - 5, 10, 10);
        this.graphic.setColor(new Color(255, 255, 0, 64));
        this.graphic.fillOval(MainClass.getDimension() / 2 + this.offset.x - 5, MainClass.getDimension() / 2 + this.offset.y - 5, 10, 10);
        Tools.writeImage(this.derivatedScreenShot, this.timestamp + "_derivated.png");
    }
}