package com.scnsoft.eldermark.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;


public abstract class XmlToMapParser {

    private static final ThreadLocal<SAXParserFactory> factoryThreadLocal = ThreadLocal.withInitial(() -> {
        try {
            return SAXParserFactory.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });

    private XmlToMapParser() {
    }

    @SuppressFBWarnings(value = "XXE_SAXPARSER", justification = "responseXml doesnt depend on user input")
    public static Map<String, String> parse(String responseXml) {
        try {
            var parser = getSaxParser();
            var handler = new ResponseHandler();
            parser.parse(new ByteArrayInputStream(responseXml.getBytes()), handler);
            return handler.valueMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> parseEscaped(String escapedResponseXml) {
        return parse(StringEscapeUtils.unescapeXml(escapedResponseXml));
    }

    private static SAXParser getSaxParser() throws ParserConfigurationException, SAXException {
        return factoryThreadLocal.get().newSAXParser();
    }

    private static class ResponseHandler extends DefaultHandler {

        private Map<String, String> valueMap;
        private Deque<String> keyStack;

        @Override
        public void startDocument() {
            valueMap = new HashMap<>();
            keyStack = new ArrayDeque<>();
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            var key = keyStack.peek();
            valueMap.put(key, new String(ch, start, length));
        }

        @Override
        public void startElement(String uri, String lName, String qName, Attributes attr) {
            keyStack.push(qName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            keyStack.pop();
        }
    }
}
