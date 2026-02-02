import React, { Component } from "react";

import cn from "classnames";
import PropTypes from "prop-types";

import { chain, compose, isBoolean, isNumber, map, noop } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { Link, withRouter } from "react-router-dom";

import { Button, UncontrolledTooltip as Tooltip } from "reactstrap";

import { withDownloadingStatusInfoToast } from "hocs";

import { withAssessmentUtils } from "hocs/clients";

import { Dropdown, ErrorViewer, Loader, Modal, ScrollTop, Tabs } from "components";

import { SuccessDialog, WarningDialog } from "components/dialogs";

import { Detail as BaseDetail } from "components/business/common";

import NoteEditor from "../NoteEditor/NoteEditor";
import AppointmentViewer from "containers/Appointments/AppointmentViewer/AppointmentViewer";
import MedicationViewer from "containers/Clients/Clients/Medications/MedicationViewer/MedicationViewer";
import IncidentReportEditor from "containers/IncidentReports/IncidentReportEditor/IncidentReportEditor";

import LoadCanViewIrAction from "actions/events/LoadCanViewIrAction";
import LoadEventDetailsAction from "actions/events/LoadEventDetailsAction";
import LoadCanAddEventNoteAction from "actions/events/LoadCanAddEventNoteAction";
import LoadAssessmentTypesAction from "actions/directory/LoadAssessmentTypesAction";

import incidentReportDetailsActions from "redux/incident/report/details/incidentReportDetailsActions";
import eventDetailsActions from "redux/event/details/eventDetailsActions";

import { ReactComponent as AddItem } from "images/add-item.svg";

import { DateUtils as DU, getAddress, hyphenate, isEmpty, isNotEmpty } from "lib/utils/Utils";

import { isNotEmptyOrBlank } from "lib/utils/ObjectUtils";

import { path } from "lib/utils/ContextUtils";
import { Response } from "lib/utils/AjaxUtils";

import {
  ALLOWED_FILE_FORMATS,
  ASSESSMENT_TYPES,
  E_SIGN_STATUSES,
  RESPONSIVE_BREAKPOINTS,
  SERVER_ERROR_CODES,
} from "lib/Constants";

import EventNotes from "./EventNotes/EventNotes";
import EventNotifications from "./EventNotifications/EventNotifications";

import "./EventDetails.scss";

const SECTIONS = {
  CLIENT_INFO: { name: "client", title: "Client Info" },
  ESSENTIALS: { name: "essentials", title: "Event Essentials" },
  DESCRIPTION: { name: "description", title: "Event Description" },
  TREATMENT: { name: "treatment", title: "Treatment Details" },
  RESPONSIBLE_MANAGER: { name: "responsibleManager", title: "Responsible Manager" },
  REGISTERED_NURSE: { name: "registeredNurse", title: "Registered Nurse (RN)" },
  PATIENT_VISIT: { name: "patientVisit", title: "Patient Visit" },
  INSURANCE: { name: "insurances", title: "Insurance" },
  GUARANTOR: { name: "guarantors", title: "Guarantor" },
  PROCEDURES: { name: "procedures", title: "Procedures" },
  DIAGNOSIS: { name: "diagnoses", title: "Diagnoses" },
  ALLERGIES: { name: "allergies", title: "Allergies" },
};

const { SIGNED } = E_SIGN_STATUSES;

const { format, formats } = DU;

const DATE_FORMAT = formats.americanMediumDate;
const DATE_TIME_FORMAT = formats.longDateMediumTime12;
const DATE_TIME_TIMEZONE_FORMAT = formats.longDateMediumTime12TimeZone;

const { TABLET_PORTRAIT } = RESPONSIVE_BREAKPOINTS;

const TAB = {
  EVENTS: 0,
  NOTIFICATIONS: 1,
  NOTES: 2,
};

const INCIDENT_REPORT_TAB = {
  EDIT: 0,
  VIEW: 1,
  DOWNLOAD: 2,
};

const TAB_TITLE = {
  [TAB.EVENTS]: "Event Description",
  [TAB.NOTIFICATIONS]: "Notifications Sent",
  [TAB.NOTES]: "Related Notes",
};

const TAB_TITLE_ADAPTED = {
  [TAB.EVENTS]: "Description",
  [TAB.NOTIFICATIONS]: "Notifications",
  [TAB.NOTES]: "Notes",
};

const INCIDENT_REPORT_TAB_TITLE = {
  [INCIDENT_REPORT_TAB.EDIT]: "Edit incident report",
  [INCIDENT_REPORT_TAB.VIEW]: "View incident report",
  [INCIDENT_REPORT_TAB.DOWNLOAD]: "Download Pdf",
};

const { PDF } = ALLOWED_FILE_FORMATS;

const { GAD7, PHQ9, IN_HOME, CARE_MGMT, IN_HOME_CARE, COMPREHENSIVE } = ASSESSMENT_TYPES;

function isIgnoredError(e = {}) {
  return [SERVER_ERROR_CODES.ACCESS_DENIED, SERVER_ERROR_CODES.ACCOUNT_INACTIVE].includes(e.code);
}

function mapStateToProps(state) {
  const { ir, note, details } = state.event;

  return {
    data: details.data,
    error: details.error,
    isFetching: details.isFetching,
    fetchCount: details.fetchCount,
    shouldReload: details.shouldReload,

    canViewIr: ir.can.view.value,
    canAddNote: note.can.add.value,
    canViewNotes: state.note.can.view.value,
    isClientActive: state.client?.details?.data?.isActive || details?.data?.client?.isActive,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(eventDetailsActions, dispatch),
      incidentReport: {
        details: bindActionCreators(incidentReportDetailsActions, dispatch),
      },
    },
  };
}

function Detail({ title, children, className, titleClassName, valueClassName }) {
  return (
    isNotEmpty(children) && (
      <BaseDetail
        title={title}
        titleClassName={cn("EventDetail-Title", titleClassName)}
        valueClassName={cn("EventDetail-Value", valueClassName)}
        className="EventDetail"
      >
        {children}
      </BaseDetail>
    )
  );
}

function SubDetail({ title, value }) {
  return (
    isNotEmpty(value) && (
      <div className="EventSubDetail">
        <span className="EventSubDetail-Title">{title}</span>
        <span className="EventSubDetail-Value">{value}</span>
      </div>
    )
  );
}

function YesNoWithoutBlank({ value, title }) {
  return isBoolean(value) ? <Detail title={title}>{value ? "Yes" : "No"}</Detail> : null;
}

class EventDetails extends Component {
  static propTypes = {
    eventId: PropTypes.number,
    clientId: PropTypes.number,
    organizationId: PropTypes.number,

    onLoadSuccess: PropTypes.func,
    onLoadFailure: PropTypes.func,
    onSaveNoteSuccess: PropTypes.func,
    onSaveNoteSuccessDialogOpen: PropTypes.func,
    isWorkflowEvent: PropTypes.bool,
  };

  static defaultProps = {
    onLoadSuccess: noop,
    onLoadFailure: noop,
    onSaveNoteSuccess: noop,
    onSaveNoteSuccessDialogOpen: noop,
  };

