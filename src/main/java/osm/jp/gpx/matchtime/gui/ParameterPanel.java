package osm.jp.gpx.matchtime.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

/**
 * パラメータを設定する為のパネル。
 * この１インスタンスで、１パラメータをあらわす。
 */
public abstract class ParameterPanel extends JPanel implements PropertyChangeListener {
    private static final long serialVersionUID = 4629824800747170556L;
    public String propertyName;
    public JTextField argField;
    public JLabel argLabel;
    public ResourceBundle i18n = ResourceBundle.getBundle("i18n");
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public ParameterPanel(String name, String label, String text) {
        this();
        this.setName(name);
        this.setLabel(label);
        this.setText(text);
    }

    ParameterPanel() {
        super();
        propertyName = "";
        argLabel = new JLabel();
        argField = new JTextField();
		
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setMaximumSize(new Dimension(1920, 40));
        this.add(argLabel);
        this.add(argField);
        
        // 'argField' ’が変更されたら、「update イベントを発火させる
        this.argField.getDocument().addDocumentListener(
            new SimpleDocumentListener() {
                @Override
                public void update(DocumentEvent e) {
                	pcs.firePropertyChange(getName(), "", argField.getText());
                }
            }
        );
    }

    public ParameterPanel setLabel(String label) {
    	this.argLabel.setText(label);
        return this;
    }

    public void addActionListener(ActionListener l) {
    	this.argField.addActionListener(l);
    }

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}
    
    public abstract boolean isEnable();
    
    @Override
    public void setName(String name) {
    	this.propertyName = name;
    }
    
    @Override
    public String getName() {
        return this.propertyName;
    }

    public void setText(String text) {
        this.argField.setText(text);
    }
    
    public String getText() {
        return this.argField.getText();
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
}
