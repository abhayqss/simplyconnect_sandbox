package com.scnsoft.eldermark.hl7v2.processor.patient.demographics;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.message.ADT_A39;
import com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.datatype.DataTypeConverter;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PatientDemographicsExtractorImpl implements PatientDemographicsExtractor {

    private static final Set<String> SUPPORTED_VERSIONS = Set.of(
            "2.1",
            "2.2",
            "2.3",
            "2.3.1",
            "2.4",
            "2.5",
            "2.5.1"
    );

    @Autowired
    private DataTypeConverter dataTypeService;

    @Override
    public HL7v2PatientDemographics extractDemographics(Message message, MessageSource messageSource) throws ApplicationException, HL7Exception {
        if (!SUPPORTED_VERSIONS.contains(message.getVersion())) {
            throw new ApplicationException("Unexpected HL7 version " + message.getVersion());
        }
        InitializingHL7v2PatientDemographics demographics;
        if (message instanceof ADT_A39) {
            demographics = new HL7v2PatientDemographicsADTA39(message, messageSource, dataTypeService, 0);
        } else {
            demographics = new HL7v2PatientDemographicsDefault(message, messageSource, dataTypeService);
        }
        demographics.init();
        return demographics;
    }
}
