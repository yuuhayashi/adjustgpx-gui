package osm.jp.gpx.matchtime.gui.card.time;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import osm.jp.gpx.AppParameters;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.parameters.ParameterPanel;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.dfjp;
import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

/**
 * パラメータを設定する為のパネル。
 * この１インスタンスで、１パラメータをあらわす。
 */
public class ParameterPanelTime extends ParameterPanel {
	private static final long serialVersionUID = 1683226418990348336L;
	static SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateTimeInstance();
    public static SimpleDateFormat exifDateTime = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    ParameterPanelImageFile imageFile;  // 基準時刻画像    
    
    // 基準時刻の指定グループ (排他選択)
    public ButtonGroup baseTimeGroup = new ButtonGroup();
    public JRadioButton exifBase = null;       // EXIF日時を基準にする／ !(ファイル更新日時を基準にする)
    public JRadioButton fupdateBase = null;    // File更新日時を基準にする／ !(EXIF日時を基準にする)
    
    public JButton updateButton;
    public JButton resetButton;
    Window owner;

    public ParameterPanelTime(String text, ParameterPanelImageFile imageFile) {
        super(AppParameters.GPX_BASETIME, i18n.getString("label.310"), text);
        
        this.imageFile = imageFile;
        this.imageFile.addPropertyChangeListener(new BaseTimeImgUpdateAction());
        
        // "ボタン[変更...]"
        UpdateButtonAction buttonAction = new UpdateButtonAction(this);
        updateButton = new JButton(i18n.getString("button.update"));
        updateButton.addActionListener(buttonAction);
        this.add(updateButton);
        
        // "ボタン[再設定...]"
        ResetButtonAction resetAction = new ResetButtonAction(this);
        resetButton = new JButton(i18n.getString("button.reset"));
        resetButton.addActionListener(resetAction);
        resetButton.setVisible(false);
        this.add(resetButton);
    }
    
    public ParameterPanelTime setOwner(Window owner) {
        this.owner = owner;
        return this;
    }

    /**
     * 「EXIFの日時を基準にする」
     * @param label         テキスト
     * @param params        プロパティ
     */
    public void addExifBase(String label) {
        boolean selected = false;
        if (AdjustTerra.params.getProperty(AppParameters.GPX_BASETIME).equals("EXIF_TIME")) {
            selected = true;
        }
        exifBase = new JRadioButton(label, selected);
        baseTimeGroup.add(exifBase);
    }

    /**
     * 「File更新日時を基準にする」
     * @param label         テキスト
     * @param params        プロパティ
     */
    public void addFileUpdate(String label) {
        boolean selected = false;
        if (AdjustTerra.params.getProperty(AppParameters.GPX_BASETIME).equals("FILE_UPDATE")) {
            selected = true;
        }
        fupdateBase = new JRadioButton(label, selected);
        baseTimeGroup.add(fupdateBase);
    }
    
    public ParameterPanelImageFile getImageFile() {
        return this.imageFile;
    }

    /**
     * Action : Update 'arg2_baseTimeImg'
     * 
     */
    class BaseTimeImgUpdateAction implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			try {
				String timeStr = getTimeStr();
				argField.setText(timeStr);
			} catch (Exception e) {
				argField.setText("Error : "+ e.toString());
			}
		}
    }
    
    /**
     * [変更...]ボタンのアクション
     */
    class UpdateButtonAction implements java.awt.event.ActionListener
    {
        ParameterPanelTime param;
        
        public UpdateButtonAction(ParameterPanelTime param) {
            this.param = param;
        }
        
        public void actionPerformed(ActionEvent e) {
            fileSelect_Action(param);
            (new DialogCorectTime(param, owner)).setVisible(true);
        }
    }
    
    /**
     * [再設定...]ボタンのアクション
     */
    class ResetButtonAction implements java.awt.event.ActionListener
    {
        ParameterPanelTime paramPanelTime;
        
        public ResetButtonAction(ParameterPanelTime param) {
            this.paramPanelTime = param;
        }
        
        public void actionPerformed(ActionEvent e) {
            fileSelect_Action(paramPanelTime);
        }
    }
    
    /**
     * 画像ファイルが選択されたときのアクション
     * １．ラジオボタンの選択を参照してTEXTフィールドにファイルの「日時」を設定する
     * @param param
     */
    void fileSelect_Action(ParameterPanelTime param) {
    	param.argField.setText(getTimeStr());
    }
    
    String getTimeStr() {
		if (!imageFile.isEnable()) {
			return "Error : ImageFile is not selected.";
		}
		
        File timeFile = imageFile.getImageFile();

        // Radio Selector
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss z");
        if ((exifBase != null) && exifBase.isSelected()) {
            try {
                ImageMetadata meta = Imaging.getMetadata(timeFile);
                JpegImageMetadata jpegMetadata = (JpegImageMetadata)meta;
                if (jpegMetadata != null) {
                    TiffImageMetadata exif = jpegMetadata.getExif();
                    if (exif != null) {
                        String dateTimeOriginal = exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)[0];
                        long lastModifyTime = exifDateTime.parse(dateTimeOriginal).getTime();
                        return (dfjp.format(new Date(lastModifyTime)));
                    }
                    else {
                        return ("Error : 'ExIF is null!'");
                    }
                }
                else {
                	return "Error : ";
                }
            }
            catch (IOException | ParseException | ImageReadException ex) {
            	return "Error : "+ ex.toString();
            }
        }
        else {
            long lastModified = timeFile.lastModified();
            return (sdf.format(new Date(lastModified)));
        }
    }
    
    @Override
    public boolean isEnable() {
		if (!this.imageFile.isEnable()) {
			return false;
		}
		
    	String text = this.argField.getText();
        if (text == null) {
        	return false;
        }
        return isValid(text);
    }
    
    /**
     * 時刻フォーマットに適合しているか判定する
     * @param str
     * @return
     */
    public static boolean isValid(String str) {
		if (str != null) {
            try {
                sdf.applyPattern("yyyy-MM-dd HH:mm:ss z");
                sdf.parse(str);
                return true;
            }
            catch (ParseException ex) {
            	return false;
            }
		}
		return false;
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
