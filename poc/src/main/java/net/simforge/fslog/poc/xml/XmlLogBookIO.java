package net.simforge.fslog.poc.xml;

import net.simforge.fslog.poc.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.simforge.fslog.poc.FSLogConsoleApp.HHmm;

public class XmlLogBookIO {
    public static LogBook readLogBook(InputStream is) {
        try {
            LogBook logBook = new LogBook();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(is);

            Element root = document.getDocumentElement();
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
                LogBookEntry entry;
                if (tagName.equals("FlightReport")) {
                    entry = readFlightReport(element);
                } else if (tagName.equals("Transfer")) {
                    entry = readTransfer(element);
                } else if (tagName.equals("Discontinuity")) {
                    entry = readDiscontinuity(element);
                } else {
                    throw new IllegalArgumentException();
                }

                ValidationResult result = logBook.add(entry);
                if (result.getOverallResult() != ValidationResult.Result.OK) {
                    throw new IllegalStateException();
                }
            }

            return logBook;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeLogBook(LogBook logBook, OutputStream os) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element root = document.createElement("FSLog");
            document.appendChild(root);

            List<LogBookEntry> entries = logBook.getEntries();
            for (LogBookEntry entry : entries) {
                if (entry instanceof Discontinuity) {
                    writeDiscontinuity(document, (Discontinuity) entry);
                } else if (entry instanceof FlightReport) {
                    writeFlightReport(document, (FlightReport) entry);
                } else if (entry instanceof Transfer) {
                    writeTransfer(document, (Transfer) entry);
                } else {
                    throw new IllegalStateException();
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(os);

            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private static FlightReport readFlightReport(Element element) {
        FlightReport.Builder builder = new FlightReport.Builder();

        Element copy = (Element) element.cloneNode(true);

        builder = builder
                .setDate(readDateAndRemove(copy, "Date"))
                .setCallsign(readTextAndRemove(copy, "Callsign"))
                .setFlightNumber(readTextAndRemove(copy, "FlightNumber"))
                .setAircraftType(readTextAndRemove(copy, "AircraftType"))
                .setAircraftRegistration(readTextAndRemove(copy, "AircraftRegistration"))
                .setDeparture(readTextAndRemove(copy, "Departure"))
                .setDestination(readTextAndRemove(copy, "Destination"))
                .setTimeOut(readTimeAndRemove(copy, "TimeOut"))
                .setTimeOff(readTimeAndRemove(copy, "TimeOff"))
                .setTimeOn(readTimeAndRemove(copy, "TimeOn"))
                .setTimeIn(readTimeAndRemove(copy, "TimeIn"))
                .setRestOfXml(copy);

        return builder.build();
    }

    private static void writeFlightReport(Document document, FlightReport flightReport) {
        Element element;
        if (flightReport.getRestOfXml() == null) {
            element = document.createElement("FlightReport");
        } else {
            element = (Element) document.importNode(flightReport.getRestOfXml(), true);
        }
        document.getDocumentElement().appendChild(element);

        if (flightReport.getDate() != null)
            writeText(element, "Date", DateTimeFormatter.ISO_DATE.format(flightReport.getDate()));
        if (flightReport.getCallsign() != null)
            writeText(element, "Callsign", flightReport.getCallsign());
        if (flightReport.getFlightNumber() != null)
            writeText(element, "FlightNumber", flightReport.getFlightNumber());
        if (flightReport.getAircraftType() != null)
            writeText(element, "AircraftType", flightReport.getAircraftType());
        if (flightReport.getAircraftRegistration() != null)
            writeText(element, "AircraftRegistration", flightReport.getAircraftRegistration());
        if (flightReport.getDeparture() != null)
            writeText(element, "Departure", flightReport.getDeparture());
        if (flightReport.getDestination() != null)
            writeText(element, "Destination", flightReport.getDestination());
        if (flightReport.getTimeOut() != null)
            writeText(element, "TimeOut", HHmm.format(flightReport.getTimeOut()));
        if (flightReport.getTimeOff() != null)
            writeText(element, "TimeOff", HHmm.format(flightReport.getTimeOff()));
        if (flightReport.getTimeOn() != null)
            writeText(element, "TimeOn", HHmm.format(flightReport.getTimeOn()));
        if (flightReport.getTimeIn() != null)
            writeText(element, "TimeIn", HHmm.format(flightReport.getTimeIn()));
    }

    private static Transfer readTransfer(Element element) {
        Transfer.Builder builder = new Transfer.Builder();

        Element copy = (Element) element.cloneNode(true);

        builder = builder
                .setDate(readDateAndRemove(copy, "Date"))
                .setDeparture(readTextAndRemove(copy, "Departure"))
                .setDestination(readTextAndRemove(copy, "Destination"))
                .setTimeOut(readTimeAndRemove(copy, "TimeOut"))
                .setTimeIn(readTimeAndRemove(copy, "TimeIn"))
                .setMethod(readEnumAndRemove(copy, "Method", Transfer.Method.class))
                .setStatus(readEnumAndRemove(copy, "Status", Transfer.Status.class))
                .setRestOfXml(copy);

        return builder.build();
    }

    private static void writeTransfer(Document document, Transfer transfer) {
        Element element;
        if (transfer.getRestOfXml() == null) {
            element = document.createElement("Transfer");
        } else {
            element = (Element) document.importNode(transfer.getRestOfXml(), true);
        }
        document.getDocumentElement().appendChild(element);

        if (transfer.getDate() != null)
            writeText(element, "Date", DateTimeFormatter.ISO_DATE.format(transfer.getDate()));
        if (transfer.getDeparture() != null)
            writeText(element, "Departure", transfer.getDeparture());
        if (transfer.getDestination() != null)
            writeText(element, "Destination", transfer.getDestination());
        if (transfer.getTimeOut() != null)
            writeText(element, "TimeOut", HHmm.format(transfer.getTimeOut()));
        if (transfer.getTimeIn() != null)
            writeText(element, "TimeIn", HHmm.format(transfer.getTimeIn()));
        if (transfer.getMethod() != null)
            writeText(element, "Method", transfer.getMethod().code());
        if (transfer.getStatus() != null)
            writeText(element, "Status", transfer.getStatus().code());
    }

    private static Discontinuity readDiscontinuity(Element element) {
        Discontinuity.Builder builder = new Discontinuity.Builder();

        Element copy = (Element) element.cloneNode(true);

        builder
                .setDate(readDateAndRemove(copy, "Date"))
                .setTime(readTimeAndRemove(copy, "Time"))
                .setComment(readTextAndRemove(copy, "Comment"))
                .setRestOfXml(copy);

        return builder.build();
    }

    private static void writeDiscontinuity(Document document, Discontinuity discontinuity) {
        Element element;
        if (discontinuity.getRestOfXml() == null) {
            element = document.createElement("Discontinuity");
        } else {
            element = (Element) document.importNode(discontinuity.getRestOfXml(), true);
        }
        document.getDocumentElement().appendChild(element);

        if (discontinuity.getDate() != null)
            writeText(element, "Date", DateTimeFormatter.ISO_DATE.format(discontinuity.getDate()));
        if (discontinuity.getTime() != null)
            writeText(element, "Time", HHmm.format(discontinuity.getTime()));
        if (discontinuity.getComment() != null)
            writeText(element, "Comment", discontinuity.getComment());
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

    private static void writeText(Element element, String name, String text) {
        Document document = element.getOwnerDocument();
        Element textNode = document.createElement(name);
        textNode.appendChild(document.createTextNode(text));
        element.appendChild(textNode);
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

    private static <T> T readEnumAndRemove(Element copy, String name, Class clazz) {
        String text = readTextAndRemove(copy, name);
        if (text == null) {
            return null;
        } else {
            Method byCode;
            try {
                byCode = clazz.getMethod("byCode", String.class);
                T result = (T) byCode.invoke(null, text);
                if (result != null) {
                    return result;
                } else {
                    throw new IllegalArgumentException("Could not find " + clazz.getSimpleName() + " enum value by '" + text + "' code");
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
