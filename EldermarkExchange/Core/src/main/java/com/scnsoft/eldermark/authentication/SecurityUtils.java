package com.scnsoft.eldermark.authentication;

import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.RoleCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SecurityUtils {
    public static ExchangeUserDetails getAuthenticatedUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("Invalid authentication token");
        } else {
            return (ExchangeUserDetails) authentication.getPrincipal();
        }
    }

    public static void updatePrincipal(Employee employee) {
        ExchangeUserDetails user = getAuthenticatedUser();
        Authentication auth = getAuthentication();

        ExchangeUserDetails newUser = new ExchangeUserDetails(employee, user.getAuthorities(), user.getLogoPaths(), user.getLinkedEmployees(), user.getAuthoritiesMap(), user.getEmployeeAuthoritiesMap(), user.isCredentialsNonExpired());
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(newUser, auth.getCredentials(), auth.getAuthorities());
        newAuth.setDetails(auth.getDetails());

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public static boolean isEldermarkUser() {
        return hasRole(RoleCode.ROLE_ELDERMARK_USER);
    }

    public static boolean isCloudUser() {
        return hasRole(RoleCode.ROLE_CLOUD_STORAGE_USER);
    }

    public static boolean isCloudManager() {
        return hasRole(RoleCode.ROLE_SCAN_SOL_MANAGER);
    }

    public static boolean isManager() {
        return hasRole(RoleCode.ROLE_MANAGER);
    }

    public static boolean isDirectManager() {
        return hasRole(RoleCode.ROLE_DIRECT_MANAGER);
    }

    private static Authentication getAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication();
    }

    public static boolean hasRole(RoleCode role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return false;
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role.getName()));
    }

    public static boolean hasRole(String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return false;
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(name));
    }

    public static boolean hasRole(Collection<GrantedAuthority> authorities, RoleCode role) {
        if (CollectionUtils.isEmpty(authorities))
            return false;
        return authorities.contains(new SimpleGrantedAuthority(role.getName()));
    }

    public static boolean hasRole(Collection<GrantedAuthority> authorities, String name) {
        if (CollectionUtils.isEmpty(authorities))
            return false;
        return authorities.contains(new SimpleGrantedAuthority(name));
    }

    public static Set<CareTeamRoleCode> getCareTeamRoleCodes() {
        Set<CareTeamRoleCode> result = new HashSet<CareTeamRoleCode>();
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            CareTeamRoleCode code = CareTeamRoleCode.getByCode(authority.getAuthority());
            if (code != null) {
                result.add(code);
            }
        }
        return result;
    }

    public static Set<CareTeamRoleCode> getCareTeamRoleCodes(Iterable<GrantedAuthority> authorities) {
        Set<CareTeamRoleCode> result = new HashSet<CareTeamRoleCode>();
        for (GrantedAuthority authority : authorities) {
            CareTeamRoleCode code = CareTeamRoleCode.getByCode(authority.getAuthority());
            if (code != null) {
                result.add(code);
            }
        }
        return result;
    }

    public static String getRemoteAddress() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof WebAuthenticationDetails) {
            return ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        } else {
            return null;
        }
    }

    public static boolean hasAnyRole(String... codes) {
        for (String code : codes) {
            if (SecurityUtils.hasRole(code)) return true;
        }
        return false;
    }

    public static boolean hasAnyRole(Collection<GrantedAuthority> authorities, String... codes) {
        for (String code : codes) {
            if (SecurityUtils.hasRole(authorities, code)) return true;
        }
        return false;
    }

    public static boolean hasAnyRole(RoleCode... codes) {
        for (RoleCode code : codes) {
            if (SecurityUtils.hasRole(code)) return true;
        }
        return false;
    }

    public static boolean hasAnyRole(Collection<GrantedAuthority> authorities, RoleCode... codes) {
        for (RoleCode code : codes) {
            if (SecurityUtils.hasRole(authorities, code)) return true;
        }
        return false;
    }

    public static void hasAnyRoleOrThrowException(String... codes) {
        boolean hasRole = hasAnyRole(codes);
        if (!hasRole) throw new AccessDeniedException("User do not have enough privileges for that operation");
    }

    public static void hasAnyRoleOrThrowException(RoleCode... codes) {
        boolean hasRole = hasAnyRole(codes);
        if (!hasRole) throw new AccessDeniedException("User do not have enough privileges for that operation");
    }

    public static String hasAnyRoleExpression(String... codes) {
        StringBuilder builder = new StringBuilder("hasAnyRole(");
        boolean first = true;
        for (String code : codes) {
            if (first) first = false;
            else builder.append(",");
            builder.append("'").append(code).append("'");
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * Checks if user view data not related to databases that he head access to. In case of affiliated view
     */
    public static boolean isAffiliatedView() {
        return !hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR) &&
                !getAuthenticatedUser().getCurrentAndLinkedDatabaseIds().contains(getAuthenticatedUser().getCurrentDatabaseId());
    }

    public static boolean isUnaffiliatedUser() {
        return !hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR) && getAuthenticatedUser().getEmployee().getDatabase().getOid() != null &&
                getAuthenticatedUser().getEmployee().getDatabase().getOid().equals("UNAFFILIATED"); //TODO use property unaffiliated.organization.oid
    }

    public static boolean isUnAffiliatedOrg() {
        // TODO use property unaffiliated.organization.oid
        return "UNAFFILIATED".equals(getAuthenticatedUser().getCurrentDatabaseOid());
    }

}