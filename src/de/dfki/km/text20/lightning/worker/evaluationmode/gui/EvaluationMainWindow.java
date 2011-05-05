/*
 * Created by JFormDesigner on Thu May 05 09:47:37 CEST 2011
 */

package de.dfki.km.text20.lightning.worker.evaluationmode.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
public class EvaluationMainWindow extends JFrame {
    public EvaluationMainWindow() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        labelDescription = new JLabel();
        panelContent = new JPanel();
        buttonNext = new JButton();
        buttonCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Project Lightning (Desktop) Evaluation Mode");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default:grow, 2*($lcgap, 50dlu), $lcgap, default:grow",
            "default, $lgap, default:grow, $lgap, default"));

        //---- labelDescription ----
        labelDescription.setText("text");
        contentPane.add(labelDescription, cc.xywh(1, 1, 7, 1));

        //======== panelContent ========
        {
            panelContent.setLayout(new GridLayout());
        }
        contentPane.add(panelContent, cc.xywh(1, 3, 7, 1, CellConstraints.FILL, CellConstraints.FILL));

        //---- buttonNext ----
        buttonNext.setText("Next");
        contentPane.add(buttonNext, cc.xy(3, 5));

        //---- buttonCancel ----
        buttonCancel.setText("Cancel");
        contentPane.add(buttonCancel, cc.xy(5, 5));
        setSize(400, 320);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JLabel labelDescription;
    protected JPanel panelContent;
    protected JButton buttonNext;
    protected JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
