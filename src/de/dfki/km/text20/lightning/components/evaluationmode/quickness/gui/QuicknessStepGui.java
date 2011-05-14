/*
 * Created by JFormDesigner on Sat May 14 15:15:39 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.quickness.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
public class QuicknessStepGui extends JFrame {
    public QuicknessStepGui() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        panel1 = new JPanel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Quickness Mode");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "center:default:grow",
            "default:grow"));

        //======== scrollPane1 ========
        {

            //======== panel1 ========
            {
                panel1.setLayout(new GridLayout(1, 1));
            }
            scrollPane1.setViewportView(panel1);
        }
        contentPane.add(scrollPane1, cc.xywh(1, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JPanel panel1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
