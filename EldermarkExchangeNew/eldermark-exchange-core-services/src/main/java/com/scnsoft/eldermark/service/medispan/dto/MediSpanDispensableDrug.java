package com.scnsoft.eldermark.service.medispan.dto;

import java.util.List;

public class MediSpanDispensableDrug {

    public static final String CONCEPT_TYPE_FIELD = "conceptType";
    public static final String NAME_FIELD = "name";
    public static final String ID_FIELD = "id";
    public static final String MEDI_SPAN_ID_FIELD = "mediSpanId";
    public static final String BIOEQUIVALENCE_FIELD = "bioequivalence";
    public static final String DISPENSABLE_AHFSS_dispensableAHFSs_FIELD = "dispensableAHFSs";
    public static final String DISPENSABLE_DEA_dispensableDEA_FIELD = "dispensableDEA";
    public static final String DISPENSABLE_DESI_FIELD = "dispensableDESI";
    public static final String DISPENSABLE_DRUG_NAME_TYPE_FIELD = "dispensableDrugNameType";
    public static final String DISPENSABLE_GENERIC_PRODUCT_FIELD = "dispensableGenericProduct";
    public static final String DISPENSABLE_INGREDIENT_SET_FIELD = "dispensableIngredientSet";
    public static final String DISPENSABLE_MULTI_SOURCE_FIELD = "dispensableMultiSource";
    public static final String DISPENSABLE_RX_OTC_FIELD = "dispensableRxOTC";
    public static final String DISPENSABLE_SCHEDULE_FIELD = "dispensableSchedule";
    public static final String DISPENSABLE_TEES_FIELD = "dispensableTEEs";
    public static final String DOSE_FORM_FIELD = "doseForm";
    public static final String DRUG_NAME_SOURCE_FIELD = "drugNameSource";
    public static final String EQUIVALENT_DISPENSABLE_DRUGS_FIELD = "equivalentDispensableDrugs";
    public static final String IS_MEDICAL_DEVICE_FIELD = "isMedicalDevice";
    public static final String MARKET_END_DATE_FIELD = "marketEndDate";
    public static final String OBSOLETE_DATE_FIELD = "obsoleteDate";
    public static final String PACKAGED_DRUGS_FIELD = "packagedDrugs";
    public static final String ROUTED_DRUG_FIELD = "routedDrug";
    public static final String STRENGTH_FIELD = "strength";
    public static final String RELATED_CONCEPTS_FIELD = "relatedConcepts";

    private String conceptType;
    private String name;
    private String id;
    private String mediSpanId;
    private MediSpanIdField bioequivalence;
    private List<MediSpanIdField> dispensableAHFSs;
    private MediSpanIdField dispensableDEA;
    private MediSpanIdField dispensableDESI;
    private MediSpanIdField dispensableDrugNameType;
    private MediSpanIdField dispensableGenericProduct;
    private MediSpanIdField dispensableIngredientSet;
    private MediSpanIdField dispensableMultiSource;
    private MediSpanIdField dispensableRxOTC;
    private MediSpanIdField dispensableSchedule;
    private List<MediSpanIdField> dispensableTEEs;
    private MediSpanIdField doseForm;
    private MediSpanIdField drugNameSource;
    private List<MediSpanIdField> equivalentDispensableDrugs;
    private String isMedicalDevice;
    private String marketEndDate;
    private String obsoleteDate;
    private List<MediSpanIdField> packagedDrugs;
    private MediSpanIdField routedDrug;
    private String strength;
    private Object relatedConcepts;

    public String getConceptType() {
        return conceptType;
    }

