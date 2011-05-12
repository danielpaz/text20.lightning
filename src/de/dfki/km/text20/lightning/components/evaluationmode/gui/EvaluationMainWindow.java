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
        scrollPane1 = new JScrollPane();
        panelContent = new JPanel();
        panelButtonPanel = new JPanel();
        buttonNext = new JButton();
        labelDescription = new JLabel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Project Lightning (Desktop) Evaluation Mode");
        setBackground(Color.white);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "1dlu, $lcgap, 1dlu:grow",
            "1dlu, $lgap, 1dlu:grow, $lgap, default, $lgap, 1dlu"));

        //======== scrollPane1 ========
        {

            //======== panelContent ========
            {
                panelContent.setLayout(new GridBagLayout());
                ((GridBagLayout)panelContent.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)panelContent.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)panelContent.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)panelContent.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
            }
            scrollPane1.setViewportView(panelContent);
        }
        contentPane.add(scrollPane1, cc.xywh(1, 1, 3, 3, CellConstraints.FILL, CellConstraints.FILL));

        //======== panelButtonPanel ========
        {
            panelButtonPanel.setLayout(new FormLayout(
                "60dlu, $lcgap, default:grow",
                "default"));

            //---- buttonNext ----
            buttonNext.setText("Next");
            panelButtonPanel.add(buttonNext, cc.xy(1, 1));

            //---- labelDescription ----
            labelDescription.setText("text");
            panelButtonPanel.add(labelDescription, cc.xy(3, 1));
        }
        contentPane.add(panelButtonPanel, cc.xy(3, 5));
        setSize(470, 375);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    protected JPanel panelContent;
    protected JPanel panelButtonPanel;
    protected JButton buttonNext;
    protected JLabel labelDescription;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
