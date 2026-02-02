<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="/resources/images/logo.svg" var="logoImgUrl"/>
<c:url value="/resources/images/sheet.gif" var="sheetImgUrl"/>
<c:url value="/resources/images/sheet.gif" var="envelope"/>
<spring:message var="insuranceInputPlaceholder" code="marketplace.filter.insurance.input.placeholder"/>
<spring:message var="primaryFocusLabel" code="marketplace.filter.primaryFocus.label"/>
<spring:message var="communityTypeLabel" code="marketplace.filter.communityType.label"/>
<spring:message var="servicesLabel" code="marketplace.filter.services.label"/>
<spring:message var="insuranceCarrierLabel" code="marketplace.filter.insuranceCarrier.label"/>

<%--<lt:layout cssClass="event-log">--%>
<div id="market-place-filter-content">
    <lt:layout cssClass="filterPanel">
        <lt:layout cssClass="boxHeader panel panel-primary">
            <lt:layout cssClass="panel-heading">
                Find Location / Service / Community
            </lt:layout>
        </lt:layout>

        <wgForm:form role="form" id="marketplaceFilterFrom" action = "marketplace" commandName="marketplaceFilter">
            <wgForm:hidden path="searchText" value="${searchText}"/>
            <wgForm:hidden path="pageNumber" />
            <wgForm:hidden path="initLatitude" />
            <wgForm:hidden path="initLongitude" />
            
            <lt:layout cssClass="col-md-3">
                <c:set var="primaryFocusesEmpty" value="${empty marketplaceFilter.primaryFocusIds}" />
                <div class="filter-label">${primaryFocusLabel}</div>
                <wgForm:select path="primaryFocusIds"
                               id="orgPrimaryFocus"
                               cssClass="form-control spicker"
                               multiple="true">
                    <option data-hidden="true"></option>
                    <c:choose>
                        <c:when test="${primaryFocusesEmpty}">
                            <wgForm:option value="0" label="All" selected="true"/>
                        </c:when>
                        <c:otherwise>
                            <wgForm:option value="0" label="All" />
                        </c:otherwise>
                    </c:choose>

                    <c:forEach var="item" items="${primaryFocuses}">
                        <wgForm:option value="${item.id}" label="${item.label}"/>
                    </c:forEach>
                </wgForm:select>
            </lt:layout>

            <lt:layout cssClass="col-md-2">
                <c:set var="communityTypesEmpty" value="${empty marketplaceFilter.communityTypeIds}" />
                <div class="filter-label">${communityTypeLabel}</div>
                <wgForm:select path="communityTypeIds"
                               id="orgCommunityType"
                               class="form-control spicker"
                               multiple="true">
                    <option data-hidden="true"></option>
                    <c:choose>
                        <c:when test="${communityTypesEmpty}">
                            <wgForm:option value="0" label="All" selected="true"/>
                        </c:when>
                        <c:otherwise>
                            <wgForm:option value="0" label="All" />
                        </c:otherwise>
                    </c:choose>
                    <c:forEach var="item" items="${communityTypes}">
                        <wgForm:option value="${item.id}" label="${item.label}"/>
                    </c:forEach>
                </wgForm:select>
            </lt:layout>

            <lt:layout cssClass="col-md-2">
                <c:set var="servicesEmpty" value="${empty marketplaceFilter.serviceIds}" />
                <div class="filter-label">${servicesLabel}</div>
                <wgForm:select path="serviceIds"
                               id="serviceId"
                               cssClass="form-control spicker"
                               multiple="true">
                    <option data-hidden="true"></option>
                    <c:choose>
                        <c:when test="${servicesEmpty}">
                            <wgForm:option value="0" label="All" selected="true"/>
                        </c:when>
                        <c:otherwise>
                            <wgForm:option value="0" label="All" />
                        </c:otherwise>
                    </c:choose>
                    <c:forEach var="item" items="${services}">
                        <wgForm:option value="${item.id}" label="${item.label}"/>
                    </c:forEach>
                </wgForm:select>
            </lt:layout>

            <%--<lt:layout id="careTeamRoleSelectBox" cssClass="col-md-2">--%>
                <%--<c:set var="servicesEmpty" value="${empty marketplaceFilter.serviceIds}" />--%>
                <%--<wg:label _for="careTeamRoleSelect">${servicesLabel}</wg:label>--%>
                <%--<wgForm:select path="serviceIds" id="serviceId" name="serviceIds" cssClass="form-control" multiple="true"--%>
                <%-->--%>
                    <%--<option data-hidden="true"></option>--%>
                    <%--<c:choose>--%>
                        <%--<c:when test="${servicesEmpty}">--%>
                            <%--<wgForm:option value="0" label="All services" selected="true"/>--%>
                        <%--</c:when>--%>
                        <%--<c:otherwise>--%>
                            <%--<wgForm:option value="0" label="All services"/>--%>
                        <%--</c:otherwise>--%>
                    <%--</c:choose>--%>
                    <%--<c:forEach var="item" items="${services}">--%>
                        <%--<wgForm:option value="${item.id}" label="${item.label}"/>--%>
                    <%--</c:forEach>--%>
                <%--</wgForm:select>--%>
            <%--</lt:layout>--%>

            <lt:layout cssClass="col-md-3 insurance-filter">
                <div class="filter-label">${insuranceCarrierLabel}</div>
                <wg:combobox-wizard id="insuranceFilter" firstValuePath="inNetworkInsuranceId" secondValuePath="insurancePlanId"
                                    firstTabTitle="Choose carrier"
                                    firstTabTitleSection1="Popular carriers"
                                    firstTabTitleSection2="All carriers"
                                    secondTabTitle="Choose plan"
                                    secondTabTitleSection1="Popular plans"
                                    secondTabTitleSection2="All plans"
                                    chooseDifferentValue="Choose a different insurance"
                                    firstTabAllOptions="${allInNetworkInsurances}"
                                    firstTabOptionsWithoutSection0="${inNetworkInsurancesExceptSection0}"
                                    secondTabAllOptions="${insurancePlans}"
                                    firstTabOptionsSection0="${inNetworkInsurancesSection1}"
                                    firstTabOptionsSection1="${popularInNetworkInsurances}"
                                    secondTabOptionsSection1="${popularInsurancePlans}"
                                    inputPlaceholder="Insurrance Carrier and Plan"
                                    inputCssClass="searchable" />
            </lt:layout>

            <lt:layout cssClass="col-md-1 button-container">
                <%--<wg:label _for="filterClear"/>--%>
                <wg:button domType="button" id="filterClear" name="filterClear" type="button"
                           cssClass="btn-default form-control font14">
                    CLEAR
                </wg:button>
            </lt:layout>
            <lt:layout cssClass="col-md-1 button-container">
                <%--<wg:label _for="filterApply"/>--%>
                <wg:button domType="button" type="submit" cssClass="btn-primary form-control font14" name="filterApply"
                           id="filterApply">
                    APPLY
                </wg:button>
            </lt:layout>


        </wgForm:form>
    </lt:layout>
</div>
<div id="market-place-community-details-header" style="display: none">
    <ol class="breadcrumb">
        <li class="backToCommunity">
    <span class="ldr-ui-label">
    <c:url value="/resources/images/arrow_back.svg" var="backImg"/>
    <img src="${backImg}" class="back">
    </span>
            <span class="crumb">
    Community Listing
    </span>
        </li>
        <li class="active">
    <span class="crumb">
        Community <span id="communityId">#${community.id}</span>
    </span>
        </li>
    </ol>
</div>

<script>
    $(document).on('ready', function(){
        $(".autocomplete-tabs input.custom-combobox-input").addClass("form-control");
    });
</script>
<%--</lt:layout>--%>