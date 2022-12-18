package osm.jp.gpx.matchtime.gui.parameters;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import osm.jp.gpx.AppParameters;

@SuppressWarnings("serial")
public class ParameterPanelOutput extends ParameterPanelFolder
{
    //public JCheckBox outputIMG;		// IMGの変換 する／しない
    //public JCheckBox outputIMG_all;	// 'out of GPX time'でもIMGの変換をする　{ON | OFF}
    //public JCheckBox exifON;			// EXIF 書き出しモード ／ !(EXIFの書き換えはしない)
    public JCheckBox gpxOverwriteMagvar;	// ソースGPXの<MAGVAR>を無視する
    public JCheckBox gpxOutputSpeed;	// GPXに<SPEED>を書き出す
    public ParameterPanelSimplify simplifyMeters;	// 「単純化(m)」正の整数 TEXT
    
    /**
     * コンストラクタ
     * ディレクトリのみ選択可能なダイアログ
     * @param label
     * @param text 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public ParameterPanelOutput(String name, String label, String text) {
        super(name, label, text, JFileChooser.DIRECTORIES_ONLY);
        
        //---- CENTER -----
        this.gpxOverwriteMagvar = new JCheckBox(i18n.getString("label.560"), false);
        this.gpxOutputSpeed = new JCheckBox(i18n.getString("label.570"), false);
        this.simplifyMeters = new ParameterPanelSimplify("simplify", i18n.getString("label.580"), "3");

        try {
            AppParameters params = new AppParameters();
            
            // チェックボックス "ソースGPXの<MAGVAR>を無視する"
            if (params.getProperty(AppParameters.GPX_OVERWRITE_MAGVAR).equals("true")) {
            	this.gpxOverwriteMagvar.setEnabled(true);
            }

            // チェックボックス "出力GPXに[SPEED]を上書きする"
            if (params.getProperty(AppParameters.GPX_OUTPUT_SPEED).equals("true")) {
            	this.gpxOutputSpeed.setEnabled(true);
            }

            // TEXTボックス "単純化(m)"
            if (params.getProperty(AppParameters.SIMPLIFY_METERS) != null) {
            	String str = params.getProperty(AppParameters.SIMPLIFY_METERS);
                if (str.isEmpty()) {
                	this.simplifyMeters.setText("3");
                	this.simplifyMeters.setEnabled(true);
                }
                else {
                	this.simplifyMeters.setText(str);
                	this.simplifyMeters.setEnabled(true);
                }
            }
        }
        catch (Exception e) {}
    }
    
    public boolean isUpdateMagvar() {
    	return gpxOverwriteMagvar.isSelected();
    }
    
    public boolean isUpdateSpeed() {
    	return gpxOutputSpeed.isSelected();
    }
    
    public int getSimplify() {
    	try {
    		return this.simplifyMeters.getSimplify();
    	}
    	catch (NumberFormatException e) {
    		return 0;
    	}
    }
}