package osm.jp.gpx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class GpxFile extends File {
    Node gpx = null;
    ElementMapTRKSEG mapTRKSEG = null;
    Document document;

    @SuppressWarnings("LeakingThisInConstructor")
    public GpxFile(File file) throws ParserConfigurationException, DOMException, SAXException, IOException, ParseException {
        super(file.getParentFile(), file.getName());

        DocumentBuilderFactory factory = null;
        DocumentBuilder        builder = null;
        
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        factory.setValidating(true);

        // GPXファイルをパースする
        mapTRKSEG = new ElementMapTRKSEG();
        document = mapTRKSEG.parse(this);
        
        // パースされた mapTRKSEG の中身を出力する
        mapTRKSEG.printinfo();
        
        // GPX file --> Node root
        gpx = builder.parse(this).getFirstChild();
    }
	
    /**
     * GPX 変換出力
     * @param outDir
     * @throws FileNotFoundException 
     * @throws TransformerException 
     */
    public void output(File outDir) throws FileNotFoundException, TransformerException {
        String fileName = this.getName();
        String iStr = fileName.substring(0, fileName.length() - 4);
        File outputFile = new File(outDir, iStr +"_.gpx");
        System.out.println(this.getAbsolutePath() + " => "+ outputFile.getAbsolutePath());
        
        outputFile.getParentFile().mkdirs();
        DOMSource source = new DOMSource(this.gpx);
        FileOutputStream os = new FileOutputStream(outputFile);
        StreamResult result = new StreamResult(os);
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.transform(source, result);         

        os = new FileOutputStream(outputFile);
        result = new StreamResult(os);
        transformer.transform(source, result);
    }

    /**
     *	＜wpt lat="35.25714922" lon="139.15490497">
     *		＜ele>62.099998474121094＜/ele>
     *		＜time>2012-06-11T00:44:38Z＜/time>
     *		＜name><![CDATA[写真]]>＜/name>
     *		＜link href="2012-06-11_09-44-38.jpg">
     *			＜text>2012-06-11_09-44-38.jpg＜/text>
     *		＜/link>
     *	＜/wpt>
     *
     *	＜trkpt lat="35.32123832" lon="139.56965631">
     *		＜ele>47.20000076293945＜/ele>
     *		＜time>2012-06-15T03:00:29Z＜/time>
     *	＜/trkpt>
     *
     * @param imgDir
     * @return
     * @param iFile
     * @param timestamp
     * @param trkpt
     */
    public Element createWptTag(File iFile, File imgDir, long timestamp, Element trkpt) {
        Element wpt = document.createElement("wpt");

        NamedNodeMap nodeMap = trkpt.getAttributes();
        if (null != nodeMap) {
            for (int j=0; j < nodeMap.getLength(); j++ ) {
                switch (nodeMap.item(j).getNodeName()) {
                case "lat":
                    String lat = nodeMap.item(j).getNodeValue();
                    wpt.setAttribute("lat", lat);
                    break;
                case "lon":
                    String lon = nodeMap.item(j).getNodeValue();
                    wpt.setAttribute("lon", lon);
                    break;
                }
            }
        }

        NodeList nodes1 = trkpt.getChildNodes();
        for (int i1=0; i1 < nodes1.getLength(); i1++) {
            Node node1 = nodes1.item(i1);
            NodeList nodes2 = node1.getChildNodes();
            switch (node1.getNodeName()) {
            case "ele":
                for (int i2=0; i2 < nodes2.getLength(); i2++) {
                    Node node2 = nodes2.item(i2);
                    if (node2 != null) {
                        if (node2.getNodeType() == Node.TEXT_NODE) {
                            String eleStr = node2.getNodeValue();
                            Element eleE = document.createElement("ele");
                            eleE.setTextContent(eleStr);
                            wpt.appendChild(eleE);
                        }
                    }
                }
                break;
            case "time":
                for (int i2=0; i2 < nodes2.getLength(); i2++) {
                    Node node2 = nodes2.item(i2);
                    if (node2 != null) {
                        if (node2.getNodeType() == Node.TEXT_NODE) {
                            String timeStr = node2.getNodeValue();
                            Element timeE = document.createElement("time");
                            timeE.setTextContent(timeStr);
                            wpt.appendChild(timeE);
                        }
                    }
                }
                break;
            case "magvar":
                for (int i2=0; i2 < nodes2.getLength(); i2++) {
                    Node node2 = nodes2.item(i2);
                    if (node2 != null) {
                        if (node2.getNodeType() == Node.TEXT_NODE) {
                            String magvarStr = node2.getNodeValue();
                            Element magvarE = document.createElement("magvar");
                            magvarE.setTextContent(magvarStr);
                            wpt.appendChild(magvarE);
                        }
                    }
                }
                break;
            case "speed":
                for (int i2=0; i2 < nodes2.getLength(); i2++) {
                    Node node2 = nodes2.item(i2);
                    if (node2 != null) {
                        if (node2.getNodeType() == Node.TEXT_NODE) {
                            String speedStr = node2.getNodeValue();
                            Element speedE = document.createElement("speed");
                            speedE.setTextContent(speedStr);
                            wpt.appendChild(speedE);
                        }
                    }
                }
                break;
            }
        }

        Element name = document.createElement("name");
        name.appendChild(document.createCDATASection("写真"));
        wpt.appendChild(name);

        Element link = document.createElement("link");
        link.setAttribute("href", ImportPicture.getShortPathName(imgDir, iFile));
        Element text = document.createElement("text");
        text.setTextContent(iFile.getName());
        link.appendChild(text);
        wpt.appendChild(link);

        return wpt;
    }
}
