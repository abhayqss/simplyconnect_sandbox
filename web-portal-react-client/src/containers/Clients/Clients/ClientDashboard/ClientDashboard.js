import React, { useCallback, useEffect, useRef, useState } from "react";

import { compact } from "underscore";

import $ from "jquery";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { useHistory, useParams } from "react-router-dom";

import DocumentTitle from "react-document-title";

import { Button } from "components/buttons";

import { useQueryInvalidation, useQueryParams } from "hooks/common";

import { useAuthUser } from "hooks/common/redux";

import { useConversations } from "hooks/business/conversations";

import {
  useCanAddAssessmentQuery,
  useCanAddEventQuery,
  useCanAddLabResearchOrderQuery,
  useCanAddNoteQuery,
  useCanAddServicePlanQuery,
  useCanEditClientQuery,
} from "hooks/business/client";

import {
  useClientAccessApprovingQuery,
  useClientAccessDecliningQuery,
  useClientAccessRequestQuery,
  useClientAccessRequestsQuery,
} from "hooks/business/client/records";

import { useDashboardPermissionsQuery } from "hooks/business/client/dashboard";

import { useCanViewClientAssessmentsQuery } from "hooks/business/client/assessments";

import { useCanViewClientServicePlansQuery } from "hooks/business/client/service-plans";

import { useCanViewIncidentReports } from "hooks/business/incident-report";

import { Breadcrumbs, Dropdown, ErrorViewer, Loader } from "components";

import { ConfirmDialog, LoadingDialog, SuccessDialog, WarningDialog } from "components/dialogs";

import NoteEditor from "containers/Events/NoteEditor/NoteEditor";
import EventEditor from "containers/Events/EventEditor/EventEditor";
import AppointmentEditor from "containers/Appointments/AppointmentEditor/AppointmentEditor";
import LabOrderEditor from "containers/Labs/LabOrderEditor/LabOrderEditor";
import ReferralRequestEditor from "containers/Referrals/ReferralRequestEditor/ReferralRequestEditor";
import ClientActivationEditor from "./ClientActivationEditor/ClientActivationEditor";
import ClientDeactivationEditor from "./ClientDeactivationEditor/ClientDeactivationEditor";

import UpdateSideBarAction from "actions/clients/UpdateSideBarAction";

import * as clientStatusActions from "redux/client/status/clientStatusActions";
import * as clientDetailsActions from "redux/client/details/clientDetailsActions";

import transpRideRequestActions from "redux/transportation/ride/request/transpRideRequestActions";
import transpRideHistoryActions from "redux/transportation/ride/history/transpRideHistoryActions";

import { CLIENT_STATUSES, SYSTEM_ROLES, TRANSPORTATION_ACTION, TRANSPORTATION_ACTION_DESC } from "lib/Constants";

import { allAreInteger, allAreNotEmpty, isEmpty, isInteger, isNotEmpty } from "lib/utils/Utils";

import { map, moveItem, reject } from "lib/utils/ArrayUtils";

import { path } from "lib/utils/ContextUtils";

import { Response } from "lib/utils/AjaxUtils";

import ClientEditor from "../ClientEditor/ClientEditor";

import {
  ClientAllergiesSummary,
  ClientAssessmentsSummary,
  ClientDetails,
  ClientDocumentsDevicesSummary,
  ClientMedicationsSummary,
  ClientProblemsSummary,
  ClientRecentEventsSummary,
  ClientRecentNotesSummary,
  ClientServicePlanSummary,
} from "./";

import "./ClientDashboard.scss";
import ClientMedicationEditor from "./ClientMedicationEditor/ClientMedicationEditor";
import { ReactComponent as Warning } from "images/warning.svg";
import services from "../../../../services/CareTeamMemberService";

const { ADMINISTRATOR, COMMUNITY_ADMINISTRATOR } = SYSTEM_ROLES;

