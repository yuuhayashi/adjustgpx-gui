package osm.jp.gpx.matchtime.gui;

import java.io.File;
import javax.swing.filechooser.*;

public class GpxAndFolderFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            return extension.equals("gpx");
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Just GPXs";
    }
}
