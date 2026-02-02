package com.scnsoft.eldermark.consana.sync.server.services.consumers.impl;

import com.scnsoft.eldermark.consana.sync.common.services.db.SqlServerService;
import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;
import com.scnsoft.eldermark.consana.sync.server.log.*;
import com.scnsoft.eldermark.consana.sync.server.model.ResidentIdentifyingData;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Encounter;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Medication;
import com.scnsoft.eldermark.consana.sync.server.model.entity.*;
import com.scnsoft.eldermark.consana.sync.server.services.*;
import com.scnsoft.eldermark.consana.sync.server.services.consumers.CommitWrapper;
import com.scnsoft.eldermark.consana.sync.server.services.consumers.ReceivePatientService;
import com.scnsoft.eldermark.consana.sync.server.services.converter.*;
import com.scnsoft.eldermark.consana.sync.server.services.gateway.ConsanaGateway;
import com.scnsoft.eldermark.consana.sync.server.services.producers.DocumentUploadQueueProducer;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import com.scnsoft.eldermark.consana.sync.server.validator.ReceivePatientDtoValidator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hl7.fhir.instance.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.consana.sync.server.constants.FhirConstants.SIMPLY_CONNECT_ID_EXTENSION_URL;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
@Transactional(noRollbackFor = Exception.class)
public class ReceivePatientServiceImpl implements ReceivePatientService {

    private static final Logger logger = LoggerFactory.getLogger(ReceivePatientServiceImpl.class);

    @Autowired
    private SqlServerService sqlServerService;

    @Autowired
    private ReceivePatientDtoValidator receivePatientDtoValidator;

    @Autowired
    private ConsanaGateway consanaGateway;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private PersonService personService;

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private ProblemObservationService problemObservationService;

    @Autowired
    private AllergyObservationService allergyObservationService;

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private XCoverageToInsuranceConverter xCoverageToInsuranceConverter;

    @Autowired
    private PatientToResidentConverter patientToResidentConverter;

    @Autowired
    private ConsanaDomainResourceToLongLegacyIdAwareEntityConverter<MedicationOrder, Medication> medicationOrderToMedicationConverter;

    @Autowired
    private ConsanaDomainResourceToLongLegacyIdAwareEntityConverter<Condition, ProblemObservation> conditionToProblemObservationConverter;

    @Autowired
    private ConsanaDomainResourceToLongLegacyIdAwareEntityConverter<AllergyIntolerance, AllergyObservation> allergyIntoleranceToAllergyObservationConverter;

    @Autowired
    private ConsanaDomainResourceToLongLegacyIdAwareEntityConverter<org.hl7.fhir.instance.model.Encounter, Encounter> consanaEncounterToEncounterConverter;

    @Autowired
    private DocumentUploadQueueProducer documentUploadQueueProducer;

    @Autowired
    private DocumentUploadQueueConverter documentUploadQueueConverter;

    @Autowired
    private LogService logService;

    @Autowired
    private CommitWrapper commitWrapper;

    @Autowired
    private XMapToMedicationActionPlanDataConverter medicationActionPlanDataConverter;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    public void receive(ReceiveConsanaPatientQueueDto patientDto) {
        logger.info("Received " + patientDto);
        var patientLog = new ConsanaPatientLog(patientDto.getConsanaXRefId(), patientDto.getOrganizationId(),
                patientDto.getCommunityId(), patientDto.getUpdateType());

        try {
            receivePatientDtoValidator.validate(patientDto);
        } catch (Exception e) {
            logger.error("Consana payload is not valid {}", patientDto);
            processException(patientLog, e);
            return;
        }

        sqlServerService.openKey();

        switch (patientDto.getUpdateType()) {
            case PATIENT_UPDATE:
                processPatientUpdate(patientDto, patientLog);
                return;
            case MAP_CLOSED:
                processMapClosed(patientDto, patientLog);
                return;
        }
    }

    private void processPatientUpdate(ReceiveConsanaPatientQueueDto patientDto, ConsanaPatientLog patientLog) {
        var patientResidentPair = updateResident(patientDto, patientLog);
        if (patientResidentPair == null) {
            return;
        }

        updateMedications(patientResidentPair.getFirst(), patientResidentPair.getSecond(), patientLog);
        updateEncounters(patientResidentPair.getFirst(), patientResidentPair.getSecond(), patientLog);
        updateProblems(patientResidentPair.getFirst(), patientResidentPair.getSecond(), patientLog);
        updateAllergies(patientResidentPair.getFirst(), patientResidentPair.getSecond(), patientLog);
    }

