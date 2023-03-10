package osm.jp.gpx.matchtime.gui;

import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import osm.jp.gpx.matchtime.gui.parameters.PanelAction;
import osm.jp.gpx.matchtime.gui.parameters.ParameterPanelFolder;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

/**
 * [対象フォルダ]設定パネル
 * @author yuu
 */
public class CardSourceFolder extends Card implements PanelAction {
	private static final long serialVersionUID = -5496892696559069841L;
	ParameterPanelFolder arg_srcFolder;    // 対象フォルダ
    
    /**
     * コンストラクタ
     * @param tabbe parent panel
     * @param arg_srcFolder        対象フォルダ
     */
    public CardSourceFolder(JTabbedPane tabbe, ParameterPanelFolder arg_srcFolder) {
        super(tabbe, AdjustTerra.i18n.getString("tab.100"), -1, 1);
        this.arg_srcFolder = arg_srcFolder;
        this.mainPanel.add(new JLabel(i18n.getString("label.100")), BorderLayout.NORTH);

        JPanel argsPanel = new JPanel();    // パラメータ設定パネル	(上部)
        argsPanel.setLayout(new BoxLayout(argsPanel, BoxLayout.Y_AXIS));
        argsPanel.add(arg_srcFolder);
        this.mainPanel.add(argsPanel, BorderLayout.CENTER);
    }

    /**
     *  入力条件が満たされているかどうか
     * @return
     */
    @Override
    public boolean isEnable() {
       return this.arg_srcFolder.isEnable();
    }

    @Override
    public void openAction() {
       ; // 何もしない
    }
}
