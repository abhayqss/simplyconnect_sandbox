<%@page contentType="text/html;charset=UTF-8" language="java" %>

<table style="width:100%; vertical-align: top">
    <tr valign="top">
        <th>
            <div id="reportsMenu" class="reportsMenu"></div>
        </th>
        <th width="100%">
            <iframe id="reportFrame" class="reportFrame" frameborder="0"></iframe>
        </th>
    </tr>
</table>


<input type="hidden" value="${kyubitUrl}" id="baseAddr"/>
<input type="hidden" value="${microsoftUrl}" id="SSRSUrl"/>
<form method="post" id="loginForm" class="loginForm" action="${kyubitUrl}/Forms/Login.aspx"
      style="width: 0;height:0;overflow: hidden;" target="kyubit_login_iframe">
    <input type="hidden" name="txtDomain" value="${txtDomain}"/>
    <input type="hidden" name="txtUserName" value="${txtUserName}"/>
    <input type="hidden" name="txtUserPassword" value="${txtUserPassword}"/>
    <input type="hidden" name="btnLogin" value="Login"/>
    <input type="hidden" name="__VIEWSTATE" value="${viewState}"/>
    <input type="hidden" name="__EVENTVALIDATION" value="${eventValidation}"/>
    <input type="hidden" name="btnLogin" value="Login"/>
</form>
