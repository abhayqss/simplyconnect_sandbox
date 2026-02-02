package com.scnsoft.eldermark.hl7v2.processor;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.AbstractPrimitive;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.MSG;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import com.scnsoft.eldermark.dao.AdtMessageDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import com.scnsoft.eldermark.hl7v2.exception.RegistryPatientException;
import com.scnsoft.eldermark.hl7v2.parse.facade.AdtMessageParserFacade;
import com.scnsoft.eldermark.hl7v2.processor.allergy.HL7v2AllergyService;
import com.scnsoft.eldermark.hl7v2.processor.insurance.HL7v2InsuranceService;
import com.scnsoft.eldermark.hl7v2.processor.patient.PatientDemographicsUpdater;
import com.scnsoft.eldermark.hl7v2.processor.patient.PatientFactory;
import com.scnsoft.eldermark.hl7v2.processor.patient.PatientIdentifiersExtractor;
import com.scnsoft.eldermark.hl7v2.processor.patient.PatientResolver;
import com.scnsoft.eldermark.hl7v2.processor.patient.demographics.PatientDemographicsExtractor;
import com.scnsoft.eldermark.hl7v2.processor.problem.HL7v2ProblemService;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HL7v2MessageServiceImpl implements HL7v2MessageService {
    private static final Logger logger = LoggerFactory.getLogger(HL7v2MessageServiceImpl.class);

    //todo refactoring - each message type should have its own processing strategy
    private static final List<String> UPDATE_DEMOGRAPHICS_TYPES = List.of("A08");

    @Autowired
    private PatientIdentifiersExtractor patientIdentifiersExtractor;

    @Autowired
    private PatientResolver patientResolver;

    @Autowired
    private PatientFactory patientFactory;

    @Autowired
    private PatientDemographicsExtractor patientDemographicsExtractor;

    @Autowired
    private PatientDemographicsUpdater patientDemographicsUpdater;

    @Autowired
    private AdtMessageParserFacade adtMessageParserFacade;

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private HL7v2InsuranceService insuranceService;

    @Autowired
    private HL7v2AllergyService allergyService;

    @Autowired
    private HL7v2ProblemService problemService;

    @Override
    @Transactional
    public MessageProcessingResult processMessage(Message message, MessageSource messageSource) throws HL7Exception, ApplicationException {
        if (messageSource.getHl7v2IntegrationPartner() == null) {
            throw new RegistryPatientException("Failed to resolve integration partner");
        }

        var adtType = resolveAdtType(message);
        logger.info("Resolved adt type {}", adtType);

        //todo message validations?
        var pair = getOrCreatePatient(message, messageSource, adtType);
        var patient = pair.getFirst();
        var isNew = pair.getSecond();

        //todo different actions for different message types
        logger.info("Parsing message into Simply Connect database...");
        var msg = parseAndSaveAdtMessage(message, messageSource);

        var result = new MessageProcessingResult();
        result.setAdtType(adtType);
        result.setClientId(patient.getId());
        result.setClientNew(isNew);

        //todo create allergies and problems
        if (msg != null) {
            logger.info("Parsed message id {}", msg.getId());
            result.setParsedAdtMessageId(msg.getId());
            updateClientCcdData(msg, patient);
            insuranceService.updateInsurances(patient, msg);
        }

        return result;
    }

    private Pair<Client, Boolean> getOrCreatePatient(Message message, MessageSource messageSource, String adtType) throws HL7Exception, ApplicationException {
        var patientIdentifiers = patientIdentifiersExtractor.extractPatientIdentifiers(message, messageSource);
        logger.info("Searching for clients for identifiers {}", patientIdentifiers);

        var clientOpt = patientResolver.resolvePatient(patientIdentifiers, messageSource);

        if (clientOpt.isPresent()) {
            var client = clientOpt.get();
            logger.info("Found client {} for ADT message", client.getId());
            if (shouldUpdateDemographics(adtType)) {
                logger.info("Updating client {} demographics", client.getId());
                var demographics = patientDemographicsExtractor.extractDemographics(message, messageSource);
                return new Pair<>(patientDemographicsUpdater.updateDemographics(client, demographics), false);
            } else {
                logger.info("Client {} demographics won't be updated", client.getId());
                return new Pair<>(clientOpt.get(), false);
            }
        } else {
            var demographics = patientDemographicsExtractor.extractDemographics(message, messageSource);
            logger.info("Creating client for ADT message");
            var client = patientFactory.createPatient(patientIdentifiers, demographics, messageSource);
            logger.info("Created client {} for ADT message", client.getId());
            return new Pair<>(client, true);
        }
    }

    private String resolveAdtType(Message message) throws HL7Exception {
        var msh = HapiUtils.getMSH(message);
        return Optional.of(msh)
                .map(MSH::getMessageType)
                .map(MSG::getMsg2_TriggerEvent)
                .map(AbstractPrimitive::getValue)
                .orElseThrow();
    }

    private boolean shouldUpdateDemographics(String adtType) {
        return UPDATE_DEMOGRAPHICS_TYPES.contains(adtType);
    }

    private AdtMessage parseAndSaveAdtMessage(Message message, MessageSource messageSource) throws ApplicationException, HL7Exception {
        return adtMessageParserFacade.parse(message, messageSource).map(adtMessage -> {
                    adtMessageDao.save(adtMessage);
                    return adtMessage;
                })
                .orElse(null);
    }

    private void updateClientCcdData(AdtMessage msg, Client client) {
        allergyService.updateAllergies(client, msg);
        problemService.updateProblems(client, msg);
    }
}
