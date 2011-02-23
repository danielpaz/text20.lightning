/*
 * Tools.java
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
package de.dfki.km.text20.lightning.tools;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import de.dfki.km.text20.lightning.MainClass;

/**
 * This class provides tools which can be used form anywhere in the program.  
 * 
 * @author Christoph Käding
 * 
 */
public class Tools {

    /**
     * writes given image with given name to the global output path
     * 
     * @param picture
     * @param name
     * @return true is successful
     */
    public static boolean writeImage(BufferedImage picture, String name) {
        try {
            File outputfile = new File(MainClass.getProperties().getOutputPath() + File.separator + name);
            ImageIO.write(picture, "png", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * updates the logfile with the given logstring
     * 
     * @param logString
     * @return true is successful
     */
    public static boolean updateLogFile(String logString) {
        String existingLog = new String("");
        if (new File(MainClass.getProperties().getOutputPath() + File.separator + "log.txt").exists()) {
            existingLog = Tools.readLogFile();
        }
        return Tools.writeLogFile(existingLog + logString);
    }

    /**
     * if the logfile already exists, its content is readed
     * 
     * @return true if successful
     */
    public static String readLogFile() {
        byte[] buffer = new byte[(int) new File(MainClass.getProperties().getOutputPath() + File.separator + "log.txt").length()];
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(MainClass.getProperties().getOutputPath() + File.separator + "log.txt"));
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
     * @return true is successful
     */
    public static boolean writeLogFile(String logString) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(MainClass.getProperties().getOutputPath() + File.separator + "log.txt"));
            outputStream.write(logString.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Checks if the required JIntellitype.dll is placed in the windows directory.
     * If it is not there it tries to copy it into the System32 directory.
     * 
     * @return true if it is there or the copy was successful 
     */
    // FIXME: works only with admin rights
    public static boolean checkJIntellyTypeDLL() {
        File destination = new File(System.getenv("SYSTEMROOT") + "/System32/JIntellitype.dll");
        
        // check if it is already there
        if (!destination.exists()) {
            
            try {
                
                // set necessary variables
                // nio is used here because the os organizes the process
                File source = new File("./JIntellitype.dll");
                long overallBytesTransfered = 0;
                long bytesTransfered = 0;
                FileInputStream fileInputStream = new FileInputStream(source);
                FileOutputStream fileOutputStream = new FileOutputStream(destination);
                FileChannel inputChannel = fileInputStream.getChannel();
                ByteChannel outputChannel = fileOutputStream.getChannel();

                // copy file
                while (overallBytesTransfered < source.length()) {
                    bytesTransfered = inputChannel.transferTo(overallBytesTransfered, Math.min(1024 * 1024, source.length() - overallBytesTransfered), outputChannel);
                    overallBytesTransfered += bytesTransfered;
                }

                // cleanup
                fileInputStream.close();
                fileOutputStream.close();

                // set last modified
                destination.setLastModified(source.lastModified());
                
            } catch (Exception e) {
                e.printStackTrace();
                MainClass.showTrayMessage("Project Lightning (Desktop)", "Initializing failed. A necessary DLL-file could not be copied into your " + destination.getParent() + " directory. Please do it by yourself or run Project Lightning (Desktop) with granted administration rights.");
                return false;
            }
        }
        
        return true;
    }
}
