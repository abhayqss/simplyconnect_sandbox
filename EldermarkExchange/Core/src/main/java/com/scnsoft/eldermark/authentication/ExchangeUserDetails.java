package com.scnsoft.eldermark.authentication;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

public class ExchangeUserDetails extends User {
    private final Employee employee;

    private Long currentDatabaseId;
    private String currentDatabaseOid;
    private String currentDatabaseName;

    private Pair<String, String> logoPaths;

    private List<Long> currentCommunityIds = new ArrayList<Long>();  //TODO check it! I guess it not working...

    //Expiry Data
    private List<Long> availableCommunityIds = null;
    private Date availableCommunityIdsActualDate = null;

    private List<LinkedContactDto> linkedEmployees;

    //Authorities grouped by database
    private Map<Long, Set<GrantedAuthority>> authoritiesMap;

    //Authorities grouped by employee id
    private Map<Long, Set<GrantedAuthority>> employeeAuthoritiesMap;

    private String newAccountToLinkToken;


    public ExchangeUserDetails(Employee employee, Collection<? extends GrantedAuthority> authorities, Pair<String, String> logoPaths,
                               List<LinkedContactDto> linkedEmployees, Map<Long, Set<GrantedAuthority>> authoritiesMap, String token, Map<Long, Set<GrantedAuthority>> employeeAuthoritiesMap, Boolean credentialsNonExpired) {
        super(employee.getLoginName(), employee.getPassword(), employee.getStatus().equals(EmployeeStatus.ACTIVE), true, credentialsNonExpired, true, authorities);
        this.employee = employee;
        this.currentDatabaseId = employee.getDatabase().getId();
        this.currentDatabaseName = employee.getDatabase().getName();
        this.logoPaths = logoPaths;
        this.linkedEmployees = linkedEmployees;
        this.authoritiesMap = authoritiesMap;
        this.newAccountToLinkToken = token;
        this.employeeAuthoritiesMap = employeeAuthoritiesMap;
//        for (GrantedAuthority authority:authorities) {
//            if (authority.getAuthority().equals(CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
//                currentCommunityIds.add(employee.getCommunityId());
//                break;
//            }
//        }
    }

    public ExchangeUserDetails(Employee employee, Collection<? extends GrantedAuthority> authorities, Pair<String, String> logoPaths,
                               List<LinkedContactDto> linkedEmployees, Map<Long, Set<GrantedAuthority>> authoritiesMap, Map<Long, Set<GrantedAuthority>> employeeAuthoritiesMap, Boolean credentialsNonExpired) {
        this(employee, authorities, logoPaths, linkedEmployees, authoritiesMap, null, employeeAuthoritiesMap, credentialsNonExpired);
    }

    public Long getCurrentDatabaseId() {
        return currentDatabaseId;
    }

    public String getCompanyCode() {
        return employee.getDatabase().getSystemSetup().getLoginCompanyId();
    }

    public void setCurrentDatabaseId(Database database) {
//        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            this.currentDatabaseId = database.getId();
            this.currentDatabaseName = database.getName();
            this.currentDatabaseOid = database.getOid();
            this.currentCommunityIds.clear();
//        }
    }

    public void setCurrentCommunityIdsForUser(List<Long> communityIds) {
        this.currentCommunityIds = communityIds;
    }

    public String getAlternativeDatabaseId() {
        return employee != null ? employee.getDatabaseAlternativeId() : null;
    }

    public Employee getEmployee() {
        return employee;
    }

    public String getEmployeeFirstName() {
        return employee != null ? employee.getFirstName() : null;
    }

    public String getEmployeeLastName() {
        return employee != null ? employee.getLastName() : null;
    }

    public String getEmployeeLogin() {
        return employee != null ? employee.getLoginName() : null;
    }

    public String getSecureMessaging() {
        return employee != null ? employee.getSecureMessaging() : null;
    }

    public Long getEmployeeId() {
        return employee != null ? employee.getId() : null;
    }

    public Long getCommunityId() {
        return employee != null ? employee.getCommunityId() : null;
    }

