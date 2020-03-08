package osm.jp.gpx;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata.GPSInfo;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.junit.runner.*;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;

public class ImportPictureTest {
    static class Expecter {
        String value;
        boolean expect;
        String timeStr;
        double latD;
        double lonD;
        boolean magvar;

        public Expecter(String value, boolean expect, String timeStr, double latD, double lonD, boolean magvar) {
            this.value = value;
            this.expect = expect;
            this.timeStr = timeStr;
            this.latD = latD;
            this.lonD = lonD;
            this.magvar = magvar;
        }
    }

    static class Fixture {
        String comment;				// テスト概要（コメント）
        String tarFilePath;			// TARデータ
        String gpxSourcePath;		// GPXファイル（オリジナル）
        String gpxDestinationPath;	// GPXファイル（配置先）
        String iniFilePath;			// iniファイル
        Expecter[] expecters;

        public Fixture(
            String comment,
            String tarFilePath,
            String gpxSourcePath,
            String gpxDestinationPath,
            String iniFilePath,
            Expecter[] expecters
        ) {
            this.comment = comment;
            this.tarFilePath = tarFilePath;
            this.gpxSourcePath = gpxSourcePath;
            this.gpxDestinationPath = gpxDestinationPath;
            this.iniFilePath = iniFilePath;
            this.expecters = expecters;
        }

        @Override
        public String toString() {
            String msg = "テストパターン : "+ comment + "\n";
            msg += "\ttarFilePath = "+ tarFilePath +"\n";
            msg += "\tgpxSourcePath = "+ gpxSourcePath +"\n";
            msg += "\tgpxDestinationPath = "+ gpxDestinationPath +"\n";
            msg += "\tiniFilePath = "+ iniFilePath;
            return msg;
        }
    }


    @RunWith(Theories.class)
    public static class 各種カメラGPXファイル {

