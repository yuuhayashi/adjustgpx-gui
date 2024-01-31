package osm.jp.gpx.matchtime.gui.card.gpxfolder;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import osm.jp.gpx.AppParameters;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.GpxAndFolderFilter;
import osm.jp.gpx.matchtime.gui.parameters.ParameterPanel;

@SuppressWarnings("serial")
public class ParameterPanelGpx extends ParameterPanel implements ActionListener
{
    JFileChooser fc;
    JButton selectButton;
    public JCheckBox noFirstNode;      // CheckBox: "セグメント'trkseg'の最初の１ノードは無視する。"
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /**
     * コンストラクタ
     * [ヒモ付を行うGPXファイルを選択してください。]
     * @param label
     * @param text 
     */
    public ParameterPanelGpx(String text) {
        super(AppParameters.GPX_SOURCE_FOLDER, i18n.getString("label.410") + ": ", text);
        
        // 1-2. ヒモ付を行うGPXファイルを選択してください。
        //    - フォルダを指定すると、フォルダ内のすべてのGPXファイルを対象とします。
        
        // "セグメント'trkseg'の最初の１ノードは無視する。"
        this.setNoFirstNode(i18n.getString("label.420"), AdjustTerra.params);
        //this.add(this.noFirstNode);
        
        if (!isEnable()) {
            Path path = Paths.get(".");
        	this.setText(path.toAbsolutePath().toString());
        }
        
        // [選択...]ボタン
        selectButton = new JButton(
                i18n.getString("button.select"), 
                AdjustTerra.createImageIcon("/images/Open16.gif")
        );
        selectButton.addActionListener(this);
        this.add(selectButton);
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.selectButton){
            System.out.println("ParameterPanelGpx.actionPerformed('selectButton')");
            String old = this.argField.getText();
            File sdir = new File(old);
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
                String text = file.getAbsolutePath();
                this.setText(text);
                
                // 「update イベントを発火させる
            	this.propertyChangeSupport.firePropertyChange(this.propertyName, null, text);
            }
        }
        else if (e.getSource() == this.argField){
            String text = this.getText();
            System.out.println(String.format("GPX_SOURCE_FOLDER: '%s'", text));
            
            // 'argField' ’が有効ならば、「update イベントを発火させる
        	this.propertyChangeSupport.firePropertyChange(this.propertyName, null, text);
        }
    }
    
    public File getGpxFolder() {
        if (isEnable()) {
            return Paths.get(getText()).toFile();
        }
        return null;
    }
    
    /**
     * "セグメント'trkseg'の最初の１ノードは無視する。"
     * @param label         テキスト
     * @param params        プロパティ
     */
    public void setNoFirstNode(String label, AppParameters params) {
        boolean selected = false;
        if (params.getProperty(AppParameters.GPX_NO_FIRST_NODE).equals("true")) {
            selected = true;
        }
        noFirstNode = new JCheckBox(label, selected);
    }
    
    public JCheckBox getNoFirstNode() {
    	return this.noFirstNode;
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
        return checkGpxFolder(text);
    }
    
	/**
	 * "GPX_SOURCE_FOLDER"の設定内容が有効かどうかを判別する
	 * @param str
	 * @return
	 */
	boolean checkGpxFolder(String str) {
		if (str != null) {
			Path p = Paths.get(str);
			if (p != null) {
				if (Files.exists(p)) {
					if (Files.isDirectory(p)) {
						List<Path> entries;
						try {
							entries = Files.list(p).collect(Collectors.toList());
							for (Path file : entries) {
								if (file.toString().toLowerCase().endsWith(".gpx")) {
									return true;
								}
							}
						} catch (IOException e) {
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void setText(String text) {
        this.argField.setText(text);
	}

	@Override
	public String getText() {
        return this.argField.getText();
	}
}
