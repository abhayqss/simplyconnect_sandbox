<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="modal fade" role="dialog" id="linkAccountsModal" data-backdrop="static">
  <lt:layout cssClass="modal-dialog" role="document" style="width:1000px;">
    <lt:layout cssClass="modal-content">
      <wgForm:form role="form" id="linkAccountsForm" commandName="newAccountLinkedDto">
        <wg:modal-header closeBtn="true">
          <span id="communityCreateHeader">Create a New Account</span>
        </wg:modal-header>
        <wg:modal-body cssClass="col-md-12 createCommunityBody">

          <lt:layout cssClass="italic" style="padding-left: 15px">
            ${newAccountLinkedDto.creatorName} has invited you with a ${newAccountLinkedDto.role} role in ${newAccountLinkedDto.organization} Facility.
          </lt:layout>
            <hr class="linkNewAccountLine"/>

          <lt:layout cssClass="col-md-12 no-horizontal-padding">
            <span class="sectionHead">Current Accounts</span>
          </lt:layout>

          <lt:layout cssClass="col-md-12 no-horizontal-padding linkAccountsTable">
            <lt:layout id="employeeListContainer"/>
          </lt:layout>

          <lt:layout cssClass="col-md-12 no-horizontal-padding linkSectionHeader">
            <span class="sectionHead">Credentials for New Account</span>
          </lt:layout>

          <lt:layout cssClass="italic linkCredentialsMessage" style="padding-left: 15px">
            Please create a password for future access to this account. Use <b>${newAccountLinkedDto.login}</b> as Login and <b>${newAccountLinkedDto.organizationCode}</b> as Company ID.
          </lt:layout>
          <div id="formError" class="form-error" style="padding-left: 5px"/>


            <wgForm:hidden path="login"/>
            <wgForm:hidden path="organizationCode"/>
            <wgForm:hidden path="token"/>
            <wgForm:hidden path="creatorName"/>
            <wgForm:hidden path="databaseId"/>

            <lt:layout cssClass="col-md-6 form-group linkNewAccountPassword">
                <label for="password" class="normalFont">Password*</label>
                <a href="#" class="help-icon" id="passwordHelp" data-toggle="popover" data-trigger="hover"></a>
                <wgForm:password path="password"
                                 id="password"
                                 cssClass="form-control"
                        />
            </lt:layout>

          <lt:layout cssClass="col-md-6 form-group linkNewAccountConfirmPassword">
            <label for="confirmPassword" class="normalFont">Confirm Password*</label>
            <wgForm:password path="confirmPassword"
                               id="confirmPassword"
                               cssClass="form-control"
                      />
          </lt:layout>

        </wg:modal-body>
        <wg:modal-footer-btn-group>
          <%--<lt:layout cssClass="btn-group" role="group">--%>
          <wg:button name="linkAccountsModal"
                     domType="link"
                     dataToggle="modal"
                     dataTarget="#linkAccountsModal"
                     cssClass="btn-default btn cancelBtn">
            CANCEL
          </wg:button>

          <wg:button name="linkCreateAccounts"
                     id="linkCreateAccounts"
                     domType="link"
                     dataToggle="modal"
                     cssClass="btn-primary btn submitBtn">
            SAVE

          </wg:button>
          <%--</lt:layout>--%>
        </wg:modal-footer-btn-group>
      </wgForm:form>
    </lt:layout>
  </lt:layout>
</div>
<lt:layout id="passwordHelpTemplate" cssClass="datasourcePreview hidden">
  <lt:layout  style="clear:both" >
    <jsp:include page="../login/passwordRequirements.jsp">
      <jsp:param name="cssClass" value="passwordHelpReset"/>
    </jsp:include>
  </lt:layout>

</lt:layout>
