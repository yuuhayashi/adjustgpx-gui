package osm.jp.gpx.matchtime.gui.parameters;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import osm.jp.gpx.AppParameters;

@SuppressWarnings("serial")
public class ParameterPanelOutput extends ParameterPanelFolder
{
    public JCheckBox outputIMG;		// IMGの変換 する／しない
    public JCheckBox outputIMG_all;	// 'out of GPX time'でもIMGの変換をする　{ON | OFF}
    public JCheckBox exifON;			// EXIF 書き出しモード ／ !(EXIFの書き換えはしない)
    public JCheckBox gpxOverwriteMagvar;	// ソースGPXの<MAGVAR>を無視する
    public JCheckBox gpxOutputSpeed;	// GPXに<SPEED>を書き出す
    
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
        this.outputIMG = new JCheckBox(i18n.getString("label.510"), false);
        this.outputIMG_all = new JCheckBox(i18n.getString("label.520"), false);
        this.exifON = new JCheckBox(i18n.getString("label.540"), false);
        this.gpxOverwriteMagvar = new JCheckBox(i18n.getString("label.560"), false);
        this.gpxOutputSpeed = new JCheckBox(i18n.getString("label.570"), false);

        try {
            AppParameters params = new AppParameters();
            
            // チェックボックス "IMGの変換をする"
            if (params.getProperty(AppParameters.IMG_OUTPUT).equals("true")) {
            	this.outputIMG.setEnabled(true);
            }

            // チェックボックス "IMGの変換をする"
            if (params.getProperty(AppParameters.IMG_OUTPUT_ALL).equals("true")) {
            	this.outputIMG_all.setEnabled(true);
            }
            
            // チェックボックス "EXIFの変換をする"
            if (params.getProperty(AppParameters.IMG_OUTPUT_EXIF).equals("true")) {
            	this.exifON.setEnabled(true);
            }

            // チェックボックス "ソースGPXの<MAGVAR>を無視する"
            if (params.getProperty(AppParameters.GPX_OVERWRITE_MAGVAR).equals("true")) {
            	this.gpxOverwriteMagvar.setEnabled(true);
            }

            // チェックボックス "出力GPXに[SPEED]を上書きする"
            if (params.getProperty(AppParameters.GPX_OUTPUT_SPEED).equals("true")) {
            	this.gpxOutputSpeed.setEnabled(true);
            }
        }
        catch (Exception e) {}
    }
    
    public boolean isUpdateImages() {
    	return outputIMG.isSelected();
    }
    
    public boolean isUpdateImagesAtAll() {
    	return outputIMG_all.isSelected();
    }
    
    public boolean isUpdateExif() {
    	return exifON.isSelected();
    }
    
    public boolean isUpdateMagvar() {
    	return gpxOverwriteMagvar.isSelected();
    }
    
    public boolean isUpdateSpeed() {
    	return gpxOutputSpeed.isSelected();
    }
}