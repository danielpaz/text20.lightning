/*
 * Created by JFormDesigner on Sat May 14 15:09:43 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class LiveConfigGui extends JFrame {
    public LiveConfigGui() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        listFiles = new JList();
        buttonSelect = new JButton();
        buttonRemove = new JButton();
        labelOne = new JLabel();
        comboBoxOne = new JComboBox();
        buttonOneConfig = new JButton();
        labelTwo = new JLabel();
        comboBoxTwo = new JComboBox();
        buttonTwoConfig = new JButton();
        labelSteps = new JLabel();
        spinnerSteps = new JSpinner();
        buttonOK = new JButton();
        buttonCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setResizable(false);
        setTitle("Quickness Mode");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "1dlu, 2*($lcgap, 60dlu:grow), $lcgap, 1dlu",
            "1dlu, $lgap, default:grow, 7*($lgap, default), $lgap, 1dlu"));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(listFiles);
        }
        contentPane.add(scrollPane1, cc.xywh(3, 3, 3, 1, CellConstraints.FILL, CellConstraints.FILL));

        //---- buttonSelect ----
        buttonSelect.setText("Select");
        contentPane.add(buttonSelect, cc.xy(3, 5));

        //---- buttonRemove ----
        buttonRemove.setText("Remove");
        contentPane.add(buttonRemove, cc.xy(5, 5));

        //---- labelOne ----
        labelOne.setText("Algorithm A");
        contentPane.add(labelOne, cc.xywh(3, 7, 1, 3));
        contentPane.add(comboBoxOne, cc.xy(5, 7));

        //---- buttonOneConfig ----
        buttonOneConfig.setText("Configuration");
        contentPane.add(buttonOneConfig, cc.xy(5, 9));

        //---- labelTwo ----
        labelTwo.setText("Algorithm B");
        contentPane.add(labelTwo, cc.xywh(3, 11, 1, 3));
        contentPane.add(comboBoxTwo, cc.xy(5, 11));

        //---- buttonTwoConfig ----
        buttonTwoConfig.setText("Configuration");
        contentPane.add(buttonTwoConfig, cc.xy(5, 13));

        //---- labelSteps ----
        labelSteps.setText("Steps");
        contentPane.add(labelSteps, cc.xy(3, 15));

        //---- spinnerSteps ----
        spinnerSteps.setModel(new SpinnerNumberModel(1, 1, null, 1));
        contentPane.add(spinnerSteps, cc.xy(5, 15));

        //---- buttonOK ----
        buttonOK.setText("Start");
        contentPane.add(buttonOK, cc.xy(3, 17));

        //---- buttonCancel ----
        buttonCancel.setText("Cancel");
        contentPane.add(buttonCancel, cc.xy(5, 17));
        setSize(465, 300);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    protected JList listFiles;
    protected JButton buttonSelect;
    protected JButton buttonRemove;
    protected JLabel labelOne;
    protected JComboBox comboBoxOne;
    protected JButton buttonOneConfig;
    protected JLabel labelTwo;
    protected JComboBox comboBoxTwo;
    protected JButton buttonTwoConfig;
    protected JLabel labelSteps;
    protected JSpinner spinnerSteps;
    protected JButton buttonOK;
    protected JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
