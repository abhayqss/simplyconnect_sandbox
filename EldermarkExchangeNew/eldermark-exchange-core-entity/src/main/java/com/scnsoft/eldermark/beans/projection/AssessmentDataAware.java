package com.scnsoft.eldermark.beans.projection;

import java.time.Instant;

public interface AssessmentDataAware extends ClientIdNamesAware, EmployeeNamesAware, ClientCommunityIdNameAware {
    String getAssessmentShortName();
    String getResult();
    Instant getDateCompleted();
    Instant getLastModifiedDate();
}