import React, { Component } from "react";

import cn from "classnames";

import { compact, map } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators, compose } from "redux";

import { withRouter } from "react-router-dom";

import { withDownloadingStatusInfoToast, withQueryCache } from "hocs";

import { withAssessmentUtils } from "hocs/clients";

import DocumentTitle from "react-document-title";

import { Badge, Col, Row, UncontrolledTooltip as Tooltip } from "reactstrap";

import { Breadcrumbs, ErrorViewer, SearchField, Table } from "components";

import { AddButton, Button, DownloadButton, EditButton, HideButton, RefreshButton } from "components/buttons";

import { ConfirmDialog, SuccessDialog } from "components/dialogs";

import { LoadAssessmentTypesAction } from "actions/directory";

import {
  LoadCanDownloadInTuneReportAction,
  LoadCanGenerateInTuneReportAction,
  LoadClientDetailsAction,
  UpdateSideBarAction,
} from "actions/clients";

import * as assessmentListActions from "redux/client/assessment/list/assessmentListActions";
import inTuneReportDetailsActions from "redux/client/assessment/report/in-tune/details/inTuneReportDetailsActions";
import * as isAnyAssessmentInProcessActions from "redux/client/assessment/anyInProcess/isAnyAssessmentInProcessActions";

import { ASSESSMENT_STATUSES, ASSESSMENT_TYPES, PAGINATION, SERVER_ERROR_CODES, SYSTEM_ROLES } from "lib/Constants";

import { DateUtils, isEmpty } from "lib/utils/Utils";

import { ReactComponent as Warning } from "images/alert-yellow.svg";

import AssessmentViewer from "./AssessmentViewer/AssessmentViewer";
import AssessmentEditor from "./AssessmentEditor/AssessmentEditor";
import AssessmentVisibilityEditor from "./AssessmentVisibilityEditor/AssessmentVisibilityEditor";

import "./Assessments.scss";

const { FIRST_PAGE } = PAGINATION;

const { format, formats } = DateUtils;

const DATE_FORMAT = formats.americanMediumDate;

const { HOME_CARE_ASSISTANT } = SYSTEM_ROLES;

const SYS_ROLES_WITH_NOT_VIEWABLE_CLIENT = [HOME_CARE_ASSISTANT];

const {
  GAD7,
  PHQ9,
  IN_HOME,
  HOUSING,
  CARE_MGMT,
  ARIZONA_SSM,
  IN_HOME_CARE,
  COMPREHENSIVE,
  NOR_CAL_COMPREHENSIVE,
  HMIS_ADULT_CHILD_INTAKE,
  HMIS_ADULT_CHILD_REASESSMENT,
  HMIS_ADULT_CHILD_REASESSMENT_EXIT,
  BENEFICIARY_SCREENING_DIABETIC_FOOT_AND_EYE_EXAM,
  BENEFICIARY_COLORECTAL_CANCER_SCREENING,
  BENEFICIARY_MAMMOGRAM_SCREENING,
  BENEFICIARY_SCREENING_FOR_TYPE_II_DIABETES,
  FAST,
  SHORT,
  REVISED,
  SCREEN,
} = ASSESSMENT_TYPES;

const { HIDDEN } = ASSESSMENT_STATUSES;

const STATUS_COLORS = {
  HIDDEN: "#e0e0e0",
  INACTIVE: "#e0e0e0",
  COMPLETED: "#d1ebfe",
  IN_PROCESS: "#d5f3b8",
};

function isIgnoredError(e = {}) {
  return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE;
}

function mapStateToProps(state) {
  const { document, assessment, servicePlan } = state.client;

  return {
    error: assessment.list.error,
    isFetching: assessment.list.isFetching,
    fetchCount: assessment.list.fetchCount,
    dataSource: assessment.list.dataSource,
    shouldReload: assessment.list.shouldReload,

    auth: state.auth,
    client: state.client,
    directory: state.directory,

    count: assessment.count.value,
    canAdd: assessment.can.add.value,
    canDownloadInTuneReport: assessment.report.inTune.can.download,
    canGenerateInTuneReport: assessment.report.inTune.can.generate.value,

    isAnyAssessmentInProcess: assessment.anyInProcess.value,

    documentCount: document.count.value,
    servicePlanCount: servicePlan.count.value,
    eventCount: state.event.note.composed.count.value,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(assessmentListActions, dispatch),
      report: {
        inTune: {
          details: bindActionCreators(inTuneReportDetailsActions, dispatch),
        },
      },
      isAnyAssessmentInProcess: bindActionCreators(isAnyAssessmentInProcessActions, dispatch),
    },
  };
}

