<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="button.search" var="search"/>

<lt:layout>
    <lt:layout id="patientsContent">
        <lt:layout style="padding: 20px 0px 20px 20px" cssClass="col-md-12 filterPanel">
            <wgForm:form role="form" id="patientFilterSuggested" action="patients" method="post" commandName="patientFilterSuggested">
                <lt:layout cssClass="col-md-2" style="width: 400px; padding-left : 0px">
                    <wgForm:input path="query"
                                  id="filter.query"
                                  cssClass="form-control" cssStyle="height: 34px"
                    />
                </lt:layout>
                <lt:layout cssClass="col-md-2" style="width: 100px">
                    <wg:button name="patientSearchSuggested"
                               id="patientSearchSuggested"
                               domType="button"
                               dataToggle="modal"
                               cssClass="btn-primary pull-right"
                    >${search}</wg:button>
                </lt:layout>
            </wgForm:form>
        </lt:layout>

        <jsp:include page="patient-list.jsp"/>
    </lt:layout>
    <lt:layout id="patientsComparedContent" style="display:none;" cssClass="patientsComparedContent">
        <jsp:include page="step2.jsp"/>
    </lt:layout>
</lt:layout>

