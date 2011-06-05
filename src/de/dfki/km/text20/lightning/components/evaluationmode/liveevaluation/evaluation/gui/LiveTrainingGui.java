/*
 * Created by JFormDesigner on Fri Jun 03 18:56:43 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
public class LiveTrainingGui extends JFrame {
    public LiveTrainingGui() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        textPaneContent = new JTextPane();
        buttonReady = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Project Lightning Evaluation Training");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "1dlu, $lcgap, 60dlu, $lcgap, default:grow",
            "default:grow, $lgap, default, $lgap, 1dlu"));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(textPaneContent);
        }
        contentPane.add(scrollPane1, cc.xywh(1, 1, 5, 1, CellConstraints.FILL, CellConstraints.FILL));

        //---- buttonReady ----
        buttonReady.setText("Ready");
        contentPane.add(buttonReady, cc.xy(3, 3));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    protected JTextPane textPaneContent;
    protected JButton buttonReady;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
