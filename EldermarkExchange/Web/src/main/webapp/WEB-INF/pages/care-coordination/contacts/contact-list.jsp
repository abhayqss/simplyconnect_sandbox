<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<lt:layout cssClass="contactsListBox panel panel-primary">
    <lt:layout style="padding:0 25px; ">
        <span class="sectionHead">Contacts List</span>
        <c:if test="${not affiliatedView}">
            <wg:button name="createContact"
                               id="createContact"
                               domType="button"
                               dataToggle="modal"
                               cssClass="btn-primary pull-right">
            CREATE NEW CONTACT
            </wg:button>
        </c:if>
    </lt:layout>


    <lt:layout cssClass="boxBody">
        <wg:grid id="contactsList" cssClass="contactsList"
                 colIds="displayName,role,status,email,phone,actions"
                 colNames="Name,System Role,Status,Email,Phone,Actions"
                 colFormats="string,string,string,string,string,fake"
                 dataUrl="care-coordination/contacts"
                 deferLoading="true"
                />
    </lt:layout>
</lt:layout>

<lt:layout cssClass="hidden rowActions">
    <a type="button" class="btn btn-default editContact">
        <span class="glyphicon" aria-hidden="true"></span>
    </a>
</lt:layout>
