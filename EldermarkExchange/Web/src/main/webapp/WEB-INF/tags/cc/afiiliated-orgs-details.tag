<%@ tag pageEncoding="UTF-8" %>

<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ attribute name="affiliatedOrg" required="true" rtexprvalue="true" type="com.scnsoft.eldermark.shared.carecoordination.GroupedAffiliatedOrganizationDto"%>

<p class="col-md-8">
  <c:set var="countVar" value="is"/>
  <c:if test="${not empty affiliatedOrg.affiliatedCommunityNames}">
    <c:forEach var="affiliatedCommName" items="${affiliatedOrg.affiliatedCommunityNames}"
               varStatus="loopAffilComm">
      ${loopAffilComm.index == 0 ? '' : ','}"${affiliatedCommName}"
    </c:forEach>
  </c:if>
  <c:if test="${not empty affiliatedOrg.affiliatedCommunityNames && fn:length(affiliatedOrg.affiliatedCommunityNames) > 1}">
    communities of
    <c:set var="countVar" value="are"/>
  </c:if>
  <c:if test="${not empty affiliatedOrg.affiliatedCommunityNames && fn:length(affiliatedOrg.affiliatedCommunityNames) == 1}">
    community of
  </c:if>

  <c:if test="${not empty affiliatedOrg.affiliatedOrganizationNames}">
    <c:forEach var="affiliatedOrgName" items="${affiliatedOrg.affiliatedOrganizationNames}"
               varStatus="loopAffilOrg">
      ${loopAffilOrg.index == 0 ? '' : ','}"${affiliatedOrgName}"
    </c:forEach>
  </c:if>
  <c:if test="${not empty affiliatedOrg.affiliatedOrganizationNames && fn:length(affiliatedOrg.affiliatedOrganizationNames) > 1}">
    organizations
    <c:set var="countVar" value="are"/>
  </c:if>
  <c:if test="${not empty affiliatedOrg.affiliatedOrganizationNames && fn:length(affiliatedOrg.affiliatedOrganizationNames) == 1}">
    organization
  </c:if>
  ${countVar} affiliated for

  <c:if test="${not empty affiliatedOrg.primaryCommunityNames}">
    <c:forEach var="primaryCommName" items="${affiliatedOrg.primaryCommunityNames}" varStatus="loopPrimComm">
      ${loopPrimComm.index == 0 ? '' : ','}"${primaryCommName}"
    </c:forEach>
  </c:if>
  <c:if test="${not empty affiliatedOrg.primaryCommunityNames && fn:length(affiliatedOrg.primaryCommunityNames) > 1}">
    communities of
  </c:if>
  <c:if test="${not empty affiliatedOrg.primaryCommunityNames && fn:length(affiliatedOrg.primaryCommunityNames) == 1}">
    community of
  </c:if>

  <c:if test="${not empty affiliatedOrg.primaryOrganizationNames}">
    <c:forEach var="primaryOrgName" items="${affiliatedOrg.primaryOrganizationNames}" varStatus="loopPrimOrg">
      ${loopPrimOrg.index == 0 ? '' : ','}"${primaryOrgName}"
    </c:forEach>
  </c:if>
  <c:if test="${not empty affiliatedOrg.primaryOrganizationNames && fn:length(affiliatedOrg.primaryOrganizationNames) > 1}">
    organizations
  </c:if>
  <c:if test="${not empty affiliatedOrg.primaryOrganizationNames && fn:length(affiliatedOrg.primaryOrganizationNames) == 1}">
    organization
  </c:if>
</p>
