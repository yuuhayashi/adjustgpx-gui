package osm.jp.gpx.matchtime.gui;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import osm.jp.gpx.AppParameters;

@SuppressWarnings("serial")
public class ParameterPanelOutput extends ParameterPanelFolder
{
    JCheckBox outputIMG;	// IMGの変換 する／しない
    JCheckBox outputIMG_all;	// 'out of GPX time'でもIMGの変換をする　{ON | OFF}
    JCheckBox exifON;		// EXIF 書き出しモード ／ !(EXIFの書き換えはしない)
    JCheckBox gpxOverwriteMagvar;	// ソースGPXの<MAGVAR>を無視する
    JCheckBox gpxOutputSpeed;	// GPXに<SPEED>を書き出す
    
    /**
     * コンストラクタ
     * ディレクトリのみ選択可能なダイアログ
     * @param label
     * @param text 
     */
    public ParameterPanelOutput(String name, String label, String text) {
        super(name, label, text, JFileChooser.DIRECTORIES_ONLY);
    }

    /**
     * チェックボックス "IMGの変換をする"
     * @param label         テキスト
     * @param params        プロパティ
     */
    public void addCheckChangeImage(String label, AppParameters params) {
        boolean selected = false;
        if (params.getProperty(AppParameters.IMG_OUTPUT).equals("true")) {
            selected = true;
        }
        outputIMG = new JCheckBox(label, selected);
    }
    
    /**
     * チェックボックス "GPXファイル時間外のファイルもコピーする"
     * @param label
     * @param params 
     */
    public void addCheckOutofGpxTime(String label, AppParameters params) {
        boolean selected = false;
        if (params.getProperty(AppParameters.IMG_OUTPUT_ALL).equals("true")) {
            selected = true;
        }
        outputIMG_all = new JCheckBox(label, selected);
    }
    
    /**
     * チェックボックス "EXIFの変換をする"
     * @param label
     * @param params 
     */
    public void addCheckOutputExif(String label, AppParameters params) {
        boolean selected = false;
        if (params.getProperty(AppParameters.IMG_OUTPUT_EXIF).equals("true")) {
            selected = true;
        }
        exifON = new JCheckBox(label, selected);
    }

    /**
     * チェックボックス "ソースGPXの＜MAGVAR＞を無視する"
     * @param label
     * @param params 
     */
    public void addCheckIgnoreMagvar(String label, AppParameters params) {
        boolean selected = false;
        if (params.getProperty(AppParameters.GPX_OVERWRITE_MAGVAR).equals("true")) {
            selected = true;
        }
        gpxOverwriteMagvar = new JCheckBox(label, selected);
        gpxOverwriteMagvar.setEnabled(true);
    }

    /**
     * チェックボックス "出力GPXに[SPEED]を上書きする"
     * @param label
     * @param params 
     */
    public void addCheckOutputSpeed(String label, AppParameters params) {
        boolean selected = false;
        if (params.getProperty(AppParameters.GPX_OUTPUT_SPEED).equals("true")) {
            selected = true;
        }
        gpxOutputSpeed = new JCheckBox(label, selected);
        gpxOutputSpeed.setEnabled(true);
    }

    /**
     * checkbox[IMG変換]を変更した場合のアクション
     * 	ON ー＞ IMG出力フォルダのフィールドを有効にする
     *  OFF -> IMG出力フォルダのフィールドを無効にする
     * @param event
     */
    class ChangeImageAction implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent event) {
            Object object = event.getSource();
            if (object == outputIMG) {
                setEnabled(outputIMG.isEnabled());
            }
        }
    }
}