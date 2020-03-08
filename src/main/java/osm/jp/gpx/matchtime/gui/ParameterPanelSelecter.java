package osm.jp.gpx.matchtime.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ParameterPanelSelecter extends JPanel implements ActionListener {
    public static final int ITEM_WIDTH_1 = 160;
    public static final int ITEM_WIDTH_2 = 240;
    public static final int LINE_WIDTH = ITEM_WIDTH_1 + ITEM_WIDTH_2;
    public static final int LINE_HEIGHT = 18;
    public JLabel label;
    public JComboBox<String> field;
    public String value;

    public ParameterPanelSelecter(String title, String[] items) {
        super(null);
        this.value = items[0];

        this.label = new JLabel(title, JLabel.RIGHT);
        this.label.setBounds(0, 0, ITEM_WIDTH_1 - 6, LINE_HEIGHT);
        add(this.label);

        this.field = new JComboBox<>();
        this.field.addActionListener(this);
        for (String item : items) {
            this.field.addItem(item);
        }
        this.field.setBounds(ITEM_WIDTH_1, 0, ITEM_WIDTH_2, LINE_HEIGHT);
        add(this.field);

        setPreferredSize(new Dimension(ITEM_WIDTH_1, LINE_HEIGHT));
    }
	
    @Override
    public void actionPerformed(ActionEvent e) {
        this.value = (String)this.field.getSelectedItem();
    }
}