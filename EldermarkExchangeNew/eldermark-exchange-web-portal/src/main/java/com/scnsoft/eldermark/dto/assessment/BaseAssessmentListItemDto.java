package com.scnsoft.eldermark.dto.assessment;

import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.dto.TypeDto;
import com.scnsoft.eldermark.entity.assessment.Assessment_;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;

public class BaseAssessmentListItemDto {
    private Long id;

    private Long typeId;
    private String typeName;

    @EntitySort(joined = {ClientAssessmentResult_.ASSESSMENT, Assessment_.NAME})
    private String typeTitle;
    private String typeShortTitle;

    @EntitySort(ClientAssessmentResult_.ASSESSMENT_STATUS)
    private TypeDto status;

    @EntitySort(ClientAssessmentResult_.DATE_STARTED)
    private Long dateStarted;

    @EntitySort(ClientAssessmentResult_.DATE_COMPLETED)
    private Long dateCompleted;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeDto getStatus() {
        return status;
    }

    public void setStatus(TypeDto status) {
        this.status = status;
    }

    public Long getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Long dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Long getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Long dateCompleted) {
        this.dateCompleted = dateCompleted;
    }



    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeShortTitle() {
        return typeShortTitle;
    }

    public void setTypeShortTitle(String typeShortTitle) {
        this.typeShortTitle = typeShortTitle;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

}
