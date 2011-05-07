/*
 * Created by JFormDesigner on Thu May 05 09:47:37 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class EvaluationMainWindow extends JFrame {
    public EvaluationMainWindow() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panelContent = new JPanel();
        panelButtonPanel = new JPanel();
        buttonNext = new JButton();
        buttonCancel = new JButton();
        labelDescription = new JLabel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Project Lightning (Desktop) Evaluation Mode");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "1dlu, $lcgap, 1dlu:grow",
            "1dlu, $lgap, 1dlu:grow, $lgap, default, $lgap, 1dlu"));

        //======== panelContent ========
        {
            panelContent.setLayout(new GridBagLayout());
            ((GridBagLayout)panelContent.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)panelContent.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)panelContent.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)panelContent.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
        }
        contentPane.add(panelContent, cc.xywh(1, 1, 3, 3, CellConstraints.CENTER, CellConstraints.CENTER));

        //======== panelButtonPanel ========
        {
            panelButtonPanel.setLayout(new FormLayout(
                "2*(60dlu, $lcgap), default:grow",
                "default"));

            //---- buttonNext ----
            buttonNext.setText("Next");
            panelButtonPanel.add(buttonNext, cc.xy(1, 1));

            //---- buttonCancel ----
            buttonCancel.setText("Cancel");
            panelButtonPanel.add(buttonCancel, cc.xy(3, 1));
            panelButtonPanel.add(labelDescription, cc.xy(5, 1));
        }
        contentPane.add(panelButtonPanel, cc.xy(3, 5));
        setSize(470, 375);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JPanel panelContent;
    protected JPanel panelButtonPanel;
    protected JButton buttonNext;
    protected JButton buttonCancel;
    protected JLabel labelDescription;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
