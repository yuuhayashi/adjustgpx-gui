package osm.jp.gpx;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.parsers.*;
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

public class ImportPicture extends Thread {
    
    /**
     * 実行中に発生したExceptionを保持する場所
     */
    public Exception ex = null;
	
    /**
     * ログ設定プロパティファイルのファイル内容
     */
    protected static final String LOGGING_PROPERTIES_DATA
        = "handlers=java.util.logging.ConsoleHandler\n"
        + ".level=FINEST\n"
        + "java.util.logging.ConsoleHandler.level=INFO\n"
        + "java.util.logging.ConsoleHandler.formatter=osm.jp.gpx.YuuLogFormatter";

    /**
     * static initializer によるログ設定の初期化
     */
    public static final Logger LOGGER = Logger.getLogger("CommandLogging");
    static {
        try (InputStream inStream = new ByteArrayInputStream(LOGGING_PROPERTIES_DATA.getBytes("UTF-8"))) {
            try {
                LogManager.getLogManager().readConfiguration(inStream);
                // "ログ設定: LogManagerを設定しました。"
                LOGGER.config("LoggerSettings: LogManager setuped.");
            }
            catch (IOException e) {
                // LogManager設定の際に例外が発生しました.
                String str = "LoggerSettings: Exception occered:" + e.toString();
                LOGGER.warning(str);
            }
        }
        catch (UnsupportedEncodingException e) {
            String str = "LoggerSettings: Not supported 'UTF-8' encoding: " + e.toString();
            LOGGER.severe(str);
        } catch (IOException e1) {
            LOGGER.severe(e1.toString());
		}
    }
    
    /** メイン
     * 画像ファイルをGPXファイルに取り込みます。
     * 
     * ・画像ファイルの更新日付をその画像の撮影日時とします。(Exi情報は無視します)
     *    ※ 対象とするファイルは'*.jpg'のみ
     * ・精確な時刻との時差を入力することで、撮影日時を補正します。
     * ・画像ファイルの更新日付リストをCSV形式のファイルとして出力する。
     * ・・結果は、取り込み元のGPXファイルとは別に、元ファイル名にアンダーバー「_」を付加した.ファイルに出力します。
     * 
     *  exp) $ java -cp .:AdjustTime.jar:commons-imaging-1.0-SNAPSHOT.jar [AdjustTime.ini]
     *  exp) > java -cp .;AdjustTime.jar;commons-imaging-1.0-SNAPSHOT.jar [AdjustTime.ini]
     *
     * @param argv
     * argv[0] = INIファイルのパス名
     * 
     * @throws IOException
     * @throws ImageReadException 
     */
    public static void main(String[] argv) throws Exception
    {
        ImportPicture obj = new ImportPicture();
        obj.setUp(((argv.length < 1) ? AppParameters.FILE_PATH : argv[0]));
    }
    
    public File gpxDir;
    public File imgDir;
    public File outDir;
    public long delta = 0;
    public boolean exif = false;
    public boolean exifBase = false;
    public ArrayList<File> gpxFiles = new ArrayList<>();
    public AppParameters params;
    public boolean param_GpxSplit = false;
    public static boolean param_GpxNoFirstNode = false;
    public boolean param_GpxReuse = false;
    public boolean param_GpxOutputWpt = true;
    public boolean param_ImgOutputAll = false;
    public String param_GpxSourceFolder = ".";
    
    public static final String TIME_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String EXIF_DATE_TIME_FORMAT_STRING = "yyyy:MM:dd HH:mm:ss";
    public ResourceBundle i18n = ResourceBundle.getBundle("i18n");
    
