import React, { createContext, useCallback, useEffect, useMemo, useState } from "react";

import { compact, findWhere, map } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { Link, useHistory, useLocation, useParams } from "react-router-dom";

import { Button } from "reactstrap";

import DocumentTitle from "react-document-title";

import { useExternalProviderRoleCheck } from "hooks/business/external";
import { useRequestContactsQuery } from "hooks/business/admin/referrals";

import { Breadcrumbs, Dropdown, ErrorViewer, Loader, MultiSelect, ScrollTop, Table } from "components";

import { ConfirmDialog } from "components/dialogs";

import { Detail, PictureDetail } from "components/business/common";

import { DocumentDetail } from "components/business/Documents";

import { UpdateSideBarAction } from "actions/clients";
import { UpdateSideBarAction as UpdateReferralSideBarAction } from "actions/referrals";

import ClientDocumentDetail from "containers/Clients/Clients/Documents/DocumentDetail/DocumentDetail";

import actions from "redux/referral/details/referralDetailsActions";

import assignActions from "redux/referral/request/assign/referralRequestAssignActions";
import acceptActions from "redux/referral/request/accept/referralRequestAcceptActions";
import cancelActions from "redux/referral/request/cancel/referralRequestCancelActions";
import unassignActions from "redux/referral/request/unassign/referralRequestUnassignActions";
import preadmitActions from "redux/referral/request/pre-admit/referralRequestPreadmitActions";
import {
  ALLOWED_FILE_FORMATS,
  REFERRAL_STATUS_COLORS,
  REFERRAL_STATUSES,
  REFERRAL_TYPES,
  SERVER_ERROR_CODES,
} from "lib/Constants";

import { DateUtils as DU, getAddress, getFileFormatByMimeType, isEmpty, isInteger } from "lib/utils/Utils";

import { ReactComponent as Close } from "images/close.svg";
import { ReactComponent as Pencil } from "images/pencil.svg";
import { ReactComponent as Warning } from "images/alert-yellow.svg";

import { path } from "lib/utils/ContextUtils";
import { isNotEmptyOrBlank } from "lib/utils/ObjectUtils";

import "./ReferralDetails.scss";

import ReferralRequests from "../ReferralRequests/ReferralRequests";
import ReferralCommunication from "../ReferralCommunication/ReferralCommunication";

import RequestInfoEditor from "../RequestInfoEditor/RequestInfoEditor";
import RequestDeclineEditor from "../RequestDeclineEditor/RequestDeclineEditor";

import ReferralStatusEditor from "../ReferralStatusEditor/RequestStatusEditor";
import ReferralRequestAcceptEditor from "../ReferralRequestAcceptEditor/ReferralRequestAcceptEditor";

export const DetailsContext = createContext();

const { INBOUND, OUTBOUND } = REFERRAL_TYPES;

const { PRE_ADMIT, ACCEPTED, DECLINED } = REFERRAL_STATUSES;

const { ACCESS_DENIED, ACCOUNT_INACTIVE } = SERVER_ERROR_CODES;

const { PDF, XML, DOC, TIFF, DOCX } = ALLOWED_FILE_FORMATS;

const REQUEST_INFO = "REQUEST_INFO";

const REFERRAL_TYPE_TITLES = {
  [INBOUND]: "Inbound",
  [OUTBOUND]: "Outbound",
};

const { format, formats } = DU;

const DATE_FORMAT = formats.longDateMediumTime12;

function isIgnoredError(e = {}) {
  return e.code === ACCOUNT_INACTIVE;
}

function valueTextMapper(o) {
  return { value: o.id, text: o.title };
}

function mapStateToProps(state) {
  return {
    state: state.referral.details,
    user: state.auth.login.user.data,
    contact: state.referral.request.contact,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(actions, dispatch),
      assign: bindActionCreators(assignActions, dispatch),
      accept: bindActionCreators(acceptActions, dispatch),
      cancel: bindActionCreators(cancelActions, dispatch),
      unassign: bindActionCreators(unassignActions, dispatch),
      preadmit: bindActionCreators(preadmitActions, dispatch),
    },
  };
}

