package osm.jp.gpx.matchtime.gui;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.swing.*;

import osm.jp.gpx.*;
import osm.jp.gpx.matchtime.gui.card.gpxfolder.ParameterPanelGpx;
import osm.jp.gpx.matchtime.gui.card.source.ParameterPanelSourceFolder;
import osm.jp.gpx.matchtime.gui.card.gpxfolder.CardGpxFile;
import osm.jp.gpx.matchtime.gui.card.perform.CardExifPerform;
import osm.jp.gpx.matchtime.gui.card.source.CardSourceFolder;
import osm.jp.gpx.matchtime.gui.card.time.CardImageFile;

/**
 * 本プログラムのメインクラス
 */
@SuppressWarnings("serial")
public class AdjustTerra extends JFrame implements PropertyChangeListener
{
    public static final String PROGRAM_NAME = "AdjustGpx";

    public static AppParameters params;
    
    public static SimpleDateFormat dfjp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    // Used for addNotify check.
    boolean fComponentsAdjusted = false;
    public static ResourceBundle i18n = ResourceBundle.getBundle("i18n");
    
    //{{DECLARE_CONTROLS
    JTabbedPane cardPanel;       // ウィザード形式パネル（タブ型）
    Card[] cards;
    //}}

    //{{DECLARE_MENUS
    java.awt.MenuBar mainMenuBar;
    java.awt.Menu menu1;
    java.awt.MenuItem miDoNewFileList;
    java.awt.MenuItem miDoDirSize;
    java.awt.MenuItem miDoReadXML;
    java.awt.MenuItem miExit;
    java.awt.Menu menu3;
    java.awt.MenuItem miAbout;
    //}}

    class SymWindow extends java.awt.event.WindowAdapter {
        /**
         * このFrameが閉じられるときの動作。
         * このパネルが閉じられたら、このアプリケーションも終了させる。
         */
        @Override
        public void windowClosing(java.awt.event.WindowEvent event) {
            Object object = event.getSource();
            if (object == AdjustTerra.this) {
                DbMang_WindowClosing(event);
            }
        }
    }

