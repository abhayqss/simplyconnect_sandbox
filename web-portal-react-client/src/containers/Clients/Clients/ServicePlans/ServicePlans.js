import React, { Component } from "react";

import { compact, compose } from "underscore";

import cn from "classnames";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { withRouter } from "react-router-dom";

import { Badge, Button, UncontrolledTooltip as Tooltip } from "reactstrap";

import DocumentTitle from "react-document-title";

import { withDownloadingStatusInfoToast } from "hocs";

import Table from "components/Table/Table";
import Actions from "components/Table/Actions/Actions";
import ErrorViewer from "components/ErrorViewer/ErrorViewer";
import Breadcrumbs from "components/Breadcrumbs/Breadcrumbs";
import ErrorDialog from "components/dialogs/ErrorDialog/ErrorDialog";
import SuccessDialog from "components/dialogs/SuccessDialog/SuccessDialog";

import {
  LoadCanAddServicePlanAction,
  LoadCanReviewServicePlanByClinicianAction,
  LoadClientDetailsAction,
  UpdateSideBarAction,
} from "actions/clients";

import * as servicePlanListActions from "redux/client/servicePlan/list/servicePlanListActions";
import * as servicePlanDetailsActions from "redux/client/servicePlan/details/servicePlanDetailsActions";
import servicePlanDomainsActions from "redux/client/servicePlan/domain/list/servicePlanDomainListActions";

import { PAGINATION, SERVER_ERROR_CODES } from "lib/Constants";

import { Response } from "lib/utils/AjaxUtils";
import { DateUtils, isEmpty } from "lib/utils/Utils";

import "./ServicePlans.scss";

import ServicePlanFilter from "./ServicePlanFilter/ServicePlanFilter";
import ServicePlanEditor from "./ServicePlanEditor/ServicePlanEditor";
import ServicePlanViewer from "./ServicePlanViewer/ServicePlanViewer";
import DomainsSelectionEditor from "./DomainsSelectionEditor/DomainsSelectionEditor";
import CustomizeServicePlan from "./CustomizeServicePlan/CustomizeServicePlan";
import clientService from "../../../../services/ClientService";

const { FIRST_PAGE } = PAGINATION;

const { format, formats } = DateUtils;

const ICON_SIZE = 36;
const DATE_FORMAT = formats.americanMediumDate;

const STATUS_COLORS = {
  IN_DEVELOPMENT: "#d5f3b8",
  SHARED_WITH_CLIENT: "#ffedc2",
};

function isIgnoredError(e = {}) {
  return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE;
}

function getOnSort(fieldName, context) {
  return (_, order) => context.onSort(fieldName, order);
}

function mapStateToProps(state) {
  const { assessment, servicePlan } = state.client;

  const { list, details } = servicePlan;

  return {
    error: list.error,
    isFetching: list.isFetching,
    fetchCount: list.fetchCount,
    dataSource: list.dataSource,
    shouldReload: list.shouldReload,

    details,

    client: state.client,

    count: servicePlan.count.value,
    assessmentCount: assessment.count.value,
    servicePlanCount: servicePlan.count.value,

    auth: state.auth,
    canAdd: servicePlan.can.add.value,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(servicePlanListActions, dispatch),
      details: bindActionCreators(servicePlanDetailsActions, dispatch),
      domain: {
        list: bindActionCreators(servicePlanDomainsActions, dispatch),
      },
    },
  };
}

class ServicePlans extends Component {
  state = {
    selected: null,
    selectedArchived: null,
    selectedDomains: [],

    isEditorOpen: false,
    isViewerOpen: false,
    isArchiveViewerOpen: false,
    isDomainsSelectionEditorOpen: false,

    isSaveSuccessDialogOpen: false,
    isInDevelopmentErrorDialogOpen: false,

    showAddNewPlanModel: false,

    showServicePlanEditModal: false,
    servicePlanTemplate: "",
    servicePlanTemplatesData: "",
    editServicePlanId: "",
    editServicePlanTemplateId: "",

    editServiceScoring: [],

    isEditServicePlan: false,

    clientDetailData: {},
  };

