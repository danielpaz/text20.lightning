/*
 * Created by JFormDesigner on Thu May 26 13:27:12 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.precision.screenshotviewer.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
public class ScreenshotViewerGUI extends JFrame {
    public ScreenshotViewerGUI() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        panelContent = new JPanel();
        buttonSelect = new JButton();
        labelDescription = new JLabel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Screenshot Viewer");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "60dlu, $lcgap, default:grow",
            "default:grow, $lgap, default"));

        //======== scrollPane1 ========
        {

            //======== panelContent ========
            {
                panelContent.setLayout(new GridLayout());
            }
            scrollPane1.setViewportView(panelContent);
        }
        contentPane.add(scrollPane1, cc.xywh(1, 1, 3, 1, CellConstraints.FILL, CellConstraints.FILL));

        //---- buttonSelect ----
        buttonSelect.setText("Select");
        contentPane.add(buttonSelect, cc.xy(1, 3));

        //---- labelDescription ----
        labelDescription.setText("text");
        contentPane.add(labelDescription, cc.xywh(3, 3, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    protected JPanel panelContent;
    protected JButton buttonSelect;
    protected JLabel labelDescription;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
