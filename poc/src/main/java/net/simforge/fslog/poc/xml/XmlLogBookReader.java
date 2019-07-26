package net.simforge.fslog.poc.xml;

import net.simforge.fslog.poc.Discontinuity;
import net.simforge.fslog.poc.FlightReport;
import net.simforge.fslog.poc.LogBook;
import net.simforge.fslog.poc.Transfer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static net.simforge.fslog.poc.FSLogConsoleApp.HHmm;

public class XmlLogBookReader {
    public static LogBook readLogBook(InputStream is) {
        try {
            LogBook logBook = new LogBook();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element root = doc.getDocumentElement();
            root.normalize();

            if (!root.getTagName().equals("FSLog")) {
                throw new IllegalArgumentException();
            }

            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);

                if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element element = (Element) childNode;
                String tagName = element.getTagName();
                if (tagName.equals("FlightReport")) {
                    logBook.add(readFlightReport(element));
                } else if (tagName.equals("Transfer")) {
                    logBook.add(readTransfer(element));
                } else if (tagName.equals("Discontinuity")) {
                    logBook.add(readDiscontinuity(element));
                } else {
                    throw new IllegalArgumentException();
                }
            }

            return logBook;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static FlightReport readFlightReport(Element element) {
        FlightReport.Builder builder = new FlightReport.Builder();

        Element copy = (Element) element.cloneNode(true);

        builder = builder
                .setDate(readDateAndRemove(copy, "Date"))
                .setDeparture(readTextAndRemove(copy, "Departure"))
                .setDestination(readTextAndRemove(copy, "Destination"))
                .setTimeOut(readTimeAndRemove(copy, "TimeOut"))
                .setTimeOff(readTimeAndRemove(copy, "TimeOff"))
                .setTimeOn(readTimeAndRemove(copy, "TimeOn"))
                .setTimeIn(readTimeAndRemove(copy, "TimeIn"))
                .setRestOfXml(copy);

        return builder.build();
    }

    private static Transfer readTransfer(Element element) {
        Transfer.Builder builder = new Transfer.Builder();

        Element copy = (Element) element.cloneNode(true);

        builder = builder
                .setDate(readDateAndRemove(copy, "Date"))
                .setDeparture(readTextAndRemove(copy, "Departure"))
                .setDestination(readTextAndRemove(copy, "Destination"))
//        tranfer.setTimeOut(parseTime(element.getElementsByTagName("TimeOut")));
//        tranfer.setTimeOff(parseTime(element.getElementsByTagName("TimeOff")));
//        tranfer.setTimeOn(parseTime(element.getElementsByTagName("TimeOn")));
//        tranfer.setTimeIn(parseTime(element.getElementsByTagName("TimeIn")));
                .setRestOfXml(copy);

        return builder.build();
    }

    private static Discontinuity readDiscontinuity(Element element) {
        Discontinuity.Builder builder = new Discontinuity.Builder();

        Element copy = (Element) element.cloneNode(true);

        builder
                .setDate(readDateAndRemove(copy, "Date"))
                .setTime(readTimeAndRemove(copy, "Time"))
                .setRestOfXml(copy);

        return builder.build();
    }

    private static String readTextAndRemove(Element copy, String name) {
        NodeList list = copy.getElementsByTagName(name);
        if (list == null || list.getLength() == 0) {
            return null;
        }
        Node node = list.item(0);
        String text = node.getTextContent();
        copy.removeChild(node);
        return text;
    }

    private static LocalTime readTimeAndRemove(Element copy, String name) {
        String text = readTextAndRemove(copy, name);
        if (text == null) {
            return null;
        } else {
            return LocalTime.parse(text, HHmm);
        }
    }

    private static LocalDate readDateAndRemove(Element copy, String name) {
        String text = readTextAndRemove(copy, name);
        if (text == null) {
            return null;
        } else {
            return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
        }
    }
}
