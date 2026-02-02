package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.OrganizationAddressDao;
import com.scnsoft.eldermark.exchange.dao.target.OrganizationDao;
import com.scnsoft.eldermark.exchange.model.source.PharmacyData;
import com.scnsoft.eldermark.exchange.model.target.Organization;
import com.scnsoft.eldermark.exchange.model.target.OrganizationAddress;
import com.scnsoft.eldermark.exchange.model.target.OrganizationType;
import com.scnsoft.eldermark.exchange.resolvers.PharmacyIdResolver;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PharmacySyncService extends StandardSyncService<PharmacyData, Long, Void> {
    @Autowired
    @Qualifier("pharmacySourceDao")
    private StandardSourceDao<PharmacyData, Long> sourceDao;

    @Value("${pharmacies.idmapping.cache.size}")
    private int idMappingSizeLimit;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private OrganizationAddressDao organizationAddressDao;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        return Collections.emptyList();
    }

    @Override
    protected StandardSourceDao<PharmacyData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(PharmacyData.TABLE_NAME, PharmacyData.CODE, PharmacyData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return organizationDao.getPharmaciesIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<Void> resolveForeignKeys(DatabaseSyncContext syncContext, PharmacyData entity) {
        return null;
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<PharmacyData> pharmacies,
                                       Map<PharmacyData, Void> foreignKeysMap) {
        DatabaseInfo database = syncContext.getDatabase();

        List<Organization> organizations = new ArrayList<Organization>();
        for (PharmacyData pharmacy : pharmacies) {
            Organization organization = new Organization();
            organization.setLegacyId(String.valueOf(pharmacy.getCode()));
            organization.setDatabaseId(database.getId());
            organization.setLegacyTable(OrganizationType.PHARMACY.getLegacyTableName());
            organization.setUpdatable(createOrganizationUpdatable(pharmacy));

            organizations.add(organization);
        }

        long lastId = organizationDao.getLastId();
        organizationDao.insert(organizations);
        IdMapping<Long> idMapping = organizationDao.getPharmaciesIdMapping(database, lastId);

        List<OrganizationAddress> addresses = new ArrayList<OrganizationAddress>();
        for (PharmacyData pharmacy : pharmacies) {
            long pharmacyLegacyId = pharmacy.getCode();
            long pharmacyNewId = idMapping.getNewIdOrThrowException(pharmacyLegacyId);

            OrganizationAddress address = new OrganizationAddress();
            address.setUpdatable(createAddressUpdatable(pharmacy));
            address.setOrganizationId(pharmacyNewId);
            address.setDatabaseId(database.getId());
            address.setLegacyTable(OrganizationType.PHARMACY.getLegacyTableName());
            address.setLegacyId(String.valueOf(pharmacy.getCode()));

            addresses.add(address);
        }
        organizationAddressDao.insert(addresses);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext,
                                    List<PharmacyData> pharmacies,
                                    Map<PharmacyData, Void> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        for (PharmacyData pharmacy : pharmacies) {
            long pharmacyLegacyId = pharmacy.getCode();
            long pharmacyNewId = idMapping.getNewIdOrThrowException(pharmacyLegacyId);

            Organization.Updatable organizationUpdate = createOrganizationUpdatable(pharmacy);
            organizationDao.update(organizationUpdate, pharmacyNewId);

            OrganizationAddress.Updatable addressUpdate = createAddressUpdatable(pharmacy);
            organizationAddressDao.update(addressUpdate, pharmacyNewId);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext,
                                String legacyIdString) {
        DatabaseInfo database = syncContext.getDatabase();
        organizationAddressDao.delete(database, OrganizationType.PHARMACY, legacyIdString);
        organizationDao.delete(database, OrganizationType.PHARMACY, legacyIdString);
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<Long> idMapping = organizationDao.getPharmaciesIdMapping(context.getDatabase(), idMappingSizeLimit);
        context.putSharedObject(PharmacyIdResolver.class, new PharmacyIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = organizationDao.getPharmacyNewId(database, legacyId);
                }
                return newId;
            }
        });
    }

    private Organization.Updatable createOrganizationUpdatable(PharmacyData pharmacy) {
        Organization.Updatable organizationUpdatable = new Organization.Updatable();
        organizationUpdatable.setName(pharmacy.getName());
        return organizationUpdatable;
    }

    private OrganizationAddress.Updatable createAddressUpdatable(PharmacyData pharmacy) {
        OrganizationAddress.Updatable address = new OrganizationAddress.Updatable();
        address.setPostalAddressUse(null);
        address.setStreetAddress(pharmacy.getStreetAddress());
        address.setCity(pharmacy.getCity());
        address.setState(pharmacy.getState());
        address.setPostalCode(pharmacy.getZip());
        address.setCountry("US");
        return address;
    }
}
