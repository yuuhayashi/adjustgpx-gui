package osm.jp.gpx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.xml.sax.SAXException;

public class ImgFolder extends ArrayList<File> {
	File[] imgfiles;
    AppParameters params;
    File imgDir;
    File outDir;
	
	public ImgFolder(AppParameters params) {
		this.params = params;
		imgDir = params.getImgSourceFolder();
        imgfiles = imgDir.listFiles(new JpegFileFilter());
        Arrays.sort(imgfiles, new FileSort());
	}
	
	public ImgFolder setParams(AppParameters params) {
		this.params = params;
		return this;
	}
	
	public void setOutDir(File outDir) {
		this.outDir = outDir;
	}

	public File getImgDir() {
		return this.imgDir;
	}
	
	public File getImgBaseFile() {
		return new File(imgDir, params.getProperty(AppParameters.IMG_BASE_FILE));
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
    void procGPXfile(GpxFile gpxFile) throws ParserConfigurationException, SAXException, IOException, ParseException, ImageReadException, ImageWriteException, TransformerException {
        System.gc();
        
        ElementMapTRKSEG seg = gpxFile.parse();

        long delta = 0;
        String timeStr = params.getProperty(AppParameters.IMG_TIME);
        try {
            Date t = ImportPicture.toUTCDate(timeStr);

            // 基準時刻ファイルの「更新日時」を使って時刻合わせを行う。
            // argv[1] --> AppParameters.IMG_BASE_FILE に置き換え
        	Date imgtime = adjustTime(getImgBaseFile());
            delta = t.getTime() - imgtime.getTime();
        }
        catch (ParseException e) {
            // "'%s'の書式が違います(%s)"
            System.out.println(
                String.format(
                    ImportPicture.i18n.getString("msg.130"),
                    timeStr,
                    ImportPicture.TIME_FORMAT_STRING
                )
            );
            return;
        }

        
        System.out.println("time difference: "+ (delta / 1000) +"(sec)");
        System.out.println("     Target GPX: ["+ gpxFile.getAbsolutePath() +"]");
        System.out.println("           EXIF: "+ (params.isImgOutputExif() ? ("convert to '" + outDir.getAbsolutePath() +"'") : "off"));
        System.out.println();

        // imgDir内の画像ファイルを処理する
        System.out.println("|--------------------------------|--------------------|--------------------|--------------|--------------|--------|------|------|");
        System.out.println("| name                           | Camera Time        | GPStime            |   Latitude   |   Longitude  | ele    |magvar| km/h |");
        System.out.println("|--------------------------------|--------------------|--------------------|--------------|--------------|--------|------|------|");
        
        proc(delta, seg, params.isImgOutputExif(), gpxFile);

        System.out.println("|--------------------------------|--------------------|--------------------|--------------|--------------|--------|------|------|");
    }

    /**
     * 再帰メソッド
     * @throws ParseException 
     * @throws IOException 
     * @throws ImageReadException 
     * @throws ImageWriteException 
     */
    boolean proc(long delta, ElementMapTRKSEG mapTRKSEG, boolean exifWrite, GpxFile gpxFile) throws ParseException, ImageReadException, IOException, ImageWriteException {
        boolean ret = false;
        for (File image : imgfiles) {
            System.out.print(String.format("|%-32s|", image.getName()));
            if (image.isDirectory()) {
                ret = (new ImgFolder(params)).proc(delta, mapTRKSEG, exifWrite, gpxFile);
                continue;
            }
            
            String imageName = image.getName();
            if (!checkFile(imageName)) {
                System.out.println(String.format("%20s ", "it is not image file."));
            	continue;
            }
            
            Discripter result = procImageFile(image, delta, mapTRKSEG, exifWrite, gpxFile);
            ret |= result.ret;
            switch (result.control) {
            case Discripter.CONTINUE:
            	continue;
            case Discripter.BREAK:
            	break;
            }
        }
        return ret;
    }

    Discripter procImageFile(File imageFile, long delta, ElementMapTRKSEG mapTRKSEG, boolean exifWrite, GpxFile gpxFile) throws ParseException, ImageReadException, IOException, ImageWriteException {
        Discripter result = new Discripter(false);
        
        // itime <-- 画像ファイルの撮影時刻
        //			ファイルの更新日時／EXIFの撮影日時
        Date itime = new Date(imageFile.lastModified());
        if (params.isExifBase()) {
            // 基準時刻（EXIF撮影日時)
            ImageMetadata meta = Imaging.getMetadata(imageFile);
            JpegImageMetadata jpegMetadata = (JpegImageMetadata)meta;
            if (jpegMetadata == null) {
                // "'%s'にEXIF情報がありません"
                System.out.println(
                    String.format(
                        ImportPicture.i18n.getString("msg.140"), 
                        imageFile.getAbsolutePath()
                    )
                );
                result.control = Discripter.CONTINUE;
                return result;
            }
            TiffImageMetadata exif = jpegMetadata.getExif();
            if (exif == null) {
                // "'%s'にEXIF情報がありません"
                System.out.println(
                    String.format(
                    	ImportPicture.i18n.getString("msg.140"), 
                        imageFile.getAbsolutePath()
                    )
                );
                result.control = Discripter.CONTINUE;
                return result;
            }
            String dateTimeOriginal = exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)[0];
            itime = ImportPicture.toEXIFDate(dateTimeOriginal);
        }
        System.out.print(String.format("%20s|", ImportPicture.toUTCString(itime)));

        // uktime <-- 画像撮影時刻に対応するGPX時刻(補正日時)
        Date correctedtime = new Date(itime.getTime() + delta);
        System.out.print(String.format("%20s|", ImportPicture.toUTCString(correctedtime)));

        // 時刻uktimeにおける<magver>をtrkptに追加する
        String eleStr = "-";
        String magvarStr = "-";
        String speedStr = "-";
        TagTrkpt trkptT = null;

        for (Map.Entry<Date,ElementMapTRKPT> map : mapTRKSEG.entrySet()) {
            ElementMapTRKPT mapTRKPT = map.getValue();
            trkptT = mapTRKPT.getValue(correctedtime);
            if (trkptT != null) {
                break;
            }
        }

        if (trkptT == null) {
            System.out.print(String.format("%-14s|%-14s|", "", ""));
            System.out.println(String.format("%8s|%6s|%6s|", "", "", ""));
            if (!params.isImgOutputAll()) {
                result.control = Discripter.CONTINUE;
                return result;
            }
        }
        else {
            double latitude = trkptT.lat;
            double longitude = trkptT.lon;
            
            if (trkptT.eleStr != null) {
            	eleStr = trkptT.eleStr;
            }
            
            if (trkptT.magvarStr != null) {
            	magvarStr = trkptT.magvarStr;
            }
            
            if (trkptT.speedStr != null) {
            	speedStr = trkptT.speedStr;
            }
            System.out.print(String.format("%14.10f|%14.10f|", latitude, longitude));
            System.out.println(String.format("%8s|%6s|%6s|", eleStr, magvarStr, speedStr));
        }

        result.ret = true;
        outDir.mkdir();

        if (exifWrite) {
            exifWrite(imageFile, correctedtime, trkptT);
        }
        else {
            if (params.isImgOutputAll()) {
                // EXIFの変換を伴わない単純なファイルコピー
                FileInputStream sStream = new FileInputStream(imageFile);
                FileInputStream dStream = new FileInputStream(new File(outDir, imageFile.getName()));
                FileChannel srcChannel = sStream.getChannel();
                FileChannel destChannel = dStream.getChannel();
                try {
                    srcChannel.transferTo(0, srcChannel.size(), destChannel);
                }
                finally {
                    srcChannel.close();
                    destChannel.close();
                    sStream.close();
                    dStream.close();
                }
            }
        }
        result.control = Discripter.NEXT;
        return result;
    }
    