    private Pair<Patient, Resident> updateResident(ReceiveConsanaPatientQueueDto patientDto, ConsanaPatientLog patientLog) {
        return commitWrapper.executeWithCommit(() -> {
            var patient = consanaGateway.getPatient(patientDto.getConsanaXRefId(), patientDto.getOrganizationId());
            if (patient == null) {
                logger.error("No Consana patient with scid = {} and organization oid = {}", patientDto.getConsanaXRefId(), patientDto.getOrganizationId());
                patientLog.setUpdateType(ConsanaLogUpdateType.CREATED);
                processException(patientLog, "No Consana patient");
                return null;
            }
            var resident = createOrUpdatePatient(patientDto, patient, patientLog);
            return Pair.of(patient, resident);
        }, e -> {
            logger.error("Exception during creating or updating patient", e);
            processException(patientLog, e);
        });
    }

    private Resident createOrUpdatePatient(ReceiveConsanaPatientQueueDto patientDto, Patient patient, ConsanaPatientLog patientLog) {
        var convertedResident = patientToResidentConverter
                .convert(patient, patientDto.getConsanaXRefId(), patientDto.getOrganizationId(), patientDto.getCommunityId());
        var foundResident = residentService.find(fetchIdentifyingData(patientDto.getConsanaXRefId(), convertedResident));

        Resident resident;
        if (foundResident.isEmpty()) {
            patientLog.setUpdateType(ConsanaLogUpdateType.CREATED);
            resident = residentService.create(convertedResident);
            processSuccess(patientLog);
            logger.info("New resident with id {} is created based on patient scid {} information", resident.getId(), patientDto.getConsanaXRefId());
        } else {
            //todo check if found resident's community has integration enabled because it might be disabled after sync
            resident = foundResident.get();
            patientLog.setUpdateType(ConsanaLogUpdateType.UPDATED);
            try {
                resident = residentService.updateEmptyFields(foundResident.get(), convertedResident);
                resident.setPerson(personService.updateEmptyFields(resident.getPerson(), convertedResident.getPerson()));
                resident = residentService.update(resident);
                updateInsurance(patient, resident, patientDto.getOrganizationId());
                processSuccess(patientLog);
                logger.info("Resident with id {} is updated with patient scid {} information", foundResident.get().getId(), patientDto.getConsanaXRefId());
            } catch (Exception e) {
                processException(patientLog, e);
            }
        }
        return resident;
        //todo somehow (jms?) run merging of just created resident. Old portal has ResidentMatcherService#findFullMatchedResidents for that
    }

    private ResidentIdentifyingData fetchIdentifyingData(String xrefId, Resident resident) {
        var result = new ResidentIdentifyingData();
        result.setConsanaXrefId(xrefId);
        result.setCommunityId(resident.getFacility().getId());
        result.setFirstName(resident.getFirstName());
        result.setLastName(resident.getLastName());
        result.setBirthDate(resident.getBirthDate());
        result.setSsn(resident.getSocialSecurity());
        return result;
    }

    private void updateMedications(Patient patient, Resident resident, ConsanaPatientLog patientLog) {
        commitWrapper.executeWithCommit(() -> {
            var medicationOrders = consanaGateway.getMedicationOrders(patient, resident.getDatabase().getConsanaXOwningId());
            var foundMedications = medicationService.findAllConsanaMedicationsByResident(resident)
                    .stream()
                    .collect(Collectors.toMap(Medication::getConsanaId, Function.identity()));

            if (isNotEmpty(medicationOrders)) {
                medicationOrders.forEach(order -> {
                    var foundMedication = foundMedications.getOrDefault(order.getId(), null);
                    var medicationLog = new ConsanaMedicationLog(order.getId(), patientLog);
                    commitWrapper.executeWithCommit(() -> {
                        var medication = convertAndSetUpdateType(order, foundMedication, medicationOrderToMedicationConverter, resident, medicationLog);
                        if (medication != null) {
                            medicationService.saveMedication(medication);
                            foundMedications.remove(order.getId());
                            processSuccess(medicationLog);
                        }
                        return null;
                    }, e -> {
                        foundMedications.remove(order.getId());
                        processException(medicationLog, e);
                    });
                });
                if (!foundMedications.isEmpty()) {
                    logger.info("Delete all unfound medications with consana ids: {}", foundMedications.keySet());
                    deleteMedications(foundMedications, patientLog);
                }
            } else {
                logger.info("Medication order list was empty so all medications were deleted for residentId: {}", resident.getId());
                deleteMedications(foundMedications, patientLog);
            }
            return null;
        }, e -> processException(new ConsanaMedicationLog(null, patientLog), e));
    }

