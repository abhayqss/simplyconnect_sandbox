package com.scnsoft.eldermark.dto.assessment;

import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.dto.TypeDto;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;

public class ClientAssessmentListItemDto extends BaseAssessmentListItemDto {
    @EntitySort.List(
            {
                    @EntitySort(joined = {ClientAssessmentResult_.EMPLOYEE, Employee_.FIRST_NAME}),
                    @EntitySort(joined = {ClientAssessmentResult_.EMPLOYEE, Employee_.LAST_NAME})
            }
    )
    private String author;

    @EntitySort(ClientAssessmentResult_.ASSESSMENT_STATUS)
    private TypeDto status;

    private String score;

    private boolean canEdit;

    private boolean exportable;
    private boolean canHide;
    private boolean canRestore;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean getExportable() {
        return exportable;
    }

    public void setExportable(boolean exportable) {
        this.exportable = exportable;
    }

    @Override
    public TypeDto getStatus() {
        return status;
    }

    @Override
    public void setStatus(TypeDto status) {
        this.status = status;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public boolean isCanHide() {
        return canHide;
    }

    public void setCanHide(boolean canHide) {
        this.canHide = canHide;
    }

    public boolean isCanRestore() {
        return canRestore;
    }

    public void setCanRestore(boolean canRestore) {
        this.canRestore = canRestore;
    }
}
