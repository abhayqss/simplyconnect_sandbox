package com.scnsoft.eldermark.service.medispan.dto;

import java.util.List;

public class MediSpanPackagedDrug {

    public static final String RX_OTC_FIELD = "rxOTC";
    public static final String CONXCEPT_TYPE_FIELD = "conceptType";
    public static final String NAME_FIELD = "name";
    public static final String ID_FIELD = "id";
    public static final String PPID_FIELD = "ppid";
    public static final String RELATED_CONCEPT_SOURCE_FIELD = "relatedConceptSource";
    public static final String CLINICAL_DOSE_PACKAGED_SIZE_FIELD = "clinicalDosePackageSize";
    public static final String DEA_FIELD = "dea";
    public static final String DESI_FIELD = "desi";
    public static final String DIN_FIELD = "din";
    public static final String DISPENASABLE_DRUG_FIELD = "dispensableDrug";
    public static final String DRUG_NAME_TYPE_FIELD = "drugNameType";
    public static final String EQUIVALENT_PACKAGED_DRUGS_FIELD = "equivalentPackagedDrugs";
    public static final String GPPC_FIELD = "gppc";
    public static final String HRI_FIELD = "hri";
    public static final String INGREDIENT_SET_FIELD = "ingredientSet";
    public static final String INNER_PACKAGED_DRUGS_FIELD = "innerPackagedDrugs";
    public static final String IS_CLINIC_PACK_FIELD = "isClinicPack";
    public static final String IS_PACKAGED_FIELD = "isRepackaged";
    public static final String LABELER_FIELD = "labeler";
    public static final String LIMITED_DISTRIBUTION_FIELD = "limitedDistribution";
    public static final String MARKET_END_DATE_FIELD = "marketEndDate";
    public static final String MULTI_SOURCE_FIELD = "multiSource";
    public static final String NDC_FIELD = "ndc";
    public static final String OUTHER_PACKAGED_DRUG_FIELD = "outerPackagedDrug";
    public static final String QUANTITY_SOLD_RANKING_FIELD = "quantitySoldRanking";
    public static final String REVENUE_RANKING_FIELD = "revenueRanking";
    public static final String SCHEDULE_FIELD = "schedule";
    public static final String TEES_FIELD = "tees";
    public static final String UPC_FIELD = "upc";
    public static final String RELATED_CONCEPTS_FIELD = "relatedConcepts";

    private MediSpanIdField rxOTC;
    private String conceptType;
    private String name;
    private String id;
    private String ppid;
    private String relatedConceptSource;
    private String clinicalDosePackageSize;
    private MediSpanIdField dea;
    private MediSpanIdField desi;
    private String din;
    private MediSpanIdField dispensableDrug;
    private MediSpanIdField drugNameType;
    private List<MediSpanIdField> equivalentPackagedDrugs;
    private MediSpanIdField gppc;
    private String hri;
    private MediSpanIdField ingredientSet;
    private List<MediSpanIdField> innerPackagedDrugs;
    private String isClinicPack;
    private String isRepackaged;
    private MediSpanIdField labeler;
    private MediSpanIdField limitedDistribution;
    private String marketEndDate;
    private MediSpanIdField multiSource;
    private String ndc;
    private MediSpanIdField outerPackagedDrug;
    private MediSpanIdField quantitySoldRanking;
    private MediSpanIdField revenueRanking;
    private MediSpanIdField schedule;
    private List<MediSpanIdField> tees;
    private String upc;
    private Object relatedConcepts;

    public MediSpanIdField getRxOTC() {
        return rxOTC;
    }

    public void setRxOTC(MediSpanIdField rxOTC) {
        this.rxOTC = rxOTC;
    }

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

    public String getPpid() {
        return ppid;
    }

    public void setPpid(String ppid) {
        this.ppid = ppid;
    }

    public String getRelatedConceptSource() {
        return relatedConceptSource;
    }

    public void setRelatedConceptSource(String relatedConceptSource) {
        this.relatedConceptSource = relatedConceptSource;
    }

    public String getClinicalDosePackageSize() {
        return clinicalDosePackageSize;
    }

    public void setClinicalDosePackageSize(String clinicalDosePackageSize) {
        this.clinicalDosePackageSize = clinicalDosePackageSize;
    }

    public MediSpanIdField getDea() {
        return dea;
    }

