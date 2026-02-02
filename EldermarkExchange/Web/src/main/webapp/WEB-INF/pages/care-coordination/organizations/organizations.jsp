<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<lt:layout>
   <lt:layout id="organizationContent"  cssClass="careCoordinationContent col-md-12">
      <jsp:include page="organization-list.jsp"/>
   </lt:layout>
   <lt:layout id="organizationDetailsContent" style="display:none;" cssClass="organizationDetailsContent">
      <jsp:include page="organization-details.jsp"/>
   </lt:layout>
   <lt:layout id="createOrganizationContainer"/>
   <lt:layout id="passwordSettingsContainer"/>

   <div id="loader-div" class="hidden ajaxLoader"/>
</lt:layout>