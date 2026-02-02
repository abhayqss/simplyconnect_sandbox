<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html class="html">
<head>
<script language="JavaScript" type="text/javascript"
        src="<c:url value="/resources/js/plugins/jquery-1.12.4.min.js"/>"></script>
</head>
<body>
<form method="post" id="ssoForm" action="${ssoUrl}">
  <input type="hidden" name="Payload" value="${payload}" />
  <input type="hidden" name="RelayState" value="token" />
  <input type="hidden" name="RedirectUrl" value="${redirectUrl}" />
</form>

<script type="text/javascript">

  $(document).ready(function () {
    $("#ssoForm").submit();
  });

</script>
</body>
</html>
