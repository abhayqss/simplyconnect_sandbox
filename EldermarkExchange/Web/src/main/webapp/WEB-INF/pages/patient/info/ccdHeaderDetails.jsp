<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="ccd" tagdir="/WEB-INF/tags/ccd" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="patient.info.ccd.authors" var="authorsLabel"/>
<spring:message code="patient.info.ccd.dataEnterer" var="dataEntererLabel"/>
<spring:message code="patient.info.ccd.informants" var="informantsLabel"/>
<spring:message code="patient.info.ccd.custodian" var="custodianLabel"/>
<spring:message code="patient.info.ccd.informationRecipients" var="informationRecipientsLabel"/>
<spring:message code="patient.info.ccd.legalAuthenticator" var="legalAuthenticatorLabel"/>
<spring:message code="patient.info.ccd.authenticators" var="authenticatorsLabel"/>
<spring:message code="patient.info.ccd.participants" var="participantsLabel"/>
<spring:message code="patient.info.ccd.documentationOfs" var="documentationOfsLabel"/>
<spring:message code="patient.detail.field.time" var="timeLabel"/>
<spring:message code="patient.detail.field.time.low" var="timeLowLabel"/>
<spring:message code="patient.detail.field.time.high" var="timeHighLabel"/>
<spring:message code="patient.detail.field.role" var="roleLabel"/>
<spring:message code="patient.detail.field.relationship" var="relationshipLabel"/>


<lt:layout>

    <!-- Authors -->
    <ccd:header-section-layout content="${ccdHeaderDetails.authors}" name="${authorsLabel}">
        <lt:layout cssClass="authors">
            <c:forEach var="item" items="${ccdHeaderDetails.authors}">
                <lt:layout cssClass="item">
                    <lt:layout cssClass="content">
                        <ccd:label-for-value label="${timeLabel}" value="${item.time}"/>
                        <ccd:person person="${item.person}"/>
                        <ccd:organization organization="${item.organization}"/>
                    </lt:layout>
                </lt:layout>
            </c:forEach>
        </lt:layout>
    </ccd:header-section-layout>

    <!-- Data Enterer -->
    <ccd:header-section-layout content="${ccdHeaderDetails.dataEnterer}" name="${dataEntererLabel}">
    	<lt:layout cssClass="dataEnterer">
    		<lt:layout cssClass="item">
                    <lt:layout cssClass="content">
				        <c:set var="item" value="${ccdHeaderDetails.dataEnterer}"/>
				        <ccd:person person="${item.person}"/>
				    </lt:layout>
			</lt:layout>
		</lt:layout>
    </ccd:header-section-layout>

    <!-- Informants -->
    <ccd:header-section-layout content="${ccdHeaderDetails.informants}" name="${informantsLabel}">
        <lt:layout cssClass="informants">
            <c:forEach var="item" items="${ccdHeaderDetails.informants}">
                <lt:layout cssClass="item">
                    <lt:layout cssClass="content">
                        <ccd:person person="${item.person}"/>
                    </lt:layout>
                </lt:layout>
            </c:forEach>
        </lt:layout>
    </ccd:header-section-layout>

    <!-- Custodian -->
    <ccd:header-section-layout content="${ccdHeaderDetails.custodian}" name="${custodianLabel}">
    	<lt:layout cssClass="custodian">
    		<lt:layout cssClass="item">
                    <lt:layout cssClass="content">
				        <c:set var="item" value="${ccdHeaderDetails.custodian}"/>
				        <ccd:organization organization="${item.organization}"/>
        			</lt:layout>
			</lt:layout>
		</lt:layout>
    </ccd:header-section-layout>

    <!-- Information Recipients -->
    <ccd:header-section-layout content="${ccdHeaderDetails.informationRecipients}" name="${informationRecipientsLabel}">
        <lt:layout cssClass="informants">
            <c:forEach var="item" items="${ccdHeaderDetails.informationRecipients}">
                <lt:layout cssClass="item">
                    <lt:layout cssClass="content">
                        <ccd:person person="${item.person}"/>
                        <ccd:organization organization="${item.organization}"/>
                    </lt:layout>
                </lt:layout>
            </c:forEach>
        </lt:layout>
    </ccd:header-section-layout>

    <!--Legal Authenticator-->
    <ccd:header-section-layout content="${ccdHeaderDetails.legalAuthenticator}" name="${legalAuthenticatorLabel}">
    	<lt:layout cssClass="authenticators">
    		<lt:layout cssClass="item">
                    <lt:layout cssClass="content">
				        <c:set var="item" value="${ccdHeaderDetails.legalAuthenticator}"/>
				        <ccd:label-for-value label="${timeLabel}" value="${item.time}"/>
				        <ccd:person person="${item.person}"/>
        			</lt:layout>
			</lt:layout>
		</lt:layout>
    </ccd:header-section-layout>

    <!--Authenticators-->
    <ccd:header-section-layout content="${ccdHeaderDetails.authenticators}" name="${authenticatorsLabel}">
        <lt:layout cssClass="authenticators">
            <c:forEach var="item" items="${ccdHeaderDetails.authenticators}">
                <lt:layout cssClass="item">
                    <lt:layout cssClass="content">
                        <ccd:label-for-value label="${timeLabel}" value="${item.time}"/>
                        <ccd:person person="${item.person}"/>
                    </lt:layout>
                </lt:layout>
            </c:forEach>
        </lt:layout>
    </ccd:header-section-layout>

    <!--Participants-->
    <ccd:header-section-layout content="${ccdHeaderDetails.participants}" name="${participantsLabel}">
        <lt:layout cssClass="participants">
            <c:forEach var="item" items="${ccdHeaderDetails.participants}">
                <lt:layout cssClass="item">
                    <lt:layout cssClass="content">
                        <ccd:label-for-value label="${roleLabel}" value="${item.roleCode}"/>
                        <ccd:label-for-value label="${relationshipLabel}" value="${item.relationship}"/>
                        <ccd:label-for-value label="${timeLowLabel}" value="${item.timeLow}"/>
                        <ccd:label-for-value label="${timeHighLabel}" value="${item.timeHigh}"/>
                        <ccd:person person="${item.person}"/>
                    </lt:layout>
                </lt:layout>
            </c:forEach>
        </lt:layout>
    </ccd:header-section-layout>

    <!--Documentation Of-->
    <ccd:header-section-layout content="${ccdHeaderDetails.documentationOfs}" name="${documentationOfsLabel}">
        <lt:layout cssClass="documentationOf">
            <c:forEach var="item" items="${ccdHeaderDetails.documentationOfs}">
                <lt:layout cssClass="item">
                    <lt:layout cssClass="content">
                        <ccd:label-for-value label="${timeLowLabel}" value="${item.effectiveTimeLow}"/>
                        <ccd:label-for-value label="${timeHighLabel}" value="${item.effectiveTimeHigh}"/>
                        <c:forEach var="person" items="${item.persons}">
                            <ccd:person person="${person}"/>
                        </c:forEach>
                    </lt:layout>
                </lt:layout>
            </c:forEach>
        </lt:layout>
    </ccd:header-section-layout>

</lt:layout>