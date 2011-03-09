/*
 * Created by JFormDesigner on Fri Mar 04 14:58:19 CET 2011
 */

package de.dfki.km.text20.lightning.evaluator.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class EvaluationWindow extends JFrame {
    public EvaluationWindow() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        labelDescription = new JLabel();
        checkBoxImages = new JCheckBox();
        checkBoxSummary = new JCheckBox();
        buttonSelect = new JButton();
        scrollPane1 = new JScrollPane();
        listFiles = new JList();
        buttonRemove = new JButton();
        scrollPane2 = new JScrollPane();
        listDetectors = new JList();
        buttonStart = new JButton();
        progressBar = new JProgressBar();

        //======== this ========
        setTitle("Project Lightning (Desktop) Evaluator");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new GridBagLayout());
            ((GridBagLayout)dialogPane.getLayout()).columnWidths = new int[] {70, 10, 55, 10, 70, 10, 55, 0};
            ((GridBagLayout)dialogPane.getLayout()).rowHeights = new int[] {0, 10, 0, 10, 20, 10, 0, 10, 87, 10, 0, 0};
            ((GridBagLayout)dialogPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout)dialogPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};

            //---- labelDescription ----
            labelDescription.setText("text");
            dialogPane.add(labelDescription, new GridBagConstraints(0, 0, 7, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //---- checkBoxImages ----
            checkBoxImages.setText("write images");
            dialogPane.add(checkBoxImages, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //---- checkBoxSummary ----
            checkBoxSummary.setText("write summary");
            dialogPane.add(checkBoxSummary, new GridBagConstraints(4, 2, 3, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //---- buttonSelect ----
            buttonSelect.setText("Select");
            dialogPane.add(buttonSelect, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //======== scrollPane1 ========
            {

                //---- listFiles ----
                listFiles.setVisibleRowCount(3);
                scrollPane1.setViewportView(listFiles);
            }
            dialogPane.add(scrollPane1, new GridBagConstraints(2, 4, 5, 3, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //---- buttonRemove ----
            buttonRemove.setText("Remove");
            dialogPane.add(buttonRemove, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //======== scrollPane2 ========
            {
                scrollPane2.setViewportView(listDetectors);
            }
            dialogPane.add(scrollPane2, new GridBagConstraints(0, 8, 7, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //---- buttonStart ----
            buttonStart.setText("text");
            dialogPane.add(buttonStart, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
            dialogPane.add(progressBar, new GridBagConstraints(2, 10, 5, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    protected JLabel labelDescription;
    protected JCheckBox checkBoxImages;
    protected JCheckBox checkBoxSummary;
    protected JButton buttonSelect;
    private JScrollPane scrollPane1;
    protected JList listFiles;
    protected JButton buttonRemove;
    private JScrollPane scrollPane2;
    protected JList listDetectors;
    protected JButton buttonStart;
    protected JProgressBar progressBar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
