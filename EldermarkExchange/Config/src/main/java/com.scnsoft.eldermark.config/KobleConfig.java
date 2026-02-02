package com.scnsoft.eldermark.config;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.hoh.util.HapiSocketTlsFactoryWrapper;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.PipeParser;
import com.scnsoft.eldermark.services.hl7.HL7CertificateTlsSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ReflectionUtils;

@Configuration
@PropertySource("classpath:integration/koble/koble.properties")
public class KobleConfig {

    private static final Logger logger = LoggerFactory.getLogger(NucleusConfig.class);

    @Value("${koble.keystore.path}")
    private String keystorePath;

    @Value("${koble.keystore.password}")
    private String keystorePassword;

    @Bean
    public HL7CertificateTlsSocketFactory hapiSocketFactory(){
        HL7CertificateTlsSocketFactory trustSocketFactory = new HL7CertificateTlsSocketFactory();
        trustSocketFactory.setKeystoreFilename(keystorePath);
        trustSocketFactory.setKeystorePassphrase(keystorePassword);
        return trustSocketFactory;
    }

    @Bean
    public HapiContext kobleContext(){
         HapiContext hapiContext = new DefaultHapiContext() {
            public synchronized PipeParser getPipeParser() {
                try {
                    java.lang.reflect.Field parserField = DefaultHapiContext.class.getDeclaredField("pipeParser");
                    parserField.setAccessible(true);
                    if (ReflectionUtils.getField(parserField, this) == null) {
                        PipeParser parser = new PipeParser(this) {
                            @Override
                            public Message parse(String message) throws HL7Exception {
                                logger.info("[KOBLE] {}", message);
                                return super.parse(message);
                            }
                        };
                        ReflectionUtils.setField(parserField, this, parser);
                    }
                    return super.getPipeParser();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            public synchronized GenericParser getGenericParser() {
                try {
                    java.lang.reflect.Field parserField = DefaultHapiContext.class.getDeclaredField("genericParser");
                    parserField.setAccessible(true);
                    if (ReflectionUtils.getField(parserField, this) == null) {
                        GenericParser parser = new GenericParser(this) {
                            @Override
                            public Message parse(String message) throws HL7Exception {
                                logger.info("[KOBLE] {}", message);
                                return super.parse(message);
                            }
                        };
                        ReflectionUtils.setField(parserField, this, parser);
                    }
                    return super.getGenericParser();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        };
        HapiSocketTlsFactoryWrapper hapiSocketFactory = new HapiSocketTlsFactoryWrapper(hapiSocketFactory());
        hapiContext.setSocketFactory(hapiSocketFactory);
        return hapiContext;
    }

}
