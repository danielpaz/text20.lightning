/*
 * Created by JFormDesigner on Mon May 02 10:57:14 CEST 2011
 */

package de.dfki.km.text20.lightning.worker.evaluator.plugins.coveragedetector.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class CoverageDetectorConfig extends JFrame {
    public CoverageDetectorConfig() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        labelLetterHeight = new JLabel();
        spinnerLetterHeight = new JSpinner();
        labelLineSize = new JLabel();
        spinnerLineSize = new JSpinner();
        labelSensitivity = new JLabel();
        spinnerSensitivity = new JSpinner();
        labelDebug = new JLabel();
        checkBoxDebug = new JCheckBox();
        buttonOK = new JButton();
        buttonDefault = new JButton();
        buttonCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Coverage Detector Config");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default:grow",
            "default:grow"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "2*(60dlu:grow, $lcgap), 60dlu:grow",
                    "4*(default, $lgap), bottom:default:grow"));

                //---- labelLetterHeight ----
                labelLetterHeight.setText("min letter height in Pixel");
                contentPanel.add(labelLetterHeight, cc.xywh(1, 1, 3, 1));
                contentPanel.add(spinnerLetterHeight, cc.xy(5, 1));

                //---- labelLineSize ----
                labelLineSize.setText("max line size in Pixel");
                contentPanel.add(labelLineSize, cc.xywh(1, 3, 3, 1));
                contentPanel.add(spinnerLineSize, cc.xy(5, 3));

                //---- labelSensitivity ----
                labelSensitivity.setText("sensitivity");
                contentPanel.add(labelSensitivity, cc.xywh(1, 5, 3, 1));
                contentPanel.add(spinnerSensitivity, cc.xy(5, 5));

                //---- labelDebug ----
                labelDebug.setText("draw debug images");
                contentPanel.add(labelDebug, cc.xywh(1, 7, 3, 1));
                contentPanel.add(checkBoxDebug, cc.xy(5, 7));

                //---- buttonOK ----
                buttonOK.setText("OK");
                contentPanel.add(buttonOK, cc.xy(1, 9));

                //---- buttonDefault ----
                buttonDefault.setText("Default");
                contentPanel.add(buttonDefault, cc.xy(3, 9));

                //---- buttonCancel ----
                buttonCancel.setText("Cancel");
                contentPanel.add(buttonCancel, cc.xy(5, 9));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, cc.xywh(1, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    protected JLabel labelLetterHeight;
    protected JSpinner spinnerLetterHeight;
    protected JLabel labelLineSize;
    protected JSpinner spinnerLineSize;
    protected JLabel labelSensitivity;
    protected JSpinner spinnerSensitivity;
    protected JLabel labelDebug;
    protected JCheckBox checkBoxDebug;
    protected JButton buttonOK;
    protected JButton buttonDefault;
    protected JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
