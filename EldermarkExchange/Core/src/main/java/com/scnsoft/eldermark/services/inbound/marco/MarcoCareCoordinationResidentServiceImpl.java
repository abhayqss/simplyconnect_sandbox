package com.scnsoft.eldermark.services.inbound.marco;

import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationResidentJpaDao;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Conditional(MarcoInboundFilesServiceRunCondition.class)
public class MarcoCareCoordinationResidentServiceImpl implements MarcoCareCoordinationResidentService {

    private final CareCoordinationResidentJpaDao careCoordinationResidentJpaDao;

    @Autowired
    public MarcoCareCoordinationResidentServiceImpl(CareCoordinationResidentJpaDao careCoordinationResidentJpaDao) {
        this.careCoordinationResidentJpaDao = careCoordinationResidentJpaDao;
    }

    public List<CareCoordinationResident> getPatientDetailsByIdentityFields(MarcoDocumentMetadata metadata) {
        String firstName = metadata.getFirstName();
        String lastName = metadata.getLastName();
        if (StringUtils.isAllEmpty(firstName, lastName)) {
            Pair<String, String> firstLastNames = parseFullName(metadata.getFullName());
            firstName = firstLastNames.getFirst();
            lastName = firstLastNames.getSecond();
        }

        //todo rewrite with specifications
        if (StringUtils.isEmpty(metadata.getSsn())) {
            return careCoordinationResidentJpaDao.getAllByIdentityFields(
                    metadata.getOrganizationName(),
                    firstName,
                    lastName,
                    metadata.getDateOfBirth());
        }
        return careCoordinationResidentJpaDao.getAllByIdentityFields(
                metadata.getOrganizationName(),
                firstName,
                lastName,
                metadata.getDateOfBirth(),
                Normalizer.normalizePhone(metadata.getSsn()));
    }

    private Pair<String, String> parseFullName(String fullName) {
        if (fullName.contains(", ")) {
            String[] split = fullName.split(", ");
            if (split.length != 2) {
                throw new IllegalArgumentException("Unknown full name format: " + fullName);
            }
            return new Pair<>(split[1], split[0]);
        }
        throw new IllegalArgumentException("Unknown full name format: " + fullName);
    }

}