    void exifWrite(File imageFile, Date correctedtime, TagTrkpt trkptT) throws ImageReadException, IOException, ImageWriteException {
        DecimalFormat yearFormatter = new DecimalFormat("0000");
        DecimalFormat monthFormatter = new DecimalFormat("00");
        DecimalFormat dayFormatter = new DecimalFormat("00");
        
        TiffOutputSet outputSet = null;

        ImageMetadata meta = Imaging.getMetadata(imageFile);
        JpegImageMetadata jpegMetadata = (JpegImageMetadata)meta;
        if (jpegMetadata != null) {
            TiffImageMetadata exif = jpegMetadata.getExif();
            if (exif != null) {
                outputSet = exif.getOutputSet();
            }
        }

        if (outputSet == null) {
            outputSet = new TiffOutputSet();
        }

        //---- EXIF_TAG_DATE_TIME_ORIGINAL / 「撮影日時/オリジナル画像の生成日時」----
        TiffOutputDirectory exifDir = outputSet.getOrCreateExifDirectory();
        {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.setTime(correctedtime);
            exifDir.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
            exifDir.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, ImportPicture.toEXIFString(cal.getTime()));
        }

        //---- EXIF GPS_TIME_STAMP ----
        TiffOutputDirectory gpsDir = outputSet.getOrCreateGPSDirectory();
        {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTimeZone(TimeZone.getTimeZone("GMT+00"));
            cal.setTime(correctedtime);
            final String yearStr = yearFormatter.format(cal.get(Calendar.YEAR));
            final String monthStr = monthFormatter.format(cal.get(Calendar.MONTH) + 1);
            final String dayStr = dayFormatter.format(cal.get(Calendar.DAY_OF_MONTH));
            final String dateStamp = yearStr +":"+ monthStr +":"+ dayStr;

            gpsDir.removeField(GpsTagConstants.GPS_TAG_GPS_TIME_STAMP);
            gpsDir.add(
                GpsTagConstants.GPS_TAG_GPS_TIME_STAMP,
                RationalNumber.valueOf(cal.get(Calendar.HOUR_OF_DAY)),
                RationalNumber.valueOf(cal.get(Calendar.MINUTE)),
                RationalNumber.valueOf(cal.get(Calendar.SECOND))
            );
            gpsDir.removeField(GpsTagConstants.GPS_TAG_GPS_DATE_STAMP);
            gpsDir.add(GpsTagConstants.GPS_TAG_GPS_DATE_STAMP, dateStamp);
        }

