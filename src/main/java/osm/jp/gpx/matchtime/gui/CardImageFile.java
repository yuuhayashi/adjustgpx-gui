package osm.jp.gpx.matchtime.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

/**
 * [基準画像（開始/終了）]選択パネル
 * @author haya4
 */
public class CardImageFile extends Card  implements PanelAction {
	private static final long serialVersionUID = 1L;
	ParameterPanelImageFile arg_baseTimeImg;
    ParameterPanelTime arg_basetime;
    
    /**
     * コンストラクタ
     * @param tabbe parent panel
     * @param arg_basetime         	// 開始画像の基準時刻:
     * @param owner
     * @param text
     * @param pre
     * @param next
     */
    public CardImageFile(
            JTabbedPane tabbe, 
            ParameterPanelTime arg_basetime,
            Window owner,
            String text,
            int pre, int next
    ) {
        super(tabbe, text, pre, next);
        arg_basetime.setOwner(owner);
        this.arg_baseTimeImg = arg_basetime.getImageFile();
        this.arg_basetime = arg_basetime;
        
        JPanel argsPanel = new JPanel();
        argsPanel.setLayout(new BoxLayout(argsPanel, BoxLayout.PAGE_AXIS));
        argsPanel.add(packLine(new JLabel(i18n.getString("label.200")), new JPanel()));
        argsPanel.add(arg_baseTimeImg);
        
        JPanel separater = new JPanel();
        separater.setMinimumSize(new Dimension(40, 20));
        argsPanel.add(separater);

        argsPanel.add(packLine(new JLabel(i18n.getString("label.300")), new JPanel()));
        argsPanel.add(arg_basetime);
        
        // ラジオボタン: 「EXIF日時を基準にする」
        if (arg_basetime.exifBase != null) {
            argsPanel.add(arg_basetime.exifBase);
        }
        
        // ラジオボタン: 「File更新日時を基準にする」
        if (arg_basetime.fupdateBase != null) {
            argsPanel.add(arg_basetime.fupdateBase);
        }
        
        JPanel space = new JPanel();
        space.setMinimumSize(new Dimension(40, 20));
        space.setMaximumSize(new Dimension(40, Short.MAX_VALUE));
        argsPanel.add(space);
        
        this.mainPanel.add(argsPanel, BorderLayout.CENTER);
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
}
