package com.scnsoft.eldermark.service.security;

public interface IncidentReportSecurityService {

    boolean canViewList();

    boolean hasAccessByEventId(Long eventId);

    boolean canView(Long id);

    boolean canViewByClient(Long clientId);

    boolean canDelete(Long id);

    boolean canViewIncidentPicture(Long pictureId);

}
