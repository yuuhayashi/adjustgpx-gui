package osm.jp.gpx.matchtime.gui.card.perform;

import osm.jp.gpx.matchtime.gui.parameters.ParameterPanel;

/**
 * 『単純化(m)」パラメータを設定する為のパネル。正の整数
 * simplify distance (m) 
 */
@SuppressWarnings("serial")
public class ParameterPanelSimplify extends ParameterPanel
{
    public ParameterPanelSimplify(
    		String name,
            String label, 
            String text
    ) {
        super(name, label, text);
    }
    
    public int getSimplify() throws NumberFormatException {
    	int simplify = 0;
    	String text = this.argField.getText();
    	if (text == null) {
    		simplify = 0;
    	}
    	else {
        	if (text.isEmpty()) {
        		simplify = 0;
        	}
        	else {
    			simplify = Integer.parseInt(text);
        	}
    	}
        return simplify;
    }

	@Override
	public boolean isEnable() {
        String text = this.argField.getText();
        if (text == null) {
            return false;
        }
        try {
        	getSimplify();
			return true;
        }
        catch (Exception e) {
            return false;
        }
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
