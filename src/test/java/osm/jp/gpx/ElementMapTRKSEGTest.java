package osm.jp.gpx;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ElementMapTRKSEGTest {

    @RunWith(Theories.class)
    public static class 各種GPXファイルを食わせる {
        static class Fixture {
            String gpxSourcePath;		// GPXファイル（オリジナル）
            int segCount;				// GPXファイルに含まれるTRKSEGノードの数

            public Fixture(String gpxSourcePath, int segCount) {
                this.gpxSourcePath = gpxSourcePath;
                this.segCount = segCount;
            }

            @Override
            public String toString() {
                String msg = "テストパターン : \n";
                msg += "\tgpxSourcePath = "+ gpxSourcePath +"\n";
                msg += "\tsegCount = "+ segCount;
                return msg;
            }
        }

        @DataPoints
        public static Fixture[] datas = {
            new Fixture("target/test-classes/gpx/20170517.gpx", 1),
            new Fixture("target/test-classes/gpx/20170518.gpx", 1),
            new Fixture("target/test-classes/gpx/2019-09-07 16.17.12 Day.gpx", 1),
            new Fixture("target/test-classes/gpx/2019-12-29 06.50.19 Day.gpx", 1),
            new Fixture("target/test-classes/gpx/2020-02-29 13.35.58 Day.gpx", 1),
            //new Fixture("target/test-classes/gpx/muiltiTRK.GarminColorado.gpx.xml", 3),
            //new Fixture("target/test-classes/gpx/muiltiTRKSEG.GarminColorado.gpx.xml", 3),
            //new Fixture("target/test-classes/gpx/muiltiTRKSEG.noNameSpace.gpx.xml", 3),
            //new Fixture("target/test-classes/gpx/multiTRKSEG.eTrex_20J.gpx.xml", 3),
            //new Fixture("target/test-classes/gpx/multiTRKSEGreverse.eTrex_20J.gpx.xml", 3),
        };

        @Theory
        public void TRKSEGを読み込む(Fixture dataset) {
            try {
                System.out.println("GPX file: "+ dataset.gpxSourcePath);
                GpxFile gpx = new GpxFile(new AppParameters(AppParameters.FILE_PATH), new File(dataset.gpxSourcePath));
                gpx.parse();
                assertThat(gpx.gpx.trkseg.size(), is(dataset.segCount));
                for (Date key : gpx.gpx.trkseg.keySet()) {
                    assertThat(key, is(notNullValue()));
                }
            }
            catch (IOException | ParseException | ParserConfigurationException | SAXException e) {
                fail();
            }
        }
        
        @Test
        public void test整形されていないGPX() {
        	String gpxSourcePath = "target/test-classes/gpx/2020-02-29 13.35.58 Day.gpx";
            try {
                System.out.println("GPX file: "+ gpxSourcePath);
                GpxFile gpx = new GpxFile(new AppParameters(AppParameters.FILE_PATH), new File(gpxSourcePath));
                gpx.parse();
                ElementMapTRKSEG seg = gpx.gpx.trkseg;
                assertTrue(seg.size() == 1);
                for (Date key : seg.keySet()) {
                    assertThat(key, notNullValue());
                }
            }
            catch (IOException e) {
                fail();
            }
            catch (ParseException e) {
                fail();
            }
            catch (ParserConfigurationException e) {
                fail();
            }
            catch (SAXException e) {
            	// 整形されていないXML
            }
        }
    }
}
