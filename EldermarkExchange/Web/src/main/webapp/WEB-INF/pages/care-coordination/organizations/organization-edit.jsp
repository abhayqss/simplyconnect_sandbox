<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<jsp:useBean id="orgDto" scope="request" type="com.scnsoft.eldermark.shared.carecoordination.OrganizationDto"/>
<jsp:useBean id="unaffiliated" scope="request" type="java.lang.Boolean"/>
<jsp:useBean id="states" scope="request" type="java.util.List"/>
<jsp:useBean id="primaryFocuses" scope="request" type="java.util.List"/>
<jsp:useBean id="communityTypes" scope="request" type="java.util.List"/>
<jsp:useBean id="levelsOfCare" scope="request" type="java.util.List"/>
<jsp:useBean id="ageGroups" scope="request" type="java.util.List"/>
<jsp:useBean id="servicesTreatmentApproaches" scope="request" type="java.util.List"/>
<jsp:useBean id="emergencyServices" scope="request" type="java.util.List"/>
<jsp:useBean id="languageServices" scope="request" type="java.util.List"/>
<jsp:useBean id="ancillaryServices" scope="request" type="java.util.List"/>
<jsp:useBean id="insuranceCarriers" scope="request" type="java.util.List"/>

<sec:authorize var="isSuperadmin" access="<%=SecurityExpressions.IS_CC_SUPERADMIN%>"/>

