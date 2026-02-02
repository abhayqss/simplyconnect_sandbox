/**
 * Copyright (c) 2009-2010 Misys Open Source Solutions (MOSS) and others
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * <p>
 * Contributors:
 * Misys Open Source Solutions - initial API and implementation
 * -
 */

package org.openhealthtools.openxds.registry.patient;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import com.misyshealthcare.connect.net.Identifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthexchange.openpixpdq.data.MessageHeader;
import org.openhealthexchange.openpixpdq.data.Patient;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;
import org.openhealthtools.openxds.XdsFactory;
import org.openhealthtools.openxds.dao.DaoImpl;
import org.openhealthtools.openxds.dao.RawXdsMessageDao;
import org.openhealthtools.openxds.dao.ResidentUpdateQueueDao;
import org.openhealthtools.openxds.dao.XdsRegistryPatientDao;
import org.openhealthtools.openxds.entity.PersonIdentifier;
import org.openhealthtools.openxds.entity.RawXdsMessage;
import org.openhealthtools.openxds.entity.Resident;
import org.openhealthtools.openxds.entity.message.AdtMessage;
import org.openhealthtools.openxds.exchange.CcnRestClient;
import org.openhealthtools.openxds.registry.api.RegistryPatientContext;
import org.openhealthtools.openxds.registry.api.RegistryPatientException;
import org.openhealthtools.openxds.registry.api.XdsRegistryPatientService;
import org.openhealthtools.openxds.registry.patient.parser.facade.AdtMessageParserFacade;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The class is the core of XDS Registry Patient Manager and
 * provides the patient life cycle operations such as createPatient,
 * updatePatient, mergePatients and unmergePatients.
 *
 * @author <a href="mailto:Rasakannu.Palaniyandi@misys.com">Raja</a>
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, value = "eexTransactionManager")
public class XdsRegistryPatientServiceImpl implements XdsRegistryPatientService {

    private static Log log = LogFactory.getLog(XdsRegistryPatientServiceImpl.class);

    private CcnRestClient ccnRestClient;
    private Boolean sendOutEventToExchange;


    private XdsRegistryPatientDao xdsRegistryPatientDao;
    private ResidentUpdateQueueDao residentUpdateQueueDao;

    private RawXdsMessageDao rawXdsMessageDao;
    private DaoImpl baseDao;

    private XdsRepoPatientService eexRepoPatient;

    private String assigningAuthorityNamespace;
    private String assigningAuthorityUniversal;

    private Identifier assigningAuthorityIdentifier = null;

    private AdtMessageParserFacade adtMessageParserFacade = XdsFactory.getInstance().getBean(AdtMessageParserFacade.class);

    @Transactional(propagation = Propagation.REQUIRES_NEW, value = "eexTransactionManager")
    public boolean isValidPatient(PatientIdentifier pid, RegistryPatientContext context) throws RegistryPatientException {
        try {
            PersonIdentifier identifier = PersonIdentifier.createFromPatientIdentifier(pid);
            PersonIdentifier personIdentifier = xdsRegistryPatientDao.getPersonById(identifier);

            return personIdentifier != null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed while trying to determine if the patient with the given identifier is known." + e, e);
            throw new RegistryPatientException("Failed while trying to determine if the patient with the given identifier is known." + e, e);
        }
    }

    @Override
    public Long getResidentId(PatientIdentifier pid, RegistryPatientContext context) throws RegistryPatientException {
        try {
            PersonIdentifier identifier = PersonIdentifier.createFromPatientIdentifier(pid);
            PersonIdentifier personIdentifier = xdsRegistryPatientDao.getPersonById(identifier);

            return personIdentifier.getResidentId();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed while trying to determine if the patient with the given identifier is known." + e, e);
            throw new RegistryPatientException("Failed while trying to determine if the patient with the given identifier is known." + e, e);
        }
    }

