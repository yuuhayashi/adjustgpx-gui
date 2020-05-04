package osm.jp.gpx;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
        Expecter.check(dataset);
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