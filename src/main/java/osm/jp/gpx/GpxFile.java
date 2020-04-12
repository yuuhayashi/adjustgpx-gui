package osm.jp.gpx;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@SuppressWarnings("serial")
public class GpxFile extends File {
	GpxParser gpx = new GpxParser();
	
    public GpxFile(File file) throws ParserConfigurationException, SAXException, IOException, ParseException {
        super(file.getParentFile(), file.getName());
    }
    
    public ElementMapTRKSEG parse() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        
        SAXParser parser;
		parser = factory.newSAXParser();
        try {
			parser.parse(this, gpx);
		} catch (SAXParseException e) {}

        // XMLが閉じられなかったデータを救出する
		if (gpx.tag != null) {
			if (gpx.tag.getTime() != null) {
				gpx.trkpt.put(gpx.tag.clone());
			}
			gpx.tag = null;
		}
		if (gpx.trkpt.size() > 0) {
			gpx.trkseg.put((ElementMapTRKPT)gpx.trkpt.clone());
			gpx.trkpt.clear();
		}
		
		// 表示
		gpx.trkseg.printinfo();
		return gpx.trkseg;
    }
}
