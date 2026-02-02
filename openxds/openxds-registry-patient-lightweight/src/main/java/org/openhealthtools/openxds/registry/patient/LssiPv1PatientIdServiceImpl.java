package org.openhealthtools.openxds.registry.patient;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.CX;
import ca.uhn.hl7v2.model.v231.datatype.PL;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.model.v231.segment.PV1;
import com.misyshealthcare.connect.net.Identifier;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;
import org.openhealthtools.openxds.dao.OrganizationDao;
import org.openhealthtools.openxds.dao.XdsRegistryPatientDao;
import org.openhealthtools.openxds.entity.Organization;
import org.openhealthtools.openxds.entity.PersonIdentifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Transactional
public class LssiPv1PatientIdServiceImpl implements LssiPv1PatientIdService {

    private static final Log log = LogFactory.getLog(LssiPv1PatientIdServiceImpl.class);

    private static final String LSSI_DATABASE_OID = "2.16.840.1.113883.3.1452.100.55";

    private XdsRegistryPatientDao xdsRegistryPatientDao;

    private OrganizationDao organizationDao;

    @Override
    public String findExistingIdentifier(String originalPatientIdentifierStr) {
        try {
            if (StringUtils.isEmpty(originalPatientIdentifierStr)) {
                return originalPatientIdentifierStr;
            }
            final PatientIdentifier patientIdentifier = XdsEntityUtil.convertPatientIdentifier(originalPatientIdentifierStr, null);
            if (!isLssiIdentifier(patientIdentifier) || hasAssigningFacility(patientIdentifier)) {
                return originalPatientIdentifierStr;
            }
            final PersonIdentifier personIdentifier = PersonIdentifier.createFromPatientIdentifier(patientIdentifier);

            //1. try to find identifier in default community.
            final PersonIdentifier foundDefaultCommunityPersonIdentifier = xdsRegistryPatientDao.getPersonById(personIdentifier);
            if (foundDefaultCommunityPersonIdentifier != null) {
                copyAssigningFacility(patientIdentifier, foundDefaultCommunityPersonIdentifier);
                return XdsEntityUtil.convertPatientIdentifier(patientIdentifier);
            }

            //2. if not found - try to find any identifier and take first
            final List<PersonIdentifier> allPersonIdentifiers = xdsRegistryPatientDao.getPersonsByPatientIdAndAssigningAuthority(personIdentifier.getPatientId(), personIdentifier.getAssigningAuthority());
            if (CollectionUtils.isEmpty(allPersonIdentifiers)) {
                return originalPatientIdentifierStr;
            }
            final PersonIdentifier first = allPersonIdentifiers.get(0);
            copyAssigningFacility(patientIdentifier, first);
            return XdsEntityUtil.convertPatientIdentifier(patientIdentifier);
        } catch (Exception e) {
            log.warn("Couldn't apply lssi assigning facility trick to string " + originalPatientIdentifierStr
                    + " Root cause " + ExceptionUtils.getFullStackTrace(e));
        }
        return originalPatientIdentifierStr;

    }

    @Override
    public void updateAssigningFacilityAccordingToPv1(Message adtMessage) {
        try {
            final CX patientIdCX = fetchLssiPatientId(adtMessage);
            if (patientIdCX == null) {
                log.info("LSSI patient id not found");
                return;
            }
            if (StringUtils.isNotEmpty(patientIdCX.getAssigningFacility().getUniversalID().getValue())) {
                log.info("Assigning Facility is already set for LSSI patient");
                return;
            }
            final String pv1PointOfCare = fetchPv1PointOfCare(adtMessage);
            if (StringUtils.isEmpty(pv1PointOfCare)) {
                log.info("PV1 point of care is not set for LSSI patient.");
                return;
            }
            final Organization organization = organizationDao.findFirstByNameAndDatabaseOid(pv1PointOfCare, LSSI_DATABASE_OID);
            if (organization == null) {
                log.info("System couldn't find community [" + pv1PointOfCare + "] for LSSI");
                return;
            }
            if (Boolean.TRUE.equals(organization.getXdsDefault())) {
                log.info("Community from PV1 [" + pv1PointOfCare + "] is already set as XdsDefault");
                return;
            }
            if (StringUtils.isEmpty(organization.getOid())) {
                log.info("LSSI community [" + pv1PointOfCare + "] doesn't have OID");
                return;
            }

            patientIdCX.getAssigningFacility().getUniversalID().setValue(organization.getOid());
            patientIdCX.getAssigningFacility().getUniversalIDType().setValue("ISO");

        } catch (HL7Exception e) {
            log.warn("Error during updating assigning facility from PV1-3 for LSSI", e);
            e.printStackTrace();
        }
    }


    private CX fetchLssiPatientId(Message adtMessage) throws HL7Exception {
        final PID pid = (PID) adtMessage.get("PID");
        if (pid == null) {
            return null;
        }
        final CX[] cxArr = pid.getPatientIdentifierList();
        if (cxArr == null || cxArr.length == 0) {
            return null;
        }
        for (CX cx : cxArr) {
            if (isLssiIdentifier(cx)) {
                return cx;
            }
        }
        return null;
    }


    private String fetchPv1PointOfCare(Message adtMessage) throws HL7Exception {
        final PV1 pv1 = (PV1) adtMessage.get("PV1");
        if (pv1 == null) {
            return null;
        }
        final PL pl = pv1.getAssignedPatientLocation();
        if (pl == null) {
            return null;
        }
        return pl.getPointOfCare().getValue();
    }

    private boolean isLssiIdentifier(PatientIdentifier identifier) {
        return identifier != null
                && identifier.getAssigningAuthority() != null
                && LSSI_DATABASE_OID.equals(identifier.getAssigningAuthority().getUniversalId());
    }


    private boolean isLssiIdentifier(CX identifier) {
        return identifier != null
                && identifier.getAssigningAuthority() != null
                && LSSI_DATABASE_OID.equals(identifier.getAssigningAuthority().getUniversalID().getValue());
    }

    private boolean hasAssigningFacility(PatientIdentifier identifier) {
        final Identifier assigningFacility = identifier.getAssigningFacility();
        return assigningFacility != null && StringUtils.isNotEmpty(assigningFacility.getUniversalId()) && StringUtils.isNotEmpty(assigningFacility.getUniversalIdType());
    }

    void copyAssigningFacility(PatientIdentifier patientIdentifier, PersonIdentifier personIdentifier) {
        Identifier assigningFacility = new Identifier(personIdentifier.getAssigningFacilityNamespace(), personIdentifier.getAssigningFacilityUniversal(), personIdentifier.getAssigningFacilityUniversalType());
        patientIdentifier.setAssigningFacility(assigningFacility);
    }

    public XdsRegistryPatientDao getXdsRegistryPatientDao() {
        return xdsRegistryPatientDao;
    }

    public void setXdsRegistryPatientDao(XdsRegistryPatientDao xdsRegistryPatientDao) {
        this.xdsRegistryPatientDao = xdsRegistryPatientDao;
    }

    public OrganizationDao getOrganizationDao() {
        return organizationDao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }
}
