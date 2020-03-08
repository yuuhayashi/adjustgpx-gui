package osm.jp.gpx.matchtime.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

/**
 * [GPXファイル]選択パネル
 * @author haya4
 */
public class CardGpxFile extends Card  implements PanelAction {
	private static final long serialVersionUID = -6130918418152241845L;
	ParameterPanelGpx arg_gpxFile;
    
    /**
     * コンストラクタ
     * @param tabbe parent panel
     * @param arg_gpxFile         	// 開始画像の基準時刻:
     * @param text
     * @param pre
     * @param next
     */
    public CardGpxFile(
            JTabbedPane tabbe, 
            ParameterPanelGpx arg_gpxFile,
            String text,
            int pre, int next
    ) {
        super(tabbe, text, pre, next);
        this.arg_gpxFile = arg_gpxFile;
        
        // 4. ヒモ付を行うGPXファイルを選択してください。
        //    - フォルダを指定すると、フォルダ内のすべてのGPXファイルを対象とします。
        JPanel argsPanel = new JPanel();
        argsPanel.setLayout(new BoxLayout(argsPanel, BoxLayout.PAGE_AXIS));
        argsPanel.add(packLine(new JLabel(i18n.getString("label.400")), new JPanel()));
        argsPanel.add(arg_gpxFile);
        
        // "セグメント'trkseg'の最初の１ノードは無視する。"
        if (arg_gpxFile.noFirstNode != null) {
            argsPanel.add(arg_gpxFile.noFirstNode);
        }

        // "生成されたGPXファイル（ファイル名が'_.gpx'で終わるもの）も変換の対象にする"
        if (arg_gpxFile.gpxReuse != null) {
            argsPanel.add(arg_gpxFile.gpxReuse);
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
       return (arg_gpxFile.isEnable());
    }
    
    @Override
    public void openAction() {
       ; // 何もしない
    }
}
