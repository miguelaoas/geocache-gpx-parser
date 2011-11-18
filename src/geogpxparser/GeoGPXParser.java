/*
 * The MIT License - Copyright (c) 2011 ZeroOne
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package geogpxparser;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class can be used to parse geocaches from a Groundspeak .gpx file
 * into plain old Java objects (POJO). The caches are then saved as a
 * tab delimited .txt file.
 * 
 * @author ZeroOne
 */
public class GeoGPXParser {
    private String file = null;
    
    private final DateTimeFormatter XML_DATE_TIME_FORMAT = ISODateTimeFormat.dateTimeNoMillis();
    private final DateTimeFormatter OUTPUT_DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Usage:");
            System.out.println("1) java -jar GeoGPXParser.jar caches.gpx");
            System.out.println("2) java -jar GeoGPXParser.jar some/directory/with/gpx/files");
            System.exit(1);
        }
        GeoGPXParser parser = new GeoGPXParser(args[0]);
        List<Geocache> caches = parser.parse();
        parser.saveTextFile("caches.txt", caches);
        info("Done!");
    }

    private static void info(String text) {
        System.out.println(text);
    }
    
    public GeoGPXParser(String path) {
        this.file = path;
    }
    
    public List<Geocache> parse() {
        return parseXmlFilesToObjects(this.file);
    }
    
    public void saveTextFile(String fileName, List<Geocache> caches) {
        info("Writing the caches into a file...");
        final String separator = "\t";

        StringBuilder sb = new StringBuilder();
        sb.append("gccode").append(separator);
        sb.append("type").append(separator);
        sb.append("name").append(separator);
        sb.append("longitude").append(separator);
        sb.append("latitude").append(separator);
        sb.append("size").append(separator);
        sb.append("difficulty").append(separator);
        sb.append("terrain").append(separator);
        sb.append("published").append(separator);
        sb.append("owner").append(separator);
        sb.append("\n");
        
        for (Geocache cache : caches) {
            sb.append(cache.getGcCode()).append(separator);
            sb.append(cache.getType()).append(separator);
            sb.append(cache.getName().replace(separator, "")).append(separator);
            sb.append(cache.getLongitude()).append(separator);
            sb.append(cache.getLatitude()).append(separator);
            sb.append(cache.getSize()).append(separator);
            sb.append(cache.getDifficulty()).append(separator);
            sb.append(cache.getTerrain()).append(separator);
            sb.append(OUTPUT_DATE_TIME_FORMAT.print(cache.getPublished())).append(separator);
            sb.append(cache.getOwner().replace(separator, "")).append(separator);
            sb.append("\n");
        }

        try {
            Files.write(Paths.get(fileName), sb.toString().getBytes());
        } catch (IOException ex) {
            System.out.println("Saving the result file failed!");
            ex.printStackTrace();
        }
    }

    private List<Geocache> parseXMLtoObjects(Document dom) {
        List<Geocache> geocaches = new LinkedList<>();
        Element root = dom.getDocumentElement();
        
        NodeList caches = root.getElementsByTagName("wpt");
        info(caches.getLength()+" caches found...");
        if (caches == null || caches.getLength() < 1) {
            return new LinkedList<>();
        }

        for (int i = 0; i < caches.getLength(); i++) {
            Element wptElement = (Element) caches.item(i);
            Geocache geocache = getGeocache(wptElement);
            geocaches.add(geocache);
        }
        return geocaches;
    }

    private static Element getSubElement(Element parent, String subElementName) {
        return (Element) parent.getElementsByTagName(subElementName).item(0);
    }
    
    private static String getSubElementContent(Element parent, String subElementName) {
        return getSubElement(parent, subElementName).getTextContent();
    }

    private Geocache getGeocache(Element wptElement) {
        Geocache cache = new Geocache();

        cache.setLatitude(Double.valueOf(wptElement.getAttribute("lat")));
        cache.setLongitude(Double.valueOf(wptElement.getAttribute("lon")));

        DateTime time = XML_DATE_TIME_FORMAT.parseDateTime(getSubElementContent(wptElement, "time"));
        cache.setPublished(time);
        cache.setGcCode(getSubElementContent(wptElement, "name"));

        Element groundspeak = getSubElement(wptElement, "groundspeak:cache");
        cache.setArchived(Boolean.valueOf(groundspeak.getAttribute("archived")));
        cache.setAvailable(Boolean.valueOf(groundspeak.getAttribute("available")));
        
        cache.setName(getSubElementContent(groundspeak,"groundspeak:name"));
        cache.setCountry(getSubElementContent(groundspeak,"groundspeak:country"));
        cache.setState(getSubElementContent(groundspeak,"groundspeak:state"));
        cache.setName(getSubElementContent(groundspeak,"groundspeak:name"));
        cache.setCountry(getSubElementContent(groundspeak,"groundspeak:name"));
        cache.setState(getSubElementContent(groundspeak,"groundspeak:state"));
        cache.setOwner(getSubElementContent(groundspeak,"groundspeak:owner"));
        switch (getSubElementContent(groundspeak,"groundspeak:type")) {
            case "Traditional Cache":
                cache.setType(Geocache.CacheType.Traditional);
                break;
            case "Unknown Cache":
                cache.setType(Geocache.CacheType.Mystery);
                break;
            case "Multi-cache":
                cache.setType(Geocache.CacheType.Multi);
                break;
            default:
                cache.setType(Geocache.CacheType.Other);
                break;
        }
        try {
            cache.setSize(Geocache.CacheSize.valueOf(getSubElementContent(groundspeak,"groundspeak:container")));
        } catch (IllegalArgumentException ex) {
            cache.setSize(Geocache.CacheSize.Not_chosen);
        }
        cache.setDifficulty(Float.parseFloat(getSubElementContent(groundspeak,"groundspeak:difficulty")));
        cache.setTerrain(Float.parseFloat(getSubElementContent(groundspeak,"groundspeak:terrain")));
        cache.setShortDescription(getSubElementContent(groundspeak,"groundspeak:short_description"));
        cache.setLongDescription(getSubElementContent(groundspeak,"groundspeak:long_description"));
        cache.setHint(getSubElementContent(groundspeak,"groundspeak:encoded_hints"));
        Map<String, Boolean> attributes = new HashMap<>();
        Element attributesElement = getSubElement(groundspeak,"groundspeak:attributes");
        if (attributesElement != null) {
            Node attributeNode = attributesElement.getFirstChild();
            while (attributeNode != null) {
                if (attributeNode != null && attributeNode instanceof Element ) {
                    Element attributeElement = (Element) attributeNode;
                    attributes.put(attributeElement.getTagName(), "1".equals(attributeElement.getAttribute("inc")));
                }
                attributeNode = attributeNode.getNextSibling();
            }
        }
        
        return cache;
    }

    private List<Geocache> parseXmlFilesToObjects(String path) {
        List<Geocache> caches = new LinkedList<>();
        File[] files;
        File gpx = new File(path);
        if (gpx.isDirectory()) {
            files = gpx.listFiles(new GpxFileFilter());
        } else {
            files = new File[1];
            files[0] = new File(path);
        }
        info("Found "+files.length+" files.");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        for (File xmlFile : files) {
            info("Parsing file "+xmlFile+"...");
            try {
                DocumentBuilder db = dbFactory.newDocumentBuilder();
                Document xml = db.parse(xmlFile);
                caches.addAll(this.parseXMLtoObjects(xml));
            } catch (ParserConfigurationException | SAXException xmlException) {
                System.err.println("Error in parsing XML!");
                xmlException.printStackTrace();
            } catch (IllegalArgumentException | IOException ioException) {
                System.err.println("Error in reading file '"+xmlFile+"'!");
                ioException.printStackTrace();
            }
        }
        return caches;
    }

    private static final class GpxFileFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".gpx");
        }
    }
}