    public void setConceptType(String conceptType) {
        this.conceptType = conceptType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediSpanId() {
        return mediSpanId;
    }

    public void setMediSpanId(String mediSpanId) {
        this.mediSpanId = mediSpanId;
    }

    public MediSpanIdField getBioequivalence() {
        return bioequivalence;
    }

    public void setBioequivalence(MediSpanIdField bioequivalence) {
        this.bioequivalence = bioequivalence;
    }

    public List<MediSpanIdField> getDispensableAHFSs() {
        return dispensableAHFSs;
    }

    public void setDispensableAHFSs(List<MediSpanIdField> dispensableAHFSs) {
        this.dispensableAHFSs = dispensableAHFSs;
    }

    public MediSpanIdField getDispensableDEA() {
        return dispensableDEA;
    }

    public void setDispensableDEA(MediSpanIdField dispensableDEA) {
        this.dispensableDEA = dispensableDEA;
    }

    public MediSpanIdField getDispensableDESI() {
        return dispensableDESI;
    }

    public void setDispensableDESI(MediSpanIdField dispensableDESI) {
        this.dispensableDESI = dispensableDESI;
    }

    public MediSpanIdField getDispensableDrugNameType() {
        return dispensableDrugNameType;
    }

    public void setDispensableDrugNameType(MediSpanIdField dispensableDrugNameType) {
        this.dispensableDrugNameType = dispensableDrugNameType;
    }

    public MediSpanIdField getDispensableGenericProduct() {
        return dispensableGenericProduct;
    }

    public void setDispensableGenericProduct(MediSpanIdField dispensableGenericProduct) {
        this.dispensableGenericProduct = dispensableGenericProduct;
    }

    public MediSpanIdField getDispensableIngredientSet() {
        return dispensableIngredientSet;
    }

    public void setDispensableIngredientSet(MediSpanIdField dispensableIngredientSet) {
        this.dispensableIngredientSet = dispensableIngredientSet;
    }

    public MediSpanIdField getDispensableMultiSource() {
        return dispensableMultiSource;
    }

    public void setDispensableMultiSource(MediSpanIdField dispensableMultiSource) {
        this.dispensableMultiSource = dispensableMultiSource;
    }

    public MediSpanIdField getDispensableRxOTC() {
        return dispensableRxOTC;
    }

    public void setDispensableRxOTC(MediSpanIdField dispensableRxOTC) {
        this.dispensableRxOTC = dispensableRxOTC;
    }

    public MediSpanIdField getDispensableSchedule() {
        return dispensableSchedule;
    }

    public void setDispensableSchedule(MediSpanIdField dispensableSchedule) {
        this.dispensableSchedule = dispensableSchedule;
    }

    public List<MediSpanIdField> getDispensableTEEs() {
        return dispensableTEEs;
    }

    public void setDispensableTEEs(List<MediSpanIdField> dispensableTEEs) {
        this.dispensableTEEs = dispensableTEEs;
    }

    public MediSpanIdField getDoseForm() {
        return doseForm;
    }

    public void setDoseForm(MediSpanIdField doseForm) {
        this.doseForm = doseForm;
    }

    public MediSpanIdField getDrugNameSource() {
        return drugNameSource;
    }

    public void setDrugNameSource(MediSpanIdField drugNameSource) {
        this.drugNameSource = drugNameSource;
    }

    public List<MediSpanIdField> getEquivalentDispensableDrugs() {
        return equivalentDispensableDrugs;
    }

    public void setEquivalentDispensableDrugs(List<MediSpanIdField> equivalentDispensableDrugs) {
        this.equivalentDispensableDrugs = equivalentDispensableDrugs;
    }

    public String getIsMedicalDevice() {
        return isMedicalDevice;
    }

    public void setIsMedicalDevice(String isMedicalDevice) {
        this.isMedicalDevice = isMedicalDevice;
    }

    public String getMarketEndDate() {
        return marketEndDate;
    }

    public void setMarketEndDate(String marketEndDate) {
        this.marketEndDate = marketEndDate;
    }

    public String getObsoleteDate() {
        return obsoleteDate;
    }

    public void setObsoleteDate(String obsoleteDate) {
        this.obsoleteDate = obsoleteDate;
    }

    public List<MediSpanIdField> getPackagedDrugs() {
        return packagedDrugs;
    }

    public void setPackagedDrugs(List<MediSpanIdField> packagedDrugs) {
        this.packagedDrugs = packagedDrugs;
    }

    public MediSpanIdField getRoutedDrug() {
        return routedDrug;
    }

    public void setRoutedDrug(MediSpanIdField routedDrug) {
        this.routedDrug = routedDrug;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public Object getRelatedConcepts() {
        return relatedConcepts;
    }

    public void setRelatedConcepts(Object relatedConcepts) {
        this.relatedConcepts = relatedConcepts;
    }
}
