package org.openhealthtools.openxds.registry.patient;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.group.ADT_A01_IN1IN2IN3;
import ca.uhn.hl7v2.model.v231.group.ADT_A01_PR1ROL;
import ca.uhn.hl7v2.model.v231.group.ADT_A03_PR1ROL;
import ca.uhn.hl7v2.model.v231.message.ADT_A01;
import ca.uhn.hl7v2.model.v231.message.ADT_A03;
import ca.uhn.hl7v2.model.v231.segment.IN1;
import ca.uhn.hl7v2.model.v231.segment.PR1;
import com.misyshealthcare.connect.net.IConnectionDescription;
import com.misyshealthcare.connect.net.Identifier;
import org.apache.commons.lang.StringUtils;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;
import org.openhealthexchange.openpixpdq.ihe.registry.HL7;
import org.openhealthtools.common.utils.CustomAssigningAuthorityUtil;
import org.openhealthtools.openxds.registry.patient.helpers.MsgFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class XdsEntityUtil {

    private final static Logger LOG = Logger.getLogger(XdsEntityUtil.class.getName());

    private final static Map<Class, MsgFunction<PR1>> GET_PR1_FUNCTION_MAP = new HashMap<Class, MsgFunction<PR1>>();

    static {
        GET_PR1_FUNCTION_MAP.put(ADT_A01.class, new MsgFunction<PR1>() {
            @Override
            public PR1 apply(final Message message) throws HL7Exception {
                final ADT_A01_PR1ROL pr1ROL = ((ADT_A01_PR1ROL) message.get("PR1ROL"));
                return pr1ROL.getPR1();
            }
        });
        GET_PR1_FUNCTION_MAP.put(ADT_A03.class, new MsgFunction<PR1>() {
            @Override
            public PR1 apply(final Message message) throws HL7Exception {
                final ADT_A03_PR1ROL pr1ROL = ((ADT_A03_PR1ROL) message.get("PR1ROL"));
                return pr1ROL.getPR1();
            }
        });
    }

    private final static Map<Class, MsgFunction<IN1>> GET_IN1_FUNCTION_MAP = new HashMap<Class, MsgFunction<IN1>>();

    static {
        GET_IN1_FUNCTION_MAP.put(ADT_A01.class, new MsgFunction<IN1>() {
            @Override
            public IN1 apply(final Message message) throws HL7Exception {
                final ADT_A01_IN1IN2IN3 in1In2In3 = ((ADT_A01_IN1IN2IN3) message.get("IN1IN2IN3"));
                return in1In2In3.getIN1();
            }
        });
    }


    public static String convertPatientIdentifier(List<PatientIdentifier> patient) {
        for (PatientIdentifier patientIdentifier : patient) {
            String pid = convertPatientIdentifier(patientIdentifier);
            if (StringUtils.isNotEmpty(pid)) {
                return pid;
            }
        }
        return null;
    }

    public static String convertPatientIdentifier(PatientIdentifier patientIdentifier) {
        String assignAuth = encodeAssigningIdentifier(patientIdentifier.getAssigningAuthority());
        String assignFac = encodeAssigningIdentifier(patientIdentifier.getAssigningFacility());
        String patientId = patientIdentifier.getId();
        String typeCode = patientIdentifier.getIdentifierTypeCode();
        //todo refactor
        if (assignFac == null && typeCode == null)
            return patientId + "^^^" + assignAuth;
        else if (assignFac != null && typeCode == null) {
            return patientId + "^^^" + assignAuth + "^^" + assignFac;
        } else if (assignFac == null && typeCode != null) {
            return patientId + "^^^" + assignAuth + "^" + typeCode;
        } else if (assignFac != null && typeCode != null) {
            return patientId + "^^^" + assignAuth + "^" + typeCode + "^" + assignFac;
        }
        return null;
    }

    private static String encodeAssigningIdentifier(Identifier identifier) {
        String namespaceId = identifier.getNamespaceId();
        String universalId = identifier.getUniversalId();
        String universalIdType = identifier.getUniversalIdType();
        if (namespaceId != null && universalId != null && universalIdType != null)
            return namespaceId + "&" + universalId + "&" + universalIdType;
        else if (namespaceId == null && universalId != null && universalIdType != null) {
            return "&" + universalId + "&" + universalIdType;
        } else if (namespaceId != null && universalIdType == null) {
            return namespaceId + "&" + universalId + "&";
        }
        return null;
    }

    /**
     * Converts a patientId from CX format to an {@link PatientIdentifier} object.
     *
     * @param patientId the patient id, exp. 12321^^^&1.3.6.1.4.1.21367.2009.1.2.300&ISO
     * @return the {@link PatientIdentifier}
     */
    public static PatientIdentifier convertPatientIdentifier(String patientId, IConnectionDescription connection){
        String patId = HL7.getIdFromCX(patientId);

        Identifier assigningAuthority = HL7.getAssigningAuthorityFromCX(patientId);
        if (connection != null) {
            assigningAuthority = CustomAssigningAuthorityUtil.reconcileIdentifier(assigningAuthority, connection);
        }
        Identifier assigningFacility = CustomHL7.getAssigningFacilityFromCX(patientId);

        PatientIdentifier pid = new PatientIdentifier();
        pid.setId(patId);
        pid.setAssigningAuthority(assigningAuthority);
        pid.setAssigningFacility(assigningFacility);
        return pid;
    }
}
