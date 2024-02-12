package osm.jp.gpx.matchtime.gui.card.perform;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

import javax.swing.JCheckBox;

import osm.jp.gpx.AppParameters;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.parameters.ParameterPanelWithComment;

/**
 * 『省略距離(m)」パラメータを設定する為のパネル。正の整数
 * simplify distance (m) 
 */
@SuppressWarnings("serial")
public class ParameterPanelSimplify extends ParameterPanelWithComment
{
    public JCheckBox gpxOverwriteMagvar;	// ソースGPXの<MAGVAR>を無視する
    public JCheckBox gpxOutputSpeed;	// GPXに<SPEED>を書き出す
	
    public ParameterPanelSimplify() {
        super(AppParameters.SIMPLIFY_METERS, i18n.getString("label.580") + ": ", "0");
        
        // 初期値の設定
        String str = AdjustTerra.params.getProperty(AppParameters.SIMPLIFY_METERS);
        if ((str != null) && !str.isEmpty()) {
        	this.setText(str);
        }
        
        this.gpxOverwriteMagvar = new JCheckBox(i18n.getString("label.560"), false);
        this.gpxOutputSpeed = new JCheckBox(i18n.getString("label.570"), false);

        try {
            // チェックボックス "ソースGPXの<MAGVAR>を無視する"
            if (AdjustTerra.params.getProperty(AppParameters.GPX_OVERWRITE_MAGVAR).equals("true")) {
            	this.gpxOverwriteMagvar.setEnabled(true);
            }

            // チェックボックス "出力GPXに[SPEED]を上書きする"
            if (AdjustTerra.params.getProperty(AppParameters.GPX_OUTPUT_SPEED).equals("true")) {
            	this.gpxOutputSpeed.setEnabled(true);
            }

        }
        catch (Exception e) {}
        
        this.add(gpxOverwriteMagvar);
        this.add(gpxOutputSpeed);
    }
    
    public double	 getSimplify() throws NumberFormatException {
    	double simplify = 0;
    	String text = this.argField.getText();
    	if (text == null) {
    		simplify = 0;
    	}
    	else {
        	if (text.isEmpty()) {
        		simplify = 0;
        	}
        	else {
    			simplify = Double.parseDouble(text);
        	}
    	}
        return simplify;
    }
    
    public boolean isUpdateMagvar() {
    	return gpxOverwriteMagvar.isSelected();
    }
    
    public boolean isUpdateSpeed() {
    	return gpxOutputSpeed.isSelected();
    }
    
	@Override
	public boolean isEnable() {
        String text = this.argField.getText();
        if (text == null) {
			// 'Simplify' is NULL.
			this.setComment(i18n.getString("msg.581"), false);
            return false;
        }
        if (text.isEmpty()) {
			// 'Simplify' is empty.
			this.setComment(i18n.getString("msg.582"), false);
            return false;
        }
        try {
        	double simplify = getSimplify();
        	if (simplify < 0) {
    			this.setComment(i18n.getString("msg.583"), false);
    			return false;
        	}
			this.setComment(i18n.getString("msg.580"), true);
			return true;
        }
        catch (Exception e) {
			// 'Simplify' is not Number format.
			this.setComment(i18n.getString("msg.585"), false);
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