    private void deleteMedications(Map<String, Medication> medications, ConsanaPatientLog patientLog) {
        medications.values().forEach(m -> commitWrapper.executeWithCommit(() -> {
            medicationService.delete(m);
            var log = new ConsanaMedicationLog(m.getConsanaId(), patientLog);
            log.setUpdateType(ConsanaLogUpdateType.DELETED);
            processSuccess(log);
            return null;
        }, e -> {
            var log = new ConsanaMedicationLog(m.getConsanaId(), patientLog);
            log.setUpdateType(ConsanaLogUpdateType.DELETED);
            processException(log, e);
        }));
    }

    private void updateInsurance(Patient patient, Resident resident, String orgId) {
        var coverage = consanaGateway.getXCoverage(patient, orgId);
        Long insurancesCount = insuranceService.countResidentInsurances(resident.getId());
        if (insurancesCount == 0) {
            ofNullable(coverage)
                    .map(xCoverageToInsuranceConverter::convert)
                    .ifPresent(ins -> insuranceService.updateInsurance(ins, resident));
        }
    }

    private void processMapClosed(ReceiveConsanaPatientQueueDto patientDto, ConsanaPatientLog patientLog) {
        var patientResidentPair = commitWrapper.executeWithCommit(() -> updateResident(patientDto, patientLog), ex -> {
        });
        if (patientResidentPair == null) {
            return;
        }

        commitWrapper.executeWithCommit(() -> {
            var xMedicationActionPlans = consanaGateway.getXMedicationActionPlans(patientResidentPair.getFirst().getId(), patientDto.getOrganizationId());
            if (isNotEmpty(xMedicationActionPlans)) {
                var foundConsanaIds = documentService.getAllDocumentConsanaIds(patientResidentPair.getSecond());
                xMedicationActionPlans.stream()
                        .filter(plan -> "Closed".equals(plan.getStatus()))
                        .filter(plan -> !foundConsanaIds.contains(plan.getId()))
                        .forEach(plan ->
                                commitWrapper.executeWithCommit(() -> {
                                    var xMedia = consanaGateway.getXMedia(plan.getId(), patientDto.getOrganizationId());
                                    var data = medicationActionPlanDataConverter.convert(xMedia);
                                    if (data != null) {
                                        var messageDto = documentUploadQueueConverter.convert(data, patientResidentPair.getSecond(), plan);
                                        documentUploadQueueProducer.send(messageDto);
                                        var log = new ConsanaMedicationActionPlanLog(plan.getId(), patientLog);
                                        log.setUpdateType(ConsanaLogUpdateType.CREATED);
                                        processSuccess(log);
                                    }
                                    return null;
                                }, e -> processException(new ConsanaMedicationActionPlanLog(plan.getId(), patientLog), e)));
            }
            return null;
        }, e -> processException(new ConsanaMedicationActionPlanLog(null, patientLog), e));
    }

