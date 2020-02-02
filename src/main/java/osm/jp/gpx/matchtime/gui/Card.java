package osm.jp.gpx.matchtime.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

public class Card extends JPanel {
    JTabbedPane tabbe;
    public JPanel mainPanel;
    String title;
    int backNumber = -1;
    int nextNumber = -1;
    public JButton nextButton;     // [次へ]ボタン
    public JButton backButton;     // [戻る]ボタン
    
    public Card(JTabbedPane tabbe, String title, int backNumber, int nextNumber) {
        super();
        this.tabbe = tabbe;
        this.title = title;
        this.backNumber = backNumber;
        this.nextNumber = nextNumber;

        // INIT_CONTROLS
        this.setLayout(new BorderLayout());
        
        //---- CENTER -----
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);
        
        //---- SOUTH -----
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        buttonPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.SOUTH);
        
        //{{REGISTER_LISTENERS
        SymAction lSymAction = new SymAction();
        if (nextNumber >= 0) {
            nextButton = new JButton(i18n.getString("button.next"));
            nextButton.setEnabled(false);
            buttonPanel.add(nextButton, BorderLayout.EAST);
            nextButton.addActionListener(lSymAction);
        }

        if (backNumber >= 0) {
            backButton = new JButton(i18n.getString("button.previous"));
            backButton.setEnabled(false);
            buttonPanel.add(backButton, BorderLayout.WEST);
            backButton.addActionListener(lSymAction);
        }
        //}}
    }
    
    public static JPanel packLine(JComponent[] components, JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        int max = 0;
        for (JComponent component : components) {
            panel.add(component);
            Dimension size = component.getMaximumSize();
            if (max < size.height) {
                max = size.height;
            }
        }
        Dimension size = new Dimension();
        size.width = Short.MAX_VALUE;
        size.height = max;
        panel.setMaximumSize(size);
        return panel;
    }

    public static JPanel packLine(JComponent component, JPanel panel) {
        ArrayList<JComponent> array = new ArrayList<>();
        array.add(component);
        return packLine(array.toArray(new JComponent[array.size()]), panel);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.tabbe.setEnabledAt(nextNumber - 1, enabled);
    }
    
    public String getTitle() {
        return this.title;
    }

    class SymAction implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent event) {
            Object object = event.getSource();
            if (object == nextButton) {
            	nextButton_Action(event);
            }
            else if (object == backButton) {
            	backButton_Action(event);
            }
        }
    }
    
    /**
     * [次へ]ボタンをクリックした時の動作
     * @param event 
     */
    void nextButton_Action(ActionEvent event) {
        this.tabbe.setSelectedIndex(this.nextNumber);
    }

    /**
     * [戻る]ボタンをクリックした時の動作
     * @param event
     */
    void backButton_Action(ActionEvent event) {
        this.tabbe.setSelectedIndex(this.backNumber);
    }
}
