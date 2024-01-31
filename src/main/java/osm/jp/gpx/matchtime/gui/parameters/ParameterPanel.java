package osm.jp.gpx.matchtime.gui.parameters;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * パラメータを設定する為のパネル。
 * この１インスタンスで、１パラメータをあらわす。
 */
public abstract class ParameterPanel extends JPanel {
    private static final long serialVersionUID = 4629824800747170556L;
    public String propertyName;
    public JTextField argField;
    public JLabel argLabel;

    public ParameterPanel(String propertyName, String label, String text) {
        this();
        this.setName(propertyName);
        this.setLabel(label);
        this.setText(text);
    }

    ParameterPanel() {
        super();
        this.propertyName = "";
        this.argLabel = new JLabel();
        this.argField = new JTextField();
		
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setMaximumSize(new Dimension(1920, 40));
        this.add(argLabel);
        this.add(argField);
    }

    public ParameterPanel setLabel(String label) {
    	this.argLabel.setText(label);
        return this;
    }

    public void addActionListener(ActionListener l) {
    	this.argField.addActionListener(l);
    }

    public JPanel packLine(JComponent[] components, JPanel panel) {
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

    public JPanel packLine(JComponent component, JPanel panel) {
        List<JComponent> array = new ArrayList<>();
        array.add(component);
        return packLine(array.toArray(new JComponent[array.size()]), panel);
    }
    
    @Override
    public void setName(String name) {
    	this.propertyName = name;
    }
    
    @Override
    public String getName() {
        return this.propertyName;
    }

    public abstract boolean isEnable();
    
    /**
     * 'argField' ’が変更されたら、「update イベントを発火させる
     * 		pcs.firePropertyChange(this.propertyName, old, text);
     * 
     * @param text
     */
    public abstract void setText(String text);
    
    public abstract String getText();
}
