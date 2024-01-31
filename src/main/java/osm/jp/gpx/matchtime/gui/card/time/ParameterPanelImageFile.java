package osm.jp.gpx.matchtime.gui.card.time;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import osm.jp.gpx.AppParameters;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.ImageFileView;
import osm.jp.gpx.matchtime.gui.ImageFilter;
import osm.jp.gpx.matchtime.gui.ImagePreview;
import osm.jp.gpx.matchtime.gui.card.source.ParameterPanelSourceFolder;
import osm.jp.gpx.matchtime.gui.parameters.ParameterPanel;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

@SuppressWarnings("serial")
public class ParameterPanelImageFile extends ParameterPanel implements ActionListener {
    JFileChooser fc;
    public JButton openButton;
    public ParameterPanelSourceFolder paramDir;
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public ParameterPanelImageFile(String text, ParameterPanelSourceFolder paramDir) {
        super(AppParameters.IMG_BASE_FILE, i18n.getString("label.210") +": ", text);

        // [選択...]ボタン
        openButton = new JButton(
            i18n.getString("button.select"),
            AdjustTerra.createImageIcon("/images/Open16.gif")
        );
        openButton.addActionListener(this);
        this.add(openButton);
        
        //Create a file chooser
        this.paramDir = paramDir;
    }
    
	/**
	 * [選択...]ボタンのアクション
	 */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            File sdir = new File(paramDir.getText());
            System.out.println(sdir.toPath());
            if (sdir.isDirectory()) {
                fc = new JFileChooser(sdir);
            }
            else {
                fc = new JFileChooser();
            }

            fc.addChoosableFileFilter(new ImageFilter());
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileView(new ImageFileView());
            fc.setAccessory(new ImagePreview(fc));

            //Show it.　"選択"
            int returnVal = fc.showDialog(ParameterPanelImageFile.this, i18n.getString("dialog.select"));
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String text = file.getName();
                argField.setText(text);
                
                // 「update イベントを発火させる
            	this.propertyChangeSupport.firePropertyChange(getName(), null, text);
            }
            else {
                fc.setSelectedFile(null);
            }
        }
        else if (e.getSource() == this.argField) {
            String text = this.getText();
            System.out.println(String.format("%s: '%s'", getName(), text));
            
            // 「update イベント」を発火させる
        	this.propertyChangeSupport.firePropertyChange(getName(), null, text);
        }
    }

    public File getImageFile() {
        if (this.paramDir.isEnable()) {
            String text = this.argField.getText();
            if (text != null) {
                try {
                    File dir = this.paramDir.getDirectory();
                    File file = new File(dir, text);
                    if (file.exists() && file.isFile()) {
                        return file;
                    }
                }
                catch (FileNotFoundException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean isEnable() {
        if (this.paramDir.isEnable()) {
            String text = this.argField.getText();
            if (text != null) {
                try {
                    File dir = this.paramDir.getDirectory();
                    File file = new File(dir, text);
                    if (file.exists() && file.isFile()) {
                        String name = file.getName().toUpperCase();
                        if (name.endsWith(".JPG") || name.endsWith(".JPEG")) {
                            return true;
                        }
                    }
                }
                catch (FileNotFoundException e) {
                    return false;
                }
            }
        }
        return false;
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