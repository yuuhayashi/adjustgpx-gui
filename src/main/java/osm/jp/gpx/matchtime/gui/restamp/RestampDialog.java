package osm.jp.gpx.matchtime.gui.restamp;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import osm.jp.gpx.AppParameters;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.Card;
import osm.jp.gpx.matchtime.gui.ParameterPanelFolder;
import osm.jp.gpx.matchtime.gui.ParameterPanelImageFile;
import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;
import osm.jp.gpx.matchtime.gui.ParameterPanelTime;
import osm.jp.gpx.matchtime.gui.SimpleDocumentListener;

@SuppressWarnings("serial")
public class RestampDialog extends JDialog
{
    //{{DECLARE_CONTROLS
    java.awt.Button closeButton;
    JTabbedPane cardPanel;       // ウィザード形式パネル（タブ型）
    Card[] cards;
    ParameterPanelFolder arg1_srcFolder;    // 対象フォルダ
    ParameterPanelImageFile arg2_baseTimeImg;   // 開始画像ファイルパス
    ParameterPanelTime arg2_basetime;	// 開始画像の基準時刻:
    ParameterPanelImageFile arg3_baseTimeImg;   // 終了画像ファイルパス
    ParameterPanelTime arg3_basetime;	// 終了画像の基準時刻:
    AppParameters params;
    //}}

    class SymWindow extends java.awt.event.WindowAdapter
    {
        @Override
        public void windowClosing(java.awt.event.WindowEvent event) {
            Object object = event.getSource();
            if (object == RestampDialog.this) {
                dispose();
            }
        }
    }

    class SymAction implements java.awt.event.ActionListener
    {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent event) {
            Object object = event.getSource();
            if (object == closeButton) {
                dispose();
            }
        }
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public RestampDialog(Frame parent, boolean modal) throws IOException {
        super(parent, modal);

        // INIT_CONTROLS
        setLayout(new BorderLayout());
        setSize(
            getInsets().left + getInsets().right + 720,
            getInsets().top + getInsets().bottom + 480
        );
        setTitle(i18n.getString("menu.restamp") + "... ");
        
        //---- CENTER -----
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        //---- SOUTH -----
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        southPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);
        
        //---- SPACE -----
        add(Box.createVerticalStrut(30), BorderLayout.NORTH);
        add(Box.createHorizontalStrut(10), BorderLayout.WEST);
        add(Box.createHorizontalStrut(10), BorderLayout.EAST);
        
        closeButton = new java.awt.Button();
        closeButton.setLabel(i18n.getString("button.close") );
        closeButton.setBounds(145,65,66,27);
        southPanel.add(closeButton);
        //}}
        
        //---------------------------------------------------------------------
        params = new AppParameters();
        cards = new Card[4];
        cardPanel = new JTabbedPane(JTabbedPane.LEFT);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
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
        // 2. [基準画像（開始）]選択パネル
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
            
            CardImageFile card = new CardImageFile(
                    cardPanel, arg2_basetime, (Window)this, 
                    AdjustTerra.i18n.getString("tab.restamp.200"), 0, 2);
            cardPanel.addTab(card.getTitle(), card);
            cardPanel.setEnabledAt(cardNo, false);
            cards[cardNo] = card;
            cardNo++;
        }

        //---------------------------------------------------------------------
        // 3. 最終画像の本当の時刻を設定の入力画面
        {
            // 基準時刻画像
            arg3_baseTimeImg = new ParameterPanelImageFile(
                i18n.getString("label.210") +": ", 
                null, 
                arg1_srcFolder
            );
            
            // 3a. 基準時刻:
            arg3_basetime = new ParameterPanelTime(
                    i18n.getString("label.310"), 
                    null, 
                    arg3_baseTimeImg
            );
            arg3_basetime.argField.getDocument().addDocumentListener(
                new SimpleDocumentListener() {
                    @Override
                    public void update(DocumentEvent e) {
                        toEnable(2, arg3_basetime.isEnable());
                    }
                }
            );
            
            CardImageFile card = new CardImageFile(
                cardPanel, arg3_basetime, (Window)this, 
                AdjustTerra.i18n.getString("tab.restamp.250"), 1, 3
            );
            cardPanel.addTab(card.getTitle(), card);
            cardPanel.setEnabledAt(cardNo, false);
            cards[cardNo] = card;
            cardNo++;
        }
        
        //---------------------------------------------------------------------
        // ４. 実行画面
        {
            CardPerformFile card = new CardPerformFile(
                    cardPanel, 
                    arg2_basetime,
                    arg3_basetime
            );
            cardPanel.addTab(card.getTitle(), card);
            cardPanel.setEnabledAt(cardNo, false);
            cards[cardNo] = card;
            cardNo++;
        }
        
        //{{REGISTER_LISTENERS
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);
        SymAction lSymAction = new SymAction();
        closeButton.addActionListener(lSymAction);
        //}}
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

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public RestampDialog(Frame parent, String title, boolean modal) throws IOException {
        this(parent, modal);
        setTitle(title);
    }

    // Used for addNotify redundency check.
    boolean fComponentsAdjusted = false;

    @Override
    public void addNotify() {
        // Record the size of the window prior to calling parents addNotify.
        super.addNotify();

        // Only do this once.
        if (fComponentsAdjusted) {
            return;
        }

        // Adjust components according to the insets
        setSize(getInsets().left + getInsets().right + getSize().width, getInsets().top + getInsets().bottom + getSize().height);
        Component components[] = getComponents();
        for (Component component : components) {
            Point p = component.getLocation();
            p.translate(getInsets().left, getInsets().top);
            component.setLocation(p);
        }

        // Used for addNotify check.
        fComponentsAdjusted = true;
    }

    /**
    * Shows or hides the component depending on the boolean flag b.
    * @param b  if true, show the component; otherwise, hide the component.
    * @see java.awt.Component#isVisible
    */
    @Override
    public void setVisible(boolean b) {
        if(b) {
            Rectangle bounds = getParent().getBounds();
            Rectangle abounds = getBounds();
            setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
            bounds.y + (bounds.height - abounds.height)/2);
        }
        super.setVisible(b);
    }
}
