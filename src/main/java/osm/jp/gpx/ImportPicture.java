package osm.jp.gpx;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
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
    public ImgFolder imgFolder;
    //public File outDir;
    public ArrayList<File> gpxFiles = new ArrayList<>();
    public AppParameters params;
    
    private static final String EXIF_DATE_TIME_FORMAT_STRING = "yyyy:MM:dd HH:mm:ss";
    public static final ResourceBundle i18n = ResourceBundle.getBundle("i18n");
    
    public void setUp(String paramFilePath) throws Exception {
        System.out.println("Param File = '"+ paramFilePath +"'");
        this.params = new AppParameters(paramFilePath);
        params.printout();

        this.ex = null;
        
        // AppParameters.IMG_SOURCE_FOLDER に置き換え
        imgFolder = new ImgFolder(params);
        
        this.gpxDir = params.getGpxSourceFolder();
    	if (this.gpxDir != null) {
            if (!this.gpxDir.exists()) {
            	// GPXファイルまたはディレクトリが存在しません。('%s')
                System.out.println(
                    String.format(i18n.getString("msg.100"), gpxDir.getAbsolutePath())
                );
            	return;
            }
    	}
        else {
            this.gpxDir = imgFolder.getImgDir();
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
            if (params.isImgOutputAll() && (files.length > 1)) {
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
                        if (!filename.toUpperCase().endsWith("_.GPX") || params.isGpxReuse()) {
                            this.gpxFiles.add(file);
                        }
                    }
                }
            }
        }
        else {
            this.gpxFiles.add(this.gpxDir);
        }
        
        // 出力ファイル
        // AppParameters.IMG_OUTPUT に置き換え
        File outDir = new File(params.getProperty(AppParameters.IMG_OUTPUT_FOLDER));
        if (params.isImgOutput()) {
            outDir = new File(outDir, imgFolder.getImgDir().getName());
        }
        else {
            outDir = gpxDir;
        }
        imgFolder.setOutDir(outDir);

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
            for (File gpxFile : this.gpxFiles) {
            	imgFolder.procGPXfile(new GpxFile(params, gpxFile));
            }
        }
        catch(ParserConfigurationException | SAXException | IOException | ParseException | ImageReadException | ImageWriteException | IllegalArgumentException | TransformerException e) {
            e.printStackTrace();
            this.ex = new Exception(e);
        }
    }
    
	
    
    
    
    public static final String TIME_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static Date toUTCDate(String timeStr) throws ParseException {
    	DateFormat dfUTC = new SimpleDateFormat(TIME_FORMAT_STRING);
    	dfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return dfUTC.parse(timeStr);
    }

    public static String toUTCString(Date localdate) {
    	DateFormat dfUTC = new SimpleDateFormat(TIME_FORMAT_STRING);
    	dfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return dfUTC.format(localdate);
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
}