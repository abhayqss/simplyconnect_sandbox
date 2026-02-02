<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:forEach var="item" items="${passwordSettingsDto.databasePasswordSettingsList}" varStatus="i">

    <c:if test="${item.passwordSettings.passwordSettingsType.section == param.groupName}">
        <wgForm:hidden path="databasePasswordSettingsList[${i.index}].id"/>
        <wgForm:hidden path="databasePasswordSettingsList[${i.index}].databaseId"/>
        <wgForm:hidden path="databasePasswordSettingsList[${i.index}].passwordSettings.id"/>
        <wgForm:hidden path="databasePasswordSettingsList[${i.index}].passwordSettings.passwordSettingsType"/>
        <lt:layout cssClass="col-md-10 form-group">
            <wgForm:checkbox path="databasePasswordSettingsList[${i.index}].enabled"
                             id="passwordSettings${i.index}"
                             cssClass="passwordSettingsCheckbox" disabled="${item.passwordSettings.passwordSettingsType.mandatory}"
            />
            <lt:layout cssClass="passwordSettingsInfo">
                <spring:message code="organization.password.settings.${item.passwordSettings.passwordSettingsType}.info"/><br/>
                <span style="color:#888888"><spring:message code="organization.password.settings.${item.passwordSettings.passwordSettingsType}.help"/></span>
            </lt:layout>
        </lt:layout>

        <lt:layout cssClass="col-md-2 form-group">
            <wgForm:input path="databasePasswordSettingsList[${i.index}].value"
                          id="passwordSettings${i.index}Value"
                          cssClass="form-control"
                          readonly="${not item.enabled}"
            />
        </lt:layout>
    </c:if>
</c:forEach>
