package osm.jp.gpx.matchtime.gui.card.time;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.Card;
import osm.jp.gpx.matchtime.gui.card.source.ParameterPanelSourceFolder;
import osm.jp.gpx.matchtime.gui.parameters.PanelAction;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

/**
 * [基準画像（開始/終了）]選択パネル
 * @author haya4
 */
public class CardImageFile extends Card  implements PanelAction, PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	
    ParameterPanelImageFile arg_baseTimeImg;	// 開始画像ファイルパス
    ParameterPanelTime arg_basetime;			// 開始画像の基準時刻
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * コンストラクタ
     * @param tabbe parent panel
     * @param arg_basetime         	// 開始画像の基準時刻:
     * @param owner
     * @param text
     * @param pre
     * @param next
     */
    public CardImageFile(JTabbedPane tabbe, int pre, int next,
            ParameterPanelSourceFolder srcFolder, Window owner)
    {
        super(tabbe, AdjustTerra.i18n.getString("tab.300"), pre, next);
        
        // a. 基準時刻画像
        this.arg_baseTimeImg = new ParameterPanelImageFile(null, srcFolder);

        // b. 基準時刻:
        this.arg_basetime = new ParameterPanelTime(null, arg_baseTimeImg);
        this.arg_basetime.setOwner(owner);

        JPanel argsPanel = new JPanel();
        argsPanel.setLayout(new BoxLayout(argsPanel, BoxLayout.PAGE_AXIS));
        argsPanel.add(packLine(new JLabel(i18n.getString("label.200")), new JPanel()));
        argsPanel.add(this.arg_baseTimeImg);
        
        JPanel separater = new JPanel();
        separater.setMinimumSize(new Dimension(40, 20));
        argsPanel.add(separater);

        argsPanel.add(packLine(new JLabel(i18n.getString("label.300")), new JPanel()));
        argsPanel.add(arg_basetime);

		this.arg_baseTimeImg.addPropertyChangeListener(this);
		this.arg_basetime.addPropertyChangeListener(this);
        
        // ラジオボタン: 「EXIF日時を基準にする」
        if (this.arg_basetime.exifBase != null) {
            argsPanel.add(this.arg_basetime.exifBase);
        }
        
        // ラジオボタン: 「File更新日時を基準にする」
        if (this.arg_basetime.fupdateBase != null) {
            argsPanel.add(this.arg_basetime.fupdateBase);
        }
        
        JPanel space = new JPanel();
        space.setMinimumSize(new Dimension(40, 20));
        space.setMaximumSize(new Dimension(40, Short.MAX_VALUE));
        argsPanel.add(space);
        
        this.mainPanel.add(argsPanel, BorderLayout.CENTER);
    }
    
    public ParameterPanelTime getBaseTime() {
    	return this.arg_basetime;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    	this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
    	this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

    /**
     *  入力条件が満たされているかどうか
     * @return
     */
    @Override
    public boolean isEnable() {
       return (arg_baseTimeImg.isEnable() && arg_basetime.isEnable());
    }
    
    @Override
    public void openAction() {
       ; // 何もしない
    }
    
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (!arg_baseTimeImg.isEnable()) {
			arg_basetime.setText("");
		}
        // 「update イベント」を発火させる
    	this.propertyChangeSupport.firePropertyChange(getName(), arg_baseTimeImg.getText(), arg_basetime.getText());
	}
}
