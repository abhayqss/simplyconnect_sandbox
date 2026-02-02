import React, { Component } from "react";

import cn from "classnames";

import { compose, isNumber, map } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { Link, Redirect, withRouter } from "react-router-dom";

import DocumentTitle from "react-document-title";

import { Badge, Button, UncontrolledTooltip as Tooltip } from "reactstrap";

import { Action, Breadcrumbs, ErrorViewer, Loader, Table } from "components";

import { Detail } from "components/business/common";

import { AffiliatedCommunitiesDetail, PrimaryCommunitiesDetail } from "components/business/Admin/Organizations";

import { ConfirmDialog, SuccessDialog } from "components/dialogs";

import Actions from "components/Table/Actions/Actions";

import { saveAs } from "file-saver";

import {
  LoadAddCategoriesAction,
  LoadOrganizationCategoriesAction,
  LoadViewCategoriesAction,
  UpdateSideBarAction,
} from "actions/admin";

import * as sideBarActions from "redux/sidebar/sideBarActions";
import * as communityListActions from "redux/community/list/communityListActions";
import * as communityFormActions from "redux/community/form/communityFormActions";
import * as communityCountActions from "redux/community/count/communityCountActions";
import * as canAddCommunityActions from "redux/community/can/add/canAddCommunityActions";
import * as organizationLogoActions from "redux/organization/logo/organizationLogoActions";
import * as organizationFormActions from "redux/organization/form/organizationFormActions";
import * as organizationDetailsActions from "redux/organization/details/organizationDetailsActions";

import { PAGINATION, SERVER_ERROR_CODES } from "lib/Constants";

import { path } from "lib/utils/ContextUtils";

import { allAreInteger, DateUtils, isEmpty, isNotEmpty, PhoneNumberUtils as PNU, promise } from "lib/utils/Utils";

import { ReactComponent as Indicatior } from "images/dot.svg";
import { ReactComponent as Warning } from "images/alert-yellow.svg";
import TopImg from "images/chevron-top-3.svg";
import BottomImg from "images/chevron-bottom-3.svg";

import OrganizationEditor from "../OrganizationEditor/OrganizationEditor";
import CommunityEditor from "../Communities/CommunityEditor/CommunityEditor";

import "./OrganizationDetails.scss";
import QrCode from "../QrCode/QrCode";
import { featAllOrgCommunitiesQrCode } from "../../../../redux/QrCode/QrcodeActions";

const { MAX_SIZE, FIRST_PAGE } = PAGINATION;

const { format, formats } = DateUtils;

const ICON_SIZE = 36;
const DATE_FORMAT = formats.americanMediumDate;

function isIgnoredError(e = {}) {
  return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE;
}

function mapStateToProps(state) {
  return {
    data: state.organization.details.data,
    error: state.organization.details.error,
    isFetching: state.organization.details.isFetching,
    shouldReload: state.organization.details.shouldReload,

    community: state.community,
    category: state.organization.category,
    orgCommunitiesQrCode: state.organization.details.orgCommunitiesQrCode,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      form: bindActionCreators(organizationFormActions, dispatch),
      logo: bindActionCreators(organizationLogoActions, dispatch),
      details: bindActionCreators(organizationDetailsActions, dispatch),

      community: {
        list: bindActionCreators(communityListActions, dispatch),
        form: bindActionCreators(communityFormActions, dispatch),
        count: bindActionCreators(communityCountActions, dispatch),
        can: {
          add: bindActionCreators(canAddCommunityActions, dispatch),
        },
      },

      sidebar: bindActionCreators(sideBarActions, dispatch),
    },
  };
}

class OrganizationDetails extends Component {
  state = {
    shouldOpenCommunityDetails: false,

    isEditorOpen: false,
    isSaveSuccessDialogOpen: false,
    isCancelEditConfirmDialogOpen: false,

    selectedCommunity: null,
    isCommunityEditorOpen: false,
    isSaveCommunitySuccessDialogOpen: false,
    isCancelEditCommunityConfirmDialogOpen: false,

    showQrList: false,
    showQrModal: false,
    isDownload: false,
  };

  componentDidMount() {
    this.refresh().then(() => {
      this.loadLogo();
    });

    this.canAddCommunity();

    this.loadCommunityCount();
    this.refreshCommunityList();

    const { match } = this.props;

    const { orgId } = match.params;

    const { details } = this.props.actions;

    details.featAllOrgCommunitiesQrCode(orgId);
  }