        @DataPoints
        public static Fixture[] datas = {
            // 1
            new Fixture(
                "[A1].SONYカメラの場合.FILE_UPDATE時間を基準にして時間外のファイルはコピー対象外の時",
                "src/test/data/Sony20170518.tar.gz", 
                "src/test/data/20170518.gpx",
                "src/test/data/cameradata/20170518.gpx",
                "src/test/data/AdjustTime.20170518.A1.ini",
                new Expecter[] {
                    new Expecter("10170518/20170518_.gpx", true, null, 90.0D, 180.0D, false),
                    new Expecter("10170518/DSC05183.JPG", false, null, 90.0D, 180.0D, false),
                    new Expecter("10170518/DSC05184.JPG", true, "2017:05:18 09:34:44", 35.4367520000D, 139.4082730000D, true),
                    new Expecter("10170518/DSC05196.JPG", true, "2017:05:18 09:37:32", 35.4376820000D, 139.4085150000D, true),
                    new Expecter("10170518/DSC05204.JPG", true, "2017:05:18 09:46:48", 35.4368560000D, 139.4082190000D, true),
                    new Expecter("10170518/DSC05205.JPG", false, null, 90.0D, 180.0D, false),
                }
            ),
            // 2
            new Fixture(
                "[A2].SONYカメラの場合.FILE_UPDATE時間を基準にして時間外のファイルもコピーする時",
                "src/test/data/Sony20170518.tar.gz", 
                "src/test/data/20170518.gpx",
                "src/test/data/cameradata/20170518.gpx",
                "src/test/data/AdjustTime.20170518.A2.ini",
                new Expecter[] {
                    new Expecter("10170518/20170518_.gpx", true, null, 90.0D, 180.0D, false),
                    new Expecter("10170518/DSC05183.JPG", true, "2017:05:18 09:16:48", 90.0D, 180.0D, true),
                    new Expecter("10170518/DSC05184.JPG", true, "2017:05:18 09:34:44", 35.4367520000D, 139.4082730000D, true),
                    new Expecter("10170518/DSC05196.JPG", true, "2017:05:18 09:37:32", 35.4376820000D, 139.4085150000D, true),
                    new Expecter("10170518/DSC05204.JPG", true, "2017:05:18 09:46:48", 35.4368560000D, 139.4082190000D, true),
                    new Expecter("10170518/DSC05205.JPG", true, "2017:05:18 09:48:04", 90.0D, 180.0D, true),
                }
            ),
            // 3.
            new Fixture(
                "[B1].WiMiUSカメラの場合.FILE_UPDATE時間を基準にして時間外のファイルはコピー対象外の時",
                "src/test/data/WiMiUS20170518.tar.gz", 
                "src/test/data/20170518.gpx",
                "src/test/data/cameradata/20170518.gpx",
                "src/test/data/AdjustTime.20170518.B1.ini",
                new Expecter[] {
                    new Expecter("cameradata/20170518_.gpx", true, null, 90.0D, 180.0D, false),
                    new Expecter("cameradata/20170518_092031A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("cameradata/20170518_094226A_snap.jpg", true, "2017:05:18 09:42:26", 35.4366860000D, 139.4082650000D, true),
                    new Expecter("cameradata/20170518_094737A.jpg", true, "2017:05:18 09:47:36", 35.4368200000D, 139.4082810000D, true),
                    new Expecter("cameradata/20170518_094827A.jpg", false, null, 90.0D, 180.0D, false),
                }
            ),
            // 4.
            new Fixture(
                "[B2].WiMiUSカメラの場合.FILE_UPDATE時間を基準にして時間外のファイルもコピーする時",
                "src/test/data/WiMiUS20170518.tar.gz", 
                "src/test/data/20170518.gpx",
                "src/test/data/cameradata/20170518.gpx",
                "src/test/data/AdjustTime.20170518.B2.ini",
                new Expecter[] {
                    new Expecter("cameradata/20170518_.gpx", true, null, 90.0D, 180.0D, false),
                    new Expecter("cameradata/20170518_092031A.jpg", true, "2017:05:18 09:20:30", 90.0D, 180.0D, true),
                    new Expecter("cameradata/20170518_094226A_snap.jpg", true, "2017:05:18 09:42:26", 35.4366860000D, 139.4082650000D, true),
                    new Expecter("cameradata/20170518_094737A.jpg", true, "2017:05:18 09:47:36", 35.4368200000D, 139.4082810000D, true),
                    new Expecter("cameradata/20170518_094827A.jpg", true, "2017:05:18 09:48:26", 90.0D, 180.0D, true),
                }
            ),
            // 5.
            new Fixture(
                "[M1a].GPXが複数のTRKSEGに分割している場合.FILE_UPDATE時間を基準.GarminColorado",
                "src/test/data/separate.tar.gz",
                "src/test/data/muiltiTRK.GarminColorado.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M1a.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102314A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:04", 35.8808881603D, 137.9979396332D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102418A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_103545A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:18", 35.8339846227D, 138.0625408050D, true),
                }
            ),

            new Fixture(
                "[M1b].GPXが複数のTRKSEGに分割している場合.FILE_UPDATE時間を基準.GarminColorado",
                "src/test/data/separate.tar.gz", 
                "src/test/data/muiltiTRK.GarminColorado.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M1b.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", true, "2017:05:29 10:23:06", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102314A.jpg", true, "2017:05:29 10:23:14", 90.0D, 180.0D, true),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:04", 35.8808881603D, 137.9979396332D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", true, "2017:05:29 10:24:10", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102418A.jpg", true, "2017:05:29 10:24:18", 90.0D, 180.0D, true),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", true, "2017:05:29 10:33:14", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_103545A.jpg", true, "2017:05:29 10:35:44", 90.0D, 180.0D, true),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:18", 35.8339846227D, 138.0625408050D, true),
                }
            ),

            new Fixture(
                "[M1c].GPXが複数のTRKSEGに分割している場合.EXIF時間を基準.GarminColorado",
                "src/test/data/separate.tar.gz", 
                "src/test/data/muiltiTRK.GarminColorado.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M1c.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102314A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:05", 35.8808641881D, 137.9981065169D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102418A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_103545A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:19", 35.8339846227D, 138.0625408050D, true),
                }
            ),

            new Fixture(
                "[M1d].GPXが複数のTRKSEGに分割している場合.EXIF時間を基準.GarminColorado",
                "src/test/data/separate.tar.gz", 
                "src/test/data/muiltiTRK.GarminColorado.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M1d.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", true, "2017:05:29 10:23:05", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102314A.jpg", true, "2017:05:29 10:23:14", 90.0D, 180.0D, true),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:05", 35.8808641881D, 137.9981065169D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", true, "2017:05:29 10:24:09", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102418A.jpg", true, "2017:05:29 10:24:18", 90.0D, 180.0D, true),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", true, "2017:05:29 10:33:15", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_103545A.jpg", true, "2017:05:29 10:35:45", 90.0D, 180.0D, true),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:19", 35.8339846227D, 138.0625408050D, true),
                }
            ),


            new Fixture(
                "[M2a].GPXが複数のTRKSEGに分割している場合.FILE_UPDATE時間を基準.eTrex_20J",
                "src/test/data/separate.tar.gz", 
                "src/test/data/multiTRKSEG.eTrex_20J.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M2a.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102314A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:04", 35.8808881603D, 137.9979396332D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102418A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_103545A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:18", 35.8339846227D, 138.0625408050D, true),
                }
            ),

            new Fixture(
                "[M2b].GPXが複数のTRKSEGに分割している場合.FILE_UPDATE時間を基準.eTrex_20J",
                "src/test/data/separate.tar.gz", 
                "src/test/data/multiTRKSEG.eTrex_20J.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M2b.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", true, "2017:05:29 10:23:06", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102314A.jpg", true, "2017:05:29 10:23:14", 90.0D, 180.0D, true),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:04", 35.8808881603D, 137.9979396332D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", true, "2017:05:29 10:24:10", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102418A.jpg", true, "2017:05:29 10:24:18", 90.0D, 180.0D, true),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", true, "2017:05:29 10:33:14", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_103545A.jpg", true, "2017:05:29 10:35:44", 90.0D, 180.0D, true),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:18", 35.8339846227D, 138.0625408050D, true),
                }
            ),

            new Fixture(
                "[M2c].GPXが複数のTRKSEGに分割している場合.EXIF時間を基準.eTrex_20J",
                "src/test/data/separate.tar.gz", 
                "src/test/data/multiTRKSEG.eTrex_20J.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M2c.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102314A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:05", 35.8808641881D, 137.9981065169D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102418A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_103545A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:19", 35.8339889813D, 138.0625394639D, true),
                }
            ),

            new Fixture(
                "[M2d].GPXが複数のTRKSEGに分割している場合.EXIF時間を基準.eTrex_20J",
                "src/test/data/separate.tar.gz", 
                "src/test/data/multiTRKSEG.eTrex_20J.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M2d.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", true, "2017:05:29 10:23:05", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102314A.jpg", true, "2017:05:29 10:23:14", 90.0D, 180.0D, true),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:05", 35.8808641881D, 137.9981065169D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", true, "2017:05:29 10:24:09", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102418A.jpg", true, "2017:05:29 10:24:18", 90.0D, 180.0D, true),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", true, "2017:05:29 10:33:15", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_103545A.jpg", true, "2017:05:29 10:35:45", 90.0D, 180.0D, true),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:19", 35.8339889813D, 138.0625394639D, true),
                }
            ),

            new Fixture(
                "[M3a].GPXが複数のTRKSEGに分割している場合.FILE_UPDATE時間を基準.eTrex_20Jreverse",
                "src/test/data/separate.tar.gz", 
                "src/test/data/multiTRKSEGreverse.eTrex_20J.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M2a.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102314A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:04", 35.8808881603D, 137.9979396332D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102418A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_103545A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:18", 35.8339846227D, 138.0625408050D, true),
                }
            ),

            new Fixture(
                "[M3b].GPXが複数のTRKSEGに分割している場合.FILE_UPDATE時間を基準.eTrex_20Jreverse",
                "src/test/data/separate.tar.gz", 
                "src/test/data/multiTRKSEGreverse.eTrex_20J.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M2b.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", true, "2017:05:29 10:23:06", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102314A.jpg", true, "2017:05:29 10:23:14", 90.0D, 180.0D, true),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:04", 35.8808881603D, 137.9979396332D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", true, "2017:05:29 10:24:10", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102418A.jpg", true, "2017:05:29 10:24:18", 90.0D, 180.0D, true),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", true, "2017:05:29 10:33:14", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_103545A.jpg", true, "2017:05:29 10:35:44", 90.0D, 180.0D, true),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:18", 35.8339846227D, 138.0625408050D, true),
                }
            ),

            new Fixture(
                "[M3c].GPXが複数のTRKSEGに分割している場合.EXIF時間を基準.eTrex_20Jreverse",
                "src/test/data/separate.tar.gz", 
                "src/test/data/multiTRKSEGreverse.eTrex_20J.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M2c.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102314A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:05", 35.8808641881D, 137.9981065169D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_102418A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", false, null, 90.0D, 180.0D, false),
                    new Expecter("separate/20170529_103545A.jpg", false, null, 90.0D, 180.0D, false),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:19", 35.8339889813D, 138.0625394639D, true),
                }
            ),

            new Fixture(
                "[M3d].GPXが複数のTRKSEGに分割している場合.EXIF時間を基準.eTrex_20Jreverse",
                "src/test/data/separate.tar.gz", 
                "src/test/data/multiTRKSEGreverse.eTrex_20J.gpx.xml",
                "src/test/data/cameradata/separate.gpx",
                "src/test/data/AdjustTime.M2d.separate.ini",
                new Expecter[] {
                    // GPX file
                    new Expecter("separate/separate_.gpx", true, null, 90.0D, 180.0D, false),

                    // out of time ( - 2017-05-29T01:23:18)
                    new Expecter("separate/20170529_102305A.jpg", true, "2017:05:29 10:23:05", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102314A.jpg", true, "2017:05:29 10:23:14", 90.0D, 180.0D, true),

                    // in TRKSEG(1) (2017-05-29T01:23:18 - 2017-05-29T01:24:05)
                    new Expecter("separate/20170529_102318A.jpg", true, "2017:05:29 10:23:18", 35.8812697884D, 137.9952202085D, true),
                    new Expecter("separate/20170529_102322A.jpg", true, "2017:05:29 10:23:22", 35.8810500987D, 137.9951669835D, true),
                    new Expecter("separate/20170529_102405A.jpg", true, "2017:05:29 10:24:05", 35.8808641881D, 137.9981065169D, true),

                    // out of time (2017-05-29T01:24:05 - 2017-05-29T01:24:37)
                    new Expecter("separate/20170529_102409A.jpg", true, "2017:05:29 10:24:09", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_102418A.jpg", true, "2017:05:29 10:24:18", 90.0D, 180.0D, true),

                    // in TRKSEG(2) (2017-05-29T01:24:37 - 2017-05-29T01:33:03)
                    new Expecter("separate/20170529_102448A.jpg", true, "2017:05:29 10:24:48", 35.8788877353D, 138.0039562471D, true),
                    new Expecter("separate/20170529_103246A.jpg", true, "2017:05:29 10:32:46", 35.8405660931D, 138.0353022180D, true),

                    // out of time (2017-05-29T01:33:03 - 2017-05-29T01:35:53)
                    new Expecter("separate/20170529_103315A.jpg", true, "2017:05:29 10:33:15", 90.0D, 180.0D, true),
                    new Expecter("separate/20170529_103545A.jpg", true, "2017:05:29 10:35:45", 90.0D, 180.0D, true),

                    // in TRKSEG(3) (2017-05-29T01:35:53 - 2017-05-29T01:47:35)
                    new Expecter("separate/20170529_103615A.jpg", true, "2017:05:29 10:36:14", 35.8359798510D, 138.0600296706D, true),
                    new Expecter("separate/20170529_104119A.jpg", true, "2017:05:29 10:41:19", 35.8339889813D, 138.0625394639D, true),
                }
            ),
        };

        @Theory
        public void パラメータテスト(Fixture dataset) throws Exception {
            ImportPictureTest.setup(dataset);
            ImportPictureTest.testdo(dataset.iniFilePath);
            //SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.UK);

            Expecter[] es = dataset.expecters;
            AppParameters params = new AppParameters(dataset.iniFilePath);
            File outDir = new File(params.getProperty(AppParameters.IMG_OUTPUT_FOLDER));
            for (Expecter e : es) {
                File file = new File(outDir, e.value);
                System.out.println("[JUnit.debug] assert file='"+ file.getAbsolutePath() +"'");
                assertThat(file.exists(), is(e.expect));
                if (e.timeStr != null) {
                    // JPEG メタデータが存在すること
                    ImageMetadata meta = Imaging.getMetadata(file);
                    // メタデータは インスタンスJpegImageMetadata であること
                    assertThat((meta instanceof JpegImageMetadata), is(true));
                    JpegImageMetadata jpegMetadata = (JpegImageMetadata)meta;
                    assertNotNull(jpegMetadata);
                    // EXIFデータが存在すること
                    TiffImageMetadata exif = jpegMetadata.getExif();
                    assertNotNull(exif);
                    // EXIF-TIME が正しく設定されていること
                    String exifTime = ImportPicture.toEXIFString(ImportPicture.toEXIFDate(exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)[0]));
                    System.out.println("[debug] exifTime = '"+ exifTime +"' <--> '" + e.timeStr + "'");
                    assertThat(exifTime, is(e.timeStr));
                    // LAT,LON
                    GPSInfo gpsInfo = exif.getGPS();
                    if (e.latD != 90.0D) {
                        assertThat(comparePosition(gpsInfo.getLatitudeAsDegreesNorth()), is(comparePosition(e.latD)));
                    }
                    if (e.lonD != 180.0D) {
                        assertThat(comparePosition(gpsInfo.getLongitudeAsDegreesEast()), is(comparePosition(e.lonD)));
                    }
                }
            }
        }

        static String comparePosition(double b) {
            return String.format("%.4f", b);
        }
    }

    static void setup(Fixture dataset) throws IOException {
        System.out.println(dataset.toString());

        // カメラディレクトリを削除する
        File dir = new File("src/test/data/cameradata");
        if (dir.exists()) {
            ImportPictureTest.delete(dir);
        }
        File outDir = new File("src/test/data/output");
        if (outDir.exists()) {
            ImportPictureTest.delete(outDir);
        }
        outDir.mkdir();

        // カメラディレクトリを作成する
        ImportPictureTest.uncompress(new File(dataset.tarFilePath), new File("src/test/data/cameradata"));

        // GPXファイルをセット
        try (FileInputStream inStream = new FileInputStream(new File(dataset.gpxSourcePath));
            FileOutputStream outStream = new FileOutputStream(new File(dataset.gpxDestinationPath));
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel())
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
    }

    /**
     * 実行する
     * @throws Exception
     */
    static void testdo(String iniFilePath) {
        try {
            String[] argv = {iniFilePath};
            ImportPicture.main(argv);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Exceptionが発生した。");
        }
    }

    /**
     * *.tar.gz解凍
     * ファイル更新日時をオリジナルと同じにします。
     * @param tazFile 解凍する*.tar.gzファイル
     * @param dest 解凍先フォルダ
     * @throws IOException 
     */
    public static void uncompress(File tazFile, File dest) throws IOException {
        dest.mkdir();
        
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(tazFile))))) {
            TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
            while (tarEntry != null) {
                File destPath = new File(dest, tarEntry.getName());
                //System.out.println("uncompress: " + destPath.getCanonicalPath());
                if (tarEntry.isDirectory()) {
                    destPath.mkdirs();
                }
                else {
                    File dir = new File(destPath.getParent());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    destPath.createNewFile();
                    byte[] btoRead = new byte[1024];
                    try (BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath))) {
                        int len;
                        while ((len = tarIn.read(btoRead)) != -1) {
                            bout.write(btoRead, 0, len);
                        }
                    }
                    destPath.setLastModified(tarEntry.getLastModifiedDate().getTime());
                }
                tarEntry = tarIn.getNextTarEntry();
            }
        }
    }

    public static void delete(File file) throws IOException {
        if (!file.exists()) {
            System.out.println("ERROR: ファイルまたはディレクトリが見つかりませんでした。");
            throw new IOException("File not found.");
        }
        
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    delete(file1); // 再帰呼び出し
                }
            }
        }
        if (!file.delete()) {
            System.out.println("ERROR: ファイルは削除できませんでした。 '" + file.getAbsolutePath() +"'");
        }
    }
}