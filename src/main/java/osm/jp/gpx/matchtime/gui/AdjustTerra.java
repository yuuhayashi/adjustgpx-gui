package osm.jp.gpx.matchtime.gui;

import osm.jp.gpx.matchtime.gui.restamp.RestampDialog;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import osm.jp.gpx.*;
import osm.jp.gpx.matchtime.gui.restamp.CardImageFile;
import osm.jp.gpx.matchtime.gui.restamp.CardSourceFolder;

/**
 * 本プログラムのメインクラス
 */
@SuppressWarnings("serial")
public class AdjustTerra extends JFrame
{
    public static final String PROGRAM_NAME = "AdjustTerra for JOSM";
    public static final String PROGRAM_VARSION = "5.2";
    public static final String PROGRAM_UPDATE = "2020/02/02";

    AppParameters params;
    public static SimpleDateFormat dfjp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    // Used for addNotify check.
    boolean fComponentsAdjusted = false;
    public static ResourceBundle i18n = ResourceBundle.getBundle("i18n");
    
    //{{DECLARE_CONTROLS
    JTabbedPane cardPanel;       // ウィザード形式パネル（タブ型）
    Card[] cards;
    //}}

    //---入力フィールド----------------------------------------------------
    ParameterPanelFolder arg1_srcFolder;    // 対象フォルダ
    ParameterPanelImageFile arg2_baseTimeImg;   // 開始画像ファイルパス
    ParameterPanelTime arg2_basetime;       // 開始画像の基準時刻:
    ParameterPanelGpx arg3_gpxFile;         // GPX file or Folder
    ParameterPanelOutput arg4_output;       // EXIF & 書き出しフォルダ

