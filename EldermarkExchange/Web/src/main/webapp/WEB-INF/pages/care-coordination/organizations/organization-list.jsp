<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<lt:layout cssClass="patientListBox panel panel-primary">

    <lt:layout cssClass="boxHeader panel panel-heading">
        Organizations List
    </lt:layout>

    <lt:layout style="padding:10px 25px; " cssClass="col-md-12 filterPanel">
        <wgForm:form role="form" id="orgFilterFrom" commandName="orgFilter">
            <lt:layout cssClass="col-md-2" style="width:400px;padding-left:0px;">
                <wgForm:input path="name"
                              id="filter.name"
                              cssClass="form-control" cssStyle="height: 34px"
                        />
            </lt:layout>
            <lt:layout cssClass="col-md-2" style="width:100px">
                <wg:button name="searchOrganization"
                           id="searchOrganization"
                           domType="button"
                           dataToggle="modal"
                           cssClass="btn-primary pull-right">
                    SEARCH
                </wg:button>
            </lt:layout>
        </wgForm:form>

        <wg:button name="createOrganization"
                   id="createOrganization"
                   domType="button"
                   dataToggle="modal"
                   cssClass="btn-primary pull-right">
            ADD ORGANIZATION
        </wg:button>
    </lt:layout>

   <lt:layout cssClass="boxBody">
      <wg:grid id="organizationList" cssClass="careCoordinationList"
               colIds="name, communityCount, affilatedCount, createdAutomatically, lastModified"
               colNames="Name, Communities, Affiliated Organizations, Created Automatically, Modified On"
               colFormats="custom, custom, custom, custom, date"
               dataUrl="care-coordination/organizations"
               dataRequestMethod="GET"
               deferLoading="true"
              />
   </lt:layout>
</lt:layout>

<lt:layout cssClass="hidden rowActions">
    <a type="button" class="btn btn-default editOrg">
        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
    </a>
</lt:layout>

<lt:layout id="affiliatedOrgsTemplate" cssClass="affiliatedPreview hidden">
    <lt:layout id="affiliatedOrgsLayout">
        <wg:label id="affiliatedOrgsLayout-org" cssClass="text affiliatedPreviewOrgName">Org Name</wg:label>
        <wg:link id="affiliatedOrgsLayout-link" href="#" cssClass="recordLink affiliatedPreviewLink">View Details</wg:link>
    </lt:layout>
</lt:layout>

