package com.scnsoft.eldermark.services.converters;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.dao.ResidentJpaDao;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.predicates.ConsanaCommunityIntegrationEnabledPredicate;
import com.scnsoft.eldermark.shared.ConsanaXrefPatientIdDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ConsanaXrefPatientDtoToResidentConverter implements Converter<ConsanaXrefPatientIdDto, Optional<Resident>> {

    private final ConsanaCommunityIntegrationEnabledPredicate consanaCommunityIntegrationEnabledPredicate;
    private final ResidentJpaDao residentJpaDao;

    @Autowired
    public ConsanaXrefPatientDtoToResidentConverter(ConsanaCommunityIntegrationEnabledPredicate consanaCommunityIntegrationEnabledPredicate, ResidentJpaDao residentJpaDao) {
        this.consanaCommunityIntegrationEnabledPredicate = consanaCommunityIntegrationEnabledPredicate;
        this.residentJpaDao = residentJpaDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Resident> convert(ConsanaXrefPatientIdDto consanaXrefPatientIdDto) {
        if (isEmpty(consanaXrefPatientIdDto)) {
            return Optional.absent();
        }
        final Optional<Resident> residentOptional = residentJpaDao.findFirstByConsanaXrefIdAndDatabaseOidAndFacilityOid(
                consanaXrefPatientIdDto.getIdentifier(),
                consanaXrefPatientIdDto.getOrganizationOID(),
                consanaXrefPatientIdDto.getCommunityOID());
        if (residentOptional.isPresent() && consanaCommunityIntegrationEnabledPredicate.apply(residentOptional.get().getFacility())) {
            return Optional.of(residentOptional.get());
        }
        return Optional.absent();
    }

    private boolean isEmpty(ConsanaXrefPatientIdDto consanaXrefPatientDto) {
        return consanaXrefPatientDto == null
                || StringUtils.isBlank(consanaXrefPatientDto.getIdentifier())
                || StringUtils.isBlank(consanaXrefPatientDto.getOrganizationOID())
                || StringUtils.isBlank(consanaXrefPatientDto.getCommunityOID());
    }
}