const EDIT_DETAILS = "EDIT_DETAILS";
const CREATE_EVENT = "CREATE_EVENT";
const MARK_AS_INACTIVE = "MARK_AS_INACTIVE";
const CREATE_ASSESSMENT = "CREATE_ASSESSMENT";
const TRANSPORTATION_REQUEST_A_NEW_RIDE = "TRANSPORTATION_REQUEST_A_NEW_RIDE";
const TRANSPORTATION_RIDE_HISTORY = "TRANSPORTATION_RIDE_HISTORY";
const REQUEST_A_NEW_RIDE = "REQUEST_A_NEW_RIDE";
const CREATE_SERVICE_PLAN = "CREATE_SERVICE_PLAN";
const CARE_TEAM = "CARE_TEAM";
const ACTIVATE_CLIENT = "ACTIVATE_CLIENT";
const DEACTIVATE_CLIENT = "DEACTIVATE_CLIENT";
const CREATE_REFERRAL = "CREATE_REFERRAL";
const PLACE_ORDER = "PLACE_ORDER";
const VIEW_INCIDENT_REPORTS = "VIEW_INCIDENT_REPORTS";
const CHAT = "CHAT";
const VIDEO_CHAT = "VIDEO_CHAT";
const CREATE_APPOINTMENT = "CREATE_APPOINTMENT";
const ADD_MEDICATION = "ADD_MEDICATION";

const OPTIONS = [
  { name: CREATE_EVENT, title: "Create Event", tooltip: "You don't have permissions to Client's Events&Notes" },
  { name: CREATE_ASSESSMENT, title: "Create Assessment" },
  { name: CREATE_SERVICE_PLAN, title: "Create Service Plan" },
  { name: TRANSPORTATION_REQUEST_A_NEW_RIDE, title: "Request a New Ride", value: 3 },
  { name: TRANSPORTATION_RIDE_HISTORY, title: "Ride History", value: 4 },
  { name: VIEW_INCIDENT_REPORTS, title: "View Incident Reports", hasSeparator: true },
  { name: CHAT, title: "Chat" },
  { name: VIDEO_CHAT, title: "Video", hasSeparator: true },
  /* {name: 'MARK_AS_INACTIVE', text: 'Mark as Inactive', value: 6},*/
  { name: EDIT_DETAILS, title: "Edit Client Record" },
  { name: ACTIVATE_CLIENT, title: "Activate" },
  { name: DEACTIVATE_CLIENT, title: "Deactivate" },
  // { name: CREATE_REFERRAL, title: 'Create referral' },
  { name: PLACE_ORDER, title: "Place Order" },
  { name: CREATE_APPOINTMENT, title: "Create Appointment" },
  { name: ADD_MEDICATION, title: "Add Medication" },
];

const { PENDING } = CLIENT_STATUSES;

export function isDataValid(data = {}) {
  const { lastName, firstName, address, genderId, birthDate } = data;

  const { zip, city, street, stateId } = address || {};

  const phone = data?.cellPhone || data?.phone || data?.communityPhone || data?.organizationPhone;

  return allAreNotEmpty(lastName, firstName, phone, genderId, birthDate, zip, city, street, stateId);
}

function mapStateToProps(state) {
  const { can, details, assessment, servicePlan } = state.client;

  return {
    details,
    canEdit: can.edit.value,

    canViewAssessments: assessment.can.view.value,
    canViewServicePlans: servicePlan.can.view.value,
    canViewIncidentReports: state.incident.report.can.view.value,

    canAddNote: state.note.can.add.value,
    canAddEvent: state.event.can.add.value,
    canAddAssessment: assessment.can.add.value,
    canAddServicePlan: servicePlan.can.add.value,
    canAddResearchOrder: state.lab.research.order.can.add.value,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      details: bindActionCreators(clientDetailsActions, dispatch),
      status: bindActionCreators(clientStatusActions, dispatch),
      transportation: {
        ride: {
          request: bindActionCreators(transpRideRequestActions, dispatch),
          history: bindActionCreators(transpRideHistoryActions, dispatch),
        },
      },
    },
  };
}

