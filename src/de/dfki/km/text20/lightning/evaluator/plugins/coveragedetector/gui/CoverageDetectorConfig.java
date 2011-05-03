/*
 * Created by JFormDesigner on Mon May 02 10:57:14 CEST 2011
 */

package de.dfki.km.text20.lightning.evaluator.plugins.coveragedetector.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
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
        labelStemSize = new JLabel();
        spinnerStemSize = new JSpinner();
        labelLineSize = new JLabel();
        spinnerLineSize = new JSpinner();
        labelDebug = new JLabel();
        checkBoxDebug = new JCheckBox();
        labelMerge = new JLabel();
        checkBoxMerge = new JCheckBox();
        labelDelete = new JLabel();
        checkBoxDelete = new JCheckBox();
        separator1 = new JSeparator();
        labelDestinyFactor = new JLabel();
        spinnerDestinyFactor = new JSpinner();
        labelMass = new JLabel();
        spinnerMass = new JSpinner();
        labelDist1 = new JLabel();
        spinnerDist1 = new JSpinner();
        labelDistFac = new JLabel();
        spinnerDistFac = new JSpinner();
        labelDist2 = new JLabel();
        spinnerDist2 = new JSpinner();
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
                    "6*(default, $lgap), 3dlu, 5*($lgap, default), $lgap, bottom:default:grow"));

                //---- labelLetterHeight ----
                labelLetterHeight.setText("min letter height in Pixel");
                contentPanel.add(labelLetterHeight, cc.xywh(1, 1, 3, 1));
                contentPanel.add(spinnerLetterHeight, cc.xy(5, 1));

                //---- labelStemSize ----
                labelStemSize.setText("min stem size in %");
                contentPanel.add(labelStemSize, cc.xywh(1, 3, 3, 1));
                contentPanel.add(spinnerStemSize, cc.xy(5, 3));

                //---- labelLineSize ----
                labelLineSize.setText("max line size in Pixel");
                contentPanel.add(labelLineSize, cc.xywh(1, 5, 3, 1));
                contentPanel.add(spinnerLineSize, cc.xy(5, 5));

                //---- labelDebug ----
                labelDebug.setText("draw debug images");
                contentPanel.add(labelDebug, cc.xywh(1, 7, 3, 1));
                contentPanel.add(checkBoxDebug, cc.xy(5, 7));

                //---- labelMerge ----
                labelMerge.setText("use merge");
                contentPanel.add(labelMerge, cc.xywh(1, 9, 3, 1));
                contentPanel.add(checkBoxMerge, cc.xy(5, 9));

                //---- labelDelete ----
                labelDelete.setText("use delete");
                contentPanel.add(labelDelete, cc.xywh(1, 11, 3, 1));
                contentPanel.add(checkBoxDelete, cc.xy(5, 11));
                contentPanel.add(separator1, cc.xywh(1, 13, 5, 1));

                //---- labelDestinyFactor ----
                labelDestinyFactor.setText("merge_densityFactor");
                contentPanel.add(labelDestinyFactor, cc.xywh(1, 15, 3, 1));
                contentPanel.add(spinnerDestinyFactor, cc.xy(5, 15));

                //---- labelMass ----
                labelMass.setText("merge_mass");
                contentPanel.add(labelMass, cc.xywh(1, 17, 3, 1));
                contentPanel.add(spinnerMass, cc.xy(5, 17));

                //---- labelDist1 ----
                labelDist1.setText("merge_dist1");
                contentPanel.add(labelDist1, cc.xywh(1, 19, 3, 1));
                contentPanel.add(spinnerDist1, cc.xy(5, 19));

                //---- labelDistFac ----
                labelDistFac.setText("merge_distfac");
                contentPanel.add(labelDistFac, cc.xywh(1, 21, 3, 1));
                contentPanel.add(spinnerDistFac, cc.xy(5, 21));

                //---- labelDist2 ----
                labelDist2.setText("merge_dist2");
                contentPanel.add(labelDist2, cc.xywh(1, 23, 3, 1));
                contentPanel.add(spinnerDist2, cc.xy(5, 23));

                //---- buttonOK ----
                buttonOK.setText("OK");
                contentPanel.add(buttonOK, cc.xy(1, 25));

                //---- buttonDefault ----
                buttonDefault.setText("Default");
                contentPanel.add(buttonDefault, cc.xy(3, 25));

                //---- buttonCancel ----
                buttonCancel.setText("Cancel");
                contentPanel.add(buttonCancel, cc.xy(5, 25));
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
    protected JLabel labelStemSize;
    protected JSpinner spinnerStemSize;
    protected JLabel labelLineSize;
    protected JSpinner spinnerLineSize;
    protected JLabel labelDebug;
    protected JCheckBox checkBoxDebug;
    protected JLabel labelMerge;
    protected JCheckBox checkBoxMerge;
    protected JLabel labelDelete;
    protected JCheckBox checkBoxDelete;
    private JSeparator separator1;
    protected JLabel labelDestinyFactor;
    protected JSpinner spinnerDestinyFactor;
    protected JLabel labelMass;
    protected JSpinner spinnerMass;
    protected JLabel labelDist1;
    protected JSpinner spinnerDist1;
    protected JLabel labelDistFac;
    protected JSpinner spinnerDistFac;
    protected JLabel labelDist2;
    protected JSpinner spinnerDist2;
    protected JButton buttonOK;
    protected JButton buttonDefault;
    protected JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
