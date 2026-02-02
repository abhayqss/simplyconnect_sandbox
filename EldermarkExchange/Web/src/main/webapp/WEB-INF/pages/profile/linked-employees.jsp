<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@taglib prefix="ccd" tagdir="/WEB-INF/tags/ccd" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:if test="${fn:length(linkedEmployees) > 1}">
  <lt:layout cssClass="row">
    <wg:label cssClass="name">Linked Accounts</wg:label>
  </lt:layout>
  <lt:layout cssClass="guardians">
    <c:forEach var="linkedEmployee" items="${linkedEmployees}">
      <c:if test="${linkedEmployee.id != contactDto.id}">
        <lt:layout cssClass="item">
          <lt:layout cssClass="content">
            <lt:layout cssClass="row">
              <div class="ldr-ui-layout col-md-12">
                <p class="col-md-4">Name:</p>

                <p class="col-md-8"><a href="#" class="unlinkButton" onclick="ExchangeApp.modules.Profile.editLinkedContact(${linkedEmployee.id}, this)">${linkedEmployee.displayName}</a></p>
              </div>

              <%-- <cc:label-for-value label="Name:"
                                  value="${linkedEmployee.displayName}"/> --%>
            </lt:layout>
            <lt:layout cssClass="row">
              <cc:label-for-value label="System Role:" value="${linkedEmployee.role}"/>
            </lt:layout>
            <lt:layout cssClass="row">
              <cc:label-for-value label="Login:" value="${linkedEmployee.login}"/>
            </lt:layout>
            <lt:layout cssClass="row">
              <cc:label-for-value label="Company ID:" value="${linkedEmployee.companyId}"/>
            </lt:layout>
            <lt:layout cssClass="row">
              <c:choose>
                <c:when test="${empty linkedEmployee.community}">
                  <div class="ldr-ui-layout col-md-12">
                    <p class="col-md-4">Organization:</p>

                    <p class="col-md-5">${linkedEmployee.organization}</p>

                    <p class="col-md-3"><a href="#" class="unlinkButton" onclick="ExchangeApp.modules.Profile.unlinkAccount(${linkedEmployee.id})">Unlink
                      Account</a></p>
                  </div>
                </c:when>
                <c:otherwise>
                  <cc:label-for-value label="Organization:"
                                      value="${linkedEmployee.organization}"/>
                </c:otherwise>
              </c:choose>
            </lt:layout>
            <lt:layout cssClass="row">
              <c:choose>
                <c:when test="${empty linkedEmployee.community}">
                  <cc:label-for-value label="Community:" value="${linkedEmployee.community}"/>
                </c:when>
                <c:otherwise>
                  <div class="ldr-ui-layout col-md-12">
                    <p class="col-md-4">Community:</p>

                    <p class="col-md-5">${linkedEmployee.community}</p>

                    <p class="col-md-3"><a href="#" class="unlinkButton" onclick="ExchangeApp.modules.Profile.unlinkAccount(${linkedEmployee.id})">Unlink
                      Account</a></p>
                  </div>
                </c:otherwise>
              </c:choose>

            </lt:layout>

          </lt:layout>
        </lt:layout>
      </c:if>
    </c:forEach>
  </lt:layout>
</c:if>
