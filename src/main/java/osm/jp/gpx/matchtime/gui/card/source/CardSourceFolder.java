package osm.jp.gpx.matchtime.gui.card.source;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import osm.jp.gpx.AppParameters;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.Card;
import osm.jp.gpx.matchtime.gui.parameters.PanelAction;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

/**
 * [対象フォルダ]設定パネル
 * @author yuu
 */
public class CardSourceFolder extends Card implements PanelAction {
	private static final long serialVersionUID = -5496892696559069841L;
	ParameterPanelSourceFolder arg_srcFolder;    // 対象フォルダ
    
    /**
     * コンストラクタ
     * @param tabbe parent panel
     * @param arg_srcFolder        対象フォルダ
     */
    public CardSourceFolder(JTabbedPane tabbe, int pre, int next) {
        super(tabbe, AdjustTerra.i18n.getString("tab.100"), pre, next);
        
        arg_srcFolder = new ParameterPanelSourceFolder(AdjustTerra.params.getProperty(AppParameters.IMG_SOURCE_FOLDER));

        this.mainPanel.add(new JLabel(i18n.getString("label.100")), BorderLayout.NORTH);

        JPanel argsPanel = new JPanel();    // パラメータ設定パネル	(上部)
        argsPanel.setLayout(new BoxLayout(argsPanel, BoxLayout.Y_AXIS));
        argsPanel.add(arg_srcFolder);
        this.mainPanel.add(argsPanel, BorderLayout.CENTER);
    }
    
    public ParameterPanelSourceFolder getSourceFolder() {
    	return this.arg_srcFolder;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.arg_srcFolder.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.arg_srcFolder.removePropertyChangeListener(listener);
	}

    /**
     *  入力条件が満たされているかどうか
     * @return
     */
    @Override
    public boolean isEnable() {
		return arg_srcFolder.isEnable();
    }

    @Override
    public void openAction() {
       ; // 何もしない
    }

}
