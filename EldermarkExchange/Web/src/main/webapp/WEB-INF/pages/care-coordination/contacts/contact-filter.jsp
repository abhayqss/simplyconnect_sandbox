<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<lt:layout cssClass="contactFilterBox">

    <lt:layout cssClass="boxHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            Search
        </lt:layout>
    </lt:layout>

    <wgForm:form role="form" id="contactsFilter" commandName="contactFilter" style="padding: 0 20px 20px 20px">
        <lt:layout>
            <wg:label _for="email">Email</wg:label>
            <wgForm:input path="email" type="text" name="email" cssClass="form-control"/>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="email">System Role</wg:label>
            <lt:layout cssClass="form-inline">
                <wgForm:select path="roleId">
                    <wgForm:option value="" label="All"/>
                    <c:forEach var="item" items="${careTeamRoles}">
                        <wgForm:option value="${item.id}">${item.label}</wgForm:option>
                    </c:forEach>
                </wgForm:select>
            </lt:layout>

        </lt:layout>
        <lt:layout cssClass="form-group">
            <wg:label _for="firstName">First Name</wg:label>
            <wgForm:input path="firstName" type="text" name="firstName" cssClass="form-control"/>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="lastName">Last Name</wg:label>
            <wgForm:input path="lastName" type="text" name="lastName" cssClass="form-control"/>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="email">Status</wg:label>
            <lt:layout cssClass="form-inline">
                <wgForm:select path="status">
                    <wgForm:option value="-1" label="All"/>
                    <c:forEach var="item" items="${employeeStatuses}">
                        <wgForm:option value="${item.value}">${item.text}</wgForm:option>
                    </c:forEach>
                </wgForm:select>
            </lt:layout>

        </lt:layout>


        <lt:layout cssClass="form-inline form-group buttons" >
            <wg:button domType="button" id="contactSearchClear" name="contactSearchClear" type="button"
                       cssClass="btn-default pull-rigth">
                CLEAR
            </wg:button>

            <wg:button domType="button" type="submit" cssClass="btn-primary" name="contactSearch"
                       id="contactSearch">
                SEARCH
            </wg:button>

        </lt:layout>

    </wgForm:form>
</lt:layout>