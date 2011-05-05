/*
 * Created by JFormDesigner on Thu May 05 09:59:29 CEST 2011
 */

package de.dfki.km.text20.lightning.worker.evaluationmode.gui;

import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
public class ContentPanelOne extends JPanel {
    public ContentPanelOne() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        textPaneContent = new JTextPane();
        buttonStart = new JButton();
        labelHint = new JLabel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setLayout(new FormLayout(
            "80dlu, 5dlu, default:grow",
            "default:grow, $lgap, default"));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(textPaneContent);
        }
        add(scrollPane1, cc.xywh(1, 1, 3, 1, CellConstraints.FILL, CellConstraints.FILL));

        //---- buttonStart ----
        buttonStart.setText("Start");
        add(buttonStart, cc.xy(1, 3));

        //---- labelHint ----
        labelHint.setText("text");
        add(labelHint, cc.xy(3, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    protected JTextPane textPaneContent;
    protected JButton buttonStart;
    protected JLabel labelHint;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
