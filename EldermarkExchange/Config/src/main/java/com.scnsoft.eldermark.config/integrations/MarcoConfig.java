package com.scnsoft.eldermark.config.integrations;

import com.scnsoft.eldermark.services.inbound.marco.MarcoDocumentMetadata;
import com.scnsoft.eldermark.services.inbound.marco.MarcoInboundFilesServiceRunCondition;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Conditional(MarcoInboundFilesServiceRunCondition.class)
@PropertySource("classpath:integration/marco/marco.properties")
public class MarcoConfig {

    @Bean
    public XStream xStream(){
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("MarcoDocumentMetadata", MarcoDocumentMetadata.class);
        xstream.processAnnotations(MarcoDocumentMetadata.class);
        return xstream;
    }
}