    public void setDea(MediSpanIdField dea) {
        this.dea = dea;
    }

    public MediSpanIdField getDesi() {
        return desi;
    }

    public void setDesi(MediSpanIdField desi) {
        this.desi = desi;
    }

    public String getDin() {
        return din;
    }

    public void setDin(String din) {
        this.din = din;
    }

    public MediSpanIdField getDispensableDrug() {
        return dispensableDrug;
    }

    public void setDispensableDrug(MediSpanIdField dispensableDrug) {
        this.dispensableDrug = dispensableDrug;
    }

    public MediSpanIdField getDrugNameType() {
        return drugNameType;
    }

    public void setDrugNameType(MediSpanIdField drugNameType) {
        this.drugNameType = drugNameType;
    }

    public List<MediSpanIdField> getEquivalentPackagedDrugs() {
        return equivalentPackagedDrugs;
    }

    public void setEquivalentPackagedDrugs(List<MediSpanIdField> equivalentPackagedDrugs) {
        this.equivalentPackagedDrugs = equivalentPackagedDrugs;
    }

    public MediSpanIdField getGppc() {
        return gppc;
    }

    public void setGppc(MediSpanIdField gppc) {
        this.gppc = gppc;
    }

    public String getHri() {
        return hri;
    }

    public void setHri(String hri) {
        this.hri = hri;
    }

    public MediSpanIdField getIngredientSet() {
        return ingredientSet;
    }

    public void setIngredientSet(MediSpanIdField ingredientSet) {
        this.ingredientSet = ingredientSet;
    }

    public List<MediSpanIdField> getInnerPackagedDrugs() {
        return innerPackagedDrugs;
    }

    public void setInnerPackagedDrugs(List<MediSpanIdField> innerPackagedDrugs) {
        this.innerPackagedDrugs = innerPackagedDrugs;
    }

    public String getIsClinicPack() {
        return isClinicPack;
    }

    public void setIsClinicPack(String isClinicPack) {
        this.isClinicPack = isClinicPack;
    }

    public String getIsRepackaged() {
        return isRepackaged;
    }

    public void setIsRepackaged(String isRepackaged) {
        this.isRepackaged = isRepackaged;
    }

    public MediSpanIdField getLabeler() {
        return labeler;
    }

    public void setLabeler(MediSpanIdField labeler) {
        this.labeler = labeler;
    }

    public MediSpanIdField getLimitedDistribution() {
        return limitedDistribution;
    }

    public void setLimitedDistribution(MediSpanIdField limitedDistribution) {
        this.limitedDistribution = limitedDistribution;
    }

    public String getMarketEndDate() {
        return marketEndDate;
    }

    public void setMarketEndDate(String marketEndDate) {
        this.marketEndDate = marketEndDate;
    }

    public MediSpanIdField getMultiSource() {
        return multiSource;
    }

    public void setMultiSource(MediSpanIdField multiSource) {
        this.multiSource = multiSource;
    }

    public String getNdc() {
        return ndc;
    }

    public void setNdc(String ndc) {
        this.ndc = ndc;
    }

    public MediSpanIdField getOuterPackagedDrug() {
        return outerPackagedDrug;
    }

    public void setOuterPackagedDrug(MediSpanIdField outerPackagedDrug) {
        this.outerPackagedDrug = outerPackagedDrug;
    }

    public MediSpanIdField getQuantitySoldRanking() {
        return quantitySoldRanking;
    }

    public void setQuantitySoldRanking(MediSpanIdField quantitySoldRanking) {
        this.quantitySoldRanking = quantitySoldRanking;
    }

    public MediSpanIdField getRevenueRanking() {
        return revenueRanking;
    }

    public void setRevenueRanking(MediSpanIdField revenueRanking) {
        this.revenueRanking = revenueRanking;
    }

    public MediSpanIdField getSchedule() {
        return schedule;
    }

    public void setSchedule(MediSpanIdField schedule) {
        this.schedule = schedule;
    }

    public List<MediSpanIdField> getTees() {
        return tees;
    }

    public void setTees(List<MediSpanIdField> tees) {
        this.tees = tees;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public Object getRelatedConcepts() {
        return relatedConcepts;
    }

    public void setRelatedConcepts(Object relatedConcepts) {
        this.relatedConcepts = relatedConcepts;
    }
}
