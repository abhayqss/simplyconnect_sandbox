import React, { Component } from "react";

import cn from "classnames";

import { chain, compose, filter, find, map, omit, without } from "underscore";

import memoize from "memoize-one";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";
import { Link, Redirect, withRouter } from "react-router-dom";

import DocumentTitle from "react-document-title";

import { Badge, Button, Collapse, UncontrolledTooltip as Tooltip } from "reactstrap";

import { ErrorViewer, IconButton, Table } from "components";

import { Dialog, SuccessDialog, WarningDialog } from "components/dialogs";

import Actions from "components/Table/Actions/Actions";

import Avatar from "containers/Avatar/Avatar";
import { RequestSignatureEditor } from "containers/Documents";

import { ReactComponent as HL7WarningIcon } from "images/warning-hl7.svg";

import LoadCanAddClientAction from "actions/clients/LoadCanAddClientAction";
import LoadCanAddSignatureAction from "actions/clients/LoadCanAddSignatureAction";

import * as errorActions from "redux/error/errorActions";
import * as sideBarActions from "redux/sidebar/sideBarActions";
import clientListActions from "redux/client/list/clientListActions";
import * as clientFormActions from "redux/client/form/clientFormActions";
import * as clientCountActions from "redux/client/count/clientCountActions";
import * as clientDetailsActions from "redux/client/details/clientDetailsActions";

import { CLIENT_STATUSES, PAGINATION, RESPONSIVE_BREAKPOINTS, SERVER_ERROR_CODES, SYSTEM_ROLES } from "lib/Constants";

import { DateUtils as DU, isEmpty, isInteger } from "lib/utils/Utils";

import { first, isUnary } from "lib/utils/ArrayUtils";

import { path } from "lib/utils/ContextUtils";

import { TestDynaFormEditor } from "containers/common/editors";

import { ReactComponent as Asset } from "images/asset.svg";
import { ReactComponent as Delete } from "images/delete.svg";
import { ReactComponent as Filter } from "images/filters.svg";

import ClientFilter from "./ClientFilter/ClientFilter";
import ClientEditor from "./ClientEditor/ClientEditor";
import ClientMatches from "./ClientMatches/ClientMatches";
import ClientPrimaryFilter from "./ClientPrimaryFilter/ClientPrimaryFilter";

import { PROFESSIONAL_SYSTEM_ROLES } from "../../ClientRecords/Constants";

import "./Clients.scss";
import service from "../../../services/DirectoryService";
import services from "services/CareTeamMemberService";
import { selected } from "@trendmicro/react-sidenav";

const { FIRST_PAGE } = PAGINATION;

const { HOME_CARE_ASSISTANT } = SYSTEM_ROLES;

const { TABLET_LANDSCAPE, MOBILE_LANDSCAPE } = RESPONSIVE_BREAKPOINTS;

const { format, formats } = DU;

const ACTION_ICON_SIZE = 36;

const DATE_FORMAT = formats.americanMediumDate;

const MODES = [
  { text: "All Clients", value: 0, isSelected: true },
  { text: "My Caseloads", value: 1 },
  { text: "Unassigned", value: 2 },
];

const ROLES_WITH_DISABLED_FILTER = [HOME_CARE_ASSISTANT];

const ROLE_SPECIFIC_COLUMN_NAMES = {
  [HOME_CARE_ASSISTANT]: ["fullName", "gender", "unit", "community.name", "createdDate"],
};

const { PENDING, DECLINED } = CLIENT_STATUSES;

function ClientLink({ id, name, hasComma, isDisabled }) {
  const title = name + (hasComma ? "," : "");

  return isDisabled ? (
    <span className="ClientList-Link_Disabled">{title}</span>
  ) : (
    <Link to={path(`/clients/${id}`)} className="ClientList-Link">
      {title}
    </Link>
  );
}

function isIgnoredError(e = {}) {
  return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE;
}

