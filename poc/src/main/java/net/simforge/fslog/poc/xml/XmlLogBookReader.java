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

        builder = builder
                .setDate(LocalDate.parse(element.getElementsByTagName("Date").item(0).getTextContent(), DateTimeFormatter.ISO_DATE))
                .setDeparture(element.getElementsByTagName("Departure").item(0).getTextContent())
                .setDestination(element.getElementsByTagName("Destination").item(0).getTextContent())
                .setTimeOut(parseTime(element.getElementsByTagName("TimeOut")))
                .setTimeOff(parseTime(element.getElementsByTagName("TimeOff")))
                .setTimeOn(parseTime(element.getElementsByTagName("TimeOn")))
                .setTimeIn(parseTime(element.getElementsByTagName("TimeIn")));

        return builder.build();
    }

    private static Transfer readTransfer(Element element) {
        Transfer.Builder builder = new Transfer.Builder();

        builder = builder
                .setDate(LocalDate.parse(element.getElementsByTagName("Date").item(0).getTextContent(), DateTimeFormatter.ISO_DATE))
                .setDeparture(element.getElementsByTagName("Departure").item(0).getTextContent())
                .setDestination(element.getElementsByTagName("Destination").item(0).getTextContent());
//        tranfer.setTimeOut(parseTime(element.getElementsByTagName("TimeOut")));
//        tranfer.setTimeOff(parseTime(element.getElementsByTagName("TimeOff")));
//        tranfer.setTimeOn(parseTime(element.getElementsByTagName("TimeOn")));
//        tranfer.setTimeIn(parseTime(element.getElementsByTagName("TimeIn")));

        return builder.build();
    }

    private static Discontinuity readDiscontinuity(Element element) {
        Discontinuity.Builder builder = new Discontinuity.Builder();

        builder.setDate(LocalDate.parse(element.getElementsByTagName("Date").item(0).getTextContent(), DateTimeFormatter.ISO_DATE));

        return builder.build();
    }

    private static LocalTime parseTime(NodeList timeNodeList) {
        if (timeNodeList == null || timeNodeList.getLength() == 0) {
            return null;
        }
        String time = timeNodeList.item(0).getTextContent();
        if (time == null) {
            return null;
        } else {
            return LocalTime.parse(time, HHmm);
        }
    }
}
