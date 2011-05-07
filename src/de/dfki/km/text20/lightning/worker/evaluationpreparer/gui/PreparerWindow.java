/*
 * Created by JFormDesigner on Sat May 07 13:03:33 CEST 2011
 */

package de.dfki.km.text20.lightning.worker.evaluationpreparer.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
public class PreparerWindow extends JFrame {
    public PreparerWindow() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        labelDimensionX = new JLabel();
        spinnerDimensionX = new JSpinner();
        labelDimensionY = new JLabel();
        spinnerDimensionY = new JSpinner();
        labelAmount = new JLabel();
        spinnerAmount = new JSpinner();
        labelPath = new JLabel();
        buttonSelect = new JButton();
        textFieldPath = new JTextField();
        buttonStart = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Project Lighning Evaluation Preparer");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "1dlu, $lcgap, 80dlu, 2*($lcgap, 80dlu:grow), $lcgap, 1dlu",
            "1dlu, 5*($lgap, default), $lgap, default:grow, $lgap, 1dlu"));

        //---- labelDimensionX ----
        labelDimensionX.setText("Dimension - X (0 = whole screen)");
        contentPane.add(labelDimensionX, cc.xywh(3, 3, 3, 1));
        contentPane.add(spinnerDimensionX, cc.xy(7, 3));

        //---- labelDimensionY ----
        labelDimensionY.setText("Dimension - Y (0 = whole screen)");
        contentPane.add(labelDimensionY, cc.xywh(3, 5, 3, 1));
        contentPane.add(spinnerDimensionY, cc.xy(7, 5));

        //---- labelAmount ----
        labelAmount.setText("Amount of Screenshots");
        contentPane.add(labelAmount, cc.xywh(3, 7, 3, 1));
        contentPane.add(spinnerAmount, cc.xy(7, 7));

        //---- labelPath ----
        labelPath.setText("Output Path");
        contentPane.add(labelPath, cc.xywh(3, 9, 3, 3));

        //---- buttonSelect ----
        buttonSelect.setText("Select");
        contentPane.add(buttonSelect, cc.xywh(7, 9, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
        contentPane.add(textFieldPath, cc.xy(7, 11));

        //---- buttonStart ----
        buttonStart.setText("Start");
        contentPane.add(buttonStart, cc.xywh(5, 13, 1, 1, CellConstraints.DEFAULT, CellConstraints.BOTTOM));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JLabel labelDimensionX;
    protected JSpinner spinnerDimensionX;
    protected JLabel labelDimensionY;
    protected JSpinner spinnerDimensionY;
    protected JLabel labelAmount;
    protected JSpinner spinnerAmount;
    protected JLabel labelPath;
    protected JButton buttonSelect;
    protected JTextField textFieldPath;
    protected JButton buttonStart;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
