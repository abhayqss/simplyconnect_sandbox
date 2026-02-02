package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.ExchangeUtils;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.*;
import com.scnsoft.eldermark.exchange.fk.OrganizationForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.OrganizationAddressResolver;
import com.scnsoft.eldermark.exchange.validators.CcdCodesValidator;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class CompanySyncService extends StandardSyncService<CompanyData, String, OrganizationForeignKeys> {
    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private OrganizationAddressDao organizationAddressDao;

    @Autowired
    private OrganizationTelecomDao organizationTelecomDao;

    @Autowired
    private OrganizationHieConsentPolicyDao organizationHieConsentPolicyDao;

    @Autowired
    private StateDao stateDao;

    @Autowired
    private VitalSignSyncService vitalSignSyncService;

    @Autowired
    @Qualifier("companySourceDao")
    private StandardSourceDao<CompanyData, String> companySourceDao;
    
    @Autowired
    private CcdCodesValidator ccdCodesValidator;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        return Collections.emptyList();
    }

    @Override
    protected StandardSourceDao<CompanyData, String> getSourceDao() {
        return companySourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(CompanyData.TABLE_NAME, CompanyData.CODE, CompanyData.class);
    }

    @Override
    protected IdMapping<String> getIdMapping(DatabaseSyncContext syncContext, List<String> legacyIds) {
        return organizationDao.getCompaniesIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<OrganizationForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, CompanyData entity) {
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        OrganizationForeignKeys foreignKeys = new OrganizationForeignKeys();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(CompanyData.TABLE_NAME, entity.getId());
        
        Long resResuscitateCodeId = ExchangeUtils.replaceZeroByNull(entity.getResResuscitateCcdId());
        if (resResuscitateCodeId != null) {
            if (ccdCodesValidator.validate(resResuscitateCodeId)) {
                foreignKeys.setResResuscitateCodeId(resResuscitateCodeId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(CompanyData.RES_RESUSCITATE_CCDID, resResuscitateCodeId));
            }
        }
        
        Long resAdvDir1CcdId = ExchangeUtils.replaceZeroByNull(entity.getResAdvDir1CcdId());
        if (resAdvDir1CcdId != null) {
            if (ccdCodesValidator.validate(resAdvDir1CcdId)) {
                foreignKeys.setResAdvDir1CodeId(resAdvDir1CcdId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(CompanyData.RES_ADVDIR_1_CCDID, resAdvDir1CcdId));
            }
        }
        
        Long resAdvDir2CcdId = ExchangeUtils.replaceZeroByNull(entity.getResAdvDir2CcdId());
        if (resAdvDir2CcdId != null) {
            if (ccdCodesValidator.validate(resAdvDir2CcdId)) {
                foreignKeys.setResAdvDir2CodeId(resAdvDir2CcdId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(CompanyData.RES_ADVDIR_2_CCDID, resAdvDir2CcdId));
            }
        }
        
        Long resAdvDir3CcdId = ExchangeUtils.replaceZeroByNull(entity.getResAdvDir3CcdId());
        if (resAdvDir3CcdId != null) {
            if (ccdCodesValidator.validate(resAdvDir3CcdId)) {
                foreignKeys.setResAdvDir3CodeId(resAdvDir3CcdId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(CompanyData.RES_ADVDIR_3_CCDID, resAdvDir3CcdId));
            }
        }
        
        Long resAdvDir4CcdId = ExchangeUtils.replaceZeroByNull(entity.getResAdvDir4CcdId());
        if (resAdvDir4CcdId != null) {
            if (ccdCodesValidator.validate(resAdvDir4CcdId)) {
                foreignKeys.setResAdvDir4CodeId(resAdvDir4CcdId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(CompanyData.RES_ADVDIR_4_CCDID, resAdvDir4CcdId));
            }
        }
        
        Long resCodeStat1CodeId = ExchangeUtils.replaceZeroByNull(entity.getResCodeStat1CcdId());
        if (resCodeStat1CodeId != null) {
            if (ccdCodesValidator.validate(resCodeStat1CodeId)) {
                foreignKeys.setResCodeStat1CodeId(resCodeStat1CodeId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(CompanyData.RES_CODESTAT_1_CCDID, resCodeStat1CodeId));
            }
        }
        
        Long resCodeStat2CodeId = ExchangeUtils.replaceZeroByNull(entity.getResCodeStat2CcdId());
        if (resCodeStat2CodeId != null) {
            if (ccdCodesValidator.validate(resCodeStat2CodeId)) {
                foreignKeys.setResCodeStat2CodeId(resCodeStat2CodeId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(CompanyData.RES_CODESTAT_2_CCDID, resCodeStat2CodeId));
            }
        }
        
        Long resCodeStat3CodeId = ExchangeUtils.replaceZeroByNull(entity.getResCodeStat3CcdId());
        if (resCodeStat3CodeId != null) {
            if (ccdCodesValidator.validate(resCodeStat3CodeId)) {
                foreignKeys.setResCodeStat3CodeId(resCodeStat3CodeId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(CompanyData.RES_CODESTAT_3_CCDID, resCodeStat3CodeId));
            }
        }
        
        Long resCodeStat4CodeId = ExchangeUtils.replaceZeroByNull(entity.getResCodeStat4CcdId());
        if (resCodeStat4CodeId != null) {
            if (ccdCodesValidator.validate(resCodeStat4CodeId)) {
                foreignKeys.setResCodeStat4CodeId(resCodeStat4CodeId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(CompanyData.RES_CODESTAT_4_CCDID, resCodeStat4CodeId));
            }
        }
        
        return new FKResolveResult<OrganizationForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<CompanyData> sourceNewCompanies,
                                       Map<CompanyData, OrganizationForeignKeys> foreignKeysMap) {
        DatabaseInfo database = syncContext.getDatabase();

        List<Organization> organizations = new ArrayList<Organization>();
        for (CompanyData company : sourceNewCompanies) {
        	OrganizationForeignKeys foreignKeys = foreignKeysMap.get(company);
            Organization organization = createOrganization(company, database, foreignKeys);
            organizations.add(organization);
        }

        long lastIdBeforeInsertion = organizationDao.getLastId();
        organizationDao.insert(organizations);
        IdMapping<String> insertedCompaniesIdMapping = organizationDao.getCompaniesIdMapping(database,
                lastIdBeforeInsertion);

        List<OrganizationTelecom> telecoms = new ArrayList<OrganizationTelecom>();
        List<OrganizationAddress> addresses = new ArrayList<OrganizationAddress>();
        List<OrganizationHieConsentPolicy> organizationHieConsentPolicies = new ArrayList<OrganizationHieConsentPolicy>();
        for (CompanyData company : sourceNewCompanies) {
            String legacyId = company.getId();
            long organizationId = insertedCompaniesIdMapping.getNewIdOrThrowException(legacyId);

            OrganizationTelecom telecom = createTelecom(company, organizationId, database);
            telecoms.add(telecom);

            OrganizationAddress address = createAddress(company, organizationId, database);
            addresses.add(address);

            OrganizationHieConsentPolicy organizationHieConsentPolicy = createOrganizationHieConsentPolicy(company, organizationId);
            organizationHieConsentPolicies.add(organizationHieConsentPolicy);
        }
        organizationTelecomDao.insert(telecoms);
        organizationAddressDao.insert(addresses);
        organizationHieConsentPolicyDao.insert(organizationHieConsentPolicies);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<CompanyData> sourceEntities,
                                    Map<CompanyData, OrganizationForeignKeys> foreignKeysMap, IdMapping<String> idMapping) {
        for (CompanyData company : sourceEntities) {
            String legacyId = company.getId();
            long organizationId = idMapping.getNewIdOrThrowException(legacyId);

            OrganizationForeignKeys foreignKeys = foreignKeysMap.get(company);
            Organization.Updatable organizationUpdate = createOrganizationUpdatable(company, foreignKeys);
            OrganizationTelecom.Updatable telecomUpdate = createTelecomUpdate(company);
            OrganizationAddress.Updatable addressUpdate = createAddressUpdate(company);

            organizationDao.update(organizationUpdate, organizationId);
            organizationTelecomDao.update(telecomUpdate, organizationId);
            organizationAddressDao.update(addressUpdate, organizationId);
            vitalSignSyncService.updateMeasurementUnits(company, organizationId);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        DatabaseInfo database = syncContext.getDatabase();
        long organizationId = organizationDao.getCompanyNewId(database, legacyIdString);
        organizationTelecomDao.delete(database, OrganizationType.COMPANY, legacyIdString);
        organizationAddressDao.delete(database, OrganizationType.COMPANY, legacyIdString);
        organizationHieConsentPolicyDao.deleteByOrganizationId(organizationId);
        organizationDao.delete(database, OrganizationType.COMPANY, legacyIdString);
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        DatabaseInfo database = context.getDatabase();
        final IdMapping<String> idMapping = organizationDao.getCompaniesIdMapping(database);
        context.putSharedObject(CompanyIdResolver.class, new CompanyIdResolver() {
            @Override
            public long getId(String legacyId, DatabaseInfo database) {
                return idMapping.getNewIdOrThrowException(legacyId);
            }
        });

        List<OrganizationAddress> addresses = organizationAddressDao.getAddresses(database, OrganizationType.COMPANY);
        final Map<Long, List<OrganizationAddress>> addressesMap = groupAddressesByOrganizations(addresses);

        context.putSharedObject(OrganizationAddressResolver.class, new OrganizationAddressResolver() {
            @Override
            public OrganizationAddress getCompanyAddress(long companyId) {
                List<OrganizationAddress> companyAddresses = addressesMap.get(companyId);

                if (companyAddresses == null || companyAddresses.isEmpty()) {
                    throw new IllegalStateException("Company id=" + companyId + " doesn't have an address");
                }

                if (companyAddresses.size() > 1) {
                    throw new IllegalStateException("More than one address entity has been found for company id="
                            + companyId + ". Currently only one address is allowed.");
                }

                return companyAddresses.get(0);
            }
        });
    }

    private Map<Long, List<OrganizationAddress>> groupAddressesByOrganizations(List<OrganizationAddress> addresses) {
        final Map<Long, List<OrganizationAddress>> addressesMap = new HashMap<Long, List<OrganizationAddress>>();
        for (OrganizationAddress address : addresses) {
            long organizationId = address.getOrganizationId();
            List<OrganizationAddress> organizationAddresses = addressesMap.get(organizationId);
            if (organizationAddresses == null) {
                organizationAddresses = new ArrayList<OrganizationAddress>();
                addressesMap.put(organizationId, organizationAddresses);
            }
            organizationAddresses.add(address);
        }
        return addressesMap;
    }

    private Organization createOrganization(CompanyData company, DatabaseInfo database, OrganizationForeignKeys foreignKeys) {
        Organization.Updatable updatable = createOrganizationUpdatable(company, foreignKeys);

        Organization organization = new Organization();
        organization.setLegacyId(company.getCode());
        organization.setDatabaseId(database.getId());
        organization.setLegacyTable(OrganizationType.COMPANY.getLegacyTableName());
        organization.setUpdatable(updatable);
        return organization;
    }

    private Organization.Updatable createOrganizationUpdatable(CompanyData company, OrganizationForeignKeys foreignKeys) {
        Organization.Updatable update = new Organization.Updatable();
        update.setName(company.getName());
        update.setLogoPictId(company.getLogoPictId());
        update.setProviderNpi(company.getProviderNpi());
        update.setSalesRegion(company.getSalesRegion());
        update.setTestingTraining(company.getTestingTrainingFacility());
        update.setInactive(company.getInactive());
        update.setModuleHie(company.getModuleHie());
        update.setModuleCloudStorage(company.getModuleCloudStorage());
        update.setResResuscitateCodeId(foreignKeys.getResResuscitateCodeId());
        update.setResAdvDir1CodeId(foreignKeys.getResAdvDir1CodeId());
        update.setResAdvDir2CodeId(foreignKeys.getResAdvDir2CodeId());
        update.setResAdvDir3CodeId(foreignKeys.getResAdvDir3CodeId());
        update.setResAdvDir4CodeId(foreignKeys.getResAdvDir4CodeId());
        update.setResCodeStat1CodeId(foreignKeys.getResCodeStat1CodeId());
        update.setResCodeStat2CodeId(foreignKeys.getResCodeStat2CodeId());
        update.setResCodeStat3CodeId(foreignKeys.getResCodeStat3CodeId());
        update.setResCodeStat4CodeId(foreignKeys.getResCodeStat4CodeId());
        return update;
    }

    private OrganizationTelecom createTelecom(CompanyData company, long organizationId, DatabaseInfo database) {
        OrganizationTelecom.Updatable updatable = createTelecomUpdate(company);

        OrganizationTelecom telecom = new OrganizationTelecom();
        telecom.setOrganizationId(organizationId);
        telecom.setUpdatable(updatable);
        telecom.setDatabaseId(database.getId());
        telecom.setLegacyTable(OrganizationType.COMPANY.getLegacyTableName());
        telecom.setLegacyId(company.getCode());
        return telecom;
    }

    private OrganizationTelecom.Updatable createTelecomUpdate(CompanyData company) {
        OrganizationTelecom.Updatable update = new OrganizationTelecom.Updatable();
        update.setValue(Utils.ensureLeadingPlusInPhoneNumberExists(company.getPhoneNumber()));
        update.setUseCode("WP");
        return update;
    }

    private OrganizationAddress createAddress(CompanyData company, long organizationId, DatabaseInfo database) {
        OrganizationAddress.Updatable updatable = createAddressUpdate(company);

        OrganizationAddress address = new OrganizationAddress();
        address.setOrganizationId(organizationId);
        address.setUpdatable(updatable);
        address.setDatabaseId(database.getId());
        address.setLegacyTable(OrganizationType.COMPANY.getLegacyTableName());
        address.setLegacyId(company.getCode());
        return address;
    }

    private OrganizationAddress.Updatable createAddressUpdate(CompanyData company) {
        OrganizationAddress.Updatable update = new OrganizationAddress.Updatable();
        update.setPostalAddressUse(null);
        update.setStreetAddress(company.getAddress1());
        update.setCity(company.getCity());
        update.setState(company.getState());
        update.setPostalCode(company.getZip());
        update.setCountry("US");
        return update;
    }

    private OrganizationHieConsentPolicy createOrganizationHieConsentPolicy(CompanyData company, long organizationId) {
        OrganizationHieConsentPolicy organizationHieConsentPolicy = new OrganizationHieConsentPolicy();
        organizationHieConsentPolicy.setArchived(false);
        organizationHieConsentPolicy.setLastModifiedDate(new Date());
        organizationHieConsentPolicy.setOrganizationId(organizationId);
        organizationHieConsentPolicy.setStatus("CREATED");
        String hieConsentPolicy = null;
        if (!StringUtils.isEmpty(company.getState())) {
            hieConsentPolicy = stateDao.getHieConsentPolicy(company.getState());
        }
        if (!StringUtils.isEmpty(hieConsentPolicy)) {
            organizationHieConsentPolicy.setType(hieConsentPolicy);
        } else {
            organizationHieConsentPolicy.setType("OPT_OUT");
        }
        return organizationHieConsentPolicy;
    }
}
