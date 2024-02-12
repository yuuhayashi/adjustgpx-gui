package osm.jp.gpx.matchtime.gui.card.perform;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import osm.jp.gpx.AppParameters;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.parameters.ParameterPanelWithComment;
import osm.jp.hayashi.tools.files.Directory;

@SuppressWarnings("serial")
public class ParameterPanelOutput extends ParameterPanelWithComment implements ActionListener
{
    JFileChooser fc;
    JButton selectButton;
    int chooser;
    
    /**
     * コンストラクタ
     * ディレクトリのみ選択可能なダイアログ
     * @param label
     * @param text 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public ParameterPanelOutput() {
        super(AppParameters.IMG_OUTPUT_FOLDER, i18n.getString("label.530") + ": ", "");
        
    	String text = AdjustTerra.params.getProperty(AppParameters.IMG_OUTPUT_FOLDER);
    	this.setText(text);
    	this.setEnabled(true);
    	
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
	                this.argField.setText(p.toAbsolutePath().toString());
				} catch (URISyntaxException e1) {
	                this.argField.setText(".");
				}
                sdir = new File(this.argField.getText());
            }
            if (sdir.exists()) {
                this.fc = new JFileChooser(sdir);
            }
            else {
                this.fc = new JFileChooser();
            }
            this.fc.setFileSelectionMode(this.chooser);

            int returnVal = this.fc.showOpenDialog(ParameterPanelOutput.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = this.fc.getSelectedFile();
                if (!file.isDirectory()) {
                	file = file.getParentFile();
                }
                String text = file.getAbsolutePath();
                this.argField.setText(text);
                this.setText(text);
            }
        }
	}
	
    File getDirectory() throws FileNotFoundException {
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

    public void setEnable(boolean f) {
        super.setEnabled(f);
        selectButton.setEnabled(f);
    }

	@Override
	public boolean isEnable() {
        String str = this.argField.getText();
		if (str != null) {
			Path p = Paths.get(str);
			if (p != null) {
				if (Files.exists(p)) {
					if (Files.isDirectory(p)) {
						// 'output Folder' is Enable.
						this.setComment(i18n.getString("msg.505"), true);
						return true;
					}
					else {
						// 'output Folder' is not directory.
						this.setComment(i18n.getString("msg.501"), false);
					}
				}
				else {
					// 'output Folder' is not exists.
					this.setComment(i18n.getString("msg.502"), false);
				}
			}
			else {
				// 'output Folder' is not directory.
				this.setComment(i18n.getString("msg.502"), false);
			}
		}
		else {
			// 'output Folder' is null.
			this.setComment(i18n.getString("msg.500"), false);
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
}