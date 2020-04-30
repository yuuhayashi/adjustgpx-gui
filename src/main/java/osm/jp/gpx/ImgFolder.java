package osm.jp.gpx;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.xml.sax.SAXException;

public class ImgFolder extends ArrayList<ImgFile> {
    private static final long serialVersionUID = -1137199371724546343L;
    AppParameters params;
    File imgDir;
    File outDir;
	
	public ImgFolder(AppParameters params) {
		this.params = params;
		imgDir = params.getImgSourceFolder();
        File[] files = imgDir.listFiles(new ImgFileFilter());
        Arrays.sort(files, new FileSort());
        for (File file : files) {
        	this.add(new ImgFile(file));
        }
	}
	
	public void setOutDir(File outDir) {
		this.outDir = outDir;
	}
	
	public File getOutDir() {
		return this.outDir;
	}

	public File getImgDir() {
		return this.imgDir;
	}
	
    /**
     * 個別のGPXファイルを処理する
     * 
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws ParseException 
     * @throws ImageWriteException 
     * @throws ImageReadException 
     * @throws TransformerException 
     */
    void procGPXfile(GpxFile gpxFile, long delta) throws ParserConfigurationException, SAXException, IOException, ParseException, ImageReadException, ImageWriteException, TransformerException {
        // imgDir内の画像ファイルを処理する
        //System.out.println("|--------------------------------|--------------------|--------------------|--------------|--------------|--------|------|------|");
        //System.out.println("| name                           | Camera Time        | GPStime            |   Latitude   |   Longitude  | ele    |magvar| km/h |");
        //System.out.println("|--------------------------------|--------------------|--------------------|--------------|--------------|--------|------|------|");
        
        //ElementMapTRKSEG seg = gpxFile.parse();
        for (ImgFile image : this) {
        	try {
        		if (!image.isDone()) {
                    if(image.procImageFile(params, delta, gpxFile, outDir)) {
                    	image.setDone(true);
                    }
        		}
        	}
        	catch(Exception e) {
                System.out.print(String.format("%s", e.toString()));
                continue;
        	}
        }

        //System.out.println("|--------------------------------|--------------------|--------------------|--------------|--------------|--------|------|------|");
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
}
