package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.projection.AssociatedClientIdsAware;
import com.scnsoft.eldermark.mobile.dto.home.HomeSectionType;
import com.scnsoft.eldermark.mobile.dto.home.HomeSectionsDto;
import com.scnsoft.eldermark.mobile.dto.home.MedicationUpdateHomeSectionDto;
import com.scnsoft.eldermark.mobile.facade.home.CareTeamUpdateHomeSectionProvider;
import com.scnsoft.eldermark.mobile.facade.home.DocumentsHomeSectionProvider;
import com.scnsoft.eldermark.mobile.facade.home.MissedChatsAndCallsHomeSectionProvider;
import com.scnsoft.eldermark.service.CareTeamMemberService;
import com.scnsoft.eldermark.service.ClientCareTeamMemberModifiedService;
import com.scnsoft.eldermark.service.ContactService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class HomeFacadeImpl implements HomeFacade {
    private static final Logger logger = LoggerFactory.getLogger(HomeFacadeImpl.class);

    private static final int ITEMS_LIMIT = 15;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ContactService employeeService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private DocumentsHomeSectionProvider documentsHomeSectionProvider;

    @Autowired
    private MissedChatsAndCallsHomeSectionProvider missedChatsAndCallsHomeSectionProvider;

    @Autowired
    private CareTeamUpdateHomeSectionProvider careTeamUpdateHomeSectionProvider;

    @Autowired
    private ClientCareTeamMemberModifiedService clientCareTeamMemberModifiedService;

    @Override
    public HomeSectionsDto getSections(Set<HomeSectionType> sectionTypes) {
        var result = new HomeSectionsDto();

        if (CollectionUtils.isEmpty(sectionTypes)) {
            return result;
        }

        sectionTypes.forEach(section -> loadSectionData(section, result));
        return result;
    }

    private void loadSectionData(HomeSectionType homeSectionType, HomeSectionsDto result) {
        var currentEmployeeIdLazy = Lazy.of(() -> loggedUserService.getCurrentEmployeeId());
        var currentEmployeeClientIdsLazy = Lazy.of(
                () -> employeeService.findById(currentEmployeeIdLazy.get(), AssociatedClientIdsAware.class).getAssociatedClientIds()
        );
        var permissionFilter = Lazy.of(() -> permissionFilterService.createPermissionFilterForCurrentUser());

        switch (homeSectionType) {
            case DOCUMENTS:
                logger.info("Loading documents home section for user [{}]", currentEmployeeIdLazy.get());
                result.setDocuments(documentsHomeSectionProvider.loadDocuments(
                        currentEmployeeIdLazy.get(),
                        currentEmployeeClientIdsLazy.get(),
                        permissionFilter.get(),
                        ITEMS_LIMIT)
                );
                break;
            case CARE_TEAM_UPDATES:
                logger.info("Loading care team updates for user [{}], clients {}", currentEmployeeIdLazy.get(), currentEmployeeClientIdsLazy.get());
                result.setCareTeamUpdates(careTeamUpdateHomeSectionProvider.loadCareTeamUpdates(
                                currentEmployeeIdLazy.get(),
                                currentEmployeeClientIdsLazy.get(),
                                ITEMS_LIMIT
                        )
                );
                break;
            case MEDICATION_UPDATES:
                logger.info("Loading medication updates for user [{}], clients {}", currentEmployeeIdLazy.get(), currentEmployeeClientIdsLazy.get());
                result.setMedicationUpdates(loadMedications(currentEmployeeClientIdsLazy.get()));
                break;
            case MISSED_CHATS_AND_CALLS:
                logger.info("Loading documents home section for user [{}]", currentEmployeeIdLazy.get());
                result.setMissedChatsAndCalls(missedChatsAndCallsHomeSectionProvider.loadMissedChatsAndCalls(
                        currentEmployeeIdLazy.get(),
                        ITEMS_LIMIT)
                );
                break;
            default:
                throw new NotImplementedException("Unsupported section " + homeSectionType);
        }
    }

    private List<MedicationUpdateHomeSectionDto> loadMedications(Set<Long> clientIds) {
        return mockMedications();
    }

    private List<MedicationUpdateHomeSectionDto> mockMedications() {
        var dto1 = new MedicationUpdateHomeSectionDto();
        dto1.setUpdateId(1L);
        dto1.setMedicationId(5L);
        dto1.setName("Mocked new medication");
        dto1.setStatus(MedicationUpdateHomeSectionDto.Status.NEW);
        dto1.setDateTime(Instant.now().toEpochMilli());
        dto1.setClientId(12345L);

        var dto2 = new MedicationUpdateHomeSectionDto();
        dto2.setUpdateId(2L);
        dto2.setMedicationId(6L);
        dto2.setName("Mocked changed medication");
        dto2.setStatus(MedicationUpdateHomeSectionDto.Status.CHANGED);
        dto2.setDateTime(Instant.now().toEpochMilli());
        dto2.setClientId(12345L);

        var dto3 = new MedicationUpdateHomeSectionDto();
        dto3.setUpdateId(3L);
        dto3.setMedicationId(7L);
        dto3.setName("Mocked deleted medication");
        dto3.setStatus(MedicationUpdateHomeSectionDto.Status.DELETED);
        dto3.setDateTime(Instant.now().toEpochMilli());
        dto3.setClientId(12345L);

        return List.of(dto1, dto2, dto3);
    }

    @Override
    @Transactional
    public void readCareTeamMemberUpdates(Long careTeamMemberId) {
        clientCareTeamMemberModifiedService.careTeamMemberViewed(careTeamMemberId, loggedUserService.getCurrentEmployeeId());
    }
}
