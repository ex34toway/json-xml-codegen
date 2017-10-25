package uw.codegen.jsonxml.example.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * @author liliang
 * @since 2017/8/15
 */
public class JAXBXmlNameSpaceFilter extends XMLFilterImpl {

    private boolean ignoreNamespace = true;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (this.ignoreNamespace) uri = "";
        super.startElement(uri, localName, qName, atts);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.ignoreNamespace) uri = "";
        super.endElement(uri, localName, localName);
    }

    @Override
    public void startPrefixMapping(String prefix, String url) throws SAXException {
        if (!this.ignoreNamespace) super.startPrefixMapping("", url);
    }
}
