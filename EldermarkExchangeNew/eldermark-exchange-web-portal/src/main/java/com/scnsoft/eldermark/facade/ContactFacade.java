package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.AppointmentContactFilter;
import com.scnsoft.eldermark.beans.ContactFilter;
import com.scnsoft.eldermark.beans.ContactNameFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatContactFilter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface ContactFacade {

    Long add(ContactDto contact);

    Long edit(ContactDto contact);

    Page<ContactListItemDto> find(ContactFilter contactFilter, Pageable pageRequest);

    ContactDto findById(Long contactId);

    void invite(Long contactId);

    Long count(ContactFilter contactFilter);

    Boolean validateUnique(String login, Long organizationId);

    List<IdentifiedNamedEntityDto> findNames(ContactNameFilter filter);

    List<IdentifiedNamedEntityDto> findChatAccessibleNamesWithChatEnabled(AccessibleChatContactFilter filter);

    List<ContactNameRoleDto> findNamesWithRoles(ContactNameFilter filter);

    List<NameRoleStatusCommunityDto> findAppointmentContacts(AppointmentContactFilter filter);

    LocationDto findAddressLocationById(Long contactId);

    List<RoleDto> getQaUnavailableRoles();
}