	/*@Override
	public PatientIdentifier getPatientId(Long residentId, RegistryPatientContext context) throws RegistryPatientException {
		try {
			PersonIdentifier pid = xdsRegistryPatientDao.getPersonByResidentId(residentId);
			Identifier assigningAuthotrity = new Identifier(pid.getAssigningAuthorityNamespace(), pid.getAssigningAuthorityUniversal(), pid.getAssigningFacilityUniversalType());
			PatientIdentifier patientIdentifier = new PatientIdentifier(pid.getPatientId(), assigningAuthotrity);
			return patientIdentifier;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed while trying to determine if the patient with the given identifier is known." + e, e);
			throw new RegistryPatientException(e.getMessage());
		}
	}*/

    @Transactional(propagation = Propagation.REQUIRES_NEW, value = "eexTransactionManager")
    public void createPatient(Patient patient, RegistryPatientContext context, Long msgDbId) throws RegistryPatientException {
        try {
            Resident patientInRepo = null;
//			boolean isLocalMPIPresent = false;
            //STEP 1
            // - Search for existing patient
            // - Update existing MPI Records
            // - Search if LocalMPI record presented
            for (PatientIdentifier pid : patient.getPatientIds()) {
                PersonIdentifier identifier = PersonIdentifier.createFromPatientIdentifier(pid);
                identifier.setDeleted(patient.isDeathIndicator() ? "Y" : "N");
                identifier.setMerged("N");
                PersonIdentifier personIdentifier = xdsRegistryPatientDao.getPersonById(identifier);
                if (personIdentifier != null) {
                    if (personIdentifier.getMerged().equals("Y")) {
                        personIdentifier.setMerged("N");
                        xdsRegistryPatientDao.updatePersonIdentifier(personIdentifier);
                        //TODO unmerge
                    }
                    if (assigningAuthorityUniversal.equals(personIdentifier.getAssigningAuthorityUniversal())) {
                        patientInRepo = personIdentifier.getResident();
//						isLocalMPIPresent = true;
                    } else {
                        if (patientInRepo == null) patientInRepo = personIdentifier.getResident();
                    }
                }
            }

            // -if resident not already in the system - create new
            boolean newPatient = false;
            if (patientInRepo == null) {
                patientInRepo = eexRepoPatient.createPatient(patient);
                newPatient = true;
            }

            // -if no local MPI Record present , create new one
/*			if (!isLocalMPIPresent) {
				PatientIdentifier localPid = new PatientIdentifier(patientInRepo.getId().toString(), getAssigningAuthorityIdentifier());
				PersonIdentifier identifier = PersonIdentifier.createFromPatientIdentifier(localPid);
				identifier.setDeleted(patient.isDeathIndicator() ? "Y" : "N");
				identifier.setMerged("N");
				identifier.setResident(patientInRepo);
				xdsRegistryPatientDao.savePersonIdentifier(identifier);
			}*/
            if (Boolean.TRUE.equals(sendOutEventToExchange)) {
                final String adtType = context.getMessageHeader().getTriggerEvent();
                ccnRestClient.postAdt(patientInRepo.getId(), adtType, msgDbId, newPatient);
            }

            //TODO: Patient matching may be there
            //Create new Patient Identifier records in MPI
            for (PatientIdentifier pid : patient.getPatientIds()) {
                PersonIdentifier identifier = PersonIdentifier.createFromPatientIdentifier(pid);
                identifier.setDeleted(patient.isDeathIndicator() ? "Y" : "N");
                identifier.setMerged("N");

                PersonIdentifier personIdentifier = xdsRegistryPatientDao.getPersonById(identifier);
                if (personIdentifier == null) {
                    identifier.setResident(patientInRepo);
                    xdsRegistryPatientDao.savePersonIdentifier(identifier);
                }
            }
        } catch (Exception e) {
            log.error("Failed while trying to save a new patient record in the patient registry." + e, e);
            throw new RegistryPatientException("Failed while trying to save a new patient record in the patient registry." + e, e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, value = "eexTransactionManager")
    public void updatePatient(Patient patient, RegistryPatientContext context, final Long msgDbId) throws RegistryPatientException {
        try {
            Long repositoryPatientId = null;

            for (PatientIdentifier pid : patient.getPatientIds()) {
                PersonIdentifier identifier = PersonIdentifier.createFromPatientIdentifier(pid);

                PersonIdentifier personIdentifier = xdsRegistryPatientDao.getPersonById(identifier);

                PersonIdentifier updatedIdentifier = PersonIdentifier.createFromPatientIdentifier(personIdentifier, pid);
                updatedIdentifier.setDeleted(patient.isDeathIndicator() ? "Y" : "N");

                xdsRegistryPatientDao.updatePersonIdentifier(updatedIdentifier);

                repositoryPatientId = personIdentifier.getResidentId();
            }

            final Resident resident = eexRepoPatient.updatePatient(patient, repositoryPatientId);
            if (sendOutEventToExchange) {
                final String adtType = context.getMessageHeader().getTriggerEvent();
                ccnRestClient.postAdt(repositoryPatientId, adtType, msgDbId, false);
            }
        } catch (Exception e) {
            log.error("Failed while trying to update a patient record in the patient registry." + e, e);
            throw new RegistryPatientException("Failed while trying to update a patient record in the patient registry." + e, e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "eexTransactionManager")
    public void mergePatients(Patient survivingPatient, Patient mergePatient, RegistryPatientContext context) throws RegistryPatientException {
        PatientIdentifier survivingId = survivingPatient.getPatientIds().get(0);
        PersonIdentifier personIdentifier = PersonIdentifier.createFromPatientIdentifier(survivingId);
        PersonIdentifier survivingPersonId = xdsRegistryPatientDao.getPersonById(personIdentifier);
        for (PatientIdentifier pid : mergePatient.getPatientIds()) {
            PersonIdentifier retiredId = PersonIdentifier.createFromPatientIdentifier(pid);
            PersonIdentifier retiredPersonId = xdsRegistryPatientDao.getPersonById(retiredId);
            if (retiredPersonId == null || survivingPersonId == null) {
                log.error("Unable to locate one of the two patient records that need to be merged.");
                throw new RegistryPatientException("Unable to identify the two patient records that need to be merged.");
            }
            retiredPersonId.setSurvivingPatientId(survivingPersonId.getPatientId());
            retiredPersonId.setMerged("Y");
            try {

                xdsRegistryPatientDao.mergePersonIdentifier(retiredPersonId);
                //todo don't insert if record exists
                xdsRegistryPatientDao.mergeResidents(retiredPersonId.getResidentId(), survivingPersonId.getResidentId());
                residentUpdateQueueDao.pushResidentMerge(retiredPersonId.getResidentId(), survivingPersonId.getResidentId());

            } catch (Exception e) {
                log.error("Failed while trying to merge two patient records in the patient registry." + e, e);
                throw new RegistryPatientException("Failed while trying to merge two patient records in the patient registry." + e, e);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "eexTransactionManager")
    public void unmergePatients(Patient survivingPatient, Patient mergePatient, RegistryPatientContext context) throws RegistryPatientException {
        PatientIdentifier survivingId = survivingPatient.getPatientIds().get(0);
        PersonIdentifier personIdentifier = PersonIdentifier.createFromPatientIdentifier(survivingId);
        PersonIdentifier survivingPersonId = xdsRegistryPatientDao.getPersonById(personIdentifier);
        for (PatientIdentifier pid : mergePatient.getPatientIds()) {
            PersonIdentifier retiredId = PersonIdentifier.createFromPatientIdentifier(pid);
            PersonIdentifier retiredPersonId = xdsRegistryPatientDao.getPersonById(retiredId);
            if (retiredPersonId == null || survivingPersonId == null) {
                log.error("Unable to locate one of the two patient records that need to be unmerged.");
                throw new RegistryPatientException("Unable to identify the two patient records that need to be unmerged.");
            }
            if (retiredPersonId.getSurvivingPatientId().equals(survivingPersonId.getPatientId())) {
                retiredPersonId.setSurvivingPatientId("");
                retiredPersonId.setMerged("N");
            } else {
                log.error("Unable to unmerge the patient because surviving_patient_id of merge patient is not matched with surviving patient");
                throw new RegistryPatientException("Unable to unmerge the patient because surviving_patient_id of merge patient is not matched with surviving patient");
            }
            try {
                xdsRegistryPatientDao.mergePersonIdentifier(retiredPersonId);
                xdsRegistryPatientDao.unMergeResidents(retiredPersonId.getResidentId(), survivingPersonId.getResidentId());
                residentUpdateQueueDao.pushResidentMerge(retiredPersonId.getResidentId(), survivingPersonId.getResidentId());
            } catch (Exception e) {
                log.error("Failed while trying to unmerge two patient records in the patient registry." + e, e);
                throw new RegistryPatientException("Failed while trying to unmerge two patient records in the patient registry." + e, e);
            }
        }
    }

//	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false, value = "eexTransactionManager")
//	public void saveRawXdsMessage (String message) {
//		RawXdsMessage rawXdsMessage = new RawXdsMessage();
//		rawXdsMessage.setDate(new Date());
//		rawXdsMessage.setMessage(message);
//		rawXdsMessageDao.saveRawXdsMessage(rawXdsMessage);
//	}

    public void setXdsRegistryPatientDao(XdsRegistryPatientDao xdsRegistryPatientDao) {
        this.xdsRegistryPatientDao = xdsRegistryPatientDao;
    }

    public void setResidentUpdateQueueDao(ResidentUpdateQueueDao residentUpdateQueueDao) {
        this.residentUpdateQueueDao = residentUpdateQueueDao;
    }

    public void setEexRepoPatient(XdsRepoPatientService eexRepoPatient) {
        this.eexRepoPatient = eexRepoPatient;
    }

    public String getAssigningAuthorityNamespace() {
        return assigningAuthorityNamespace;
    }

    public void setAssigningAuthorityNamespace(String assigningAuthorityNamespace) {
        this.assigningAuthorityNamespace = assigningAuthorityNamespace;
    }

    public String getAssigningAuthorityUniversal() {
        return assigningAuthorityUniversal;
    }

    public void setAssigningAuthorityUniversal(String assigningAuthorityUniversal) {
        this.assigningAuthorityUniversal = assigningAuthorityUniversal;
    }

    private Identifier getAssigningAuthorityIdentifier() {
        if (assigningAuthorityIdentifier == null) {
            assigningAuthorityIdentifier = new Identifier(assigningAuthorityNamespace, assigningAuthorityUniversal, "ISO");
        }
        System.out.println("AssignAuthIdent: " + assigningAuthorityIdentifier.getUniversalId() + " " + assigningAuthorityIdentifier.getAuthorityNameString() + " " + assigningAuthorityIdentifier.getNamespaceId() + " " + assigningAuthorityIdentifier.getUniversalIdType());
        return assigningAuthorityIdentifier;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false, value = "eexTransactionManager")
    public void saveRawXdsMessage(String message) {
        RawXdsMessage rawXdsMessage = new RawXdsMessage();
        rawXdsMessage.setDate(new Date());
        rawXdsMessage.setMessage(message);
        rawXdsMessageDao.saveRawXdsMessage(rawXdsMessage);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false, value = "eexTransactionManager")
    public Long saveXdsMessage(MessageHeader header, Message msgIn) throws HL7Exception, ApplicationException {
//        RawXdsMessage rawXdsMessage = new RawXdsMessage();
//        rawXdsMessage.setDate(new Date());
        final AdtMessage adtMessage = adtMessageParserFacade.parse(msgIn);
        if (adtMessage != null) {
            baseDao.persist(adtMessage);
            return adtMessage.getId();
        }
        return null;
    }


    public CcnRestClient getCcnRestClient() {
        return ccnRestClient;
    }

    public void setCcnRestClient(CcnRestClient ccnRestClient) {
        this.ccnRestClient = ccnRestClient;
    }

    public Boolean getSendOutEventToExchange() {
        return sendOutEventToExchange;
    }

    public void setSendOutEventToExchange(Boolean sendOutEventToExchange) {
        this.sendOutEventToExchange = sendOutEventToExchange;
    }

    public void setRawXdsMessageDao(RawXdsMessageDao rawXdsMessageDao) {
        this.rawXdsMessageDao = rawXdsMessageDao;
    }

    public void setBaseDao(DaoImpl dao) {
        this.baseDao = dao;
    }
}
