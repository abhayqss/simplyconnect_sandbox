package com.scnsoft.eldermark.service.medispan.dto;

public class MediSpanRoute {

    public static final String CONCEPT_TYPE_FIELD = "conceptType";
    public static final String NAME_FIELD = "name";
    public static final String ID_FIELD = "id";
    public static final String DOSING_ROUTE_ID_FIELD = "dosingRouteId";
    public static final String RELATED_CONCEPT_SOURCE_FIELD = "relatedConceptSource";
    public static final String ROUTE_ID_FIELD = "routeId";
    public static final String RELATED_CONCEPTS_FIELD = "relatedConcepts";

    private String conceptType;
    private String name;
    private String id;
    private String dosingRouteId;
    private String relatedConceptSource;
    private String routeId;
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

    public String getDosingRouteId() {
        return dosingRouteId;
    }

    public void setDosingRouteId(String dosingRouteId) {
        this.dosingRouteId = dosingRouteId;
    }

    public String getRelatedConceptSource() {
        return relatedConceptSource;
    }

    public void setRelatedConceptSource(String relatedConceptSource) {
        this.relatedConceptSource = relatedConceptSource;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Object getRelatedConcepts() {
        return relatedConcepts;
    }

    public void setRelatedConcepts(Object relatedConcepts) {
        this.relatedConcepts = relatedConcepts;
    }
}