<lt class="modal fade" role="dialog" id="createOrgModal" data-backdrop="static">
    <lt:layout cssClass="modal-dialog" role="document" style="width:1000px;">
        <lt:layout cssClass="modal-content">
            <wgForm:form role="form" id="orgForm" commandName="orgDto">
                <wgForm:hidden path="id"/>
                <c:set var="isNew" value="${orgDto.id == null}"/>
                <c:set var="modalTitle" value="${isNew ? 'Create Organization' : 'Edit Organization'}"/>
                <c:set var="createdOrUpdated" value="${isNew ? 'created' : 'updated'}"/>
                <c:set var="submitBtnLabel" value="${isNew ? 'CREATE' : 'SAVE'}"/>
                <c:set var="activeStep" value="0"/>

				<wg:modal-header closeBtn="true">
					<span id="contactHeader">${modalTitle}</span>
				</wg:modal-header>
				<lt:layout cssClass="modal-layout">
					<wg:modal-body cssClass="col-md-12 createOrgBody new-modal-body">
						<wg:wizard id="createOrgWzd" activeStep="${activeStep}">

                            <wg:wizard-header cssClass="nav nav-tabs">
                                <wg:wizard-head-item href="#legalInfoTab" cssClass="organizationWizardLnk"
                                                     linkId="legalInfoHeadLnk"
                                                     linkCssClass="ldr-ui-label ldr-head-lnk table-cell-box modal-tab">
                                    <wg:label cssClass="lnk-text">Legal Info</wg:label>
                                    <lt:layout/>
                                </wg:wizard-head-item>
                                <wg:wizard-head-item href="#marketplaceTab" cssClass="organizationWizardLnk"
                                                     linkId="marketplaceHeadLnk"
                                                     linkCssClass="ldr-ui-label ldr-head-lnk table-cell-box modal-tab">
                                    <wg:label cssClass="lnk-text">MarketPlace</wg:label>
                                    <lt:layout/>
                                </wg:wizard-head-item>
                                <c:if test="${isSuperadmin}">
                                    <wg:wizard-head-item href="#affiliateRelationshipTab"
                                                         cssClass="organizationWizardLnk"
                                                         linkId="affiliateRelationshipHeadLnk"
                                                         linkCssClass="ldr-ui-label ldr-head-lnk table-cell-box modal-tab">
                                        <wg:label cssClass="lnk-text">Affiliate Relationship</wg:label>
                                        <lt:layout/>
                                    </wg:wizard-head-item>
                                </c:if>
                            </wg:wizard-header>

							<wg:wizard-content
											cssClass="organizationWizardContent col-md-12 nav-tab-box-shadow content-indent">
								<%-- === STEP 1 : Legal Info === --%>
								<wg:wizard-content-item id="legalInfoTab" cssClass="legalInfoTab row">

									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead">General Data</span>
									</lt:layout>

									<lt:layout cssClass="col-md-12 form-group input-field">
										<wg:label _for="org.name">Organization Name*</wg:label>
										<wgForm:input path="name"
																	id="org.name"
																	cssClass="form-control"
										/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:infoLabel text="Organization OID* " _for="orgOid"
																	id="oidHelp"
																	data_content='<span class="info-popover">Unique Organization Number throughout the system</span>'/>
										<wgForm:input path="oid"
																	id="orgOid"
																	disabled="${!isNew}"
																	maxlength="30"
																	cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group">
										<wg:infoLabel text="Company ID*" _for="orgCompanyCode"
																	id="companyIdHelp"
																	data_content='<span class="info-popover">Value of the field will be used on the "Login" page.</span>'/>
										<wgForm:input path="loginCompanyId"
																	id="orgCompanyCode"
																	disabled="${!isNew}"
																	maxlength="10"
																	cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="org.email">Email*</wg:label>
										<wgForm:input path="email"
																	id="org.email"
																	maxlength="100"
																	cssClass="form-control"
										/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="org.phone">Phone Number*</wg:label>
										<wgForm:input path="phone"
																	id="org.phone"
																	cssClass="form-control"
																	maxlength="16"
																	disabled="${unaffiliated}"
										/>
									</lt:layout>

									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead">Address</span>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgStreet"
															cssClass="addressPart">Street<span style="display: none">*</span></wg:label>
										<wgForm:input path="street"
																	id="orgStreet"
																	maxlength="200"
																	cssClass="form-control addressPart"
																	disabled="${unaffiliated}"
										/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgCity"
															cssClass="addressPart">City<span style="display: none">*</span></wg:label>
										<wgForm:input path="city"
																	id="orgCity"
																	maxlength="100"
																	cssClass="form-control addressPart"
																	disabled="${unaffiliated}"
										/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgStateId"
															cssClass="addressPart">State<span style="display: none">*</span></wg:label>
										<wgForm:select path="stateId"
																	 id="orgStateId"
																	 disabled="${unaffiliated}"
																	 title="Select State"
																	 cssClass="form-control spicker addressPart dropdown-8">
											<option data-hidden="false"></option>
											<c:forEach var="item" items="${states}">
												<wgForm:option value="${item.id}" label="${item.label}"/>
											</c:forEach>
										</wgForm:select>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgPostalCode"
															cssClass="addressPart">Zip Code<span style="display: none">*</span></wg:label>
										<wgForm:input path="postalCode"
																	id="orgPostalCode"
																	cssClass="form-control addressPart"
																	disabled="${unaffiliated}"
										/>
									</lt:layout>

									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead">Organization Logo </span>
									</lt:layout>

									<lt:layout cssClass="col-md-12 form-group file-input-layout ">
										<input type="hidden" name="logoChanged" value="0" id="logoChanged"/>
										<input type="hidden" name="logoRemoved" value="0" id="logoRemoved"/>
										<wg:infoLabel text="Select file" _for="logo" id="companyLogoHelp"
																	data_content='<ul class="ul-padding">
                                            <li>The maximum file size for uploads is <b>1 MB</b></li>
                                            <li>Only image files (<b>JPG, GIF, PNG</b>) are allowed</li>
                                            <li>Recommended aspect ration is <b>3:1</b></li>
                                            <li>Recommended image resolution is <b>147x42</b></li>
                                            <li class="label-form-notes">Note: Changes will be applied after re-login</li>
                                        </ul>'/>
										<wg:file name="logo" cssClass="filestyle form-control " buttonText="BROWSE"
														 id="logoOrgInput"
														 dataPlaceholder="${orgDto.mainLogoPath}"/>
										<wg:label _for="logo" cssClass="documentLbl format-info-label">
											Supported file types: JPG, PNG, GIF | Max 1mb
										</wg:label>
									</lt:layout>
								</wg:wizard-content-item>

								<%-- === STEP 2 : MarketPlace === --%>
								<wg:wizard-content-item id="marketplaceTab" cssClass="marketplaceTab row">
									<lt:layout cssClass="col-md-12">
										<wg:label cssClass="form-check-label form-checkbox-label">
											<wgForm:checkbox cssClass="form-check-input right12"
																			 path="marketplace.confirmVisibility"
																			 id="orgConfirmVisibilityInMarketplace"/>
											<wg:infoLabel
															text="Confirm that organization will be visible in MarketPlace"
															id="confirmCompanyHelp" cssClass="normalFont"
															data_content='<span class="info-popover">Your organization will be available in search results in PHR app for Consumers.</span>'/>
										</wg:label>
									</lt:layout>

									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead top30">Basic Info</span>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgPrimaryFocus">Primary focus*</wg:label>
										<wgForm:select path="marketplace.primaryFocusIds"
																	 id="orgPrimaryFocus"
																	 cssClass="form-control spicker dropdown-10"
																	 title="Select Value"
																	 required="true"
																	 multiple="true">
											<option data-hidden="true"></option>
											<c:forEach var="item" items="${primaryFocuses}">
												<wgForm:option value="${item.id}" label="${item.label}"/>
											</c:forEach>
										</wgForm:select>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgCommunityType">Community type*</wg:label>
										<wgForm:select path="marketplace.communityTypeIds"
																	 id="orgCommunityType"
																	 class="form-control spicker dropdown-10"
																	 title="Select Value"
																	 required="true"
																	 multiple="true">
											<option data-hidden="true"></option>
											<c:forEach var="items" items="${communityTypes}">
												<optgroup>
													<c:forEach var="item" items="${items}">
														<wgForm:option cssClass="optionSpace" value="${item.id}" label="${item.label}"/>
													</c:forEach>
												</optgroup>
											</c:forEach>
										</wgForm:select>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgLevelsOfCare">Levels of Care</wg:label>
										<wgForm:select path="marketplace.levelOfCareIds"
																	 id="orgLevelsOfCare"
																	 cssClass="form-control spicker dropdown-10"
																	 title="Select Value"
																	 multiple="true">
											<option data-hidden="true"></option>
											<c:forEach var="item" items="${levelsOfCare}">
												<wgForm:option value="${item.id}" label="${item.label}"/>
											</c:forEach>
											<wgForm:option value="-1" label="None"/>
										</wgForm:select>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgAgeGroupsAccepted">Age Groups Accepted</wg:label>
										<wgForm:select path="marketplace.ageGroupIds"
																	 id="orgAgeGroupsAccepted"
																	 cssClass="form-control spicker dropdown-10"
																	 title="Select Value"
																	 multiple="true">
											<option data-hidden="true"></option>
											<wgForm:option value="0" label="All"/>
											<c:forEach var="item" items="${ageGroups}">
												<wgForm:option value="${item.id}" label="${item.label}"/>
											</c:forEach>
										</wgForm:select>
									</lt:layout>

									<lt:layout cssClass="col-md-12 no-horizontal-padding top15">
										<span class="sectionHead">Services</span>
									</lt:layout>

									<lt:layout cssClass="form-group col-md-12 input-field">
										<wg:label
														_for="orgServicesSummaryDescription">Services Summary Description*</wg:label>
										<wgForm:textarea path="marketplace.servicesSummaryDescription"
																		 name="orgServicesSummaryDescription"
																		 id="orgServicesSummaryDescription"
																		 placeholder="Maximum 500 symbols"
																		 minlength="2" maxlength="500"
																		 required="true"
																		 cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgServicesTreatmentApproaches">Services / Treatment Approaches*</wg:label>
										<wgForm:select path="marketplace.serviceTreatmentApproachIds"
																	 id="orgServicesTreatmentApproaches"
																	 cssClass="form-control spicker dropdown-10"
																	 title="Select Value"
                                                                     required="true"
																	 multiple="true">
											<option data-hidden="true"></option>
											<c:forEach var="items" items="${servicesTreatmentApproaches}">
												<optgroup>
													<c:forEach var="item" items="${items}">
														<wgForm:option cssClass="optionSpace" value="${item.id}" label="${item.label}"/>
													</c:forEach>
												</optgroup>
											</c:forEach>
										</wgForm:select>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgEmergencyServices">Emergency Services</wg:label>
										<wgForm:select path="marketplace.emergencyServiceIds"
																	 id="orgEmergencyServices"
																	 cssClass="form-control spicker dropdown-10"
																	 title="Select Value"
																	 multiple="true">
											<option data-hidden="true"></option>
											<wgForm:option value="0" label="All"/>
											<c:forEach var="item" items="${emergencyServices}">
												<wgForm:option value="${item.id}" label="${item.label}"/>
											</c:forEach>
										</wgForm:select>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgLanguageServices">Language Services</wg:label>
										<wgForm:select path="marketplace.languageServiceIds"
																	 id="orgLanguageServices"
																	 cssClass="form-control spicker dropdown-10"
																	 title="Select Value"
																	 multiple="true">
											<option data-hidden="true"></option>
											<c:forEach var="item" items="${languageServices}">
												<wgForm:option value="${item.id}" label="${item.label}"/>
											</c:forEach>
											<wgForm:option value="-1" label="None"/>
										</wgForm:select>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgAncillaryServices">Ancillary Services</wg:label>
										<wgForm:select path="marketplace.ancillaryServiceIds"
																	 id="orgAncillaryServices"
																	 cssClass="form-control spicker dropdown-10"
																	 title="Select Value"
																	 multiple="true">
											<option data-hidden="true"></option>
											<wgForm:option value="0" label="All"/>
											<c:forEach var="item" items="${ancillaryServices}">
												<wgForm:option value="${item.id}" label="${item.label}"/>
											</c:forEach>
											<wgForm:option value="-1" label="None"/>
										</wgForm:select>
									</lt:layout>

                                <lt:layout cssClass="col-md-12 no-horizontal-padding top15">
                                    <span class="sectionHead">Program requirements</span>
                                </lt:layout>

                                <lt:layout cssClass="form-group col-md-12">
                                    <wg:label _for="orgPrerequisite">Prerequisite</wg:label>
                                    <wgForm:textarea path="marketplace.prerequisite"
                                                     name="orgPrerequisite"
                                                     id="orgPrerequisite"
                                                     placeholder="Maximum 5000 symbols"
                                                     maxlength="5000"
                                                     cssClass="form-control"/>
                                </lt:layout>

                                <lt:layout cssClass="form-group col-md-12">
                                    <wg:label _for="orgExclusion">Exclusion</wg:label>
                                    <wgForm:textarea path="marketplace.exclusion"
                                                     name="orgExclusion"
                                                     id="orgExclusion"
                                                     placeholder="Maximum 5000 symbols"
                                                     maxlength="5000"
                                                     cssClass="form-control"/>
                                </lt:layout>

                                <lt:layout cssClass="col-md-12 no-horizontal-padding top15">
                                    <span class="sectionHead">Appointments</span>
                                </lt:layout>

									<lt:layout cssClass="col-md-12 bottom15">
										<wg:label cssClass="form-check-label form-checkbox-label normalFont">
											<wgForm:checkbox cssClass="form-check-input right12"
																			 path="marketplace.allowAppointments"
																			 id="allowAppointments"/>
											Allow appointments via PHR app
										</wg:label>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="orgAppointmentsEmail">Email</wg:label>
										<wgForm:input path="marketplace.appointmentsEmail"
																	id="orgAppointmentsEmail"
																	maxlength="150"
																	cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field"
														 id="orgAppointmentsSecureEmailDiv">
										<wg:label _for="orgAppointmentsSecureEmail">Secure Email</wg:label>
										<wgForm:input path="marketplace.appointmentsSecureEmail"
																	id="orgAppointmentsSecureEmail"
																	maxlength="150"
																	cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead">Payment & Insurance Accepted</span>
									</lt:layout>

									<lt:layout cssClass="col-md-12 form-group has-feedback">
										<wg:label _for="searchNetworks">Search Network by Name</wg:label>
										<wg:searchInput path="marketplace.selectedInNetworkInsuranceIds" id="searchNetworks"/>
									</lt:layout>

								</wg:wizard-content-item>

								<%-- === STEP 3 : Affiliate Relationship === --%>
								<c:if test="${isSuperadmin}">
									<wg:wizard-content-item id="affiliateRelationshipTab"
																					cssClass="affiliateRelationshipTab row">
										<lt:layout cssClass="col-md-12 no-horizontal-padding affiliatedOrgs">
											<lt:layout cssClass="sectionHead">Affiliated Organizations & Communities</lt:layout>
											<wg:infoLabel id="affiliatedOrgHelp" cssClass="normalFont no-margin "
																		data_content='<span class="info-popover">This option allows users associated with the affiliated organizations or communities to receive notifications about events coming to organization that is being ${createdOrUpdated}.</span>'/>
											<lt:layout cssClass="affiliatedOrgsButton"/>
										</lt:layout>

										<c:forEach var="detail" items="${orgDto.affiliatedDetails}" varStatus="stat">
											<lt:layout
															cssClass="col-md-12 form-group no-horizontal-padding affiliatedDetails">
												<span class="sectionHead top13 relationship-header">Relationship #${stat.index + 1}</span>
												<lt:layout cssClass="closeAffiliatedOrgsButton"/>
												<lt:layout id="communityListLayout" cssClass="col-md-12 form-group input-field"
																	 style="padding-top: 5px">
													<wg:label _for="communitySelect_${stat.index}">
														I want to share information about the events coming to*
													</wg:label>
													<wgForm:select class="form-control communitySelect spicker dropdown-10"
																				 id="communitySelect_${stat.index}"
																				 multiple="true"
																				 path="affiliatedDetails[${stat.index}].communityIds"
																				 title="Select Community"
																				 itemValue="id" items="${communities}"
																				 itemLabel="name">
														<option data-hidden="true"></option>
													</wgForm:select>
												</lt:layout>

												<lt:layout cssClass="col-md-6 form-group input-field">
													<wg:label
																	_for="affOrgSelect_${stat.index}">Share with Organization*</wg:label>
													<wgForm:select class="form-control affOrgSelect spicker dropdown-10"
																				 id="affOrgSelect_${stat.index}"
																				 path="affiliatedDetails[${stat.index}].affOrgId"
																				 title="Select Organization"
																				 itemValue="id" items="${affOrganizations}"
																				 itemLabel="label">
														<option data-hidden="true"></option>
													</wgForm:select>
												</lt:layout>
												<lt:layout cssClass="col-md-6 form-group input-field">
													<wg:label
																	_for="affCommunitySelect_${stat.index}">Share with Community*</wg:label>
													<wgForm:select class="form-control affCommunitySelect spicker dropdown-10"
																				 id="affCommunitySelect_${stat.index}"
																				 multiple="true"
																				 path="affiliatedDetails[${stat.index}].affCommunitiesIds"
																				 title="Select Community"
																				 itemValue="id" items="${affCommunities[stat.index]}"
																				 itemLabel="name">
														<option data-hidden="true"></option>
													</wgForm:select>
												</lt:layout>
											</lt:layout>
										</c:forEach>

										<lt:layout
														cssClass="col-md-12 form-group no-horizontal-padding affiliatedDetails hidden">
											<span class="sectionHead top13 relationship-header">Relationship</span>
											<lt:layout cssClass="closeAffiliatedOrgsButton top13"/>
											<lt:layout id="communityListLayout" cssClass="col-md-12 form-group input-field"
																 style="padding-top: 5px">
												<label class="ldr-ui-label"
															 for="communitySelectTemplate">I want to share information about
													the events coming to*</label>
												<select class="form-control spicker-template communitySelect"
																multiple="true"
																id="communitySelectTemplate"
																title="Select Community"
																<c:if test="${isNew}">disabled</c:if>>
													<option data-hidden="true"></option>
													<c:forEach var="item" items="${communities}">
														<option value="${item.id}">${item.name}</option>
													</c:forEach>
												</select>
											</lt:layout>

											<lt:layout cssClass="col-md-6 form-group input-field">
												<label class="ldr-ui-label"
															 for="affOrgSelectTemplate">Share with Organization*</label>
												<select class="form-control spicker-template affOrgSelect"
																id="affOrgSelectTemplate"
																title="Select Organization"
																<c:if test="${isNew}">disabled</c:if>>
													<option data-hidden="true"></option>
													<c:forEach var="item" items="${affOrganizations}">
														<option value="${item.id}">${item.label}</option>
													</c:forEach>
												</select>
											</lt:layout>
											<lt:layout cssClass="col-md-6 form-group input-field">
												<label class="ldr-ui-label"
															 for="affCommunitySelectTemplate">Share with Community*</label>
												<select class="form-control spicker-template affCommunitySelect"
																multiple="true"
																id="affCommunitySelectTemplate"
																title="Select Community"
																<c:if test="${isNew}">disabled</c:if>>
													<option data-hidden="true"></option>
												</select>
											</lt:layout>

										</lt:layout>
									</wg:wizard-content-item>
								</c:if>
							</wg:wizard-content>

						</wg:wizard>
					</wg:modal-body>

					<wg:modal-footer-btn-group cssClass="wzBtns">
						<wg:button id="cancelBtn"
											 name="cancelBtn"
											 domType="link"
											 dataToggle="modal"
											 dataTarget="#createOrgModal"
											 cssClass="btn-default cancelBtn legalInfoStep">
							CANCEL
						</wg:button>
						<wg:button id="nextBtn" name="nextBtn" domType="link" cssClass="btn-primary next legalInfoStep">
							NEXT
						</wg:button>

						<wg:button id="backBtn" name="backBtn" domType="link"
											 cssClass="btn-default previous marketplaceStep affiliateRelationshipStep">
							BACK
						</wg:button>
						<c:choose>
							<c:when test="${isSuperadmin}">
								<wg:button id="nextBtn2" name="nextBtn2" domType="link"
													 cssClass="btn-primary next marketplaceStep">
									NEXT
								</wg:button>
								<wg:button id="createBtn2" name="createBtn2" domType="link" dataToggle="modal"
													 cssClass="btn-primary finish affiliateRelationshipStep">
									${submitBtnLabel}
								</wg:button>
							</c:when>
							<c:otherwise>
								<wg:button id="createBtn" name="createBtn" domType="link" dataToggle="modal"
													 cssClass="btn-primary finish marketplaceStep">
									${submitBtnLabel}
								</wg:button>
							</c:otherwise>
						</c:choose>
					</wg:modal-footer-btn-group>
				</lt:layout>


			</wgForm:form>
		</lt:layout>
	</lt:layout>
</lt>