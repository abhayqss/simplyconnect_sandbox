package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.dao.target.CcdCodeDao;
import com.scnsoft.eldermark.exchange.dao.target.ResidentDao;
import com.scnsoft.eldermark.exchange.dao.target.VitalSignDao;
import com.scnsoft.eldermark.exchange.dao.target.VitalSignObservationDao;
import com.scnsoft.eldermark.exchange.fk.VitalSignForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.source.VitalSignData;
import com.scnsoft.eldermark.exchange.model.target.CcdCode;
import com.scnsoft.eldermark.exchange.model.target.VitalSign;
import com.scnsoft.eldermark.exchange.model.target.VitalSignObservation;
import com.scnsoft.eldermark.exchange.model.target.VitalSignType;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.ResidentIdResolver;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class VitalSignSyncService extends StandardSyncService<VitalSignData, Long, VitalSignForeignKeys> {
    private static final Logger logger = LoggerFactory.getLogger(VitalSignSyncService.class);

    @Autowired
    @Qualifier("vitalSignSourceDao")
    private StandardSourceDao<VitalSignData, Long> sourceDao;

    @Autowired
    private VitalSignDao vitalSignDao;

    @Autowired
    private VitalSignObservationDao vitalSignObservationDao;

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private ResidentDao residentDao;

    private Map<VitalSignType, CcdCode> resultTypeCcdCodes = new HashMap<VitalSignType, CcdCode>();

    @PostConstruct
    public void init() {
        for (VitalSignType vitalSignType : VitalSignType.values()) {
            CcdCode typeCcdCode = ccdCodeDao.getCode(vitalSignType.getCode(), vitalSignType.getCodeSystem());
            resultTypeCcdCodes.put(vitalSignType, typeCcdCode);
        }
    }

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(ResidentSyncService.class);
        dependencies.add(CompanySyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<VitalSignData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(VitalSignData.TABLE_NAME, VitalSignData.RES_VITALS_ID, VitalSignData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return vitalSignDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<VitalSignForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                       VitalSignData vitalSignData) {
        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        DatabaseInfo database = syncContext.getDatabase();

        VitalSignForeignKeys foreignKeys = new VitalSignForeignKeys();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(VitalSignData.TABLE_NAME, vitalSignData.getId());
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        Long resNumber = vitalSignData.getResNumber();
        if (!Utils.isNullOrZero(resNumber)) {
            try {
                long residentId = residentIdResolver.getId(resNumber, database);
                foreignKeys.setResidentId(residentId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, resNumber));
            }
        }

        String facilityCode = vitalSignData.getFacility();
        if (!Utils.isEmpty(facilityCode)) {
            try {
                long facilityId = companyIdResolver.getId(facilityCode, database);
                foreignKeys.setOrganizationId(facilityId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facilityCode));
            }
        }

        return new FKResolveResult<VitalSignForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<VitalSignData> sourceEntities,
                                       Map<VitalSignData, VitalSignForeignKeys> foreignKeysMap) {
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();

        List<VitalSign> vitalSigns = new ArrayList<VitalSign>();
        for (VitalSignData sourceEntity : sourceEntities) {
            VitalSignForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            VitalSign vitalSign = new VitalSign();
            vitalSign.setLegacyUuid(sourceEntity.getUuid());
            vitalSign.setLegacyId(sourceEntity.getId());
            vitalSign.setDatabaseId(database.getId());
            vitalSign.setUpdatable(createVitalSignUpdatable(sourceEntity, foreignKeys));

            vitalSigns.add(vitalSign);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                VitalSign mappedVitalSign = new VitalSign();
                mappedVitalSign.setLegacyUuid(sourceEntity.getUuid());
                mappedVitalSign.setLegacyId(sourceEntity.getId());
                mappedVitalSign.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                mappedVitalSign.setUpdatable(createMappedVitalSignUpdatable(sourceEntity, databaseIdWithMappedResidentId.getId(), foreignKeys.getOrganizationId()));
                vitalSigns.add(mappedVitalSign);
            }
        }
        long lastId = vitalSignDao.getLastId();
        vitalSignDao.insert(vitalSigns);
        IdMapping<Long> vitalSignsIdMapping = vitalSignDao.getIdMapping(database, lastId);

        List<VitalSignObservation> vitalSignObservations = new ArrayList<VitalSignObservation>();
        for (VitalSignData sourceEntity : sourceEntities) {
            long vitalSignId = vitalSignsIdMapping.getNewIdOrThrowException(sourceEntity.getId());
            VitalSignForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            for(VitalSignType vitalSignType : VitalSignType.values()) {
                VitalSignObservation.Updatable updatable = createObservationUpdatable(sourceEntity, vitalSignType);
                // insert new observation if it is not empty
                if (isObservationNotEmpty(updatable.getValue())) {
                    VitalSignObservation vitalSignObservation = new VitalSignObservation();
                    vitalSignObservation.setVitalSignId(vitalSignId);
                    vitalSignObservation.setDatabaseId(database.getId());
                    vitalSignObservation.setLegacyId(sourceEntity.getId());
                    vitalSignObservation.setUpdatable(updatable);

                    vitalSignObservations.add(vitalSignObservation);
                }
            }

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                Long mappedVitalSignId = vitalSignDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), sourceEntity.getId(), databaseIdWithMappedResidentId.getId());
                for(VitalSignType vitalSignType : VitalSignType.values()) {
                    VitalSignObservation.Updatable updatable = createObservationUpdatable(sourceEntity, vitalSignType);
                    // insert new observation if it is not empty
                    if (isObservationNotEmpty(updatable.getValue())) {
                        VitalSignObservation vitalSignObservation = new VitalSignObservation();
                        vitalSignObservation.setVitalSignId(mappedVitalSignId);
                        vitalSignObservation.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                        vitalSignObservation.setLegacyId(sourceEntity.getId());
                        vitalSignObservation.setUpdatable(updatable);

                        vitalSignObservations.add(vitalSignObservation);
                    }
                }
            }
        }
        vitalSignObservationDao.insert(vitalSignObservations);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<VitalSignData> sourceEntities,
                                    Map<VitalSignData, VitalSignForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
        List<Long> sourceIds = Utils.getIds(sourceEntities);

        HashMap<VitalSignType, IdMapping<Long>> typeIdMappings = new HashMap<VitalSignType, IdMapping<Long>>();
        for(VitalSignType vitalSignType : VitalSignType.values()) {
            typeIdMappings.put(vitalSignType, vitalSignObservationDao.getIdMapping(database, resultTypeCcdCodes.get(vitalSignType), sourceIds));
        }

        for (VitalSignData sourceEntity : sourceEntities) {
            VitalSignForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            long vitalSignLegacyId = sourceEntity.getId();
            long vitalSignId = idMapping.getNewIdOrThrowException(sourceEntity.getId());

            vitalSignDao.update(createVitalSignUpdatable(sourceEntity, foreignKeys), vitalSignId);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                Long mappedVitalSignId = vitalSignDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), sourceEntity.getId(), databaseIdWithMappedResidentId.getId());
                vitalSignDao.update(createMappedVitalSignUpdatable(sourceEntity, databaseIdWithMappedResidentId.getId(), foreignKeys.getOrganizationId()), mappedVitalSignId);
            }

            List<VitalSignObservation> entitiesToAdd = new ArrayList<VitalSignObservation>();

            for(VitalSignType vitalSignType : VitalSignType.values()) {
                IdMapping<Long> typeIdMapping = typeIdMappings.get(vitalSignType);
                Long typeId = typeIdMapping.getNewId(vitalSignLegacyId);
                updateObservation(database.getId(), sourceEntity, vitalSignId, entitiesToAdd, vitalSignType, typeId);
            }

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                for(VitalSignType vitalSignType : VitalSignType.values()) {
                    Long mappedVitalSignId = vitalSignDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), sourceEntity.getId(), databaseIdWithMappedResidentId.getId());
                    Long mappedTypeId = vitalSignObservationDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), resultTypeCcdCodes.get(vitalSignType), mappedVitalSignId);
                    updateObservation(databaseIdWithMappedResidentId.getDatabaseId(), sourceEntity, mappedVitalSignId, entitiesToAdd, vitalSignType, mappedTypeId);
                }
            }

            vitalSignObservationDao.insert(entitiesToAdd);
        }
    }

    private void updateObservation(Long databaseId, VitalSignData sourceEntity, long vitalSignId, List<VitalSignObservation> entitiesToAdd, VitalSignType vitalSignType, Long typeId) {
        VitalSignObservation.Updatable updatable = createObservationUpdatable(sourceEntity, vitalSignType);
        if (typeId != null) {
            if (isObservationNotEmpty(updatable.getValue())) {
                // update existing observation if it is not empty
                vitalSignObservationDao.update(updatable, typeId);
            } else {
                // delete observation that became empty
                vitalSignObservationDao.deleteById(typeId);
            }
        } else {
            // insert new observation if it is not empty
            if (isObservationNotEmpty(updatable.getValue())) {
                VitalSignObservation entity = new VitalSignObservation();

                entity.setVitalSignId(vitalSignId);
                entity.setDatabaseId(databaseId);
                entity.setLegacyId(sourceEntity.getId());
                entity.setUpdatable(updatable);

                entitiesToAdd.add(entity);
            }
        }
    }

    public void updateMeasurementUnits(CompanyData companyData, Long companyNewId) {
        if(companyData.getWeightMeasureUnit() != null) {
            vitalSignObservationDao.updateUnit(companyData.getWeightMeasureUnit(), companyNewId, resultTypeCcdCodes.get(VitalSignType.WEIGHT).getId());
        }
        if(companyData.getHeightMeasureUnit() != null) {
            vitalSignObservationDao.updateUnit(companyData.getHeightMeasureUnit(), companyNewId, resultTypeCcdCodes.get(VitalSignType.WEIGHT).getId());
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();

        Long legacyId;
        if(legacyIdString.length() > 20) {
            legacyId = vitalSignDao.getLegacyIdByLegacyUUID(database, legacyIdString);
        } else {
            legacyId = Long.valueOf(legacyIdString);
        }

        if (legacyId != null) {
            Long residentId = vitalSignDao.getResidentId(database.getId(), legacyId);
            if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
                Long mappedVitalSignId = vitalSignDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), legacyId, databaseIdWithMappedResidentId.getId());
                vitalSignObservationDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, mappedVitalSignId);
                vitalSignDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, databaseIdWithMappedResidentId.getId());
            }
            vitalSignObservationDao.delete(database, legacyId);
            vitalSignDao.delete(database, legacyId);
        } else {
            logger.info("Unable to delete " + VitalSign.TABLE_NAME + " record. UUID is null. Legacy_id=" + legacyIdString);
        }
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
    }

    private VitalSign.Updatable createVitalSignUpdatable(VitalSignData vitalSignData, VitalSignForeignKeys foreignKeys) {
        VitalSign.Updatable updatable = new VitalSign.Updatable();
        updatable.setResidentId(foreignKeys.getResidentId());
        updatable.setEffectiveTime(createEffectiveTime(vitalSignData));
        updatable.setOrganizationId(foreignKeys.getOrganizationId());
        return updatable;
    }

    private VitalSign.Updatable createMappedVitalSignUpdatable(VitalSignData vitalSignData, Long residentId, Long organizationId) {
        VitalSign.Updatable updatable = new VitalSign.Updatable();
        updatable.setResidentId(residentId);
        updatable.setEffectiveTime(createEffectiveTime(vitalSignData));
        updatable.setOrganizationId(organizationId);
        return updatable;
    }

    private VitalSignObservation.Updatable createObservationUpdatable(VitalSignData vitalSignData, VitalSignType resultType) {
        VitalSignObservation.Updatable updatable = new VitalSignObservation.Updatable();
        updatable.setEffectiveTime(createEffectiveTime(vitalSignData));
        updatable.setResultTypeCodeId(resultTypeCcdCodes.get(resultType).getId());
        switch (resultType) {
            case OXYGEN_SATURATION:
                updatable.setValue(toDouble(vitalSignData.getO2Saturation()));
                updatable.setUnit("%");
                break;
            case HEART_BEAT:
                updatable.setValue(toDouble(vitalSignData.getPulse()));
                updatable.setUnit("/min");
                break;
            case INTRAVASCULAR_SYSTOLIC:
                updatable.setValue(toDouble(extractIntravascular(vitalSignData.getBloodPressure(), VitalSignType.INTRAVASCULAR_SYSTOLIC)));
                updatable.setUnit("mm Hg");
                break;
            case INTRAVASCULAR_DIASTOLIC:
                updatable.setValue(toDouble(extractIntravascular(vitalSignData.getBloodPressure(), VitalSignType.INTRAVASCULAR_DIASTOLIC)));
                updatable.setUnit("mm Hg");
                break;
            case BODY_TEMPERATURE:
                updatable.setValue(toDouble(vitalSignData.getTemperature()));
                updatable.setUnit("degF");
                break;
            case RESPIRATION_RATE:
                updatable.setValue(toDouble(vitalSignData.getRespiration()));
                updatable.setUnit("/min");
                break;
            case HEIGHT:
                updatable.setValue(toDouble(vitalSignData.getHeight() != null ? vitalSignData.getHeight().toString() : null));
                updatable.setUnit(vitalSignData.getHeightMeasureUnit() != null ? vitalSignData.getHeightMeasureUnit() : "in_us");
                break;
            case WEIGHT:
                updatable.setValue(toDouble(vitalSignData.getWeight() != null ? vitalSignData.getWeight().toString() : null));
                updatable.setUnit(vitalSignData.getWeightMeasureUnit() != null ? vitalSignData.getWeightMeasureUnit() : "lb_av");
                break;
        }
        return updatable;
    }

    private String extractIntravascular(String bloodPressure, VitalSignType resultType) {
        if (Utils.isEmpty(bloodPressure))
            return null;

        String[] data = bloodPressure.split("/");
        switch (resultType) {
            case INTRAVASCULAR_SYSTOLIC:
                return (data.length > 0) ? data[0] : null;
            case INTRAVASCULAR_DIASTOLIC:
                return (data.length > 1) ? data[1] : null;
        }

        return null;
    }

    private Date createEffectiveTime(VitalSignData vitalSignData) {
        Date effectiveTime = null;

        java.sql.Date vitalSignDate = vitalSignData.getDate();
        java.sql.Time vitalSignTime = vitalSignData.getTime();
        if (vitalSignDate != null) {
            if (vitalSignTime != null) {
                effectiveTime = Utils.mergeDateTime(vitalSignDate, vitalSignTime);
            } else {
                effectiveTime = vitalSignDate;
            }
        }

        return effectiveTime;
    }

    private Double toDouble(String value) {
        if (value == null || value.isEmpty() || "0".equals(value))
            return null;

        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isObservationNotEmpty(Double value) {
        return value != null;
    }
}
