/*
 * Created by JFormDesigner on Mon Mar 21 11:10:43 CET 2011
 */

package de.dfki.km.text20.lightning.plugins.mouseWarp.impl.improvedSimpleWarper.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class ImprovedWarperConfig extends JFrame {
    public ImprovedWarperConfig() {
        initComponents();
    }

    private void buttonDefaultActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        labelAngleThreshold = new JLabel();
        spinnerAngle = new JSpinner();
        labelDistanceThreshold = new JLabel();
        spinnerDistance = new JSpinner();
        labelDurationThreshold = new JLabel();
        spinnerDuration = new JSpinner();
        spinnerSetRadius = new JSpinner();
        labelHomeRadius = new JLabel();
        spinnerHomeRadius = new JSpinner();
        labelSetRadius = new JLabel();
        buttonOK = new JButton();
        buttonCancel = new JButton();
        buttonDefault = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setResizable(false);
        setTitle("Improved Simple Sobel");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "2*(50dlu, $lcgap), 50dlu",
                    "5*(default, $lgap), default"));

                //---- labelAngleThreshold ----
                labelAngleThreshold.setText("Angle Threshold");
                contentPanel.add(labelAngleThreshold, cc.xywh(1, 1, 3, 1));

                //---- spinnerAngle ----
                spinnerAngle.setModel(new SpinnerNumberModel(10, 0, 180, 1));
                contentPanel.add(spinnerAngle, cc.xy(5, 1));

                //---- labelDistanceThreshold ----
                labelDistanceThreshold.setText("Distance Threshold");
                contentPanel.add(labelDistanceThreshold, cc.xywh(1, 3, 3, 1));

                //---- spinnerDistance ----
                spinnerDistance.setModel(new SpinnerNumberModel(0, 0, 2147483647, 1));
                contentPanel.add(spinnerDistance, cc.xy(5, 3));

                //---- labelDurationThreshold ----
                labelDurationThreshold.setText("Duration Threshold");
                contentPanel.add(labelDurationThreshold, cc.xywh(1, 5, 3, 1));

                //---- spinnerDuration ----
                spinnerDuration.setModel(new SpinnerNumberModel(300, 200, 2147483647, 100));
                contentPanel.add(spinnerDuration, cc.xy(5, 5));

                //---- spinnerSetRadius ----
                spinnerSetRadius.setModel(new SpinnerNumberModel(20, 0, 2147483647, 1));
                contentPanel.add(spinnerSetRadius, cc.xy(5, 9));

                //---- labelHomeRadius ----
                labelHomeRadius.setText("Home Radius");
                contentPanel.add(labelHomeRadius, cc.xywh(1, 7, 3, 1));

                //---- spinnerHomeRadius ----
                spinnerHomeRadius.setModel(new SpinnerNumberModel(0, 0, 2147483647, 1));
                contentPanel.add(spinnerHomeRadius, cc.xy(5, 7));

                //---- labelSetRadius ----
                labelSetRadius.setText("Set Radius");
                contentPanel.add(labelSetRadius, cc.xywh(1, 9, 3, 1));

                //---- buttonOK ----
                buttonOK.setText("OK");
                contentPanel.add(buttonOK, cc.xy(1, 11));

                //---- buttonCancel ----
                buttonCancel.setText("Cancel");
                contentPanel.add(buttonCancel, cc.xy(3, 11));

                //---- buttonDefault ----
                buttonDefault.setText("Default");
                buttonDefault.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonDefaultActionPerformed(e);
                    }
                });
                contentPanel.add(buttonDefault, cc.xy(5, 11));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    protected JLabel labelAngleThreshold;
    protected JSpinner spinnerAngle;
    protected JLabel labelDistanceThreshold;
    protected JSpinner spinnerDistance;
    protected JLabel labelDurationThreshold;
    protected JSpinner spinnerDuration;
    protected JSpinner spinnerSetRadius;
    protected JLabel labelHomeRadius;
    protected JSpinner spinnerHomeRadius;
    protected JLabel labelSetRadius;
    protected JButton buttonOK;
    protected JButton buttonCancel;
    protected JButton buttonDefault;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