    class SymAction implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent event) {
            Object object = event.getSource();
            if (object == miAbout) {
                miAbout_Action(event);
            }
            else if (object == miExit) {
                miExit_Action(event);
            }
        }
    }

    /**
     * データベース内のテーブルを一覧で表示するFrame
     * @throws IOException 
     */
    public AdjustTerra() throws Exception
    {
        dfjp.setTimeZone(TimeZone.getTimeZone("JST"));

        // INIT_CONTROLS
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        setSize(
            getInsets().left + getInsets().right + 720,
            getInsets().top + getInsets().bottom + 480
        );
        setTitle(AdjustTerra.PROGRAM_NAME);
        
        //---- CENTER -----
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        container.add(mainPanel, BorderLayout.CENTER);
        
        //---- SOUTH -----
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        southPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        container.add(southPanel, BorderLayout.SOUTH);
        
        //---- SPACE -----
        container.add(Box.createVerticalStrut(30), BorderLayout.NORTH);
        container.add(Box.createHorizontalStrut(10), BorderLayout.WEST);
        container.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
        
        params = new AppParameters();
        
        //---------------------------------------------------------------------
        cardPanel = new JTabbedPane(JTabbedPane.LEFT);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        
        cards = new Card[4];
        CardGpxFile card0;
        CardSourceFolder card1;
        CardImageFile card2;
        CardExifPerform card3;
        int cardNo = 0;

        //---------------------------------------------------------------------
        // 1.GPXファイル設定画面
        {
            // 1. GPXファイルを選択
            card0 = new CardGpxFile(cardPanel, cardNo, cardNo+1);
            cardPanel.addTab(card0.getTitle(), card0);
            cardPanel.setEnabledAt(cardNo, true);
            card0.addPropertyChangeListener(this);
            cards[cardNo] = card0;
            cardNo++;
        }
        
        //---------------------------------------------------------------------
        // 2.[対象フォルダ]設定パネル
        {
            card1 = new CardSourceFolder(cardPanel, cardNo-1, cardNo+1);
            cardPanel.addTab(card1.getTitle(), card1);
            cardPanel.setEnabledAt(cardNo, true);
            card1.addPropertyChangeListener(this);
            cards[cardNo] = card1;
            cardNo++;
        }
        
        //---------------------------------------------------------------------
        // 3.[基準時刻]パネル
        // 3a.基準画像を選択フィールド
        // 3b.基準時刻の入力フィールド
        {
            card2 = new CardImageFile(cardPanel, cardNo-1, cardNo+1, card1.getSourceFolder(), (Window)this);
            cardPanel.addTab(card2.getTitle(), card2);
            cardPanel.setEnabledAt(cardNo, false);
            card2.addPropertyChangeListener(this);
            cards[cardNo] = card2;
            cardNo++;
        }
        
        //---------------------------------------------------------------------
        // 4.EXIF更新設定画面 & 実行画面
        {
            // パネル表示
            card3 = new CardExifPerform(cardPanel, 2, -1,card2.getBaseTime(), card0.getGpxFile());
            cardPanel.addTab(card3.getTitle(), card3);
            cardPanel.setEnabledAt(cardNo, false);
            cards[cardNo] = card3;
        }

        //---------------------------------------------------------------------
        // INIT_MENUS
        menu1 = new java.awt.Menu("File");
        miExit = new java.awt.MenuItem(i18n.getString("menu.quit"));
        miExit.setFont(new Font("Dialog", Font.PLAIN, 12));
        menu1.add(miExit);

        miAbout = new java.awt.MenuItem("About...");
        miAbout.setFont(new Font("Dialog", Font.PLAIN, 12));

        menu3 = new java.awt.Menu("Help");
        menu3.setFont(new Font("Dialog", Font.PLAIN, 12));
        menu3.add(miAbout);

        mainMenuBar = new java.awt.MenuBar();
        mainMenuBar.setHelpMenu(menu3);
        mainMenuBar.add(menu1);
        mainMenuBar.add(menu3);
        setMenuBar(mainMenuBar);

        //{{REGISTER_LISTENERS
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);
        SymAction lSymAction = new SymAction();
        miAbout.addActionListener(lSymAction);
        miExit.addActionListener(lSymAction);
        //}}
    }
    
    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b	trueのときコンポーネントを表示; その他のとき, componentを隠す.
     * @see java.awt.Component#isVisible
     */
    @Override
    public void setVisible(boolean b) {
        if(b) {
            setLocation(50, 50);
        }
        super.setVisible(b);
    }
    
    /**
     * このクラスをインスタンスを生成して表示する。
     * コマンドラインの引数はありません。
     * @param args
     */    
    static public void main(String args[]) {
    	SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private static void createAndShowGUI() throws Exception {
    	(new AdjustTerra()).setVisible(true);
    }

    @Override
    public void addNotify()	{
        // Record the size of the window prior to calling parents addNotify.
        Dimension d = getSize();

        super.addNotify();

        if (fComponentsAdjusted)
            return;

        // Adjust components according to the insets
        setSize(getInsets().left + getInsets().right + d.width, getInsets().top + getInsets().bottom + d.height);
        Component components[] = getComponents();
        for (Component component : components) {
            Point p = component.getLocation();
            p.translate(getInsets().left, getInsets().top);
            component.setLocation(p);
        }
        fComponentsAdjusted = true;
    }

    void DbMang_WindowClosing(java.awt.event.WindowEvent event)	{
        setVisible(false);  // hide the Manager
        dispose();			// free the system resources
        System.exit(0);		// close the application
    }

    void miAbout_Action(java.awt.event.ActionEvent event) {
        // Action from About Create and show as modal
        (new AboutDialog(this, true)).setVisible(true);
    }
    
    void miExit_Action(java.awt.event.ActionEvent event) {
        // Action from Exit Create and show as modal
        //(new hayashi.yuu.tools.gui.QuitDialog(this, true)).setVisible(true);
        (new QuitDialog(this, true)).setVisible(true);
    }
    
    //ImageIcon refImage;
    
    /** Returns an ImageIcon, or null if the path was invalid.
     * @param path
     * @return  */
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = AdjustTerra.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
        Object eventTriggerObject = evt.getSource();
        String propertyName = evt.getPropertyName();

        if (ParameterPanelGpx.class.isInstance(eventTriggerObject)) {
            // [Next]ボタンを有効にする
            ParameterPanelGpx ins = (ParameterPanelGpx) eventTriggerObject;
            System.out.println("[Next] button 0 --> "+ ins.isEnable());
        	toEnable(0, ins.isEnable());
        }
        else if (ParameterPanelSourceFolder.class.isInstance(eventTriggerObject)) {
            // [Next]ボタンを有効にする
        	ParameterPanelSourceFolder ins = (ParameterPanelSourceFolder) eventTriggerObject;
            System.out.println("[Next] button 1 --> "+ ins.isEnable());
        	toEnable(1, ins.isEnable());
        }
        else if (CardImageFile.class.isInstance(eventTriggerObject)) {
            // [Next]ボタンを有効にする
        	CardImageFile ins = (CardImageFile) eventTriggerObject;
        	toEnable(2, ins.isEnable());
            System.out.println("[Next] button 2 --> "+ ins.isEnable());
        }
        else if (CardExifPerform.class.isInstance(eventTriggerObject)) {
            if (propertyName.equals(AppParameters.GPX_BASETIME)) {
                // [実行]ボタンを有効にする
            	CardExifPerform ins = (CardExifPerform) eventTriggerObject;
            	toEnable(3, ins.isEnable());
                System.out.println("[Perform] button 3 --> "+ ins.isEnable());
            }
        }
	}
	
	void toEnable(final int cardNo, final boolean enable) {
        if ((cardNo >= 0) && (cardNo < cards.length)) {
            cardPanel.setEnabledAt(cardNo, enable);
            if ((cardNo -1) >= 0) {
                cards[cardNo -1].nextButton.setEnabled(enable);
            }
            if ((cardNo +1) < cards.length) {
                cardPanel.setEnabledAt(cardNo+1, enable);
                cards[cardNo +1].backButton.setEnabled(enable);
                cards[cardNo].nextButton.setEnabled(enable);
            }
        }
    }
}
