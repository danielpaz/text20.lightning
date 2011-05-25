/*
 * Created by JFormDesigner on Fri May 13 14:06:52 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.gui;

import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class DoubleBlindGui extends JFrame {
    public DoubleBlindGui() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        labelOne = new JLabel();
        comboBoxOne = new JComboBox();
        buttonOneConfig = new JButton();
        labelTwo = new JLabel();
        comboBoxTwo = new JComboBox();
        buttonTwoConfig = new JButton();
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
            "1dlu, 5*($lgap, default), $lgap, 1dlu:grow, $lgap, default, $lgap, 1dlu"));

        //---- labelOne ----
        labelOne.setText("Algorithm A");
        contentPane.add(labelOne, cc.xywh(3, 3, 1, 3));
        contentPane.add(comboBoxOne, cc.xy(5, 3));

        //---- buttonOneConfig ----
        buttonOneConfig.setText("Configuration");
        contentPane.add(buttonOneConfig, cc.xy(5, 5));

        //---- labelTwo ----
        labelTwo.setText("Algorithm Two");
        contentPane.add(labelTwo, cc.xywh(3, 7, 1, 3));
        contentPane.add(comboBoxTwo, cc.xy(5, 7));

        //---- buttonTwoConfig ----
        buttonTwoConfig.setText("Configuration");
        contentPane.add(buttonTwoConfig, cc.xy(5, 9));

        //---- labelTime ----
        labelTime.setText("available time in minutes");
        contentPane.add(labelTime, cc.xy(3, 11));

        //---- spinnerTime ----
        spinnerTime.setModel(new SpinnerNumberModel(1, 1, null, 1));
        contentPane.add(spinnerTime, cc.xy(5, 11));

        //---- buttonStart ----
        buttonStart.setText("Start");
        contentPane.add(buttonStart, cc.xy(3, 15));

        //---- buttonCancel ----
        buttonCancel.setText("Cancel");
        contentPane.add(buttonCancel, cc.xy(5, 15));
        setSize(400, 215);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JLabel labelOne;
    protected JComboBox comboBoxOne;
    protected JButton buttonOneConfig;
    protected JLabel labelTwo;
    protected JComboBox comboBoxTwo;
    protected JButton buttonTwoConfig;
    protected JLabel labelTime;
    protected JSpinner spinnerTime;
    protected JButton buttonStart;
    protected JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