  state = {
    tab: 0,
    anchors: [],
    isNoteEditorOpen: false,
    isMedicationViewerOpen: false,
    isAppointmentViewerOpen: false,
    isIncidentReportEditorOpen: false,
    isSaveNoteSuccessDialogOpen: false,
    isDocumentDeletedDialogOpen: false,
  };

  componentWillUnmount() {
    this.actions.clear();
  }

  onResetError = () => {
    this.actions.clearError();
  };

  onChangeTab = (tab) => {
    this.changeTab(tab);
  };

  onChangeIncidentReportTab = (tab) => {
    const TabActionMap = {
      [INCIDENT_REPORT_TAB.EDIT]: this.onAddEditIncidentReport,
      [INCIDENT_REPORT_TAB.VIEW]: this.onViewIncidentReport,
      [INCIDENT_REPORT_TAB.DOWNLOAD]: this.onDownloadIncidentReport,
    };

    TabActionMap[tab]();
  };

  onAddNote = () => {
    this.setState({
      isNoteEditorOpen: true,
    });
  };

  onAddEditIncidentReport = () => {
    this.setState({ isIncidentReportEditorOpen: true });
  };

  onViewIncidentReport = () => {
    this.props.history.push(path(`/incident-reports/${this.props.data.incidentReportId}`));
  };

  onSaveIncidentReportSuccess = () => {
    this.actions.refresh();
  };

  onCloseNoteEditor = () => {
    this.setState({
      isNoteEditorOpen: false,
    });
  };

  onSaveNoteSuccess = () => {
    this.setState({
      isNoteEditorOpen: false,
      isSaveNoteSuccessDialogOpen: true,
    });

    this.props.onSaveNoteSuccessDialogOpen(true);
  };

  onCloseSaveNoteSuccessDialog = (shouldRedirectToNote) => {
    this.setState({ isSaveNoteSuccessDialogOpen: false });
    this.props.onSaveNoteSuccess(shouldRedirectToNote);
  };

  onCloseIncidentReportEditor = () => {
    this.setState({ isIncidentReportEditorOpen: false });
  };

  onDownloadIncidentReport = () => {
    const { withDownloadingStatusInfoToast } = this.props;

    withDownloadingStatusInfoToast(() =>
      this.actions.incidentReport.details.download(this.props.data.incidentReportId, { format: PDF }),
    );
  };

  viewAssessment = ({ clientId, assessmentId, assessmentTypeName }) => {
    this.props.history.push(path(`/clients/${clientId}/assessments`), { assessmentId, assessmentTypeName });
  };

  viewDocument = () => {
    const {
      data: {
        client,
        documentSignature: { isDeleted, documentId },
      },
    } = this.props;

    if (isDeleted) {
      this.setState({
        isDocumentDeletedDialogOpen: true,
      });
    } else {
      this.props.history.push(path(`/clients/${client.id}/documents?documentId=${documentId}`));
    }
  };

  onDownloadAssessment = (assessmentId, assessmentTypeName) => {
    this.props.assessmentUtils.downloadPdf(assessmentId, assessmentTypeName);
  };

  onViewMedication = () => {
    this.setState({ isMedicationViewerOpen: true });
  };

  onCloseMedicationViewer = () => {
    this.setState({ isMedicationViewerOpen: false });
  };

  onViewAppointment = () => {
    this.setState({ isAppointmentViewerOpen: true });
  };

  onCloseAppointmentViewer = () => {
    this.setState({ isAppointmentViewerOpen: false });
  };

  get actions() {
    return this.props.actions;
  }

  changeTab(tab) {
    this.setState({ tab });
  }

