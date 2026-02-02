<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="closeBtn" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<c:url var="closeImgUrl" value="/resources/images/cross.svg" />
<c:url var="saveAndValidate" value="/resources/images/save_icon.svg"/>

<div class="modal-header">
    <c:if test="${closeBtn}">
        <button id="closeIconButton" type="button" class="close" data-dismiss="modal" aria-label="Close">
            <img src="${closeImgUrl}">
        </button>
    </c:if>
    <button type="button" class="save-and-validate-button" data-dismiss="modal" aria-label="Save and Validate">
        <img src="${saveAndValidate}">
    </button>
    <h4 class="modal-title">
        <jsp:doBody/>
    </h4>
</div>