function ReferralDetails({ state, actions, user, contact }) {
  const { data, error } = state;

  const { clear, load } = actions;

  const params = useParams();

  const clientId = parseInt(params.clientId);

  const isClient = isInteger(clientId);

  const requestId = parseInt(params.requestId) || undefined;

  const referralId = parseInt(params.referralId) || undefined;

  const history = useHistory();

  const { pathname } = useLocation();

  const type = pathname.includes("inbound") ? INBOUND : OUTBOUND;

  const isExternalProvider = useExternalProviderRoleCheck();

  const friendlyName = REFERRAL_TYPE_TITLES[type];

  const [assignee, setAssignee] = useState(null);
  const [nextAssignee, setNextAssignee] = useState(null);

  const [isFetching, setIsFetching] = useState(true);
  const [isAssigning, setIsAssigning] = useState(false);

  const [isCancelRequestDialogOpen, toggleCancelRequestDialog] = useState(false);
  const [isRequestDeclineEditorOpen, toggleRequestDeclineEditor] = useState(false);
  const [isRequestInfoEditorOpen, toggleRequestInfoEditor] = useState(false);
  const [isRequestPreadmitDialogOpen, toggleRequestPreadmitDialog] = useState(false);
  const [isRequestAcceptDialogOpen, toggleRequestAcceptDialog] = useState(false);
  const [isAssignConfirmDialogOpen, toggleAssignConfirmDialog] = useState(false);
  const [isStatusEditorDialogOpen, toggleStatusEditorDialog] = useState(false);

  const contacts = contact.list.dataSource.data;

  const mappedContacts = useMemo(() => map(contacts, valueTextMapper), [contacts]);

  function fetch() {
    setIsFetching(true);

    load({
      clientId,
      requestId,
      referralId,
    })
      .then(({ data }) => {
        setAssignee({
          id: data?.assigneeId,
          name: data?.assigneeName,
        });
      })
      .finally(() => setIsFetching(false));
  }

  const onFetch = useCallback(fetch, [clientId, load, referralId, requestId]);

  const onChangeAssignee = useCallback(
    (id, cancel) => {
      const assignee = { id, cancel };

      if (isInteger(id)) {
        const contact = findWhere(contacts, { id });
        assignee.name = contact?.title;
      }

      setNextAssignee(assignee);
      toggleAssignConfirmDialog(true);
    },
    [contacts],
  );

  const onRequestInfo = useCallback(() => toggleRequestInfoEditor(true), []);

  const onPreAdmit = useCallback(() => toggleRequestPreadmitDialog(true), []);

  const onAccept = useCallback(() => toggleRequestAcceptDialog(true), []);

  const onDecline = useCallback(() => toggleRequestDeclineEditor(true), []);

  const onCancelRequest = useCallback(() => toggleCancelRequestDialog(true), []);

  const onChangeStatus = useCallback(() => toggleStatusEditorDialog(true), []);

  const onCancelRequestConfirm = useCallback(() => {
    actions.cancel.load(referralId).then(onFetch);
    toggleCancelRequestDialog(false);
  }, [actions.cancel, onFetch, referralId]);

  const onPreadmitRequestConfirm = useCallback(() => {
    actions.preadmit.load(requestId).then(onFetch);
    toggleRequestPreadmitDialog(false);
  }, [actions.preadmit, onFetch, requestId]);

  const onAcceptRequestConfirm = useCallback(() => {
    onFetch();
    toggleRequestAcceptDialog(false);
  }, [onFetch]);

  const onCloseDeclineRequestEditor = useCallback(() => toggleRequestDeclineEditor(false), []);
  const onCloseRequestInfoEditor = useCallback(() => toggleRequestInfoEditor(false), []);

  const onConfirmAssigning = useCallback(() => {
    setIsAssigning(false);
    setNextAssignee(null);
    toggleAssignConfirmDialog(false);

    const { id, name } = nextAssignee;

    if (isInteger(id)) {
      setAssignee({ id, name });
      actions.assign.load(id, { requestId });
    } else {
      setAssignee(null);
      actions.unassign.load({ requestId });
    }
  }, [actions, requestId, nextAssignee]);

  const onCancelAssigning = useCallback(() => {
    nextAssignee.cancel();
    setIsAssigning(false);
    setNextAssignee(null);
    toggleAssignConfirmDialog(false);
  }, [nextAssignee]);

  const onStatusChangeSuccess = useCallback(() => {
    onFetch();
    toggleStatusEditorDialog(false);
  }, [onFetch]);

  useRequestContactsQuery({ requestId });

  useEffect(() => {
    if (isExternalProvider && error?.code === ACCESS_DENIED) {
      history.push(path("/external-provider/login"));
    }
  }, [error, history, isExternalProvider]);

  let content;

  if (isFetching) {
    content = <Loader />;
  } else if (isEmpty(data)) {
    content = <h4>No Data</h4>;
  } else {
    const referralActions = [
      {
        value: PRE_ADMIT,
        text: "Pre-admit",
        onClick: onPreAdmit,
        isVisible: data.canPreadmit && (isExternalProvider || isEmpty(data.marketplace)),
      },
      {
        value: REQUEST_INFO,
        text: "Request info",
        onClick: onRequestInfo,
        isVisible: data.canRequestInfo,
      },
      {
        value: ACCEPTED,
        text: "Accept",
        onClick: onAccept,
        isVisible: data.canAccept,
      },
      {
        value: DECLINED,
        text: "Decline",
        onClick: onDecline,
        isVisible: data.canDecline,
      },
    ].filter((o) => o.isVisible);

    content = (
      <>
        <Breadcrumbs
          items={compact([
            {
              title: "Referrals and Inquiries",
              href: `${isExternalProvider ? "/external-provider" : ""}/${type.toLowerCase()}-referrals`,
              isEnabled: isExternalProvider,
            },
            ...(!isExternalProvider
              ? [
                  {
                    title: friendlyName,
                    href: `${type.toLowerCase()}-referrals`,
                    isEnabled: true,
                  },
                ]
              : []),
            {
              title: "Referral Details",
              href: `${isExternalProvider ? "/external-provider" : ""}/${type.toLowerCase()}-referrals/${referralId}/requests/${requestId}`,
              isActive: true,
            },
          ])}
          className="margin-bottom-40"
        />
        <div className="ReferralDetails-Header">
          <div className="ReferralDetails-Title">{friendlyName} Referral</div>
          <div className="ReferralDetails-ControlPanel">
            {type === INBOUND ? (
              referralActions.length > 0 && (
                <Dropdown
                  items={referralActions}
                  toggleText="Take Action"
                  className="ReferralDetails-ActionDropdown"
                  // isDisabled={!user?.hieAgreement}
                />
              )
            ) : (
              <>
                {data.canCancel && (
                  <Button color="success" onClick={onCancelRequest} className="ReferralDetails-Btn">
                    Cancel request
                  </Button>
                )}
                {![ACCEPTED, DECLINED].includes(data.statusName) && data?.marketplace?.sharedChannel === "FAX" && (
                  <Button color="success" onClick={onChangeStatus} className="ReferralDetails-Btn">
                    Change Status
                  </Button>
                )}
              </>
            )}
          </div>
        </div>
        <div className="ReferralDetails-Body">
          <div className="ReferralDetails-Section">
            <div className="ReferralDetails-SectionTitle">Request</div>
            <>
              <Detail
                title="Request Id"
                titleClassName="ReferralDetail-Title"
                valueClassName="ReferralDetail-Value"
                className="ReferralDetail"
              >
                {data.id}
              </Detail>
              <Detail
                title="Request Date"
                titleClassName="ReferralDetail-Title"
                valueClassName="ReferralDetail-Value"
                className="ReferralDetail"
              >
                {format(data.date, DATE_FORMAT)}
              </Detail>
              {data.marketplace && (
                <Detail
                  title="Sent through"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail"
                >
                  {data.marketplace.sharedChannel === "FAX" ? "FAX" : "Email"}
                </Detail>
              )}
              {type === INBOUND && (
                <>
                  <Detail
                    title="Status"
                    titleClassName="ReferralDetail-Title"
                    valueClassName="ReferralDetail-Value"
                    className="ReferralDetail"
                  >
                    <div
                      className="ReferralDetail-Status"
                      style={{ backgroundColor: REFERRAL_STATUS_COLORS[data.statusName] }}
                    >
                      {data.statusTitle}
                    </div>
                  </Detail>
                  <Detail
                    title="Decline reason"
                    titleClassName="ReferralDetail-Title"
                    valueClassName="ReferralDetail-Value"
                    className="ReferralDetail"
                  >
                    {data.declineReason}
                  </Detail>
                  <Detail
                    title="Comment"
                    titleClassName="ReferralDetail-Title"
                    valueClassName="ReferralDetail-Value"
                    className="ReferralDetail"
                  >
                    {data.comment}
                  </Detail>
                  <Detail
                    title="Service Start Date"
                    titleClassName="ReferralDetail-Title"
                    valueClassName="ReferralDetail-Value"
                    className="ReferralDetail"
                  >
                    {format(data.serviceStartDate, DATE_FORMAT)}
                  </Detail>
                  <Detail
                    title="Service End Date"
                    titleClassName="ReferralDetail-Title"
                    valueClassName="ReferralDetail-Value"
                    className="ReferralDetail"
                  >
                    {format(data.serviceEndDate, DATE_FORMAT)}
                  </Detail>
                </>
              )}
              <Detail
                title="Priority"
                titleClassName="ReferralDetail-Title"
                valueClassName="ReferralDetail-Value"
                className="ReferralDetail"
              >
                {data.priorityTitle}
              </Detail>
              <Detail
                title="Service"
                titleClassName="ReferralDetail-Title"
                valueClassName="ReferralDetail-Value"
                className="ReferralDetail"
              >
                {data.serviceTitle}
              </Detail>
              <Detail
                title="Referring Individual"
                titleClassName="ReferralDetail-Title"
                valueClassName="ReferralDetail-Value"
                className="ReferralDetail"
              >
                {data.person}
              </Detail>
              <Detail
                isDisabled
                title="Referring Community"
                titleClassName="ReferralDetail-Title"
                valueClassName="ReferralDetail-Value"
                className="ReferralDetail"
              >
                {data.communityTitle}
              </Detail>
              <Detail
                title="Phone #"
                titleClassName="ReferralDetail-Title"
                valueClassName="ReferralDetail-Value"
                className="ReferralDetail"
              >
                {data.organizationPhone}
              </Detail>
              <Detail
                title="Email"
                titleClassName="ReferralDetail-Title"
                valueClassName="ReferralDetail-Value"
                className="ReferralDetail"
              >
                {data.organizationEmail}
              </Detail>
              <Detail
                title="Referring instructions"
                titleClassName="ReferralDetail-Title"
                valueClassName="ReferralDetail-Value"
                className="ReferralDetail"
              >
                {data.instructions}
              </Detail>
              {type === INBOUND && !isExternalProvider && (
                <Detail
                  title="Assigned To"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail"
                >
                  {isAssigning ? (
                    <>
                      <MultiSelect
                        name="assignedTo"
                        value={assignee?.id}
                        options={[{ value: "unassigned", text: "Unassigned" }, ...mappedContacts]}
                        hasAutoScroll
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        isMultiple={false}
                        hasEmptyValue={false}
                        placeholder="Choose assignee"
                        className="ReferralDetail-AssigneeSelect margin-right-10"
                        onChange={onChangeAssignee}
                      />
                      <Close
                        onClick={() => {
                          setIsAssigning(false);
                        }}
                        className="ReferralDetail-CancelAssigningBtn"
                      />
                    </>
                  ) : (
                    <>
                      <span className="margin-right-10">{assignee?.name ?? "Unassigned"}</span>
                      {data.canAssign && (
                        <Pencil
                          onClick={() => {
                            setIsAssigning(true);
                          }}
                          className="ReferralDetail-EditAssigneeBtn"
                        />
                      )}
                    </>
                  )}
                </Detail>
              )}
              {type === OUTBOUND && (
                <Detail
                  title="Sent By"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail"
                >
                  {data.person}
                </Detail>
              )}
            </>
          </div>
          {isNotEmptyOrBlank(data.client) && (
            <div className="ReferralDetails-Section">
              <div className="ReferralDetails-SectionTitle">Client information</div>
              <>
                <Detail
                  title="Name"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail"
                >
                  {data.client.canView && !isExternalProvider ? (
                    <Link className="ReferralDetails-Client" to={path(`/clients/${data.client.id}`)}>
                      {data.client.fullName}
                    </Link>
                  ) : (
                    data.client.fullName
                  )}
                </Detail>
                <Detail
                  title="Gender"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail"
                >
                  {data.client.gender}
                </Detail>
                <Detail
                  title="Date Of Birth"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail"
                >
                  {data.client.birthDate}
                </Detail>
                <Detail
                  title="Diagnosis"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail ReferralDetail-Diagnosis"
                >
                  {map(data.client.diagnoses, (o) => (
                    <div>{o}</div>
                  ))}
                </Detail>
                <Detail
                  title="Location"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail"
                >
                  {data.client.location}
                </Detail>
                <Detail
                  title="Location Phone"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail"
                >
                  {data.client.locationPhone}
                </Detail>
                <Detail
                  title="Address"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail"
                >
                  {getAddress(data.client.address, ",")}
                </Detail>
                <Detail
                  title="Insurance Network"
                  titleClassName="ReferralDetail-Title"
                  valueClassName="ReferralDetail-Value"
                  className="ReferralDetail"
                >
                  {data.client.insuranceNetworkTitle}
                </Detail>
              </>
            </div>
          )}

          {isNotEmptyOrBlank(data.client) && (
            <>
              <div className="ReferralDetails-SectionTitle">Invited team personnel</div>

              <Table
                hasHover
                hasOptions
                keyField="id"
                hasCaption={false}
                noDataText="No inbound referrals and inquiries."
                isLoading={isFetching}
                className="ReferralList"
                containerClass="ReferralListContainer"
                data={data?.vendorCareTeams}
                hasPagination={false}
                columns={[
                  {
                    dataField: "contactName",
                    text: "Contact Name",
                  },
                  {
                    dataField: "roleName",
                    text: "Role",
                  },
                  {
                    dataField: "teamType",
                    text: "Team Type",
                  },
                ]}
                columnsMobile={["contactName"]}
                onRefresh={fetch}
              />
            </>
          )}
          {isNotEmptyOrBlank(data.client) &&
            (data.isCcdShared || data.isFacesheetShared || (data.hasSharedServicePlan && data.isServicePlanShared)) && (
              <div className="ReferralDetails-Section">
                <div className="ReferralDetails-SectionTitle">Additional clinical information</div>
                {data.isFacesheetShared && (
                  <ClientDocumentDetail
                    id="facesheet"
                    title="Facesheet"
                    format={PDF}
                    date={Date.now()}
                    canView={false}
                    clientId={data.client.id}
                  />
                )}
                {data.isCcdShared && (
                  <ClientDocumentDetail
                    id="ccd"
                    title="CCD"
                    format={XML}
                    date={Date.now()}
                    clientId={data.client.id}
                    clientName={data.client.fullName}
                  />
                )}
                {data.hasSharedServicePlan && data.isServicePlanShared && (
                  <ClientDocumentDetail
                    id="shared-service-plan"
                    title="Service Plan"
                    format={PDF}
                    date={Date.now()}
                    canView={false}
                    clientId={data.client.id}
                    clientName={data.client.fullName}
                  />
                )}
              </div>
            )}
          {isNotEmptyOrBlank(data.attachments) && (
            <div className="ReferralDetails-Section">
              <div className="ReferralDetails-SectionTitle">Attachments</div>
              {map(data.attachments, (o) => {
                const format = getFileFormatByMimeType(o.mimeType);

                const params = {
                  format,
                  id: o.id,
                  name: o.name,
                  mimeType: o.mimeType,
                  viewHint: "Click to preview the file",
                  downloadHint: "Click to download the file",
                  path: `/referrals/${type === OUTBOUND ? referralId : `referral-requests/${requestId}`}/attachments/${o.id}`,
                };

                return [PDF, DOC, TIFF, DOCX].includes(format) ? (
                  <DocumentDetail {...params} canView={false} />
                ) : (
                  <PictureDetail {...params} />
                );
              })}
            </div>
          )}
          {type === OUTBOUND && (
            <div className="ReferralDetails-Section">
              <ReferralRequests clientId={clientId} referralId={referralId} requestId={data.requestId} />
            </div>
          )}
          <div className="ReferralDetails-Section">
            <ReferralCommunication referralType={type} referralId={referralId} requestId={data.requestId} />
          </div>
        </div>
        <RequestDeclineEditor
          requestId={requestId}
          isOpen={isRequestDeclineEditorOpen}
          onClose={onCloseDeclineRequestEditor}
          onSubmit={onFetch}
        />

        <RequestInfoEditor
          requestId={requestId}
          isOpen={isRequestInfoEditorOpen}
          onClose={onCloseRequestInfoEditor}
          onSubmit={onFetch}
        />

        <ConfirmDialog
          isOpen={isCancelRequestDialogOpen}
          icon={Warning}
          confirmBtnText="Confirm"
          title="The referral request will be cancelled."
          onConfirm={onCancelRequestConfirm}
          onCancel={() => toggleCancelRequestDialog(false)}
        />

        <ConfirmDialog
          isOpen={isRequestPreadmitDialogOpen}
          icon={Warning}
          confirmBtnText="Pre-admit"
          title={`By clicking on the "Pre-admit" button, you confirm that the referral request has been reviewed and the initial contact to determine eligibility and schedule assessment has been made.`}
          onConfirm={onPreadmitRequestConfirm}
          onCancel={() => toggleRequestPreadmitDialog(false)}
        />

        <ReferralRequestAcceptEditor
          isOpen={isRequestAcceptDialogOpen}
          onClose={toggleRequestAcceptDialog}
          requestId={data.requestId}
          onSaveSuccess={onAcceptRequestConfirm}
        />

        <ReferralStatusEditor
          isOpen={isStatusEditorDialogOpen}
          canAccept={data.canAccept}
          canDecline={data.canDecline}
          requestId={data.referralRequestId}
          onSaveSuccess={onStatusChangeSuccess}
          onClose={() => toggleStatusEditorDialog(false)}
        />

        <ConfirmDialog
          isOpen={isAssignConfirmDialogOpen}
          icon={Warning}
          confirmBtnText="Confirm"
          title={
            isInteger(nextAssignee?.id)
              ? `Referral request will be assigned to ${nextAssignee.name}`
              : "Referral request will be unassigned"
          }
          onConfirm={onConfirmAssigning}
          onCancel={onCancelAssigning}
        />
      </>
    );
  }

  const contextValue = useMemo(
    () => ({
      refresh: onFetch,
    }),
    [onFetch],
  );

  useEffect(() => {
    onFetch();
  }, [onFetch]);

  useEffect(() => clear, [clear]);

  return (
    <DocumentTitle title={`Simply Connect | Details of ${friendlyName} referral`}>
      <div className="ReferralDetails">
        {user && <>{isClient ? <UpdateSideBarAction params={{ clientId }} /> : <UpdateReferralSideBarAction />}</>}
        <DetailsContext.Provider value={contextValue}>{content}</DetailsContext.Provider>
        {error && !isIgnoredError(error) && <ErrorViewer isOpen error={error} onClose={actions.clearError} />}
        <ScrollTop scrollable=".SideBar-Content" scrollTopBtnClass="ReferralDetails-ScrollTopBtn" />
      </div>
    </DocumentTitle>
  );
}

export default connect(mapStateToProps, mapDispatchToProps)(ReferralDetails);
