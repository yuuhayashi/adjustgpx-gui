package osm.jp.gpx.matchtime.gui.card.perform;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.*;
import javax.swing.*;

import osm.jp.gpx.matchtime.gui.Command;

/**
 *	処理 
 */
@SuppressWarnings("serial")
public class DoDialog extends JDialog {
    public static final String TITLE = "Do Command";
	
    // Used for addNotify check.
    boolean fComponentsAdjusted = false;
    String[] args;
    
    //{{DECLARE_CONTROLS
    JPanel buttonPanel;     // ボタン配置パネル	(下部)
    JButton closeButton;      // [クローズ]ボタン
    JButton doButton;      // [実行]ボタン
    JTextArea textArea;      // 実行結果を表示するJTextArea	(中央)
    //}}

    public DoDialog(String[] args) {
        super();   // モーダルダイアログを基盤にする
        this.args = args;
                
        // INIT_CONTROLS
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        //parentFrame.setVisible(false);
        setSize(getInsets().left + getInsets().right + 980, getInsets().top + getInsets().bottom + 480);
        setTitle(DoDialog.TITLE);
        
        // コントロールパネル
        buttonPanel = new JPanel();

        doButton = new JButton("実行");
        doButton.setToolTipText("処理を実行します.");
        doButton.setEnabled(true);
        doButton.addActionListener((ActionEvent event) -> {
            // 処理中であることを示すため
            // ボタンの文字列を変更し，使用不可にする
            doButton.setText("処理中...");
            doButton.setEnabled(false);
            
            // SwingWorker を生成し，実行する
            LongTaskWorker worker = new LongTaskWorker(doButton);
            worker.execute();
        });
        buttonPanel.add(doButton);

        closeButton = new JButton("閉じる");
        closeButton.setToolTipText("処理を終了します.");
        closeButton.addActionListener((ActionEvent event) -> {
            dispose();
        });
        buttonPanel.add(closeButton);
        
        this.getContentPane().add("South", buttonPanel);
        
        // 説明文
        textArea = new JTextArea();
        JScrollPane sc=new JScrollPane(textArea);
        textArea.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
        textArea.setTabSize(4);
        this.getContentPane().add("Center", sc);
        
        try {
            textArea.append("> java -cp importPicture.jar osm.jp.gpx.ImportPicture");
            for (String arg : args) {
                textArea.append(" '" + arg + "'");
            }
            textArea.append("\n\n");
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }

        // JFrameの表示
        //parentFrame.setVisible(true);
    }
    
    /**
    * Shows or hides the component depending on the boolean flag b.
    * @param b	trueのときコンポーネントを表示; その他のとき, componentを隠す.
    * @see java.awt.Component#isVisible
    */
    @Override
    public void setVisible(boolean b) {
        if(b) {
            setLocation(80, 80);
        }
        super.setVisible(b);
    }

    @Override
    public void addNotify()	{
        // Record the size of the window prior to calling parents addNotify.
        Dimension d = getSize();

        super.addNotify();

        if (fComponentsAdjusted) {
            return;
        }

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

    
    /**
     * JTextAreaに書き出すOutputStream
     */
    public static class JTextAreaOutputStream extends OutputStream {
        private final ByteArrayOutputStream os;
        
        /** 書き出し対象 */
        private final JTextArea textArea;
        private final String encode;

        public JTextAreaOutputStream(JTextArea textArea, String encode) {
            this.textArea = textArea;
            this.encode = encode;
            this.os = new ByteArrayOutputStream();
        }
        
        /** 
         * OutputStream#write(byte[])のオーバーライド
         * @param arg
         * @throws java.io.IOException
         */
        @Override
        public void write(int arg) throws IOException {
            this.os.write(arg);
        }
        
        /**
         * flush()でJTextAreaに書き出す
         * @throws java.io.IOException
         */
        @Override
        public void flush() throws IOException {
            // 文字列のエンコード
            final String str = new String(this.os.toByteArray(), this.encode);
            // 実際の書き出し処理
            SwingUtilities.invokeLater(
                new Runnable(){
                    @Override
                    public void run() {
                        JTextAreaOutputStream.this.textArea.append(str);
                    }
                }
            );
            // 書き出した内容はクリアする
            this.os.reset();
        }
    }
    
    // 非同期に行う処理を記述するためのクラス
    class LongTaskWorker extends SwingWorker<Object, Object> {
        private final JButton button;

        public LongTaskWorker(JButton button) {
            this.button = button;
        }

        // 非同期に行われる処理
        @Override
        public Object doInBackground() {
            // ながーい処理
            PrintStream defOut = System.out;
            PrintStream defErr = System.err;

            OutputStream os = new JTextAreaOutputStream(textArea, "UTF-8");
            PrintStream stdout = new PrintStream(os, true);      // 自動flushをtrueにしておく

            // System.out にJTextAreaOutputStreamに書き出すPrintStreamを設定
            System.setOut(stdout);
            System.setErr(stdout);

            try {
                Command command = new Command(osm.jp.gpx.ImportPicture.class);
                command.setArgs(args);
                command.start();		// コマンドを実行
                while (command.isAlive()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
            catch(Exception e) {
                e.printStackTrace(stdout);
            }
            finally {
                System.setOut(defOut);
                System.setErr(defErr);
                doButton.setEnabled(true);
            }

            return null;
        }

        // 非同期処理後に実行
        @Override
        protected void done() {
            // 処理が終了したので，文字列を元に戻し
            // ボタンを使用可能にする
            button.setText("実行");
            button.setEnabled(true);
        }
    }
}
