package de.dfki.km.text20.lightning.worker;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.plugins.MethodManager;
import de.dfki.km.text20.lightning.tools.Tools;

//TODO: add comments
//TODO: do sth with the trainingsdata
//TODO: use plugin for trainings method
public class PrecisionTrainer {

    private Point fixation;
    private Point mousePosition;
    private Point offset;
    private Robot robot;
    private long timestamp;
    private MethodManager methodManager;

    public PrecisionTrainer(MethodManager manager) {
        this.fixation = new Point();
        this.offset = new Point();
        this.methodManager = manager;

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            MainClass.exit();
        }
    }

    public void setFixationPoint(Point fixation) {
        this.fixation = fixation;
        this.timestamp = System.currentTimeMillis();
    }

    public void setMousePosition(Point mousePosition) {
        this.mousePosition = mousePosition;
        if (MainClass.showImages()) {
            this.drawPicture();
        }
        this.offset.setLocation(this.mousePosition.x - this.fixation.x, this.mousePosition.y - this.fixation.y);
        Tools.updateLogFile("Training - Timestamp: " + this.timestamp + ", Fixation: (" + this.fixation.x + "," + this.fixation.y + "), Mouseposition: (" + this.mousePosition.x + "," + this.mousePosition.y + "), Dimension: " + MainClass.getDimension() + System.getProperty("line.separator"));
        System.out.println("step recognised...");
    }

    private void drawPicture() {
        Rectangle screenShotRect = new Rectangle(this.fixation.x - MainClass.getDimension() / 2, this.fixation.y - MainClass.getDimension() / 2, MainClass.getDimension(), MainClass.getDimension());
        BufferedImage screenShot = this.robot.createScreenCapture(screenShotRect);
        Graphics2D graphic = screenShot.createGraphics();
        graphic.setColor(new Color(255, 255, 0, 255));
        graphic.drawOval(MainClass.getDimension() / 2 - 5, MainClass.getDimension() / 2 - 5, 10, 10);
        graphic.setColor(new Color(255, 255, 0, 32));
        graphic.fillOval(MainClass.getDimension() / 2 - 5, MainClass.getDimension() / 2 - 5, 10, 10);
        graphic.setColor(new Color(255, 0, 0, 255));
        graphic.drawOval(this.offset.x + MainClass.getDimension() / 2, this.offset.y + MainClass.getDimension() / 2, 10, 10);
        graphic.setColor(new Color(255, 0, 0, 32));
        graphic.fillOval(this.offset.x + MainClass.getDimension() / 2, this.offset.y + MainClass.getDimension() / 2, 10, 10);
        Tools.writeImage(screenShot, this.timestamp + "_training.png");
    }
}
