<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<table class="ldr-ui-app">
    <tr id="header" class="ldr-ui-header">
        <tiles:insertAttribute name="header"/>
    </tr>
    <tr>
        <td id="content" class="ldr-ui-content">
            <noscript><div class="loginError">Warning: either you have javascript disabled or your browser does not support javascript. To work properly, this page requires javascript to be enabled.</div></noscript>
            <tiles:insertAttribute name="body"/>
        </td>
    </tr>
    <tr id="footer" class="ldr-ui-footer">
        <tiles:insertAttribute name="footer"/>
    </tr>
</table>
