package uw.codegen.jsonxml.codegen;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author liliang
 * @since 2017/12/15
 */
public class XmlCodeGenTest {

    @Test
    public void testParse() throws IOException {
        String content = FileUtils.readFileToString(new File("D:\\tmp\\lvmama_price.xml"));
        com.fasterxml.jackson.dataformat.xml.XmlMapper xmlMapper = new com.fasterxml.jackson.dataformat.xml.XmlMapper();
//        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        GDSPublisherRequest request = xmlMapper.readValue(content,GDSPublisherRequest.class);
        System.out.println(request.getFIDELIO_RateUpdateNotifRQ().getProperties());
    }
}
