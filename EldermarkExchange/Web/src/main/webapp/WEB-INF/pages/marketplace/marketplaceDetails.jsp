<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%--<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>--%>

<lt:layout style="float:left">
    <lt:layout cssClass="boxHeader panel panel-primary ldr-ui-layout marketplaceMenuNavigator whiteBackground ldr-center-block" id="marketplaceCommunityDetails">

    </lt:layout>
    <lt:layout cssClass="boxBody">
        <lt:layout id="communityDetails" style="padding-left:15px">
            <lt:layout cssClass="marketplace-details-subheader"
                       style="padding-top:0">About</lt:layout>
            <div class="communityName"
                 style="padding-bottom: 2px;">${community.organizationName}, ${community.communityName}</div>
            <div class="marketplace-community-type"><c:forEach var="type"
                                                                                                            items="${community.communityTypes}"
                                                                                                            varStatus="typeLoop">
                ${fn:trim(type)}<c:if test="${!typeLoop.last}">,</c:if>
            </c:forEach>
            </div>

            <div  style="padding-top:5px" class="marketplace-service-description">
                <c:set var="numberSymbolsDescription" value="228"/>
                <c:choose>
                    <c:when test="${fn:length(community.servicesSummaryDescription)< (numberSymbolsDescription + 10)}">
                        ${community.servicesSummaryDescription}
                    </c:when>
                    <c:otherwise>
                        ${fn:substring(community.servicesSummaryDescription, 0, numberSymbolsDescription)}

                        <span class="additionalTextSpan" style="display:none;"> ${fn:substring(community.servicesSummaryDescription, numberSymbolsDescription, fn:length(community.servicesSummaryDescription)-1)}</span>
                        <a href="#" class="more">more...</a>
                        <%--<a href="#" class="more" style="display: none;">less...</a>--%>
                    </c:otherwise>
                </c:choose>

            </div>

            <lt:layout cssClass="marketplace-details-subheader contact-info">Contact Info</lt:layout>
            <div class="marketplace-details-address">${community.address}</div>
            <div class="marketplace-details-phone">
                <a href="tel:${community.phoneNumber}">
                    ${community.phoneNumber}
                </a>
            </div>
            <c:if test="${not empty community.levelOfCares or not empty community.serviceTreatmentApproaches
            or not empty community.emergencyServices or not empty community.ancillaryServices or not empty community.ageGroupsAccepted
            or not empty community.languageServices or not empty community.paymentInsuranceAccepted}">
                <lt:layout cssClass="marketplace-details-subheader services">Services</lt:layout>
            </c:if>
        </lt:layout>
        <c:if test="${not empty community.levelOfCares}">
        <wg:collapsed-simple-panel id="levelOfCare" panelHeaderId = "levelOfCareCollapsedPanel"
                            clpHeaderText="Level of Care"
                            itemList="${community.levelOfCares}" panelCssClass="community-details-services-panel">
        </wg:collapsed-simple-panel>
            <%--itemList="${community.levelOfCares}"--%>
        </c:if>
        <c:if test="${not empty community.serviceTreatmentApproaches}">
            <wg:collapsed-simple-panel id="serviceTreatmentApproaches" panelHeaderId = "serviceTreatmentApproachesCollapsedPanel"
                                       clpHeaderText="Services/Treatment Approaches"
                                       itemList="${community.serviceTreatmentApproaches}"  panelCssClass="community-details-services-panel">
            </wg:collapsed-simple-panel>
            <%--itemList="${community.levelOfCares}"--%>
        </c:if>
        <c:if test="${not empty community.emergencyServices}">
            <wg:collapsed-simple-panel id="emergencyServices" panelHeaderId = "emergencyServicesCollapsedPanel"
                                       clpHeaderText="Emergency Services"
                                       itemList="${community.emergencyServices}"  panelCssClass="community-details-services-panel">
            </wg:collapsed-simple-panel>
            <%--itemList="${community.levelOfCares}"--%>
        </c:if>
        <c:if test="${not empty community.ancillaryServices}">
            <wg:collapsed-simple-panel id="ancillaryServices" panelHeaderId = "ancillaryServicesCollapsedPanel"
                                       clpHeaderText="Ancillary Services"
                                       itemList="${community.ancillaryServices}"  panelCssClass="community-details-services-panel">
            </wg:collapsed-simple-panel>
            <%--itemList="${community.levelOfCares}"--%>
        </c:if>
        <c:if test="${not empty community.ageGroupsAccepted}">
            <wg:collapsed-simple-panel id="ageGroupsAccepted" panelHeaderId = "geGroupsAcceptedPanel"
                                       clpHeaderText="Age Groups Accepted"
                                       itemList="${community.ageGroupsAccepted}"  panelCssClass="community-details-services-panel">
            </wg:collapsed-simple-panel>
            <%--itemList="${community.levelOfCares}"--%>
        </c:if>
        <c:if test="${not empty community.languageServices}">
            <wg:collapsed-simple-panel id="languageServices" panelHeaderId = "languageServicesPanel"
                                       clpHeaderText="Language Services"
                                       itemList="${community.languageServices}"  panelCssClass="community-details-services-panel">
            </wg:collapsed-simple-panel>
            <%--itemList="${community.levelOfCares}"--%>
        </c:if>
        <c:if test="${not empty community.selectedInNetworkInsurancePlanNames}">

            <wg:collapsed-simple-panel-two-rows id="paymentInsuranceAccepted" panelHeaderId = "paymentInsuranceAcceptedCollapsedPanel"
                                       clpHeaderText="Payment/Insurance Accepted"
                                       itemMap="${community.selectedInNetworkInsurancePlanNames}"  panelCssClass="community-details-services-panel">
            </wg:collapsed-simple-panel-two-rows>
        </c:if>

        <%--href="#levelOfCareContent"--%>
        <%--target="levelOfCareContent"--%>


        <%--<c:if test="${itemList!=null}">--%>
            <%--<div id="levelOfCareContent">--%>
                <%--&lt;%&ndash;<c:forEach var="item" items="${community.levelOfCares}">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div>${item}</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</c:forEach>&ndash;%&gt;--%>
            <%--<div>asdjfkljLKAJSFdkjHSdkfjhkjsdhfksjhfkj </div>--%>
            <%--</div>--%>
        <%--</c:if>--%>
        <%--<div id="levelOfCareContent">--%>
            <%--<c:forEach var="item" items="${community.levelOfCares}">--%>
                <%--<div>${item}</div>--%>
            <%--</c:forEach>--%>
        <%--</div>--%>

        <%--<a class="btn btn-primary" data-toggle="collapse" href="#collapseExample" aria-expanded="false" aria-controls="collapseExample">--%>
            <%--Link with href--%>
        <%--</a>--%>
        <%--<div class="collapse" id="collapseExample">--%>
            <%--<div class="card card-block">--%>
                <%--Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident.--%>
            <%--</div>--%>
        <%--</div>--%>
    </lt:layout>
</lt:layout>
