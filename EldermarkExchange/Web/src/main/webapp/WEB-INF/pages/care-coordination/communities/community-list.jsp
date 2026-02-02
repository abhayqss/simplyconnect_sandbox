<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@ page import="com.scnsoft.eldermark.entity.CareTeamRole" %>
<%@ page import="com.scnsoft.eldermark.entity.CareTeamRoleCode" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<lt:layout cssClass="patientListBox panel panel-primary">
    <lt:layout cssClass="boxHeader panel panel-heading">
       Communities
    </lt:layout>

    <c:if test="${not affiliatedView}">
        <lt:layout style="padding:10px 25px; " cssClass="col-md-12 filterPanel">
            <label class="ldr-ui-label">${organizationName}</label>
            <wgForm:form role="form" id="communityFilterForm" commandName="communityFilter">
                <lt:layout cssClass="col-md-2" style="width:400px;padding-left:0px;">
                    <wgForm:input path="name"
                                  id="filter.name"
                                  cssClass="form-control" cssStyle="height: 34px"
                    />
                </lt:layout>
                <lt:layout cssClass="col-md-2" style="width:100px">
                    <wg:button name="searchCommunity"
                               id="searchCommunity"
                               domType="button"
                               dataToggle="modal"
                               cssClass="btn-primary pull-right">
                        SEARCH
                    </wg:button>
                </lt:layout>
            </wgForm:form>
                <c:if test="${canAddCommunity}">
                    <wg:button name="createCommunity"
                               id="createCommunity"
                               domType="button"
                               dataToggle="modal"
                               cssClass="btn-primary pull-right">
                        ADD NEW COMMUNITY
                    </wg:button>
                </c:if>
        </lt:layout>
    </c:if>

    <lt:layout cssClass="boxBody">
        <wg:grid id="communitiesList" cssClass="communitiesList"
                 colIds="name, oid, createdAutomatically, lastModified"
                 colNames="Name, Community OID, Created Automatically, Modified On"
                 colFormats="string, string, custom, date"
                 dataUrl="care-coordination/communities"
                 dataRequestMethod="GET"
                 deferLoading="true"
                />
    </lt:layout>
</lt:layout>