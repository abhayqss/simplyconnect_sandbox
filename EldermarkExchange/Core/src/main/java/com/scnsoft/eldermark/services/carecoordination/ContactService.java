package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by pzhurba on 29-Oct-15.
 */
@Transactional
public interface ContactService {

    Page<ContactListItemDto> list(ContactFilterDto contactFilter, Pageable pageRequest);
    //Page<ContactListItemDto> getContactListItemDto(final Set<Long> employeeIds, ContactFilterDto contactFilter, Pageable pageRequest);

    ContactDto getContact(final Long contactId);

    ContactDto createOrUpdate(final Employee creator, final ContactDto contact, final Database database, Boolean createdAutomatically);

    List<KeyValueDto> searchEmployee(Long databaseId, Long communityId, String searchString);

    List<KeyValueDto> getEmployeeSelectList(Long communityId, Long patientId, boolean affiliated, Set<Long> employeeIdsAvailableForPatient, Long careTeamMemberEmployeeId);

    void createResetPasswordToken(String login, String companyCode);

    List<KeyValueDto> getAffiliatedEmployees(Long communityId);

    List<LinkedContactDto> getLinkedEmployees(Long employeeId);

    List<Employee> getLinkedEmployeeEntities(Long employeeId);

    List<ContactDto> getContacts(final List<Long> contactIds);

    LinkedContactDto createLinkedEmployee(Employee employee, Employee employeeToLink);

    void deleteLinkedEmployee(Long linkedEmployeeIdToRemove, Long currentEmployeeId);

    Long getEmployeeCommunityId(long employeeId);

    List<CareTeamRoleDto> getCareTeamRolesToEdit(ContactDto contact);

    List<CareTeamRoleDto> getAllCareTeamRolesToEdit();

    boolean isValidContact(ContactDto dto);

    boolean isValidContact(Employee contact);

//    public List<KeyValueDto> getAffiliatedEmployeesForResident(Long residentId);
}
