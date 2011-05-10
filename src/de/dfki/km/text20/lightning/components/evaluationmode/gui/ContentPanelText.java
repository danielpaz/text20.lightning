/*
 * Created by JFormDesigner on Thu May 05 09:59:29 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.gui;

import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class ContentPanelText extends JPanel {
    public ContentPanelText() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        textPaneContent = new JTextPane();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setLayout(new FormLayout(
            "80dlu:grow",
            "default:grow"));
        add(textPaneContent, cc.xywh(1, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JTextPane textPaneContent;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

