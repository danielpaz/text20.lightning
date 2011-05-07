/*
 * Created by JFormDesigner on Sat May 07 16:54:56 CEST 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.gui;

import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class ContentPanelZero extends JPanel {
    public ContentPanelZero() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        labelPrecision = new JLabel();
        checkBoxPrecision = new JCheckBox();
        labelCoordinates = new JLabel();
        buttonSelectCoordinates = new JButton();
        textFieldPathCoordinates = new JTextField();
        labelDetector = new JLabel();
        checkBoxDetector = new JCheckBox();
        labelWarper = new JLabel();
        checkBoxWarper = new JCheckBox();
        labelText = new JLabel();
        buttonSelectText = new JButton();
        textFieldPathText = new JTextField();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setLayout(new FormLayout(
            "3dlu, 2*($lcgap, 120dlu)",
            "1dlu, 7*($lgap, default)"));

        //---- labelPrecision ----
        labelPrecision.setText("evaluate precision");
        add(labelPrecision, cc.xy(3, 3));
        add(checkBoxPrecision, cc.xy(5, 3));

        //---- labelCoordinates ----
        labelCoordinates.setText("choose PreparedCoordinates.xml");
        add(labelCoordinates, cc.xywh(3, 5, 1, 3));

        //---- buttonSelectCoordinates ----
        buttonSelectCoordinates.setText("Select");
        add(buttonSelectCoordinates, cc.xy(5, 5));
        add(textFieldPathCoordinates, cc.xy(5, 7));

        //---- labelDetector ----
        labelDetector.setText("ebaluate current SaliencyDetector");
        add(labelDetector, cc.xy(3, 9));
        add(checkBoxDetector, cc.xy(5, 9));

        //---- labelWarper ----
        labelWarper.setText("evaluate current MouseWarper");
        add(labelWarper, cc.xy(3, 11));
        add(checkBoxWarper, cc.xy(5, 11));

        //---- labelText ----
        labelText.setText("choose PreparedText.xml");
        add(labelText, cc.xywh(3, 13, 1, 3));

        //---- buttonSelectText ----
        buttonSelectText.setText("Select");
        add(buttonSelectText, cc.xy(5, 13));
        add(textFieldPathText, cc.xy(5, 15));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JLabel labelPrecision;
    protected JCheckBox checkBoxPrecision;
    protected JLabel labelCoordinates;
    protected JButton buttonSelectCoordinates;
    protected JTextField textFieldPathCoordinates;
    protected JLabel labelDetector;
    protected JCheckBox checkBoxDetector;
    protected JLabel labelWarper;
    protected JCheckBox checkBoxWarper;
    protected JLabel labelText;
    protected JButton buttonSelectText;
    protected JTextField textFieldPathText;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
