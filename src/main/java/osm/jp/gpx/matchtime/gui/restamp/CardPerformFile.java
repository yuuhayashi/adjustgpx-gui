package osm.jp.gpx.matchtime.gui.restamp;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;
import osm.jp.gpx.matchtime.gui.Card;
import osm.jp.gpx.matchtime.gui.PanelAction;
import osm.jp.gpx.matchtime.gui.ParameterPanelTime;

/**
 * [基準画像（開始/終了）]選択パネル
 * @author yuu
 */
public class CardPerformFile extends Card  implements PanelAction {
	private static final long serialVersionUID = -4796133437768564759L;
    ParameterPanelTime arg1_basetime;
    ParameterPanelTime arg2_basetime;
    JButton doButton;       // [処理実行]ボタン
    
    /**
     * コンストラクタ
     * @param tabbe parent panel
     * @param arg1_basetime         	// 開始画像の基準時刻:
     * @param arg2_basetime         	// 開始画像の基準時刻:
     */
    public CardPerformFile(
            JTabbedPane tabbe,
            ParameterPanelTime arg1_basetime,
            ParameterPanelTime arg2_basetime
    ) {
        super(tabbe, AdjustTerra.i18n.getString("tab.restamp.400"), 2, 4);
        this.arg1_basetime = arg1_basetime;
        this.arg2_basetime = arg2_basetime;
        
        JPanel argsPanel = new JPanel();
        argsPanel.setLayout(new BoxLayout(argsPanel, BoxLayout.PAGE_AXIS));
        argsPanel.add(packLine(new JLabel(i18n.getString("label.200")), new JPanel()));

        // [処理実行]ボタン
        doButton = new JButton(
            i18n.getString("button.execute"),
            AdjustTerra.createImageIcon("/images/media_playback_start.png")
        );
        argsPanel.add(doButton);
                
        this.mainPanel.add(argsPanel, BorderLayout.CENTER);

        //{{REGISTER_LISTENERS
        SymAction lSymAction = new SymAction();
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
        ArrayList<String> arry = new ArrayList<>();
        File file = arg1_basetime.getImageFile().getImageFile();
        File dir = file.getParentFile();
        arry.add(dir.getAbsolutePath());
        arry.add(file.getName());
        arry.add(arg1_basetime.argField.getText());
        file = arg2_basetime.getImageFile().getImageFile();
        arry.add(file.getName());
        arry.add(arg2_basetime.argField.getText());
    	String[] argv = arry.toArray(new String[arry.size()]);
        (new DoRestamp(argv)).setVisible(true);
    }

    /**
     *  入力条件が満たされているかどうか
     * @return
     */
    @Override
    public boolean isEnable() {
       return (arg1_basetime.isEnable() && arg2_basetime.isEnable());
    }
    
    @Override
    public void openAction() {
       ; // 何もしない
    }
}
