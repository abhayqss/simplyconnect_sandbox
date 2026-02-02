<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<table class="ldr-ui-app">
    <tr id="header" class="ldr-ui-header">
        <td class="markup-frg">
            <tiles:insertAttribute name="header"/>
        </td>
    </tr>
    <tr>
        <td id="content" class="ldr-ui-content">
            <table class="ldr-ui-main markup-frg <tiles:insertAttribute name="mainCssClass"/>">
                <tr>
                    <td class="ldr-ui-menu <tiles:insertAttribute name="menuCssClass"/>">
                        <tiles:insertAttribute name="menu"/>
                    </td>
                    <td class="ldr-ui-body <tiles:insertAttribute name="bodyCssClass"/>">
                        <tiles:insertAttribute name="body"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td id="footer" class="ldr-ui-footer markup-frg">
            <tiles:insertAttribute name="footer"/>
        </td>
    </tr>
</table>