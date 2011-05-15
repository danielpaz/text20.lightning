/*
 * Created by JFormDesigner on Thu May 12 12:08:11 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.precision.screenshotmarker.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class MarkerWindow extends JFrame {
    public MarkerWindow() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPaneContent = new JScrollPane();
        panelContent = new JPanel();
        panel1 = new JPanel();
        buttonSelect = new JButton();
        buttonRemove = new JButton();
        buttonSave = new JButton();
        labelDescription = new JLabel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Screenshot Marker");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default:grow",
            "default:grow, $lgap, default, $lgap, 1dlu"));

        //======== scrollPaneContent ========
        {

            //======== panelContent ========
            {
                panelContent.setLayout(new GridLayout(1, 1));
            }
            scrollPaneContent.setViewportView(panelContent);
        }
        contentPane.add(scrollPaneContent, cc.xywh(1, 1, 1, 1, CellConstraints.CENTER, CellConstraints.CENTER));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "1dlu, 3*($lcgap, 60dlu), $lcgap, default:grow",
                "default"));

            //---- buttonSelect ----
            buttonSelect.setText("Select");
            panel1.add(buttonSelect, cc.xy(3, 1));

            //---- buttonRemove ----
            buttonRemove.setText("Remove");
            panel1.add(buttonRemove, cc.xy(5, 1));

            //---- buttonSave ----
            buttonSave.setText("Save");
            panel1.add(buttonSave, cc.xy(7, 1));

            //---- labelDescription ----
            labelDescription.setText("text");
            panel1.add(labelDescription, cc.xy(9, 1));
        }
        contentPane.add(panel1, cc.xy(1, 3));
        setSize(535, 485);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JScrollPane scrollPaneContent;
    protected JPanel panelContent;
    private JPanel panel1;
    protected JButton buttonSelect;
    protected JButton buttonRemove;
    protected JButton buttonSave;
    protected JLabel labelDescription;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
