package osm.jp.gpx.matchtime.gui.card.gpxfolder;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
 * [GPXファイル]選択パネル
 * @author haya4
 */
public class CardGpxFile extends Card  implements PanelAction {
	private static final long serialVersionUID = -6130918418152241845L;
	
	ParameterPanelGpx gpxSourceFolder;	// GPX_SOURCE_FOLDER
	
    /**
     * コンストラクタ
     * @param tabbe parent panel
     * @param pre
     * @param next
     */
    public CardGpxFile(JTabbedPane tabbe, int pre, int next) {
        super(tabbe, AdjustTerra.i18n.getString("tab.400"), pre, next);
        
        // 1-1. GPXファイル選択パラメータ
        this.gpxSourceFolder = new ParameterPanelGpx(AdjustTerra.params.getProperty(AppParameters.GPX_SOURCE_FOLDER));

        // 1-2. ヒモ付を行うGPXファイルを選択してください。
        //    - フォルダを指定すると、フォルダ内のすべてのGPXファイルを対象とします。
        JPanel argsPanel = new JPanel();
        argsPanel.setLayout(new BoxLayout(argsPanel, BoxLayout.PAGE_AXIS));
        argsPanel.add(packLine(new JLabel(i18n.getString("label.400")), new JPanel()));
        argsPanel.add(gpxSourceFolder);
        argsPanel.add(gpxSourceFolder.getNoFirstNode());
        
        JPanel space = new JPanel();
        space.setMinimumSize(new Dimension(40, 20));
        space.setMaximumSize(new Dimension(40, Short.MAX_VALUE));
        argsPanel.add(space);
        
        this.mainPanel.add(argsPanel, BorderLayout.CENTER);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.gpxSourceFolder.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.gpxSourceFolder.removePropertyChangeListener(listener);
	}

	public ParameterPanelGpx getGpxFile() {
    	return this.gpxSourceFolder;
    }

    /**
     *  入力条件が満たされているかどうか
     * @return
     */
    @Override
    public boolean isEnable() {
       return gpxSourceFolder.isEnable();
    }
    
    @Override
    public void openAction() {
       ; // 何もしない
    }
}
