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
 * このパネルはパラメータを表す縦配列 Y_AXIS のパネルの中に、
 * パラメータを示す横配列 X_AXIS のパネルを追加する。
 */
public abstract class ParameterPanel extends JPanel {
	private static final long serialVersionUID = 2L;

	public String propertyName;
    public JTextField argField;
    public JLabel argLabel;
    
    protected JPanel xPanel;    // パラメータ設定パネル(横配列)

    ParameterPanel() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.propertyName = "";
        this.argLabel = new JLabel();
        this.argField = new JTextField();
		
        this.xPanel = new JPanel();    // パラメータ設定パネル	(横配列)
        this.xPanel.setLayout(new BoxLayout(xPanel, BoxLayout.X_AXIS));
        this.xPanel.add(argLabel);
        this.xPanel.add(argField);
        this.xPanel.setMaximumSize(new Dimension(1920, 40));

        this.add(xPanel);
    }

    public ParameterPanel(String propertyName, String label, String text) {
        this();
        this.setName(propertyName);
        this.setLabel(label);
        this.setText(text);
    }
    
    public JPanel getInnerPanel() {
    	return this.xPanel;
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
