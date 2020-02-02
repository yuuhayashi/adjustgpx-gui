package osm.jp.gpx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("serial")
public class AppParameters extends Properties {
    static final String FILE_PATH = "AdjustTime.ini";

    // GPX: 時間的に間隔が開いたGPXログを別の<trkseg>セグメントに分割する。 {ON | OFF}
    public static String GPX_GPXSPLIT = "GPX.gpxSplit";

    // GPX: <trkseg>セグメントの最初の１ノードは無視する。 {ON | OFF}
    public static String GPX_NO_FIRST_NODE = "GPX.noFirstNode";

    // GPX: 生成されたGPXファイル（ファイル名が'_.gpx'で終わるもの）も対象にする。 {ON | OFF}
    public static String GPX_REUSE = "GPX.REUSE";

    // GPX: 基準時刻 {FILE_UPDATE | EXIF_TIME}
    public static String GPX_BASETIME = "GPX.BASETIME";

    // GPX: ファイル更新時刻 yyyy:MM:dd HH:mm:ss
    public static String IMG_TIME = "IMG.TIME";

    // 対象IMGフォルダ:(位置情報を付加したい画像ファイルが格納されているフォルダ)
    public static String IMG_SOURCE_FOLDER = "IMG.SOURCE_FOLDER";

    // 基準時刻画像(正確な撮影時刻が判明できる画像)
    public static String IMG_BASE_FILE = "IMG.BASE_FILE";

    // 対象GPXフォルダ:(GPXファイルが格納されているフォルダ)
    public static String GPX_SOURCE_FOLDER = "GPX.SOURCE_FOLDER";

    // 出力フォルダ:(変換した画像ファイルとGPXファイルを出力するフォルダ)
    public static String IMG_OUTPUT_FOLDER = "IMG.OUTPUT_FOLDER";

    // 出力IMG: IMG出力をする  {ON | OFF}
    public static String IMG_OUTPUT = "IMG.OUTPUT";

    // 出力IMG: 'out of time'も IMG出力の対象とする  {ON | OFF}
    // 　　この場合は、対象IMGフォルダ内のすべてのIMGファイルが出力フォルダに出力される
    public static String IMG_OUTPUT_ALL = "IMG.OUTPUT_ALL";

    // 出力IMG: EXIFを変換する
    public static String IMG_OUTPUT_EXIF = "IMG.OUTPUT_EXIF";

    // 出力GPX: <SPEED>を上書き出力する {ON | OFF}
    public static String GPX_OUTPUT_SPEED = "GPX.OUTPUT_SPEED";

    // 出力GPX: ソースGPXの<MAGVER>を無視する {ON | OFF}
    public static String GPX_OVERWRITE_MAGVAR = "GPX.OVERWRITE_MAGVAR";

    // 出力GPX: マーカー<wpt>を出力する {ON | OFF}
    public static String GPX_OUTPUT_WPT = "GPX.OUTPUT_WPT";

    File file;

    public AppParameters() throws FileNotFoundException, IOException {
        super();
        this.file = new File(FILE_PATH);
        syncFile();
    }

    public AppParameters(Properties defaults) throws FileNotFoundException, IOException {
        super(defaults);
        this.file = new File(FILE_PATH);
        syncFile();
    }

    public AppParameters(String iniFileName) throws FileNotFoundException, IOException {
        super();
        this.file = new File(iniFileName);
        syncFile();
    }

