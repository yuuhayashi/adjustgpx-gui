package osm.jp.gpx.matchtime.gui.card.source;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import osm.jp.gpx.AppParameters;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.parameters.ParameterPanelWithComment;
import osm.jp.hayashi.tools.files.Directory;

@SuppressWarnings("serial")
public class ParameterPanelSourceFolder extends ParameterPanelWithComment implements ActionListener
{
    JFileChooser fc;
    JButton selectButton;
    int chooser;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /**
     * コンストラクタ
     * ディレクトリのみ選択可能なダイアログ
     * @param label
     * @param text 
     */
    public ParameterPanelSourceFolder(String text) {
        super(AppParameters.IMG_SOURCE_FOLDER, i18n.getString("label.110") +": ", text);

        // Create a file chooser
        this.chooser = JFileChooser.DIRECTORIES_ONLY;

        // "選択..."
        selectButton = new JButton(
            i18n.getString("button.select"),
            AdjustTerra.createImageIcon("/images/Open16.gif")
        );
        selectButton.addActionListener(this);
        this.getInnerPanel().add(selectButton);
    }
    
    public void setEnable(boolean f) {
        super.setEnabled(f);
        selectButton.setEnabled(f);
    }

    public File getDirectory() throws FileNotFoundException {
        String path = this.argField.getText();
        if (path == null) {
            throw new FileNotFoundException("Folder is Not specifiyed yet.");
        }
        File sdir = new File(path);
        if (!sdir.exists()) {
            throw new FileNotFoundException(String.format("Folder '%s' is Not exists.", path));
        }
        if (!sdir.isDirectory()) {
        	sdir = sdir.getParentFile();
        }
        return sdir;
    }
	
    /**
     * 有効な値が設定されているかどうか
     * 
     * @return [folder.text=有効なディレクトリを示している]
     */
    @Override
    public boolean isEnable() {
        String text = this.argField.getText();
        if (text == null) {
            return false;
        }
        return checkImgSource(text);
    }
    
	/**
	 * "IMG_SOURCE_FOLDER"の設定内容が有効かどうかを判別する
	 * @param str
	 * @return
	 */
	public boolean checkImgSource(String str) {
		if (str != null) {
			Path p = Paths.get(str);
			if (p != null) {
				if (Files.exists(p)) {
					if (Files.isDirectory(p)) {
						List<Path> entries;
						try {
							entries = Files.list(p).collect(Collectors.toList());
							for (Path file : entries) {
								if (file.toString().toLowerCase().endsWith(".jpeg") || file.toString().toLowerCase().endsWith(".jpg")) {
									// 'Image Folder' is Enable.
									this.setComment(i18n.getString("msg.125"), true);
									return true;
								}
							}
							// Not exists JPEG file in the 'Image Folder'.
							this.setComment(i18n.getString("msg.124"), false);
						} catch (IOException e) {
							this.setComment(e.getCause().toString(), false);
							return false;
						}
					}
					else {
						// 'Image Folder' is not directory.
						this.setComment(i18n.getString("msg.123"), false);
					}
				}
				else {
					// 'Image Folder' is not exists.
					this.setComment(i18n.getString("msg.122"), false);
				}
			}
			else {
				// 'Image Folder' is not directory.
				this.setComment(i18n.getString("msg.121"), false);
			}
		}
		else {
			// 'Image Folder' is null.
			this.setComment(i18n.getString("msg.120"), false);
		}
		return false;
	}
    
	@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectButton){
            File sdir;
            try {
                sdir = getDirectory();
            } catch (FileNotFoundException ex) {
            	Path p;
                try {
                	p = Directory.getCurrentDirectory();
                	String text = p.toAbsolutePath().toString();
	                this.setText(text);
	                
	                // 「update イベントを発火させる
	            	this.propertyChangeSupport.firePropertyChange(getName(), null, text);
				} catch (URISyntaxException e1) {
	                this.setText(".");
	                
	                // 「update イベントを発火させる
	            	this.propertyChangeSupport.firePropertyChange(getName(), null, ".");
				}
                sdir = new File(this.getText());
            }
            if (sdir.exists()) {
                this.fc = new JFileChooser(sdir);
            }
            else {
                this.fc = new JFileChooser();
            }
            this.fc.setFileSelectionMode(this.chooser);

            int returnVal = this.fc.showOpenDialog(ParameterPanelSourceFolder.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = this.fc.getSelectedFile();
                if (!file.isDirectory()) {
                	file = file.getParentFile();
                }
                String text = file.getAbsolutePath();
                this.setText(text);
                
                // 「update イベントを発火させる
            	this.propertyChangeSupport.firePropertyChange(getName(), null, text);
            }
        }
        else if (e.getSource() == this.argField) {
            String text = this.getText();
            System.out.println(String.format("%s: '%s'", getName(), text));
            
            // 「update イベント」を発火させる
        	this.propertyChangeSupport.firePropertyChange(getName(), null, text);
        }
    }

	@Override
	public void setText(String text) {
        this.argField.setText(text);
	}

	@Override
	public String getText() {
        return this.argField.getText();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}
}