  componentDidUpdate() {
    const { community, shouldReload } = this.props;

    if (shouldReload) {
      this.refresh().then(() => {
        this.loadLogo();
      });
    }

    if (community.list.shouldReload) {
      this.refreshCommunityList();
    }
  }

  onRefresh = (page) => {
    this.refreshCommunityList(page);
  };

  onEdit = () => {
    this.setState({ isEditorOpen: true });
  };

  onConfigureCategories = () => {
    const { match, history } = this.props;

    const { orgId } = match.params;

    history.push(path(`/admin/organizations/${orgId}/categories`));
  };

  onEditCommunity = (community) => {
    this.setState({
      selectedCommunity: community,
      isCommunityEditorOpen: true,
    });
  };

  onAddCommunity = () => {
    this.setState({ isCommunityEditorOpen: true });
  };

  onCancelConfirmDialog = () => {
    this.setState({
      isCancelEditConfirmDialogOpen: false,
    });
  };

  onCommunityCancelConfirmDialog = () => {
    this.setState({
      isCancelEditCommunityConfirmDialogOpen: false,
    });
  };

  onCloseEditor = (shouldConfirm = false) => {
    this.setState({
      isEditorOpen: shouldConfirm,
      isSaveSuccessDialogOpen: false,
      isCancelEditConfirmDialogOpen: shouldConfirm,
    });
  };

  onCloseCommunityEditor = (shouldConfirm = false) => {
    this.setState((s) => ({
      isCommunityEditorOpen: shouldConfirm,
      isSaveCommunitySuccessDialogOpen: false,
      isCancelEditCommunityConfirmDialogOpen: shouldConfirm,
      selectedCommunity: shouldConfirm ? s.selectedCommunity : null,
    }));
  };

  onCloseCommunitySuccessDialog = () => {
    this.setState({
      isSaveCommunitySuccessDialogOpen: false,
    });
  };

  onCommunityDetails = () => {
    this.setState({
      shouldOpenCommunityDetails: true,
    });
  };

  onSaveSuccess = () => {
    this.setState({
      isEditorOpen: false,
      isSaveSuccessDialogOpen: true,
    });
  };

  onSaveCommunitySuccess = (id, isNew) => {
    this.refreshCommunityList();

    this.setState((s) => ({
      isCommunityEditorOpen: false,
      isSaveCommunitySuccessDialogOpen: true,
      selectedCommunity: isNew ? { id, isNew } : s.selectedCommunity,
    }));
  };

  onSort = (field, order) => {
    this.props.actions.community.list.sort(field, order);
  };

  onResetError = () => {
    const { details, community } = this.props.actions;

    details.clearError();
    community.form.clearError();
  };

  loadLogo() {
    const { match, actions } = this.props;

    actions.logo.download(+match.params.orgId);
  }

  canAddCommunity() {
    const { match, actions } = this.props;

    return actions.community.can.add.load(+match.params.orgId);
  }

  refresh() {
    return this.update(true);
  }

  refreshCommunityList(page) {
    this.updateCommunityList(true, page || FIRST_PAGE);
  }

  update(isReload) {
    if (isReload) {
      const { match, actions } = this.props;

      return actions.details.load(+match.params.orgId, true);
    }

    return promise();
  }

  updateCommunityList(isReload, page) {
    const {
      match,
      actions,
      community: { list },
    } = this.props;

    const { pagination, sorting, filter } = list.dataSource;

    const { orgId } = match.params;

    if (isReload) {
      const { field, order } = sorting;
      const { page: p, size } = pagination;

      actions.community.list.load({
        orgId,
        size,
        page: page || p,
        ...filter.toJS(),
        sort: `${field},${order}`,
      });
    }
  }

  isLoading() {
    const { isFetching, shouldReload } = this.props;

    return isFetching || shouldReload;
  }

  clear() {
    this.props.actions.details.clear();
  }

  clearCommunityList() {
    this.props.actions.community.list.clear();
  }

  getError() {
    const { error, community } = this.props;

    return error || community.form.error;
  }

  loadCommunityCount() {
    const { match, actions } = this.props;

    actions.community.count.load(+match.params.orgId);
  }

  showQrListFc = () => {
    this.setState((prevState) => ({
      showQrList: !prevState.showQrList,
    }));
  };

  showQrModalFc = () => {
    this.setState((prevState) => ({
      showQrModal: !prevState.showQrModal,
      showQrList: false,
    }));
  };

