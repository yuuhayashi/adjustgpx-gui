package osm.jp.gpx.matchtime.gui.parameters;

import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class ParameterPanelCheckbox extends JCheckBox
{
	String paramname;
    
    public ParameterPanelCheckbox(String paramname, String label, boolean enable) {
        super(label, enable);
        this.paramname = paramname;
    }
}