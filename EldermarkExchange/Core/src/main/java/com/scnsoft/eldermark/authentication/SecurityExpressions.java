package com.scnsoft.eldermark.authentication;

import com.scnsoft.eldermark.entity.CareTeamRoleCode;

public class SecurityExpressions {
    private static final String ELDERMARK_USER = "ROLE_ELDERMARK_USER";
    private static final String DIRECT_MANAGER = "ROLE_DIRECT_MANAGER";
    private static final String SCAN_SOL_MANAGER = "ROLE_SCAN_SOL_MANAGER";

    public static final String IS_ELDERMARK_USER = "hasRole('" + ELDERMARK_USER + "')";
    public static final String IS_EXCHANGE_USER = "hasAnyRole('" + ELDERMARK_USER + "'," + CareTeamRoleCode.allRoles + ")";

    public static final String IS_DIRECT_MANAGER = "hasRole('" + DIRECT_MANAGER + "') " +
            "and hasRole('" + ELDERMARK_USER + "')";

    public static final String IS_ROLE_SCAN_SOL_MANAGER = "hasRole('" + SCAN_SOL_MANAGER + "')";

    public static final String IS_CC_USER = "hasAnyRole(" + CareTeamRoleCode.allRoles + ")";

    public static final String IS_CC_ADMIN = "hasAnyRole('" + CareTeamRoleCode.ADMINISTRATOR + "','"+CareTeamRoleCode.SUPER_ADMINISTRATOR+ "')";

    public static final String NOT_CC_ADMIN = "!hasAnyRole('" + CareTeamRoleCode.ADMINISTRATOR + "','"+CareTeamRoleCode.SUPER_ADMINISTRATOR+ "')";

    public static final String IS_CC_COMMUNITYADMIN = "hasAnyRole('" + CareTeamRoleCode.COMMUNITY_ADMINISTRATOR +"')";

    public static final String IS_CC_SUPERADMIN = "hasRole('" + CareTeamRoleCode.SUPER_ADMINISTRATOR+"')";

    public static final String IS_ROLES_CAN_VIEW_REPORTS = "hasAnyRole('" + CareTeamRoleCode.ADMINISTRATOR + "','"+CareTeamRoleCode.SUPER_ADMINISTRATOR+ "','"+CareTeamRoleCode.COMMUNITY_ADMINISTRATOR+ "','"+CareTeamRoleCode.CASE_MANAGER+ "','"+CareTeamRoleCode.CARE_COORDINATOR+ "','"+CareTeamRoleCode.PRIMARY_PHYSICIAN+ "','"+CareTeamRoleCode.BEHAVIORAL_HEALTH+ "','"+CareTeamRoleCode.SERVICE_PROVIDER+ "','"+CareTeamRoleCode.COMMUNITY_MEMBERS+ "')";

    public static final String NOT_ADMIN_OR_COMMUNITY_ADMIN = "!hasAnyRole('" + CareTeamRoleCode.ADMINISTRATOR + "','"+ CareTeamRoleCode.COMMUNITY_ADMINISTRATOR + "','" +CareTeamRoleCode.SUPER_ADMINISTRATOR+ "')";

    public static final String IS_LINKING_NEW_ACCOUNT = "hasRole('" + CareTeamRoleCode.LINKING_NEW_ACCOUNT+"')";

    public static final String IS_NOTIFY_USER = "hasRole('" + CareTeamRoleCode.ROLE_NOTIFY_USER+"')";

//    public static final String IS_ROLES_CAN_VIEW_CONTACTS= "hasAnyRole('" + CareTeamRoleCode.ADMINISTRATOR + "','"+CareTeamRoleCode.SUPER_ADMINISTRATOR+ "','"+CareTeamRoleCode.CASE_MANAGER+ "','"+CareTeamRoleCode.CARE_COORDINATOR+ "','"+CareTeamRoleCode.COMMUNITY_MEMBERS+ "','"+CareTeamRoleCode.COMMUNITY_MEMBERS+ "')";
}