    public void setUp(String paramFilePath) throws Exception {
        System.out.println("Param File = '"+ paramFilePath +"'");
        this.params = new AppParameters(paramFilePath);

        Date imgtime;

        System.out.println(" - param： "+ AppParameters.IMG_TIME +"="+ this.params.getProperty(AppParameters.IMG_TIME) );
        System.out.println(" - param： "+ AppParameters.IMG_BASE_FILE +"="+ this.params.getProperty(AppParameters.IMG_BASE_FILE) );
        System.out.println(" - param： "+ AppParameters.GPX_BASETIME +"="+ this.params.getProperty(AppParameters.GPX_BASETIME) );
        System.out.println(" - param： "+ AppParameters.IMG_SOURCE_FOLDER +"="+ this.params.getProperty(AppParameters.IMG_SOURCE_FOLDER) );
        System.out.println(" - param： "+ AppParameters.IMG_OUTPUT_FOLDER +"="+ this.params.getProperty(AppParameters.IMG_OUTPUT_FOLDER) );
        System.out.println(" - param： "+ AppParameters.IMG_OUTPUT +"="+ this.params.getProperty(AppParameters.IMG_OUTPUT));     
        System.out.println(" - param： "+ AppParameters.IMG_OUTPUT_ALL +"="+ this.param_ImgOutputAll);
        System.out.println(" - param： "+ AppParameters.IMG_OUTPUT_EXIF +"= "+ String.valueOf(this.exif));
        System.out.println(" - param： "+ AppParameters.GPX_SOURCE_FOLDER +"="+ this.params.getProperty(AppParameters.GPX_SOURCE_FOLDER) );
        System.out.println(" - param： "+ AppParameters.GPX_OVERWRITE_MAGVAR +"="+ Complementation.param_GpxOverwriteMagvar);
        System.out.println(" - param： "+ AppParameters.GPX_OUTPUT_SPEED +"="+ Complementation.param_GpxOutputSpeed);
        System.out.println(" - param： "+ AppParameters.GPX_GPXSPLIT +"="+ this.param_GpxSplit);
        System.out.println(" - param： "+ AppParameters.GPX_NO_FIRST_NODE +"="+ ImportPicture.param_GpxNoFirstNode);        

        this.ex = null;
        // argv[0] --> AppParameters.IMG_SOURCE_FOLDER に置き換え
        this.imgDir = new File(this.params.getProperty(AppParameters.IMG_SOURCE_FOLDER));

        // 基準時刻（ファイル更新日時 | EXIF撮影日時)
    	this.exifBase = (this.params.getProperty(AppParameters.GPX_BASETIME).equals("EXIF_TIME"));

        // 基準時刻ファイルの「更新日時」を使って時刻合わせを行う。
        // argv[1] --> AppParameters.IMG_BASE_FILE に置き換え
    	imgtime = this.adjustTime(new File(this.imgDir, this.params.getProperty(AppParameters.IMG_BASE_FILE)));

        // 出力ファイル
        // argv[3] --> AppParameters.IMG_OUTPUT に置き換え
        this.outDir = new File(this.params.getProperty(AppParameters.IMG_OUTPUT_FOLDER));

        // その他のパラメータを読み取る
    	String paramStr = this.params.getProperty(AppParameters.GPX_GPXSPLIT);
    	if ((paramStr != null) && (paramStr.equals(Boolean.toString(true)))) {
            this.param_GpxSplit = true;
    	}
        
    	paramStr = this.params.getProperty(AppParameters.GPX_NO_FIRST_NODE);
    	if ((paramStr != null) && (paramStr.equals(Boolean.toString(true)))) {
            ImportPicture.param_GpxNoFirstNode = true;
    	}
    	
    	paramStr = this.params.getProperty(AppParameters.IMG_OUTPUT_ALL);
    	if ((paramStr != null) && (paramStr.equals(Boolean.toString(true)))) {
            this.param_ImgOutputAll = true;
    	}

    	paramStr = this.params.getProperty(AppParameters.GPX_OVERWRITE_MAGVAR);
    	if ((paramStr != null) && (paramStr.equals(Boolean.toString(true)))) {
            Complementation.param_GpxOverwriteMagvar = true;
    	}

    	paramStr = this.params.getProperty(AppParameters.GPX_OUTPUT_SPEED);
    	if ((paramStr != null) && (paramStr.equals(Boolean.toString(true)))) {
            Complementation.param_GpxOutputSpeed = true;
    	}

    	paramStr = this.params.getProperty(AppParameters.GPX_SOURCE_FOLDER);
    	if (paramStr != null) {
            this.param_GpxSourceFolder = paramStr;
            this.gpxDir = new File(this.param_GpxSourceFolder);
            if (!this.gpxDir.exists()) {
            	// GPXファイルまたはディレクトリが存在しません。('%s')
                System.out.println(
                    String.format(i18n.getString("msg.100"), paramStr)
                );
            	return;
            }
    	}
        else {
            this.gpxDir = this.imgDir;
        }

    	// 指定されたディレクトリ内のGPXファイルすべてを対象とする
        if (this.gpxDir.isDirectory()) {
            File[] files = this.gpxDir.listFiles();
            if (files == null) {
            	// 対象となるGPXファイルがありませんでした。('%s')
            	System.out.println(
                    String.format(i18n.getString("msg.110"), this.gpxDir.getAbsolutePath())
                );
            	return;
            }
            if (this.param_ImgOutputAll && (files.length > 1)) {
                // "複数のGPXファイルがあるときには、'IMG.OUTPUT_ALL'オプションは指定できません。"
            	System.out.println(
                    i18n.getString("msg.120")
                );
            	return;
            }
            
            java.util.Arrays.sort(
                files, new java.util.Comparator<File>() {
                    @Override
                    public int compare(File file1, File file2){
                        return file1.getName().compareTo(file2.getName());
                    }
                }
            );
            for (File file : files) {
                if (file.isFile()) {
                    String filename = file.getName().toUpperCase();
                    if (filename.toUpperCase().endsWith(".GPX")) {
                        if (!filename.toUpperCase().endsWith("_.GPX") || this.param_GpxReuse) {
                            this.gpxFiles.add(file);
                        }
                    }
                }
            }
        }
        else {
            this.gpxFiles.add(this.gpxDir);
        }

    	paramStr = this.params.getProperty(AppParameters.IMG_OUTPUT_EXIF);
    	if ((paramStr != null) && (paramStr.equals(Boolean.toString(true)))) {
            this.exif = true;
    	}
    	
        String timeStr = this.params.getProperty(AppParameters.IMG_TIME);
        try {
            Date t = toUTCDate(timeStr);
            this.delta = t.getTime() - imgtime.getTime();
        }
        catch (ParseException e) {
            // "'%s'の書式が違います(%s)"
            System.out.println(
                String.format(
                    i18n.getString("msg.130"),
                    timeStr,
                    TIME_FORMAT_STRING
                )
            );
            return;
        }

        this.start();
        try {
            this.join();
        } catch(InterruptedException end) {}
        if (this.ex != null) {
            throw this.ex;
        }
    }
    