    private void updateProblems(Patient patient, Resident resident, ConsanaPatientLog patientLog) {
        commitWrapper.executeWithCommit(() -> {
            var conditions = consanaGateway.getConditions(patient.getId(), resident.getDatabase().getConsanaXOwningId());
            var foundProblemObservations = problemObservationService.findAllConsanaProblemObservationsByResident(resident)
                    .stream()
                    .collect(Collectors.toMap(ProblemObservation::getConsanaId, Function.identity()));

            if (isNotEmpty(conditions)) {
                conditions.forEach(condition -> {
                    var foundProblemObservation = foundProblemObservations.getOrDefault(condition.getId(), null);
                    var problemObservationLog = new ConsanaProblemObservationLog(condition.getId(), patientLog);
                    commitWrapper.executeWithCommit(() -> {
                        ProblemObservation problemObservation = convertAndSetUpdateType(condition, foundProblemObservation, conditionToProblemObservationConverter, resident, problemObservationLog);
                        if (problemObservation != null) {
                            var savedProblemObservation = problemObservationService.saveProblemObservation(problemObservation);
                            if (condition.hasEncounter()) {
                                encounterService.addProblemObservationToEncounterByConsanaId(savedProblemObservation, condition.getEncounter().getReference());
                            }
                            foundProblemObservations.remove(condition.getId());
                            processSuccess(problemObservationLog);
                        }
                        return null;
                    }, e -> {
                        foundProblemObservations.remove(condition.getId());
                        processException(problemObservationLog, e);
                    });
                });
                if (!foundProblemObservations.isEmpty()) {
                    logger.info("Delete all unfound problem observations with consana ids: {}", foundProblemObservations.keySet());
                    deleteProblemObservations(foundProblemObservations, patientLog);
                }
            } else {
                logger.info("Condition list was empty so all problem observations were deleted for residentId: {}", resident.getId());
                deleteProblemObservations(foundProblemObservations, patientLog);
            }
            return null;
        }, e -> processException(new ConsanaProblemObservationLog(null, patientLog), e));
    }

    private void deleteProblemObservations(Map<String, ProblemObservation> problemObservations, ConsanaPatientLog patientLog) {
        problemObservations.values().forEach(p -> commitWrapper.executeWithCommit(() -> {
            problemObservationService.delete(p);
            var log = new ConsanaProblemObservationLog(p.getConsanaId(), patientLog);
            log.setUpdateType(ConsanaLogUpdateType.DELETED);
            processSuccess(log);
            return null;
        }, e -> {
            var log = new ConsanaProblemObservationLog(p.getConsanaId(), patientLog);
            log.setUpdateType(ConsanaLogUpdateType.DELETED);
            processException(log, e);
        }));
    }

    private void updateAllergies(Patient patient, Resident resident, ConsanaPatientLog patientLog) {
        commitWrapper.executeWithCommit(() -> {
            var allergyIntolerances = consanaGateway.getAllergyIntolerances(patient.getId(), resident.getDatabase().getConsanaXOwningId());
            var foundAllergyObservations = allergyObservationService.findAllConsanaAllergyObservations(resident)
                    .stream()
                    .collect(Collectors.toMap(AllergyObservation::getConsanaId, Function.identity()));

            if (isNotEmpty(allergyIntolerances)) {
                allergyIntolerances.forEach(allergyIntolerance -> {
                    var foundAllergyObservation = foundAllergyObservations.getOrDefault(allergyIntolerance.getId(), null);
                    var allergyObservationLog = new ConsanaAllergyObservationLog(allergyIntolerance.getId(), patientLog);
                    commitWrapper.executeWithCommit(() -> {
                        AllergyObservation allergyObservation = convertAndSetUpdateType(allergyIntolerance, foundAllergyObservation, allergyIntoleranceToAllergyObservationConverter, resident, allergyObservationLog);
                        if (allergyObservation != null) {
                            allergyObservationService.saveAllergyObservation(allergyObservation);
                            foundAllergyObservations.remove(allergyIntolerance.getId());
                            processSuccess(allergyObservationLog);
                        }
                        return null;
                    }, e -> {
                        foundAllergyObservations.remove(allergyIntolerance.getId());
                        processException(allergyObservationLog, e);
                    });
                });
                if (!foundAllergyObservations.isEmpty()) {
                    logger.info("Delete all unfound allergy observations with consana ids: {}", foundAllergyObservations.keySet());
                    deleteAllergyObservations(foundAllergyObservations, patientLog);
                }
            } else {
                logger.info("AllergyIntolerance list was empty so all allergy observations were deleted for residentId: {}", resident.getId());
                deleteAllergyObservations(foundAllergyObservations, patientLog);
            }
            return null;
        }, e -> processException(new ConsanaAllergyObservationLog(null, patientLog), e));
    }

