package osm.jp.gpx;

import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @code{
 * ＜trkpt lat="35.32123832" lon="139.56965631">
 *		<ele>47.20000076293945</ele>
 *		<time>2012-06-15T03:00:29Z</time>
 *		<magvar></magvar>
 *		<speed></speed>
 *	＜/trkpt>
 * }
 *
 */
public class TagTrkpt {
    public Element trkpt = null;
    public Double lat = null;
    public Double lon = null;
    public String eleStr = null;
    public Date time = null;
    public String magvarStr = null;
    public String speedStr = null;

    public TagTrkpt(Element trkpt) {
        this.trkpt = (Element) trkpt.cloneNode(true);
		
        NamedNodeMap nodeMap = trkpt.getAttributes();
        for (int j=0; j < nodeMap.getLength(); j++ ) {
            switch (nodeMap.item(j).getNodeName()) {
                case "lat":
                    String latStr = nodeMap.item(j).getNodeValue();
                    this.lat = new Double(latStr);
                    break;
                case "lon":
                    String lonStr = nodeMap.item(j).getNodeValue();
                    this.lon = new Double(lonStr);
                    break;
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
                            this.eleStr = node2.getNodeValue();
                        }
                    }
                }
                break;
            case "time":
                for (int i2=0; i2 < nodes2.getLength(); i2++) {
                    Node node2 = nodes2.item(i2);
                    if (node2 != null) {
                        if (node2.getNodeType() == Node.TEXT_NODE) {
                            try {
                                this.time = ImportPicture.toUTCDate(node2.getNodeValue());
                            } catch (ParseException e) {
                                this.time = null;
                            }
                        }
                    }
                }
                break;
            case "magvar":
                for (int i2=0; i2 < nodes2.getLength(); i2++) {
                    Node node2 = nodes2.item(i2);
                    if (node2 != null) {
                        if (node2.getNodeType() == Node.TEXT_NODE) {
                            this.magvarStr = node2.getNodeValue();
                        }
                    }
                }
                break;
            case "speed":
                for (int i2=0; i2 < nodes2.getLength(); i2++) {
                    Node node2 = nodes2.item(i2);
                    if (node2 != null) {
                        if (node2.getNodeType() == Node.TEXT_NODE) {
                            this.speedStr = node2.getNodeValue();
                        }
                    }
                }
                break;
            }
        }
    }
	
    public void removeElement(String eleName) {
        Node child;
        for (child = trkpt.getFirstChild(); child != null; child = child.getNextSibling()) {
            NodeList nodeList = child.getChildNodes();
            for(int i = 0; i < nodeList.getLength(); i++) {
                Node grandChild = child.getChildNodes().item(i);
                if (grandChild.getNodeName().equals(eleName)) {
                    child.removeChild(grandChild);
                }
            }
        }
    }
    
    public void appendElement(String eleName, String valueStr) {
        Document doc = trkpt.getOwnerDocument();
        Element newElement = doc.createElement(eleName);
        newElement.setTextContent(valueStr);
        trkpt.appendChild(newElement);
    }
}
