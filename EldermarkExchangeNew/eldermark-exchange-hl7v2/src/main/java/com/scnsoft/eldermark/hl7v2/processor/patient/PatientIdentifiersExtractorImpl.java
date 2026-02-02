package com.scnsoft.eldermark.hl7v2.processor.patient;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.CX;
import ca.uhn.hl7v2.model.v251.datatype.HD;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import com.scnsoft.eldermark.hl7v2.model.Identifier;
import com.scnsoft.eldermark.hl7v2.model.PatientIdentifiersHolder;
import com.scnsoft.eldermark.hl7v2.model.PersonIdentifier;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PatientIdentifiersExtractorImpl implements PatientIdentifiersExtractor {

    @Override
    public PatientIdentifiersHolder extractPatientIdentifiers(Message message, MessageSource messageSource) throws HL7Exception {
        var pid = HapiUtils.getPid(message);

        var result = new PatientIdentifiersHolder();

        result.setPid2Identifier(cxToPatientIdentifier(pid.getPid2_PatientID(), messageSource));

        if (pid.getPid3_PatientIdentifierList() == null || pid.getPid3_PatientIdentifierList().length == 0) {
            result.setPid3Identifiers(List.of());
        } else {
            result.setPid3Identifiers(Stream.of(pid.getPid3_PatientIdentifierList())
                    .map(cx -> cxToPatientIdentifier(cx, messageSource))
                    .collect(Collectors.toList()));
        }

        return result;
    }

    private PersonIdentifier cxToPatientIdentifier(CX cx, MessageSource messageSource) {
        var identifier = new PersonIdentifier();
        identifier.setId(cx.getCx1_IDNumber().getValue());
        identifier.setIdentifierTypeCode(cx.getIdentifierTypeCode()
                .getValue());

        Identifier assignAuth = hdToIdentifier(cx.getAssigningAuthority());
        Identifier assignFac = hdToIdentifier(cx.getAssigningFacility());

        identifier.setAssigningAuthority(messageSource.getHl7v2IntegrationPartner().adjustAssigningAuthority(assignAuth));
        identifier.setAssigningFacility(messageSource.getHl7v2IntegrationPartner().adjustAssigningFacility(assignFac));

        return identifier;
    }

    private Identifier hdToIdentifier(HD hd) {
        return new Identifier(
                hd.getNamespaceID().getValue(),
                hd.getUniversalID().getValue(),
                hd.getUniversalIDType().getValue());
    }
}
