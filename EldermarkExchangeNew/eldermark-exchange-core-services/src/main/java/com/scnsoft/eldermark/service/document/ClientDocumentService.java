package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.beans.DocumentCount;
import com.scnsoft.eldermark.beans.InternalClientDocumentFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientDocumentSecurityAwareEntity;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.document.DocumentFieldsAware;
import com.scnsoft.eldermark.service.ProjectingService;
import com.scnsoft.eldermark.service.SecurityAwareEntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ClientDocumentService extends SecurityAwareEntityService<ClientDocumentSecurityAwareEntity, Long>, DocumentService {

    ClientDocument findById(long id);

    <P> P findById(Long id, Class<P> projectionClass);

    List<ClientDocument> findAllByIds(Collection<Long> ids);

    Page<ClientDocument> find(InternalClientDocumentFilter documentFilter, PermissionFilter permissionFilter, Pageable pageRequest);

    List<DocumentCount> countGroupedBySignatureStatus(
            InternalClientDocumentFilter documentFilter,
            PermissionFilter permissionFilter
    );

    List<ClientDocument> updateIfCda(List<ClientDocument> documents);

    ClientDocument updateIfCda(ClientDocument document);

    <T extends DocumentFieldsAware> boolean defineIsCdaDocument(T document);

    Long count(InternalClientDocumentFilter documentFilter, PermissionFilter permissionFilter);

    Optional<Instant> findOldestDate(InternalClientDocumentFilter documentFilter, PermissionFilter permissionFilter);

    <P> List<P> findDocumentShouldBeSignedByEmployeeId(Long employeeId, Collection<Long> associatedClientIds,
                                                       PermissionFilter permissionFilter,
                                                       Class<P> projectionClass, Sort sort, int limit);
}