function ClientDashboard({
  details,
  actions,

  canEdit,

  canViewIncidentReports,

  canAddNote,
  canAddEvent,
  canAddAssessment,
  canAddServicePlan,
  canAddResearchOrder,
}) {
  const [error, setError] = useState(null);
  const [saved, setSaved] = useState(null);

  const [accessError, setAccessError] = useState(null);

  const [isEditorOpen, toggleEditor] = useState(false);

  const [isNoteEditorOpen, toggleNoteEditor] = useState(false);
  const [isNoteSaveSuccessDialogOpen, toggleNoteSaveSuccessDialog] = useState(false);

  const [isInvalidDataWarningDialogOpen, toggleInvalidDataWarningDialog] = useState(false);

  const [transportationAction, setTransportationAction] = useState(null);

  const [isEventEditorOpen, toggleEventEditor] = useState(false);
  const [isEventSaveSuccessDialogOpen, toggleEventSaveSuccessDialog] = useState(false);

  const [isReferralRequestEditorOpen, toggleReferralRequestEditor] = useState(false);
  const [isLabResearchOrderEditorOpen, toggleLabResearchOrderEditor] = useState(false);

  const [isActivationEditorOpen, toggleActivationEditor] = useState(false);
  const [isDeactivationEditorOpen, toggleDeactivationEditor] = useState(false);

  const [isAppointmentEditorOpen, toggleAppointmentEditor] = useState(false);
  const [isMedicationEditor, toggleMedicationEditor] = useState(false);
  const [isRefresh, setIsRefresh] = useState(false);
  const [isAppointmentSaveSuccessDialogOpen, toggleAppointmentSaveSuccessDialog] = useState(false);
  const [isMedicationSaveSuccessDialogOpen, setIsMedicationSaveSuccessDialogOpen] = useState(false);
  const [isHL7WarningDialogOpen, toggleHL7WarningDialog] = useState(false);

  const [accessRequestDialogs, setAccessRequestDialogs] = useState([]);

  const [isAccessApprovingSuccessDialogOpen, toggleAccessApprovingSuccessDialog] = useState(false);
  const [isAccessDecliningSuccessDialogOpen, toggleAccessDecliningSuccessDialog] = useState(false);
  const [isAccessRequestStatusChangedDialogOpen, toggleAccessRequestStatusChangedDialog] = useState(false);

  const [isCancelEditConfirmDialogOpen, setIsCancelEditConfirmDialogOpen] = useState(false);
  const [canShowReminder, setCanShowReminder] = useState(false);
  const [waringDialog, setWaringDialog] = useState(false);

  const transportationFormRef = useRef();

  const history = useHistory();

  const params = useParams();

  const clientId = parseInt(params.clientId);

  const { accessRequestId } = useQueryParams();

  const user = useAuthUser();

  const isOrganizationAdmin = user?.roleName === ADMINISTRATOR;
  const isCommunityAdmin = user?.roleName === COMMUNITY_ADMINISTRATOR;

  const invalidate = useQueryInvalidation();

  const { data, isFetching } = details;

  const isActive = data?.isActive;

  const isDataEmpty = isEmpty(data);

  const contact = data?.associatedContact;

  const { emit } = useConversations();

  const fetchData = (id) => {
    if (id) {
      services.showRemindFill(id).then((res) => {
        setCanShowReminder(res);
      });
    }
  };

  const changeWaringDialog = () => {
    setWaringDialog(!waringDialog);
  };

  const setRemindShow = (clientId) => {
    services.neverShowRemindFill(clientId).then((res) => {
      setCanShowReminder(res);
    });
  };

  useClientAccessRequestsQuery(
    { clientId, status: PENDING },
    {
      enabled:
        isNotEmpty(data) &&
        ((isCommunityAdmin && user.communityId === data.communityId) ||
          (isOrganizationAdmin && user.organizationId === data.organizationId)),
      onSuccess: ({ data: requests }) => {
        if (isNotEmpty(requests)) {
          if (isInteger(accessRequestId)) {
            requests = moveItem(requests, (o) => o.id === accessRequestId, requests.length - 1);
          }

          setAccessRequestDialogs(
            map(requests, (o) => ({
              id: o.id,
              isOpen: true,
              title: `${o.employeeFullName} has added ${data?.fullName} to his/her Clients list. Your approval will allow ${o.employeeFullName} to access Client's details. Please approve or decline the request.`,
              buttons: [
                {
                  text: "Decline",
                  outline: true,
                  onClick: () => onDeclineAccess(o),
                },
                {
                  text: "Approve",
                  onClick: () => onApproveAccess(o),
                },
              ],
            })),
          );
        }
      },
      onError: setAccessError,
    },
  );

  const { data: accessRequest, isFetching: isFetchingAccessRequest } = useClientAccessRequestQuery(
    { clientId, requestId: accessRequestId },
    {
      enabled: allAreInteger(clientId, accessRequestId),
      onError: setError,
    },
  );

  const { mutateAsync: approveAccess, isLoading: isApprovingAccess } = useClientAccessApprovingQuery(
    { clientId },
    { onError: setError },
  );

  const { mutateAsync: declineAccess, isLoading: isDecliningAccess } = useClientAccessDecliningQuery(
    { clientId },
    { onError: setError },
  );

  const { data: dashboardPermissions } = useDashboardPermissionsQuery({ clientId });

  const { data: canViewAssessments } = useCanViewClientAssessmentsQuery({ clientId }, { enabled: isInteger(clientId) });

  const { data: canViewServicePlans } = useCanViewClientServicePlansQuery(
    { clientId },
    { enabled: isInteger(clientId) },
  );

  const fetchDetails = useCallback(() => {
    actions.details.load(clientId);
  }, [clientId, actions]);

  const clearDetails = useCallback(() => {
    actions.details.clear();
  }, [actions]);

  function submitTransportationForm(url, token, action = "") {
    if (allAreNotEmpty(url, token)) {
      const form = transportationFormRef.current;

      $(form).attr("action", url);

      $(form).find('[name="payload"]').val(token);
      $(form).find('[name="action"]').val(action);

      form.submit();
    }
  }

  const onCreateTransportationRideRequest = useCallback(() => {
    actions.transportation.ride.request.load({ clientId }).then(
      Response(({ data: { url, token } = {} }) => {
        submitTransportationForm(url, token, "create");
      }),
    );
  }, [actions, clientId]);

  const onOpenTransportationRideHistory = useCallback(() => {
    actions.transportation.ride.history.load({ clientId }).then(
      Response(({ data: { url, token } = {} }) => {
        submitTransportationForm(url, token);
      }),
    );
  }, [actions, clientId]);

  const onSelectOption = useCallback(
    async (name) => {
      switch (name) {
        case EDIT_DETAILS: {
          fetchData(clientId);
          if (data.isHL7) {
            toggleHL7WarningDialog(true);
          } else {
            toggleEditor(true);
          }
          break;
        }
        case CREATE_EVENT: {
          toggleEventEditor(true);
          break;
        }
        case CREATE_ASSESSMENT: {
          history.push(path(`clients/${clientId}/assessments`), { shouldCreate: true });
          break;
        }
        case CREATE_SERVICE_PLAN: {
          history.push(path(`clients/${clientId}/service-plans`), { shouldCreate: true });
          break;
        }
        case VIEW_INCIDENT_REPORTS:
          history.push(path(`/incident-reports`));
          break;
        case CHAT:
          history.push(path(`/chats`), {
            employeeIds: [user?.id, contact?.id],
            conversationSid: contact?.conversationSid,
          });
          break;
        case VIDEO_CHAT: {
          if (user?.roleName === "ROLE_PERSON_RECEIVING_SERVICES") {
            history.push(path(`/chats`), {
              employeeIds: [user?.id, contact?.id],
              conversationSid: contact?.conversationSid,
            });
            break;
          }
          emit("attemptCall", {
            companionAvatarId: contact.avatarId,
            employeeIds: [user.id, contact?.id],
            conversationSid: contact?.conversationSid,
          });
          break;
        }
        case TRANSPORTATION_REQUEST_A_NEW_RIDE: {
          setTransportationAction(TRANSPORTATION_ACTION.RIDE);

          if (isDataValid(data) && (data.email || user?.email)) {
            onCreateTransportationRideRequest();
          } else toggleInvalidDataWarningDialog(true);
          break;
        }
        case TRANSPORTATION_RIDE_HISTORY: {
          setTransportationAction(TRANSPORTATION_ACTION.HISTORY);

          if (isDataValid(data) && (data.email || user?.email)) {
            onOpenTransportationRideHistory();
          } else toggleInvalidDataWarningDialog(true);
          break;
        }
        case CARE_TEAM: {
          history.push(path(`clients/${clientId}/care-team`));
          break;
        }
        case ACTIVATE_CLIENT: {
          toggleActivationEditor(true);
          break;
        }
        case DEACTIVATE_CLIENT: {
          toggleDeactivationEditor(true);
          break;
        }
        case CREATE_REFERRAL:
          toggleReferralRequestEditor(true);
          break;

        case PLACE_ORDER:
          toggleLabResearchOrderEditor(true);
          break;

        case CREATE_APPOINTMENT: {
          toggleAppointmentEditor(true);
          break;
        }
        case ADD_MEDICATION: {
          toggleMedicationEditor(true);
          break;
        }
      }
    },
    [user, data, emit, contact, history, clientId, onOpenTransportationRideHistory, onCreateTransportationRideRequest],
  );

  const onSaveSuccess = useCallback(() => {
    fetchDetails();
    toggleEditor(false);
    setTransportationAction(null);
    toggleDeactivationEditor(false);
    toggleActivationEditor(false);

    invalidate("Client", { clientId });
    invalidate("DocumentTemplateScheme", { clientId });
  }, [fetchDetails, invalidate, clientId]);

  const onCloseEditor = useCallback(() => {
    toggleEditor(false);
    setTransportationAction(null);
    toggleDeactivationEditor(false);
    toggleActivationEditor(false);
  }, []);

  const onApproveAccess = useCallback(
    (o) => {
      setAccessRequestDialogs(reject(accessRequestDialogs, (d) => d.id === o.id));
      approveAccess({ requestId: o.id }).then(() => {
        toggleAccessApprovingSuccessDialog(true);
      });
    },
    [approveAccess, accessRequestDialogs],
  );

  const onDeclineAccess = useCallback(
    (o) => {
      setAccessRequestDialogs(reject(accessRequestDialogs, (d) => d.id === o.id));
      declineAccess({ requestId: o.id }).then(() => {
        toggleAccessDecliningSuccessDialog(true);
      });
    },
    [declineAccess, accessRequestDialogs],
  );

  const onEventSaveSuccess = useCallback((id) => {
    setSaved({ id });
    toggleEventEditor(false);
    toggleEventSaveSuccessDialog(true);
  }, []);

  const onAppointmentSaveSuccess = useCallback(
    (appointmentId) => {
      invalidate("Appointments");
      invalidate("Events", { clientId, limit: 4 });

      toggleAppointmentEditor(false);

      toggleAppointmentSaveSuccessDialog(true);
    },
    [clientId, invalidate, toggleAppointmentEditor, toggleAppointmentSaveSuccessDialog],
  );

  const onMedicationSaveSuccess = useCallback(
    (appointmentId) => {
      invalidate("Medication");
      invalidate("Events", { clientId, limit: 4 });
      toggleMedicationEditor(false);
      setIsRefresh(!isRefresh);
      setIsMedicationSaveSuccessDialogOpen(true);
    },
    [clientId, invalidate, toggleMedicationEditor, setIsMedicationSaveSuccessDialogOpen],
  );

  const onNoteSaveSuccess = useCallback(() => {
    toggleNoteEditor(false);
    toggleNoteSaveSuccessDialog(true);
  }, []);

  const onCloseNoteEditor = useCallback(() => toggleNoteEditor(false), []);

  useCanAddNoteQuery({ clientId });
  useCanAddEventQuery({ clientId });
  useCanEditClientQuery({ clientId });
  useCanAddAssessmentQuery({ clientId });
  useCanAddServicePlanQuery({ clientId });
  useCanAddLabResearchOrderQuery({ clientId });

  useCanViewIncidentReports(null);

  let content = null;

  if (isDataEmpty && isFetching) {
    content = <Loader />;
  } else if (isDataEmpty) {
    content = <h4>No Data</h4>;
  } else {
    const canStartConversation = isInteger(contact?.id) && (contact?.conversationSid || contact?.canStartConversation);

    const canStartVideoCall = isInteger(contact?.id) && contact?.canStartVideoCall;

    // 检查localStorage中的triggerCurrentOrgId，根据环境隐藏Create Assessment选项
    const triggerCurrentOrgId = localStorage.getItem("triggerCurrentOrgId");
    const disableCreateAssessment = (() => {
      // 根据当前环境判断
      const environment = process.env.REACT_APP_SENTRY_ENVIRONMENT;

      if (environment === "localhost") {
        // 本地/localhost，检查是否为2978
        return triggerCurrentOrgId === "2978";
      } else if (environment === "production") {
        // app环境，检查是否为10492
        return triggerCurrentOrgId === "10492";
      }
      // 其他环境不隐藏
      return false;
    })();

    const permissions = {
      [EDIT_DETAILS]: canEdit && isActive,
      [CREATE_EVENT]: isActive,
      // TODO: fix appointment
      [CREATE_APPOINTMENT]: isActive,
      [ADD_MEDICATION]: isActive,
      [CREATE_ASSESSMENT]: canAddAssessment && isActive,
      [CREATE_SERVICE_PLAN]: canAddServicePlan && isActive,
      [VIEW_INCIDENT_REPORTS]: canViewIncidentReports,
      [CHAT]: canStartConversation && isActive,
      [VIDEO_CHAT]: canStartVideoCall && isActive,
      [ACTIVATE_CLIENT]: canEdit && !isActive,
      [DEACTIVATE_CLIENT]: canEdit && isActive,
      [PLACE_ORDER]: canAddResearchOrder && isActive,
      [TRANSPORTATION_REQUEST_A_NEW_RIDE]: data.canRequestRide && isNotEmpty(user?.email) && isActive,
      [TRANSPORTATION_RIDE_HISTORY]: data.canViewRideHistory && isNotEmpty(user?.email),
    };

    const disabled = {
      [CREATE_EVENT]: !(dashboardPermissions?.canViewEvents && canAddEvent),
      [CREATE_ASSESSMENT]: disableCreateAssessment,
    };

    const options = OPTIONS.filter((option) => permissions[option.name]).map((o) => ({
      id: o.name,
      text: o.title,
      value: o.name,
      tooltip: o?.tooltip,
      hasSeparator: o.hasSeparator,
      isDisabled: disabled[o.name],
      onClick: () => onSelectOption(o.name),
    }));

    content = (
      <>
        <div className="ClientDashboard-Body">
          <Breadcrumbs
            className="ClientDashboard-Breadcrumbs"
            items={compact([
              {
                title: "Clients",
                href: "/clients",
                isEnabled: true,
              },
              data && {
                title: data.fullName,
                href: `/clients/${clientId}`,
                isActive: true,
              },
              {
                title: "Dashboard",
                href: "#",
                isActive: true,
              },
            ])}
          />
          <div className="ClientDashboard-Header">
            <div className="ClientDashboard-Title">
              Client Details
              <span className="ClientDashboard-ClientName">&nbsp;/&nbsp;{data.fullName}</span>
            </div>
            <div className="ClientDashboard-ControlPanel">
              {options.length > 0 && (
                <Dropdown items={options} toggleText="More Options" className="ClientDashboard-MoreOptionsDropdown" />
              )}
              <Button
                color="success"
                disabled={!(canAddNote && isActive)}
                className="ClientDashboard-AddNoteBtn"
                onClick={() => toggleNoteEditor(true)}
                tooltip={canAddNote && isActive ? "" : "You don't have permissions to Client's Events&Notes"}
              >
                Add a Note
              </Button>
            </div>
          </div>
          <div className="ClientDashboard-Section">
            <ClientDetails clientId={clientId} onRefresh={fetchDetails} />
            <ClientDocumentsDevicesSummary
              clientId={clientId}
              clientName={data.fullName}
              onViewAllDocuments={() => {
                history.push(path(`clients/${clientId}/documents`));
              }}
            />
          </div>

          {canViewServicePlans && <ClientServicePlanSummary clientId={clientId} className="margin-bottom-60" />}

          {canViewAssessments && <ClientAssessmentsSummary clientId={clientId} className="margin-bottom-60" />}

          <div className="ClientDashboard-Section">
            {dashboardPermissions?.canViewEvents && (
              <ClientRecentEventsSummary clientId={clientId} className="ClientDashboard-RecentEventsSummarySection" />
            )}

            {dashboardPermissions?.canViewNotes && (
              <ClientRecentNotesSummary clientId={clientId} className="ClientDashboard-RecentNotesSummarySection" />
            )}
          </div>
          {dashboardPermissions?.canViewMedications && (
            <ClientMedicationsSummary
              onCreateMedication={() => toggleMedicationEditor(true)}
              isRefresh={isRefresh}
              isMedicationSaveSuccessDialogOpen={isMedicationSaveSuccessDialogOpen}
              clientId={clientId}
              className="margin-bottom-60"
            />
          )}
          <ClientProblemsSummary clientId={clientId} className="margin-bottom-60" />
          <ClientAllergiesSummary clientId={clientId} />
          <ClientEditor
            isOnDashboard
            isOpen={isEditorOpen}
            clientId={clientId}
            isClientEmailRequired={isNotEmpty(transportationAction) && !(data.email || user?.email)}
            isValidationNeed={isNotEmpty(transportationAction)}
            onClose={onCloseEditor}
            onSaveSuccess={onSaveSuccess}
            changeWaringDialog={changeWaringDialog}
            canShowReminder={canShowReminder}
            canEdit={canEdit}
            clientFullName={data?.fullName}
          />

          {isInvalidDataWarningDialogOpen && (
            <WarningDialog
              isOpen
              title={`Please fill in the required fields to ${TRANSPORTATION_ACTION_DESC[transportationAction]}`}
              buttons={[
                {
                  text: "Cancel",
                  outline: true,
                  onClick: () => {
                    setTransportationAction(null);
                    toggleInvalidDataWarningDialog(false);
                  },
                },
                {
                  text: "Edit Record",
                  onClick: () => {
                    toggleEditor(true);
                    toggleInvalidDataWarningDialog(false);
                  },
                },
              ]}
            />
          )}

          {isHL7WarningDialogOpen && (
            <WarningDialog
              isOpen
              title="SC allows you to edit the data but it can be automatically updated through next Pharmacy sync."
              buttons={[
                {
                  text: "Cancel",
                  outline: true,
                  onClick: () => {
                    toggleHL7WarningDialog(false);
                  },
                },
                {
                  text: "Ok",
                  onClick: () => {
                    toggleHL7WarningDialog(false);
                    toggleEditor(true);
                  },
                },
              ]}
            />
          )}

          {(isCommunityAdmin || isOrganizationAdmin) &&
            map(accessRequestDialogs, (o) => <WarningDialog key={o.id} {...o} />)}

          {isAccessRequestStatusChangedDialogOpen && (
            <WarningDialog
              isOpen
              title="The Client's status has been already changed"
              buttons={[{ text: "Close", onClick: () => toggleAccessRequestStatusChangedDialog(false) }]}
            />
          )}

          {isAccessApprovingSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title="The request has been approved"
              buttons={[
                {
                  text: "Close",
                  outline: true,
                  onClick: () => toggleAccessApprovingSuccessDialog(false),
                },
                {
                  text: "View Care Team",
                  onClick: () => history.push(path(`/clients/${clientId}/care-team`)),
                },
              ]}
            />
          )}

          {isAccessDecliningSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title="The request has been declined"
              buttons={[
                {
                  text: "Close",
                  outline: true,
                  onClick: () => toggleAccessDecliningSuccessDialog(false),
                },
              ]}
            />
          )}

          {isEventEditorOpen && (
            <EventEditor
              isOpen
              clientId={clientId}
              onClose={() => toggleEventEditor(false)}
              onSaveSuccess={onEventSaveSuccess}
            />
          )}

          <AppointmentEditor
            clientId={clientId}
            isOpen={isAppointmentEditorOpen}
            onClose={() => toggleAppointmentEditor(false)}
            onSaveSuccess={onAppointmentSaveSuccess}
          />

          <ClientMedicationEditor
            clientId={clientId}
            onClose={() => toggleMedicationEditor(false)}
            onSaveSuccess={onMedicationSaveSuccess}
            isOpen={isMedicationEditor}
          />

          {isAppointmentSaveSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title="The appointment has been created."
              buttons={[
                {
                  text: "Close",
                  onClick: () => toggleAppointmentSaveSuccessDialog(false),
                },
              ]}
            />
          )}

          {isMedicationSaveSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title="The medication has been created"
              buttons={[
                {
                  text: "Close",
                  onClick: () => setIsMedicationSaveSuccessDialogOpen(false),
                },
              ]}
            />
          )}

          {isEventSaveSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title="The event has been submitted"
              buttons={[
                {
                  text: "Close",
                  outline: true,
                  onClick: () => toggleEventSaveSuccessDialog(false),
                },
                {
                  text: "View details",
                  onClick: () => {
                    history.push(path(`clients/${clientId}/events`), { selected: saved });
                  },
                },
              ]}
            />
          )}

          {isNoteEditorOpen && (
            <NoteEditor
              isOpen
              clientId={clientId}
              clientName={data.fullName}
              onClose={onCloseNoteEditor}
              onSaveSuccess={onNoteSaveSuccess}
            />
          )}

          {isNoteSaveSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title="The note has been created."
              buttons={[
                {
                  text: "Close",
                  outline: true,
                  onClick: () => toggleNoteSaveSuccessDialog(false),
                },
                {
                  text: "View Note",
                  onClick: () => {
                    history.push(path(`clients/${clientId}/events`), { tab: 2 });
                  },
                },
              ]}
            />
          )}

          <ClientActivationEditor
            isOpen={isActivationEditorOpen}
            clientId={clientId}
            onClose={onCloseEditor}
            onSaveSuccess={onSaveSuccess}
          />

          <ClientDeactivationEditor
            isOpen={isDeactivationEditorOpen}
            clientId={clientId}
            onClose={onCloseEditor}
            onSaveSuccess={onSaveSuccess}
          />

          <ReferralRequestEditor
            isOpen={isReferralRequestEditorOpen}
            communityId={data.communityId}
            onClose={() => toggleReferralRequestEditor(false)}
            onSaveSuccess={() => toggleReferralRequestEditor(false)}
          />

          <LabOrderEditor
            clientId={clientId}
            communityId={data.communityId}
            organizationId={data.organizationId}
            isOpen={isLabResearchOrderEditorOpen}
            onClose={() => toggleLabResearchOrderEditor(false)}
          />
        </div>

        {(isApprovingAccess || isDecliningAccess) && <LoadingDialog isOpen />}

        {accessError && <ErrorViewer isOpen error={accessError} onClose={() => setAccessError(null)} />}

        {error && <ErrorViewer isOpen error={error} onClose={() => setError(null)} />}
      </>
    );
  }

  useEffect(() => {
    if (accessRequest && accessRequest.status !== PENDING && (isCommunityAdmin || isOrganizationAdmin)) {
      toggleAccessRequestStatusChangedDialog(true);
    }
  }, [accessRequest, isCommunityAdmin, isOrganizationAdmin]);

  useEffect(() => {
    fetchDetails();
    return clearDetails;
  }, [fetchDetails, clearDetails]);

  return (
    <DocumentTitle title="Simply Connect | Clients | Client Dashboard">
      <div className="ClientDashboard">
        <UpdateSideBarAction
          params={{
            clientId,
            shouldRefresh: isEventSaveSuccessDialogOpen || isNoteSaveSuccessDialogOpen,
          }}
        />
        {content}
        <form method="POST" target="_blank" className="d-none" ref={transportationFormRef}>
          <input name="action" />
          <input name="payload" />
        </form>

        <WarningDialog
          isOpen={waringDialog}
          title="Are you sure you don't want to show this message again?"
          buttons={[
            {
              text: "Cancel",
              onClick: () => {
                changeWaringDialog();
              },
            },
            {
              text: "OK",
              onClick: () => {
                changeWaringDialog();
                setRemindShow(clientId);
                fetchData(clientId);
              },
            },
          ]}
        />
      </div>
    </DocumentTitle>
  );
}

export default connect(mapStateToProps, mapDispatchToProps)(ClientDashboard);
