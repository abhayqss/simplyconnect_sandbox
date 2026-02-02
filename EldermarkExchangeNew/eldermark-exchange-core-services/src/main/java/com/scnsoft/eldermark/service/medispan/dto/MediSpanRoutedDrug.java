package com.scnsoft.eldermark.service.medispan.dto;

import java.util.List;

public class MediSpanRoutedDrug {

    public static final String CONCEPT_TYPE_FIELD = "conceptType";
    public static final String NAME_FIELD = "name";
    public static final String ID_FIELD = "id";
    public static final String MEDI_SPAN_ID_FIELD = "mediSpanId";
    public static final String RELATED_CONCEPT_SOURCE_FIELD = "relatedConceptSource";
    public static final String OBSOLETE_DATE_FIELD = "obsoleteDate";
    public static final String MARKET_END_FIELD = "marketEndDate";
    public static final String DISPENSABLE_AHFSS_FIELD = "dispensableAHFSs";
    public static final String DISPENSABLE_DRUGS_FIELD = "dispensableDrugs";
    public static final String EQUIVALENT_ROUTED_DRUGS_FIELD = "equivalentRoutedDrugs";
    public static final String DRUG_NAME_FIELD = "drugName";
    public static final String ROUTE_FIELD = "route";
    public static final String RELATED_CONCEPTS_FIELD = "relatedConcepts";

    private String conceptType;
    private String name;
    private String id;
    private String mediSpanId;
    private String relatedConceptSource;
    private String obsoleteDate;
    private String marketEndDate;
    private List<MediSpanIdField> dispensableAHFSs;
    private List<MediSpanIdField> dispensableDrugs;
    private List<MediSpanIdField> equivalentRoutedDrugs;
    private MediSpanIdField drugName;
    private MediSpanIdField route;
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

    public String getRelatedConceptSource() {
        return relatedConceptSource;
    }

    public void setRelatedConceptSource(String relatedConceptSource) {
        this.relatedConceptSource = relatedConceptSource;
    }

    public String getObsoleteDate() {
        return obsoleteDate;
    }

    public void setObsoleteDate(String obsoleteDate) {
        this.obsoleteDate = obsoleteDate;
    }

    public String getMarketEndDate() {
        return marketEndDate;
    }

    public void setMarketEndDate(String marketEndDate) {
        this.marketEndDate = marketEndDate;
    }

    public List<MediSpanIdField> getDispensableAHFSs() {
        return dispensableAHFSs;
    }

    public void setDispensableAHFSs(List<MediSpanIdField> dispensableAHFSs) {
        this.dispensableAHFSs = dispensableAHFSs;
    }

    public List<MediSpanIdField> getDispensableDrugs() {
        return dispensableDrugs;
    }

    public void setDispensableDrugs(List<MediSpanIdField> dispensableDrugs) {
        this.dispensableDrugs = dispensableDrugs;
    }

    public List<MediSpanIdField> getEquivalentRoutedDrugs() {
        return equivalentRoutedDrugs;
    }

    public void setEquivalentRoutedDrugs(List<MediSpanIdField> equivalentRoutedDrugs) {
        this.equivalentRoutedDrugs = equivalentRoutedDrugs;
    }

    public MediSpanIdField getDrugName() {
        return drugName;
    }

    public void setDrugName(MediSpanIdField drugName) {
        this.drugName = drugName;
    }

    public MediSpanIdField getRoute() {
        return route;
    }

    public void setRoute(MediSpanIdField route) {
        this.route = route;
    }

    public Object getRelatedConcepts() {
        return relatedConcepts;
    }

    public void setRelatedConcepts(Object relatedConcepts) {
        this.relatedConcepts = relatedConcepts;
    }
}
