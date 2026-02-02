<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message var="buttonSearchText" code="button.search"/>
<spring:message var="emptyCommunityListMessage" code="marketplace.communityList.empty.message"/>

<lt:layout cssClass="marketplaceCommunityBox">
	<lt:layout id="marketplaceMenuContent">
		<lt:layout cssClass="boxHeader panel panel-primary" id="marketplaceMenu">
			<lt:layout cssClass="panel-heading">
				Community Listing
			</lt:layout>
		</lt:layout>
		<%--<lt:layout cssClass="boxBody">--%>
		<lt:layout cssClass="boxBody">
			<lt:layout style="padding-bottom:55px">
				<wgForm:form role="form" id="communitySearchForm" action="communitySearch" method="post"
										 commandName="searchFilter">
					<lt:layout cssClass="col-md-2" style="width: 327px;">
						<wgForm:input path="query"
													id="searchField"
													cssClass="form-control clearable" cssStyle="height: 34px"
													placeholder="Search by name, address or type"
						/>
					</lt:layout>
					<lt:layout cssClass="col-md-2">
						<wg:button name="communitySearchButton"
											 id="communitySearchButton"
											 domType="link"
											 dataToggle="modal"
											 cssClass="btn-primary"
						>${buttonSearchText}</wg:button>
					</lt:layout>
				</wgForm:form>
			</lt:layout>
			<lt:layout id="communityList" style="overflow-y:scroll; height:500px" cssClass="container-fluid">
				<c:if test="${empty marketplaceList.content}">
					<div>${emptyCommunityListMessage}</div>
				</c:if>
				<c:forEach var="marketplace" items="${marketplaceList.content}" varStatus="marketplaceLoop">
					<lt:layout cssClass="communityItem  ${marketplaceLoop.index % 2 == 0 ? 'even' : 'odd'} row" style="padding:15px">
						<div class="communityName" style="padding-bottom: 2px;">
							<wg:truncatedText maxSymbolNumber="130">
								${marketplace.organizationName}, ${marketplace.communityName}
							</wg:truncatedText>
						</div>
						<div class="marketplace-community-type truncated-text" style="padding-bottom: 2px;">
							<c:forEach var="type" items="${marketplace.communityTypes}" varStatus="typeLoop">
								${fn:trim(type)}<c:if test="${!typeLoop.last}">,</c:if>
							</c:forEach>
						</div>
						<div class="d-flex justify-content-between" style="padding-top: 5px">
							<div class="communityAddress truncated-text"
									 data-addmarker="${marketplace.addMarker}"
									 data-markercount="${marketplace.markerCount}"
									 data-sameaddrids="${marketplace.sameAddrIds}"
									 data-latitude="${marketplace.location.latitude}"
									 data-longitude="${marketplace.location.longitude}">${marketplace.address}</div>
							<div class="community-distance">${marketplace.location.displayDistanceMiles}
									<%--- ${marketplace.location.longitude} - ${marketplace.location.latitude}--%>
							</div>
						</div>
						<div class="d-flex justify-content-between" style="padding-top: 10px">
							<a class="btn btn-primary marketplace__schedule-an-appointment-btn">
								SCHEDULE AN APPOINTMENT
							</a>
							<div class="marketplace-community-more-information text-right col-md-12">
								<a href="#" data-id="${marketplace.id}" class="marketplaceDetailsLink">More information</a>
							</div>
						</div>
					</lt:layout>
				</c:forEach>
			</lt:layout>
		</lt:layout>
	</lt:layout>
	<lt:layout id="marketplaceDetailsContent"/>
</lt:layout>
