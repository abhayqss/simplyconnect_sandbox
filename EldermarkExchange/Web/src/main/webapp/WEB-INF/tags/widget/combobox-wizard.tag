<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="firstTabAllOptions" type="java.util.Collection" required="false" rtexprvalue="true" %>
<%@ attribute name="firstTabOptionsWithoutSection0" type="java.util.Collection" required="false" rtexprvalue="true" %>
<%@ attribute name="firstTabOptionsSection0" type="java.util.Collection" required="false" rtexprvalue="true" %>
<%@ attribute name="firstTabOptionsSection1" type="java.util.Collection" required="false" rtexprvalue="true" %>

<%@ attribute name="secondTabAllOptions" type="java.util.Map" required="false" rtexprvalue="true" %>
<%@ attribute name="secondTabOptionsSection1" type="java.util.Map" required="false" rtexprvalue="true" %>

<%@ attribute name="firstTabTitle" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="firstTabTitleSection1" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="firstTabTitleSection2" type="java.lang.String" required="false" rtexprvalue="true" %>

<%@ attribute name="secondTabTitle" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="secondTabTitleSection1" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="secondTabTitleSection2" type="java.lang.String" required="false" rtexprvalue="true" %>

<%@ attribute name="chooseDifferentValue" type="java.lang.String" required="false" rtexprvalue="true" %>

<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="firstValuePath" required="true" rtexprvalue="true" %>
<%@ attribute name="secondValuePath" required="true" rtexprvalue="true" %>

<%@ attribute name="inputPlaceholder" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="inputCssClass" type="java.lang.String" required="false" rtexprvalue="true" %>



<%--<div class="container">--%>
<div id="${id}" class="dropdown autocomplete-tabs">

