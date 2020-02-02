package osm.jp.gpx;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

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
            new Fixture("src/test/data/20170517.gpx", 1),
            new Fixture("src/test/data/20170518.gpx", 1),
            new Fixture("src/test/data/muiltiTRK.GarminColorado.gpx.xml", 3),
            new Fixture("src/test/data/muiltiTRKSEG.GarminColorado.gpx.xml", 3),
            new Fixture("src/test/data/muiltiTRKSEG.noNameSpace.gpx.xml", 3),
            new Fixture("src/test/data/multiTRKSEG.eTrex_20J.gpx.xml", 3),
            new Fixture("src/test/data/multiTRKSEGreverse.eTrex_20J.gpx.xml", 3),
        };

        @Theory
        public void TRKSEGを読み込む(Fixture dataset) {
            try {
                ElementMapTRKSEG mapTRKSEG = new ElementMapTRKSEG();
                mapTRKSEG.parse(new File(dataset.gpxSourcePath));
                mapTRKSEG.printinfo();
                System.out.println("GPX file: "+ dataset.gpxSourcePath);
                assertThat(mapTRKSEG.size(), is(dataset.segCount));
                for (Date key : mapTRKSEG.keySet()) {
                    assertThat(key, is(notNullValue()));
                }
            }
            catch (IOException | ParseException | ParserConfigurationException | DOMException | SAXException e) {
                fail();
            }
        }
    }
}
