package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.OrganizationService;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.merging.MpiMergedResidentsService;
import com.scnsoft.eldermark.shared.ResidentFilterPhrAppDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.scnsoft.eldermark.services.carecoordination.CareCoordinationConstants.SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX;

/**
 * PHR service for create and update operations with {@link Resident} entity.
 *
 * @author phomal
 * Created on 9/5/2017.
 */
@Service
@Transactional
public class PhrResidentService {

    @Autowired
    private ResidentService residentService;

    @Autowired
    DatabasesService databasesService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    ContactService contactService;

    @Autowired
    MpiMergedResidentsService mpiMergedResidentsService;

    @Autowired
    HealthProviderService healthProviderService;

    @Autowired
    UserResidentRecordsDao userResidentRecordsDao;

    private static final String LEGACY_TABLE = "SCPHR_Resident";

    /**
     * Find any associated resident in Unaffiliated database
     */
    @Transactional(readOnly = true)
    public Resident findAssociatedResident(String ssn, String phone, String email, String firstName, String lastName) {
        final Database unaffiliated = databasesService.getUnaffiliatedDatabase();
        final Pageable top1 = new PageRequest(0, 1, Sort.Direction.ASC, "random");
        ResidentFilterPhrAppDto filter = new ResidentFilterPhrAppDto();
        filter.setDatabase(unaffiliated);
        filter.setSsn(ssn);
        filter.setPhone(phone);
        filter.setEmail(email);
        filter.setFirstName(firstName);
        filter.setLastName(lastName);
        List<Resident> residentList = residentService.getResidents(filter, top1);
        return residentList.isEmpty() ? null : residentList.get(0);
    }

    /**
     * Find associated resident in Unaffiliated organization
     */
    @Transactional(readOnly = true)
    public Resident findAssociatedResidentInUnaffiliated(Long userId) {
        final Database unaffiliated = databasesService.getUnaffiliatedDatabase();
        final Organization unaffiliatedOrg = organizationService.getUnaffiliatedOrganization(unaffiliated.getId());

        final Collection<Long> residentIds = userResidentRecordsDao.getAllResidentIdsByUserId(userId);
        List<Resident> residentList = residentService.filterResidentsByOrganization(residentIds, unaffiliatedOrg.getId());
        return residentList.isEmpty() ? null : residentList.get(0);
    }

    /**
     * Create new resident in Unaffiliated organization
     */
    public Resident createAssociatedResident(String email, String phone, String ssn, String firstName, String lastName, String legacyId,
                                             PersonAddress address) {
        final Database unaffiliated = databasesService.getUnaffiliatedDatabase();
        final Organization unaffiliatedOrg = organizationService.getUnaffiliatedOrganization(unaffiliated.getId());

        Resident resident = new Resident();
        resident.setDatabase(unaffiliated);
        resident.setDatabaseId(unaffiliated.getId());
        resident.setFacility(unaffiliatedOrg);
        resident.setProviderOrganization(unaffiliatedOrg);
        resident.setLegacyTable(LEGACY_TABLE);
        resident.setLegacyId(SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX + legacyId);

        final Person person = contactService.createPerson(unaffiliated, email, phone, PersonTelecomCode.HP, firstName, lastName, null);
        if (address != null) {
            address.setDatabase(unaffiliated);
            address.setDatabaseId(unaffiliated.getId());
            address.setPerson(person);
            person.setAddresses(Arrays.asList(address));
        }
        resident.setPerson(person);
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        resident.setSocialSecurity(ssn);
        resident.setSsnLastFourDigits(StringUtils.right(ssn, 4));

        resident = residentService.createResident(resident);
        updateLegacyIds(resident);
        return resident;
    }

    /**
     * Convenient shortcut method for {@link #createAssociatedResident(String, String, String, String, String, String, PersonAddress)}.
     * Additionally it makes the new resident merged with its original resident.
     */
    public Resident createAssociatedResidentFromUserData(User user) {
        final PersonAddress address = AddressService.cloneAddress(user.getPrimaryAddress());
        final Resident resident = createAssociatedResident(user.getResidentEmailLegacy(), user.getResidentPhoneLegacy(), user.getSsn(),
                user.getResidentFirstNameLegacy(), user.getResidentLastNameLegacy(), String.valueOf(user.getId()), address);
        if (user.getResident() != null) {
            mpiMergedResidentsService.createOrUpdateMpiMergedResidents(resident, user.getResident());
            final Collection<Long> residentIds = userResidentRecordsDao.getAllResidentIdsByUserId(user.getId());
            residentIds.add(resident.getId());
            healthProviderService.updateUserResidentRecords(user, new HashSet<>(residentIds), false);
        }
        return resident;
    }

    private static void updateLegacyIds(Resident resident) {
        Person person = resident.getPerson();
        ContactService.updateLegacyIds(person);
    }

}
