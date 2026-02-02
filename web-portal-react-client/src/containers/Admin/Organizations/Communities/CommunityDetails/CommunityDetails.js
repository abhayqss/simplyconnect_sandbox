import React, { useState, useEffect, useCallback } from "react";

import cn from "classnames";

import { map, compose } from "underscore";

import { Button } from "reactstrap";

import { useParams, withRouter } from "react-router-dom";

import DocumentTitle from "react-document-title";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { Loader, Action, Picture, Breadcrumbs, ErrorViewer, Tabs } from "components";

import { Detail } from "components/business/common";

import { PrimaryCommunitiesDetail, AffiliatedCommunitiesDetail } from "components/business/Admin/Organizations";

import { SuccessDialog, ConfirmDialog } from "components/dialogs";

import { useAuthUser } from "hooks/common/redux";

import { LoadCommunityAction, UpdateSideBarAction } from "actions/admin";

import * as communityLogoActions from "redux/community/logo/communityLogoActions";
import * as communityFormActions from "redux/community/form/communityFormActions";
import * as communityDetailsActions from "redux/community/details/communityDetailsActions";

import { isEmpty, isNotEmpty, allAreInteger, toNumberExcept, PhoneNumberUtils as PNU } from "lib/utils/Utils";

import { SERVER_ERROR_CODES, CARE_TEAM_AFFILIATION_TYPES } from "lib/Constants";

import CareTeamMemberList from "./CareTeamMemberList/CareTeamMemberList";

import CommunityEditor from "../CommunityEditor/CommunityEditor";

import { ReactComponent as Warning } from "images/alert-yellow.svg";

import "./CommunityDetails.scss";
import QrCode from "../../QrCode/QrCode";
import VendorOfAssociation from "./VendorOfAssociation";

const { REGULAR, AFFILIATED } = CARE_TEAM_AFFILIATION_TYPES;

function isIgnoredError(e = {}) {
  return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE;
}

function mapStateToProps(state) {
  const { details } = state.community;

  return {
    data: details.data,
    error: details.error,
    isFetching: details.isFetching,
    shouldReload: details.shouldReload,

    auth: state.auth,
    care: state.care,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(communityDetailsActions, dispatch),

      logo: bindActionCreators(communityLogoActions, dispatch),
      form: bindActionCreators(communityFormActions, dispatch),
    },
  };
}

