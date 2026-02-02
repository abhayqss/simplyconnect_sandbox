package com.scnsoft.eldermark.dao.ccd;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.shared.ccd.CcdSectionDto;
import com.scnsoft.eldermark.shared.ccd.CountDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GenericCcdSectionDaoImpl<T extends CcdSectionDto> implements GenericCcdSectionDao<T> {
    private EntityManager entityManager;
    private ResidentDao residentDao;
    private Class<T> ccdSectionDtoClazz;

    public GenericCcdSectionDaoImpl(Class<T> classToSet, EntityManager entityManager, ResidentDao residentDao) {
        this.ccdSectionDtoClazz = classToSet;
        this.entityManager = entityManager;
        this.residentDao = residentDao;
    }

    @Override
    public List<T> getSectionDto(Long residentId, Pageable pageable, Boolean aggregated) {
        if (residentId == null || pageable == null)
            throw new IllegalArgumentException();

        NamedNativeQueries nativeQueries = ccdSectionDtoClazz.getAnnotation(NamedNativeQueries.class);

        String storedProcedure = null;
        for (NamedNativeQuery nativeQuery : nativeQueries.value()) {
            if (nativeQuery.resultClass() == ccdSectionDtoClazz) {
                storedProcedure = nativeQuery.name();
            }
        }

        TypedQuery<T> query = entityManager.createNamedQuery(storedProcedure, ccdSectionDtoClazz);

        query.setParameter("residentId", residentId);
        query.setParameter("offset", pageable.getOffset());
        query.setParameter("limit", pageable.getPageSize());
        query.setParameter("sortBy", null);
        query.setParameter("sortDir", null);
        query.setParameter("aggregated", aggregated);

        Sort sort = pageable.getSort();
        if (sort != null) {
            Map<String, String> fieldToColumnMapping = new HashMap<String, String>();
            for (Field field : ccdSectionDtoClazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    fieldToColumnMapping.put(field.getName(), column.name());
                }
            }

            if (sort.iterator().hasNext() && !fieldToColumnMapping.isEmpty()) {
                Sort.Order order = sort.iterator().next();
                query.setParameter("sortBy", fieldToColumnMapping.get(order.getProperty()));
                query.setParameter("sortDir", order.isAscending() ? "ASC" : "DESC");
            }
        }

        List<T> resultList = query.getResultList();

        for (T result : resultList) {
            final Long recordResidentId = result.getResidentId();
            final Resident resident = residentDao.get(recordResidentId);
            final Database dataSource = resident.getDatabase();
            final Organization community = resident.getFacility();
            if (dataSource != null) {
                result.setDataSource(dataSource.getName());
                result.setDataSourceOid(dataSource.getOid());
            }
            if (community != null) {
                result.setCommunity(community.getName());
                result.setCommunityOid(community.getOid());
            }

            //todo merged residents
            result.setEditable(result.getManual() && CcdSecurityUtils.canEditCcd(SecurityUtils.getAuthenticatedUser(),
                    SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds(),
                    Collections.singletonList(resident)));
            result.setDeletable(result.getManual() && CcdSecurityUtils.canDeleteCcd(SecurityUtils.getAuthenticatedUser(),
                    SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds(),
                    Collections.singletonList(resident)));
            result.setViewable(CcdSecurityUtils.canViewCcd(SecurityUtils.getAuthenticatedUser(),
                    SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds(),
                    Collections.singletonList(resident)));

        }

        return resultList;
    }

    @Override
    public long getSectionDtoCount(Long residentId, Boolean aggregated) {
        if (residentId == null)
            throw new IllegalArgumentException();

        NamedNativeQueries nativeQueries = ccdSectionDtoClazz.getAnnotation(NamedNativeQueries.class);

        String storedProcedure = null;
        for (NamedNativeQuery nativeQuery : nativeQueries.value()) {
            if (nativeQuery.resultClass() == CountDto.class) {
                storedProcedure = nativeQuery.name();
            }
        }

        TypedQuery<CountDto> query = entityManager.createNamedQuery(storedProcedure, CountDto.class);

        query.setParameter("residentId", residentId);
        query.setParameter("aggregated", aggregated);

        return query.getSingleResult().longValue();
    }
}
