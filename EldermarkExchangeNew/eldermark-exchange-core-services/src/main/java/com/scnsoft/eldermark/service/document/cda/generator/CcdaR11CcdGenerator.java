package com.scnsoft.eldermark.service.document.cda.generator;

import com.scnsoft.eldermark.cda.service.schema.CdaDocumentType;
import com.scnsoft.eldermark.service.document.cda.CdaGenerator;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.service.document.templates.consol.factory.sections.*;
import org.apache.commons.collections4.CollectionUtils;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ContinuityOfCareDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CcdaR11CcdGenerator implements CdaGenerator<ContinuityOfCareDocument> {

    @Autowired
    private AdvanceDirectiveFactory advanceDirectiveFactory;
    @Autowired
    private AllergiesFactory allergiesFactory;
    @Autowired
    private EncountersFactory encountersFactory;
    @Autowired
    private FamilyHistoryFactory familyHistoryFactory;
    @Autowired
    private FunctionalStatusFactory functionalStatusFactory;
    @Autowired
    private ImmunizationsFactory immunizationsFactory;
    @Autowired
    private MedicalEquipmentFactory medicalEquipmentFactory;
    @Autowired
    private MedicationsFactory medicationsFactory;
    @Autowired
    private PayerFactory payerFactory;
    @Autowired
    private PlanOfCareFactory planOfCareFactory;
    @Autowired
    private ProblemsFactory problemsFactory;
    @Autowired
    private ProceduresFactory proceduresFactory;
    @Autowired
    private ResultsFactory resultsFactory;
    @Autowired
    private SocialHistoryFactory socialHistoryFactory;
    @Autowired
    private VitalSignFactory vitalSignFactory;

    @Autowired
    private CcdConstructionUtils ccdConstructionUtils;

    @Override
    public ContinuityOfCareDocument generate(ClinicalDocumentVO srcDocument) {
        final ContinuityOfCareDocument document = ConsolFactory.eINSTANCE.createContinuityOfCareDocument().init();

        // header sections
        ccdConstructionUtils.constructGeneralCCDAHeader(document, srcDocument);
        applyHeaderConstraints(document);

        // body sections
        addSection(document, advanceDirectiveFactory, srcDocument.getAdvanceDirectives());
        addSection(document, allergiesFactory, srcDocument.getAllergies());
        addSection(document, encountersFactory, srcDocument.getEncounters());
        addSection(document, familyHistoryFactory, srcDocument.getFamilyHistories());
        addSection(document, functionalStatusFactory, srcDocument.getFunctionalStatuses()); //todo disable?
        addSection(document, immunizationsFactory, srcDocument.getImmunizations());
        addSection(document, medicalEquipmentFactory, srcDocument.getMedicalEquipments());
        addSection(document, medicationsFactory, srcDocument.getMedications());
        addSection(document, payerFactory, srcDocument.getPayers());
        addSection(document, planOfCareFactory, srcDocument.getPlanOfCares());
        addSection(document, problemsFactory, srcDocument.getProblems());
        addSection(document, proceduresFactory, srcDocument.getProcedures());
        addSection(document, resultsFactory, srcDocument.getResults());
        addSection(document, socialHistoryFactory, srcDocument.getSocialHistories());
        addSection(document, vitalSignFactory, srcDocument.getVitalSigns());

        return document;
    }

    private void applyHeaderConstraints(ContinuityOfCareDocument document) {

        if (CollectionUtils.isNotEmpty(document.getDocumentationOfs())) {
            var firstDocumentationOf = document.getDocumentationOfs().get(0);
            document.getDocumentationOfs().clear();
            document.getDocumentationOfs().add(firstDocumentationOf);
        }
    }

    @Override
    public CdaDocumentType getGeneratedType() {
        return CdaDocumentType.CCDA_R1_1_CCD_V1;
    }

    public void setAdvanceDirectiveFactory(AdvanceDirectiveFactory advanceDirectiveFactory) {
        this.advanceDirectiveFactory = advanceDirectiveFactory;
    }

    public void setAllergiesFactory(AllergiesFactory allergiesFactory) {
        this.allergiesFactory = allergiesFactory;
    }

    public void setEncountersFactory(EncountersFactory encountersFactory) {
        this.encountersFactory = encountersFactory;
    }

    public void setFamilyHistoryFactory(FamilyHistoryFactory familyHistoryFactory) {
        this.familyHistoryFactory = familyHistoryFactory;
    }

    public void setFunctionalStatusFactory(FunctionalStatusFactory functionalStatusFactory) {
        this.functionalStatusFactory = functionalStatusFactory;
    }

    public void setImmunizationsFactory(ImmunizationsFactory immunizationsFactory) {
        this.immunizationsFactory = immunizationsFactory;
    }

    public void setMedicalEquipmentFactory(MedicalEquipmentFactory medicalEquipmentFactory) {
        this.medicalEquipmentFactory = medicalEquipmentFactory;
    }

    public void setMedicationsFactory(MedicationsFactory medicationsFactory) {
        this.medicationsFactory = medicationsFactory;
    }

    public void setPayerFactory(PayerFactory payerFactory) {
        this.payerFactory = payerFactory;
    }

    public void setPlanOfCareFactory(PlanOfCareFactory planOfCareFactory) {
        this.planOfCareFactory = planOfCareFactory;
    }

    public void setProblemsFactory(ProblemsFactory problemsFactory) {
        this.problemsFactory = problemsFactory;
    }

    public void setProceduresFactory(ProceduresFactory proceduresFactory) {
        this.proceduresFactory = proceduresFactory;
    }

    public void setResultsFactory(ResultsFactory resultsFactory) {
        this.resultsFactory = resultsFactory;
    }

    public void setSocialHistoryFactory(SocialHistoryFactory socialHistoryFactory) {
        this.socialHistoryFactory = socialHistoryFactory;
    }

    public void setVitalSignFactory(VitalSignFactory vitalSignFactory) {
        this.vitalSignFactory = vitalSignFactory;
    }

    public void setCcdConstructionUtils(CcdConstructionUtils ccdConstructionUtils) {
        this.ccdConstructionUtils = ccdConstructionUtils;
    }
}