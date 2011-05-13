/*
 * Created by JFormDesigner on Fri May 13 14:06:52 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
public class DoubleBlindGui extends JFrame {
    public DoubleBlindGui() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        labelOne = new JLabel();
        comboBoxOne = new JComboBox();
        labelTwo = new JLabel();
        comboBoxTwo = new JComboBox();
        labelTime = new JLabel();
        spinnerTime = new JSpinner();
        buttonStart = new JButton();
        buttonCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Double Blinde Mode");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "1dlu, 2*($lcgap, 80dlu:grow), $lcgap, 1dlu",
            "1dlu, 3*($lgap, default), $lgap, 1dlu:grow, $lgap, default, $lgap, 1dlu"));

        //---- labelOne ----
        labelOne.setText("Algorithm One");
        contentPane.add(labelOne, cc.xy(3, 3));
        contentPane.add(comboBoxOne, cc.xy(5, 3));

        //---- labelTwo ----
        labelTwo.setText("Algorithm Two");
        contentPane.add(labelTwo, cc.xy(3, 5));
        contentPane.add(comboBoxTwo, cc.xy(5, 5));

        //---- labelTime ----
        labelTime.setText("available time in minutes");
        contentPane.add(labelTime, cc.xy(3, 7));

        //---- spinnerTime ----
        spinnerTime.setModel(new SpinnerNumberModel(1, 1, null, 1));
        contentPane.add(spinnerTime, cc.xy(5, 7));

        //---- buttonStart ----
        buttonStart.setText("Start");
        contentPane.add(buttonStart, cc.xy(3, 11));

        //---- buttonCancel ----
        buttonCancel.setText("Cancel");
        contentPane.add(buttonCancel, cc.xy(5, 11));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JLabel labelOne;
    protected JComboBox comboBoxOne;
    protected JLabel labelTwo;
    protected JComboBox comboBoxTwo;
    protected JLabel labelTime;
    protected JSpinner spinnerTime;
    protected JButton buttonStart;
    protected JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
