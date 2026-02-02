package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.ProfessionalContactDao;
import com.scnsoft.eldermark.exchange.model.source.ProfessionalContactData;
import com.scnsoft.eldermark.exchange.model.target.ProfessionalContact;
import com.scnsoft.eldermark.exchange.resolvers.ProfessionalContactIdResolver;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProfessionalContactSyncService extends StandardSyncService<ProfessionalContactData, Long, Void> {
    @Autowired
    @Qualifier("professionalContactSourceDao")
    private StandardSourceDao<ProfessionalContactData, Long> sourceDao;

    @Autowired
    private ProfessionalContactDao professionalContactDao;

    @Value("${professionalcontacts.idmapping.cache.size}")
    private int idMappingSizeLimit;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        return null;
    }

    @Override
    protected FKResolveResult<Void> resolveForeignKeys(DatabaseSyncContext databaseSyncContext,
                                                       ProfessionalContactData professionalContactData) {
        return null;
    }

    @Override
    protected StandardSourceDao<ProfessionalContactData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(ProfessionalContactData.TABLE_NAME, ProfessionalContactData.PROF_CONTACT_ID,
                ProfessionalContactData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return professionalContactDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ProfessionalContactData> sourceEntities,
                                       Map<ProfessionalContactData, Void> foreignKeysMap) {
        List<ProfessionalContact> professionalContacts = new ArrayList<ProfessionalContact>();
        for (ProfessionalContactData sourceEntity : sourceEntities) {
            ProfessionalContact professionalContact = new ProfessionalContact();
            professionalContact.setDatabaseId(syncContext.getDatabaseId());
            professionalContact.setLegacyId(sourceEntity.getId());
            professionalContact.setUpdatable(createUpdatable(sourceEntity));

            professionalContacts.add(professionalContact);
        }
        professionalContactDao.insert(professionalContacts);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ProfessionalContactData> sourceEntities,
                                    Map<ProfessionalContactData, Void> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        for (ProfessionalContactData sourceEntity : sourceEntities) {
            long id = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            ProfessionalContact.Updatable updatable = createUpdatable(sourceEntity);
            professionalContactDao.update(updatable, id);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        professionalContactDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<Long> idMapping = professionalContactDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
        context.putSharedObject(ProfessionalContactIdResolver.class, new ProfessionalContactIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = professionalContactDao.getId(database, legacyId);
                }
                return newId;
            }
        });
    }

    private ProfessionalContact.Updatable createUpdatable(ProfessionalContactData sourceEntity) {
        ProfessionalContact.Updatable updatable = new ProfessionalContact.Updatable();
        updatable.setContactFirstName(sourceEntity.getContactFirstName());
        updatable.setContactLastName(sourceEntity.getContactLastName());
        return updatable;
    }
}
