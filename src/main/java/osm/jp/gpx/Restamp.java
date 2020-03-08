package osm.jp.gpx;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageReadException;

/**
 * 動画から一定間隔で切り出したIMAGEファイルの更新日時を書き換える
 * 
 * @author yuu
 */
public class Restamp extends Thread {
    static public final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss z";

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
        InputStream inStream = null;
        try {
            inStream = new ByteArrayInputStream(LOGGING_PROPERTIES_DATA.getBytes("UTF-8"));
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
        }
        finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                String str = "LoggerSettings: Exception occored: "+ e.toString();
                LOGGER.warning(str);
            }
        }
    }

    /**
     * メイン
     * 動画から一定間隔で切り出したIMAGEのファイル更新日時を書き換える
     * 
     * ・画像ファイルの更新日付を書き換えます。(Exi情報は無視します)
     *    ※ 指定されたディレクトリ内のすべての'*.jpg'ファイルを処理の対象とします
     * ・画像は連番形式（名前順に並べられること）の名称となっていること
     * 
     * パラメータ
     * ・対象のフォルダ（ディレクトリ内のすべての'*.jpg'ファイルを処理の対象とします）
     * ・基準となる画像
     * ・基準画像の正しい日時
     * ・画像ファイルの間隔（秒）
     * 
     *  exp) $ java -cp .:AdjustTime.jar:commons-imaging-1.0-SNAPSHOT.jar [AdjustTime.ini]
     *  exp) > java -cp .;AdjustTime.jar;commons-imaging-1.0-SNAPSHOT.jar [AdjustTime.ini]
     * 
     * 1. 予め、動画から画像を切り出す
     * 　　ソースファイル（mp4ファイル）; 「-i 20160427_104154.mp4」
     *     出力先: 「-f image2 img/%06d.jpg」 imgフォルダに６桁の連番ファイルを差出力する
     * 　　切り出し開始秒数→ 「-ss 0」 （ファイルの０秒から切り出し開始）
     * 　　切り出し間隔； 「-r 30」 (１秒間隔=３０fps間隔)
     * ```
     * $ cd /home/yuu/Desktop/OSM/20180325_横浜新道
     * $ ffmpeg -ss 0  -i 20160427_104154.mp4 -f image2 -r 15 img/%06d.jpg
     * ```
     * 
     * 2. ファイルの更新日付を書き換える
     * ```
     * $ cd /home/yuu/Desktop/workspace/AdjustTime/importPicture/dist
     * $ java -cp .:AdjustTime2.jar osm.jp.gpx.Restamp /home/yuu/Desktop/OSM/20180325_横浜新道/img 000033.jpg 2018-03-25_12:20:32 003600.jpg  2018-03-25_13:20:09
     * ```
     * 
     * @param argv
     * argv[0] = 画像ファイルが格納されているディレクトリ		--> imgDir
     * argv[1] = 時刻補正の基準とする画像ファイル			--> baseFile
     * argv[2] = 基準画像ファイルの精確な撮影日時 "yyyy-MM-dd HH:mm:ss z" --> baseTime
     * argv[3] = 時刻補正の基準とする画像ファイル			--> baseFile
     * argv[4] = 基準画像ファイルの精確な撮影日時 "yyyy-MM-dd HH:mm:ss z" --> baseTime
     * 
     * @throws IOException
     * @throws ImageReadException 
     */
    public static void main(String[] argv) throws Exception
    {
        if (argv.length < 5) {
            System.out.println("java Restamp <imgDir> <baseFile1> <timeStr1> <baseFile2> <timeStr2>");
            return;
        }
        
        File imgDir = new File(argv[0]);
        if (!imgDir.exists()) {
            // "[error] <imgDir>が存在しません。"
            System.out.println(i18n.getString("msg.200"));
            return;
        }
        if (!imgDir.isDirectory()) {
            // "[error] <imgDir>がフォルダじゃない"
            System.out.println(i18n.getString("msg.210"));
            return;
        }
        
        File baseFile1 = new File(imgDir, argv[1]);
        if (!baseFile1.exists()) {
            // "[error] <baseFile1>が存在しません。"
            System.out.println(i18n.getString("msg.220"));
            return;
        }
        if (!baseFile1.isFile()) {
            // "[error] <baseFile1>がファイルじゃない"
            System.out.println(i18n.getString("msg.230"));
            return;
        }
        
        DateFormat df1 = new SimpleDateFormat(TIME_PATTERN);
    	Date baseTime1 = df1.parse(argv[2]);

        File baseFile2 = new File(imgDir, argv[3]);
        if (!baseFile2.exists()) {
            // "[error] <baseFile2>が存在しません。"
            System.out.println(i18n.getString("msg.240"));
            return;
        }
        if (!baseFile2.isFile()) {
            // "[error] <baseFile2>がファイルじゃない"
            System.out.println(i18n.getString("msg.250"));
            return;
        }
        
    	Date baseTime2 = df1.parse(argv[4]);

        Restamp obj = new Restamp();
        obj.setUp(imgDir, baseFile1, baseTime1, baseFile2, baseTime2);
    }
    
    public File imgDir;
    public Date baseTime1;
    public Date baseTime2;
    public int bCount1 = 0;
    public int bCount2 = 0;
    public long span = 0;
    public ArrayList<File> jpgFiles = new ArrayList<>();
    public static ResourceBundle i18n = ResourceBundle.getBundle("i18n");
	
    public void setUp(File imgDir, File baseFile1, Date baseTime1,  File baseFile2, Date baseTime2) throws Exception {
    	// 指定されたディレクトリ内のGPXファイルすべてを対象とする
        File[] files = imgDir.listFiles();
        java.util.Arrays.sort(files, new java.util.Comparator<File>() {
            @Override
            public int compare(File file1, File file2){
                return file1.getName().compareTo(file2.getName());
            }
        });
        bCount1 = 0;
        bCount2 = 0;
        boolean base1 = false;
        boolean base2 = false;
        for (File file : files) {
            if (file.isFile()) {
                String filename = file.getName().toUpperCase();
                if (filename.toUpperCase().endsWith(".JPG")) {
                    this.jpgFiles.add(file);
                    bCount1 += (base1 ? 0 : 1);
                    bCount2 += (base2 ? 0 : 1);
                    if (file.getName().equals(baseFile1.getName())) {
                        base1 = true;
                    }
                    if (file.getName().equals(baseFile2.getName())) {
                        base2 = true;
                    }
                }
            }
        }

        try {
            DateFormat df2 = new SimpleDateFormat(TIME_PATTERN);
            
            // imgDir内の画像ファイルを処理する
            long span = baseTime2.getTime() - baseTime1.getTime();
            span = span / (bCount2 - bCount1);
            int i = 0;
            System.out.println("-------------------------------");
            System.out.println("Update last modified date time.");
            for (File jpgFile : this.jpgFiles) {
                long deltaMsec = (i - (bCount1 -1)) * span;
                i++;
                Calendar cal = Calendar.getInstance();
                cal.setTime(baseTime1);
                cal.add(Calendar.MILLISECOND, (int) deltaMsec);
                
                System.out.println(String.format("\t%s --> %s", df2.format(cal.getTime()), jpgFile.getName()));
                jpgFile.setLastModified(cal.getTimeInMillis());
            }
            System.out.println("-------------------------------");
        }
        catch(Exception e) {
            this.ex = new Exception(e);
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