    /**
     * @code{
        <wpt lat="35.25714922" lon="139.15490497">
            <ele>62.099998474121094</ele>
            <time>2012-06-11T00:44:38Z</time>
            <hdop>0.75</hdop>
            <name><![CDATA[写真]]></name>
            <cmt><![CDATA[精度: 3.0m]]></cmt>
            <link href="2012-06-11_09-44-38.jpg">
                <text>2012-06-11_09-44-38.jpg</text>
            </link>
            <sat>9</sat>
        </wpt>
     * }
     */
    @Override
    public void run() {
        try {
            if (params.getProperty(AppParameters.IMG_OUTPUT).equals(Boolean.toString(true))) {
                outDir = new File(outDir, imgDir.getName());
            }
            else {
                outDir = gpxDir;
            }
            for (File gpxFile : this.gpxFiles) {
            	procGPXfile(new GpxFile(gpxFile));
            }
        }
        catch(ParserConfigurationException | SAXException | IOException | ParseException | ImageReadException | ImageWriteException | IllegalArgumentException | TransformerException e) {
            e.printStackTrace();
            this.ex = new Exception(e);
        }
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

        System.out.println("time difference: "+ (delta / 1000) +"(sec)");
        System.out.println("     Target GPX: ["+ gpxFile.getAbsolutePath() +"]");
        System.out.println("           EXIF: "+ (exif ? ("convert to '" + outDir.getAbsolutePath() +"'") : "off"));
        System.out.println();

        // imgDir内の画像ファイルを処理する
        System.out.println("|--------------------------------|--------------------|--------------------|--------------|--------------|--------|------|------|");
        System.out.println("| name                           | Camera Time        | GPStime            |   Latitude   |   Longitude  | ele    |magvar| km/h |");
        System.out.println("|--------------------------------|--------------------|--------------------|--------------|--------------|--------|------|------|");
        proc(imgDir, delta, seg, exif, gpxFile);
        System.out.println("|--------------------------------|--------------------|--------------------|--------------|--------------|--------|------|------|");
    }
	
