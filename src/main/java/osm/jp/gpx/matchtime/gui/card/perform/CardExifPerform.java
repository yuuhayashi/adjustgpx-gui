package osm.jp.gpx.matchtime.gui.card.perform;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import osm.jp.gpx.AppParameters;
import osm.jp.gpx.ImportPicture;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.Card;
import osm.jp.gpx.matchtime.gui.card.gpxfolder.ParameterPanelGpx;
import osm.jp.gpx.matchtime.gui.card.source.ParameterPanelSourceFolder;
import osm.jp.gpx.matchtime.gui.card.time.ParameterPanelImageFile;
import osm.jp.gpx.matchtime.gui.card.time.ParameterPanelTime;
import osm.jp.gpx.matchtime.gui.parameters.PanelAction;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.dfjp;
import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

/**
 * 実行パネル
 * @author yuu
 */
public class CardExifPerform extends Card implements PanelAction, PropertyChangeListener {
	private static final long serialVersionUID = 8902284630791931118L;
	ParameterPanelTime arg_basetime;        // 画像の基準時刻:
    ParameterPanelGpx arg_gpxFile;          // GPX file or Folder
    ParameterPanelOutput arg_output;        // EXIF & 書き出しフォルダ
    JButton doButton;       // [処理実行]ボタン
    
    /**
     * コンストラクタ
     * @param tabbe parent panel
     * @param pre
     * @param next
     * @param arg_basetime         	// 開始画像の基準時刻:
     * @param arg_gpxFile         	// GPX file or Folder:
     */
    public CardExifPerform(JTabbedPane tabbe, int pre, int next,
            ParameterPanelTime arg_basetime, ParameterPanelGpx arg_gpxFile)
    {
        super(tabbe, AdjustTerra.i18n.getString("tab.500"), pre, next);
        this.arg_basetime = arg_basetime;
        this.arg_gpxFile = arg_gpxFile;
        
        SymAction lSymAction = new SymAction();
        JPanel argsPanel = new JPanel();
        argsPanel.setLayout(new BoxLayout(argsPanel, BoxLayout.PAGE_AXIS));

        // 4. ファイル変換・実行パラメータ
        // "出力フォルダ: "
        arg_output = new ParameterPanelOutput(AdjustTerra.params.getProperty(AppParameters.IMG_OUTPUT_FOLDER));
        arg_output.addPropertyChangeListener(this);


        // 5. EXIF変換を行うかどうかを選択してください。
        //    - EXIF変換を行う場合には、変換ファイルを出力するフォルダも指定する必要があります。
        //    - 出力フォルダには、書き込み権限と、十分な空き容量が必要です。
        JLabel label5 = new JLabel();
        label5.setText(
            String.format(
                "<html><p>5. %s</p><ul><li>%s</li><li>%s</li></ul>",
                i18n.getString("label.500"),
                i18n.getString("label.501"),
                i18n.getString("label.502")
            )
        );
        argsPanel.add(packLine(label5, new JPanel()));
        
        // 出力フォルダ
        argsPanel.add(arg_output);
        argsPanel.add(arg_output.gpxOverwriteMagvar);
        argsPanel.add(arg_output.gpxOutputSpeed);
        argsPanel.add(arg_output.simplifyMeters);

        // [処理実行]ボタン
        doButton = new JButton(
            i18n.getString("button.execute"),
            AdjustTerra.createImageIcon("/images/media_playback_start.png")
        );
        argsPanel.add(doButton);
                
        this.mainPanel.add(argsPanel, BorderLayout.CENTER);

        //{{REGISTER_LISTENERS
        doButton.addActionListener(lSymAction);
        //}}
    }
    
    class SymAction implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent event) {
            Object object = event.getSource();
            if (object == doButton) {
            	doButton_Action(event);
            }
        }
    }
    
    /**
     * [実行]ボタンをクリックしたときの動作
     * @param event
     */
    void doButton_Action(java.awt.event.ActionEvent event) {
    	doButton.setEnabled(false);
        
        ParameterPanelImageFile arg_baseTimeImg = arg_basetime.getImageFile();  // 基準時刻画像
        ParameterPanelSourceFolder arg_srcFolder = arg_baseTimeImg.paramDir;
        
        try {
            AppParameters params = new AppParameters();

            params.setProperty(AppParameters.GPX_NO_FIRST_NODE, String.valueOf(arg_gpxFile.isNoFirstNodeSelected()));
            params.setProperty(AppParameters.GPX_SOURCE_FOLDER, arg_gpxFile.getText());
            if ((arg_basetime.exifBase != null) && arg_basetime.exifBase.isSelected()) {
                params.setProperty(AppParameters.GPX_BASETIME, "EXIF_TIME");
            }
            else {
                params.setProperty(AppParameters.GPX_BASETIME, "FILE_UPDATE");
            }
            params.setProperty(AppParameters.IMG_SOURCE_FOLDER, arg_srcFolder.getText());
            params.setProperty(AppParameters.IMG_BASE_FILE, arg_baseTimeImg.getText());
            params.setProperty(AppParameters.IMG_TIME, ImportPicture.toUTCString(dfjp.parse(arg_basetime.getText())));
            
            params.setProperty(AppParameters.IMG_OUTPUT_FOLDER, arg_output.getText());

            params.setProperty(AppParameters.GPX_OVERWRITE_MAGVAR, String.valueOf(arg_output.isUpdateMagvar()));
            params.setProperty(AppParameters.GPX_OUTPUT_SPEED, String.valueOf(arg_output.isUpdateSpeed()));
            params.setProperty(AppParameters.SIMPLIFY_METERS, String.valueOf(arg_output.getSimplify()));
            params.store();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        (new DoDialog(new String[0])).setVisible(true);
		
    	doButton.setEnabled(true);
    }

    /**
     *  入力条件が満たされているかどうか
     * @return
     */
    @Override
    public boolean isEnable() {
       return (arg_basetime.isEnable() && arg_gpxFile.isEnable());
    }
    
    @Override
    public void openAction() {
       ; // 何もしない
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
        Object eventTriggerObject = evt.getSource();
        String propertyName = evt.getPropertyName();

        if (ParameterPanelOutput.class.isInstance(eventTriggerObject)) {
            if (propertyName.equals(AppParameters.IMG_OUTPUT_FOLDER)) {
                Integer oldValue = (Integer) evt.getOldValue();
                Integer newValue = (Integer) evt.getNewValue();
                System.out.println("[IMG_OUTPUT_FOLDER] oldValue:" + oldValue + " -> newValue:" + newValue);
            	this.firePropertyChange(AppParameters.IMG_OUTPUT_FOLDER, evt.getNewValue(), evt.getNewValue());
            }
        }
	}
}
