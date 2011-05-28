/*
 * Created by JFormDesigner on Wed May 25 12:27:00 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class DetectorEvaluationGui extends JFrame {
    public DetectorEvaluationGui() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        labelDrawImages = new JLabel();
        checkBoxDrawImages = new JCheckBox();
        labelDimension = new JLabel();
        spinnerDimension = new JSpinner();
        scrollPane1 = new JScrollPane();
        listFiles = new JList();
        labelBigSteps = new JLabel();
        checkBoxBigSteps = new JCheckBox();
        labelAmount = new JLabel();
        spinnerAmount = new JSpinner();
        labelMin = new JLabel();
        labelMax = new JLabel();
        labelCoverage = new JLabel();
        spinnerCoverageMin = new JSpinner();
        spinnerCoverageMax = new JSpinner();
        labelHeight = new JLabel();
        spinnerHeightMin = new JSpinner();
        spinnerHeightMax = new JSpinner();
        labelWidth = new JLabel();
        spinnerWidthMin = new JSpinner();
        spinnerWidthMax = new JSpinner();
        labelSize = new JLabel();
        spinnerSizeMin = new JSpinner();
        spinnerSizeMax = new JSpinner();
        labelSensitivity = new JLabel();
        spinnerSensitivityMin = new JSpinner();
        spinnerSensitivityMax = new JSpinner();
        buttonSelect = new JButton();
        buttonRemove = new JButton();
        labelDescription = new JLabel();
        buttonStart = new JButton();
        progressBar = new JProgressBar();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Text Detector Parameter Evaluation");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "1dlu, 5*($lcgap, 60dlu), $lcgap, 60dlu:grow, $lcgap, 1dlu",
            "1dlu, 10*($lgap, default), $lgap, default:grow, $lgap, default, $lgap, 1dlu"));

        //---- labelDrawImages ----
        labelDrawImages.setText("draw images");
        contentPane.add(labelDrawImages, cc.xywh(3, 3, 3, 1));
        contentPane.add(checkBoxDrawImages, cc.xy(7, 3));

        //---- labelDimension ----
        labelDimension.setText("dimension");
        contentPane.add(labelDimension, cc.xywh(3, 7, 3, 1));

        //---- spinnerDimension ----
        spinnerDimension.setModel(new SpinnerNumberModel(150, 1, null, 1));
        contentPane.add(spinnerDimension, cc.xy(7, 7));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(listFiles);
        }
        contentPane.add(scrollPane1, cc.xywh(9, 3, 5, 17));

        //---- labelBigSteps ----
        labelBigSteps.setText("big steps");
        contentPane.add(labelBigSteps, cc.xywh(3, 5, 3, 1));
        contentPane.add(checkBoxBigSteps, cc.xy(7, 5));

        //---- labelAmount ----
        labelAmount.setText("amount of generated fixations");
        contentPane.add(labelAmount, cc.xywh(3, 9, 4, 1));

        //---- spinnerAmount ----
        spinnerAmount.setModel(new SpinnerNumberModel(1, 1, null, 1));
        contentPane.add(spinnerAmount, cc.xy(7, 9));

        //---- labelMin ----
        labelMin.setText("min");
        contentPane.add(labelMin, cc.xy(5, 11));

        //---- labelMax ----
        labelMax.setText("max");
        contentPane.add(labelMax, cc.xy(7, 11));

        //---- labelCoverage ----
        labelCoverage.setText("coverage");
        contentPane.add(labelCoverage, cc.xy(3, 13));

        //---- spinnerCoverageMin ----
        spinnerCoverageMin.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        contentPane.add(spinnerCoverageMin, cc.xy(5, 13));

        //---- spinnerCoverageMax ----
        spinnerCoverageMax.setModel(new SpinnerNumberModel(30, 1, 100, 1));
        contentPane.add(spinnerCoverageMax, cc.xy(7, 13));

        //---- labelHeight ----
        labelHeight.setText("letter height");
        contentPane.add(labelHeight, cc.xy(3, 15));

        //---- spinnerHeightMin ----
        spinnerHeightMin.setModel(new SpinnerNumberModel(3, 1, null, 0));
        contentPane.add(spinnerHeightMin, cc.xy(5, 15));

        //---- spinnerHeightMax ----
        spinnerHeightMax.setModel(new SpinnerNumberModel(10, 1, null, 1));
        contentPane.add(spinnerHeightMax, cc.xy(7, 15));

        //---- labelWidth ----
        labelWidth.setText("letter width");
        contentPane.add(labelWidth, cc.xy(3, 17));

        //---- spinnerWidthMin ----
        spinnerWidthMin.setModel(new SpinnerNumberModel(3, 1, null, 1));
        contentPane.add(spinnerWidthMin, cc.xy(5, 17));

        //---- spinnerWidthMax ----
        spinnerWidthMax.setModel(new SpinnerNumberModel(10, 1, null, 1));
        contentPane.add(spinnerWidthMax, cc.xy(7, 17));

        //---- labelSize ----
        labelSize.setText("line size");
        contentPane.add(labelSize, cc.xy(3, 19));

        //---- spinnerSizeMin ----
        spinnerSizeMin.setModel(new SpinnerNumberModel(1, 1, null, 1));
        contentPane.add(spinnerSizeMin, cc.xy(5, 19));

        //---- spinnerSizeMax ----
        spinnerSizeMax.setModel(new SpinnerNumberModel(80, 1, null, 1));
        contentPane.add(spinnerSizeMax, cc.xy(7, 19));

        //---- labelSensitivity ----
        labelSensitivity.setText("sensitivity");
        contentPane.add(labelSensitivity, cc.xy(3, 21));
        contentPane.add(spinnerSensitivityMin, cc.xy(5, 21));
        contentPane.add(spinnerSensitivityMax, cc.xy(7, 21));

        //---- buttonSelect ----
        buttonSelect.setText("Select");
        contentPane.add(buttonSelect, cc.xy(9, 21));

        //---- buttonRemove ----
        buttonRemove.setText("Remove");
        contentPane.add(buttonRemove, cc.xy(11, 21));

        //---- labelDescription ----
        labelDescription.setText("text");
        contentPane.add(labelDescription, cc.xywh(3, 23, 12, 1, CellConstraints.DEFAULT, CellConstraints.BOTTOM));

        //---- buttonStart ----
        buttonStart.setText("Start");
        contentPane.add(buttonStart, cc.xy(3, 25));
        contentPane.add(progressBar, cc.xywh(5, 25, 9, 1, CellConstraints.FILL, CellConstraints.FILL));
        setSize(600, 350);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JLabel labelDrawImages;
    protected JCheckBox checkBoxDrawImages;
    protected JLabel labelDimension;
    protected JSpinner spinnerDimension;
    private JScrollPane scrollPane1;
    protected JList listFiles;
    protected JLabel labelBigSteps;
    protected JCheckBox checkBoxBigSteps;
    protected JLabel labelAmount;
    protected JSpinner spinnerAmount;
    protected JLabel labelMin;
    protected JLabel labelMax;
    protected JLabel labelCoverage;
    protected JSpinner spinnerCoverageMin;
    protected JSpinner spinnerCoverageMax;
    protected JLabel labelHeight;
    protected JSpinner spinnerHeightMin;
    protected JSpinner spinnerHeightMax;
    protected JLabel labelWidth;
    protected JSpinner spinnerWidthMin;
    protected JSpinner spinnerWidthMax;
    protected JLabel labelSize;
    protected JSpinner spinnerSizeMin;
    protected JSpinner spinnerSizeMax;
    protected JLabel labelSensitivity;
    protected JSpinner spinnerSensitivityMin;
    protected JSpinner spinnerSensitivityMax;
    protected JButton buttonSelect;
    protected JButton buttonRemove;
    protected JLabel labelDescription;
    protected JButton buttonStart;
    protected JProgressBar progressBar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