<%--Carriers: ${fn:length(firsrtTabOptions)}--%>
        <%--<c:forEach var="insuranceCarrier" items="${firsrtTabOptions}">--%>
            <%--${insuranceCarrier}--%>
        <%--</c:forEach>--%>

        <%--Plans:--%>
        <%--<c:forEach var="insurancePlan" items="${secondTabOptions}">--%>
            <%--${insurancePlan}--%>
        <%--</c:forEach>--%>
            <%-- ///hidden select cssStyle="display: none" --%>
        <wgForm:select path="${firstValuePath}" cssClass="combo-wizard-first-select"
                       data-container-id="${id}" cssStyle="display: none">
            <wgForm:option value=""></wgForm:option>
            <c:forEach var="firstSelectItem" items="${firstTabAllOptions}">
                <wgForm:option value="${firstSelectItem.id}">${firstSelectItem.label}</wgForm:option>
            </c:forEach>
        </wgForm:select>

            <%--cssStyle="display: none"--%>
            <wgForm:select path="${secondValuePath}" cssClass="combo-wizard-second-select"
                           data-container-id="${id}" cssStyle="display: none"
            >
                <wgForm:option value=""></wgForm:option>
                <c:forEach var="secondSelectEntry" items="${secondTabAllOptions}">
                    <c:forEach var="secondSelectItem" items="${secondSelectEntry.value}">
                        <wgForm:option value="${secondSelectItem.id}" data-carrier-id="${secondSelectEntry.key}">${secondSelectItem.secondLabel}, ${secondSelectItem.label}</wgForm:option>
                    </c:forEach>
                </c:forEach>
            </wgForm:select>

            <%-- style="display: none" --%>
        <select class="autocomplete-tabs-select" data-container-id="${id}"
                data-input-placeholder="${inputPlaceholder}"
                data-input-cssClass="${inputCssClass}"
                style="display: none" >
            <option value=""></option>
            <c:forEach var="firstSelectItem" items="${firstTabAllOptions}">
                <option value="carrier-${firstSelectItem.id}" data-carrier-id="${firstSelectItem.id}" data-category="carrier">${firstSelectItem.label}</option>
            </c:forEach>

            <c:forEach var="secondSelectEntry" items="${secondTabAllOptions}">
                <c:forEach var="secondSelectItem" items="${secondSelectEntry.value}">
                    <option value="${secondSelectItem.id}" data-carrier-id="${secondSelectEntry.key}">${secondSelectItem.secondLabel}, ${secondSelectItem.label}</option>
                </c:forEach>
            </c:forEach>

            <%--<option value="1">I'm paying for myself</option>--%>
            <%--<option value="later" data-category="auxiliary-option">I'll choose my insurance later</option>--%>

            <%--<!-- carriers -->--%>
            <%--<option value="carrier-1" data-carrier-id="2" data-category="carrier">Aetna</option>--%>
            <%--<option value="carrier-2" data-carrier-id="3" data-category="carrier">Cigna</option>--%>
            <%--<option value="carrier-3" data-carrier-id="4" data-category="carrier">BCBS</option>--%>

            <!-- plans -->
            <%--<option data-carrier-id="2" value="2">Aetna HMO</option>--%>
            <%--<option data-carrier-id="2" value="3">Aetna NYC</option>--%>
            <%--<option data-carrier-id="2" value="4">Aetna EPO</option>--%>

            <%--<option data-carrier-id="3" value="5">Cigna HMO</option>--%>
            <%--<option data-carrier-id="3" value="6">Cigna NYC</option>--%>
            <%--<option data-carrier-id="3" value="7">Cigna EPO</option>--%>

            <%--<option data-carrier-id="4" value="8">BCBS HMO</option>--%>
            <%--<option data-carrier-id="4" value="9">BCBS NYC</option>--%>
            <%--<option data-carrier-id="4" value="10">BCBS EPO</option>--%>
        </select>

        <!--<input id="dropdownMenuInput" class="autocomplete-tabs-input" />-->
        <button id="dropdownMenuButton" class="btn btn-default dropdown-toggle" type="button" style="display: none">
            <span class="caret"></span></button>
        <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
            <ul class="nav nav-tabs">
                <li class="active"><a data-toggle="tab" href="#menu1">${firstTabTitle}</a></li>
                <li class="not-active"><a href="#menu2" >${secondTabTitle}</a></li>
            </ul>

            <div class="tab-content">
                <div id="menu1" class="tab-pane fade in active">
                    <span>&nbsp;</span>
                    <div class="selected-carrier-container">
                        <span class="selected-carrier-id"></span>
                        <span>&nbsp;</span>
                        <span class="clear-plan">X</span>
                        <hr>
                    </div>
                    <ol class="">
                        <%--<li class="hover-option" data-carrier-id="1">I'm paying for myself</li>--%>
                        <%--<li class="hover-option" data-carrier-id="">I'll choose my insurance later</li>--%>
                        <c:forEach var="firstTabItemSection1" items="${firstTabOptionsSection0}">
                            <li class="carrier-option hover-option" data-carrier-id="${firstTabItemSection1.id}">${firstTabItemSection1.label}</li>
                        </c:forEach>
                    </ol>
                    <hr>
                    <div class="wizard-heading1">${firstTabTitleSection1}</div>
                    <!-- todo when back is ready -->
                    <ol class="">
                        <%--<li class="carrier-option hover-option" data-carrier-id="2">Aetna</li>--%>
                        <%--<li class="carrier-option hover-option" data-carrier-id="3">Cigna</li>--%>
                        <c:forEach var="popularItem" items="${firstTabOptionsSection1}">
                            <li class="carrier-option hover-option" data-carrier-id="${popularItem.id}">${popularItem.label}</li>
                        </c:forEach>
                    </ol>
                    <hr>
                    <div class="wizard-heading1">${firstTabTitleSection2}</div>
                    <ol class="">
                        <c:forEach var="firstTabItem" items="${firstTabOptionsWithoutSection0}">
                            <c:if test="${firstTabItem.firstInLetterSection}">
                                <li class="first-letter-title"
                                    data-first-letter="${firstTabItem.titleLetter}">
                                    ${firstTabItem.titleLetter}
                                </li>
                            </c:if>
                            <li class="carrier-option hover-option"
                                data-first-letter="${firstTabItem.titleLetter}"
                                data-carrier-id="${firstTabItem.id}">
                                ${firstTabItem.label}
                            </li>
                        </c:forEach>
                        <%--<li class="carrier-option hover-option" data-carrier-id="2">Aetna</li>--%>
                        <%--<li class="carrier-option hover-option" data-carrier-id="3">Cigna</li>--%>
                        <%--<li class="carrier-option hover-option" data-carrier-id="4">BCBS</li>--%>
                    </ol>
                </div>
                <div id="menu2" class="planTabContainer tab-pane fade">
                    <div class="selected-carrier-container">
                        <span class="selected-carrier-id"></span>
                        <span>&nbsp;</span>
                        <span class="clear-plan hover-option">X</span>
                    </div>
                    <hr>
                    <div class="wizard-heading1">${secondTabTitleSection1}</div>
                    <ol class="">
                        <!-- TODO when back for popular items is ready -->
                        <%--<li class="plan-option hover-option" data-carrier-id="2" data-plan-id="2">Aetna HMO</li>--%>
                        <%--<li class="plan-option hover-option" data-carrier-id="2" data-plan-id="3">Aetna NYC</li>--%>

                        <%--<li class="plan-option hover-option" data-carrier-id="3" data-plan-id="5">Cigna HMO</li>--%>

                        <%--<li class="plan-option hover-option" data-carrier-id="4" data-plan-id="8">BCBS HMO</li>--%>
                        <%--<li class="plan-option hover-option" data-carrier-id="4" data-plan-id="9">BCBS NYC</li>--%>
                        <c:forEach var="popularEntry" items="${secondTabOptionsSection1}">
                            <c:forEach var="popularItem" items="${popularEntry.value}">
                                <li class="plan-option hover-option" data-carrier-id="${popularEntry.key}" data-plan-id="${popularItem.id}">${popularItem.label}</li>
                            </c:forEach>
                        </c:forEach>
                    </ol>
                    <hr>
                    <div class="wizard-heading1">${secondTabTitleSection2}</div>
                    <ol class="">
                        <c:forEach var="secondTabEntry" items="${secondTabAllOptions}">
                            <div class="carrier-plans-li-container" data-carrier-id="${secondTabEntry.key}">
                            <c:forEach var="secondTabItem" items="${secondTabEntry.value}">
                                <c:if test="${secondTabItem.firstInLetterSection}">
                                    <li class="first-letter-title "
                                        data-first-letter="${secondTabItem.titleLetter}">
                                        ${secondTabItem.titleLetter}
                                    </li>
                                </c:if>
                                <li class="plan-option hover-option"
                                    data-carrier-id="${secondTabEntry.key}"
                                    data-first-letter="${secondTabItem.titleLetter}"
                                    data-plan-id="${secondTabItem.id}">
                                    ${secondTabItem.label}
                                </li>
                            </c:forEach>
                            </div>
                        </c:forEach>
                        <%--<li class="plan-option hover-option" data-carrier-id="2" data-plan-id="2">Aetna HMO</li>--%>
                        <%--<li class="plan-option hover-option" data-carrier-id="2" data-plan-id="3">Aetna NYC</li>--%>
                        <%--<li class="plan-option hover-option" data-carrier-id="2" data-plan-id="4">Aetna EPO</li>--%>

                        <%--<li class="plan-option hover-option" data-carrier-id="3" data-plan-id="5">Cigna HMO</li>--%>
                        <%--<li class="plan-option hover-option" data-carrier-id="3" data-plan-id="6">Cigna NYC</li>--%>
                        <%--<li class="plan-option hover-option" data-carrier-id="3" data-plan-id="7">Cigna EPO</li>--%>

                        <%--<li class="plan-option hover-option" data-carrier-id="4" data-plan-id="8">BCBS HMO</li>--%>
                        <%--<li class="plan-option hover-option" data-carrier-id="4" data-plan-id="9">BCBS NYC</li>--%>
                        <%--<li class="plan-option hover-option" data-carrier-id="4" data-plan-id="10">BCBS EPO</li>--%>
                    </ol>
                </div>
            </div>
        </div>

        <div class="dropdown-selected-plan" aria-labelledby="dropdownMenuButton" style="display: none">
            <div>
                <span class="selected-plan-text"></span>
                <span class="selected-carrier-text"></span>
            </div>
            <div class="choose-different-insurance  hover-option">${chooseDifferentValue}</div>
        </div>
    </div>
<%--</div>--%>