  downloadQrCode = async () => {
    this.setState({
      isDownload: true,
    });

    const { orgCommunitiesQrCode } = this.props;

    await orgCommunitiesQrCode.data.forEach((item) => {
      const start = "data:image/png;base64,";
      this.downloadQRCode(start + item.qrcodeString, item.name);
    });

    this.setState((prevState) => ({
      showQrList: false,
    }));
  };

  downloadQRCode = (base64String, name) => {
    const byteCharacters = atob(base64String.split(",")[1]);
    const byteNumbers = new Array(byteCharacters.length);

    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }

    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: "image/png" });

    saveAs(blob, `${name}.png`);
  };

  render() {
    const {
      data,
      match,
      className,
      category,
      community: {
        can,
        count,
        list: { isFetching, dataSource: ds },
      },
    } = this.props;

    const { showQrList, showQrModal } = this.state;

    const organizationId = +match.params.orgId;

    const canViewCategories = category.can.view.value;
    const canManageCategories = category.can.add.value;

    const {
      shouldOpenCommunityDetails,

      isEditorOpen,
      isSaveSuccessDialogOpen,
      isCancelEditConfirmDialogOpen,

      selectedCommunity,
      isCommunityEditorOpen,
      isSaveCommunitySuccessDialogOpen,
      isCancelEditCommunityConfirmDialogOpen,
    } = this.state;

    let content = null;

    if (shouldOpenCommunityDetails) {
      return <Redirect push to={path(`admin/organizations/${organizationId}/communities/${selectedCommunity.id}`)} />;
    }

    if (this.isLoading()) {
      content = <Loader />;
    } else if (isEmpty(data)) {
      content = <h4>No Data</h4>;
    } else {
      content = (
        <>
          <Breadcrumbs
            items={[
              { title: "Organizations", href: "/admin/organizations", isEnabled: true },
              { title: "Organization details", href: `/admin/organizations/${organizationId}`, isActive: true },
            ]}
          />
          <div className="OrganizationDetails-Header">
            <div className="OrganizationDetails-Title">
              <div className="OrganizationDetails-TitleText" title={data.name}>
                {data.name}
              </div>
            </div>
            <div className="CommunityDetails-ControlPanel">
              {data.canEdit && (
                <Button color="success" outline className="OrganizationDetails-QRButton" onClick={this.showQrListFc}>
                  QR Code
                  <img src={showQrList ? BottomImg : TopImg} alt="" />
                  {showQrList && (
                    <div className="qrList" onClick={(e) => e.stopPropagation()}>
                      <div onClick={this.showQrModalFc}>View Org. QR-Code</div>
                      <div onClick={this.downloadQrCode}>Download All QR-Code</div>
                    </div>
                  )}
                </Button>
              )}
            </div>
            <div className="CommunityDetails-ControlPanel">
              {data.canEdit && (
                <Button color="success" className="OrganizationDetails-EditButton" onClick={this.onEdit}>
                  Edit Details
                </Button>
              )}
            </div>
          </div>
          <div className="OrganizationDetails-Body">
            <div className="margin-bottom-65">
              <Detail
                className="OrganizationDetail"
                titleClassName="OrganizationDetail-Title"
                valueClassName="OrganizationDetail-Value"
                title="Organization OID"
              >
                {data.oid}
              </Detail>
              <Detail
                className="OrganizationDetail"
                titleClassName="OrganizationDetail-Title"
                valueClassName="OrganizationDetail-Value"
                title="Company Code"
              >
                {data.companyId}
              </Detail>
              <Detail
                className="OrganizationDetail"
                titleClassName="OrganizationDetail-Title"
                valueClassName="OrganizationDetail-Value"
                title="Email"
              >
                {data.email}
              </Detail>
              <Detail
                className="OrganizationDetail"
                titleClassName="OrganizationDetail-Title"
                valueClassName="OrganizationDetail-Value"
                title="Phone number"
              >
                {PNU.formatPhoneNumber(data.phone)}
              </Detail>
              <Detail
                className="OrganizationDetail"
                titleClassName="OrganizationDetail-Title"
                valueClassName="OrganizationDetail-Value"
                title="Address"
              >
                {data.displayAddress}
              </Detail>
              {data.logoDataUrl && (
                <Detail
                  className="OrganizationDetail"
                  titleClassName="OrganizationDetail-Title"
                  valueClassName="OrganizationDetail-Value"
                  title="Logo"
                >
                  <img className="OrganizationDetail-Logo" src={data.logoDataUrl} alt="" />
                </Detail>
              )}
              {isNotEmpty(data.affiliationAffiliated) && (
                <AffiliatedCommunitiesDetail
                  title="Current organization is a primary one for:"
                  organizationId={organizationId}
                  data={data.affiliationAffiliated}
                  className="OrganizationDetail"
                  titleClassName="OrganizationDetail-Title"
                  valueClassName="OrganizationDetail-Value flex-column"
                />
              )}
              {isNotEmpty(data.affiliationPrimary) && (
                <PrimaryCommunitiesDetail
                  title="Current organization is added as affiliated with:"
                  organizationId={organizationId}
                  data={data.affiliationPrimary}
                  className="OrganizationDetail"
                  titleClassName="OrganizationDetail-Title"
                  valueClassName="OrganizationDetail-Value flex-column"
                />
              )}
            </div>
            {canViewCategories && (
              <div className="OrganizationDetails-Section">
                <div className="OrganizationDetails-SectionHeader">
                  <div className="OrganizationDetails-SectionTitle">
                    {isNotEmpty(category.list.dataSource.data)
                      ? "Active Document  Categories"
                      : "No Active Document Categories"}
                  </div>
                  {canManageCategories && (
                    <Button color="success" className="OrganizationDetails-Button" onClick={this.onConfigureCategories}>
                      Manage Document Categories
                    </Button>
                  )}
                </div>
                {map(category.list.dataSource.data, (o) => (
                  <div
                    key={o.id}
                    className="OrganizationDetails-Category"
                    style={{ borderColor: o.color || "#000000" }}
                  >
                    <Indicatior
                      style={{ fill: o.color || "#000000" }}
                      className="OrganizationDetails-CategoryIndicator"
                    />
                    <div className="OrganizationDetails-CategoryName">{o.name}</div>
                  </div>
                ))}
              </div>
            )}
            <Table
              hasHover
              hasOptions
              hasPagination
              keyField="id"
              title="Communities"
              isLoading={isFetching}
              className="CommunityList"
              containerClass="CommunityListContainer"
              data={ds.data}
              pagination={ds.pagination}
              columns={[
                {
                  dataField: "name",
                  text: "Name",
                  sort: true,
                  onSort: this.onSort,
                  formatter: (v, row, index, formatExtraData, isMobile) => {
                    return row.canView ? (
                      <>
                        <div className="d-flex flex-row overflow-hidden">
                          <Link
                            id={`${isMobile ? "m-" : ""}community-${row.id}`}
                            to={{
                              pathname: path(`/admin/organizations/${organizationId}/communities/${row.id}`),
                              state: { canEditCommunity: row.canEdit }, // 使用state传递数据
                            }}
                            className="CommunityList-CommunityName cursor-pointer"
                          >
                            {v}
                          </Link>
                        </div>
                        <Tooltip
                          placement="top"
                          target={`${isMobile ? "m-" : ""}community-${row.id}`}
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
                          View community details
                        </Tooltip>
                      </>
                    ) : (
                      <div title={v} id={"comm" + row.id} className="CommunityList-CommunityName">
                        {v}
                      </div>
                    );
                  },
                },
                {
                  dataField: "oid",
                  text: "Community OID",
                  sort: true,
                  onSort: this.onSort,
                },
                {
                  dataField: "createdAutomatically",
                  text: "Created Automatically",
                  sort: true,
                  onSort: this.onSort,
                  formatter: (v) => (v ? "Yes" : "No"),
                },
                {
                  dataField: "lastModified",
                  text: "Modified On",
                  sort: true,
                  onSort: this.onSort,
                  formatter: (v) => v && format(v, DATE_FORMAT),
                },
                {
                  dataField: "@actions",
                  text: "",
                  headerStyle: {
                    width: "60px",
                  },
                  formatter: (v, row) => {
                    return (
                      <Actions
                        data={row}
                        hasEditAction={row.canEdit}
                        iconSize={ICON_SIZE}
                        className="CommunityList-Actions"
                        editHintMessage="Edit community details"
                        onEdit={this.onEditCommunity}
                      />
                    );
                  },
                },
              ]}
              columnsMobile={["name", "oid"]}
              defaultSorted={[
                {
                  dataField: "name",
                  order: "asc",
                },
              ]}
              renderCaption={(title) => {
                return (
                  <div className="CommunityList-Caption">
                    <div className="CommunityList-CaptionHeader">
                      <div className="CommunityList-Title">
                        <span className="CommunityList-TitleText">{title}</span>
                        {!!count.value && (
                          <Badge color="info" className="Badge Badge_place_top-right">
                            {count.value}
                          </Badge>
                        )}
                      </div>
                      <div className="CommunityList-ControlPanel">
                        {can.add.value && (
                          <Button color="success" onClick={this.onAddCommunity}>
                            Add Community
                          </Button>
                        )}
                      </div>
                    </div>
                  </div>
                );
              }}
              onRefresh={this.onRefresh}
            />
          </div>
        </>
      );
    }

    const error = this.getError();

    return (
      <DocumentTitle
        title={
          isCommunityEditorOpen
            ? isNumber(selectedCommunity && selectedCommunity.id)
              ? "Simply Connect | Admin | Organizations | Organization Details | Edit Community Details"
              : "Simply Connect | Admin | Organizations | Organization Details | Create Community"
            : "Simply Connect | Admin | Organizations | Organization Details"
        }
      >
        <div className={cn("OrganizationDetails", className)}>
          <UpdateSideBarAction />
          <LoadAddCategoriesAction
            isMultiple
            params={{ organizationId }}
            shouldPerform={(prevParams) => organizationId !== prevParams.organizationId}
          />
          <LoadViewCategoriesAction
            isMultiple
            params={{ organizationId }}
            shouldPerform={(prevParams) => organizationId !== prevParams.organizationId}
          />
          <LoadOrganizationCategoriesAction
            isMultiple
            params={{ organizationId, size: MAX_SIZE }}
            shouldPerform={(prevParams) => organizationId !== prevParams.organizationId}
          />
          <Action
            isMultiple
            params={{ organizationId }}
            shouldPerform={(prevParams) =>
              allAreInteger(organizationId, prevParams.organizationId) && organizationId !== prevParams.organizationId
            }
            action={() => {
              this.refresh().then(() => {
                this.loadLogo();
              });

              this.canAddCommunity();

              this.loadCommunityCount();
              this.refreshCommunityList();
            }}
          />
          {content}
          {showQrModal && (
            <QrCode
              organizationId={organizationId}
              showQrModalFc={this.showQrModalFc}
              isOpen={showQrModal}
              QrTitle={data.name}
              id={"qrcode"}
              format={"PNG"}
            />
          )}
          {isEditorOpen && (
            <OrganizationEditor
              isOpen
              organizationId={organizationId}
              onClose={this.onCloseEditor}
              onSaveSuccess={this.onSaveSuccess}
            />
          )}
          {isSaveSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title="Organization details have been updated."
              className={className}
              buttons={[
                {
                  text: "OK",
                  onClick: () => {
                    this.onCloseEditor();
                  },
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
              onConfirm={this.onCloseEditor}
              onCancel={this.onCancelConfirmDialog}
            />
          )}
          {isCommunityEditorOpen && (
            <CommunityEditor
              isOpen
              communityId={selectedCommunity && selectedCommunity.id}
              organizationId={organizationId}
              onClose={this.onCloseCommunityEditor}
              onSaveSuccess={this.onSaveCommunitySuccess}
            />
          )}
          {isSaveCommunitySuccessDialogOpen && (
            <SuccessDialog
              isOpen
              type="success"
              buttons={
                selectedCommunity.isNew
                  ? [
                      {
                        text: "Close",
                        outline: true,
                        className: "margin-left-80",
                        onClick: () => {
                          this.onCloseCommunityEditor();
                        },
                      },
                      {
                        text: "View Details",
                        className: "margin-right-80",
                        onClick: this.onCommunityDetails,
                      },
                    ]
                  : [
                      {
                        text: "Ok",
                        onClick: this.onCommunityDetails,
                      },
                    ]
              }
              title={`Community ${selectedCommunity.isNew ? "has been created" : "details have been updated"}.`}
              onClose={this.onCloseCommunitySuccessDialog}
              onViewDetail={this.onCommunityDetails}
            />
          )}
          {isCancelEditCommunityConfirmDialogOpen && (
            <ConfirmDialog
              isOpen
              icon={Warning}
              confirmBtnText="OK"
              title="The updates will not be saved"
              onConfirm={this.onCloseCommunityEditor}
              onCancel={this.onCommunityCancelConfirmDialog}
            />
          )}
          {error && !isIgnoredError(error) && <ErrorViewer isOpen error={error} onClose={this.onResetError} />}
        </div>
      </DocumentTitle>
    );
  }
}

export default compose(withRouter, connect(mapStateToProps, mapDispatchToProps))(OrganizationDetails);
