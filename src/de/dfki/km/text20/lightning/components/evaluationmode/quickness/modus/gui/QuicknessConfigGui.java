/*
 * Created by JFormDesigner on Sat May 14 15:09:43 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.quickness.modus.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class QuicknessConfigGui extends JFrame {
    public QuicknessConfigGui() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        listFiles = new JList();
        buttonSelect = new JButton();
        buttonRemove = new JButton();
        labelDetector = new JLabel();
        comboBoxDetector = new JComboBox();
        buttonDetector = new JButton();
        label1 = new JLabel();
        checkBoxStartWithDetector = new JCheckBox();
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
            "1dlu, $lgap, default:grow, 6*($lgap, default), $lgap, 1dlu"));

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

        //---- labelDetector ----
        labelDetector.setText("Detector");
        contentPane.add(labelDetector, cc.xywh(3, 7, 1, 3));
        contentPane.add(comboBoxDetector, cc.xy(5, 7));

        //---- buttonDetector ----
        buttonDetector.setText("Configuration");
        contentPane.add(buttonDetector, cc.xy(5, 9));

        //---- label1 ----
        label1.setText("start with Detector");
        contentPane.add(label1, cc.xy(3, 11));
        contentPane.add(checkBoxStartWithDetector, cc.xy(5, 11));

        //---- labelSteps ----
        labelSteps.setText("Steps");
        contentPane.add(labelSteps, cc.xy(3, 13));

        //---- spinnerSteps ----
        spinnerSteps.setModel(new SpinnerNumberModel(1, 1, null, 1));
        contentPane.add(spinnerSteps, cc.xy(5, 13));

        //---- buttonOK ----
        buttonOK.setText("OK");
        contentPane.add(buttonOK, cc.xy(3, 15));

        //---- buttonCancel ----
        buttonCancel.setText("Cancel");
        contentPane.add(buttonCancel, cc.xy(5, 15));
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
    protected JLabel labelDetector;
    protected JComboBox comboBoxDetector;
    protected JButton buttonDetector;
    protected JLabel label1;
    protected JCheckBox checkBoxStartWithDetector;
    protected JLabel labelSteps;
    protected JSpinner spinnerSteps;
    protected JButton buttonOK;
    protected JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
