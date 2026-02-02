package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.note.ClientProgramNote;
import com.scnsoft.eldermark.entity.note.ClientProgramNoteAware;
import com.scnsoft.eldermark.service.basic.AuditableEntityService;

import java.util.List;

public interface ClientProgramNoteService extends AuditableEntityService<ClientProgramNote> {

    List<ClientProgramNoteAware> findAll(InternalReportFilter filter, PermissionFilter permissionFilter);
}
