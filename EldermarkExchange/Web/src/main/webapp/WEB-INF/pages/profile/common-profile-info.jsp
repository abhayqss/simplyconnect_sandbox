<jsp:useBean id="contactDto" scope="request" type="com.scnsoft.eldermark.shared.carecoordination.contacts.ContactDto"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@taglib prefix="ccd" tagdir="/WEB-INF/tags/ccd" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- <lt:layout cssClass="commonInfo" id="profileCommonInfo"> --%>
  <input type="hidden" name="currentUserId" value="${contactDto.id}" id="currentUserId"/>

  <lt:layout cssClass="item">
    <lt:layout cssClass="row linkedSectionName">
      <wg:label cssClass="name">Personal Details</wg:label>
    </lt:layout>
    <cc:label-for-value id="contactName" label="Name:" value="${contactDto.firstName} ${contactDto.lastName}"/>
    <cc:label-for-value id="contactEmail" label="Email:" value="${contactDto.email}"/>
    <cc:label-for-value id="contactSecureEmail" label="Secure Email:" value="${contactDto.secureMessaging}"/>
    <cc:label-for-value id="contactPhone" label="Phone Number:" value="${contactDto.phone}"/>
    <cc:label-for-value id="contactFax" label="Fax Number:" value="${contactDto.fax}"/>
    <cc:label-for-value id="contactAddress" label="Address:" value="${contactDto.address.displayAddress}"/>
    <lt:layout cssClass="row splitter"/>

  </lt:layout>

  <lt:layout cssClass="item">
    <lt:layout cssClass="row linkedSectionName">
      <wg:label cssClass="name">Account Details</wg:label>
    </lt:layout>
    <cc:label-for-value id="contactRole" label="System Role:" value="${contactDto.role.label}"/>
    <cc:label-for-value id="contactLogin" label="Login:" value="${contactDto.login4d}"/>
    <cc:label-for-value id="contactCompanyId" label="Company ID:" value="${contactDto.companyId}"/>
    <cc:label-for-value id="contactOrganization" label="Organization:" value="${contactDto.organization.label}"/>
    <cc:label-for-value id="contactCommunity" label="Community:" value="${contactDto.communityName}"/>
  </lt:layout>

<%-- </lt:layout> --%>