    private void syncFile() throws FileNotFoundException, IOException {
        boolean update = false;

        if (this.file.exists()) {
            // ファイルが存在すれば、その内容をロードする。
            this.load(new FileInputStream(file));
        }
        else {
            update = true;
        }

        //------------------------------------------------
        // 対象フォルダ:(位置情報を付加したい画像ファイルが格納されているフォルダ)
        String valueStr = this.getProperty(IMG_SOURCE_FOLDER);
        if (valueStr == null) {
            update = true;
            this.setProperty(IMG_SOURCE_FOLDER, (new File(".")).getAbsolutePath());
        }

        //------------------------------------------------
        // 対象フォルダ:(GPXファイルが格納されているフォルダ)
        valueStr = this.getProperty(GPX_SOURCE_FOLDER);
        if (valueStr == null) {
            update = true;
            this.setProperty(GPX_SOURCE_FOLDER, (new File(".")).getAbsolutePath());
        }

        //------------------------------------------------
        // 基準時刻画像(正確な撮影時刻が判明できる画像)
        valueStr = this.getProperty(IMG_BASE_FILE);
        if (valueStr == null) {
            update = true;
            this.setProperty(IMG_BASE_FILE, "");
        }

        //------------------------------------------------
        // 出力フォルダ:(変換した画像ファイルとGPXファイルを出力するフォルダ)
        valueStr = this.getProperty(IMG_OUTPUT_FOLDER);
        if (valueStr == null) {
            update = true;
            this.setProperty(IMG_OUTPUT_FOLDER, (new File(".")).getAbsolutePath());
        }

        //------------------------------------------------
        // IMG出力: IMGを出力する
        valueStr = this.getProperty(IMG_OUTPUT);
        if (valueStr == null) {
            update = true;
            valueStr = String.valueOf(true);
        }
        this.setProperty(IMG_OUTPUT, String.valueOf(valueStr));

        //------------------------------------------------
        // 出力IMG: 'out of time'も IMG出力の対象とする
        valueStr = this.getProperty(IMG_OUTPUT_ALL);
        if (valueStr == null) {
            update = true;
            valueStr = String.valueOf(false);
        }
        this.setProperty(IMG_OUTPUT_ALL, String.valueOf(valueStr));

        //------------------------------------------------
        // IMG出力: EXIFを変換する
        valueStr = this.getProperty(IMG_OUTPUT_EXIF);
        if (valueStr == null) {
            update = true;
            valueStr = String.valueOf(true);
        }
        this.setProperty(IMG_OUTPUT_EXIF, String.valueOf(valueStr));

        //------------------------------------------------
        // GPX出力: 時間的に間隔が開いたGPXログを別の<trkseg>セグメントに分割する。 {ON | OFF}
        valueStr = this.getProperty(GPX_GPXSPLIT);
        if (valueStr == null) {
            update = true;
            this.setProperty(GPX_GPXSPLIT, String.valueOf(true));
        }

        //------------------------------------------------
        // GPX出力: <trkseg>セグメントの最初の１ノードは無視する。 {ON | OFF}
        valueStr = this.getProperty(GPX_NO_FIRST_NODE);
        if (valueStr == null) {
            update = true;
            this.setProperty(GPX_NO_FIRST_NODE, String.valueOf(true));
        }

        //------------------------------------------------
        // GPX出力: ポイントマーカー<WPT>を出力する {ON | OFF}
        valueStr = this.getProperty(GPX_OUTPUT_WPT);
        if (valueStr == null) {
            update = true;
            this.setProperty(GPX_OUTPUT_WPT, String.valueOf(false));
        }

        //------------------------------------------------
        // GPX出力: ソースGPXの<MAGVAR>を無視する {ON | OFF}
        valueStr = this.getProperty(GPX_OVERWRITE_MAGVAR);
        if (valueStr == null) {
            update = true;
            this.setProperty(GPX_OVERWRITE_MAGVAR, String.valueOf(false));
        }

        //------------------------------------------------
        // GPX出力: <SPEED>を上書き出力する {ON | OFF}
        valueStr = this.getProperty(GPX_OUTPUT_SPEED);
        if (valueStr == null) {
            update = true;
            this.setProperty(GPX_OUTPUT_SPEED, String.valueOf(false));
        }

        //------------------------------------------------
        // GPX出力: 生成されたGPXファイル（ファイル名が'_.gpx'で終わるもの）も対象にする。 {ON | OFF}
        valueStr = this.getProperty(GPX_REUSE);
        if (valueStr == null) {
            update = true;
            this.setProperty(GPX_REUSE, String.valueOf(false));
        }

        //------------------------------------------------
        //  GPX: 基準時刻 {FILE_UPDATE | EXIF}
        valueStr = this.getProperty(GPX_BASETIME);
        if (valueStr == null) {
            update = true;
            this.setProperty(GPX_BASETIME, "FILE_UPDATE");
        }

        if (update) {
            // ・ファイルがなければ新たに作る
            // ・項目が足りない時は書き足す。
            this.store(new FileOutputStream(this.file), "defuilt settings");
        }
    }

    public void store() throws FileNotFoundException, IOException {
        this.store(new FileOutputStream(this.file), "by AdjustTime");
    }
}
