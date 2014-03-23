package utils;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler {

    private static String CLASSNAME = "class";
    private String lastElement;
    private boolean inElement = false;
    public Object object;

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        lastElement = qName;
        if (qName != CLASSNAME)
            inElement = true;
        else {
            String className = attributes.getValue(0);
            object = ReflectionHelper.createInstance(className);
        }
    }

    public void endElement(String uri, String localName, String qName) {
        lastElement = null;
        inElement = false;
    }

    public void characters(char ch[], int start, int length) {
        if (inElement) {
            String value = new String(ch, start, length);
            ReflectionHelper.setFieldValue(object, lastElement, value);
        }
    }
}