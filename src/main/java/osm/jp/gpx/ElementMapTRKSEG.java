package osm.jp.gpx;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class ElementMapTRKSEG extends TreeMap<Date, ElementMapTRKPT> {
    /**
     * TESTing
     * @param argv
    * @throws ParseException 
    * @throws ParserConfigurationException 
    * @throws IOException 
    * @throws SAXException 
    * @throws DOMException 
    */
   public static void main(String[] argv) throws DOMException, SAXException, IOException, ParserConfigurationException, ParseException {
        ElementMapTRKSEG mapTRKSEG = null;
        mapTRKSEG = new ElementMapTRKSEG();
        mapTRKSEG.parse(new File("testdata/cameradata/separate.gpx"));
        mapTRKSEG.printinfo();
    }
	
    public ElementMapTRKSEG() {
        super(new TimeComparator());
    }

    /**
     * GPXファイルをパースする
     * @param gpxFile
     * @code{
     * <gpx>
     *   <trk>
     *     <trkseg>
     *       <trkpt lat="35.32123832" lon="139.56965631">
     *         <ele>47.20000076293945</ele>
     *         <time>2012-06-15T03:00:29Z</time>
     *         <hdop>0.5</hdop>
     *       </trkpt>
     *     </trkseg>
     *   </trk>
     * </gpx>
     * }
     * 
     * @param gpxFile
     * @return Document
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws DOMException
     * @throws ParseException
     */
    public Document parse(File gpxFile) throws SAXException, IOException, ParserConfigurationException, DOMException, ParseException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder        builder = factory.newDocumentBuilder();
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        factory.setValidating(true);
        
        Node gpx    = builder.parse(gpxFile).getFirstChild();
        Document document = gpx.getOwnerDocument();
        NodeList nodes = gpx.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node2 = nodes.item(i);
            if (node2.getNodeName().equals("trk")) {
            	Element trk = (Element) node2;
                
                NodeList nodes1 = trk.getChildNodes();
                for (int i1=0; i1 < nodes1.getLength(); i1++) {
                    Node nodeTRKSEG = nodes1.item(i1);
                    if (nodeTRKSEG.getNodeName().equals("trkseg")) {
                        this.put(nodeTRKSEG);
                    }
                }
            }
        }
        return document;
    }

    /**
     * @code{
     * 拡張put value:Node<TRKSEG>をputするとNode<TRKSEG>内のNode<TRKSPT>を put(key,value)する。
     * }
     * @param nodeTRKSEG
     * @throws ParseException 
     * @throws DOMException 
     */
    public void put(Node nodeTRKSEG) throws DOMException, ParseException {
        if (nodeTRKSEG.getNodeName().equals("trkseg")) {
            NodeList nodes2 = nodeTRKSEG.getChildNodes();
            
            ElementMapTRKPT mapTRKPT = new ElementMapTRKPT();
            for (int i2 = 0; i2 < nodes2.getLength(); i2++) {
                Node nodeTRKPT = nodes2.item(i2);
                if (nodeTRKPT.getNodeName().equals("trkpt")) {
                    if (ImportPicture.param_GpxNoFirstNode && (i2 == 0)) {
                        continue;
                    }
                    mapTRKPT.put(new TagTrkpt((Element)nodeTRKPT));
                }
            }
            this.put(mapTRKPT);
        }
    }
	
    /**
     * 拡張put value:ElementMapTRKPTをputするとElementMapTRKPT内の最初のエントリのtimeを読み取ってkeyとしてthis.put(key,value)する。
     * @param value 
     * @throws DOMException 
     */
    public void put(ElementMapTRKPT value) {
        for (Date key : value.keySet()) {
            this.put(key, value);
            return;
        }
    }
	
    public void printinfo() {
        System.out.println("                                 +--------------------+--------------------|");
        System.out.println("  GPS logging time               | First Time         | Last Time          |");
        System.out.println("|--------------------------------+--------------------+--------------------|");
        for (java.util.Map.Entry<Date, ElementMapTRKPT> map : this.entrySet()) {
            ElementMapTRKPT mapTRKPT = map.getValue();
            mapTRKPT.printinfo();
        }
        System.out.println("|--------------------------------+--------------------+--------------------|");
        System.out.println();
    }
}
