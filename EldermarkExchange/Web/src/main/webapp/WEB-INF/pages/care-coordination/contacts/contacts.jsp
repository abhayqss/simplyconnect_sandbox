<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<lt:layout>
    <lt:layout id="contactsContent" cssClass="contactsContent col-md-12">
        <lt:layout cssClass="col-md-4">
            <jsp:include page="contact-filter.jsp"/>
        </lt:layout>
        <lt:layout cssClass="col-md-8">
            <jsp:include page="contact-list.jsp"/>
        </lt:layout>
    </lt:layout>
    <lt:layout id="createContactContainer"/>
</lt:layout>

