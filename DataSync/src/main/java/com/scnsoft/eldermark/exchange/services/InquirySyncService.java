package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.InquiryTargetDao;
import com.scnsoft.eldermark.exchange.fk.InquiryForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.InquiryData;
import com.scnsoft.eldermark.exchange.model.target.Inquiry;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.EmployeeIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.InquiryIdResolver;
import com.scnsoft.eldermark.exchange.services.employees.EmployeeSyncService;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InquirySyncService extends StandardSyncService<InquiryData, Long, InquiryForeignKeys> {
    @Autowired
    @Qualifier("inquirySourceDao")
    private StandardSourceDao<InquiryData, Long> sourceDao;

    @Autowired
    private InquiryTargetDao inquiryDao;

    @Value("${inquiry.idmapping.cache.size}")
    private int idMappingSizeLimit;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        dependencies.add(EmployeeSyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<InquiryData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(InquiryData.TABLE_NAME, InquiryData.INQUIRY_ID, InquiryData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return inquiryDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<InquiryForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                     InquiryData entity) {
        DatabaseInfo database = syncContext.getDatabase();
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(InquiryData.TABLE_NAME, entity.getId());
        InquiryForeignKeys foreignKeys = new InquiryForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        String facility = entity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setFacilityOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        String employeeLegacyId = entity.getSalesRepEmployeeId();
        if (!Utils.isEmpty(employeeLegacyId)) {
            try {
                foreignKeys.setSalesRepEmployeeId(employeeIdResolver.getId(employeeLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, employeeLegacyId));
            }
        }
        return new FKResolveResult<InquiryForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<InquiryData> sourceEntities,
                                       Map<InquiryData, InquiryForeignKeys> foreignKeysMap) {
        List<Inquiry> inquiries = new ArrayList<Inquiry>();
        for (InquiryData sourceEntity : sourceEntities) {
            InquiryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            Inquiry inquiry = new Inquiry();
            inquiry.setLegacyId(sourceEntity.getId());
            inquiry.setDatabaseId(syncContext.getDatabaseId());
            inquiry.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            inquiries.add(inquiry);
        }
        inquiryDao.insert(inquiries);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<InquiryData> sourceEntities,
                                    Map<InquiryData, InquiryForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
        for (InquiryData sourceEntity : sourceEntities) {
            long legacyId = sourceEntity.getId();
            long id = idMapping.getNewIdOrThrowException(legacyId);
            InquiryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            Inquiry.Updatable update = createUpdatable(sourceEntity, foreignKeys);
            inquiryDao.update(update, id);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        inquiryDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<Long> idMapping = inquiryDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
        context.putSharedObject(InquiryIdResolver.class, new InquiryIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = inquiryDao.getId(database, legacyId);
                }
                return newId;
            }
        });
    }

    private Inquiry.Updatable createUpdatable(InquiryData sourceEntity, InquiryForeignKeys foreignKeys) {
        Inquiry.Updatable updatable = new Inquiry.Updatable();
        updatable.setDate(sourceEntity.getDate());
        updatable.setFirstName(sourceEntity.getFirstName());
        updatable.setLastName(sourceEntity.getLastName());
        updatable.setProspect(sourceEntity.getProspect());
        updatable.setRelatedParty(sourceEntity.getRelatedParty());
        updatable.setNoLongerActive(sourceEntity.getNoLongerActive());
        updatable.setConverted(sourceEntity.getConverted());
        updatable.setReasonNoLongerActive(sourceEntity.getReasonNoLongerActive());
        updatable.setPhones(sourceEntity.getPhones());
        updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
        updatable.setSalesRepEmployeeId(foreignKeys.getSalesRepEmployeeId());
        return updatable;
    }

}
