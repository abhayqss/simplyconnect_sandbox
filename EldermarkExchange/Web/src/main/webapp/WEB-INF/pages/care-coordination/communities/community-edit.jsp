<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="communityDto" scope="request"
						 type="com.scnsoft.eldermark.shared.carecoordination.community.CommunityCreateDto"/>
<jsp:useBean id="organizationName" scope="request" type="java.lang.String"/>
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

<div class="modal fade" role="dialog" id="createCommunityModal" data-backdrop="static">
	<lt:layout cssClass="modal-dialog" role="document" style="width:1000px;">
		<lt:layout cssClass="modal-content">
			<wgForm:form role="form" id="communityForm" commandName="communityDto">
				<wgForm:hidden path="id"/>
				<c:set var="isNew" value="${communityDto.id == null}"/>
				<c:set var="modalTitleCreate" value="Create community for ${organizationName} organization"/>
				<c:set var="modalTitle" value="${isNew ? modalTitleCreate : 'Edit Community Details'}"/>
				<c:set var="submitBtnLabel" value="${isNew ? 'CREATE' : 'SAVE'}"/>
				<c:set var="activeStep" value="0"/>

				<wg:modal-header closeBtn="true">
					<span id="communityCreateHeader">${modalTitle}</span>
				</wg:modal-header>
				<lt:layout cssClass="modal-layout">
					<wg:modal-body cssClass="col-md-12 createCommunityBody new-modal-body">

						<wg:wizard id="createCommWzd" activeStep="${activeStep}">

							<wg:wizard-header cssClass="nav nav-tabs">
								<wg:wizard-head-item href="#legalInfoTab" cssClass="communityWizardLnk"
																		 linkId="legalInfoHeadLnk"
																		 linkCssClass="ldr-ui-label ldr-head-lnk table-cell-box modal-tab">
									<wg:label cssClass="lnk-text">Legal Info</wg:label>
									<lt:layout/>
								</wg:wizard-head-item>
								<wg:wizard-head-item href="#marketplaceTab" cssClass="communityWizardLnk"
																		 linkId="marketplaceHeadLnk"
																		 linkCssClass="ldr-ui-label ldr-head-lnk table-cell-box modal-tab">
									<wg:label cssClass="lnk-text">Marketplace</wg:label>
									<lt:layout/>
								</wg:wizard-head-item>
							</wg:wizard-header>

							<wg:wizard-content cssClass="communityWizardContent col-md-12 nav-tab-box-shadow content-indent">
								<%-- === STEP 1 : Legal Info === --%>
								<wg:wizard-content-item id="legalInfoTab" cssClass="legalInfoTab row">

									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead top30">General Data</span>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="communityName">Community Name*</wg:label>
										<wgForm:input path="name"
																	id="communityName"
																	cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:infoLabel text="Community OID* " _for="communityOid"
																	id="oidHelp"
																	data_content='<span class="info-popover">Unique Community Number throughout the system</span>'/>
										<wgForm:input path="oid"
																	id="communityOid"
																	disabled="${!isNew}"
																	cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="communityEmail">Email*</wg:label>
										<wgForm:input path="email"
																	id="communityEmail"
																	cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="communityPhone">Phone*</wg:label>
										<wgForm:input path="phone"
																	id="communityPhone"
																	cssClass="form-control"
																	maxlength="16"
																	disabled="${unaffiliated}"/>
									</lt:layout>

									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead">Address</span>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="communityStreet"
															cssClass="addressPart">Street<span style="display: none">*</span></wg:label>
										<wgForm:input path="street"
																	id="communityStreet"
																	maxlength="255"
																	cssClass="form-control addressPart"
																	disabled="${unaffiliated}"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="communityCity"
															cssClass="addressPart">City<span style="display: none">*</span></wg:label>
										<wgForm:input path="city"
																	id="communityCity"
																	maxlength="100"
																	cssClass="form-control addressPart"
																	disabled="${unaffiliated}"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="communityStateId"
															cssClass="addressPart">State<span style="display: none">*</span></wg:label>
										<wgForm:select path="stateId"
																	 name="stateId"
																	 id="communityStateId"
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
										<wg:label _for="communityPostalCode"
															cssClass="addressPart">Zip Code<span style="display: none">*</span></wg:label>
										<wgForm:input path="postalCode"
																	id="communityPostalCode"
																	cssClass="form-control addressPart"
																	disabled="${unaffiliated}"
										/>
									</lt:layout>


									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead">Community Logo </span>
									</lt:layout>

									<lt:layout cssClass="col-md-12 form-group file-input-layout input-field">
										<input type="hidden" name="logoChanged" value="0" id="logoChanged"/>
										<input type="hidden" name="logoRemoved" value="0" id="logoRemoved"/>
										<wg:infoLabel text="Select file" _for="logo" id="communityLogoHelp"
																	data_content='<ul class="ul-padding">
                                            <li>The maximum file size for uploads is <b>1 MB</b></li>
                                            <li>Only image files (<b>JPG, GIF, PNG</b>) are allowed</li>
                                            <li>Recommended aspect ration is <b>3:1</b></li>
                                            <li>Recommended image resolution is <b>147x42</b></li>
                                            <li class="label-form-notes">Note: Changes will be applied after re-login</li>
                                        </ul>'/>
										<wg:file name="logo" cssClass="filestyle form-control" buttonText="BROWSE"
														 id="logoCommunityInput"
														 dataPlaceholder="${communityDto.mainLogoPath}"/>
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
																			 id="commConfirmVisibilityInMarketplace"/>
											<wg:infoLabel
															text="Confirm that community will be visible in MarketPlace"
															id="confirmCommunityHelp" cssClass="normalFont"
															data_content='<span class="info-popover">Your community will be available in search results in PHR app for Consumers.</span>'/>
										</wg:label>
									</lt:layout>

									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead top30">Basic Info</span>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="commPrimaryFocus">Primary focus*</wg:label>
										<wgForm:select path="marketplace.primaryFocusIds"
																	 id="commPrimaryFocus"
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
										<wg:label _for="commCommunityType">Community type*</wg:label>
										<wgForm:select path="marketplace.communityTypeIds"
																	 id="commCommunityType"
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
										<wg:label _for="commLevelsOfCare">Levels of Care</wg:label>
										<wgForm:select path="marketplace.levelOfCareIds"
																	 id="commLevelsOfCare"
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
										<wg:label _for="commAgeGroupsAccepted">Age Groups Accepted</wg:label>
										<wgForm:select path="marketplace.ageGroupIds"
																	 id="commAgeGroupsAccepted"
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

									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead">Services</span>
									</lt:layout>

									<lt:layout cssClass="form-group col-md-12 input-field">
										<wg:label _for="commServicesSummaryDescription">Services Summary Description*</wg:label>
										<wgForm:textarea path="marketplace.servicesSummaryDescription"
																		 name="commServicesSummaryDescription"
																		 id="commServicesSummaryDescription"
																		 placeholder="Maximum 500 symbols"
																		 minlength="2" maxlength="500"
																		 required="true"
																		 cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="commServicesTreatmentApproaches">Services / Treatment Approaches*</wg:label>
										<wgForm:select path="marketplace.serviceTreatmentApproachIds"
																	 id="commServicesTreatmentApproaches"
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
										<wg:label _for="commEmergencyServices">Emergency Services</wg:label>
										<wgForm:select path="marketplace.emergencyServiceIds"
																	 id="commEmergencyServices"
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
										<wg:label _for="commLanguageServices">Language Services</wg:label>
										<wgForm:select path="marketplace.languageServiceIds"
																	 id="commLanguageServices"
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
										<wg:label _for="commAncillaryServices">Ancillary Services</wg:label>
										<wgForm:select path="marketplace.ancillaryServiceIds"
																	 id="commAncillaryServices"
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
                                    <wg:label _for="commPrerequisite">Prerequisite</wg:label>
                                    <wgForm:textarea path="marketplace.prerequisite"
                                                     name="commPrerequisite"
                                                     id="commPrerequisite"
                                                     placeholder="Maximum 5000 symbols"
                                                     maxlength="5000"
                                                     cssClass="form-control"/>
                                </lt:layout>

                                <lt:layout cssClass="form-group col-md-12">
                                    <wg:label _for="commExclusion">Exclusion</wg:label>
                                    <wgForm:textarea path="marketplace.exclusion"
                                                     name="commExclusion"
                                                     id="commExclusion"
                                                     placeholder="Maximum 5000 symbols"
                                                     maxlength="5000"
                                                     cssClass="form-control"/>
                                </lt:layout>

                                <lt:layout cssClass="col-md-12 no-horizontal-padding top15">
                                    <span class="sectionHead">Appointments</span>
                                </lt:layout>

									<lt:layout cssClass="col-md-12 bottom15">
										<wg:label cssClass="form-check-label form-checkbox-label normalFont">
											<wgForm:checkbox cssClass="form-check-input right15"
																			 path="marketplace.allowAppointments"
																			 id="allowAppointments"/>
											Allow appointments via PHR app
										</wg:label>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="commAppointmentsEmail">Email</wg:label>
										<wgForm:input path="marketplace.appointmentsEmail"
																	id="commAppointmentsEmail"
																	maxlength="150"
																	cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-6 form-group input-field">
										<wg:label _for="commAppointmentsSecureEmail">Secure Email</wg:label>
										<wgForm:input path="marketplace.appointmentsSecureEmail"
																	id="commAppointmentsSecureEmail"
																	maxlength="150"
																	cssClass="form-control"/>
									</lt:layout>

									<lt:layout cssClass="col-md-12 no-horizontal-padding">
										<span class="sectionHead">Payment & Insurance Accepted</span>
									</lt:layout>
									<lt:layout cssClass="col-md-12 form-group input-field">
										<wg:label _for="searchNetworks">Search Network by Name</wg:label>
										<wg:searchInput path="marketplace.selectedInNetworkInsuranceIds" id="searchNetworks"/>
									</lt:layout>
								</wg:wizard-content-item>
							</wg:wizard-content>
						</wg:wizard>

					</wg:modal-body>
					<wg:modal-footer-btn-group cssClass="wzBtns">
						<wg:button id="cancelBtn"
											 name="cancelBtn"
											 domType="link"
											 dataToggle="modal"
											 dataTarget="#createCommunityModal"
											 cssClass="btn-default cancelBtn legalInfoStep">
							CANCEL
						</wg:button>
						<wg:button id="nextBtn" name="nextBtn" domType="link" cssClass="btn-primary next legalInfoStep">
							NEXT
						</wg:button>

						<wg:button id="backBtn" name="backBtn" domType="link"
											 cssClass="btn-default previous marketplaceStep">
							BACK
						</wg:button>
						<wg:button name="saveCommunity"
											 id="saveCommunity"
											 domType="link"
											 dataToggle="modal"
											 cssClass="btn-primary finish marketplaceStep">
							${submitBtnLabel}
						</wg:button>
					</wg:modal-footer-btn-group>
				</lt:layout>

			</wgForm:form>
		</lt:layout>
	</lt:layout>
</div>