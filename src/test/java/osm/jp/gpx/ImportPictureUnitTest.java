package osm.jp.gpx;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.runner.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.*;

@RunWith(Enclosed.class)
public class ImportPictureUnitTest {

    public static class 出力ディレクトリが存在しないとき {
    	
    	@Before
        public void setUp() throws Exception {
    		Fixture dataset = Fixture.stddatas[0];
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
            //outDir.mkdir();

            // カメラディレクトリを作成する
            UnZip.uncompress(new File(dataset.tarFilePath), new File("target/test-classes/cameradata"));
        }


        @Test
        public void 実行() throws Exception {
    		Fixture dataset = Fixture.stddatas[0];
            try {
                ImportPictureUnitTest.testdo(dataset.iniFilePath);
            }
            catch (Exception e) {
                e.printStackTrace();
                fail("Exceptionが発生した。");
            }

            AppParameters params = new AppParameters(dataset.iniFilePath);
            File outDir = new File(params.getProperty(AppParameters.IMG_OUTPUT_FOLDER));
            assertThat(outDir.exists(), is(true));
            
            Expecter.check(dataset);
        }

        @Test
        public void MAGVARをON() throws Exception {
    		Fixture dataset = Fixture.stddatas[1];
            try {
                ImportPictureUnitTest.testdo(dataset.iniFilePath);
            }
            catch (Exception e) {
                e.printStackTrace();
                fail("Exceptionが発生した。");
            }

            AppParameters params = new AppParameters(dataset.iniFilePath);
            File outDir = new File(params.getProperty(AppParameters.IMG_OUTPUT_FOLDER));
            assertThat(outDir.exists(), is(true));
            
            Expecter.check(dataset);
        }

        static String comparePosition(double b) {
            return String.format("%.4f", b);
        }
    }

    public static class 出力ディレクトリがFILEのとき {
    	
    	@Before
        public void setUp() throws Exception {
    		Fixture dataset = Fixture.stddatas[0];
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
            
            // ファイルを生成
            outDir.createNewFile();

            // カメラディレクトリを作成する
            UnZip.uncompress(new File(dataset.tarFilePath), new File("target/test-classes/cameradata"));
        }


        @Test
        public void 実行() throws Exception {
    		Fixture dataset = Fixture.stddatas[0];
    		try {
                ImportPictureUnitTest.testdo(dataset.iniFilePath);
                fail("outDirがFILEなのに、例外が発生しなかった");	// 例外が発生しなかった
    		}
    		catch (Exception e) {
    			// 例外が発生する
    			assertThat(true, is(true));
    		}

            AppParameters params = new AppParameters(dataset.iniFilePath);
            File outDir = new File(params.getProperty(AppParameters.IMG_OUTPUT_FOLDER));
            assertThat(outDir.exists(), is(true));
        }

        static String comparePosition(double b) {
            return String.format("%.4f", b);
        }
    }

    
    /**
     * 実行する
     * @throws Exception
     */
    static void testdo(String iniFilePath) throws Exception {
        String[] argv = {iniFilePath};
        ImportPicture.main(argv);
    }
}