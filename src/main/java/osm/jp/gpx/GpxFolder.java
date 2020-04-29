package osm.jp.gpx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GpxFolder extends ArrayList<File> {
	private static final long serialVersionUID = 6178901459948163548L;
	File[] gpxfiles;
    AppParameters params;
    File gpxDir;
	
	public GpxFolder(AppParameters params) throws FileNotFoundException {
		this.params = params;
		gpxDir = params.getGpxSourceFolder();
        
        gpxDir = params.getGpxSourceFolder();
    	if (gpxDir == null) {
        	// GPXファイルまたはディレクトリが存在しません。('%s')
    		throw new FileNotFoundException(String.format(ImportPicture.i18n.getString("msg.100"), gpxDir.getAbsolutePath()));
    	}
    	
        if (!gpxDir.exists()) {
        	// GPXファイルまたはディレクトリが存在しません。('%s')
    		throw new FileNotFoundException(String.format(ImportPicture.i18n.getString("msg.100"), gpxDir.getAbsolutePath()));
        }

        if (gpxDir.isFile()) {
        	if (accept(params, gpxDir.getName())) {
            	ArrayList<File> fileArray = new ArrayList<>();
            	fileArray.add(gpxDir);
            	gpxfiles = (File[]) fileArray.toArray();
        	}
        	else {
            	// GPXファイルまたはディレクトリが存在しません。('%s')
        		throw new FileNotFoundException(String.format(ImportPicture.i18n.getString("msg.100"), gpxDir.getAbsolutePath()));
        	}
        }
        else if (gpxDir.isDirectory()) {
        	// 指定されたディレクトリ内のGPXファイルすべてを対象とする
            gpxfiles = gpxDir.listFiles(new GpxFileFilter());
            if (gpxfiles == null) {
            	// 対象となるGPXファイルがありませんでした。('%s')
            	throw new FileNotFoundException(
                    String.format(ImportPicture.i18n.getString("msg.110"), this.gpxDir.getAbsolutePath())
                );
            }
            if (params.isImgOutputAll() && (gpxfiles.length > 1)) {
                // "複数のGPXファイルがあるときには、'IMG.OUTPUT_ALL'オプションは指定できません。"
            	throw new FileNotFoundException(
                    String.format(ImportPicture.i18n.getString("msg.120"))
                );
            }
        }
        else {
        	// GPXファイルまたはディレクトリが存在しません。('%s')
    		throw new FileNotFoundException(String.format(ImportPicture.i18n.getString("msg.100"), gpxDir.getAbsolutePath()));
        }

        Arrays.sort(gpxfiles, new FileSort());
	}
	
	/**
	 * 対象は '*.GPX' のみ対象とする
	 */
	public static boolean accept(AppParameters params, String name) {
		String filename = name.toUpperCase();
        if (filename.endsWith(".GPX")) {
            if (!filename.endsWith("_.GPX") || params.isGpxReuse()) {
            	return true;
            }
        }
        return false;
	}
    
    /**
     * ファイル名の順序に並び替えるためのソートクラス
     * 
     */
    static class FileSort implements Comparator<File> {
        @Override
        public int compare(File src, File target){
            int diff = src.getName().compareTo(target.getName());
            return diff;
        }
    }

    /**
     * GPXファイルフィルター
     */
    class GpxFileFilter implements FilenameFilter {
    	@Override
        public boolean accept(File dir, String name) {
    		return GpxFolder.accept(params, name);
    	}
    }
}