class Assessments extends Component {
  state = {
    selected: null,
    selectedParent: null,
    selectedArchived: null,

    isCopying: false,
    isEditorOpen: false,
    isViewerOpen: false,
    shouldOpenViewer: false,
    isArchiveViewerOpen: false,
    isVisibilityEditorOpen: false,

    isSaveSuccessDialogOpen: false,
    isChangeActivitySuccessDialogOpen: false,
    isChangeVisibilitySuccessDialogOpen: false,

    isEditCancelConfirmDialogOpen: false,
    isCompleteSuccessDialogOpen: false,
  };

  get actions() {
    return this.props.actions;
  }

  get authUser() {
    return this.props.auth.login.user.data;
  }

  get clientId() {
    return +this.props.match.params.clientId;
  }

  get error() {
    return this.props.error;
  }

  componentDidMount() {
    this.refresh();

    const { state } = this.props.location;

    if (state) {
      const { assessmentId, assessmentTypeName } = state;

      if (assessmentId) {
        this.setState({
          selected: {
            id: assessmentId,
            typeName: assessmentTypeName,
          },
          shouldOpenViewer: true,
        });
      } else
        this.setState({
          isEditorOpen: state.shouldCreate,
        });

      this.props.history.replace("assessments", {});
    }
  }

  componentDidUpdate() {
    const { shouldReload } = this.props;

    const { selected, shouldOpenViewer } = this.state;

    if (shouldReload) this.refresh();

    if (shouldOpenViewer) {
      const { typeName } = selected;
      const type = this.getTypeByName(typeName);

      if (type) {
        this.setState({ shouldOpenViewer: false });
        this.onView({ ...selected, typeId: type.id });
      }
    }
  }

  componentWillUnmount() {
    this.actions.clear();
  }

  onResetError = () => {
    this.actions.clearError();
  };

  onRefresh = (page) => {
    this.refresh(page);
  };

  onSort = (field, order) => {
    this.sort(field, order);
  };

  onChangeFilterField = (name, value) => {
    this.changeFilter({ [name]: value });
  };

  onAdd = (type, parent) => {
    this.setState({ isEditorOpen: true });

    if ([HMIS_ADULT_CHILD_INTAKE, HMIS_ADULT_CHILD_REASESSMENT].includes(parent?.typeName)) {
      this.setState({
        selected: { type },
        selectedParent: parent,
      });
    }
  };

  onDownloadInTuneReport = () => {
    this.downloadInTuneReport();
  };

  onCopy = (assessment) => {
    this.setState({
      isCopying: true,
      isEditorOpen: true,
      selected: assessment,
    });
  };

  onEdit = (assessment) => {
    this.setState({
      isEditorOpen: true,
      selected: assessment,
    });
  };

  onView = (assessment) => {
    this.setState({
      selected: assessment,
      isViewerOpen: true,
    });
  };

  onDownload = ({ id, typeName }) => {
    if (![COMPREHENSIVE, NOR_CAL_COMPREHENSIVE].includes(typeName)) {
      this.downloadPdf(id, typeName);
    }
  };

  onViewArchived = (assessment) => {
    this.setState({
      selectedArchived: assessment,
      isArchiveViewerOpen: true,
    });
  };

  onCloseEditor = (shouldConfirm = false) => {
    this.setState({
      selectedParent: null,
      isEditorOpen: shouldConfirm,
      isCompleteSuccessDialogOpen: false,
      isEditCancelConfirmDialogOpen: shouldConfirm,
    });

    if (!shouldConfirm) {
      this.setState({
        selected: null,
        isCopying: false,
      });
    }
  };

  onCloseEditCancelConfirmDialog = () => {
    this.setState({
      isEditCancelConfirmDialogOpen: false,
    });
  };

