package uw.codegen.jsonxml.example.util;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * JAXB风格解析xml工具
 * @author liliang
 * @since 2017/7/31
 */
public class JAXBXmlUtil {

    private final static JAXBXmlNameSpaceFilter nsfFilter = new JAXBXmlNameSpaceFilter();

    /**
     * 解析xml
     * @param clazz
     * @param resp
     * @param <T>
     * @return
     * @throws JAXBException
     */
    @SuppressWarnings("unchecked")
    public static <T> T read(Class<T> clazz, String resp) throws JAXBException {
        try {
            JAXBContext jc = JAXBContext.newInstance(clazz);
            Unmarshaller u = jc.createUnmarshaller();
            XMLReader reader = XMLReaderFactory.createXMLReader();
            nsfFilter.setParent(reader);
            InputSource input = new InputSource(new StringReader(resp));
            SAXSource source = new SAXSource(nsfFilter, input);
            return (T) u.unmarshal(source);
        }catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将JavaBean写入xml
     * @param load
     * @param obj
     * @return
     * @throws JAXBException
     */
    @SuppressWarnings("unchecked")
    public static String write(Class<?> load, Object obj) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(load);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "GBK");
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj,writer);
        return writer.toString();
    }
}