    //{{DECLARE_MENUS
    java.awt.MenuBar mainMenuBar;
    java.awt.Menu menu1;
    java.awt.MenuItem miDoNewFileList;
    java.awt.MenuItem miDoDirSize;
    java.awt.MenuItem miDoReadXML;
    java.awt.MenuItem miExit;
    java.awt.Menu menu2;
    java.awt.MenuItem miRestamp;
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
            else if (object == miRestamp) {
                try {
                    miRestamp_Action(event);
                } catch (IOException ex) {
                    Logger.getLogger(AdjustTerra.class.getName()).log(Level.SEVERE, null, ex);
                }
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
    public AdjustTerra() throws IOException
    {
        dfjp.setTimeZone(TimeZone.getTimeZone("JST"));

        // INIT_CONTROLS
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        setSize(
            getInsets().left + getInsets().right + 720,
            getInsets().top + getInsets().bottom + 480
        );
        setTitle(AdjustTerra.PROGRAM_NAME +" v"+ AdjustTerra.PROGRAM_VARSION);
        
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
        int cardNo = 0;

        //---------------------------------------------------------------------
        // 1.[対象フォルダ]設定パネル
        {
            arg1_srcFolder = new ParameterPanelFolder(
                    i18n.getString("label.110") +": ", 
                    params.getProperty(AppParameters.IMG_SOURCE_FOLDER)
            );
            arg1_srcFolder.argField
                .getDocument()
                .addDocumentListener(
                    new SimpleDocumentListener() {
                        @Override
                        public void update(DocumentEvent e) {
                            toEnable(0, arg1_srcFolder.isEnable());
                        }
                    }
                );

            Card card = new CardSourceFolder(cardPanel, arg1_srcFolder);
            cardPanel.addTab(card.getTitle(), card);
            cardPanel.setEnabledAt(cardNo, true);
            cards[cardNo] = card;
            cardNo++;
        }
        
        //---------------------------------------------------------------------
        // 2.[基準時刻画像]設定パネル
        // 2a.基準時刻の入力画面
        {
            // 基準時刻画像
            arg2_baseTimeImg = new ParameterPanelImageFile(
                i18n.getString("label.210") +": ", 
                null, 
                arg1_srcFolder
            );

            // 2a. 基準時刻:
            arg2_basetime = new ParameterPanelTime(
                    i18n.getString("label.310"), 
                    null, 
                    arg2_baseTimeImg
            );
            arg2_basetime.argField.getDocument().addDocumentListener(
                new SimpleDocumentListener() {
                    @Override
                    public void update(DocumentEvent e) {
                        toEnable(1, arg2_basetime.isEnable());
                    }
                }
            );
            
            // EXIFの日時を基準にする
            arg2_basetime.addExifBase(i18n.getString("label.220"), params);

            // ファイル更新日時を基準にする
            arg2_basetime.addFileUpdate(i18n.getString("label.230"), params);

            CardImageFile card = new CardImageFile(
                    cardPanel, arg2_basetime, (Window)this, 
                    AdjustTerra.i18n.getString("tab.300"), 0, 2);
            cardPanel.addTab(card.getTitle(), card);
            cardPanel.setEnabledAt(cardNo, false);
            cards[cardNo] = card;
            cardNo++;
        }
        
        //---------------------------------------------------------------------
        // 3.GPXファイル設定画面
        {
            // 3. GPXファイル選択パラメータ
            arg3_gpxFile = new ParameterPanelGpx(
                i18n.getString("label.410") + ": ", 
                params.getProperty(AppParameters.GPX_SOURCE_FOLDER)
            );
            arg3_gpxFile.argField.getDocument().addDocumentListener(
                new SimpleDocumentListener() {
                    @Override
                    public void update(DocumentEvent e) {
                        toEnable(2, arg3_gpxFile.isEnable());
                    }
                }
            );

            // "セグメント'trkseg'の最初の１ノードは無視する。"
            arg3_gpxFile.addNoFirstNode(i18n.getString("label.420"), params);

            // "生成されたGPXファイル（ファイル名が'_.gpx'で終わるもの）も変換の対象にする"
            arg3_gpxFile.addGpxReuse(i18n.getString("label.430"), params);
            
            // 3. GPXファイルを選択
            CardGpxFile card = new CardGpxFile(
                    cardPanel, arg3_gpxFile,
                     AdjustTerra.i18n.getString("tab.400"), 1, 3);
            cardPanel.addTab(card.getTitle(), card);
            cardPanel.setEnabledAt(cardNo, false);
            cards[cardNo] = card;
            cardNo++;
        }
        
        //---------------------------------------------------------------------
        // 4.EXIF更新設定画面 & 実行画面
        {
            // 4. ファイル変換・実行パラメータ
            // "出力フォルダ: "
            arg4_output = new ParameterPanelOutput(
                i18n.getString("label.530") + ": ", 
                params.getProperty(AppParameters.IMG_OUTPUT_FOLDER)
            );
            arg4_output.argField.getDocument().addDocumentListener(
                new SimpleDocumentListener() {
                    @Override
                    public void update(DocumentEvent e) {
                        toEnable(3, arg4_output.isEnable());
                    }
                }
            );

            // "IMGの変換をする"
            arg4_output.addCheckChangeImage(i18n.getString("label.510"), params);

            // "GPXファイル時間外のファイルもコピーする"
            arg4_output.addCheckOutofGpxTime(i18n.getString("label.520"), params);

            // "EXIFの変換をする"
            arg4_output.addCheckOutputExif(i18n.getString("label.540"), params);

            // "ポイントマーカー<WPT>をGPXファイルに出力する"
            arg4_output.addCheckOutputWpt(i18n.getString("label.550"), params);

            // "ソースGPXの<MAGVAR>を無視する"
            arg4_output.addCheckIgnoreMagvar(i18n.getString("label.560"), params);

            // "出力GPXに[SPEED]を上書きする"
            arg4_output.addCheckOutputSpeed(i18n.getString("label.570"), params);

            // パネル表示
            CardExifPerform card = new CardExifPerform(
                cardPanel, 
                arg2_basetime, arg3_gpxFile, arg4_output,
                AdjustTerra.i18n.getString("tab.500"),
                2, -1
            );
            cardPanel.addTab(card.getTitle(), card);
            cardPanel.setEnabledAt(cardNo, false);
            cards[cardNo] = card;
        }

        //---------------------------------------------------------------------
        // INIT_MENUS
        menu1 = new java.awt.Menu("File");
        miExit = new java.awt.MenuItem(i18n.getString("menu.quit"));
        miExit.setFont(new Font("Dialog", Font.PLAIN, 12));
        menu1.add(miExit);

        miRestamp = new java.awt.MenuItem(i18n.getString("menu.restamp") + "...");
        miRestamp.setFont(new Font("Dialog", Font.PLAIN, 12));
        
        menu2 = new java.awt.Menu(i18n.getString("menu.tools"));
        menu2.setFont(new Font("Dialog", Font.PLAIN, 12));
        menu2.add(miRestamp);

        miAbout = new java.awt.MenuItem("About...");
        miAbout.setFont(new Font("Dialog", Font.PLAIN, 12));

        menu3 = new java.awt.Menu("Help");
        menu3.setFont(new Font("Dialog", Font.PLAIN, 12));
        menu3.add(miAbout);

        mainMenuBar = new java.awt.MenuBar();
        mainMenuBar.setHelpMenu(menu3);
        mainMenuBar.add(menu1);
        mainMenuBar.add(menu2);
        mainMenuBar.add(menu3);
        setMenuBar(mainMenuBar);

        //{{REGISTER_LISTENERS
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);
        SymAction lSymAction = new SymAction();
        miAbout.addActionListener(lSymAction);
        miRestamp.addActionListener(lSymAction);
        miExit.addActionListener(lSymAction);
        arg2_baseTimeImg.openButton.addActionListener(lSymAction);
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
    private static void createAndShowGUI() throws IOException {
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
    
    void miRestamp_Action(java.awt.event.ActionEvent event) throws IOException {
        (new RestampDialog(this, false)).setVisible(true);
    }
    
    void miExit_Action(java.awt.event.ActionEvent event) {
        // Action from Exit Create and show as modal
        //(new hayashi.yuu.tools.gui.QuitDialog(this, true)).setVisible(true);
        (new QuitDialog(this, true)).setVisible(true);
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
}
