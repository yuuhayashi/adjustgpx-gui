package osm.jp.gpx.matchtime.gui;

import java.io.File;
import java.io.FileNotFoundException;

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
        super(name, label, text);
    }
    
    /**
     * 有効な値が設定されているかどうか
     * @return 
     */
    @Override
    public boolean isEnable() {
        String text = this.argField.getText();
        if (text == null) {
            return false;
        }
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