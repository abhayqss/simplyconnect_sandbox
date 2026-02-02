package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientRecordSearchFilter;
import com.scnsoft.eldermark.beans.PharmacyFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatClientFilter;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.dto.assessment.AssessmentDefaultsDto;
import com.scnsoft.eldermark.dto.client.EmergencyContactListItemDto;
import com.scnsoft.eldermark.dto.client.HouseHoldMemberListItemDto;
import com.scnsoft.eldermark.dto.client.MedicalContactDto;
import com.scnsoft.eldermark.dto.client.ProspectivePrimaryContactDto;
import com.scnsoft.eldermark.dto.clientactivation.ClientActivationDto;
import com.scnsoft.eldermark.dto.clientactivation.ClientDeactivationDto;
import com.scnsoft.eldermark.dto.conversation.ConversationClientListItemDto;
import com.scnsoft.eldermark.dto.filter.ClientFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClientFacade {
    Page<ClientListItemDto> find(ClientFilterDto clientFilter, Pageable pageable);

    Page<ClientListItemDto> findRecords(ClientRecordSearchFilter clientFilter, Pageable pageable);

    ClientDto findById(Long id);

    Long add(ClientDto clientDto);

    Long edit(ClientDto clientDto);

    Long editEssentials(ClientEssentialsDto clientDto);

    List<EmergencyContactListItemDto> findEmergencyContacts(Long clientId, Pageable pageable);

    List<MedicalContactDto> findMedicalContacts(Long clientId);

    boolean canAdd(Long organizationId);

    ClientCommunityUniquenessDto validateUniqueInCommunity(Long clientId, Long communityId, String ssn, String medicareNumber, String medicaidNumber, String memberNumber);

    ClientOrganizationUniquenessDto validateUniqueInOrganization(Long clientId, Long organizationId, String email);

    List<DirectoryClientListItemDto> findClientsWithNonBlankNames(ClientFilterDto clientFilter);

    List<ClientNameDto> findNamesWithoutRecordSearchPermissions(ClientFilterDto clientFilter);

    List<Long> findNotViewableEventTypeIds(Long clientId);

    void toggleStatus(Long clientId);

    boolean isExistsAffiliatedCommunities(Long clientId);

    List<String> findClientPharmacyNames(PharmacyFilter filter);

    List<ConversationClientListItemDto> findChatAccessibleClients(AccessibleChatClientFilter filter);

    List<ClientNameCommunityIdListItemDto> findUnassociated(Long organizationId);

    ClientNameCommunityOrganizationDto findChatClient(Long clientId);

    Optional<IdAware> findByLoginCompanyIdAndLegacyId(String loginCompanyId, String legacyId);

    boolean canView(Long clientId);

    List<String> findInaccessibleClientProperties();

    List<HouseHoldMemberListItemDto> findHouseHoldMembers(Long clientId);

    void activateClient(Long clientId, ClientActivationDto dto);

    void deactivateClient(Long clientId, ClientDeactivationDto dto);

    AssessmentDefaultsDto assessmentDefaults(Long clientId, Long assessmentTypeId, Long parentAssessmentResultId);

    Long count(Long organizationId, Boolean canRequestSignature);

    List<ProspectivePrimaryContactDto> getProspectivePrimaryContacts(Long clientId);

    List<ClientNameBirthdayDto> findNonBlankNamesWithBirthdays(ClientFilterDto clientFilter);

    ClientTelecomDto findTelecom(Long clientId);
}