  componentDidMount() {
    const { state } = this.props.location;

    clientService
      .findById(this.clientId)
      .then((res) => {
        this.setClientDetailData(res.data);
      })
      .catch((err) => {
        this.setClientDetailData({});
      });

    if (state) {
      const { shouldEdit, shouldCreate, servicePlanId } = state;

      if (shouldCreate) {
        this.isAnyInDevelopment().then(
          Response(({ data: isTrue }) => {
            this.setState({
              isInDevelopmentErrorDialogOpen: isTrue,
              isEditorOpen: !isTrue && shouldCreate,
            });
          }),
        );
      } else if (shouldEdit) {
        this.onEdit({ id: servicePlanId });
      } else if (servicePlanId) {
        this.onViewDetails({ id: state.servicePlanId });
      }

      this.props.history.replace("service-plans", {});
    }
  }

  componentDidUpdate() {
    if (this.props.shouldReload) {
      this.refresh();
    }
  }

  componentWillUnmount() {
    this.actions.clear();
  }

  onRefresh = (page) => {
    this.refresh(page);
  };

  onResetError = () => {
    this.actions.clearError();
    this.actions.details.clearError();
  };

  onAdd = () => {
    this.isAnyInDevelopment().then(
      Response(({ data: isTrue }) => {
        this.setState({
          isEditorOpen: !isTrue,
          isInDevelopmentErrorDialogOpen: isTrue,
        });
      }),
    );
  };

  onEdit = async (servicePlan) => {
    if (servicePlan.custome) {
      const data = {
        content: servicePlan.template,
      };
      this.setEditServicePlanId(servicePlan.id);
      this.setEditServicePlanTemplateId(servicePlan.templateId);
      this.setServicePlanTemplate(data);
      // Edit servicePlan data
      this.setServicePlanTemplatesData(servicePlan.result);

      this.setShowServicePlanEditModal(true);
      this.setIsEditServicePlan(true);
      this.setEditServicePlanScoring(servicePlan.scoringlist);

      return;
    }

    this.setState({
      isEditorOpen: true,
      selected: servicePlan,
    });
  };

  onCloseEditor = () => {
    this.setState({
      selected: null,
      isEditorOpen: false,
    });
  };

  onSaveSuccess = () => {
    this.refresh();
    this.setShowAddNewPlanModel(false);

    this.setState({
      selected: null,
      isEditorOpen: false,
      isSaveSuccessDialogOpen: true,
    });
  };

  onDownload = (servicePlan) => {
    const { withDownloadingStatusInfoToast } = this.props;

    const onResponse = ({ data }) => {
      let shouldShowSelector = data.length > 1;

      if (shouldShowSelector) {
        this.setState({
          selected: servicePlan,
          selectedDomains: data,
          isDomainsSelectionEditorOpen: true,
        });
      } else {
        withDownloadingStatusInfoToast(() => this.actions.details.download(this.clientId, servicePlan.id));
      }
    };

    this.actions.domain.list
      .load({
        clientId: this.clientId,
        servicePlanId: servicePlan.id,
      })
      .then(Response(onResponse));
  };

  onViewDetails = (servicePlan) => {
    this.setState({
      isViewerOpen: true,
      selected: servicePlan,
    });
  };

  onViewArchivedDetails = (servicePlan) => {
    this.setState({
      selectedArchived: servicePlan,
      isArchiveViewerOpen: true,
    });
  };

  onCloseServicePlanViewer = () => {
    this.setState({
      selected: null,
      isViewerOpen: false,
    });
  };

  onCloseArchivedServicePlanViewer = () => {
    this.setState({
      selectedArchived: null,
      isArchiveViewerOpen: false,
    });
  };

  onCloseDomainsSelectorEditor = () => {
    this.setState({
      selected: null,
      selectedDomains: [],
      isDomainsSelectionEditorOpen: false,
    });
  };

