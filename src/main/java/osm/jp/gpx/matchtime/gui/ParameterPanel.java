package osm.jp.gpx.matchtime.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * パラメータを設定する為のパネル。
 * この１インスタンスで、１パラメータをあらわす。
 */
public abstract class ParameterPanel extends JPanel implements ParamAction {
    private static final long serialVersionUID = 4629824800747170556L;
    public JTextField argField;
    public JLabel argLabel;
    public ResourceBundle i18n = ResourceBundle.getBundle("i18n");

    public ParameterPanel(String label, String text) {
        this();
        this.setName(label);
        this.setText(text);
    }

    public ParameterPanel() {
        super();

        argLabel = new JLabel();
        argField = new JTextField();
		
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setMaximumSize(new Dimension(1920, 40));
        this.add(argLabel);
        this.add(argField);
    }

    public ParameterPanel setLabel(String label) {
        this.setName(label);
        return this;
    }

    public void addActionListener(ActionListener l) {
    	this.argField.addActionListener(l);
    }

    public abstract void addPropertyChangeListener(PropertyChangeListener listener);

    public abstract void removePropertyChangeListener(PropertyChangeListener listener);
    
    @Override
    public void setName(String name) {
    	this.argLabel.setText(name);
    }
    
    @Override
    public String getName() {
        return this.argLabel.getText();
    }

    @Override
    public void setText(String text) {
        this.argField.setText(text);
    }
    
    @Override
    public String getText() {
        return this.argField.getText();
    }
}
