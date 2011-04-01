/*
 * Created by JFormDesigner on Mon Mar 21 11:10:43 CET 2011
 */

package de.dfki.km.text20.lightning.plugins.mouseWarp.impl.AdvancedWarper.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class AdvancedWarperConfig extends JFrame {
    public AdvancedWarperConfig() {
        initComponents();
    }

    private void buttonDefaultActionPerformed(ActionEvent e) {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        labelAngleThreshold = new JLabel();
        spinnerAngle = new JSpinner();
        labelSpeed = new JLabel();
        spinnerSpeed = new JSpinner();
        labelReactionTime = new JLabel();
        spinnerReactionTime = new JSpinner();
        buttonOK = new JButton();
        buttonCancel = new JButton();
        buttonDefault = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setResizable(false);
        setTitle("Advanced Warper");
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
                    "3*(default, $lgap), default"));

                //---- labelAngleThreshold ----
                labelAngleThreshold.setText("Angle Threshold in Degree");
                contentPanel.add(labelAngleThreshold, cc.xywh(1, 1, 3, 1));

                //---- spinnerAngle ----
                spinnerAngle.setModel(new SpinnerNumberModel(10, 0, 180, 1));
                contentPanel.add(spinnerAngle, cc.xy(5, 1));

                //---- labelSpeed ----
                labelSpeed.setText("minimal Speed in Pixel/ms");
                contentPanel.add(labelSpeed, cc.xywh(1, 3, 3, 1));

                //---- spinnerSpeed ----
                spinnerSpeed.setModel(new SpinnerNumberModel(0.0, 0.0, 2.147483647E9, 1.0));
                contentPanel.add(spinnerSpeed, cc.xy(5, 3));

                //---- labelReactionTime ----
                labelReactionTime.setText("Home Radius in Piexel");
                contentPanel.add(labelReactionTime, cc.xywh(1, 5, 3, 1));

                //---- spinnerReactionTime ----
                spinnerReactionTime.setModel(new SpinnerNumberModel(0, 0, 2147483647, 1));
                contentPanel.add(spinnerReactionTime, cc.xy(5, 5));

                //---- buttonOK ----
                buttonOK.setText("OK");
                contentPanel.add(buttonOK, cc.xy(1, 7));

                //---- buttonCancel ----
                buttonCancel.setText("Cancel");
                contentPanel.add(buttonCancel, cc.xy(3, 7));

                //---- buttonDefault ----
                buttonDefault.setText("Default");
                buttonDefault.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonDefaultActionPerformed(e);
                    }
                });
                contentPanel.add(buttonDefault, cc.xy(5, 7));
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
    protected JLabel labelSpeed;
    protected JSpinner spinnerSpeed;
    protected JLabel labelReactionTime;
    protected JSpinner spinnerReactionTime;
    protected JButton buttonOK;
    protected JButton buttonCancel;
    protected JButton buttonDefault;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
