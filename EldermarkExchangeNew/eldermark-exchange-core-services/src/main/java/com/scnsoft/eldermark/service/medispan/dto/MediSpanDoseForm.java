package com.scnsoft.eldermark.service.medispan.dto;

public class MediSpanDoseForm {

    public final static String CONCEPT_TYPE_FIELD = "conceptType";
    public final static String NAME_FIELD = "name";
    public final static String ID_FIELD = "id";
    public final static String MEDI_SPAN_ID_FIELD = "mediSpanId";
    public final static String RELATED_CONCEPT_SOURCE_FIELD = "relatedConceptSource";
    public final static String RELATED_CONCEPTS_FIELD = "relatedConcepts";

    private String conceptType;
    private String name;
    private String id;
    private String mediSpanId;
    private String relatedConceptSource;
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

    public Object getRelatedConcepts() {
        return relatedConcepts;
    }

    public void setRelatedConcepts(Object relatedConcepts) {
        this.relatedConcepts = relatedConcepts;
    }
}
