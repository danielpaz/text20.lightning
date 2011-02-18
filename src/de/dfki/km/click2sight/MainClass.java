package de.dfki.km.click2sight;

import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.melloware.jintellitype.JIntellitype;

import de.dfki.km.click2sight.gui.TraySymbol;
import de.dfki.km.click2sight.plugins.MethodManager;
import de.dfki.km.click2sight.worker.FixationCatcher;
import de.dfki.km.click2sight.worker.FixationEvaluator;
import de.dfki.km.click2sight.worker.PrecisionTrainer;

/**
 * 
 * @author Christoph Kaeding
 *
 */
//TODO: change name and trayicons
public class MainClass {

    /** */
    private static String outputPath;
    /** shows if the program should react on hotkeys*/
    private static boolean isActivated;
    /** shows if normal mode or trainings mode is activated*/
    private static boolean isNormalMode;
    /** shows if screenshots etc are written to the outputpath*/
    private static boolean showImages;
    /** dimension for the screenshots*/
    private static int dimension;
    /** pluginmanager for the different methods*/
    private static MethodManager methodManager;
    /** */
    private static TraySymbol trayIcon;

    /** */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Unable to load native look and feel");
        }
        isActivated = true;
        isNormalMode = true;
        showImages = false;
        dimension = 100;
        methodManager = new MethodManager();
        trayIcon = new TraySymbol(methodManager);

        //        outputPath = "C:" + File.separator + "Users" + File.separator + "nesti" + File.separator + "Desktop" + File.separator + "Click2SightOutput";
        outputPath = "." + File.separator + "output";

        try {
            new File(outputPath).mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FixationEvaluator fixationEvaluator = new FixationEvaluator(methodManager);
        PrecisionTrainer precisionTrainer = new PrecisionTrainer(methodManager);
        FixationCatcher fixationCatcher = new FixationCatcher(fixationEvaluator, precisionTrainer);
        fixationCatcher.startCatching();
    }

    /** */
    public static boolean isActivated() {
        return isActivated;
    }

    /** */
    public static void changeStatus() {
        if (isActivated) {
            System.out.println("Status changed to: inactive");
            trayIcon.showMessage("status", "tool is now deactivated");
            trayIcon.setDeactivatedIcon();
            isActivated = false;
        } else {
            System.out.println("Status changed to: active");
            trayIcon.showMessage("status", "tool is now activated");
            trayIcon.setActivatedIcon();
            isActivated = true;
        }
    }

    /** */
    public static void exit() {
        JIntellitype.getInstance().cleanUp();
        trayIcon.remove();
        methodManager.getPluginManager().shutdown();
        System.out.println("Session closed.");
        System.exit(0);
    }

    /** */
    public static boolean isNormalMode() {
        return isNormalMode;
    }

    /** */
    public static void changeMode() {
        if (isNormalMode) {
            System.out.println("Modus changed to: training");
            trayIcon.showMessage("modus", "trainings mode activated");
            isNormalMode = false;
        } else {
            System.out.println("Modus changed to: normal");
            trayIcon.showMessage("modus", "normal mode activated");
            isNormalMode = true;
        }
    }

    /** */
    public static boolean showImages() {
        return showImages;
    }

    /** */
    public static void setShowImages(boolean choice) {
        showImages = choice;
    }

    /** */
    public static int getDimension() {
        return dimension;
    }

    /** */
    public static void setDimension(int choosedDimension) {
        dimension = choosedDimension;
    }

    /** */
    public static String getOutputPath() {
        return outputPath;
    }

    /** */
    public static void setOutputPath(String choosedOutputPath) {
        try {
            File outputDir = new File(choosedOutputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            } else {
                if (outputDir.isDirectory()) {
                    outputPath = choosedOutputPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showTrayMessage(String caption, String text) {
        trayIcon.showMessage(caption, text);
    }
}
