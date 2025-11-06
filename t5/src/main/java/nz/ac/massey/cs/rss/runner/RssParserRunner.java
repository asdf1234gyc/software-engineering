package nz.ac.massey.cs.rss.runner;

import java.io.File;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import nz.ac.massey.cs.sdc.parsers.Rss;  // Root class, imported from generated JAXB classes
import nz.ac.massey.cs.sdc.parsers.RssChannel;  // Channel class (based on generated naming)
import nz.ac.massey.cs.sdc.parsers.RssItem;  // Item class (based on generated naming)
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class RssParserRunner {
    public static void main(String[] args) {
        try {
            // Create JAXB context (using the generated root class Rss)
            JAXBContext context = JAXBContext.newInstance(Rss.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // Read XML file (media-technology.xml should be in the project root directory)
            File xmlFile = new File("media-technology.xml");
            Rss rss = (Rss) unmarshaller.unmarshal(xmlFile);

            // Get channel (Rss.getChannel() returns RssChannel)
            RssChannel channel = rss.getChannel();
            // Get item list (RssChannel.getItem() returns List<RssItem>)
            List<RssItem> itemsList = channel.getItem();  // Fix: Use specific type List<RssItem> to avoid incompatibility errors

            // Iterate over each item (directly use RssItem, no cast needed)
            for (RssItem item : itemsList) {
                // Get mixed element list (RssItem.getTitleOrDescriptionOrLink() returns List<Object>, containing JAXBElement)
                List<Object> elements = item.getTitleOrDescriptionOrLink();

                String title = "";
                String description = "";
                String link = "";

                // Iterate over elements, extract title, description, link (use getLocalPart() to get tag name)
                for (Object elem : elements) {
                    if (elem instanceof JAXBElement) {
                        JAXBElement<?> jaxbElement = (JAXBElement<?>) elem;
                        QName name = jaxbElement.getName();
                        String localPart = name.getLocalPart();  // Get XML tag name, e.g., "title"

                        Object value = jaxbElement.getValue();
                        String strValue = (value != null) ? value.toString().trim() : "";

                        if ("title".equals(localPart)) {
                            title = strValue;
                        } else if ("description".equals(localPart)) {
                            // Handle possible multi-line description from CDATA
                            description = strValue.replaceAll("[\n\r]+", " ").trim();
                        } else if ("link".equals(localPart)) {
                            link = strValue;
                        }
                    }
                }

                // Output format matches tutorial example
                System.out.println("title: '" + title + "'");
                System.out.println("description: ");
                System.out.println("    " + description);
                System.out.println(" ");
                System.out.println("link: ");
                System.out.println(link);
                System.out.println(" ");
            }
        } catch (Exception e) {
            System.err.println("Error parsing RSS: " + e.getMessage());
            e.printStackTrace();  // For debugging, can be replaced with logging
        }
    }
}