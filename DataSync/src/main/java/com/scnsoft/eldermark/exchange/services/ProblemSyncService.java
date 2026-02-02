package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.dao.target.ProblemDao;
import com.scnsoft.eldermark.exchange.dao.target.ProblemObservationDao;
import com.scnsoft.eldermark.exchange.fk.ProblemForeignKeys;
import com.scnsoft.eldermark.exchange.model.IcdCodeSet;
import com.scnsoft.eldermark.exchange.model.source.ResDiagnosisData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.Problem;
import com.scnsoft.eldermark.exchange.model.target.ProblemObservation;
import com.scnsoft.eldermark.exchange.resolvers.CcdCodeResolver;
import com.scnsoft.eldermark.exchange.resolvers.DiagnosisSetupIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.EmployeeIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.ResidentIdResolver;
import com.scnsoft.eldermark.exchange.services.employees.EmployeeSyncService;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
import com.scnsoft.eldermark.exchange.services.residents.ResidentUpdateQueueService;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class ProblemSyncService extends StandardSyncService<ResDiagnosisData, Long, ProblemForeignKeys> {
    
    @Autowired
	@Qualifier("resDiagnosisSourceDao")
	private StandardSourceDao<ResDiagnosisData, Long> sourceDao;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private ProblemObservationDao problemObservationDao;

    @Autowired
    private ResidentUpdateQueueService residentUpdateQueueService;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(ResidentSyncService.class);
        dependencies.add(DiagnosisSetupSyncService.class);
        dependencies.add(EmployeeSyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<ResDiagnosisData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(ResDiagnosisData.TABLE_NAME, ResDiagnosisData.ID_COLUMN, ResDiagnosisData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return problemDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<ProblemForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
    		ResDiagnosisData resProblem) {
        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);
        DiagnosisSetupIdResolver diagnosisSetupIdResolver = syncContext.getSharedObject(DiagnosisSetupIdResolver.class);
        CcdCodeResolver ccdCodeResolver = syncContext.getSharedObject(CcdCodeResolver.class);

        ProblemForeignKeys foreignKeys = new ProblemForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResDiagnosisData.TABLE_NAME, resProblem.getId());

        Long resNumber = resProblem.getResNumber();
        if (resNumber != null) {
            try {
                long residentNewId = residentIdResolver.getId(resNumber, syncContext.getDatabase());
                foreignKeys.setResidentId(residentNewId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, resNumber));
            }
        }

        String employeeLegacyId = resProblem.getCreateUser();
        if (!Utils.isEmpty(employeeLegacyId)) {
            try {
                foreignKeys.setRecordedBy(employeeIdResolver.getId(employeeLegacyId, syncContext.getDatabase()));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, employeeLegacyId));
            }
        }

        String icdCode = resProblem.getCodeIcd();
        if (icdCode != null) {
            try {
                IcdCodeSet codeSet = diagnosisSetupIdResolver.getCodeSetFor(icdCode, syncContext.getDatabase());
                foreignKeys.setIcdCodeSet(codeSet);

                // if codeSet is null then we have not enough data to determine CCD code
                if (codeSet != null) {
                    Long ccdCodeId = ccdCodeResolver.getOrCreateCcdCodeFor(syncContext.getDatabase(), resProblem.getCodeIcd(),
                            resProblem.getDiagnosis(), codeSet);
                    if (ccdCodeId == null) {
                        errors.add(new FKResolveError("Can't find CCD code corresponding to " + codeSet.getCodeSystemName() + " code '" +
                                icdCode + "' in the SimplyConnect system database. It means that either the code value is invalid or SimplyConnect "
                                + "system CCD-codes table is incomplete."));
                    } else {
                        foreignKeys.setProblemValueCodeId(ccdCodeId);
                    }
                }
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, resNumber));
            }
        }

        return new FKResolveResult<ProblemForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResDiagnosisData> sourceProblems,
                                       Map<ResDiagnosisData, ProblemForeignKeys> foreignKeysMap) {
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();

        Set<Long> mappedConsanaResidentIds = new HashSet<>();
        List<Problem> problems = new ArrayList<Problem>();
        for (ResDiagnosisData sourceProblem : sourceProblems) {
            ProblemForeignKeys foreignKeys = foreignKeysMap.get(sourceProblem);

            Problem problem = new Problem();
            problem.setDatabaseId(database.getId());
            problem.setLegacyId(sourceProblem.getId());
            problem.setUpdatable(createProblemUpdatable(foreignKeys, sourceProblem));
            problems.add(problem);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                Problem mappedProblem = new Problem();
                mappedProblem.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                mappedProblem.setLegacyId(sourceProblem.getId());
                mappedProblem.setUpdatable(createMappedProblemUpdatable(foreignKeys, sourceProblem, databaseIdWithMappedResidentId.getId()));
                if (!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId())) {
                    mappedConsanaResidentIds.add(databaseIdWithMappedResidentId.getId());
                }
                problems.add(mappedProblem);
            }
        }

        long lastId = problemDao.getLastId();
        problemDao.insert(problems);
        IdMapping<Long> insertedProblemsIdMapping = problemDao.getIdMapping(database, lastId);

        List<ProblemObservation> observations = new ArrayList<ProblemObservation>();
        for (ResDiagnosisData sourceProblem : sourceProblems) {
            long problemLegacyId = sourceProblem.getId();
            long problemNewId = insertedProblemsIdMapping.getNewIdOrThrowException(problemLegacyId);

            ProblemForeignKeys foreignKeys = foreignKeysMap.get(sourceProblem);

            ProblemObservation observation = new ProblemObservation();
            observation.setProblemId(problemNewId);
            observation.setDatabaseId(database.getId());
            observation.setLegacyId(problemLegacyId);
            observation.setUpdatable(createObservationUpdatable(foreignKeys, sourceProblem));

            observations.add(observation);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                long mappedProblemNewId = problemDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), problemLegacyId, databaseIdWithMappedResidentId.getId());
                ProblemObservation mappedObservation = new ProblemObservation();
                mappedObservation.setProblemId(mappedProblemNewId);
                mappedObservation.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                mappedObservation.setLegacyId(problemLegacyId);
                mappedObservation.setUpdatable(createObservationUpdatable(foreignKeys, sourceProblem));
                observations.add(mappedObservation);
            }
        }
        problemObservationDao.insert(observations);
        if (!StringUtils.isEmpty(database.getConsanaXOwningId())) {
            residentUpdateQueueService.insert(foreignKeysMap, sourceProblems, "PROBLEM");
        }
        if (!CollectionUtils.isEmpty(mappedConsanaResidentIds)) {
            residentUpdateQueueService.insert(mappedConsanaResidentIds, "PROBLEM");
        }
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResDiagnosisData> problems,
                                    Map<ResDiagnosisData, ProblemForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
        Set<Long> mappedConsanaResidentIds = new HashSet<>();
        for (ResDiagnosisData sourceProblem : problems) {
            long problemLegacyId = sourceProblem.getId();
            long problemNewId = idMapping.getNewId(problemLegacyId);
            ProblemForeignKeys foreignKeys = foreignKeysMap.get(sourceProblem);

            Problem.Updatable problemUpdate = createProblemUpdatable(foreignKeys, sourceProblem);
            problemDao.update(problemUpdate, problemNewId);

            ProblemObservation.Updatable observationUpdate = createObservationUpdatable(foreignKeys, sourceProblem);
            problemObservationDao.update(observationUpdate, problemNewId);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                long mappedProblemNewId = problemDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), problemLegacyId, databaseIdWithMappedResidentId.getId());
                Problem.Updatable mappedProblemUpdate = createMappedProblemUpdatable(foreignKeys, sourceProblem, databaseIdWithMappedResidentId.getId());
                problemDao.update(mappedProblemUpdate, mappedProblemNewId);
                ProblemObservation.Updatable mappedObservationUpdate = createObservationUpdatable(foreignKeys, sourceProblem);
                if (!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId())) {
                    mappedConsanaResidentIds.add(databaseIdWithMappedResidentId.getId());
                }
                problemObservationDao.update(mappedObservationUpdate, mappedProblemNewId);
            }
        }
        if (!StringUtils.isEmpty(database.getConsanaXOwningId())) {
            residentUpdateQueueService.insert(foreignKeysMap, problems, "PROBLEM");
        }
        if (!CollectionUtils.isEmpty(mappedConsanaResidentIds)) {
            residentUpdateQueueService.insert(mappedConsanaResidentIds, "PROBLEM");
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext,
                                String legacyIdString) {
        long legacyId = Long.valueOf(legacyIdString);
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
        Long residentId = problemDao.getResidentId(database, legacyId);

        int mappedDeletedProblemObservationsCount = 0;
        int mappedDeletedProblemsCount = 0;
        if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
            DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
            long mappedProblemNewId = problemDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), legacyId, databaseIdWithMappedResidentId.getId());
            mappedDeletedProblemObservationsCount = problemObservationDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, mappedProblemNewId);
            mappedDeletedProblemsCount = problemDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, databaseIdWithMappedResidentId.getId());
        }
        int deletedProblemObservationsCount = problemObservationDao.delete(database, legacyId);
        int deletedProblemsCount = problemDao.delete(database, legacyId);
        if(!StringUtils.isEmpty(database.getConsanaXOwningId()) && residentId != null &&
                deletedProblemObservationsCount + deletedProblemsCount >= 1) {
            residentUpdateQueueService.insert(residentId, "PROBLEM");
        }
        if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
            DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
            if(!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId()) && databaseIdWithMappedResidentId.getId() != null &&
                    mappedDeletedProblemObservationsCount + mappedDeletedProblemsCount  >= 1) {
                residentUpdateQueueService.insert(databaseIdWithMappedResidentId.getId(), "PROBLEM");
            }
        }
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
    }

    public void updateResidentBirthDate(ResidentData residentData, Long residentNewId) {
        if(residentData.getBirthDate() != null) {
            problemObservationDao.updateAgeAtOnset(residentData.getBirthDate(), residentNewId);
        }
    }

    private Problem.Updatable createProblemUpdatable(ProblemForeignKeys foreignKeys, ResDiagnosisData resProblem) {
        return createProblemUpdatable(foreignKeys, resProblem, null);
    }

    private Problem.Updatable createMappedProblemUpdatable(ProblemForeignKeys foreignKeys, ResDiagnosisData resProblem, Long mappedResidentId) {
        return createProblemUpdatable(foreignKeys, resProblem, mappedResidentId);
    }

    private Problem.Updatable createProblemUpdatable(ProblemForeignKeys foreignKeys, ResDiagnosisData resProblem, Long residentId) {
        Problem.Updatable updatable = new Problem.Updatable();
        updatable.setResidentId(residentId != null ? residentId : foreignKeys.getResidentId());
        updatable.setEffectiveTimeLow(resProblem.getOnsetDate());
        updatable.setEffectiveTimeHigh(resProblem.getResolveDate());
        updatable.setRank(resProblem.getRank());
        return updatable;
    }

    private ProblemObservation.Updatable createObservationUpdatable(ProblemForeignKeys foreignKeys, ResDiagnosisData resProblem) {
        ProblemObservation.Updatable updatable = new ProblemObservation.Updatable();
        updatable.setEffectiveTimeLow(resProblem.getOnsetDate());
        updatable.setEffectiveTimeHigh(resProblem.getResolveDate());
        updatable.setProblemName(resProblem.getDiagnosis());
        updatable.setAgeUnit("a");
        updatable.setProblemIcdCode(resProblem.getCodeIcd());
        IcdCodeSet icdCodeSet = foreignKeys.getIcdCodeSet();
        if (icdCodeSet != null) {
            updatable.setProblemIcdCodeSet(icdCodeSet.getCodeSystemName());
        }

        Long valueCodeId = foreignKeys.getProblemValueCodeId();
        updatable.setProblemValueCodeId(valueCodeId);

        Integer ageAtOnset = calculateAgeAtOnset(resProblem.getOnsetDate(), resProblem.getResidentBirthDate());
        updatable.setAgeAtOnset(ageAtOnset);
        updatable.setPrimary(resProblem.getIsPrimary());
        updatable.setComments(resProblem.getNote());
        java.sql.Date createDate = resProblem.getCreateDate();
        java.sql.Time createTime = resProblem.getCreateTime();
        if (createDate != null) {
            if (createTime != null) {
                updatable.setRecordedDate(Utils.mergeDateTime(createDate, createTime));
            } else {
                updatable.setRecordedDate(createDate);
            }
        }
        updatable.setRecordedBy(foreignKeys.getRecordedBy());
        return updatable;
    }

    private Integer calculateAgeAtOnset(Date onset, Date birth) {
        if (onset == null || birth == null)
            return null;

        Period period = new Period(birth.getTime(), onset.getTime());
        return period.getYears();
    }
}
