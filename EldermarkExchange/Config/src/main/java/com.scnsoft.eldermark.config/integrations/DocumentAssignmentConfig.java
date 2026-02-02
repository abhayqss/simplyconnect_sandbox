package com.scnsoft.eldermark.config.integrations;


import com.scnsoft.eldermark.services.inbound.InboundFilesServiceRunCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Conditional(InboundFilesServiceRunCondition.class)
@PropertySource("classpath:integration/document/document-assignment.properties")
public class DocumentAssignmentConfig {

}
