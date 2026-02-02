<%--<%@page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>--%>
<%--<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>--%>
<%--<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>--%>
<%--<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>


<%--<lt:layout cssClass="event-log">--%>
    <%--<lt:layout cssClass="col-md-12 filterPanel">--%>
        <%--<lt:layout cssClass="boxHeader panel panel-primary">--%>
            <%--<lt:layout cssClass="panel-heading">--%>
                <%--Find Location / Service / Community--%>
            <%--</lt:layout>--%>
        <%--</lt:layout>--%>



        <%--<wgForm:form role="form" id="marketplaceFilterFrom" commandName="marketplaceFilter">--%>

            <%--&lt;%&ndash;<lt:layout cssClass="col-md-2">&ndash;%&gt;--%>
                <%--&lt;%&ndash;<wgForm:select path="primaryFocusId" name="primaryFocusId" id="primaryFocusId"&ndash;%&gt;--%>
                               <%--&lt;%&ndash;cssClass="form-control">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<wgForm:option value="" label="All"/>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<c:forEach var="item" items="${primaryFocuses}">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<wgForm:option value="${item.id}" label="${item.label}"/>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</c:forEach>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</wgForm:select>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</lt:layout>&ndash;%&gt;--%>


            <%--<lt:layout cssClass="col-md-2">--%>
                <%--<wgForm:select path="primaryFocusIds"--%>
                               <%--id="orgPrimaryFocus"--%>
                               <%--cssClass="form-control spicker"--%>
                               <%--multiple="true">--%>
                    <%--&lt;%&ndash;<option data-hidden="true"></option>&ndash;%&gt;--%>
                    <%--<c:forEach var="item" items="${primaryFocuses}">--%>
                        <%--<wgForm:option value="${item.id}" label="${item.label}"/>--%>
                    <%--</c:forEach>--%>
                <%--</wgForm:select>--%>
            <%--</lt:layout>--%>


            <%--&lt;%&ndash;<lt:layout cssClass="col-md-2">&ndash;%&gt;--%>
                <%--&lt;%&ndash;<wgForm:select path="communityTypeId" name="communityTypeId" id="communityTypeId"&ndash;%&gt;--%>
                               <%--&lt;%&ndash;cssClass="form-control">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<wgForm:option value="" label="All"/>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<c:forEach var="item" items="${communityTypes}">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<wgForm:option value="${item.id}" label="${item.label}"/>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</c:forEach>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</wgForm:select>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</lt:layout>&ndash;%&gt;--%>

            <%--<lt:layout cssClass="col-md-2">--%>
                <%--<wgForm:select path="communityTypeIds"--%>
                               <%--id="orgCommunityType"--%>
                               <%--class="form-control spicker"--%>
                               <%--multiple="true">--%>
                    <%--&lt;%&ndash;<option data-hidden="true"></option>&ndash;%&gt;--%>
                    <%--<wgForm:option value="" label="All types of community" selected="true"/>--%>
                    <%--<c:forEach var="item" items="${communityTypes}">--%>
                        <%--<wgForm:option value="${item.id}" label="${item.label}"/>--%>
                    <%--</c:forEach>--%>
                <%--</wgForm:select>--%>
            <%--</lt:layout>--%>

            <%--<lt:layout cssClass="col-md-2">--%>
                <%--<wgForm:select path="serviceIds"--%>
                               <%--id="serviceId"--%>
                               <%--cssClass="form-control spicker"--%>
                               <%--multiple="true">--%>
                    <%--&lt;%&ndash;<option data-hidden="true"></option>&ndash;%&gt;--%>
                    <%--<wgForm:option value="" label="All services" selected="true"/>--%>
                    <%--<wgForm:option value="0" label="All"/>--%>
                    <%--<c:forEach var="item" items="${services}">--%>
                        <%--<wgForm:option value="${item.id}" label="${item.label}"/>--%>
                    <%--</c:forEach>--%>
                <%--</wgForm:select>--%>
            <%--</lt:layout>--%>

            <%--<lt:layout cssClass="col-md-2">--%>
                <%--<wg:label _for="filterClear"/>--%>
                <%--<wg:button domType="button" id="filterClear" name="filterClear" type="button"--%>
                           <%--cssClass="btn-default form-control">--%>
                    <%--CLEAR--%>
                <%--</wg:button>--%>
            <%--</lt:layout>--%>
            <%--<lt:layout cssClass="col-md-2">--%>
                <%--<wg:label _for="filterApply"/>--%>
                <%--<wg:button domType="button" type="submit" cssClass="btn-primary form-control" name="filterApply"--%>
                           <%--id="filterApply">--%>
                    <%--APPLY--%>
                <%--</wg:button>--%>
            <%--</lt:layout>--%>

        <%--</wgForm:form>--%>

    <%--</lt:layout>--%>





    <%--<lt:layout id="contactsContent" cssClass="contactsContent col-md-12">--%>
        <%--<lt:layout cssClass="col-md-4">--%>
            <%--<lt:layout cssClass="patientFilterBox">--%>

                <%--<lt:layout cssClass="boxHeader panel panel-primary">--%>
                    <%--<lt:layout cssClass="panel-heading">--%>
                        <%--Community Listing--%>
                    <%--</lt:layout>--%>
                <%--</lt:layout>--%>

            <%--</lt:layout>--%>
        <%--</lt:layout>--%>
        <%--<div cssClass="col-md-8" id="map" style="width: 500px;height: 500px;">--%>

        <%--</div>--%>
    <%--</lt:layout>--%>



<%--</lt:layout>--%>



<%--<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC8yLpobUyUMRNXaiA1b0LbpLMbWrmbQ2I&callback=initMap"--%>
        <%--async defer></script>--%>