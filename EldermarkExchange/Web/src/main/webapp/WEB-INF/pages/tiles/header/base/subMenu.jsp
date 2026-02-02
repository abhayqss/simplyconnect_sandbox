<%@ page import="com.scnsoft.eldermark.entity.CareTeamRoleCode" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="care-coordination/templates/organizations" var="organizationModuleUrl"/>
<c:url value="/care-coordination/templates/organizations" var="organizationUrl"/>

<div id="subMenu" class="sub-menu" >
    <lt:layout cssClass="ldr-center-block baseHeader">
        <div class="row">
            <div id="" name="" style="" class="col-md-12 col-xs-12">
                <div class="current-community-tab-panel ">
                    <lt:layout cssClass="table-cell-box" style="width:1px"></lt:layout>
                    <c:if test="${showOrganizationFilter}">
                        <lt:layout id="subMenuOrgLabel" cssClass="current-organization-label table-cell-box">
                            <label>Organization:</label>
                            <span id="testWidthSpan" style="display:none"></span>
                        </lt:layout>
                        <lt:layout id="subMenuOrgInput" cssClass="table-cell-box">
                                <select id="availableOrganizationChooser" class="selectpicker current-organization-input form-control"  data-size="25" data-width="300px">
                                <option selected="">Not Chosen</option>
                            </select>
                        </lt:layout>
                    </c:if>
                    <%--<sec:authorize access="<%=CareTeamRoleCode.IS_NOT_COMMUNITY_ADMINISTRATOR %>" >--%>
                    <lt:layout id="subMenuCommunityLabel" cssClass="current-community-label table-cell-box">
                        <label>Community:</label>
                    </lt:layout>
                    <lt:layout id="subMenuCommunityInput" cssClass="table-cell-box " >
                        <select id="availableCommunitiesChooser" multiple class="selectpicker" data-width="350px">
                        </select>
                    </lt:layout>
                    <%--</sec:authorize>--%>
                    <%--<sec:authorize access="<%=CareTeamRoleCode.IS_SUPER_ADMINISTRATOR %>" >--%>
                        <%--<lt:layout id="subMenuManageOrgs" cssClass="manage-org-link table-cell-box text-right">--%>
                            <%--<a--%>
                                    <%--href="${organizationUrl}"--%>
                                    <%-- data-toggle="ajaxtab"--%>
                                    <%-- data-target="#organizationsTabContent"--%>
                                    <%--data-ajax-load="true"--%>
                                    <%--ajaxUrlParams="navigate=link"--%>
                                    <%--data-ajax-url-tmpl="${organizationModuleUrl}"--%>
                                    <%--is-manage-org-link="true"--%>
                                    <%--id="organizationsTabPanel"--%>
                                    <%-->Manage Organizations</a>--%>
                        <%--</lt:layout>--%>
                    <%--</sec:authorize>--%>
                </div>
            </div>
        </div>
    </lt:layout>
</div>