    public String getCurrentDatabaseName() {
        return currentDatabaseName;
    }

    public String getCurrentDatabaseOid() {
        return currentDatabaseOid;
    }

    public List<Long> getCurrentCommunityIds() {
        return currentCommunityIds;
    }

    public Pair<String, String> getLogoPaths() {
        return logoPaths;
    }

    public void setLogoPaths(Pair<String, String> logoPaths) {
        this.logoPaths = logoPaths;
    }


    public List<Long> getAvaliableCommunityIdsIfNotExpired() {
        if (availableCommunityIds==null) return null;
        if (new Date().getTime() > availableCommunityIdsActualDate.getTime() + 1000*60*3) return null;
        return availableCommunityIds;
    }

    public void setAvailableCommunityIds(List<Long> availableCommunityIds) {
        this.availableCommunityIds = availableCommunityIds;
        this.availableCommunityIdsActualDate = new Date();
    }

    public List<LinkedContactDto> getLinkedEmployees() {
        return linkedEmployees;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        if (currentDatabaseId == null) {
            return super.getAuthorities();
        } else if (!authoritiesMap.containsKey(currentDatabaseId)) {
            return authoritiesMap.get(employee.getDatabaseId());
        } else {
            return authoritiesMap.get(currentDatabaseId);
        }
    }

    public Map<Long, Set<GrantedAuthority>> getAuthoritiesMap() {
        return authoritiesMap;
    }

    public Set<Long> getCurrentAndLinkedDatabaseIds() {
        return authoritiesMap.keySet();
    }

    public Set<Long> getEmployeeAndLinkedEmployeeIds() {
        Set<Long> result = new HashSet<Long>();
        result.add(employee.getId());
        if (linkedEmployees != null) {
            for (LinkedContactDto linkedContactDto : linkedEmployees) {
                result.add(linkedContactDto.getId());
            }
        }
        return result;
    }

    public Set<Long> getEmployeeAndLinkedEmployeesCommunityIds() {
        Set<Long> result = new HashSet<Long>();
        result.add(employee.getCommunityId());
        if (linkedEmployees != null) {
            for (LinkedContactDto linkedContactDto : linkedEmployees) {
                result.add(linkedContactDto.getCommunityId());
            }
        }
        return result;
    }

    public Set<Long> getEmployeeCommunitiesForCurrentDatabase() {
        Set<Long> result = new HashSet<Long>();
        if (this.currentDatabaseId.equals(employee.getDatabaseId())) {
            result.add(employee.getCommunityId());
        }
        if (linkedEmployees != null) {
            for (LinkedContactDto linkedContactDto : linkedEmployees) {
                if (this.currentDatabaseId.equals(linkedContactDto.getDatabaseId())) {
                    result.add(linkedContactDto.getCommunityId());
                }
            }
        }
        return result;
    }

    public Set<Long> getEmployeeIdsForCurrentDatabase() {
        Set<Long> result = new HashSet<Long>();
        if (this.currentDatabaseId.equals(employee.getDatabaseId())) {
            result.add(employee.getId());
        }
        if (linkedEmployees != null) {
            for (LinkedContactDto linkedContactDto : linkedEmployees) {
                if (this.currentDatabaseId.equals(linkedContactDto.getDatabaseId())) {
                    result.add(linkedContactDto.getId());
                }
            }
        }
        return result;
    }

    public String getNewAccountToLinkToken() {
        return newAccountToLinkToken;
    }

    public Map<Long, Set<GrantedAuthority>> getEmployeeAuthoritiesMap() {
        return employeeAuthoritiesMap;
    }

    public LinkedContactDto getLinkedEmployeeById(Long id) {
        if (linkedEmployees != null) {
            for (LinkedContactDto linkedContactDto : linkedEmployees) {
                if (linkedContactDto.getId().equals(id)) {
                    return linkedContactDto;
                }
            }
        }
        return null;
    }

    public Set<GrantedAuthority> getAuthoritiesForDatabase(Long databaseId) {
        return authoritiesMap.get(databaseId);
    }
}