        if (trkptT != null) {
            //---- EXIF GPS elevation/ALTITUDE ----
            if (trkptT.eleStr != null) {
                final double altitude = Double.parseDouble(trkptT.eleStr);
                gpsDir.removeField(GpsTagConstants.GPS_TAG_GPS_ALTITUDE);
                gpsDir.add(GpsTagConstants.GPS_TAG_GPS_ALTITUDE, RationalNumber.valueOf(altitude));
            }

            //---- EXIF GPS magvar/IMG_DIRECTION ----
            if (trkptT.magvarStr != null) {
                final double magvar = Double.parseDouble(trkptT.magvarStr);
                gpsDir.removeField(GpsTagConstants.GPS_TAG_GPS_IMG_DIRECTION);
                gpsDir.add(GpsTagConstants.GPS_TAG_GPS_IMG_DIRECTION, RationalNumber.valueOf(magvar));
            }

            //---- EXIF GPS_ ----
            outputSet.setGPSInDegrees(trkptT.lon, trkptT.lat);
        }

        ExifRewriter rewriter = new ExifRewriter();
        try (FileOutputStream fos = new FileOutputStream(new File(outDir, imageFile.getName()))) {
            rewriter.updateExifMetadataLossy(imageFile, fos, outputSet);
        }
    }
    
    /**
     * 基準時刻ファイルの「更新日時」を使って時刻合わせを行う。
     * @param baseFile = new File(this.imgDir, this.params.getProperty(AppParameters.IMG_BASE_FILE));
     * @return
     * @throws ImageReadException
     * @throws IOException
     * @throws ParseException
     */
    private Date adjustTime(File baseFile) throws ImageReadException, IOException, ParseException {
        if (params.isExifBase()) {
            // 基準時刻（EXIF撮影日時)
            ImageMetadata meta = Imaging.getMetadata(baseFile);
            JpegImageMetadata jpegMetadata = (JpegImageMetadata)meta;
            if (jpegMetadata == null) {
                // "'%s'にEXIF情報がありません"
                System.out.println(
                    String.format(
                        ImportPicture.i18n.getString("msg.140"), 
                        baseFile.getAbsolutePath()
                    )
                );
                return null;
            }
            TiffImageMetadata exif = jpegMetadata.getExif();
            if (exif == null) {
                // "'%s'にEXIF情報がありません"
                System.out.println(
                    String.format(
                    	ImportPicture.i18n.getString("msg.140"), 
                        baseFile.getAbsolutePath()
                    )
                );
                return null;
            }
            String dateTimeOriginal = exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)[0];
            return new Date(ImportPicture.toEXIFDate(dateTimeOriginal).getTime());
        }
        else {
            // 基準時刻（ファイル更新日時)
            return new Date(baseFile.lastModified());
        }
    }
    
    /**
     * 対象は '*.JPG' のみ対象とする
     * @return 
     * @param name
     */
    public static boolean checkFile(String name) {
        return ((name != null) && name.toUpperCase().endsWith(".JPG"));
    }
    

    private static final long serialVersionUID = -1137199371724546343L;

    class Discripter {
    	static final int NEXT = 0;
    	static final int CONTINUE = -1;
    	static final int BREAK = 1;
    	
    	public boolean ret;
    	public int control;
    	public Discripter(boolean ret) {
            this.ret = ret;
            this.control = Discripter.NEXT;
    	}
    }
        
    /**
     * ファイル名の順序に並び替えるためのソートクラス
     * 
     * @author hayashi
     */
    static class FileSort implements Comparator<File> {
        @Override
        public int compare(File src, File target){
            int diff = src.getName().compareTo(target.getName());
            return diff;
        }
    }

    /**
     * JPEGファイルフィルター
     * @author yuu
     */
    class JpegFileFilter implements FilenameFilter {
    	@Override
        public boolean accept(File dir, String name) {
            return name.toUpperCase().matches(".*\\.JPG$");
    	}
    }

}
