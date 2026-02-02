package com.scnsoft.eldermark.security;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.dao.RoleDao;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Role;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.carecoordination.ContactService;
import com.scnsoft.eldermark.services.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

public class ExchangeUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ContactService contactService;

    @Autowired
    private EmployeePasswordSecurityService employeePasswordSecurityService;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException, DataAccessException
    {
        String[] split = input.split(ExtraParamAuthenticationFilter.getDelimiter());
        if(split.length < 2) {
            throw new UsernameNotFoundException("Both username and company ID must be selected");
        }

        String companyId = split[0];
        String username = split[1];
        Boolean linkNew = split.length > 2 ? BooleanUtils.toBoolean(split[2]) : Boolean.FALSE;
        String token = null;
        if (linkNew) {
            if(split.length < 4) {
                throw new UsernameNotFoundException("Token of new user should be presented");
            }
            token = split[3];
        }

        Employee employee = employeeService.getActiveEmployee(username, companyId);

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.addAll(loadUserAuthorities(employee.getId()));
        authorities.addAll(loadGroupAuthorities(employee.getId()));
        authorities.addAll(loadUserCommunityAuthorities(employee.getId()));
        authorities.addAll(loadGroupCommunityAuthorities(employee.getId()));

        if (employee.getCareTeamRole() != null) {
            authorities.add(new SimpleGrantedAuthority(employee.getCareTeamRole().getCode().name()));
        }

        if (linkNew) {
            authorities.add(new SimpleGrantedAuthority(CareTeamRoleCode.LINKING_NEW_ACCOUNT));
        }

        Map<Long, Set<GrantedAuthority>> authoritiesMap = new HashMap<Long, Set<GrantedAuthority>>();
        Map<Long, Set<GrantedAuthority>> employeeAuthoritiesMap = new HashMap<Long, Set<GrantedAuthority>>();
        authoritiesMap.put(employee.getDatabaseId(), new HashSet<GrantedAuthority>(authorities));
        employeeAuthoritiesMap.put(employee.getId(), new HashSet<GrantedAuthority>(authorities));

        Set<Long> employeeAndLinkedEmployeeIds = new HashSet<Long>();
        employeeAndLinkedEmployeeIds.add(employee.getId());
        List<LinkedContactDto> linkedEmployees =  contactService.getLinkedEmployees(employee.getId());
        if (CollectionUtils.isNotEmpty(linkedEmployees)) {
            for (LinkedContactDto linkedContact :  linkedEmployees) {
                employeeAndLinkedEmployeeIds.add(linkedContact.getId());
                if (!authoritiesMap.containsKey(linkedContact.getDatabaseId())) {
                    authoritiesMap.put(linkedContact.getDatabaseId(), new HashSet<GrantedAuthority>());
                }
                if (!employeeAuthoritiesMap.containsKey(linkedContact.getId())) {
                    employeeAuthoritiesMap.put(linkedContact.getId(), new HashSet<GrantedAuthority>());
                }
                Set<GrantedAuthority> linkedAuthorities = new HashSet<GrantedAuthority>();
                linkedAuthorities.addAll(loadUserAuthorities(linkedContact.getId()));
                linkedAuthorities.addAll(loadGroupAuthorities(linkedContact.getId()));
                linkedAuthorities.addAll(loadUserCommunityAuthorities(linkedContact.getId()));
                linkedAuthorities.addAll(loadGroupCommunityAuthorities(linkedContact.getId()));
                if (linkedContact.getCareTeamRoleCodeName() != null) {
                    linkedAuthorities.add(new SimpleGrantedAuthority(linkedContact.getCareTeamRoleCodeName()));
                }
                authoritiesMap.get(linkedContact.getDatabaseId()).addAll(linkedAuthorities);
                employeeAuthoritiesMap.get(linkedContact.getId()).addAll(linkedAuthorities);
            }
        }

        Pair<String, String> logoPaths = employeeService.getCompanyLogosForEmployee(employee.getId(), employee.getDatabaseId(), employeeAndLinkedEmployeeIds);
        Boolean expired = !BooleanUtils.isTrue(employee.getContact4d()) && employeePasswordSecurityService.isPasswordExpired(employee);

        return new ExchangeUserDetails(employee, authorities, logoPaths, linkedEmployees, authoritiesMap, token, employeeAuthoritiesMap, !expired);
    }

    protected List<GrantedAuthority> loadGroupAuthorities(long employeeId) {
        List<Role> roles = roleDao.getEmployeeGroupRoles(employeeId);
        return convertRolesToAuthorities(roles);
    }

    protected List<GrantedAuthority> loadUserAuthorities(long employeeId) {
        List<Role> roles = roleDao.getEmployeeRoles(employeeId);
        return convertRolesToAuthorities(roles);
    }

    protected List<GrantedAuthority> loadUserCommunityAuthorities(long employeeId) {
        List<Role> roles = roleDao.getEmployeeOrganizationRoles(employeeId);
        return convertRolesToAuthorities(roles, "_COMMUNITY");
    }
    protected List<GrantedAuthority> loadGroupCommunityAuthorities(long employeeId) {
        List<Role> roles = roleDao.getEmployeeOrganizationGroupRoles(employeeId);
        return convertRolesToAuthorities(roles, "_COMMUNITY");
    }

    private List<GrantedAuthority> convertRolesToAuthorities(List<Role> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for(Role role: roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getCode().getName()));
        }
        return  grantedAuthorities;
    }

    private List<GrantedAuthority> convertRolesToAuthorities(List<Role> roles, String postfix) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for(Role role: roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getCode().getName() + postfix));
        }
        return  grantedAuthorities;
    }
}
