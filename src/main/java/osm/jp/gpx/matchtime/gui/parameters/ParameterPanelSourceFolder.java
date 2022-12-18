package osm.jp.gpx.matchtime.gui.parameters;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;

import osm.jp.gpx.matchtime.gui.ImageFileFilter;

@SuppressWarnings("serial")
public class ParameterPanelSourceFolder extends ParameterPanelFolder
{
    /**
     * コンストラクタ
     * ディレクトリのみ選択可能なダイアログ
     * @param label
     * @param text 
     */
    public ParameterPanelSourceFolder(String name, String label, String text) {
        super(name, label, text, JFileChooser.FILES_AND_DIRECTORIES);
    }
    
    /**
     * 有効な値が設定されているかどうか
     * [argField.getText() = 有効なディレクトリを示している]
     * AND [folder is not empty.]
     * AND [Image file exist in the folder.]
     * @return 
     */
    @Override
    public boolean isEnable() {
        try {
            File dir = super.getDirectory();

			File[] files = dir.listFiles(new ImageFileFilter());
			if ((files == null) || (files.length < 1)) {
				return false;
			}
			return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public File getDirectory() throws FileNotFoundException {
        File dir = super.getDirectory();
        if (dir.exists() && dir.isDirectory()) {
        	return dir;
        }
        throw new FileNotFoundException();
    }
}