    private void deleteAllergyObservations(Map<String, AllergyObservation> allergyObservations, ConsanaPatientLog patientLog) {
        allergyObservations.values().forEach(a -> commitWrapper.executeWithCommit(() -> {
            allergyObservationService.delete(a);
            var log = new ConsanaAllergyObservationLog(a.getConsanaId(), patientLog);
            log.setUpdateType(ConsanaLogUpdateType.DELETED);
            processSuccess(log);
            return null;
        }, e -> {
            var log = new ConsanaAllergyObservationLog(a.getConsanaId(), patientLog);
            log.setUpdateType(ConsanaLogUpdateType.DELETED);
            processException(log, e);
        }));
    }

    private void updateEncounters(Patient patient, Resident resident, ConsanaPatientLog patientLog) {
        commitWrapper.executeWithCommit(() -> {
            var encounters = consanaGateway.getEncounters(patient.getId(), resident.getDatabase().getConsanaXOwningId());
            var foundEncounters = encounterService.findAllConsanaEncountersByResident(resident)
                    .stream()
                    .collect(Collectors.toMap(Encounter::getConsanaId, Function.identity()));

            if (isNotEmpty(encounters)) {
                encounters.forEach(enc -> {
                    var foundEncounter = foundEncounters.getOrDefault(enc.getId(), null);
                    var encounterLog = new ConsanaEncounterLog(enc.getId(), patientLog);
                    commitWrapper.executeWithCommit(() -> {
                        Encounter encounter = convertAndSetUpdateType(enc, foundEncounter, consanaEncounterToEncounterConverter, resident, encounterLog);
                        if (encounter != null) {
                            encounterService.saveEncounter(encounter);
                            foundEncounters.remove(enc.getId());
                            processSuccess(encounterLog);
                        }
                        return null;
                    }, e -> {
                        foundEncounters.remove(enc.getId());
                        processException(encounterLog, e);
                    });
                });
                if (!foundEncounters.isEmpty()) {
                    logger.info("Delete all unfound encounters with consana ids: {}", foundEncounters.keySet());
                    deleteEncounters(foundEncounters, patientLog);
                }
            } else {
                logger.info("Encounter list was empty so all encounters were deleted for residentId: {}", resident.getId());
                deleteEncounters(foundEncounters, patientLog);
            }
            return null;
        }, e -> processException(new ConsanaEncounterLog(null, patientLog), e));
    }

    private void deleteEncounters(Map<String, Encounter> encounters, ConsanaPatientLog patientLog) {
        encounters.values().forEach(ec -> commitWrapper.executeWithCommit(() -> {
            encounterService.delete(ec);
            var log = new ConsanaEncounterLog(ec.getConsanaId(), patientLog);
            log.setUpdateType(ConsanaLogUpdateType.DELETED);
            processSuccess(log);
            return null;
        }, e -> {
            var log = new ConsanaEncounterLog(ec.getConsanaId(), patientLog);
            log.setUpdateType(ConsanaLogUpdateType.DELETED);
            processException(log, e);
        }));
    }

    private <S extends DomainResource, T extends LongLegacyIdAwareEntity> T convertAndSetUpdateType(S source, T target, ConsanaDomainResourceToLongLegacyIdAwareEntityConverter<S, T> converter, Resident resident, ConsanaBaseLog log) {
        if (target != null) {
            log.setUpdateType(ConsanaLogUpdateType.UPDATED);
            return converter.convertInto(source, resident, target);
        } else {
            if (isCreatedBySimplyConnect(source.getExtension())) {
                return null;
            }
            log.setUpdateType(ConsanaLogUpdateType.CREATED);
            return converter.convert(source, resident);
        }
    }

    private boolean isCreatedBySimplyConnect(List<Extension> extensions) {
        return FhirConversionUtils.findExtensionByUrl(extensions, SIMPLY_CONNECT_ID_EXTENSION_URL) != null;
    }

    private void processSuccess(ConsanaBaseLog log) {
        log.setIsSuccess(true);
        log.setProcessTime(Instant.now());
        logService.saveInNewTransaction(log);
    }

    private void processException(ConsanaBaseLog log, Exception ex) {
        processException(log, ExceptionUtils.getMessage(ex));
    }

    private void processException(ConsanaBaseLog log, String errorMessage) {
        log.setIsSuccess(false);
        log.setProcessTime(Instant.now());
        log.setErrorMessage(errorMessage);
        logService.saveInNewTransaction(log);
    }
}