  onSort = (field, order) => {
    this.sort(field, order);
  };

  getOnSort = (field) => {
    return this.onSort.bind(this, field);
  };

  get actions() {
    return this.props.actions;
  }

  get clientId() {
    return +this.props.match.params.clientId;
  }

  get authUser() {
    return this.props.auth.login.user.data;
  }

  get error() {
    return this.props.error || this.props.details.error;
  }

  update(isReload, page) {
    const { isFetching, shouldReload, dataSource: ds } = this.props;

    if (isReload || shouldReload || (!isFetching && isEmpty(ds.data))) {
      const { field, order } = ds.sorting;
      const { page: p, size } = ds.pagination;

      this.actions.load({
        size,
        page: page || p,
        ...ds.filter.toJS(),
        clientId: this.clientId,
        sort: `${field},${order}`,
      });
    }
  }

  sort(field, order, shouldReload) {
    this.actions.sort(field, order, shouldReload);
  }

  refresh(page) {
    this.update(true, page || FIRST_PAGE);
  }

  isAnyInDevelopment() {
    return this.actions.isAnyInDevelopment(this.clientId);
  }

  setShowAddNewPlanModel = (value) => {
    this.setState({
      showAddNewPlanModel: value,
    });
  };

  setShowServicePlanEditModal = (value) => {
    this.setState({
      showServicePlanEditModal: value,
    });
  };

  setServicePlanTemplate = (value) => {
    this.setState({
      servicePlanTemplate: value,
    });
  };

  setServicePlanTemplatesData = (value) => {
    this.setState({
      servicePlanTemplatesData: value,
    });
  };

  setClientDetailData = (value) => {
    this.setState({
      clientDetailData: value,
    });
  };

  setEditServicePlanId = (value) => {
    this.setState({
      editServicePlanId: value,
    });
  };

  setEditServicePlanTemplateId = (value) => {
    this.setState({
      editServicePlanTemplateId: value,
    });
  };

  setIsEditServicePlan = (value) => {
    this.setState({
      isEditServicePlan: value,
    });
  };

  setEditServicePlanScoring = (value) => {
    this.setState({
      editServiceScoring: value,
    });
  };

