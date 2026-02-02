package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ProspectDeactivationReason;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.ProspectDao;
import com.scnsoft.eldermark.dao.ProspectHistoryDao;
import com.scnsoft.eldermark.dao.specification.ProspectSpecificationGenerator;
import com.scnsoft.eldermark.dto.ProspectSaveData;
import com.scnsoft.eldermark.dto.prospect.ProspectActivationDto;
import com.scnsoft.eldermark.dto.prospect.ProspectDeactivationDto;
import com.scnsoft.eldermark.dto.prospect.ProspectFilter;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.entity.prospect.history.ProspectHistory;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProspectServiceImpl implements ProspectService {

    @Autowired
    private ProspectSpecificationGenerator prospectSpecificationGenerator;

    @Autowired
    private ProspectDao prospectDao;

    @Autowired
    private ProspectHistoryDao prospectHistoryDao;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private Converter<Prospect, ProspectHistory> prospectHistoryConverter;

    @Override
    public ProspectHistory createHistoryRecord(Long id) {
        var prospect = findById(id);
        var prospectHistory = prospectHistoryConverter.convert(prospect);
        return prospectHistoryDao.save(prospectHistory);
    }

    @Override
    public Prospect save(Prospect prospect) {
        validateProspect(prospect);
        return prospectDao.save(prospect);
    }

    @Override
    public Prospect save(ProspectSaveData data) {
        avatarService.update(data.getProspectAvatar());
        if (data.getSecondOccupantAvatar() != null) {
            avatarService.update(data.getSecondOccupantAvatar());
        }
        return save(data.getEntity());
    }

    @Override
    @Transactional(readOnly = true)
    public Prospect findById(Long id) {
        return prospectDao.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND));
    }

    @Override
    public <T> Page<T> find(ProspectFilter filter, Pageable pageable, Class<T> projectionClass) {
        return prospectDao.findAll(prospectSpecificationGenerator.byFilter(filter), projectionClass, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdAware> findByCommunityIdAndExternalId(Long communityId, Long externalId) {
        var byCommunityId = prospectSpecificationGenerator.byCommunityId(communityId);
        var byExternalId = prospectSpecificationGenerator.byExternalId(externalId);
        return prospectDao.findFirst(byCommunityId.and(byExternalId), IdAware.class);
    }

    @Override
    public void activateProspect(Long prospectId, ProspectActivationDto dto) {
        prospectDao.activateProspect(
                prospectId, dto.getComment(), Instant.now()
        );
    }

    @Override
    public void deactivateProspect(Long prospectId, ProspectDeactivationDto dto) {
        var prospectDeactivationReason =
                ProspectDeactivationReason.fromValue(dto.getDeactivationReason());
        prospectDao.deactivateProspect(
                prospectId, prospectDeactivationReason, dto.getComment(), Instant.now()
        );
    }

    @Override
    public Boolean isValidSsn(Long prospectId, Long communityId, String ssn) {
        if (StringUtils.isNotEmpty(ssn)) {
            if (prospectId != null) {
                var ssnNotChanged = prospectDao.existsByIdAndSocialSecurity(prospectId, ssn);
                return ssnNotChanged || !prospectDao.existsByIdNotAndCommunityIdAndSocialSecurity(prospectId, communityId, ssn);
            }
            return !prospectDao.existsByAndCommunityIdAndSocialSecurity(communityId, ssn);
        }
        return null;
    }

    @Override
    public Boolean isEmailUnique(Long prospectId, Long organizationId, String email) {
        if (StringUtils.isNotEmpty(email)) {
            return Optional.ofNullable(prospectId).map(id -> !prospectDao.existsEmailInOrganizationAndIdNot(email, organizationId, id))
                    .orElse(!prospectDao.existsEmailInOrganization(email, organizationId));
        }
        return null;
    }

    @Override
    public <P> P findById(Long id, Class<P> projection) {
        return prospectDao.findById(id, projection).orElseThrow();
    }

    @Override
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return prospectDao.findByIdIn(ids, projection);
    }

    private void validateProspect(Prospect prospect) {
        if (!prospect.getOrganizationId().equals(prospect.getCommunity().getOrganizationId())) {
            throw new BusinessException("Prospect community is not in organization.");
        }

        var prospectEmail = Optional.ofNullable(prospect.getPerson().getTelecoms()).orElseGet(ArrayList::new).stream()
                .filter(personTelecom -> personTelecom.getUseCode() != null && personTelecom.getUseCode().equals(PersonTelecomCode.EMAIL.name())).findFirst();
        if (prospectEmail.isPresent() && Boolean.FALSE.equals(isEmailUnique(prospect.getId(), prospect.getOrganizationId(), prospectEmail.get().getValue()))) {
            throw new BusinessException("Email should be unique within the organization.");
        }

        if (Boolean.FALSE.equals(isValidSsn(prospect.getId(), prospect.getCommunity().getId(), prospect.getSocialSecurity()))) {
            throw new BusinessException("SSN should be unique within the community.");
        }

        if (prospect.getBirthDate().isAfter(LocalDate.now())) {
            throw new BusinessException("Birth date can not be in future.");
        }
    }
}
