package com.scnsoft.eldermark.hl7v2.source;

import com.scnsoft.eldermark.entity.document.ccd.CdaConstants;
import com.scnsoft.eldermark.hl7v2.model.Identifier;
import com.scnsoft.eldermark.hl7v2.model.PatientIdentifiersHolder;
import com.scnsoft.eldermark.hl7v2.model.PersonIdentifier;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public enum HL7v2IntegrationPartner {
    //our system to be able to test messages
    EXCHANGE {
        @Override
        public boolean matchesTCPSource(MessageSource messageSource) {
            //these are the values populated by ADT sender
            if ("OTHER_KIOSK".equals(messageSource.getSendingApplication().getHd1_NamespaceID().getValueOrEmpty()) &&
                    "HIMSSSANDIEGO".equals(messageSource.getSendingFacility().getHd1_NamespaceID().getValueOrEmpty())) {
                return true;
            }
            return false;

            //not checking localhost because other integrations can come as testing
            //otherwise check if localhost
            //todo find better way
//            return "127.0.0.1".equals(messageSource.getSourceAddress())
//                    || "localhost".equals(messageSource.getSourceAddress());
        }
    },

    YARDI {
        @Override
        public boolean matchesTCPSource(MessageSource messageSource) {
            var sendingAppName = messageSource.getSendingApplication().getHd1_NamespaceID().getValueOrEmpty();
            if ("YARDI".equals(sendingAppName) || "EHRX".equals(sendingAppName)) {
                return true;
            }

            var receivingApp = messageSource.getReceivingApplication().getHd1_NamespaceID().getValueOrEmpty();
            var receivingFacility = messageSource.getReceivingFacility().getHd1_NamespaceID().getValueOrEmpty();
            if ("SimplyHIE".equals(receivingApp) && "Yardi".equals(receivingFacility)) {
                return true;
            }

            return false;
        }

        @Override
        public Identifier adjustAssigningAuthority(Identifier assigningAuthority) {
            assigningAuthority.setUniversalId(YARDI_OID);
            assigningAuthority.setUniversalIdType("ISO");
            return assigningAuthority;
        }

        public Identifier adjustAssigningFacility(Identifier assigningAuthority) {
            //do nothing by default.
            return assigningAuthority;
        }
    },

    PROGNOCIS {
        @Override
        public boolean matchesTCPSource(MessageSource messageSource) {
            var sendingAppName = messageSource.getSendingApplication().getHd1_NamespaceID().getValueOrEmpty();
            return "PROGNOCIS".equals(sendingAppName);
            //messages from this partner come from SFTP. Enabling TCP just for testing
        }

        @Override
        public Identifier adjustAssigningAuthority(Identifier assigningAuthority) {
            assigningAuthority.setUniversalId(PROGNOCIS_OID);
            assigningAuthority.setUniversalIdType("ISO");
            return assigningAuthority;
        }

        public Identifier adjustAssigningFacility(Identifier assigningAuthority) {
            //do nothing by default.
            return assigningAuthority;
        }
    };

    public static final String YARDI_OID = CdaConstants.EXCHANGE_INTEGRATIONS_WITHOUT_OID + ".1";
    public static final String PROGNOCIS_OID = CdaConstants.EXCHANGE_INTEGRATIONS_WITHOUT_OID + ".2";

    public List<PersonIdentifier> prioritizePatientIdentifiersForSearch(PatientIdentifiersHolder patientIdentifiersHolder) {
        //by default just use pid3
        return patientIdentifiersHolder.getPid3Identifiers();
    }

    public PersonIdentifier getIdentifierToCreatePatient(PatientIdentifiersHolder patientIdentifiersHolder) {
        //pid3 by default
        return patientIdentifiersHolder.getPid3Identifiers().get(0);
    }

    public List<PersonIdentifier> getIdentifiersToCreatePatientMPI(PatientIdentifiersHolder patientIdentifiersHolder) {
        //pid3 by default
        return patientIdentifiersHolder.getPid3Identifiers();
    }

    public Identifier adjustAssigningAuthority(Identifier assigningAuthority) {
        //do nothing by default.
        //for some integrations we might need to add OID (universalId) if they don't send it
        return assigningAuthority;
    }

    public Identifier adjustAssigningFacility(Identifier assigningAuthority) {
        //do nothing by default.
        return assigningAuthority;
    }

    public abstract boolean matchesTCPSource(MessageSource messageSource);

    /**
     * For integrations sending messages as files specify file content encoding
     * UTF-8 by default
     *
     * @return
     */
    public Charset getFileContentCharset() {
        return StandardCharsets.UTF_8;
    }

}