  onCloseViewer = () => {
    this.setState({
      selected: null,
      isViewerOpen: false,
    });
  };

  onCloseArchiveViewer = () => {
    this.setState({
      selectedArchived: null,
      isArchiveViewerOpen: false,
    });
  };

  onSaveSuccess = (o, shouldClose) => {
    this.refresh();

    if (shouldClose) {
      this.setState({
        selected: o,
        isCopying: false,
        isEditorOpen: false,
        selectedParent: null,
        isSaveSuccessDialogOpen: true,
      });
    } else this.setState({ selected: o });

    this.props.cache.invalidateQueries("Assessment", {
      clientId: this.clientId,
      size: 10,
    });
  };

  onCompleteSuccess = (o) => {
    this.refresh();

    this.setState({
      selected: o,
      isCopying: false,
      isEditorOpen: false,
      isCompleteSuccessDialogOpen: true,
    });
  };

  onChangeActivitySuccess = (data, isInactive) => {
    this.refresh().then(() => {
      this.setState({
        selected: { ...data, isInactive },
        isChangeActivitySuccessDialogOpen: true,
      });
    });

    this.setState({
      isCopying: false,
      isEditorOpen: false,
    });
  };

  onOpenVisibilityEditor = (o) => {
    this.setState({
      selected: o,
      isVisibilityEditorOpen: true,
    });
  };

  onChangeVisibilitySuccess = () => {
    this.refresh();
    this.onCloseVisibilityEditor();
    this.setState({ isChangeVisibilitySuccessDialogOpen: true });
  };

  onCloseVisibilityEditor = (isCancel) => {
    this.setState({
      isVisibilityEditorOpen: false,
      ...(isCancel && { selected: null }),
    });
  };

  onCompleteSave = () => {
    this.setState({
      selected: null,
      selectedParent: null,
      isCopying: false,
      isSaveSuccessDialogOpen: false,
      isCompleteSuccessDialogOpen: false,
      isChangeActivitySuccessDialogOpen: false,
    });
  };

  get utils() {
    return this.props.assessmentUtils;
  }

  onBackToEditor = ({ shouldAddNeedsToServicePlan = false } = {}) => {
    this.setState((s) => ({
      isEditorOpen: true,
      selectedParent: null,
      isSaveSuccessDialogOpen: false,
      isCompleteSuccessDialogOpen: false,
      selected: { ...s.selected, shouldAddNeedsToServicePlan },
    }));
  };

  update(isReload, page) {
    const { isFetching, shouldReload, dataSource: ds } = this.props;

    const { isAnyAssessmentInProcess } = this.props.actions;

    if (this.getTypeByName(HOUSING)?.id)
      isAnyAssessmentInProcess.load({
        clientId: this.clientId,
        typeId: this.getTypeByName(HOUSING)?.id,
      });

    if (isReload || shouldReload || (!isFetching && isEmpty(ds.data))) {
      const { field, order } = ds.sorting;
      const { page: p, size } = ds.pagination;

      return this.actions.load({
        size,
        page: page || p,
        ...ds.filter.toJS(),
        clientId: this.clientId,
        ...(field && { sort: `${field},${order}` }),
      });
    }
  }

  sort(field, order) {
    this.actions.sort(field, order);
  }

  refresh(page) {
    return this.update(true, page || FIRST_PAGE);
  }

  getTypeByName(name) {
    return this.utils.getTypeByName(name);
  }

  getTypesByNames(names) {
    return this.utils.getTypesByNames(names);
  }

  downloadPdf(id, typeName) {
    const { withDownloadingStatusInfoToast } = this.props;

    withDownloadingStatusInfoToast(() => this.utils.downloadPdf(id, typeName));
  }

  downloadJson(id) {
    const { withDownloadingStatusInfoToast } = this.props;

    withDownloadingStatusInfoToast(() => this.utils.downloadJson(id));
  }

  downloadInTuneReport() {
    const { withDownloadingStatusInfoToast } = this.props;

    withDownloadingStatusInfoToast(() => this.actions.report.inTune.details.download({ clientId: this.clientId }));
  }

  clear() {
    this.actions.clear();
  }

