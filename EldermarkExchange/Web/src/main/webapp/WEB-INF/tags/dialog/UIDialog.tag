<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="title" required="true" rtexprvalue="true" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="width" required="true" rtexprvalue="true" %>
<%@ attribute name="height" required="true" rtexprvalue="true" %>
<%@ attribute name="autoOpen" required="true" rtexprvalue="true" type="java.lang.Boolean" %>

<%--<script language="JavaScript" type="text/javascript">
    $(document).ready(function(){
                $("#${id}").dialog({
                    title: "${title}",
                    width: ${width},
                    height: ${height},
                    resizable: true,
                    draggable: true,
                    position: "center",
                    autoOpen: ${autoOpen}
                });
            }
    );
</script>--%>

<div id="${id}-acon-dialog" <c:if test="${name != null}">name="${name}-acon-dialog"</c:if>>
    <input type="hidden" id="width" value="${width}"/>
    <input type="hidden" id="height" value="${height}"/>
    <input type="hidden" id="title" value="${title}"/>
    <input type="hidden" id="autoOpen" value="${autoOpen}"/>
    <jsp:doBody/>
</div>
