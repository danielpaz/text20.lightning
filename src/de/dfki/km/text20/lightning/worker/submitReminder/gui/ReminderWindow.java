/*
 * Created by JFormDesigner on Tue Apr 05 09:13:54 CEST 2011
 */

package de.dfki.km.text20.lightning.worker.submitReminder.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Ralf Biedert
 */
@SuppressWarnings("all")
public class ReminderWindow {
    public ReminderWindow() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        mainFrame = new JFrame();
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        labelText = new JLabel();
        buttonYes = new JButton();
        buttonNo = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== mainFrame ========
        {
            mainFrame.setTitle("Project Lightning (Desktop)");
            mainFrame.setResizable(false);
            Container mainFrameContentPane = mainFrame.getContentPane();
            mainFrameContentPane.setLayout(new BorderLayout());

            //======== dialogPane ========
            {
                dialogPane.setBorder(Borders.DIALOG_BORDER);
                dialogPane.setLayout(new BorderLayout());

                //======== contentPanel ========
                {
                    contentPanel.setLayout(new FormLayout(
                        "default:grow, 2*($lcgap, 35dlu), $lcgap, default:grow",
                        "default:grow, $lgap, default"));

                    //---- labelText ----
                    labelText.setText("text");
                    contentPanel.add(labelText, cc.xywh(1, 1, 7, 1));

                    //---- buttonYes ----
                    buttonYes.setText("Yes");
                    contentPanel.add(buttonYes, cc.xy(3, 3));

                    //---- buttonNo ----
                    buttonNo.setText("No");
                    contentPanel.add(buttonNo, cc.xy(5, 3));
                }
                dialogPane.add(contentPanel, BorderLayout.CENTER);
            }
            mainFrameContentPane.add(dialogPane, BorderLayout.CENTER);
            mainFrame.pack();
            mainFrame.setLocationRelativeTo(mainFrame.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JFrame mainFrame;
    private JPanel dialogPane;
    private JPanel contentPanel;
    protected JLabel labelText;
    protected JButton buttonYes;
    protected JButton buttonNo;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
