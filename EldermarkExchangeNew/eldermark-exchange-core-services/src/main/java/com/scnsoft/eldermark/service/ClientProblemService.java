package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientProblemCount;
import com.scnsoft.eldermark.beans.ClientProblemFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientProblemSecurityAwareEntity;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientProblemService extends SecurityAwareEntityService<ClientProblemSecurityAwareEntity, Long>,
        ProjectingService<Long> {

    Page<ClientProblem> find(ClientProblemFilter filter, PermissionFilter permissionFilter, Pageable pageRequest);

    List<ClientProblem> find(ClientProblemFilter filter, PermissionFilter permissionFilter);

    <P> List<P> find(ClientProblemFilter filter, PermissionFilter permissionFilter, Class<P> projection);

    ClientProblem findById(Long problemId);

    List<ClientProblemCount> countGroupedByStatus(ClientProblemFilter filter, PermissionFilter permissionFilter);
}
