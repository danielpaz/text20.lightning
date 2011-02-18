package de.dfki.km.text20.lightning.tools;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.dfki.km.text20.lightning.MainClass;

/**
 * 
 * @author Christoph Kaeding
 * 
 * This class provides tools which can be used form anywhere in the program.  
 */
public class Tools {

    /**
     * writes given image with given name to the global output path
     * 
     * @param picture
     * @param name
     * @return
     */
    public static boolean writeImage(BufferedImage picture, String name) {
        try {
            File outputfile = new File(MainClass.getOutputPath() + File.separator + name);
            ImageIO.write(picture, "png", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * updates the logfile
     * 
     * @param logString
     * @return
     */
    public static boolean updateLogFile(String logString) {
        if (new File(MainClass.getOutputPath() + File.separator + "log.txt").exists()) {
            logString = Tools.readLogFile() + logString;
        }
        return Tools.writeLogFile(logString);
    }

    /**
     * if the logfile already exists, its content is readed
     * 
     * @return
     */
    public static String readLogFile() {
        byte[] buffer = new byte[(int) new File(MainClass.getOutputPath() + File.separator + "log.txt").length()];
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(MainClass.getOutputPath() + File.separator + "log.txt"));
            inputStream.read(buffer);
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        return new String(buffer);
    }

    /**
     * writes the logfile to the global outputpath
     * 
     * @param logString
     * @return
     */
    public static boolean writeLogFile(String logString) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(MainClass.getOutputPath() + File.separator + "log.txt"));
            outputStream.write(logString.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
