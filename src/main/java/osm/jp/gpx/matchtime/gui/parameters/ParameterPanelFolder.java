package osm.jp.gpx.matchtime.gui.parameters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import osm.jp.gpx.matchtime.gui.AdjustTerra;

@SuppressWarnings("serial")
public abstract class ParameterPanelFolder extends ParameterPanel implements ActionListener
{
    JFileChooser fc;
    JButton selectButton;
    int chooser;
    
    /**
     * コンストラクタ
     * ディレクトリのみ選択可能なダイアログ
     * @param label
     * @param text 
     */
    public ParameterPanelFolder(String name, String label, String text) {
        this(name, label, text, JFileChooser.DIRECTORIES_ONLY);
    }

    public ParameterPanelFolder(String name, String label, String text, int chooser) {
        super(name, label, text);

        // Create a file chooser
        this.chooser = chooser;

        // "選択..."
        selectButton = new JButton(
            i18n.getString("button.select"),
            AdjustTerra.createImageIcon("/images/Open16.gif")
        );
        selectButton.addActionListener(this);
        this.add(selectButton);
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
            throw new FileNotFoundException(String.format("Folder '%s' is Not directory.", path));
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
        try {
            getDirectory();
			return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectButton){
            File sdir;
            try {
                sdir = getDirectory();
            } catch (FileNotFoundException ex) {
                sdir = new File(".");
                this.argField.setText(sdir.getAbsolutePath());
            }
            if (sdir.exists()) {
                this.fc = new JFileChooser(sdir);
            }
            else {
                this.fc = new JFileChooser();
            }
            this.fc.setFileSelectionMode(this.chooser);

            int returnVal = this.fc.showOpenDialog(ParameterPanelFolder.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = this.fc.getSelectedFile();
                String text = file.getAbsolutePath();
                this.argField.setText(text);
            }
        }
    }
}