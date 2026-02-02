package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.ResidentAdmittanceHistoryDao;
import com.scnsoft.eldermark.consana.sync.server.dao.ResidentDao;
import com.scnsoft.eldermark.consana.sync.server.model.ResidentIdentifyingData;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Name;
import com.scnsoft.eldermark.consana.sync.server.model.entity.PersonAddress;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.model.entity.ResidentAdmittanceHistory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.scnsoft.eldermark.consana.sync.server.utils.ConsanaSyncServerUtils.*;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Service
@Transactional(noRollbackFor = Exception.class)
public class ResidentServiceImpl implements ResidentService {

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private ResidentAdmittanceHistoryDao residentAdmittanceHistoryDao;

    @Override
    public Optional<Resident> find(ResidentIdentifyingData residentIdentifyingData) {
        var residentByXrefId = residentDao.findByConsanaXrefIdAndFacilityId(residentIdentifyingData.getConsanaXrefId(),
                residentIdentifyingData.getCommunityId());

        return residentByXrefId.or(
                () -> residentDao.findOne(byIdentifyingData(residentIdentifyingData)));
    }

    private Specification<Resident> byIdentifyingData(ResidentIdentifyingData residentIdentifyingData) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("facility").get("id"), residentIdentifyingData.getCommunityId()),
                criteriaBuilder.equal(root.get("firstName"), residentIdentifyingData.getFirstName()),
                criteriaBuilder.equal(root.get("lastName"), residentIdentifyingData.getLastName()),
                criteriaBuilder.equal(root.get("birthDate"), residentIdentifyingData.getBirthDate()),
                StringUtils.isEmpty(residentIdentifyingData.getSsn()) ?
                        criteriaBuilder.and() :
                        criteriaBuilder.equal(root.get("socialSecurity"), residentIdentifyingData.getSsn())
        );
    }

    @Override
    public Resident updateEmptyFields(Resident target, Resident source) {
        if (target.getConsanaXrefId() == null) target.setConsanaXrefId(source.getConsanaXrefId());
        if (target.getFacility() == null) target.setFacility(source.getFacility());
        if (target.getBirthDate() == null) target.setBirthDate(source.getBirthDate());
        if (target.getAdmitDate() == null && source.getAdmitDate() != null
                && residentAdmittanceHistoryDao.countByResidentId(target.getId()) == 0) {
            target.setAdmitDate(source.getAdmitDate());
            updateAdmitHistory(target, source.getAdmitDate());
        }
        if (target.getDeathDate() == null) target.setDeathDate(source.getDeathDate());
        if (isNotTrue(target.getDeathIndicator())) target.setDeathIndicator(source.getDeathIndicator());
        if (target.getGender() == null) target.setGender(source.getGender());
        if (target.getMaritalStatus() == null) target.setMaritalStatus(source.getMaritalStatus());
        if (target.getEthnicGroup() == null) target.setEthnicGroup(source.getEthnicGroup());
        if (target.getReligion() == null) target.setReligion(source.getReligion());
        if (target.getCreatedById() == null) target.setCreatedById(source.getCreatedById());
        if (target.getSocialSecurity() == null) target.setSocialSecurity(source.getSocialSecurity());
        if (target.getIsOptOut() == null) target.setIsOptOut(source.getIsOptOut());
        if (target.getRace() == null) target.setRace(source.getRace());
        if (target.getCitizenship() == null) target.setCitizenship(source.getCitizenship());
        if (target.getMedicareNumber() == null) target.setMedicareNumber(source.getMedicareNumber());
        if (target.getMedicaidNumber() == null) target.setMedicaidNumber(source.getMedicaidNumber());
        if (target.getMedicalRecordNumber() == null) target.setMedicalRecordNumber(source.getMedicalRecordNumber());
        if (target.getMemberNumber() == null) target.setMemberNumber(source.getMemberNumber());

        if (target.getDateCreated() == null) target.setDateCreated(source.getDateCreated());
        if (target.getLastUpdated() == null) target.setLastUpdated(source.getLastUpdated());
        if (target.getFirstName() == null) target.setFirstName(source.getFirstName());
        if (target.getMiddleName() == null) target.setMiddleName(source.getMiddleName());
        if (target.getLastName() == null) target.setLastName(source.getLastName());
        return target;
    }

    @Override
    public Resident create(Resident res) {
        res = saveResident(res);
        updateAdmitHistory(res, res.getAdmitDate());
        return res;
    }

    @Override
    public Resident update(Resident res) {
        return saveResident(res);
    }

    private Resident updateLegacyIds(Resident resident) {
        updateLegacyId(resident, createLegacyId(resident));
        updateLegacyId(resident.getPerson(), createLegacyId(resident.getPerson()));

        List<PersonAddress> personAddresses = resident.getPerson().getAddresses();
        if (isNotEmpty(personAddresses)) {
            for (PersonAddress personAddress : personAddresses) {
                String newLegacyId = createLegacyIdFromParent(personAddress, resident.getPerson());
                updateLegacyId(personAddress, newLegacyId);
            }
        }

        List<Name> personNames = resident.getPerson().getNames();
        if (isNotEmpty(personNames)) {
            for (Name name : personNames) {
                String newLegacyId = createLegacyIdFromParent(name, resident.getPerson());
                updateLegacyId(name, newLegacyId);
            }
        }
        return resident;
    }

    private void updateAdmitHistory(Resident resident, Instant admitDate) {
        ofNullable(admitDate).ifPresent(date -> {
            final ResidentAdmittanceHistory admittanceHistory = new ResidentAdmittanceHistory();
            admittanceHistory.setAdmitDate(admitDate);
            admittanceHistory.setResident(resident);
            admittanceHistory.setOrganizationId(resident.getFacility().getId());
            admittanceHistory.setDatabase(resident.getDatabase());
            admittanceHistory.setLegacyId(residentAdmittanceHistoryDao.getMaxId() + 1);
            residentAdmittanceHistoryDao.save(admittanceHistory);
        });
    }

    private Resident saveResident(Resident resident) {
        resident = updateLegacyIds(resident);
        resident = residentDao.saveAndFlush(resident);
        return resident;
    }
}
