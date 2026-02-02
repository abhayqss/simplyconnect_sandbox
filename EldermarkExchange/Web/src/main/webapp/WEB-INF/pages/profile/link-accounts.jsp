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
      <wgForm:form role="form" id="linkAccountsForm" commandName="credentialsDto">
        <wg:modal-header closeBtn="true">
          <span id="communityCreateHeader">Link Accounts</span>
        </wg:modal-header>
        <wg:modal-body cssClass="col-md-12 createCommunityBody">

          <lt:layout cssClass="col-md-12 no-horizontal-padding">
            <span class="sectionHead">Current Accounts</span>
          </lt:layout>


            <lt:layout cssClass="col-md-12 no-horizontal-padding linkAccountsTable">
                <lt:layout id="employeeListContainer"/>
            </lt:layout>

          <lt:layout cssClass="col-md-12 no-horizontal-padding linkSectionHeader">
            <span class="sectionHead">Link New Account</span>
          </lt:layout>

          <lt:layout cssClass="italic linkCredentialsMessage" style="padding-left: 15px">
            Please enter the credentials associated with account that you want to link to existing account(s).
          </lt:layout>
           <div id="formError" class="form-error"/>

          <lt:layout cssClass="col-md-4 form-group linkAccountCompany">
            <label for="communityStreet" class="normalFont">Company ID*</label>
            <wgForm:input path="company"
                          id="communityStreet"
                          cssClass="form-control"
                    />
          </lt:layout>

          <lt:layout cssClass="col-md-4 form-group linkAccountLogin">
            <label for="communityCity"  class="normalFont">Login*</label>
            <wgForm:input path="username"
                          id="communityCity"
                          cssClass="form-control"
                    />
          </lt:layout>

          <lt:layout cssClass="col-md-4 form-group linkAccountPassword">
            <label for="communityPostalCode"  class="normalFont">Password*</label>
            <wgForm:input path="password"
                          type="password"
                          id="communityPostalCode"
                          cssClass="form-control"
                    />
          </lt:layout>



        </wg:modal-body>
        <wg:modal-footer-btn-group>
          <wg:button name="linkAccountsModal"
                     domType="link"
                     dataToggle="modal"
                     dataTarget="#linkAccountsModal"
                     cssClass="btn-default btn cancelBtn">
            CANCEL
          </wg:button>

          <wg:button name="linkAccounts"
                     id="linkAccounts"
                     domType="link"
                     dataToggle="modal"
                     cssClass="btn-primary btn submitBtn">
            LINK ACCOUNTS

          </wg:button>
        </wg:modal-footer-btn-group>
      </wgForm:form>
    </lt:layout>
  </lt:layout>
</div>