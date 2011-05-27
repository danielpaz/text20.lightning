/*
 * Created by JFormDesigner on Fri Mar 04 14:58:19 CET 2011
 */

package de.dfki.km.text20.lightning.components.evaluationmode.precision.evaluator.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class EvaluationWindow {
    public EvaluationWindow() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        this.mainFrame = new JFrame();
        this.dialogPane = new JPanel();
        this.labelDescription = new JLabel();
        this.checkBoxImages = new JCheckBox();
        this.labelDimension = new JLabel();
        this.spinnerDimension = new JSpinner();
        this.labelAmount = new JLabel();
        this.spinnerAmount = new JSpinner();
        this.scrollPane1 = new JScrollPane();
        this.listFiles = new JList();
        this.buttonSelect = new JButton();
        this.buttonRemove = new JButton();
        this.scrollPane2 = new JScrollPane();
        this.listDetectors = new JList();
        this.checkBoxConfiguration = new JCheckBox();
        this.buttonConfiguration = new JButton();
        this.separator1 = new JSeparator();
        this.buttonStart = new JButton();
        this.progressBar = new JProgressBar();

        //======== mainFrame ========
        {
            this.mainFrame.setTitle("Project Lightning (Desktop) Evaluator");
            this.mainFrame.setResizable(false);
            Container mainFrameContentPane = this.mainFrame.getContentPane();
            mainFrameContentPane.setLayout(new BorderLayout());

            //======== dialogPane ========
            {
                this.dialogPane.setBorder(Borders.DIALOG_BORDER);
                this.dialogPane.setLayout(new GridBagLayout());
                ((GridBagLayout)this.dialogPane.getLayout()).columnWidths = new int[] {80, 10, 80, 10, 80, 10, 80, 10, 80, 10, 80, 10, 80, 10, 80, 0};
                ((GridBagLayout)this.dialogPane.getLayout()).rowHeights = new int[] {0, 10, 20, 10, 100, 10, 0, 10, 100, 10, 0, 10, 5, 10, 0, 0};
                ((GridBagLayout)this.dialogPane.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout)this.dialogPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                //---- labelDescription ----
                this.labelDescription.setText("text");
                this.dialogPane.add(this.labelDescription, new GridBagConstraints(0, 0, 15, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- checkBoxImages ----
                this.checkBoxImages.setText("write images");
                this.dialogPane.add(this.checkBoxImages, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- labelDimension ----
                this.labelDimension.setText("Dimension");
                this.dialogPane.add(this.labelDimension, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- spinnerDimension ----
                this.spinnerDimension.setModel(new SpinnerNumberModel(200, 0, 50000, 1));
                this.dialogPane.add(this.spinnerDimension, new GridBagConstraints(6, 2, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- labelAmount ----
                this.labelAmount.setText("Amount");
                this.dialogPane.add(this.labelAmount, new GridBagConstraints(10, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- spinnerAmount ----
                this.spinnerAmount.setModel(new SpinnerNumberModel(1, 1, null, 1));
                this.dialogPane.add(this.spinnerAmount, new GridBagConstraints(11, 2, 4, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //======== scrollPane1 ========
                {

                    //---- listFiles ----
                    this.listFiles.setVisibleRowCount(3);
                    this.scrollPane1.setViewportView(this.listFiles);
                }
                this.dialogPane.add(this.scrollPane1, new GridBagConstraints(0, 4, 15, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- buttonSelect ----
                this.buttonSelect.setText("Select");
                this.dialogPane.add(this.buttonSelect, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- buttonRemove ----
                this.buttonRemove.setText("Remove");
                this.dialogPane.add(this.buttonRemove, new GridBagConstraints(4, 6, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //======== scrollPane2 ========
                {
                    this.scrollPane2.setViewportView(this.listDetectors);
                }
                this.dialogPane.add(this.scrollPane2, new GridBagConstraints(0, 8, 15, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- checkBoxConfiguration ----
                this.checkBoxConfiguration.setText("Configuration");
                this.dialogPane.add(this.checkBoxConfiguration, new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- buttonConfiguration ----
                this.buttonConfiguration.setText("Configuration");
                this.dialogPane.add(this.buttonConfiguration, new GridBagConstraints(4, 10, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
                this.dialogPane.add(this.separator1, new GridBagConstraints(0, 12, 15, 1, 0.0, 0.0,
                    GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- buttonStart ----
                this.buttonStart.setText("text");
                this.dialogPane.add(this.buttonStart, new GridBagConstraints(0, 14, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
                this.dialogPane.add(this.progressBar, new GridBagConstraints(4, 14, 11, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            mainFrameContentPane.add(this.dialogPane, BorderLayout.CENTER);
            this.mainFrame.setSize(505, 590);
            this.mainFrame.setLocationRelativeTo(this.mainFrame.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JFrame mainFrame;
    private JPanel dialogPane;
    protected JLabel labelDescription;
    protected JCheckBox checkBoxImages;
    protected JLabel labelDimension;
    protected JSpinner spinnerDimension;
    protected JLabel labelAmount;
    protected JSpinner spinnerAmount;
    private JScrollPane scrollPane1;
    protected JList listFiles;
    protected JButton buttonSelect;
    protected JButton buttonRemove;
    private JScrollPane scrollPane2;
    protected JList listDetectors;
    protected JCheckBox checkBoxConfiguration;
    protected JButton buttonConfiguration;
    private JSeparator separator1;
    protected JButton buttonStart;
    protected JProgressBar progressBar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
