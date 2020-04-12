package osm.jp.gpx.matchtime.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import osm.jp.gpx.AppParameters;

@SuppressWarnings("serial")
public class ParameterPanelGpx extends ParameterPanel implements ActionListener
{
    JFileChooser fc;
    JButton selectButton;
    public JCheckBox noFirstNode;      // CheckBox: "セグメント'trkseg'の最初の１ノードは無視する。"
    
    /**
     * コンストラクタ
     * @param label
     * @param text 
     */
    public ParameterPanelGpx(String label, String text) {
        super(label, text);

        // "選択..."
        selectButton = new JButton(
                i18n.getString("button.select"), 
                AdjustTerra.createImageIcon("/images/Open16.gif")
        );
        selectButton.addActionListener(this);
        this.add(selectButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectButton){
            System.out.println("ParameterPanelGpx.actionPerformed(openButton)");
            File sdir = new File(this.argField.getText());
            if (sdir.exists()) {
                this.fc = new JFileChooser(sdir);
            }
            else {
                this.fc = new JFileChooser();
            }
            this.fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            this.fc.addChoosableFileFilter(new GpxAndFolderFilter());
            this.fc.setAcceptAllFileFilterUsed(false);

            int returnVal = this.fc.showOpenDialog(ParameterPanelGpx.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = this.fc.getSelectedFile();
                this.argField.setText(file.getAbsolutePath());
            }
        }
    }

    public File getGpxFile() {
        if (isEnable()) {
            return new File(getText());
        }
        return null;
    }
    
    /**
     * "セグメント'trkseg'の最初の１ノードは無視する。"
     * @param label         テキスト
     * @param params        プロパティ
     */
    public void addNoFirstNode(String label, AppParameters params) {
        boolean selected = false;
        if (params.getProperty(AppParameters.GPX_NO_FIRST_NODE).equals("true")) {
            selected = true;
        }
        noFirstNode = new JCheckBox(label, selected);
    }
    
    public boolean isNoFirstNodeSelected() {
        return (noFirstNode != null) && noFirstNode.isSelected();
    }
    
    /**
     * このフィールドに有効な値が設定されているかどうか
     * @return 
     */
    @Override
    public boolean isEnable() {
        String text = this.argField.getText();
        if (text != null) {
            File file = new File(text);
            if (file.exists()) {
                if (file.isFile()) {
                    String name = file.getName().toUpperCase();
                    if (name.endsWith(".GPX")) {
                        return true;
                    }
                }
                else if (file.isDirectory()) {
                    return true;
                }
            }
        }
        return false;
    }
}