function mapStateToProps(state) {
  return {
    auth: state.auth,
    sidebar: state.sidebar,

    error: state.client.list.error,
    isFetching: state.client.list.isFetching,
    fetchCount: state.client.list.fetchCount,
    dataSource: state.client.list.dataSource,
    shouldReload: state.client.list.shouldReload,

    canAdd: state.client.can.add.value,

    isFetchingCanAddSignature: state.client.can.addSignature.isFetching,
    canAddSignature: state.client.can.addSignature.value,

    count: state.client.count,

    form: state.client.form,

    community: state.client.community,

    directory: state.directory,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(clientListActions, dispatch),

      error: bindActionCreators(errorActions, dispatch),

      form: bindActionCreators(clientFormActions, dispatch),

      count: bindActionCreators(clientCountActions, dispatch),

      sidebar: bindActionCreators(sideBarActions, dispatch),

      details: bindActionCreators(clientDetailsActions, dispatch),
    },
  };
}

class Clients extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selected: null,
      selectedOption: 0,
      selectedClients: [],

      isFilterOpen: true,
      isEditorOpen: false,
      isClientMatchesOpen: false,
      isDynaFormEditorOpen: false,
      isCaseloadEditorOpen: false,
      isRequestSignatureOpen: false,
      isHL7WarningDialogOpen: false,
      isIntroductionDialogOpen: false,
      isSignatureRequestSuccessDialogOpen: false,
      isCannotViewClientDetailsDialogOpen: false,

      shouldRedirectToCareTeam: false,

      waringDialog: false,
      canShowReminder: false,
      isRefresh: false,
    };

    this.getListData = memoize(this.getListData);
  }

  get actions() {
    return this.props.actions;
  }

  get authUser() {
    return this.props.auth.login.user.data;
  }

  get error() {
    return this.props.error;
  }

  get isTabletView() {
    const { width } = document.body.getBoundingClientRect();
    //  && width > MOBILE_LANDSCAPE;
    return width < TABLET_LANDSCAPE;
  }

  get communityIdsBySelectedUsers() {
    return chain(this.state.selectedClients)
      .sortBy((o) => o.community)
      .pluck("communityId")
      .uniq()
      .value();
  }

  get isProfessionalRole() {
    return PROFESSIONAL_SYSTEM_ROLES.includes(this.authUser?.roleName);
  }

  componentDidMount() {
    this.updateSideBar();
    this.handleLocationState();
  }

  componentDidUpdate(prevProps) {
    if (
      this.props.shouldReload ||
      (prevProps.dataSource?.filter?.communityIds !== this.props.dataSource?.filter?.communityIds &&
        this.props.dataSource?.filter?.communityIds)
    ) {
      this.refresh();
    }
    if (
      this.props.dataSource?.filter?.organizationId &&
      prevProps.dataSource?.filter?.organizationId !== this.props.dataSource?.filter?.organizationId
    ) {
      localStorage.setItem("triggerCurrentOrgId", this.props.dataSource?.filter?.organizationId);
      this.actions.count.load({
        organizationId: this.props.dataSource?.filter?.organizationId,
        // communityIds: this.props.dataSource?.filter?.communityIds,
        canRequestSignature: true,
      });
    }
  }

  componentWillUnmount() {
    this.actions.clear();
  }

  onRefresh = (page) => {
    this.refresh(page);
  };

  onAdd = () => {
    this.setState({
      selected: null,
      isEditorOpen: true,
    });
  };

  onEdit = (client) => {
    this.setState({
      selected: client,
      isEditorOpen: !client.isHL7,
      isHL7WarningDialogOpen: client.isHL7,
    });

    this.fetchData(client?.id);
  };

  onCloseEditor = () => {
    this.setState({
      isEditorOpen: false,
    });
  };

  onRequestSignature = () => {
    this.setState({
      isRequestSignatureOpen: true,
    });
  };

  onCloseRequestSignature = () => {
    this.setState({
      isRequestSignatureOpen: false,
    });
  };

  onSaveSuccess = (isNew) => {
    this.refresh(isNew ? FIRST_PAGE : this.props.dataSource.pagination.page);

    this.setState({
      selected: null,
      isEditorOpen: false,
    });
  };

  onCloseClientMatches = () => {
    this.setState({
      selected: null,
      isClientMatchesOpen: false,
    });
  };

  onClickFilter = () => {
    this.setState((s) => ({ isFilterOpen: !s.isFilterOpen }));
  };

  onClearFilter = () => {
    this.actions.clearFilter();
  };

  onApplyFilter = () => {
    this.refresh();
  };

  onSort = (field, order) => {
    this.actions.sort(field, order);
  };

  onChangeMode = (mode) => {
    let options = map(MODES, (o) => (o.value === mode ? { ...o, isSelected: true } : { ...o, isSelected: false }));

    this.setState({
      options,
      selectedOption: mode,
    });
  };

  onConfigureClient = (client) => {
    alert("Coming soon!");
  };

  onResetError = () => {
    this.actions.clearError();
    this.actions.form.clearError();
  };

  onSelectClient = (client, isSelected) => {
    const { selectedClients } = this.state;

    this.setState({
      selectedClients: isSelected ? [...selectedClients, client] : filter(selectedClients, (c) => c.id !== client.id),
    });
  };

  onSelectAllClients = (isSelected, clients) => {
    const { selectedClients } = this.state;
    this.setState({
      selectedClients: isSelected
        ? [...selectedClients, ...filter(clients, (client) => !find(selectedClients, (c) => c.id === client.id))]
        : filter(selectedClients, (c) => !find(clients, (client) => client.id === c.id)),
    });
  };

  onUploadSuccess = () => {
    this.setState({
      isSignatureRequestSuccessDialogOpen: true,
    });
  };

  onCloseSignatureRequestSuccessDialog = () => {
    this.setState({
      isSignatureRequestSuccessDialogOpen: false,
    });
  };

  onClickClientName = ({ id, canView }) => {
    const { history } = this.props;

    if (canView) return history.push(path(`/clients/${id}`));

    this.toggleCannotViewClientDetailsDialog();
  };

  toggleCannotViewClientDetailsDialog = () => {
    this.setState({
      isCannotViewClientDetailsDialogOpen: !this.state.isCannotViewClientDetailsDialogOpen,
    });
  };

  handleLocationState() {
    const { state } = this.props.location;

    if (state) {
      if (state.alertMessage) {
        this.actions.error.change(new Error(state.alertMessage));
      }

      if (state.isIntroductionNeed) {
        this.setState({ isIntroductionDialogOpen: true });
      }

      if (state.isAddingOrAccessRecordsInstructionNeed) {
        this.setState({ isAddingOrAccessRecordsInstructionDialogOpen: true });
      }

      if (state.isCareTeamManagementInstructionNeed) {
        this.setState({ isCareTeamManagementInstructionDialogOpen: true });
      }

      if (state.isDocumentManagementInstructionNeed) {
        this.setState({ isDocumentManagementInstructionDialogOpen: true });
      }

      if (state.isAddingOrAccessRecordsAndDocumentManagementInstructionNeed) {
        this.setState({ isAddingOrAccessRecordsAndDocumentManagementInstructionDialogOpen: true });
      }

      if (state.isMedicationsInstructionNeed) {
        this.setState({ isMedicationsInstructionDialogOpen: true });
      }

      if (state.isAddingOrAccessRecordsAndMedicationsInstructionNeed) {
        this.setState({ isAddingOrAccessRecordsAndMedicationsInstructionDialogOpen: true });
      }

      if (state.isRidesInstructionNeed) {
        this.setState({ isRidesInstructionDialogOpen: true });
      }

      if (state.isAddingOrAccessRecordsAndRidesInstructionNeed) {
        this.setState({ isAddingOrAccessRecordsAndRidesInstructionDialogOpen: true });
      }

      this.props.history.replace(omit(this.props.location, "state"));
    }
  }

  updateSideBar() {
    this.actions.sidebar.update({
      isHidden: true,
    });
  }

  update(isReload, page) {
    const { isFetching, shouldReload, dataSource: ds, community } = this.props;

    if (isReload || shouldReload || (!isFetching && isEmpty(ds.data))) {
      const { field, order } = ds.sorting;
      const { page: p, size } = ds.pagination;
      ds.filter.communityIds.length !== 0 &&
        this.actions.load({
          size,
          page: page || p,
          filter: ds.filter.toJS(),
          sort: `${field},${order}`,
        });
    }
  }

  refresh(page) {
    this.update(true, page || FIRST_PAGE);
  }

  clear() {
    this.actions.clear();
  }

  changeFilter(changes, shouldReload) {
    this.actions.changeFilter(changes, shouldReload);
  }

  loadDetails(clientId, shouldNotSave) {
    return this.actions.details.load(clientId, shouldNotSave);
  }

  isFilterEnabled() {
    return Boolean(this.authUser && !ROLES_WITH_DISABLED_FILTER.includes(this.authUser.roleName));
  }

  isAllowedListColumn(name) {
    const role = this.authUser?.roleName;
    const colNames = ROLE_SPECIFIC_COLUMN_NAMES[role];
    return colNames ? colNames.includes(name) : true;
  }

  shouldDisableRow(isActive, status, canView = true) {
    return !isActive || (this.isProfessionalRole && [PENDING, DECLINED].includes(status)) || !canView;
  }

  fetchData = (id) => {
    if (id) {
      services.showRemindFill(id).then((res) => {
        this.setState({
          canShowReminder: res,
        });
      });
    }
  };

  changeWaringDialog = () => {
    this.setState({
      waringDialog: !this.state.waringDialog,
    });
  };

  setRemindShow(clientId) {
    services.neverShowRemindFill(clientId).then((res) => {
      this.setState({
        canShowReminder: res,
      });
    });
  }

  render() {
    const {
      count,
      canAdd,
      className,
      isFetching,
      dataSource: ds,
      canAddSignature,
      isFetchingCanAddSignature,
    } = this.props;

    const {
      selected,
      isEditorOpen,
      isFilterOpen,
      selectedClients,
      isDynaFormEditorOpen,
      isHL7WarningDialogOpen,
      isRequestSignatureOpen,
      isIntroductionDialogOpen,
      isRidesInstructionDialogOpen,
      isMedicationsInstructionDialogOpen,
      isCannotViewClientDetailsDialogOpen,
      isSignatureRequestSuccessDialogOpen,
      isCareTeamManagementInstructionDialogOpen,
      isDocumentManagementInstructionDialogOpen,
      isAddingOrAccessRecordsInstructionDialogOpen,
      isAddingOrAccessRecordsAndRidesInstructionDialogOpen,
      isAddingOrAccessRecordsAndMedicationsInstructionDialogOpen,
      isAddingOrAccessRecordsAndDocumentManagementInstructionDialogOpen,

      shouldRedirectToCareTeam,
    } = this.state;

    if (shouldRedirectToCareTeam) {
      return <Redirect to={path(`clients/${selected.id}/care-team`)} />;
    }

    const { organizationId, communityIds } = ds.filter;

    let columns = [
      {
        dataField: "fullName",
        text: "Name",
        sort: true,
        headerAlign: "left",
        headerClasses: "ClientList-Header-ClientName",
        style: (cell, row) =>
          this.shouldDisableRow(row.isActive, row.recordStatus, row.canView) && {
            opacity: "0.5",
          },
        onSort: this.onSort,
        formatter: (v, row, index, formatExtraData, isMobile) => {
          return (
            <div className="d-flex align-items-center ClientList-FullName">
              <Avatar
                name={v}
                id={row.avatarId}
                className={cn(
                  "ClientList-ClientAvatar",
                  this.shouldDisableRow(row.isActive, row.recordStatus) && "ClientList-ClientAvatar_black-white",
                )}
                {...(this.shouldDisableRow(row.isActive, row.recordStatus) && { nameColor: "#e0e0e0" })}
              />
              {this.isProfessionalRole && [PENDING, DECLINED].includes(row.recordStatus) ? (
                <div className="flex-1">
                  <span
                    id={`${isMobile ? "m-" : ""}client-${row.id}`}
                    className={cn("ClientList-ClientName", "ClientList-ClientName_disabled")}
                  >
                    {v}
                  </span>
                  <Tooltip
                    placement="top"
                    target={`${isMobile ? "m-" : ""}client-${row.id}`}
                    modifiers={[
                      {
                        name: "offset",
                        options: { offset: [0, 6] },
                      },
                    ]}
                  >
                    You don't have permissions to see the client's record
                  </Tooltip>
                </div>
              ) : (
                <div className="flex-1">
                  <a
                    id={`${isMobile ? "m-" : ""}client-${row.id}`}
                    onClick={() => {
                      localStorage.setItem("triggerCurrentComId", JSON.stringify(row.communityId));
                      this.onClickClientName(row);
                    }}
                    className={cn("ClientList-ClientName", row.avatarDataUrl && "margin-left-10")}
                  >
                    {v}
                  </a>
                  {row.isActive && row.canView && (
                    <Tooltip
                      placement="top"
                      target={`${isMobile ? "m-" : ""}client-${row.id}`}
                      modifiers={[
                        {
                          name: "offset",
                          options: { offset: [0, 6] },
                        },
                      ]}
                    >
                      View client details
                    </Tooltip>
                  )}
                  {!row.canView && (
                    <Tooltip
                      placement="top"
                      target={`${isMobile ? "m-" : ""}client-${row.id}`}
                      modifiers={[
                        {
                          name: "offset",
                          options: { offset: [0, 6] },
                        },
                      ]}
                    >
                      You don't have permissions to see the client's record
                    </Tooltip>
                  )}
                  {row.isHL7 && (
                    <IconButton
                      size={24}
                      tipPlace="top"
                      Icon={HL7WarningIcon}
                      shouldHighLight={false}
                      name={`HL7WarningIcon-${row.id}`}
                      tipText="Client's record was created through Pharmacy sync, thus some data might be missing."
                    />
                  )}
                </div>
              )}
            </div>
          );
        },
      },
      {
        dataField: "gender",
        text: "Gender",
        style: (cell, row) =>
          this.shouldDisableRow(row.isActive, row.recordStatus, row.canView) && {
            opacity: "0.5",
          },
        // sort: true,
        // onSort: this.onSort,
      },
      {
        dataField: "birthDate",
        text: "Date of Birth",
        sort: true,
        align: "right",
        headerAlign: "right",
        isAllowed: this.isAllowedListColumn("birthDate"),
        style: (cell, row) =>
          this.shouldDisableRow(row.isActive, row.recordStatus, row.canView) && {
            opacity: "0.5",
          },
        onSort: this.onSort,
      },
      {
        dataField: "ssnLastFourDigits",
        text: "SSN",
        headerAlign: "right",
        align: "right",
        isAllowed: this.isAllowedListColumn("ssnLastFourDigits"),
        style: (cell, row) =>
          this.shouldDisableRow(row.isActive, row.recordStatus, row.canView) && {
            opacity: "0.5",
          },
        formatter: (v) => v && `###-##-${v}`,
      },
      {
        dataField: "unit",
        text: "Unit #",
        headerAlign: "right",
        align: "right",
        sort: true,
        onSort: this.onSort,
        style: (cell, row) =>
          this.shouldDisableRow(row.isActive, row.recordStatus, row.canView) && {
            opacity: "0.5",
          },
      },
      {
        dataField: "riskScore",
        text: "Risk score",
        align: "right",
        headerAlign: "right",
        sort: true,
        headerStyle: {
          width: "10%",
        },
        isAllowed: this.isAllowedListColumn("riskScore"),
        style: (cell, row) =>
          this.shouldDisableRow(row.isActive, row.recordStatus, row.canView) && {
            opacity: "0.5",
          },
        onSort: this.onSort,
      },
      {
        dataField: "community.name",
        text: "Community",
        // sort: true,
        // onSort: this.onSort,
        style: (cell, row) =>
          this.shouldDisableRow(row.isActive, row.recordStatus, row.canView) && {
            opacity: "0.5",
          },
        formatter: (v, row) => (
          <div className="ClientList-Community" title={row.community}>
            {row.community}
          </div>
        ),
      },
      {
        dataField: "createdDate",
        text: "Created",
        sort: true,
        align: "right",
        headerAlign: "right",
        style: (cell, row) =>
          this.shouldDisableRow(row.isActive, row.recordStatus, row.canView) && {
            opacity: "0.5",
          },
        onSort: this.onSort,
        formatter: (v) => v && format(v, DATE_FORMAT),
      },
      {
        dataField: "@actions",
        text: "",
        headerStyle: {
          width: "80px",
        },
        align: "right",
        style: (cell, row) =>
          this.shouldDisableRow(row.isActive, row.recordStatus, row.canView) && {
            opacity: "0.5",
          },
        formatter: (v, row) => {
          // if (this.authUser.hieAgreement) {
          return (
            <Actions
              data={row}
              hasEditAction={row.canEdit && row.isActive && !this.isProfessionalRole}
              iconSize={ACTION_ICON_SIZE}
              editHintMessage="Edit client details"
              onEdit={this.onEdit}
              className={cn("ClientList-Actions", {
                "ClientList-Actions__RightAligned": isEmpty(row.merged) && row.id && !this.isTabletView,
              })}
            />
          );
          // }
        },
      },
    ].filter((o) => !(o.isAllowed === false));

    if (this.isTabletView) {
      columns.push({
        dataField: "merged",
        text: "Matching Records",
        style: (cell, row) =>
          this.shouldDisableRow(row.isActive, row.recordStatus, row.canView) && {
            opacity: "0.5",
          },
        formatter: (v) =>
          v?.length ? (
            <div className="Detail-Clients">
              {v.map((client, i) => (
                <ClientLink
                  id={client.id}
                  name={client.fullName}
                  isDisabled={!client.isActive}
                  hasComma={v.length > 1 && i < v.length - 1}
                />
              ))}
            </div>
          ) : null,
      });
    }

    return (
      <DocumentTitle title="Simply Connect | Clients">
        <div className={cn("Clients", className)}>
          <LoadCanAddClientAction
            isMultiple
            params={{ organizationId }}
            shouldPerform={(prevParams) => isInteger(organizationId) && organizationId !== prevParams.organizationId}
          />
          <LoadCanAddSignatureAction
            isMultiple
            params={{ organizationId }}
            shouldPerform={(prevParams) => isInteger(organizationId) && organizationId !== prevParams.organizationId}
          />
          <ClientPrimaryFilter className="margin-bottom-30" />
          <div className="Clients-Header">
            <div className="Clients-HeaderItem">
              <div className="Clients-Title">
                <div className="Clients-TitleText">Clients</div>
                {ds.pagination.totalCount > 0 && (
                  <Badge color="info" className="Badge Badge_place_top-right">
                    {ds.pagination.totalCount}
                  </Badge>
                )}
              </div>
            </div>
            <div className="Clients-HeaderItem">
              <div className="Clients-Actions">
                {this.isFilterEnabled() && (
                  <Filter
                    className={cn(
                      "ClientFilter-Icon",
                      isFilterOpen ? "ClientFilter-Icon_rotated_90" : "ClientFilter-Icon_rotated_0",
                    )}
                    onClick={this.onClickFilter}
                  />
                )}
                {canAddSignature && (
                  <Button
                    outline
                    color="success"
                    className="margin-left-20"
                    disabled={!count.value} // || !this.authUser.hieAgreement
                    onClick={this.onRequestSignature}
                  >
                    Request Signature
                  </Button>
                )}
                {canAdd && (
                  <Button color="success" onClick={this.onAdd} className="margin-left-20">
                    Add New Client
                  </Button>
                )}
              </div>
            </div>
          </div>
          {this.isFilterEnabled() && (
            <Collapse isOpen={isFilterOpen}>
              <ClientFilter className="margin-bottom-50" isProfessionalRole={this.isProfessionalRole} />
            </Collapse>
          )}
          <Table
            hasHover
            hasOptions
            hasPagination
            keyField="id"
            title="Clients"
            hasCaption={false}
            noDataText={isFilterOpen ? "No results." : "No records found"}
            isLoading={isFetching || isFetchingCanAddSignature}
            className="ClientList"
            containerClass="ClientListContainer"
            data={ds.data}
            expandRow={
              !this.isTabletView
                ? {
                    onlyOneExpanding: true,
                    showExpandColumn: true,
                    expandColumnPosition: "right",
                    expandHeaderColumnRenderer: () => null,
                    parentClassName: "ClientList-ExpandableRow",
                    nonExpandable: without(
                      map(ds?.data, (o) => isEmpty(o?.merged) && o?.id),
                      false,
                    ),
                    expandColumnRenderer: ({ expanded, rowKey, expandable }) => {
                      if (expandable) {
                        return (
                          <>
                            {expanded ? (
                              <Delete
                                id={"match-toggle-" + rowKey}
                                style={{ stroke: "#ffffff" }}
                                className="ClientList-ShowMatchesActionItem"
                              />
                            ) : (
                              <Asset id={"match-toggle-" + rowKey} className="ClientList-ShowMatchesActionItem" />
                            )}
                            <Tooltip
                              target={"match-toggle-" + rowKey}
                              modifiers={[
                                {
                                  name: "offset",
                                  options: { offset: [0, 6] },
                                },
                              ]}
                            >
                              {expanded ? "Hide Matches" : "Show Matches"}
                            </Tooltip>
                          </>
                        );
                      }
                    },
                    renderer: (row) => <ClientMatches isOpen data={row.merged} onEdit={this.onEdit} />,
                  }
                : undefined
            }
            pagination={ds.pagination}
            columns={columns}
            columnsMobile={["fullName", "community.name"]}
            onRefresh={this.onRefresh}
          />

          <ClientEditor
            isOpen={isEditorOpen}
            clientId={selected && selected.id}
            organizationId={organizationId}
            communityId={isUnary(communityIds) ? first(communityIds) : null}
            onClose={this.onCloseEditor}
            onSaveSuccess={this.onSaveSuccess}
            changeWaringDialog={this.changeWaringDialog}
            canShowReminder={this.state.canShowReminder}
            canEdit={selected?.canEdit}
            clientFullName={selected?.fullName}
          />

          <RequestSignatureEditor
            isOpen={isRequestSignatureOpen}
            onClose={this.onCloseRequestSignature}
            organizationId={organizationId}
            communityIds={this.communityIdsBySelectedUsers}
            clients={this.state.selectedClients}
            isMultipleRequest
            onUploadSuccess={() => this.onUploadSuccess()}
          />

          {isSignatureRequestSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title="The signature requests/documents have been sent to clients"
              buttons={[{ text: "Close", onClick: () => this.onCloseSignatureRequestSuccessDialog() }]}
            />
          )}

          {isDynaFormEditorOpen && (
            <TestDynaFormEditor isOpen onClose={() => this.setState({ isDynaFormEditorOpen: false })} />
          )}

          {isIntroductionDialogOpen && (
            <Dialog
              isOpen
              title="Looks like you don't have access to any client records."
              buttons={[
                {
                  text: "Close",
                  color: "success",
                  onClick: () => this.setState({ isIntroductionDialogOpen: false }),
                },
              ]}
            >
              <p>You can create a Client record by clicking Add New Client button located on the top right corner.</p>
              <p>
                If you don't have permission to create Сlient records, please contact your Community or Organization
                Admin to request access to Client(s) records via Client or Community care team.
              </p>
            </Dialog>
          )}

          {isAddingOrAccessRecordsInstructionDialogOpen && (
            <Dialog
              isOpen
              title="Create a Client then build a Care Team."
              buttons={[
                {
                  text: "Close",
                  color: "success",
                  onClick: () => this.setState({ isAddingOrAccessRecordsInstructionDialogOpen: false }),
                },
              ]}
            >
              <p>
                You don't have access to any Client records. You can create a Client record by clicking Add New Client
                button located on the top right corner.
              </p>
              <p>
                If you don't have permission to create Сlient records, please contact your Community or Organization
                Admin to request access to Client(s) records via Client or Community care team.
              </p>
              <p>Once you have access to Client(s), then you can set up care team for your Client(s).</p>
            </Dialog>
          )}

          {isCareTeamManagementInstructionDialogOpen && (
            <Dialog
              isOpen
              title="Team Care provides better aligned care and sharing of health information and secure communications."
              buttons={[
                {
                  text: "Close",
                  color: "success",
                  onClick: () => this.setState({ isCareTeamManagementInstructionDialogOpen: false }),
                },
              ]}
            >
              <p>Looks like you have access to multiple Client records.</p>
              <p>Select a Client from your list and click Care Team in the left slide menu.</p>
            </Dialog>
          )}

          {isDocumentManagementInstructionDialogOpen && (
            <Dialog
              isOpen
              title="Documents & e-Sign."
              buttons={[
                {
                  text: "Close",
                  color: "success",
                  onClick: () => this.setState({ isDocumentManagementInstructionDialogOpen: false }),
                },
              ]}
            >
              <p>Looks like you have access to multiple Client records.</p>
              <p>Select a Client from your list and click Documents in the left slide menu.</p>
            </Dialog>
          )}

          {isMedicationsInstructionDialogOpen && (
            <Dialog
              isOpen
              title="Access real-time medications."
              buttons={[
                {
                  text: "Close",
                  color: "success",
                  onClick: () => this.setState({ isMedicationsInstructionDialogOpen: false }),
                },
              ]}
            >
              <p>Looks like you have access to multiple Client records.</p>
              <p>
                Select a Client from your list and click Dashboard in the left slide menu and scroll down to Medications
                section.
              </p>
              <p>You can easily track new medications, changes or discontinued medications.</p>
            </Dialog>
          )}

          {isRidesInstructionDialogOpen && (
            <Dialog
              isOpen
              title="Non-Emergency transportation."
              buttons={[
                {
                  text: "Close",
                  color: "success",
                  onClick: () => this.setState({ isRidesInstructionDialogOpen: false }),
                },
              ]}
            >
              <p>Looks like you have access to multiple Client records.</p>
              <p>Select a Client from your list and click Rides in the left slide menu.</p>
              <p>
                You can easily schedule non-emergency transportation services and share details with family members and
                care teams.
              </p>
              <p>
                If you have any operational questions reach out to our Support team: <b>support@simplyconnect.me</b>
              </p>
            </Dialog>
          )}

          {isAddingOrAccessRecordsAndDocumentManagementInstructionDialogOpen && (
            <Dialog
              isOpen
              title="No Client records."
              buttons={[
                {
                  text: "Close",
                  color: "success",
                  onClick: () =>
                    this.setState({ isAddingOrAccessRecordsAndDocumentManagementInstructionDialogOpen: false }),
                },
              ]}
            >
              <p>
                You don't have access to any Client records. You can create a Client record by clicking Add New Client
                button located on the top right corner.
              </p>
              <p>
                If you don't have permission to create Сlient records, please contact your Community or Organization
                Admin to request access to Client(s) records via Client or Community care team.
              </p>
              <p>Once you have access to Client(s), then you can access their documents.</p>
            </Dialog>
          )}

          {isAddingOrAccessRecordsAndMedicationsInstructionDialogOpen && (
            <Dialog
              isOpen
              title="No Client records."
              buttons={[
                {
                  text: "Close",
                  color: "success",
                  onClick: () => this.setState({ isAddingOrAccessRecordsAndMedicationsInstructionDialogOpen: false }),
                },
              ]}
            >
              <p>
                You don't have access to any Client records. You can create a Client record by clicking Add New Client
                button located on the top right corner.
              </p>
              <p>
                If you don't have permission to create Сlient records, please contact your Community or Organization
                Admin to request access to Client(s) records via Client or Community care team.
              </p>
              <p>
                Once you have access to Client(s), then you can easily track new medications, changes or discontinued
                medications.
              </p>
            </Dialog>
          )}

          {isAddingOrAccessRecordsAndRidesInstructionDialogOpen && (
            <Dialog
              isOpen
              title="No Client records."
              buttons={[
                {
                  text: "Close",
                  color: "success",
                  onClick: () => this.setState({ isAddingOrAccessRecordsAndRidesInstructionDialogOpen: false }),
                },
              ]}
            >
              <p>
                You don't have access to any Client records. You can create a Client record by clicking Add New Client
                button located on the top right corner.
              </p>
              <p>
                If you don't have permission to create Сlient records, please contact your Community or Organization
                Admin to request access to Client(s) records via Client or Community care team.
              </p>
              <p>
                Once you have access to Client(s), then you can easily schedule non-emergency transportation services
                and share details with family members and care teams.
              </p>
            </Dialog>
          )}

          {isCannotViewClientDetailsDialogOpen && (
            <WarningDialog
              isOpen
              title="Access to client data is not available per client request"
              buttons={[
                {
                  text: "Close",
                  onClick: this.toggleCannotViewClientDetailsDialog,
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
                    this.setState({
                      selected: null,
                      isHL7WarningDialogOpen: false,
                    });
                  },
                },
                {
                  text: "Ok",
                  onClick: () => {
                    this.setState({
                      isEditorOpen: true,
                      isHL7WarningDialogOpen: false,
                    });
                  },
                },
              ]}
            />
          )}

          <WarningDialog
            isOpen={this.state.waringDialog}
            title="Are you sure you don't want to show this message again?"
            buttons={[
              {
                text: "Cancel",
                onClick: () => {
                  this.changeWaringDialog();
                },
              },
              {
                text: "OK",
                onClick: () => {
                  this.changeWaringDialog();
                  this.setRemindShow(selected.id);
                  this.fetchData(selected.id);
                },
              },
            ]}
          />

          {this.error && !isIgnoredError(this.error) && (
            <ErrorViewer isOpen error={this.error} onClose={this.onResetError} />
          )}
        </div>
      </DocumentTitle>
    );
  }
}

export default compose(withRouter, connect(mapStateToProps, mapDispatchToProps))(Clients);
