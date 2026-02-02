package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.CommunicationDao;
import com.scnsoft.eldermark.exchange.fk.CommunicationForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.*;
import com.scnsoft.eldermark.exchange.model.target.Communication;
import com.scnsoft.eldermark.exchange.resolvers.*;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommunicationSyncService extends
        StandardSyncService<CommunicationData, Long, CommunicationForeignKeys> {
    @Autowired
    @Qualifier("communicationSourceDao")
    private StandardSourceDao<CommunicationData, Long> sourceDao;

    @Autowired
    private CommunicationDao communicationDao;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(ProfessionalContactSyncService.class);
        dependencies.add(EmployeeSyncService.class);
        dependencies.add(InquirySyncService.class);
        dependencies.add(CommunicationTypeSyncService.class);
        dependencies.add(ProspectSyncService.class);
        dependencies.add(CompanySyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<CommunicationData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(CommunicationData.TABLE_NAME, CommunicationData.COMMUNICATION_ID,
                CommunicationData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return communicationDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<CommunicationForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                           CommunicationData sourceCommunication) {
        DatabaseInfo database = syncContext.getDatabase();

        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);
        ProfessionalContactIdResolver professionalContactIdResolver =
                syncContext.getSharedObject(ProfessionalContactIdResolver.class);
        InquiryIdResolver inquiryIdResolver = syncContext.getSharedObject(InquiryIdResolver.class);
        CommunicationTypeIdResolver communicationTypeIdResolver =
                syncContext.getSharedObject(CommunicationTypeIdResolver.class);
        ProspectIdResolver prospectIdResolver = syncContext.getSharedObject(ProspectIdResolver.class);
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        CommunicationForeignKeys foreignKeys = new CommunicationForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(CommunicationData.TABLE_NAME, sourceCommunication.getId());

        String employeeLegacyId = sourceCommunication.getCompletedByEmplId();
        if (!Utils.isEmpty(employeeLegacyId)) {
            try {
                foreignKeys.setCompletedByEmployeeId(employeeIdResolver.getId(employeeLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, employeeLegacyId));
            }
        }

        Long profContactLegacyId = sourceCommunication.getProfContactId();
        if (!Utils.isNullOrZero(profContactLegacyId)) {
            try {
                foreignKeys.setProfessionalContactId(professionalContactIdResolver.getId(profContactLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ProfessionalContactData.TABLE_NAME, profContactLegacyId));
            }
        }

        Long inquiryLegacyId = sourceCommunication.getInquiryId();
        if (!Utils.isNullOrZero(inquiryLegacyId)) {
            try {
                foreignKeys.setInquiryId(inquiryIdResolver.getId(inquiryLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(InquiryData.TABLE_NAME, inquiryLegacyId));
            }
        }

        String communicationTypeLegacyId = sourceCommunication.getCommunicationType();
        if (!Utils.isEmpty(communicationTypeLegacyId)) {
            try {
                foreignKeys.setCommunicationTypeId(communicationTypeIdResolver.getId(communicationTypeLegacyId,
                        database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CommunicationTypeData.TABLE_NAME, communicationTypeLegacyId));
            }
        }

        Long prospectLegacyId = sourceCommunication.getProspectId();
        if (!Utils.isNullOrZero(prospectLegacyId)) {
            try {
                foreignKeys.setProspectId(prospectIdResolver.getId(prospectLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ProspectData.TABLE_NAME, prospectLegacyId));
            }
        }

        String createdByEmployeeLegacyId = sourceCommunication.getCreatedByEmplId();
        if (!Utils.isEmpty(createdByEmployeeLegacyId)) {
            try {
                foreignKeys.setCreatedByEmployeeId(employeeIdResolver.getId(createdByEmployeeLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, createdByEmployeeLegacyId));
            }
        }

        String facility = sourceCommunication.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        return new FKResolveResult<CommunicationForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<CommunicationData> sourceEntities,
                                       Map<CommunicationData, CommunicationForeignKeys> foreignKeysMap) {
        List<Communication> communications = new ArrayList<Communication>();
        for (CommunicationData sourceEntity : sourceEntities) {
            CommunicationForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            Communication communication = new Communication();
            communication.setLegacyId(sourceEntity.getId());
            communication.setDatabaseId(syncContext.getDatabaseId());
            communication.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            communications.add(communication);
        }
        communicationDao.insert(communications);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<CommunicationData> sourceEntities,
                                    Map<CommunicationData, CommunicationForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        for (CommunicationData sourceEntity : sourceEntities) {
            long id = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            CommunicationForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            Communication.Updatable communication = createUpdatable(sourceEntity, foreignKeys);
            communicationDao.update(communication, id);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        communicationDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
    }

    private Communication.Updatable createUpdatable(CommunicationData sourceEntity,
                                                    CommunicationForeignKeys foreignKeys) {
        Communication.Updatable updatable = new Communication.Updatable();
        updatable.setCompletedDate(sourceEntity.getCompletedDate());
        updatable.setParentType(sourceEntity.getParentType());
        updatable.setParentRecId(sourceEntity.getParentRecId());
        updatable.setDueDate(sourceEntity.getDueByDate());
        updatable.setNotes(sourceEntity.getNotes());
        updatable.setCompletedByEmployeeId(foreignKeys.getCompletedByEmployeeId());
        updatable.setProfContactId(foreignKeys.getProfessionalContactId());
        updatable.setInquiryId(foreignKeys.getInquiryId());
        updatable.setCommunicationTypeId(foreignKeys.getCommunicationTypeId());
        updatable.setProspectId(foreignKeys.getProspectId());
        updatable.setCreatedByEmployeeId(foreignKeys.getCreatedByEmployeeId());
        updatable.setOrganizationId(foreignKeys.getOrganizationId());
        return updatable;
    }
}