    /**
     * 再帰メソッド
     * @throws ParseException 
     * @throws IOException 
     * @throws ImageReadException 
     * @throws ImageWriteException 
     */
    boolean proc(File imgDir, long delta, ElementMapTRKSEG mapTRKSEG, boolean exifWrite, GpxFile gpxFile) throws ParseException, ImageReadException, IOException, ImageWriteException {
        boolean ret = false;
        File[] imgfiles = imgDir.listFiles(new JpegFileFilter());
        Arrays.sort(imgfiles, new FileSort());
        for (File image : imgfiles) {
            System.out.print(String.format("|%-32s|", image.getName()));
            if (image.isDirectory()) {
                ret = proc(image, delta, mapTRKSEG, exifWrite, gpxFile);
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
    
    Discripter procImageFile(File imageFile, long delta, ElementMapTRKSEG mapTRKSEG, boolean exifWrite, GpxFile gpxFile) throws ParseException, ImageReadException, IOException, ImageWriteException {
        Discripter result = new Discripter(false);
        
        // itime <-- 画像ファイルの撮影時刻
        //			ファイルの更新日時／EXIFの撮影日時
        Date itime = new Date(imageFile.lastModified());
        if (this.exifBase) {
            ImageMetadata meta = Imaging.getMetadata(imageFile);
            JpegImageMetadata jpegMetadata = (JpegImageMetadata)meta;
            if (jpegMetadata == null) {
                // "'%s'にEXIF情報がありません"
                System.out.println(
                    String.format(
                        i18n.getString("msg.140"), 
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
                        i18n.getString("msg.140"), 
                        imageFile.getAbsolutePath()
                    )
                );
                result.control = Discripter.CONTINUE;
                return result;
            }
            String dateTimeOriginal = exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)[0];
            itime = ImportPicture.toEXIFDate(dateTimeOriginal);
        }
        System.out.print(String.format("%20s|", toUTCString(itime)));

        // uktime <-- 画像撮影時刻に対応するGPX時刻(補正日時)
        Date correctedtime = new Date(itime.getTime() + delta);
        System.out.print(String.format("%20s|", toUTCString(correctedtime)));

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
            if (!this.param_ImgOutputAll) {
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
            if (this.param_ImgOutputAll) {
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
    
    // 基準時刻ファイルの「更新日時」を使って時刻合わせを行う。
    // argv[1] --> AppParameters.IMG_BASE_FILE に置き換え
    // File baseFile = new File(this.imgDir, this.params.getProperty(AppParameters.IMG_BASE_FILE));
    private Date adjustTime(File baseFile) throws ImageReadException, IOException, ParseException {
        if (exifBase) {
            ImageMetadata meta = Imaging.getMetadata(baseFile);
            JpegImageMetadata jpegMetadata = (JpegImageMetadata)meta;
            if (jpegMetadata == null) {
                // "'%s'にEXIF情報がありません"
                System.out.println(
                    String.format(
                        i18n.getString("msg.140"), 
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
                        i18n.getString("msg.140"), 
                        baseFile.getAbsolutePath()
                    )
                );
                return null;
            }
            String dateTimeOriginal = exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)[0];
            return new Date(ImportPicture.toEXIFDate(dateTimeOriginal).getTime());
        }
        else {
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

    /**
     * DateをEXIFの文字列に変換する。
     * 注意：EXiFの撮影時刻はUTC時間ではない
     * @param localdate
     * @return
     */
    public static String toEXIFString(Date localdate) {
    	DateFormat dfUTC = new SimpleDateFormat(EXIF_DATE_TIME_FORMAT_STRING);
    	return dfUTC.format(localdate);
    }
    
    /**
     * EXIFの文字列をDateに変換する。
     * 注意：EXiFの撮影時刻はUTC時間ではない
     * @param timeStr
     * @return
     * @throws ParseException
     */
    public static Date toEXIFDate(String timeStr) throws ParseException {
    	DateFormat dfUTC = new SimpleDateFormat(EXIF_DATE_TIME_FORMAT_STRING);
    	//dfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return dfUTC.parse(timeStr);
    }
	
    public static String toUTCString(Date localdate) {
    	DateFormat dfUTC = new SimpleDateFormat(TIME_FORMAT_STRING);
    	dfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return dfUTC.format(localdate);
    }
	
    public static Date toUTCDate(String timeStr) throws ParseException {
    	DateFormat dfUTC = new SimpleDateFormat(TIME_FORMAT_STRING);
    	dfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return dfUTC.parse(timeStr);
    }
	
    static String getShortPathName(File dir, File iFile) {
        String dirPath = dir.getAbsolutePath();
        String filePath = iFile.getAbsolutePath();
        if (filePath.startsWith(dirPath)) {
            return filePath.substring(dirPath.length()+1);
        }
        else {
            return filePath;
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