  changeFilter(changes, shouldReload) {
    this.actions.changeFilter(changes, shouldReload);
  }

  canViewClient() {
    return Boolean(this.authUser && !SYS_ROLES_WITH_NOT_VIEWABLE_CLIENT.includes(this.authUser.roleName));
  }

  render() {
    const {
      client,
      canAdd,
      isFetching,
      fetchCount,
      dataSource: ds,
      canGenerateInTuneReport,
      canDownloadInTuneReport,
      isAnyAssessmentInProcess,
    } = this.props;

    // 检查localStorage中的triggerCurrentOrgId，根据环境隐藏Add New Assessment按钮
    const triggerCurrentOrgId = localStorage.getItem("triggerCurrentOrgId");
    const hideAddButton = (() => {
      // 根据当前环境判断
      const environment = process.env.REACT_APP_SENTRY_ENVIRONMENT;

      if (environment === "localhost") {
        // 本地，检查是否为2978
        return triggerCurrentOrgId === "2978";
      } else if (environment === "production") {
        // app环境，检查是否为10492
        return triggerCurrentOrgId === "10492";
      }

      // 其他环境不隐藏
      return false;
    })();

    const {
      selected,
      selectedParent,
      selectedArchived,

      isCopying,
      isEditorOpen,
      isViewerOpen,
      isArchiveViewerOpen,
      isVisibilityEditorOpen,

      isSaveSuccessDialogOpen,
      isChangeActivitySuccessDialogOpen,
      isChangeVisibilitySuccessDialogOpen,

      isEditCancelConfirmDialogOpen,
      isCompleteSuccessDialogOpen,
    } = this.state;

    const { fullName: clientFullName, isActive: isClientActive } = client.details.data || {};

    const clientId = this.clientId;

    const canAddComprehensive = this.getTypeByName(COMPREHENSIVE)?.canAdd;

    const preselectedType =
      this.authUser?.roleName === HOME_CARE_ASSISTANT
        ? this.getTypeByName(CARE_MGMT)
        : selectedParent
          ? selected?.type
          : null;
    return (
      <DocumentTitle title={`Simply Connect | Clients | ${clientFullName} | Assessments`}>
        <div className="Assessments">
          <LoadClientDetailsAction params={{ clientId }} />
          <LoadAssessmentTypesAction
            params={{
              clientId,
              types: [
                GAD7,
                PHQ9,
                IN_HOME,
                HOUSING,
                CARE_MGMT,
                ARIZONA_SSM,
                IN_HOME_CARE,
                COMPREHENSIVE,
                NOR_CAL_COMPREHENSIVE,
                HMIS_ADULT_CHILD_INTAKE,
                HMIS_ADULT_CHILD_REASESSMENT,
                HMIS_ADULT_CHILD_REASESSMENT_EXIT,
                BENEFICIARY_SCREENING_DIABETIC_FOOT_AND_EYE_EXAM,
                BENEFICIARY_COLORECTAL_CANCER_SCREENING,
                BENEFICIARY_MAMMOGRAM_SCREENING,
                BENEFICIARY_SCREENING_FOR_TYPE_II_DIABETES,
                FAST,
                SHORT,
                REVISED,
                SCREEN,
              ],
            }}
            onPerformed={() => {
              this.getTypeByName(HOUSING)?.id &&
                this.props.actions.isAnyAssessmentInProcess.load({
                  clientId,
                  typeId: this.getTypeByName(HOUSING)?.id,
                });
            }}
          />
          <LoadCanGenerateInTuneReportAction
            isMultiple
            params={{
              clientId,
              assessmentFetchCount: fetchCount,
            }}
            shouldPerform={(prevParams) => fetchCount !== prevParams.assessmentFetchCount}
          />
          <LoadCanDownloadInTuneReportAction
            isMultiple
            params={{
              clientId,
              assessmentFetchCount: fetchCount,
            }}
            shouldPerform={(prevParams) => fetchCount !== prevParams.assessmentFetchCount}
          />
          <UpdateSideBarAction params={{ clientId, shouldRefresh: isFetching && fetchCount > 1 }} />
          <Breadcrumbs
            items={compact([
              { title: "Clients", href: "/clients", isEnabled: true },
              client.details.data && {
                title: `${clientFullName}`,
                href: `/clients/${clientId || 1}`,
                isActive: !this.canViewClient(),
              },
              client.details.data && {
                title: "Assessments",
                href: `/clients/${clientId || 1}/assessments`,
                isActive: true,
              },
            ])}
          />
          <Table
            hasHover
            hasOptions
            hasPagination
            keyField="id"
            title="Assessments"
            isLoading={isFetching}
            className="AssessmentList"
            containerClass="AssessmentListContainer"
            data={ds.data}
            pagination={ds.pagination}
            columns={[
              {
                dataField: "typeTitle",
                text: "Assessment",
                sort: true,
                headerClasses: "ClientList-Header-AssessmentName",
                onSort: this.onSort,
                formatter: (v, row, index, formatExtraData, isMobile) => {
                  // v:文件名,row:整行信息, index: 0, formatExtraData, isMobile)
                  return (
                    <div className="overflow-hidden">
                      <div
                        id={`${isMobile ? "m-" : ""}assessment-${row.id}`}
                        className="AssessmentList-AssessmentName"
                        onClick={() => this.onView(row)}
                      >
                        {v}
                      </div>
                      <Tooltip
                        className="AssessmentList-Tooltip"
                        placement="top"
                        target={`${isMobile ? "m-" : ""}assessment-${row.id}`}
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
                        View Assessment
                      </Tooltip>
                    </div>
                  );
                },
              },
              {
                dataField: "status",
                text: "Status",
                sort: true,
                align: "left",
                headerAlign: "left",
                onSort: this.onSort,
                formatter: (v, row) => {
                  return (
                    <span
                      style={{ backgroundColor: STATUS_COLORS[row.status.name] }}
                      className="AssessmentList-AssessmentStatus"
                    >
                      {row.status.title}
                    </span>
                  );
                },
              },
              {
                dataField: "dateStarted",
                text: "Date Started",
                sort: true,
                align: "right",
                headerAlign: "right",
                headerClasses: "AssessmentList-DateStartedCol",
                onSort: this.onSort,
                formatter: (v) => v && format(v, DATE_FORMAT),
              },
              {
                dataField: "dateCompleted",
                text: "Date Completed",
                sort: true,
                align: "right",
                headerAlign: "right",
                headerClasses: "AssessmentList-DateCompletedCol",
                onSort: this.onSort,
                formatter: (v) => v && format(v, DATE_FORMAT),
              },
              {
                dataField: "author",
                text: "Author",
                sort: true,
                align: "left",
                onSort: this.onSort,
              },
              {
                dataField: "score",
                text: "Score",
                align: "left",
              },
              {
                dataField: "@actions",
                text: "",
                align: "right",
                headerClasses: "AssessmentList-ActionCol",
                formatExtraData: {
                  isAnyAssessmentInProcess,
                },
                formatter: (v, row, index, formatExtraData) => {
                  // const { isAnyAssessmentInProcess } = formatExtraData;

                  // console.log(isAnyAssessmentInProcess, "isAnyAssessmentInProcess");

                  return (
                    <div className="position-relative AssessmentList-Actions">
                      {isClientActive &&
                        row.typeName?.includes(COMPREHENSIVE) &&
                        row.status.name !== HIDDEN &&
                        row.canHide && (
                          <HideButton
                            name={`assessment-${row.id}_hide-btn`}
                            tipText="Hide the assessment"
                            onClick={() => this.onOpenVisibilityEditor(row)}
                            className="AssessmentList-ActionBtn"
                          />
                        )}
                      {isClientActive &&
                        row.typeName?.includes(COMPREHENSIVE) &&
                        row.status.name === HIDDEN &&
                        row.canRestore && (
                          <RefreshButton
                            name={`assessment-${row.id}_restore-btn`}
                            tipText="Restore the assessment"
                            onClick={() => this.onOpenVisibilityEditor(row)}
                            className="AssessmentList-ActionBtn"
                          />
                        )}
                      {canAdd && isClientActive && row.typeName === HOUSING && (
                        <AddButton
                          name={`add-copy-${row.id}`}
                          tipText={
                            !formatExtraData?.isAnyAssessmentInProcess
                              ? "Create a copy of assessment"
                              : "Can't create a new assessment as only one Housing Assessment can be in progress."
                          }
                          onClick={() => !formatExtraData?.isAnyAssessmentInProcess && this.onCopy(row)}
                          className={cn("AssessmentList-ActionBtn", {
                            "AssessmentList-ActionBtn_disabled": formatExtraData?.isAnyAssessmentInProcess,
                          })}
                          disabled={formatExtraData?.isAnyAssessmentInProcess}
                        />
                      )}
                      {canAdd && isClientActive && row.typeName === NOR_CAL_COMPREHENSIVE && (
                        <AddButton
                          name={`add-copy-${row.id}`}
                          tipText="Create a copy of assessment"
                          onClick={() => this.onCopy(row)}
                          className="AssessmentList-ActionBtn"
                        />
                      )}
                      {canAdd && isClientActive && canAddComprehensive && row.typeName === COMPREHENSIVE && (
                        <AddButton
                          name={`add-copy-${row.id}`}
                          tipText="Create a copy of assessment"
                          onClick={() => this.onCopy(row)}
                          className="AssessmentList-ActionBtn"
                        />
                      )}
                      {[HMIS_ADULT_CHILD_INTAKE, HMIS_ADULT_CHILD_REASESSMENT].includes(row.typeName) && (
                        <AddButton
                          tipPlace="bottom"
                          tipTrigger="focus"
                          tipClassName="AddOptionPicker"
                          name={`create-reassessment-${row.id}`}
                          renderTip={() => (
                            <>
                              {map(
                                this.getTypesByNames([HMIS_ADULT_CHILD_REASESSMENT, HMIS_ADULT_CHILD_REASESSMENT_EXIT]),
                                (type) => (
                                  <div
                                    key={type?.id}
                                    onClick={() => {
                                      this.onAdd(type, row);
                                    }}
                                    className="AddOptionPicker-Item"
                                  >
                                    {type?.title}
                                  </div>
                                ),
                              )}
                            </>
                          )}
                          className="AssessmentList-ActionBtn"
                        />
                      )}
                      {[
                        GAD7,
                        PHQ9,
                        IN_HOME,
                        HOUSING,
                        CARE_MGMT,
                        ARIZONA_SSM,
                        IN_HOME_CARE,
                        COMPREHENSIVE,
                        NOR_CAL_COMPREHENSIVE,
                        HMIS_ADULT_CHILD_INTAKE,
                        FAST,
                        SHORT,
                        REVISED,
                        SCREEN,
                      ].includes(row.typeName) && (
                        <>
                          <DownloadButton
                            name={`download-${row.id}`}
                            {...(!row.typeName?.includes(COMPREHENSIVE) && {
                              tipText: "Download Pdf File",
                            })}
                            onClick={() => this.onDownload(row)}
                            className="AssessmentList-ActionBtn"
                          />
                          {row.typeName?.includes(COMPREHENSIVE) && (
                            <Tooltip
                              trigger="focus"
                              placement="bottom"
                              target={`download-${row.id}`}
                              className="DownloadOptionPicker"
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
                              <div
                                onClick={() => {
                                  this.downloadPdf(row.id, row.typeName);
                                }}
                                className="DownloadOptionPicker-Item"
                              >
                                Download Pdf File
                              </div>
                              <div
                                onClick={() => {
                                  this.downloadJson(row.id);
                                }}
                                className="DownloadOptionPicker-Item"
                              >
                                Download JSON file
                              </div>
                            </Tooltip>
                          )}
                        </>
                      )}
                      {row.canEdit && isClientActive && (
                        <EditButton
                          name={`edit-${row.id}`}
                          tipText="Edit assessment"
                          onClick={() => this.onEdit(row)}
                          className="AssessmentList-ActionBtn"
                        />
                      )}
                    </div>
                  );
                },
              },
            ]}
            columnsMobile={["typeTitle", "author"]}
            noDataText="No assessments"
            renderCaption={(title, isMobile) => {
              return (
                <div className="AssessmentList-Caption">
                  <div className="Assessments-Header">
                    <div className="Assessments-Title">
                      <span className="Assessments-TitleText">{title}</span>
                      <span className="text-nowrap line-height-2">
                        <span className="Assessments-ClientName">
                          {client.details.data && " / " + client.details.data.fullName}
                        </span>
                        {ds.pagination.totalCount ? (
                          <Badge color="info" className="Badge Badge_place_top-right">
                            {ds.pagination.totalCount}
                          </Badge>
                        ) : null}
                      </span>
                    </div>
                    <div className="Assessments-Actions">
                      {(canDownloadInTuneReport.value || !!canDownloadInTuneReport.error) && (
                        <Button
                          color="success"
                          id={`run-in-tune-report${isMobile ? "-mobile" : ""}`}
                          hasTip={!canGenerateInTuneReport?.value}
                          disabled={!canGenerateInTuneReport?.value}
                          tipText={canGenerateInTuneReport?.reasonText}
                          className="Assessments-Action InTuneReportBtn"
                          title="Run InTune Report"
                          onClick={this.onDownloadInTuneReport}
                        >
                          <span className="AddAssessmentBtn-OptText">Run InTune&nbsp;</span>Report
                        </Button>
                      )}
                      {canAdd && isClientActive && (
                        <Button
                          color="success"
                          className="Assessments-Action AddAssessmentBtn"
                          title="Add New Assessment"
                          onClick={this.onAdd}
                          disabled={hideAddButton}
                          hasTip={hideAddButton}
                          tipText={hideAddButton ? "No permission" : ""}
                        >
                          Add New<span className="AddAssessmentBtn-OptText">&nbsp;Assessment</span>
                        </Button>
                      )}
                    </div>
                  </div>
                  <div className="Assessments-Filter">
                    <Row>
                      <Col md={6} lg={4}>
                        <SearchField
                          name="name"
                          value={ds.filter.name}
                          placeholder="Search"
                          onChange={this.onChangeFilterField}
                          onClear={this.onChangeFilterField}
                        />
                      </Col>
                    </Row>
                  </div>
                </div>
              );
            }}
            onRefresh={this.onRefresh}
          />
          {/* 完成的文件预览框 */}
          {isViewerOpen && (
            <AssessmentViewer
              isOpen
              assessmentId={selected?.id}
              assessmentTypeId={selected?.typeId}
              onView={this.onViewArchived}
              onClose={this.onCloseViewer}
            />
          )}
          {
            // 评估存档的预览
            isArchiveViewerOpen && (
              <AssessmentViewer
                isOpen
                isAssessmentArchived
                assessmentId={selectedArchived?.id}
                assessmentTypeId={selectedArchived?.typeId}
                onClose={this.onCloseArchiveViewer}
              />
            )
          }
          {
            // 编辑的open
            isEditorOpen && (
              <AssessmentEditor
                isOpen
                clientId={clientId}
                isCopying={isCopying}
                assessmentId={selected?.id}
                assessmentTypeId={preselectedType?.id}
                parentAssessmentId={selectedParent?.id}
                shouldAddNeedsToServicePlan={selected?.shouldAddNeedsToServicePlan}
                onClose={this.onCloseEditor}
                onSaveSuccess={this.onSaveSuccess}
                onCompleteSuccess={this.onCompleteSuccess}
                onChangeActivitySuccess={this.onChangeActivitySuccess}
              />
            )
          }
          <AssessmentVisibilityEditor
            isOpen={isVisibilityEditorOpen}
            clientId={clientId}
            assessmentId={selected?.id}
            assessmentStatus={selected?.status?.name}
            onClose={this.onCloseVisibilityEditor}
            onSaveSuccess={this.onChangeVisibilitySuccess}
          />
          {isEditCancelConfirmDialogOpen && (
            <ConfirmDialog
              isOpen
              icon={Warning}
              confirmBtnText="OK"
              title="The updates will not be saved"
              onConfirm={this.onCloseEditor}
              onCancel={this.onCloseEditCancelConfirmDialog}
            />
          )}
          {isCompleteSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title={
                "The assessment has been completed." +
                ([ARIZONA_SSM].includes(selected?.typeName) ? ` Score is ${selected?.score}` : "")
              }
              buttons={[
                {
                  outline: true,
                  text: "Close",
                  className: "min-width-170",
                  onClick: this.onCompleteSave,
                },
                {
                  text: "Back to assessment",
                  className: "min-width-170",
                  onClick: this.onBackToEditor,
                },
              ]}
            />
          )}
          {isSaveSuccessDialogOpen &&
            ([HMIS_ADULT_CHILD_INTAKE, HMIS_ADULT_CHILD_REASESSMENT, HMIS_ADULT_CHILD_REASESSMENT_EXIT].includes(
              selected?.typeName,
            ) ||
              selected?.typeName?.includes(COMPREHENSIVE)) && (
              <SuccessDialog
                isOpen
                title="The updates have been saved"
                buttons={[
                  {
                    outline: true,
                    text: "Close",
                    className: "min-width-170",
                    onClick: this.onCompleteSave,
                  },
                  {
                    text: "Back to assessment",
                    className: "min-width-170",
                    onClick: this.onBackToEditor,
                  },
                ]}
              />
            )}
          {isSaveSuccessDialogOpen && [IN_HOME, IN_HOME_CARE].includes(selected?.typeName) && (
            <SuccessDialog
              isOpen
              title={`The assessment has been completed. ${
                selected.notAddedToServicePlanNeedCount ? "Do you want to create/update a service plan?" : ""
              }`}
              buttons={
                selected.notAddedToServicePlanNeedCount
                  ? [
                      {
                        outline: true,
                        text: "No",
                        className: "min-width-170",
                        onClick: this.onCompleteSave,
                      },
                      {
                        text: "Yes",
                        className: "min-width-170",
                        onClick: () => {
                          this.onBackToEditor({ shouldAddNeedsToServicePlan: true });
                        },
                      },
                    ]
                  : [
                      {
                        text: "OK",
                        onClick: this.onCompleteSave,
                      },
                    ]
              }
            />
          )}
          {isSaveSuccessDialogOpen &&
            ![
              IN_HOME,
              HOUSING,
              IN_HOME_CARE,
              ARIZONA_SSM,
              COMPREHENSIVE,
              NOR_CAL_COMPREHENSIVE,
              HMIS_ADULT_CHILD_INTAKE,
              HMIS_ADULT_CHILD_REASESSMENT,
              HMIS_ADULT_CHILD_REASESSMENT_EXIT,
            ].includes(selected?.typeName) && (
              <SuccessDialog
                isOpen
                title="The assessment has been completed."
                buttons={[
                  {
                    text: "OK",
                    onClick: this.onCompleteSave,
                  },
                ]}
              />
            )}
          {isSaveSuccessDialogOpen && [HOUSING, ARIZONA_SSM].includes(selected?.typeName) && (
            <SuccessDialog
              isOpen
              title="The assessment has been saved."
              buttons={[
                {
                  outline: true,
                  text: "Close",
                  className: "min-width-170",
                  onClick: this.onCompleteSave,
                },
                {
                  text: "Back to assessment",
                  className: "min-width-170",
                  onClick: this.onBackToEditor,
                },
              ]}
            />
          )}
          {isChangeActivitySuccessDialogOpen && selected && (
            <SuccessDialog
              isOpen
              title={
                selected.isInactive ? "The assessment has been marked as inactive" : "The assessment is in process"
              }
              buttons={[
                {
                  text: "Ok",
                  onClick: this.onCompleteSave,
                },
              ]}
            />
          )}
          {isChangeVisibilitySuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title={`The assessment has been ${selected?.status?.name === HIDDEN ? "restored" : "hidden"}.`}
              buttons={[
                {
                  text: "Close",
                  onClick: () =>
                    this.setState({
                      selected: null,
                      isChangeVisibilitySuccessDialogOpen: false,
                    }),
                },
              ]}
            />
          )}
          {this.error && !isIgnoredError(this.error) && (
            <ErrorViewer isOpen error={this.error} onClose={this.onResetError} />
          )}
        </div>
      </DocumentTitle>
    );
  }
}

export default compose(
  withRouter,
  withAssessmentUtils,
  withDownloadingStatusInfoToast,
  connect(mapStateToProps, mapDispatchToProps),
  withQueryCache,
)(Assessments);