function CommunityDetails({
  data,
  auth,
  isFetching,
  fetchCount,
  shouldReload,

  care,
  actions,

  className,
  ...props
}) {
  const [error, setError] = useState(null);
  const [shouldRefresh, setShouldRefresh] = useState(false);

  const [isEditorOpen, toggleEditor] = useState(false);
  const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useState(false);
  const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useState(false);
  const [showQrModel, setShowQrModel] = useState(false);

  const params = useParams();
  const user = useAuthUser();

  const communityId = toNumberExcept(params.commId, [null, undefined]);
  const organizationId = toNumberExcept(params.orgId, [null, undefined]);

  const [tab, setTab] = useState(0);

  function getError() {
    return error || props.error;
  }

  function resetError() {
    setError(null);
    actions.clearError();
  }

  const onSaveSuccess = useCallback(() => {
    toggleEditor(false);
    setShouldRefresh(true);
    toggleSaveSuccessDialog(true);
  }, []);

  const onCloseEditor = useCallback(
    (shouldConfirm = false) => {
      toggleEditor(shouldConfirm);
      toggleSaveSuccessDialog(false);
      toggleCancelEditConfirmDialog(shouldConfirm);

      !shouldConfirm && actions.form.clear();
    },
    [actions],
  );

  let content = null;

  const showQrModalFc = () => {
    setShowQrModel(!showQrModel);
  };

  const onChangeTab = (tabItem) => {
    if (tab === tabItem) {
      return;
    }
    setTab(tabItem);
  };

  if (isFetching) {
    content = <Loader />;
  } else if (isEmpty(data)) {
    content = <h4>No Data</h4>;
  } else {
    content = (
      <>
        <Action
          action={() => {
            setError(data.docutrackPharmacyConfig.docutrackError);
          }}
        />
        <UpdateSideBarAction params={{ changes: { isHidden: true } }} />
        <Breadcrumbs
          items={[
            { title: "Organizations", href: "/admin/organizations", isEnabled: true },
            { title: "Organization details", href: `/admin/organizations/${organizationId}` },
            {
              title: "Community details",
              href: `/admin/organizations/${organizationId}/communities`,
              isActive: true,
            },
          ]}
        />
        <div className="CommunityDetails-Header">
          <div className="CommunityDetails-Title">
            <span className="CommunityDetails-TitleText" title={data.name}>
              {data.name}
            </span>
          </div>

          <div className="CommunityDetails-ControlPanel">
            {data.canEdit && (
              <Button
                style={{ marginRight: "10px" }}
                color="success"
                outline
                className="OrganizationDetails-QRButton"
                onClick={showQrModalFc}
              >
                QR Code
                {/*    <img src={showQrList ? BottomImg : TopImg} alt=""/>

                  {
                    showQrList && <div className='qrList' onClick={(e) => e.stopPropagation()}>
                      <div onClick={
                        this.showQrModalFc
                      }>View Org. QR-Code
                      </div>
                      <div onClick={this.downloadQrCode}>Download All QR-Code</div>
                    </div>
                  }*/}
              </Button>
            )}
          </div>

          <div className="CommunityDetails-ControlPanel">
            {data.canEdit && (
              <Button color="success" className="CommunityDetails-EditBtn" onClick={() => toggleEditor(true)}>
                Edit Details
              </Button>
            )}
          </div>
        </div>
        <div className="CommunityDetails-Body">
          <div className="margin-bottom-30">
            <Detail
              className="CommunityDetail"
              titleClassName="CommunityDetail-Title"
              valueClassName="CommunityDetail-Value"
              title="Community Oid"
            >
              {data.oid}
            </Detail>
            <Detail
              className="CommunityDetail"
              titleClassName="CommunityDetail-Title"
              valueClassName="CommunityDetail-Value"
              title="License #"
            >
              {data.licenseNumber}
            </Detail>
            <Detail
              className="CommunityDetail"
              titleClassName="CommunityDetail-Title"
              valueClassName="CommunityDetail-Value"
              title="Email"
            >
              {data.email}
            </Detail>
            <Detail
              className="CommunityDetail"
              titleClassName="CommunityDetail-Title"
              valueClassName="CommunityDetail-Value"
              title="Phone"
            >
              {PNU.formatPhoneNumber(data.phone)}
            </Detail>
            <Detail
              className="CommunityDetail"
              titleClassName="CommunityDetail-Title"
              valueClassName="CommunityDetail-Value"
              title="Address"
            >
              {data.displayAddress}
            </Detail>
            <Detail
              className="CommunityDetail"
              titleClassName="CommunityDetail-Title"
              valueClassName="CommunityDetail-Value"
              title="# of units"
            >
              {data.numberOfBeds}
            </Detail>
            <Detail
              className="CommunityDetail"
              titleClassName="CommunityDetail-Title"
              valueClassName="CommunityDetail-Value"
              title="# of open units"
            >
              {data.numberOfVacantBeds}
            </Detail>
            <Detail
              className="CommunityDetail"
              titleClassName="CommunityDetail-Title"
              valueClassName="CommunityDetail-Value"
              title="Organization"
            >
              {data.organizationName}
            </Detail>
            {isNotEmpty(data.pictures) && (
              <Detail
                className="CommunityDetail"
                titleClassName="CommunityDetail-Title"
                valueClassName="CommunityDetail-Value d-flex flex-row flex-wrap"
                title="Photos"
              >
                {map(data.pictures, (o) => (
                  <Picture
                    hasViewer
                    path={`/organizations/${organizationId}/communities/${communityId}/pictures/${o.id}`}
                    mimeType={o.mimeType}
                    className="CommunityDetail-Photo"
                  />
                ))}
              </Detail>
            )}
            <Detail
              className="CommunityDetail"
              titleClassName="CommunityDetail-Title"
              valueClassName="CommunityDetail-Value"
              title="Logo"
            >
              <Picture
                path={`/organizations/${organizationId}/communities/${communityId}/logo`}
                className="CommunityDetail-Logo"
              />
            </Detail>

            <Detail
              className="CommunityDetail"
              titleClassName="CommunityDetail-Title"
              valueClassName="CommunityDetail-Value"
              title="Cover"
            >
              <Picture
                path={`/organizations/${organizationId}/communities/${communityId}/cover`}
                className="CommunityDetail-Logo"
              />
            </Detail>
            {/*<Detail
                                className="CommunityDetail"
                                titleClassName="CommunityDetail-Title"
                                valueClassName="CommunityDetail-Value"
                                title="Community clients participate in sharing data"
                            >
                                {data.isSharingData ? 'Yes' : 'No'}
                            </Detail>*/}
            {isNotEmpty(data.affiliationAffiliated) && (
              <AffiliatedCommunitiesDetail
                title="Current community is a primary one for:"
                organizationId={organizationId}
                data={data.affiliationAffiliated}
                className="CommunityDetail"
                titleClassName="CommunityDetail-Title"
                valueClassName="CommunityDetail-Value flex-column"
              />
            )}
            {isNotEmpty(data.affiliationPrimary) && (
              <PrimaryCommunitiesDetail
                title="Current community is added as affiliated community with:"
                organizationId={organizationId}
                data={data.affiliationPrimary}
                className="CommunityDetail"
                titleClassName="CommunityDetail-Title"
                valueClassName="CommunityDetail-Value flex-column"
              />
            )}
            {isNotEmpty(data.docutrackPharmacyConfig) && (
              <Detail
                className="CommunityDetail"
                titleClassName="CommunityDetail-Title"
                valueClassName="CommunityDetail-Value"
                title="DocuTrack Integration"
              >
                {data.docutrackPharmacyConfig.isIntegrationEnabled ? "Enabled" : "Disabled"}
              </Detail>
            )}
          </div>

          <div>
            <Tabs
              containerClassName="CommunityDetails-TabsContainer"
              items={
                !isEmpty(data.affiliationAffiliated)
                  ? [
                      { title: "Community Care Team", isActive: tab === 0, hasError: false },
                      { title: "Affiliated Community Care Team", isActive: tab === 1, hasError: false },
                      { title: "Linked Vendors", isActive: tab === 2, hasError: false },
                    ]
                  : [
                      { title: "Community Care Team", isActive: tab === 0, hasError: false },
                      { title: "Linked Vendors", isActive: tab === 1, hasError: false },
                    ]
              }
              onChange={onChangeTab}
            />
          </div>

          <div className="communityTabWrap">
            {tab === 0 && (
              <CareTeamMemberList
                title="Community Care Team"
                communityId={communityId}
                clientCommunityId={communityId}
                clientOrganizationId={organizationId}
                organizationId={organizationId}
                type={REGULAR}
              />
            )}

            {tab === 1 && !isEmpty(data.affiliationAffiliated) && (
              <CareTeamMemberList
                title="Affiliated Community Care Team"
                communityId={communityId}
                clientCommunityId={communityId} // Field is meaningless
                clientOrganizationId={user?.organizationId} // Field is meaningless
                organizationId={user?.organizationId}
                type={AFFILIATED}
              />
            )}

            {tab === 2 && <VendorOfAssociation canEdit={data.canEdit} communityName={data.name} />}
            {tab === 1 && isEmpty(data.affiliationAffiliated) && (
              <VendorOfAssociation canEdit={data.canEdit} communityName={data.name} />
            )}
          </div>
        </div>

        {showQrModel && (
          <QrCode
            communityId={communityId}
            showQrModalFc={showQrModalFc}
            isOpen={showQrModel}
            QrTitle={data.name}
            id={"qrcode"}
            format={"PNG"}
          />
        )}
      </>
    );
  }

  useEffect(() => actions.clear, [actions]);

  return (
    <DocumentTitle title="Simply Connect | Community Details">
      <div className={cn("CommunityDetails", className)}>
        <UpdateSideBarAction />
        <LoadCommunityAction
          isMultiple
          params={{
            communityId,
            shouldReload,
            shouldRefresh,
            organizationId,
            isMarketplaceDataIncluded: true,
          }}
          shouldPerform={(prevParams) =>
            !isFetching &&
            allAreInteger(organizationId, communityId) &&
            ((shouldReload && !prevParams.shouldReload) ||
              (shouldRefresh && !prevParams.shouldRefresh) ||
              prevParams.communityId !== communityId ||
              fetchCount === 0)
          }
          onPerformed={() => {
            shouldRefresh && setShouldRefresh(false);
            actions.logo.download(organizationId, communityId);
          }}
        />
        {content}
        {isEditorOpen && (
          <CommunityEditor
            isOpen={isEditorOpen}
            communityId={communityId}
            organizationId={organizationId}
            onClose={onCloseEditor}
            onSaveSuccess={onSaveSuccess}
          />
        )}

        {isSaveSuccessDialogOpen && (
          <SuccessDialog
            isOpen={isSaveSuccessDialogOpen}
            title="Community details have been updated."
            className={className}
            buttons={[
              {
                text: "OK",
                color: "success",
                onClick: () => onCloseEditor(),
              },
            ]}
          />
        )}

        {isCancelEditConfirmDialogOpen && (
          <ConfirmDialog
            isOpen
            icon={Warning}
            confirmBtnText="OK"
            title="The updates will not be saved"
            onConfirm={onCloseEditor}
            onCancel={() => toggleCancelEditConfirmDialog(false)}
          />
        )}

        {error && !isIgnoredError(getError()) && <ErrorViewer isOpen error={getError()} onClose={resetError} />}
      </div>
    </DocumentTitle>
  );
}

export default compose(withRouter, connect(mapStateToProps, mapDispatchToProps))(CommunityDetails);
