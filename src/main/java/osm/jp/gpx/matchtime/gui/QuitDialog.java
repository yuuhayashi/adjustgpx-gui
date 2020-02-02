package osm.jp.gpx.matchtime.gui;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class QuitDialog extends JDialog implements WindowListener
{
    JButton yesButton;
    JButton noButton;
    JLabel label1;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public QuitDialog(JFrame parent, boolean modal) {
        super(parent, modal);
        
        ResourceBundle i18n = ResourceBundle.getBundle("i18n");

        addWindowListener((WindowListener) this);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setLayout(null);
        setSize(getInsets().left + getInsets().right + 337, getInsets().top + getInsets().bottom + 135);
        
        yesButton = new JButton(String.format("  %s  ", i18n.getString("dialog.quit")));
        yesButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent((Window)getParent(), 201));
                System.exit(0);
            }
        });
        yesButton.setBounds(getInsets().left + 72, getInsets().top + 80, 79, 22);
        yesButton.setFont(new Font("Dialog", 1, 12));
        add(yesButton);

        noButton = new JButton(i18n.getString("dialog.cancel"));
        noButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent(QuitDialog.this, WindowEvent.WINDOW_CLOSING));
                setVisible(false);
            }
        });
        noButton.setBounds(getInsets().left + 185, getInsets().top + 80, 99, 22);
        noButton.setFont(new Font("Dialog", 1, 12));
        add(noButton);
        
        label1 = new JLabel(i18n.getString("dialog.msg1"), JLabel.CENTER);
        label1.setBounds(78, 33, 180, 23);
        add(label1);
        setTitle(i18n.getString("dialog.msg1"));
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void setVisible(boolean b) {
        if(b) {
            Rectangle bounds = getParent().getBounds();
            Rectangle abounds = getBounds();
            setLocation(bounds.x + (bounds.width - abounds.width) / 2, bounds.y + (bounds.height - abounds.height) / 2);
        }
        super.setVisible(b);
    }


    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        setVisible(false);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        setVisible(false);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }
}
