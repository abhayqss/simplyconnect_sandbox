package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.ProspectDao;
import com.scnsoft.eldermark.exchange.fk.ProspectForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.*;
import com.scnsoft.eldermark.exchange.model.target.Prospect;
import com.scnsoft.eldermark.exchange.resolvers.*;
import com.scnsoft.eldermark.exchange.services.employees.EmployeeSyncService;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
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
public class ProspectSyncService extends StandardSyncService<ProspectData, Long, ProspectForeignKeys> {
    @Autowired
    @Qualifier("prospectSourceDao")
    private StandardSourceDao<ProspectData, Long> sourceDao;

    @Autowired
    private ProspectDao prospectDao;

    @Value("${prospects.idmapping.cache.size}")
    private int idMappingSizeLimit;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        dependencies.add(EmployeeSyncService.class);
        dependencies.add(ResidentSyncService.class);
        dependencies.add(ProfessionalContactSyncService.class);
        dependencies.add(InquirySyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<ProspectData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(ProspectData.TABLE_NAME, ProspectData.PROSPECT_ID, ProspectData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return prospectDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<ProspectForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                      ProspectData prospect) {
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);
        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        ProfessionalContactIdResolver professionalContactIdResolver =
                syncContext.getSharedObject(ProfessionalContactIdResolver.class);
        InquiryIdResolver inquiryIdResolver = syncContext.getSharedObject(InquiryIdResolver.class);

        DatabaseInfo database = syncContext.getDatabase();

        ProspectForeignKeys foreignKeys = new ProspectForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ProspectData.TABLE_NAME, prospect.getId());

        String reserveFacilityLegacyId = prospect.getReserveFacility();
        if (!Utils.isEmpty(reserveFacilityLegacyId)) {
            try {
                long reserveFacilityId = companyIdResolver.getId(reserveFacilityLegacyId, database);
                foreignKeys.setReserveFacilityId(reserveFacilityId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, reserveFacilityLegacyId));
            }
        }

        String primaryFacilityLegacyId = prospect.getFacilityPrimary();
        if (!Utils.isEmpty(primaryFacilityLegacyId)) {
            try {
                long primaryFacilityId = companyIdResolver.getId(primaryFacilityLegacyId, database);
                foreignKeys.setFacilityPrimaryId(primaryFacilityId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, primaryFacilityLegacyId));
            }
        }

        String residentFacilityLegacyId = prospect.getResidentFacility();
        if (!Utils.isEmpty(residentFacilityLegacyId)) {
            try {
                long residentFacilityId = companyIdResolver.getId(residentFacilityLegacyId, database);
                foreignKeys.setResidentFacilityId(residentFacilityId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, residentFacilityLegacyId));
            }
        }

        String employeeLegacyId = prospect.getSalesRepEmployeeId();
        if (!Utils.isEmpty(employeeLegacyId)) {
            try {
                long employeeId = employeeIdResolver.getId(employeeLegacyId, database);
                foreignKeys.setSalesRepEmployeeId(employeeId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, employeeLegacyId));
            }
        }

        Long residentLegacyId = prospect.getResNumber();
        if (!Utils.isNullOrZero(residentLegacyId)) {
            try {
                long residentId = residentIdResolver.getId(residentLegacyId, database);
                foreignKeys.setResidentId(residentId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
            }
        }

        Long profContactLegacyId = prospect.getReferralSourceProfContId();
        if (!Utils.isNullOrZero(profContactLegacyId)) {
            try {
                long profContactId = professionalContactIdResolver.getId(profContactLegacyId, database);
                foreignKeys.setReferralSourceProfContId(profContactId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ProfessionalContactData.TABLE_NAME, profContactLegacyId));
            }
        }

        Long inquiryLegacyId = prospect.getCopiedFromInquiryId();
        if (!Utils.isNullOrZero(inquiryLegacyId)) {
            try {
                long inquiryId = inquiryIdResolver.getId(inquiryLegacyId, database);
                foreignKeys.setCopiedFromInquiryId(inquiryId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(InquiryData.TABLE_NAME, inquiryLegacyId));
            }
        }
        return new FKResolveResult<ProspectForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ProspectData> sourceEntities,
                                       Map<ProspectData, ProspectForeignKeys> foreignKeysMap) {
        List<Prospect> prospects = new ArrayList<Prospect>();
        for (ProspectData sourceEntity : sourceEntities) {
            ProspectForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            Prospect prospect = new Prospect();
            prospect.setLegacyId(sourceEntity.getId());
            prospect.setDatabaseId(syncContext.getDatabaseId());
            prospect.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            prospects.add(prospect);
        }
        prospectDao.insert(prospects);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ProspectData> sourceEntities,
                                    Map<ProspectData, ProspectForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        for (ProspectData sourceEntity : sourceEntities) {
            ProspectForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);
            long id = idMapping.getNewIdOrThrowException(sourceEntity.getId());

            Prospect.Updatable updatable = createUpdatable(sourceEntity, foreignKeys);
            prospectDao.update(updatable, id);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        prospectDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<Long> idMapping = prospectDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
        context.putSharedObject(ProspectIdResolver.class, new ProspectIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = prospectDao.getId(database, legacyId);
                }
                return newId;
            }
        });
    }

    private Prospect.Updatable createUpdatable(ProspectData prospectData, ProspectForeignKeys foreignKeys) {
        Prospect.Updatable updatable = new Prospect.Updatable();
        updatable.setDateBecameProspect(prospectData.getDateBecameProspect());
        updatable.setFirstName(prospectData.getFirstName());
        updatable.setLastName(prospectData.getLastName());
        updatable.setReserveUnitNumber(prospectData.getReserveUnitNumber());
        updatable.setMoveInDate(prospectData.getMoveInDate());
        updatable.setMoveOutDate(prospectData.getMoveOutDate());
        updatable.setDepositDate(prospectData.getDepositDate());
        updatable.setAssessmentDate(prospectData.getAssessmentDate());
        updatable.setUnitIsReserved(prospectData.getUnitIsReserved());
        updatable.setReservedFrom(prospectData.getReservedFrom());
        updatable.setReservedTo(prospectData.getReservedTo());
        updatable.setCurrentStatus(prospectData.getCurrentStatus());
        updatable.setSecondOccupant(prospectData.getSecondOccupant());
        updatable.setResidentStatus(prospectData.getResidentStatus());
        updatable.setResidentUnit(prospectData.getResidentUnit());
        updatable.setCancelDate(prospectData.getCancelDate());
        updatable.setRelatedPartyFirstName(prospectData.getRelatedPartyFirstName());
        updatable.setRelatedPartyLastName(prospectData.getRelatedPartyLastName());
        updatable.setRelatedPartyPhones(prospectData.getRelatedPartyPhones());
        updatable.setProsPhones(prospectData.getProsPhones());
        updatable.setReserveFacilityId(foreignKeys.getReserveFacilityId());
        updatable.setFacilityPrimaryId(foreignKeys.getFacilityPrimaryId());
        updatable.setResidentFacilityId(foreignKeys.getResidentFacilityId());
        updatable.setSalesRepEmployeeId(foreignKeys.getSalesRepEmployeeId());
        updatable.setResidentId(foreignKeys.getResidentId());
        updatable.setReferralSourceProfCont(foreignKeys.getReferralSourceProfContId());
        updatable.setCopiedFromInquiryId(foreignKeys.getCopiedFromInquiryId());
        return updatable;
    }
}
