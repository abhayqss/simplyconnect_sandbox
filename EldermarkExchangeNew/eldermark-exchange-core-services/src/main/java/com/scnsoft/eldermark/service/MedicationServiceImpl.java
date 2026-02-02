package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.MedicationDao;
import com.scnsoft.eldermark.dto.medication.SaveMedicationRequest;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.CcdCode_;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.medication.*;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.document.cda.CcdCodeCustomService;
import com.scnsoft.eldermark.service.medispan.MedicationSearchService;
import com.scnsoft.eldermark.util.NdcUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MedicationServiceImpl implements MedicationService {

    public static final String LEGACY_TABLE_MEDICATION_MANUAL = "Medication_Manual";

    private static final Sort ORDER_BY_MEDICATION_START_DATE_DESC = Sort.by(
            Sort.Direction.DESC,
            Medication_.MEDICATION_STARTED
    );

    @Autowired
    private MedicationDao medicationDao;

    @Autowired
    private MedicationSearchService medicationSearchService;

    @Autowired
    private CcdCodeCustomService ccdCodeCustomService;

    @Autowired
    private PersonService personService;

    @Override
    @Transactional
    public Medication save(Medication medication) {
        return medicationDao.save(medication);
    }

    @Override
    @Transactional
    public Medication save(SaveMedicationRequest saveMedicationRequest) {

        var client = saveMedicationRequest.getClient();

        var medication = saveMedicationRequest.getId() == null
                ? createNewMedication(saveMedicationRequest, client)
                : medicationDao.getOne(saveMedicationRequest.getId());

        fillMedication(saveMedicationRequest, medication);

        medicationDao.save(medication);

        updateLegacyIds(medication);

        return medication;
    }

    private void updateLegacyIds(Medication medication) {
        var person = medication.getMedicationSupplyOrder().getAuthor().getPerson();
        personService.updateLegacyId(person);
    }

    private Medication createNewMedication(SaveMedicationRequest saveMedicationRequest, Client client) {

        var drugSearchResult = StringUtils.isNotEmpty(saveMedicationRequest.getNdcCode())
                ? medicationSearchService.findByNdc(saveMedicationRequest.getNdcCode())
                : medicationSearchService.findByMediSpanId(saveMedicationRequest.getMediSpanId());

        var drugInfo = drugSearchResult
                .filter(it -> Objects.equals(it.getMediSpanId(), saveMedicationRequest.getMediSpanId()))
                .orElseThrow(() -> new BusinessException("Invalid NDC code or mediSpanId"));

        var medicationInfo = new MedicationInformation();
        medicationInfo.setLegacyId(0);
        medicationInfo.setLegacyTable(LEGACY_TABLE_MEDICATION_MANUAL);
        medicationInfo.setProductNameText(drugInfo.getName());
        medicationInfo.setOrganization(saveMedicationRequest.getClient().getOrganization());
        var productNameCode = ccdCodeCustomService.findOrCreate(drugInfo.getGpi(), drugInfo.getName(), CodeSystem.MEDISPAN_GPI)
                .orElseThrow(() -> new IllegalStateException("Cannot find or create CCD code"));
        medicationInfo.setProductNameCode(productNameCode);
        medicationInfo.setTranslationProductCodes(new ArrayList<>());

        var medication = new Medication();
        medication.setLegacyId(0);
        medication.setMedicationInformation(medicationInfo);
        medication.setClient(client);
        medication.setManuallyCreated(true);
        medication.setMediSpanId(saveMedicationRequest.getMediSpanId());
        medication.setOrganization(client.getOrganization());

        createEmptyMedicationSupplyOrder(medication);
        createEmptyMedicationReport(medication);

        return medication;
    }

    private void fillMedication(SaveMedicationRequest saveMedicationRequest, Medication medication) {

        if (StringUtils.isNotEmpty(saveMedicationRequest.getNdcCode())) {
            var drugInfo = medicationSearchService.findByNdc(saveMedicationRequest.getNdcCode())
                    .orElse(null);

            if (drugInfo != null && Objects.equals(drugInfo.getMediSpanId(), medication.getMediSpanId())) {
                var normalizedNdcCode = NdcUtils.normalize(saveMedicationRequest.getNdcCode());
                var ndcCode = ccdCodeCustomService.findOrCreate(normalizedNdcCode, drugInfo.getName(), CodeSystem.NDC)
                        .orElseThrow(() -> new IllegalStateException("Cannot find or create CCD code"));
                var translationProductCodes = medication.getMedicationInformation().getTranslationProductCodes();
                translationProductCodes.clear();
                translationProductCodes.add(ndcCode);
            } else {
                throw new BusinessException("Invalid NDC code");
            }
        } else {
            medication.getMedicationInformation().getTranslationProductCodes().clear();
        }

        medication.setSchedule(saveMedicationRequest.getFrequency());
        medication.setFreeTextSig(saveMedicationRequest.getDirections());
        medication.setStatusCode(saveMedicationRequest.getStatus().getCode());

        medication.setMedicationStarted(
                Optional.ofNullable(saveMedicationRequest.getStartedDate())
                        .map(Date::from)
                        .orElse(null)
        );

        medication.setMedicationStopped(
                Optional.ofNullable(saveMedicationRequest.getStoppedDate())
                        .map(Date::from)
                        .orElse(null)
        );

        medication.setComment(saveMedicationRequest.getComment());

        var supplyOrder = medication.getMedicationSupplyOrder();

        supplyOrder.setQuantity(saveMedicationRequest.getPrescriptionQuantity());

        supplyOrder.setTimeHigh(
                Optional.ofNullable(saveMedicationRequest.getPrescriptionExpirationDate())
                        .map(Date::from)
                        .orElse(null)
        );
        supplyOrder.setTimeLow(
                Optional.ofNullable(saveMedicationRequest.getPrescribedDate())
                        .map(Date::from)
                        .orElse(null)
        );

        var authorName = supplyOrder.getAuthor().getPerson().getNames().get(0);

        authorName.setGiven(saveMedicationRequest.getPrescribedBy().getFirstName());
        authorName.setFamily(saveMedicationRequest.getPrescribedBy().getLastName());

        medication.getMedicationReport().setIndicatedFor(saveMedicationRequest.getIndicatedFor());
        medication.getMedicationReport().setDosage(saveMedicationRequest.getDosageQuantity());

        if (medication.getCreatedBy() == null) {
            medication.setCreatedBy(saveMedicationRequest.getAuthor());
            medication.setCreationDatetime(Instant.now());
        } else {
            medication.setUpdatedBy(saveMedicationRequest.getAuthor());
            medication.setUpdateDatetime(Instant.now());
        }
    }

    private void createEmptyMedicationReport(Medication medication) {
        var medicationReport = new MedicationReport();
        medicationReport.setLegacyId(0);
        medicationReport.setLegacyTable(LEGACY_TABLE_MEDICATION_MANUAL);
        medicationReport.setOrganization(medication.getOrganization());
        medicationReport.setMedication(medication);
        medication.setMedicationReport(medicationReport);
    }

    private void createEmptyMedicationSupplyOrder(Medication medication) {

        var supplyOrder = new MedicationSupplyOrder();
        supplyOrder.setOrganization(medication.getOrganization());
        supplyOrder.setLegacyId(0);
        supplyOrder.setLegacyTable(LEGACY_TABLE_MEDICATION_MANUAL);
        supplyOrder.setMedicationInformation(medication.getMedicationInformation());

        var author = new Author();
        author.setOrganization(medication.getOrganization());
        author.setLegacyId(0);
        author.setLegacyTable(LEGACY_TABLE_MEDICATION_MANUAL);

        var person = new Person();
        person.setOrganization(medication.getOrganization());
        person.setLegacyId(CareCoordinationConstants.LEGACY_ID_PREFIX);
        person.setLegacyTable(LEGACY_TABLE_MEDICATION_MANUAL);

        author.setPerson(person);

        var name = new Name();
        name.setLegacyId(CareCoordinationConstants.LEGACY_ID_PREFIX);
        name.setLegacyTable(LEGACY_TABLE_MEDICATION_MANUAL);
        name.setOrganization(medication.getOrganization());
        name.setPerson(person);

        person.setNames(List.of(name));

        supplyOrder.setAuthor(author);

        medication.setMedicationSupplyOrder(supplyOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medication> findHealthPartnersMedication(Long clientId, CcdCode rxNormCode,
                                                             String drugName, Integer refillNumber,
                                                             String prescriberFirstName,
                                                             String prescriberLastName,
                                                             String prescriberNpi) {
        return medicationDao.findFirst((root, q, cb) -> {
            var dispenseSub = q.subquery(Long.class);
            var medicationSubFrom = dispenseSub.from(Medication.class);

            dispenseSub.select(medicationSubFrom.get(Medication_.id));
            dispenseSub.where(
                    cb.equal(
                            medicationSubFrom
                                    .join(Medication_.medicationDispenses)
                                    .get(MedicationDispense_.fillNumber),
                            refillNumber
                    )
            );

            var withoutRefillNumber = root.get(Medication_.id).in(dispenseSub).not();

            var medInfo = root.join(Medication_.medicationInformation);
            Predicate byDrug;
            if (rxNormCode == null) {
                byDrug = cb.equal(medInfo.get(MedicationInformation_.productNameText), drugName);
            } else {
                var codePath = medInfo.join(MedicationInformation_.productNameCode);
                byDrug = cb.and(
                        cb.equal(codePath.get(CcdCode_.code), rxNormCode.getCode()),
                        cb.equal(codePath.get(CcdCode_.codeSystem), rxNormCode.getCodeSystem())
                );
            }

            var medProf = root.join(Medication_.medicationSupplyOrder).join(MedicationSupplyOrder_.medicalProfessional);
            var prescriberNameSub = q.subquery(Integer.class);
            var prescriberNameFrom = prescriberNameSub.from(Name.class);
            var byPrescriberName = prescriberNameSub.select(cb.literal(1))
                    .where(
                            nullableStringEqual(cb, prescriberNameFrom.get(Name_.given), prescriberFirstName),
                            nullableStringEqual(cb, prescriberNameFrom.get(Name_.family), prescriberLastName),
                            cb.equal(medProf.get(MedicalProfessional_.person), prescriberNameFrom.get(Name_.person))
                    );

            var byPrescriber = cb.and(
                    nullableStringEqual(cb, medProf.get(MedicalProfessional_.npi), prescriberNpi),
                    cb.exists(byPrescriberName)
            );

            return cb.and(
                    cb.equal(root.get(Medication_.clientId), clientId),
                    byDrug,
                    byPrescriber,
                    withoutRefillNumber
            );

        }, Medication.class, ORDER_BY_MEDICATION_START_DATE_DESC);
    }

    @Override
    @Transactional
    public void delete(Medication medication) {
        medicationDao.delete(medication);
    }

    private Predicate nullableStringEqual(CriteriaBuilder cb, Path<String> path, String string) {
        return string == null ?
                cb.isNull(path) :
                cb.equal(path, string);
    }
}