  render() {
    const {
      selected,
      selectedArchived,

      isEditorOpen,
      isViewerOpen,
      isArchiveViewerOpen,
      isDomainsSelectionEditorOpen,

      isSaveSuccessDialogOpen,
      isInDevelopmentErrorDialogOpen,
    } = this.state;

    const { canAdd, client, className, isFetching, fetchCount, dataSource: ds } = this.props;

    const clientId = this.clientId;
    const isClientActive = client?.details?.data?.isActive;

    return (
      <DocumentTitle title="Simply Connect | Clients List | Client Record | Service Plans">
        <div className={cn("ServicePlans", className)}>
          <LoadCanAddServicePlanAction params={{ clientId }} />
          <LoadClientDetailsAction params={{ clientId }} onPerformed={() => this.refresh()} />
          <LoadCanReviewServicePlanByClinicianAction params={{ clientId }} />
          <UpdateSideBarAction
            params={{
              clientId,
              shouldRefresh: isFetching && fetchCount > 1,
            }}
          />
          <Breadcrumbs
            items={compact([
              { title: "Clients", href: "/clients", isEnabled: true },
              client.details.data && {
                title: client.details.data.fullName,
                href: "/clients/" + clientId,
              },
              {
                title: "Service Plans",
                href: `clients/${clientId || 1}/service-plans`,
                isActive: true,
              },
            ])}
          />
          <Table
            hasHover
            hasOptions
            hasPagination
            keyField="id"
            title="Service Plans"
            noDataText={ds.filter.searchText ? "No results." : "No service plans."}
            isLoading={isFetching}
            className="ServicePlanList"
            containerClass="ServicePlanListContainer"
            data={ds.data}
            pagination={ds.pagination}
            columns={[
              {
                dataField: "#",
                text: "#",
                headerStyle: {
                  width: "40px",
                },
                classes: "hide-on-tablet",
                headerClasses: "hide-on-tablet",
                formatter: (v, row, rowIndex, formatExtraData, isMobile) => (
                  <>
                    <span
                      id={`${isMobile ? "m-" : ""}service-plan-${row.id}`}
                      className="ServicePlanList-ServicePlanNumber"
                      onClick={() => this.onViewDetails(row)}
                    >
                      {(ds.pagination.page - 1) * ds.pagination.size + (rowIndex + 1)}
                    </span>
                    <Tooltip
                      className="AssessmentList-Tooltip"
                      placement="top"
                      target={`${isMobile ? "m-" : ""}service-plan-${row.id}`}
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
                      View service plan
                    </Tooltip>
                  </>
                ),
              },
              {
                dataField: "status" /* TODO: NEED TO CHANGE THE SORTING ON API */,
                text: "Status",
                sort: true,
                headerClasses: "ServicePlanList-Header-Status",
                onSort: getOnSort("servicePlanStatus", this),
                formatter: (v, row, rowIndex, formatExtraData, isMobile) => (
                  <>
                    <span
                      id={`${isMobile ? "m-" : ""}service-plan-status-${row.id}`}
                      style={{ backgroundColor: STATUS_COLORS[row.status.name] }}
                      className="ServicePlanList-Status"
                      onClick={() => this.onViewDetails(row)}
                    >
                      {row.status.title}
                    </span>
                    <Tooltip
                      placement="top"
                      target={`${isMobile ? "m-" : ""}service-plan-status-${row.id}`}
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
                      View service plan
                    </Tooltip>
                  </>
                ),
              },
              {
                dataField: "dateCreated",
                text: "Date Started",
                sort: true,
                align: "right",
                headerAlign: "right",
                onSort: this.onSort,
                formatter: (v) => v && format(v, DATE_FORMAT),
              },
              {
                dataField: "dateCompleted",
                text: "Date Completed",
                sort: true,
                align: "right",
                headerAlign: "right",
                onSort: this.onSort,
                formatter: (v) => v && format(v, DATE_FORMAT),
              },
              {
                dataField: "scoring" /* TODO: NEED TO CHANGE THE SORTING ON API */,
                text: "Scoring",
                sort: true,
                align: "right",
                headerAlign: "right",
                onSort: getOnSort("scoring.totalScore", this),
                formatter: (v, row) => row.scoring,
              },
              {
                dataField: "author" /* TODO: NEED TO CHANGE THE SORTING ON API */,
                text: "Author",
                sort: true,
                onSort: getOnSort("employee.firstName", this),
                formatter: (v, row) => row.author,
              },
              {
                dataField: "@actions",
                text: "",
                headerClasses: "ServicePlanList-Actions",
                align: "right",
                formatter: (v, row) => {
                  return (
                    <Actions
                      data={row}
                      hasDownloadAction
                      iconSize={ICON_SIZE}
                      hasEditAction={row.canEdit && isClientActive}
                      editHintMessage="Edit service plan"
                      downloadHintMessage="Download Pdf File"
                      onEdit={this.onEdit}
                      onDownload={this.onDownload}
                    />
                  );
                },
              },
            ]}
            columnsMobile={["#", "status", "author"]}
            renderCaption={(title) => {
              return (
                <div className="ServicePlanList-Caption">
                  <div className="ServicePlanList-CaptionHeader">
                    <span className="ServicePlanList-Title">
                      <span className="ServicePlanList-TitleText">{title}</span>
                      <span className="text-nowrap">
                        <span className="ServicePlanList-ClientName">
                          {client.details.data && " / " + client.details.data.fullName}
                        </span>
                        {ds.pagination.totalCount ? (
                          <Badge color="info" className="Badge Badge_place_top-right">
                            {ds.pagination.totalCount}
                          </Badge>
                        ) : null}
                      </span>
                    </span>
                    <div className="ServicePlanList-ControlPanel">
                      {canAdd && isClientActive && (
                        <Button
                          color="success"
                          className="AddServicePlanBtn"
                          onClick={() => this.setShowAddNewPlanModel(true)}
                        >
                          Add New<span className="AddServicePlanBtn-OptText"> Plan</span>
                        </Button>
                      )}
                    </div>
                  </div>
                  <ServicePlanFilter />
                </div>
              );
            }}
            onRefresh={this.onRefresh}
          />
          {isEditorOpen && (
            <ServicePlanEditor
              isOpen
              planId={selected?.id}
              clientId={this.clientId}
              onClose={this.onCloseEditor}
              onSaveSuccess={this.onSaveSuccess}
              setShowAddNewPlanModel={this.setShowAddNewPlanModel}
            />
          )}
          {isViewerOpen && (
            <ServicePlanViewer
              isOpen
              planId={selected?.id}
              clientId={this.clientId}
              onClose={this.onCloseServicePlanViewer}
              onViewDetails={this.onViewArchivedDetails}
            />
          )}
          {isArchiveViewerOpen && (
            <ServicePlanViewer
              isOpen
              isPlanArchived
              clientId={this.clientId}
              planId={selectedArchived?.id}
              onClose={this.onCloseArchivedServicePlanViewer}
            />
          )}
          {isSaveSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title="The updates have been saved"
              buttons={[
                {
                  text: "OK",
                  onClick: () => {
                    this.setState({
                      isSaveSuccessDialogOpen: false,
                    });
                  },
                },
              ]}
            />
          )}
          {isInDevelopmentErrorDialogOpen && (
            <ErrorDialog
              isOpen
              text={
                "There is an active service plan.\n You can not " +
                "create a new one until the active plan is completed."
              }
              buttons={[
                {
                  text: "Close",
                  onClick: () => {
                    this.setState({
                      isInDevelopmentErrorDialogOpen: false,
                    });
                  },
                },
              ]}
            />
          )}
          <DomainsSelectionEditor
            clientId={clientId}
            servicePlanId={selected?.id}
            domains={this.state.selectedDomains}
            isOpen={isDomainsSelectionEditorOpen}
            onClose={this.onCloseDomainsSelectorEditor}
            onSaveSuccess={this.onCloseDomainsSelectorEditor}
          />
          {this.error && !isIgnoredError(this.error) && (
            <ErrorViewer isOpen error={this.error} onClose={this.onResetError} />
          )}

          <CustomizeServicePlan
            showAddNewPlanModel={this.state.showAddNewPlanModel}
            setShowAddNewPlanModel={this.setShowAddNewPlanModel}
            noCustomizePlanClick={this.onAdd}
            clientId={this.clientId}
            refresh={this.onRefresh}
            setShowServicePlanEditModal={this.setShowServicePlanEditModal}
            showServicePlanEditModal={this.state.showServicePlanEditModal}
            servicePlanTemplate={this.state.servicePlanTemplate}
            setServicePlanTemplate={this.setServicePlanTemplate}
            isEditServicePlan={this.state.isEditServicePlan}
            setIsEditServicePlan={this.setIsEditServicePlan}
            servicePlanTemplatesData={this.state.servicePlanTemplatesData}
            editServicePlanId={this.state.editServicePlanId}
            setEditServicePlanId={this.setEditServicePlanId}
            editServicePlanTemplateId={this.state.editServicePlanTemplateId}
            setEditServicePlanTemplateId={this.setEditServicePlanTemplateId}
            editServiceScoring={this.state.editServiceScoring}
            setEditServicePlanScoring={this.setEditServicePlanScoring}
            clientDetailData={this.state.clientDetailData}
          />
        </div>
      </DocumentTitle>
    );
  }
}

export default compose(
  withRouter,
  withDownloadingStatusInfoToast,
  connect(mapStateToProps, mapDispatchToProps),
)(ServicePlans);
