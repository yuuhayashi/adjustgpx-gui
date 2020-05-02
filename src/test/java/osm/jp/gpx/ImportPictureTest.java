package osm.jp.gpx;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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

@RunWith(Theories.class)
public class ImportPictureTest {

    @DataPoints
    public static Fixture[] datas = Fixture.datas;

    @Theory
    public void パラメータテスト(Fixture dataset) throws Exception {
        ImportPictureTest.setup(dataset);
        ImportPictureTest.testdo(dataset.iniFilePath);

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

    static void setup(Fixture dataset) throws IOException {
        System.out.println(dataset.toString());

        // カメラディレクトリを削除する
        File dir = new File("target/test-classes/cameradata");
        if (dir.exists()) {
            UnZip.delete(dir);
        }
        File outDir = new File("target/test-classes/output");
        if (outDir.exists()) {
        	UnZip.delete(outDir);
        }
        outDir.mkdir();

        // カメラディレクトリを作成する
        UnZip.uncompress(new File(dataset.tarFilePath), new File("target/test-classes/cameradata"));

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
}