  render() {
    const {
      tab,
      isNoteEditorOpen,
      isMedicationViewerOpen,
      isAppointmentViewerOpen,
      isIncidentReportEditorOpen,
      isSaveNoteSuccessDialogOpen,
      isDocumentDeletedDialogOpen,
    } = this.state;

    const {
      eventId,
      clientId,
      organizationId,

      canViewIr,

      canAddNote,
      canViewNotes,

      data,
      error,
      isFetching,
      fetchCount,
      shouldReload,

      className,
      onLoadSuccess,
      onLoadFailure,
      isClientActive,

      isWorkflowEvent,
    } = this.props;

    const { width: windowHeight } = document.body.getBoundingClientRect();

    const options = chain(TAB)
      .reject((v, k) => k === "NOTES" && !canViewNotes)
      .map((value) => ({
        value,
        text: TAB_TITLE[value],
        isActive: tab === value,
        onClick: this.onChangeTab,
      }))
      .value();

    const incidentReportOptions = map(INCIDENT_REPORT_TAB, (value) => ({
      value,
      text: INCIDENT_REPORT_TAB_TITLE[value],
      onClick: this.onChangeIncidentReportTab,
    }));

    let content = (
      <div className={cn("EventDetails", className)}>
        <LoadCanAddEventNoteAction
          isMultiple
          params={{
            eventId,
            clientId,
            organizationId,
          }}
          shouldPerform={(prevParams) => eventId !== prevParams.eventId}
        />
        <LoadEventDetailsAction
          isMultiple
          params={{
            eventId,
            clientId,
            organizationId,
            shouldReload,
          }}
          shouldPerform={(prevParams) => {
            return (
              !isFetching &&
              (eventId !== prevParams.eventId ||
                (shouldReload ? !prevParams.shouldReload : fetchCount === 0 && isEmpty(data)))
            );
          }}
          onPerform={() => {
            this.changeTab(0);
            this.actions.clear();
          }}
          onPerformed={Response(({ data }) => {
            onLoadSuccess(data);
          }, onLoadFailure)}
        />
        <LoadCanViewIrAction params={{ clientId }} />
        <LoadAssessmentTypesAction
          isMultiple
          params={{
            clientId: clientId || data?.client.id,
            types: [GAD7, PHQ9, IN_HOME, CARE_MGMT, IN_HOME_CARE, COMPREHENSIVE],
          }}
          shouldPerform={(prevParams) =>
            Boolean(clientId || data?.client.id) && prevParams.clientId !== (clientId || data?.client.id)
          }
        />
        <div className="EventDetails-Header">
          <Tabs
            items={options.map((o, i) => ({
              ...o,
              title: windowHeight < 1200 ? TAB_TITLE_ADAPTED[i] : o.text,
            }))}
            onChange={this.onChangeTab}
            className="EventDetails-Tabs"
            containerClassName="EventDetails-TabsContainer"
          />

          <Dropdown
            value={tab}
            items={options}
            toggleText={TAB_TITLE[tab]}
            className="EventDetails-Dropdown Dropdown_theme_blue adaptive"
          />

          {/* {(tab === 0) && (
                        <div>
                            {canViewIr && (
                                <>
                                    <DownloadPdf
                                        id="download-ir-pdf-btn"
                                        className="EventDetails-DownloadBtn"
                                        onClick={this.onDownload}
                                    />
                                    <Tooltip
                                        placement="top"
                                        target="download-ir-pdf-btn">
                                        View incident report
                                    </Tooltip>
                                </>
                            )}
                        </div>
                    )} */}
          {canAddNote && isClientActive && (
            <>
              <AddItem id="add-note-btn" className="EventDetails-AddNoteBtn" onClick={this.onAddNote} />
              <Tooltip
                placement="top"
                target="add-note-btn"
                trigger="hover"
                modifiers={[
                  {
                    name: "offset",
                    options: { offset: [0, 6] },
                  },
                  {
                    name: "preventOverflow",
                    options: { boundary: document.body },
                  },
                ]}
              >
                Add a New Note
              </Tooltip>
            </>
          )}
        </div>
        {tab === 0 &&
          (isFetching ? (
            <Loader style={{ marginTop: 10, marginBottom: 10 }} />
          ) : !isNumber(eventId) || isEmpty(data) ? (
            <div className="EventDetails-NoDataText">No Details</div>
          ) : (
            <div>
              {data.client.hasAlert && (
                <div className="EventDetails-Alert">
                  <span className="EventDetails-AlertText">
                    You received this alert because you are assigned as the responsible party for event types of
                    <span className="font-weight-bold">{data.essentials.typeTitle}</span> occur for
                    <span className="font-weight-bold">{data.client.displayName}</span>
                  </span>
                </div>
              )}
              <div className="EventDetails-Navigation">
                {map(SECTIONS, (section) => {
                  return isNotEmptyOrBlank(data[section.name], true) ? (
                    <a
                      key={section.name}
                      className="EventDetails-NavLink"
                      href={`#event-details__${hyphenate(section.name)}`}
                    >
                      {section.title}
                    </a>
                  ) : null;
                })}
              </div>
              {isNotEmpty(data.client) && (
                <div className="EventDetails-Section EventClientInfo">
                  <div id="event-details__client" className="EventDetails-SectionAnchor" />

                  <div className="d-flex justify-content-between margin-bottom-24 padding-right-24">
                    <div className="EventDetails-SectionTitle">Client Info</div>

                    {isClientActive &&
                      data.canAddIncidentReport &&
                      data.canHaveIncidentReport &&
                      (isNumber(data.incidentReportId) ? (
                        <Dropdown
                          items={incidentReportOptions}
                          toggleText="Incident Report"
                          className="EventDetails-Dropdown"
                        />
                      ) : (
                        <Button outline color="success" onClick={this.onAddEditIncidentReport}>
                          Create Incident Report
                        </Button>
                      ))}

                    {data.canViewAssessment && Boolean(data.assessmentId) && (
                      <div>
                        <Button
                          outline
                          color="success"
                          className="margin-right-16"
                          onClick={() =>
                            this.viewAssessment({
                              clientId: data.client.id,
                              assessmentId: data.assessmentId,
                              assessmentTypeName: data.assessmentTypeName,
                            })
                          }
                        >
                          View Assessment
                        </Button>

                        <Button
                          outline
                          color="success"
                          onClick={() => this.onDownloadAssessment(data.assessmentId, data.assessmentTypeName)}
                        >
                          Download Pdf
                        </Button>
                      </div>
                    )}

                    {data.canViewWorkflow && Boolean(data.assessmentId) && (
                      <div>
                        <Button
                          outline
                          color="success"
                          className="margin-right-16"
                          onClick={() =>
                            this.viewAssessment({
                              clientId: data.client.id,
                              assessmentId: data.assessmentId,
                              assessmentTypeName: data.assessmentTypeName,
                            })
                          }
                        >
                          View Workflow
                        </Button>

                        <Button
                          outline
                          color="success"
                          onClick={() => this.onDownloadAssessment(data.assessmentId, data.assessmentTypeName)}
                        >
                          Download Pdf
                        </Button>
                      </div>
                    )}
                  </div>

                  <Detail title="Client name">
                    {data.canViewClient ? (
                      <Link className="EventDetails-Link" to={path(`/clients/${data.client.id}`)}>
                        {data.client.fullName}
                      </Link>
                    ) : (
                      data.client.fullName
                    )}
                  </Detail>
                  <Detail title="Client Aliases">{data.client.aliases && data.client.aliases.join(", ")}</Detail>
                  <Detail title="Client identifiers">
                    {data.client.identifiers && data.client.identifiers.join(", ")}
                  </Detail>
                  <Detail title="Social security number">
                    {data.client.ssn && `###-##-${data.client.ssn.substr(-4, 4)}`}
                  </Detail>
                  <Detail title="Date of birth">{data.client.birthDate}</Detail>
                  <Detail title="Gender">{data.client.gender}</Detail>
                  <Detail title="Marital Status">{data.client.maritalStatus}</Detail>
                  <Detail title="Primary language">{data.client.primaryLanguage}</Detail>
                  <Detail title="Client Account Number">{data.client.clientAccountNumber}</Detail>
                  <Detail title="Race">{data.client.race}</Detail>
                  <Detail title="Ethnic group">{data.client.ethnicGroup}</Detail>
                  <Detail title="Nationality">{data.client.nationality}</Detail>
                  <Detail title="Religion">{data.client.religion}</Detail>
                  <Detail title="Language">{data.client.languages && data.client.languages.join(", ")}</Detail>
                  <Detail title="Maiden name">{data.client.maidenName}</Detail>
                  <Detail title="Preferred Name">{data.client.preferredName}</Detail>
                  <Detail title="Prefix">{data.client.prefix}</Detail>
                  <Detail title="Citizenship">{data.client.citizenships && data.client.citizenships.join(", ")}</Detail>
                  <Detail title="Veterans Military Status">{data.client.veteranStatus}</Detail>
                  <Detail title="Phone number - Home">
                    {data.client.homePhone && <SubDetail title="Telephone number" value={data.client.homePhone} />}
                  </Detail>
                  <Detail title="Phone number - Business">
                    {data.client.businessPhone && (
                      <SubDetail title="Telephone number" value={data.client.businessPhone} />
                    )}
                  </Detail>
                  {data.client.address && <Detail title="Address">{getAddress(data.client.address, ",")}</Detail>}
                  <Detail title="Organization">{data.client.organizationTitle}</Detail>
                  <Detail title="Community">{data.client.communityTitle}</Detail>
                  <Detail title="Death Date and Time">{format(data.client.deathDate, DATE_TIME_FORMAT)}</Detail>
                </div>
              )}
              {isNotEmpty(data.essentials) && (
                <div className="EventDetails-Section EventEssentials">
                  <div id="event-details__essentials" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Event Essentials</div>
                  </div>
                  <Detail title="Person submitting event">{data.essentials.author}</Detail>
                  <Detail title="Care team role">{data.essentials.authorRole}</Detail>
                  <Detail title="Event date and time">{format(data.essentials.date, DATE_TIME_FORMAT)}</Detail>
                  <Detail title="Event type">
                    <span style={{ backgroundColor: "#fff1ca" }} className="EventDetails-Type">
                      {data.essentials.typeTitle}
                    </span>
                  </Detail>
                  <Detail title="Emergency department visit">
                    {data.essentials.isEmergencyDepartmentVisit ? "Yes" : "No"}
                  </Detail>
                  <Detail title="Overnight in-patient">{data.essentials.isOvernightInpatient ? "Yes" : "No"}</Detail>
                  <Detail title="Client device ID">{data.essentials.deviceId}</Detail>
                  <Detail title="Event type code">{data.essentials.typeCode}</Detail>
                  <Detail title="Recorded date/time">{format(data.essentials.recordedDate, DATE_TIME_FORMAT)}</Detail>
                  {data.essentials.canViewMedication && (
                    <div className="link EventDetail padding-left-15" onClick={this.onViewMedication}>
                      View medication
                    </div>
                  )}
                </div>
              )}
              {isNotEmptyOrBlank(data.description, false) && (
                <div className="EventDetails-Section EventDescription">
                  <div id="event-details__description" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Event Description</div>
                  </div>
                  <Detail title="Location">{data.description.location}</Detail>
                  {isNotEmptyOrBlank(data.documentSignature) ? (
                    <Detail title="Situation">
                      <SubDetail
                        title={`${data.documentSignature.statusName === SIGNED ? "Signed" : "Received"} document`}
                        value={data.documentSignature.templateName}
                      />
                      <SubDetail
                        value={format(data.documentSignature.signedDate, DATE_TIME_TIMEZONE_FORMAT)}
                        title={`Date ${data.documentSignature.statusName === SIGNED ? "signed" : "received"}`}
                      />
                      <span className="EventDetails-Link" onClick={this.viewDocument}>
                        View Document
                      </span>
                    </Detail>
                  ) : (
                    <Detail title="Situation">{data.description.situation}</Detail>
                  )}
                  <Detail title="Background">{data.description.background}</Detail>
                  <Detail>
                    {data.canViewAppointment && Boolean(data.appointmentId) && (
                      <span className="EventDetails-Link" onClick={this.onViewAppointment}>
                        View Appointment
                      </span>
                    )}
                  </Detail>
                  <Detail title="Assessment">{data.description.assessment}</Detail>
                  <Detail title="Injury">{data.description.hasInjury ? "Yes" : "No"}</Detail>
                  <Detail title="Follow Up Expected">{data.description.isFollowUpExpected ? "Yes" : "No"}</Detail>
                  <Detail title="Follow Up Details">{data.description.followUpDetails}</Detail>
                  {isNotEmptyOrBlank(data.description.pccEventAdtRecordDetails) && (
                    <>
                      <Detail title="Admission Source">
                        {data.description.pccEventAdtRecordDetails.admissionSource}
                      </Detail>
                      <Detail title="Admission Type">{data.description.pccEventAdtRecordDetails.admissionType}</Detail>
                      <Detail title="Bed">{data.description.pccEventAdtRecordDetails.bedDesc}</Detail>
                      <Detail title="Destination">{data.description.pccEventAdtRecordDetails.destination}</Detail>
                      <Detail title="Destination Type">
                        {data.description.pccEventAdtRecordDetails.destinationType}
                      </Detail>
                      <Detail title="Discharge Status">
                        {data.description.pccEventAdtRecordDetails.dischargeStatus}
                      </Detail>
                      <Detail title="Effective Date/Time">
                        {format(data.description.pccEventAdtRecordDetails.effectiveDateTime, DATE_TIME_FORMAT)}
                      </Detail>
                      <Detail title="Floor">{data.description.pccEventAdtRecordDetails.floorDesc}</Detail>
                      <Detail title="Origin">{data.description.pccEventAdtRecordDetails.origin}</Detail>
                      <Detail title="Origin Type">{data.description.pccEventAdtRecordDetails.originType}</Detail>
                      <YesNoWithoutBlank
                        title="Outpatient"
                        value={data.description.pccEventAdtRecordDetails.outpatient}
                      />
                      <Detail title="Outpatient Status">
                        {data.description.pccEventAdtRecordDetails.outpatientStatus}
                      </Detail>
                      <Detail title="Room">{data.description.pccEventAdtRecordDetails.roomDesc}</Detail>
                      <YesNoWithoutBlank
                        title="Skilled Care"
                        value={data.description.pccEventAdtRecordDetails.skilledCare}
                      />
                      <Detail title="Skilled Effective From">
                        {data.description.pccEventAdtRecordDetails.skilledEffectiveFromDate}
                      </Detail>
                      <Detail title="Skilled Effective To">
                        {data.description.pccEventAdtRecordDetails.skilledEffectiveToDate}
                      </Detail>
                      <Detail title="Action Type">
                        {data.description.pccEventAdtRecordDetails.standardActionType}
                      </Detail>
                      <Detail title="Unit">{data.description.pccEventAdtRecordDetails.unitDesc}</Detail>
                      <Detail title="Admission Date">
                        {format(data.description.pccEventAdtRecordDetails.admitDate, DATE_FORMAT)}
                      </Detail>
                      <Detail title="Discharge Date">
                        {format(data.description.pccEventAdtRecordDetails.dischargeDate, DATE_FORMAT)}
                      </Detail>
                      <Detail title="Death Date/Time">
                        {format(data.description.pccEventAdtRecordDetails.deathDateTime, DATE_TIME_FORMAT)}
                      </Detail>
                      <YesNoWithoutBlank title="Deceased" value={data.description.pccEventAdtRecordDetails.deceased} />
                    </>
                  )}
                </div>
              )}
              {isNotEmptyOrBlank(data.treatment, true) && (
                <div className="EventDetails-Section Treatment">
                  <div id="event-details__treatment" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Treatment Details</div>
                  </div>
                  {isNotEmptyOrBlank(data.treatment.physician, true) && (
                    <>
                      <div className="margin-bottom-24">
                        <div className="EventDetails-SectionTitle font-size-16">Details of treating physician</div>
                      </div>
                      <Detail title="Physician Name">{data.treatment.physician.fullName}</Detail>
                      {data.treatment.physician.address && (
                        <Detail title="Address">{getAddress(data.treatment.physician.address, ",")}</Detail>
                      )}
                      <Detail title="Phone">{data.treatment.physician.phone}</Detail>
                    </>
                  )}
                  {isNotEmptyOrBlank(data.treatment.hospital, true) && (
                    <>
                      <div className="margin-top-30 margin-bottom-24">
                        <div className="EventDetails-SectionTitle font-size-16">Details of treating hospital</div>
                      </div>
                      <Detail title="Hospital/Clinic">{data.treatment.hospital.name}</Detail>
                      {data.treatment.hospital.address && (
                        <Detail title="Address">{getAddress(data.treatment.hospital.address, ",")}</Detail>
                      )}
                      <Detail title="Phone">{data.treatment.hospital.phone}</Detail>
                    </>
                  )}
                </div>
              )}
              {isNotEmptyOrBlank(data.responsibleManager, true) && (
                <div className="EventDetails-Section ResponsibleManager">
                  <div id="event-details__responsible-manager" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Responsible Manager</div>
                  </div>
                  <Detail title="Name">{data.responsibleManager.displayName}</Detail>
                  <Detail title="Phone">{data.responsibleManager.phone}</Detail>
                  <Detail title="Email">{data.responsibleManager.email}</Detail>
                </div>
              )}
              {isNotEmptyOrBlank(data.registeredNurse, true) && (
                <div className="EventDetails-Section RegisteredNurse">
                  <div id="event-details__registered-nurse" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Registered Nurse (RN)</div>
                  </div>
                  <Detail title="Name">{data.registeredNurse.displayName}</Detail>
                  <Detail title="Phone">{data.registeredNurse.phone}</Detail>
                  {data.registeredNurse.hasAddress && (
                    <Detail title="Address">{getAddress(data.registeredNurse.address, ",")}</Detail>
                  )}
                </div>
              )}
              {isNotEmpty(data.procedures) && (
                <div className="EventDetails-Section EventProcedures">
                  <div id="event-details__procedures" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Procedures</div>
                  </div>
                  {map(data.procedures, (o, i) => (
                    <div className="EventDetails-Section Procedures">
                      <div className="d-flex justify-content-between margin-bottom-24">
                        <div className="EventDetails-SectionTitle font-size-16">PROCEDURE #{i + 1}</div>
                      </div>
                      <Detail title="Set id">{o.setId}</Detail>
                      <Detail title="Procedure coding method">{o.procedureCodingMethod}</Detail>
                      {isNotEmptyOrBlank(o.procedureCode, true) && (
                        <Detail title="Procedure code" valueClassName="d-flex flex-column">
                          <SubDetail title="identifier" value={o.procedureCode.identifier} />
                          <SubDetail title="text" value={o.procedureCode.text} />
                          <SubDetail title="Name Of Coding System" value={o.procedureCode.nameOfCodingSystem} />
                        </Detail>
                      )}
                      <Detail title="Procedure Description">{o.procedureDescription}</Detail>
                      <Detail title="Procedure date/time">{format(o.procedureDatetime, DATE_TIME_FORMAT)}</Detail>
                      <Detail title="Procedure functional type">{o.procedureFunctionalType}</Detail>
                      {isNotEmptyOrBlank(o.associatedDiagnosisCode, true) && (
                        <Detail title="Associated Diagnosis Code" valueClassName="d-flex flex-column">
                          <SubDetail title="identifier" value={o.associatedDiagnosisCode.identifier} />
                          <SubDetail title="text" value={o.associatedDiagnosisCode.text} />
                          <SubDetail
                            title="Name Of Coding System"
                            value={o.associatedDiagnosisCode.nameOfCodingSystem}
                          />
                        </Detail>
                      )}
                    </div>
                  ))}
                </div>
              )}
              {isNotEmptyOrBlank(data.patientVisit, true) && (
                <div className="EventDetails-Section PatientVisit">
                  <div id="event-details__patient-visit" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Patient Visit</div>
                  </div>
                  <Detail title="Client class">{data.patientVisit.patientClass}</Detail>
                  {isNotEmptyOrBlank(data.patientVisit.assignedPatientLocation, true) && (
                    <Detail title="Assigned Client Location" valueClassName="d-flex flex-column">
                      <SubDetail title="point of care" value={data.patientVisit.assignedPatientLocation.pointOfCare} />
                      <SubDetail title="room" value={data.patientVisit.assignedPatientLocation.room} />
                      <SubDetail title="bed" value={data.patientVisit.assignedPatientLocation.bed} />
                      <SubDetail
                        title="facility"
                        value={data.patientVisit.assignedPatientLocation.facility?.universalID}
                      />
                      <SubDetail
                        title="location status"
                        value={data.patientVisit.assignedPatientLocation.locationStatus}
                      />
                      <SubDetail
                        title="person location status"
                        value={data.patientVisit.assignedPatientLocation.personLocationStatus}
                      />
                      <SubDetail title="building" value={data.patientVisit.assignedPatientLocation.building} />
                      <SubDetail title="floor" value={data.patientVisit.assignedPatientLocation.floor} />
                      <SubDetail
                        title="location description"
                        value={data.patientVisit.assignedPatientLocation.locationDescription}
                      />
                    </Detail>
                  )}
                  <Detail title="Admission type">{data.patientVisit.admissionType}</Detail>
                  {isNotEmptyOrBlank(data.patientVisit.priorPatientLocation, true) && (
                    <Detail title="Prior Client Location" valueClassName="d-flex flex-column">
                      <SubDetail title="point of care" value={data.patientVisit.priorPatientLocation.pointOfCare} />
                      <SubDetail title="room" value={data.patientVisit.priorPatientLocation.room} />
                      <SubDetail title="bed" value={data.patientVisit.priorPatientLocation.bed} />
                      <SubDetail
                        title="facility"
                        value={data.patientVisit.priorPatientLocation.facility?.universalID}
                      />
                      <SubDetail
                        title="location status"
                        value={data.patientVisit.priorPatientLocation.locationStatus}
                      />
                      <SubDetail
                        title="person location status"
                        value={data.patientVisit.priorPatientLocation.personLocationStatus}
                      />
                      <SubDetail title="building" value={data.patientVisit.priorPatientLocation.building} />
                      <SubDetail title="floor" value={data.patientVisit.priorPatientLocation.floor} />
                      <SubDetail
                        title="location description"
                        value={data.patientVisit.priorPatientLocation.locationDescription}
                      />
                    </Detail>
                  )}
                  {map(data.patientVisit.attendingDoctors, (doctor, i) => (
                    <Detail
                      title={
                        data.patientVisit.attendingDoctors.length > 1
                          ? `Attending doctor #${i + 1}`
                          : "Attending doctor"
                      }
                      valueClassName="d-flex flex-column"
                    >
                      <SubDetail title="First Name" value={doctor.firstName} />
                      <SubDetail title="Last Name" value={doctor.lastName} />
                      <SubDetail title="Middle Name" value={doctor.middleName} />
                      <SubDetail title="Degree" value={doctor.degree} />
                      {isNotEmptyOrBlank(doctor.assigningAuthority, true) && (
                        <SubDetail
                          title="Assigning Authority"
                          value={`${doctor.assigningAuthority.namespaceID} ${doctor.assigningAuthority.universalID}`}
                        />
                      )}
                      {isNotEmptyOrBlank(doctor.assigningFacility, true) && (
                        <SubDetail title="Assigning Facility ID" value={doctor.assigningFacility.universalID} />
                      )}
                    </Detail>
                  ))}
                  {map(data.patientVisit.referringDoctors, (doctor, i) => (
                    <Detail
                      title={
                        data.patientVisit.referringDoctors.length > 1
                          ? `Referring doctor #${i + 1}`
                          : "Referring doctor"
                      }
                      valueClassName="d-flex flex-column"
                    >
                      <SubDetail title="First Name" value={doctor.firstName} />
                      <SubDetail title="Last Name" value={doctor.lastName} />
                      <SubDetail title="Middle Name" value={doctor.middleName} />
                      <SubDetail title="Degree" value={doctor.degree} />
                      {isNotEmptyOrBlank(doctor.assigningAuthority, true) && (
                        <SubDetail
                          title="Assigning Authority"
                          value={`${doctor.assigningAuthority.namespaceID} ${doctor.assigningAuthority.universalID}`}
                        />
                      )}
                      {isNotEmptyOrBlank(doctor.assigningFacility, true) && (
                        <SubDetail title="Assigning Facility ID" value={doctor.assigningFacility.universalID} />
                      )}
                    </Detail>
                  ))}
                  {map(data.patientVisit.consultingDoctors, (doctor, i) => (
                    <Detail
                      title={
                        data.patientVisit.consultingDoctors.length > 1
                          ? `Consulting doctor #${i + 1}`
                          : "Consulting doctor"
                      }
                      valueClassName="d-flex flex-column"
                    >
                      <SubDetail title="First Name" value={doctor.firstName} />
                      <SubDetail title="Last Name" value={doctor.lastName} />
                      <SubDetail title="Middle Name" value={doctor.middleName} />
                      <SubDetail title="Degree" value={doctor.degree} />
                      {isNotEmptyOrBlank(doctor.assigningAuthority, true) && (
                        <SubDetail
                          title="Assigning Authority"
                          value={`${doctor.assigningAuthority.namespaceID} ${doctor.assigningAuthority.universalID}`}
                        />
                      )}
                      {isNotEmptyOrBlank(doctor.assigningFacility, true) && (
                        <SubDetail title="Assigning Facility ID" value={doctor.assigningFacility.universalID} />
                      )}
                    </Detail>
                  ))}
                  <Detail title="Preadmit Test Indicator">{data.patientVisit.preadmitTestIndicator}</Detail>
                  <Detail title="Readmission Indicator">{data.patientVisit.readmissionIndicator}</Detail>
                  <Detail title="Admit source">{data.patientVisit.admitSource}</Detail>
                  <Detail title="Ambulatory Status">{data.patientVisit.ambulatoryStatuses?.join(",")}</Detail>
                  {map(data.patientVisit.admittingDoctors, (doctor, i) => (
                    <Detail
                      title={
                        data.patientVisit.admittingDoctors.length > 1
                          ? `Admitting doctor #${i + 1}`
                          : "Admitting doctor"
                      }
                      valueClassName="d-flex flex-column"
                    >
                      <SubDetail title="ID Number" value={doctor.idNumber} />
                      <SubDetail title="First Name" value={doctor.firstName} />
                      <SubDetail title="Last Name" value={doctor.lastName} />
                      <SubDetail title="Middle Name" value={doctor.middleName} />
                      <SubDetail title="Degree" value={doctor.degree} />
                      {isNotEmptyOrBlank(doctor.assigningAuthority, true) && (
                        <SubDetail
                          title="Assigning Authority"
                          value={`${doctor.assigningAuthority.namespaceID} ${doctor.assigningAuthority.universalID}`}
                        />
                      )}
                      {isNotEmptyOrBlank(doctor.assigningFacility, true) && (
                        <SubDetail title="Assigning Facility ID" value={doctor.assigningFacility.universalID} />
                      )}
                    </Detail>
                  ))}
                  <Detail title="Discharge Disposition">{data.patientVisit.dischargeDisposition}</Detail>
                  {isNotEmptyOrBlank(data.patientVisit.dischargedToLocation, true) && (
                    <Detail title="Discharged To Location" valueClassName="d-flex flex-column">
                      <SubDetail
                        title="discharge location"
                        value={data.patientVisit.dischargedToLocation.dischargeLocation}
                      />
                      <SubDetail
                        title="effective date"
                        value={format(data.patientVisit.dischargedToLocation.effectiveDate, DATE_TIME_FORMAT)}
                      />
                    </Detail>
                  )}
                  <Detail title="Servicing Facility">{data.patientVisit.servicingFacility}</Detail>
                  <Detail title="Admit date/time">{format(data.patientVisit.admitDate, DATE_TIME_FORMAT)}</Detail>
                  <Detail title="Discharge date/time">
                    {format(data.patientVisit.dischargeDate, DATE_TIME_FORMAT)}
                  </Detail>
                  {map(data.patientVisit.otherHealthcareProviders, (provider, i) => (
                    <Detail
                      title={
                        data.patientVisit.otherHealthcareProviders.length > 1
                          ? `Other Healthcare Provider #${i + 1}`
                          : "Other Healthcare Provider"
                      }
                      valueClassName="d-flex flex-column"
                    >
                      <SubDetail title="First Name" value={provider.firstName} />
                      <SubDetail title="Last Name" value={provider.lastName} />
                      <SubDetail title="Middle Name" value={provider.middleName} />
                      <SubDetail title="Degree" value={provider.degree} />
                      {isNotEmptyOrBlank(provider.assigningAuthority, true) && (
                        <SubDetail
                          title="Assigning Authority"
                          value={`${provider.assigningAuthority.namespaceID} ${provider.assigningAuthority.universalID}`}
                        />
                      )}
                      {isNotEmptyOrBlank(provider.assigningFacility, true) && (
                        <SubDetail title="Assigning Facility ID" value={provider.assigningFacility.universalID} />
                      )}
                    </Detail>
                  ))}
                </div>
              )}
              {isNotEmpty(data.diagnoses) && (
                <div className="EventDetails-Section EventDiagnoses">
                  <div id="event-details__diagnoses" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Diagnoses</div>
                  </div>
                  {map(data.diagnoses, (o, i) => (
                    <div className="EventDetails-Section Diagnosis">
                      <div className="d-flex justify-content-between margin-bottom-24">
                        <div className="EventDetails-SectionTitle font-size-16">DIAGNOSIS #{i + 1}</div>
                      </div>
                      <Detail title="Set id">{o.setId}</Detail>
                      <Detail title="Diagnosis coding method">{o.diagnosisCodingMethod}</Detail>
                      {isNotEmptyOrBlank(o.diagnosisCode, true) && (
                        <Detail title="Diagnosis code" valueClassName="d-flex flex-column">
                          <SubDetail title="identifier" value={o.diagnosisCode.identifier} />
                          <SubDetail title="text" value={o.diagnosisCode.text} />
                          <SubDetail title="Name Of Coding System" value={o.diagnosisCode.nameOfCodingSystem} />
                        </Detail>
                      )}
                      <Detail title="Diagnosis Description">{o.diagnosisDescription}</Detail>
                      <Detail title="Diagnosis date/time">{format(o.diagnosisDateTime, DATE_TIME_FORMAT)}</Detail>
                      <Detail title="Diagnosis type">{o.diagnosisType}</Detail>
                      {map(o.diagnosingClinicians, (clinician, i) => (
                        <Detail
                          title={
                            o.diagnosingClinicians.length > 1
                              ? `Diagnosing Clinician #${i + 1}`
                              : "Diagnosing Clinician"
                          }
                          valueClassName="d-flex flex-column"
                        >
                          <SubDetail title="First Name" value={clinician.firstName} />
                          <SubDetail title="Last Name" value={clinician.lastName} />
                          <SubDetail title="Middle Name" value={clinician.middleName} />
                          <SubDetail title="Degree" value={clinician.degree} />
                          {isNotEmptyOrBlank(clinician.assigningAuthority, true) && (
                            <SubDetail
                              title="Assigning Authority"
                              value={`${clinician.assigningAuthority.namespaceID} ${clinician.assigningAuthority.universalID}`}
                            />
                          )}
                          {isNotEmptyOrBlank(clinician.assigningFacility, true) && (
                            <SubDetail title="Assigning Facility ID" value={clinician.assigningFacility.universalID} />
                          )}
                        </Detail>
                      ))}
                    </div>
                  ))}
                </div>
              )}
              {isNotEmpty(data.guarantors) && (
                <div className="EventDetails-Section EventGuarantors">
                  <div id="event-details__guarantors" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Guarantors</div>
                  </div>
                  {map(data.guarantors, (o, i) => (
                    <div className="EventDetails-Section Guarantors">
                      <div className="d-flex justify-content-between margin-bottom-24">
                        <div className="EventDetails-SectionTitle font-size-16">GUARANTOR #{i + 1}</div>
                      </div>
                      <Detail title="Set id">{o.setId}</Detail>
                      {isNotEmptyOrBlank(o.guarantorNumbers, true) && (
                        <Detail title="Guarantor Number">
                          {map(o.guarantorNumbers, (n, i) => (
                            <SubDetail title={`# ${i + 1}`} value={n.pId} />
                          ))}
                        </Detail>
                      )}
                      {map(o.guarantorNames, (name, i) => (
                        <Detail
                          title={o.guarantorNames.length > 1 ? `Guarantor name #${i + 1}` : `Guarantor name`}
                          valueClassName="d-flex flex-column"
                        >
                          <SubDetail title="first name" value={name.firstName} />
                          <SubDetail title="last name" value={name.lastName} />
                          <SubDetail title="middle name" value={name.middleName} />
                        </Detail>
                      ))}
                      {map(o.guarantorAddresses, (address, i) => (
                        <Detail
                          title={o.guarantorAddresses.length > 1 ? `Guarantor Address #${i + 1}` : "Guarantor Address"}
                          valueClassName="d-flex flex-column"
                        >
                          <SubDetail title="street address" value={address.street} />
                          <SubDetail title="city" value={address.city} />
                          <SubDetail title="state or province" value={address.stateName} />
                          <SubDetail title="zip or postal code" value={address.zip} />
                        </Detail>
                      ))}
                      {map(o.guarantorHomePhones, (phone, i) => (
                        <Detail
                          title={
                            o.guarantorHomePhones.length > 1
                              ? `Guarantor Phone Number - Home #${i + 1}`
                              : "Guarantor Phone Number - Home"
                          }
                          valueClassName="d-flex flex-column"
                        >
                          <SubDetail title="Telephone Number" value={phone.telephoneNumber} />
                          <SubDetail title="Country Code" value={phone.countryCode} />
                          <SubDetail title="Area/city Code" value={phone.areaCode} />
                          <SubDetail title="Phone Number" value={phone.phoneNumber} />
                          <SubDetail title="Extension" value={phone.extension} />
                          <SubDetail title="Email Address" value={phone.email} />
                        </Detail>
                      ))}
                      {isNotEmpty(o.guarantorDatetimeOfBirth) && (
                        <Detail title="Guarantor Date of Birth">
                          {format(o.guarantorDatetimeOfBirth, DATE_FORMAT)}
                        </Detail>
                      )}
                      <Detail title="Guarantor Type">{o.guarantorType}</Detail>
                      {isNotEmptyOrBlank(o.guarantorRelationship, true) && (
                        <Detail title="Guarantor Relationship" valueClassName="d-flex flex-column">
                          <SubDetail title="identifier" value={o.guarantorRelationship.identifier} />
                          <SubDetail title="text" value={o.guarantorRelationship.text} />
                          <SubDetail title="Name Of Coding System" value={o.guarantorRelationship.nameOfCodingSystem} />
                        </Detail>
                      )}
                      {isNotEmptyOrBlank(o.primaryLanguage, true) && (
                        <Detail title="Guarantor primary language" valueClassName="d-flex flex-column">
                          <SubDetail title="identifier" value={o.primaryLanguage.identifier} />
                          <SubDetail title="text" value={o.primaryLanguage.text} />
                          <SubDetail title="Name Of Coding System" value={o.primaryLanguage.nameOfCodingSystem} />
                        </Detail>
                      )}
                    </div>
                  ))}
                </div>
              )}
              {isNotEmpty(data.insurances) && (
                <div className="EventDetails-Section EventInsurances">
                  <div id="event-details__insurances" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Insurances</div>
                  </div>
                  {map(data.insurances, (o, i) => (
                    <div className="EventDetails-Section Insurances">
                      <div className="d-flex justify-content-between margin-bottom-24">
                        <div className="EventDetails-SectionTitle font-size-16">INSURANCE #{i + 1}</div>
                      </div>
                      <Detail title="Set id">{o.setId}</Detail>
                      {isNotEmptyOrBlank(o.insurancePlanId, true) && (
                        <Detail title="Insurance Plan ID" valueClassName="d-flex flex-column">
                          <SubDetail title="identifier" value={o.insurancePlanId.identifier} />
                          <SubDetail title="text" value={o.insurancePlanId.text} />
                          <SubDetail title="Name Of Coding System" value={o.insurancePlanId.nameOfCodingSystem} />
                        </Detail>
                      )}
                      {map(o.insuranceCompanyIds, (id, i) => (
                        <Detail
                          title={
                            o.insuranceCompanyIds.length > 1 ? `Insurance Company ID #${i + 1}` : "Insurance Company ID"
                          }
                          valueClassName="d-flex flex-column"
                        >
                          <SubDetail title="ID" value={id.pId} />
                          <SubDetail title="Identifier Type Code" value={id.identifierTypeCode} />
                        </Detail>
                      ))}
                      {map(o.insuranceCompanyNames, (name, i) => (
                        <Detail
                          title={
                            o.insuranceCompanyNames.length > 1
                              ? `Insurance Company Name #${i + 1}`
                              : "Insurance Company Name"
                          }
                          valueClassName="d-flex flex-column"
                        >
                          <SubDetail title="Organization Name" value={name?.organizationName} />
                          <SubDetail title="Organization Name Type Code" value={name?.organizationNameTypeCode} />
                          <SubDetail title="ID Number" value={name?.idNumber} />
                          {isNotEmptyOrBlank(name.assigningAuthority, true) && (
                            <SubDetail
                              title="Assigning Authority"
                              value={`${name.assigningAuthority.namespaceID} ${name.assigningAuthority.universalID}`}
                            />
                          )}
                          {isNotEmptyOrBlank(name.assigningFacilityId, true) && (
                            <SubDetail title="Assigning Facility ID" value={name.assigningFacilityId.universalID} />
                          )}
                        </Detail>
                      ))}
                      {map(o.insuranceCompanyAddresses, (address, i) => (
                        <Detail
                          title={
                            o.insuranceCompanyAddresses.length > 1
                              ? `Insurance Company Address #${i + 1}`
                              : "Insurance Company Address"
                          }
                          valueClassName="d-flex flex-column"
                        >
                          <SubDetail title="street address" value={address.street} />
                          <SubDetail title="city" value={address.city} />
                          <SubDetail title="state or province" value={address.stateName} />
                          <SubDetail title="zip or postal code" value={address.zip} />
                        </Detail>
                      ))}
                      {map(o.insuranceCoPhoneNumbers, (number, i) => (
                        <Detail
                          title={
                            o.insuranceCoPhoneNumbers.length > 1
                              ? `Insurance Co Phone Number #${i + 1}`
                              : "Insurance Co Phone Number"
                          }
                          valueClassName="d-flex flex-column"
                        >
                          <SubDetail title="Telephone Number" value={number.telephoneNumber} />
                          <SubDetail title="Country Code" value={number.countryCode} />
                          <SubDetail title="Area/city Code" value={number.areaCode} />
                          <SubDetail title="Phone Number" value={number.phoneNumber} />
                          <SubDetail title="Extension" value={number.extension} />
                          <SubDetail title="Email Address" value={number.email} />
                        </Detail>
                      ))}
                      <Detail title="Group Number">{o.groupNumber}</Detail>
                      {map(o.groupNames, (group, i) => (
                        <Detail
                          title={`Group Name ${o.groupNames.length > 1 ? `#${i + 1}` : ""}`}
                          valueClassName="d-flex flex-column"
                        >
                          <SubDetail title="Organization Name" value={group.organizationName} />
                          <SubDetail title="Organization Name Type Code" value={group.organizationNameTypeCode} />
                          <SubDetail title="ID Number" value={group.idNumber} />
                          <SubDetail title="Assigning Authority ID" value={group.assigningAuthority?.universalID} />
                          <SubDetail title="Assigning Facility ID" value={group.assigningFacility?.universalID} />
                        </Detail>
                      ))}
                      <Detail title="Plan Effective Date">{format(o.planEffectiveDate, DATE_TIME_FORMAT)}</Detail>
                      <Detail title="Plan Expiration Date">{format(o.planExpirationDate, DATE_TIME_FORMAT)}</Detail>
                      <Detail title="Plan Type">{o.planType}</Detail>
                      {map(o.namesOfInsured, (name, i) => (
                        <Detail
                          title={`Name of insured ${o.namesOfInsured.length > 1 ? `#${i + 1}` : ""}`}
                          valueClassName="d-flex flex-column"
                        >
                          <SubDetail title="first name" value={name.firstName} />
                          <SubDetail title="last name" value={name.lastName} />
                          <SubDetail title="middle name" value={name.middleName} />
                        </Detail>
                      ))}
                      {isNotEmptyOrBlank(o.insuredsRelationshipToPatient, true) && (
                        <Detail title="Insured's Relationship to Client" valueClassName="d-flex flex-column">
                          <SubDetail title="identifier" value={o.insuredsRelationshipToPatient.identifier} />
                          <SubDetail title="text" value={o.insuredsRelationshipToPatient.text} />
                          <SubDetail
                            title="Name Of Coding System"
                            value={o.insuredsRelationshipToPatient.nameOfCodingSystem}
                          />
                        </Detail>
                      )}
                      <Detail title="Pre Admit Cert">{o.preAdmitCert}</Detail>
                      <Detail title="Type Of Agreement Code">{o.typeOfAgreementCode}</Detail>
                      <Detail title="Policy Number">{o.policyNumber}</Detail>
                    </div>
                  ))}
                </div>
              )}
              {isNotEmpty(data.allergies) && (
                <div className="EventDetails-Section EventAllergies">
                  <div id="event-details__allergies" className="EventDetails-SectionAnchor" />
                  <div className="d-flex justify-content-between margin-bottom-24">
                    <div className="EventDetails-SectionTitle">Allergies</div>
                  </div>
                  {map(data.allergies, (o, i) => (
                    <div className="EventDetails-Section Allergy">
                      <div className="d-flex justify-content-between margin-bottom-24">
                        <div className="EventDetails-SectionTitle font-size-16">ALLERGY #{i + 1}</div>
                      </div>
                      <Detail title="Set id">{o.setId}</Detail>
                      <Detail title="Type">{o.allergyType}</Detail>
                      {isNotEmptyOrBlank(o.allergyCode, true) && (
                        <Detail title="Code / Mnemonic / Description" valueClassName="d-flex flex-column">
                          <SubDetail title="identifier" value={o.allergyCode.identifier} />
                          <SubDetail title="text" value={o.allergyCode.text} />
                          <SubDetail title="name of coding system" value={o.allergyCode.nameOfCodingSystem} />
                        </Detail>
                      )}
                      <Detail title="Severity">{o.allergySeverity}</Detail>
                      <Detail title="Reaction">{o?.allergyReactions.join(",")}</Detail>
                      <Detail title="Identification Date">{format(o.identificationDate, DATE_TIME_FORMAT)}</Detail>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        {tab === 1 && <EventNotifications eventId={eventId} clientId={clientId} organizationId={organizationId} />}
        {tab === 2 && <EventNotes eventId={eventId} clientId={clientId} organizationId={organizationId} />}
        <NoteEditor
          isOpen={isNoteEditorOpen}
          eventId={eventId}
          clientId={clientId || data?.client?.id}
          clientName={data?.client?.fullName}
          organizationId={organizationId}
          onClose={this.onCloseNoteEditor}
          onSaveSuccess={this.onSaveNoteSuccess}
        />

        <IncidentReportEditor
          reportId={data?.incidentReportId}
          eventId={eventId}
          clientId={clientId}
          organizationId={organizationId}
          isOpen={isIncidentReportEditorOpen}
          onSaveSuccess={this.onSaveIncidentReportSuccess}
          onEditDraft={this.onAddEditIncidentReport}
          onClose={this.onCloseIncidentReportEditor}
        />

        {isMedicationViewerOpen && (
          <MedicationViewer
            isOpen
            clientId={data?.client.id}
            medicationId={data?.essentials.medicationId}
            onClose={this.onCloseMedicationViewer}
          />
        )}

        {isAppointmentViewerOpen && (
          <AppointmentViewer
            isOpen={isAppointmentViewerOpen}
            appointmentId={data?.appointmentId}
            onClose={this.onCloseAppointmentViewer}
            readOnly
          />
        )}

        {isSaveNoteSuccessDialogOpen && (
          <SuccessDialog
            isOpen
            title="The note has been created."
            buttons={[
              {
                text: "Close",
                outline: true,
                onClick: () => this.onCloseSaveNoteSuccessDialog(false),
              },
              {
                text: "View Note",
                onClick: () => this.onCloseSaveNoteSuccessDialog(true),
              },
            ]}
          />
        )}

        {isDocumentDeletedDialogOpen && (
          <WarningDialog
            isOpen
            title="This document has been deleted and it is not accessible."
            buttons={[
              {
                text: "Close",
                outline: true,
                onClick: () => this.setState({ isDocumentDeletedDialogOpen: false }),
              },
            ]}
          />
        )}
        <ScrollTop scrollable=".App-Content, .SideBar-Content" scrollTopBtnClass="EventDetails-ScrollTopBtn" />
        {error && !isIgnoredError(error) && <ErrorViewer isOpen error={error} onClose={this.onResetError} />}
      </div>
    );

    return windowHeight < TABLET_PORTRAIT || isWorkflowEvent ? (
      <Modal
        isOpen={true}
        onClose={this.props.onClose}
        hasCloseBtn={false}
        title={TAB_TITLE[tab]}
        className="EventDetailsMobile"
        renderFooter={() => (
          <Button color="success" onClick={this.props.onClose}>
            Close
          </Button>
        )}
      >
        {content}
      </Modal>
    ) : (
      content
    );
  }
}

export default compose(
  withRouter,
  withAssessmentUtils,
  withDownloadingStatusInfoToast,
  connect(mapStateToProps, mapDispatchToProps),
)(EventDetails);
