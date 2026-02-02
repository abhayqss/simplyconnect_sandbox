import React, { memo, useEffect, useState } from "react";

import { ReactComponent as Timer } from "images/Event/timer.svg";
import "./Workflow.scss";

import PTypes from "prop-types";

import cn from "classnames";

import { compact } from "underscore";

import DocumentTitle from "react-document-title";
import { Link, useParams } from "react-router-dom";
import { Badge, Col, Row, UncontrolledTooltip as Tooltip } from "reactstrap";
import { Breadcrumbs, ErrorViewer, Table } from "components";
import { Button } from "components/buttons";
import { useAuthUser, useListState, useQueryInvalidation } from "hooks/common";
import { useSideBarUpdate } from "hooks/business/client";
import { useClientQuery } from "hooks/business/client/queries";
import { useCanViewEventsAndNotesQuery } from "hooks/business/client/events";

import { toNumberExcept } from "lib/utils/Utils";
import TextField from "components/Form/TextField/TextField";
import moment from "moment";
import { path } from "../../../../lib/utils/ContextUtils";
import feedbackImg from "images/workflow/feedback.svg";
import editImg from "images/workflow/edit.svg";
import downloadImg from "images/workflow/download.svg";
import AddWorkflowModal from "components/Events/AddWorkflowModal/AddWorkflowModal";
import clientWorkflowService from "services/ClientWorkflowService";
import { SurveyPDF } from "survey-pdf";
import workflowService from "../../../../services/WorkflowService";
import { ReactComponent as DeleteImg } from "images/workflow/deleteImg.svg";
import { WarningDialog } from "../../../../components/dialogs";
import initSurveyCustomComponent, {
  uninstallSurveyCustomComponent,
} from "../../../Admin/Workflow/WorkflowManagementCreate/CustomComponent/initSurveyCustomComponent";

import "survey-core/defaultV2.min.css";
import { useQueryClient } from "@tanstack/react-query";

const Workflow = ({ className }) => {
  const queryClient = useQueryClient();
  const user = useAuthUser();
  const [page, setPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [isFetching, setIsFetching] = useState(false);
  const [workflowList, setWorkflowList] = useState([]);
  const [workflowSearchName, setWorkflowSearchName] = useState(null);
  const [sort, setSort] = useState("");
  const [isAddWorkflowModalShow, setIsAddWorkflowModalShow] = useState(false);
  const [communityIds, setCommunityIds] = useState([]);
  const [organizationId, setOrganizationId] = useState();

  const params = useParams();
  const invalidate = useQueryInvalidation();
  const clientId = toNumberExcept(params.clientId, [null, undefined]);
  const { state, setError, clearError } = useListState({});
  const { data: client } = useClientQuery({ clientId }, { staleTime: 0 });
  const updateSideBar = useSideBarUpdate({ clientId });
  const { data: canViewEventsAndNotes } = useCanViewEventsAndNotesQuery({ clientId });

  const [isRefreshData, setIsRefreshData] = useState(false);

  const [currentId, setCurrentId] = useState("");

  const [waringDialog, setWaringDialog] = useState(false);

  const isQA = JSON.parse(localStorage.getItem("AUTHENTICATED_USER")).roleName === "ROLE_QUALITY_ASSURANCE_CODE";

  useEffect(() => {
    initSurveyCustomComponent();

    return () => {
      uninstallSurveyCustomComponent();
    };
  }, []);

  useEffect(() => {
    if (client) {
      setCommunityIds([client.communityId]);
      setOrganizationId(client.organizationId);
    }
  }, [client]);

  useEffect(() => {
    setIsFetching(true);

    getWorkflowList({
      page,
      size: 12,
      clientId,
      workflowName: workflowSearchName,
      sort,
    });
  }, [page, clientId, workflowSearchName, sort, isRefreshData]);

  const getWorkflowList = (params) => {
    clientWorkflowService.adminFindClientWorkflow(params).then((res) => {
      if (res.success) {
        setIsFetching(false);
        setWorkflowList(res?.data);
        setTotalCount(res?.totalCount);
      }
    });
  };

  useEffect(() => {
    updateSideBar();
  }, [updateSideBar]);

  const onChangeWorkflowSearchName = (filed, value) => {
    setWorkflowSearchName(value);
    setPage(1);
  };

  const onSort = (field, order) => {
    setSort(`${field},${order}`);
  };

  const downloadWorkflow = (row) => {
    //    调取接口，获取问题和答案
    const params = {
      clientWorkflowId: row.id,
    };
    clientWorkflowService.findClientWorkflowDetail(params).then(async (res) => {
      const resultJson = res.data.resultContent;
      const answerJson = res.data.templateContent;

      const pdfDocOptions = {
        fontSize: 12,
      };

      const savePdf = function () {
        const surveyPdf = new SurveyPDF(answerJson, pdfDocOptions);

        // 下载的pdf 是否能编辑
        surveyPdf.mode = "display";
        surveyPdf.data = JSON.parse(resultJson);
        surveyPdf.save(row.workflowName);
      };
      savePdf();
    });
  };

  const deleteWorkflow = (id) => {
    setIsFetching(true);
    workflowService
      .deleteWorkflowTemplate(id)
      .then(() => {
        setIsRefreshData(!isRefreshData);
        setIsFetching(false);

        queryClient.invalidateQueries(["Client.Workflow.Count", { clientId }]);
      })
      .catch(() => {
        setIsFetching(false);
      });
  };

  const columns = [
    {
      dataField: "submissionTime",
      text: "Resubmission Date",
      sort: true,
      onSort,
      headerStyle: {
        width: "15%",
      },
      headerClasses: "hide-on-tablet",
      classes: "hide-on-tablet",
      formatter: (v, row) => {
        return (
          <div className={"workflow-table-text workflow-date"}>{v ? moment(v).format("MM/DD/YYYY HH:mm") : "-"}</div>
        );
      },
    },
    {
      dataField: "workflowName",
      text: "Workflow",
      sort: true,
      onSort,
      formatter: (v, row) => {
        const isFillNowFlag =
          row.fillType === "FILLNOW" && row.workflowStatus !== "SUBMITTED" && row.workflowStatus !== "APPROVED";
        return (
          <Link
            id={row.id}
            to={path(`/clients/${clientId}/workflow/${row.id}/${row.clientId}/preview?FN=${isFillNowFlag}`)}
            className={cn("DocumentList-FolderTitleText", "DocumentList-Link")}
          >
            <div className={"workflow-name workflow-table-text"} title={v}>
              {v}
            </div>
          </Link>
        );
      },
    },
    {
      dataField: "score",
      text: "Score",
      align: "right",
      headerAlign: "right",
    },
    {
      dataField: "submissionRecordSize",
      text: "Submit Times",
      align: "center",
      headerAlign: "center",
      formatter: (v, row) => {
        return (
          <div className={"workflow-table-text"}>
            {v}
            {v > 0 && (
              <>
                <Timer className="workflow-timer-Icon" id={`workflow_line_timer_${row.id}`} />
                <Tooltip
                  trigger="focus"
                  placement="top"
                  autohide={false}
                  className={"DropzoneField-BrowserPopup"}
                  innerClassName={"tooltips-box-show-inner"}
                  target={`workflow_line_timer_${row.id}`}
                >
                  <div className={"tooltip-content"}>
                    {row.stepRecorder?.map((item, index) => {
                      return (
                        <div className="tooltips-box-show-item  workflow-date" key={index}>
                          {item?.stepName} &nbsp;
                          {moment(item?.operationTime).format("MM/DD HH:mm")}&nbsp;({item?.contactName})
                        </div>
                      );
                    })}
                  </div>
                </Tooltip>
              </>
            )}
          </div>
        );
      },
    },
    {
      dataField: "createdBy",
      text: "Created By",
      headerClasses: "hide-on-tablet",
      classes: "hide-on-tablet",
      formatter: (v) => {
        return (
          <div className={"workflow-table-text workflow-create-name"} title={v.fullName}>
            {v?.fullName || "-"}
          </div>
        );
      },
    },
    {
      dataField: "approvalTime",
      text: "Approval Date",
      sort: true,
      onSort,
      headerClasses: "hide-on-tablet",
      classes: "hide-on-tablet",
      formatter: (v) => {
        return (
          <div className={"workflow-table-text workflow-date"}>{v ? moment(v).format("MM/DD/YYYY HH:mm") : "-"}</div>
        );
      },
    },
    {
      dataField: "workflowStatus",
      text: "Status",
      formatter: (v) => {
        return (
          <>
            {v === "SUBMITTED" && <div className="line-workflow-status submit-status">Submitted</div>}
            {v === "PENDING" && <div className="line-workflow-status pending-status">Pending</div>}
            {v === "INPROCESS" && <div className="line-workflow-status in-progress-status">In Process</div>}
            {v === "FEEDBACK" && <div className=" line-workflow-status feed-back-status">Feedback</div>}
            {v === "APPROVED" && <div className=" line-workflow-status approved-status">Approved</div>}
          </>
        );
      },
    },
    {
      dataField: "@actions",
      text: "",
      align: "right",
      formatter: (v, row) => {
        const isFillNowFlag =
          row.fillType === "FILLNOW" && row.workflowStatus !== "SUBMITTED" && row.workflowStatus !== "APPROVED";
        return (
          <div className={"qaWorkflowAction"}>
            {row.workflowStatus !== "PENDING" &&
              row.workflowStatus !== "INPROCESS" &&
              row.workflowStatus !== "FEEDBACK" && (
                <img src={downloadImg || ""} alt="" onClick={() => downloadWorkflow(row)} />
              )}

            {user.roleTitle === "Quality Assurance" &&
              row.workflowStatus !== "PENDING" &&
              row.workflowStatus !== "INPROCESS" &&
              row.workflowStatus !== "FEEDBACK" &&
              row.workflowStatus !== "APPROVED" && (
                <Link
                  id={row.id}
                  to={path(`/clients/${clientId}/workflow/${row.id}/${row.clientId}/feedback?FN=${isFillNowFlag}`)}
                  className={cn("DocumentList-FolderTitleText", "DocumentList-Link")}
                >
                  <img src={feedbackImg || ""} alt="" />
                </Link>
              )}
            {user.roleTitle !== "Quality Assurance" && (isFillNowFlag || row.canFill) && (
              <Link
                id={row.id}
                to={path(
                  `/clients/${clientId}/workflow/${row.id}/${row.clientId}/edit?FN=${row.canFill || isFillNowFlag}`,
                )}
                className={cn("DocumentList-FolderTitleText", "DocumentList-Link")}
              >
                <img src={editImg || ""} alt="" />
              </Link>
            )}

            {row.canDelete && (
              <DeleteImg
                onClick={() => {
                  setCurrentId(row.id);
                  setWaringDialog(true);
                }}
                style={{ cursor: "pointer", marginLeft: 10 }}
              />
            )}
          </div>
        );
      },
    },
  ];
  const onRefresh = (number) => {
    setPage(number);
  };

  const onAddWorkflow = () => {
    setIsAddWorkflowModalShow(true);
  };
  const onAddWorkflowCancel = () => {
    setIsAddWorkflowModalShow(false);
  };

  const onAddWorkflowSuccess = () => {
    setIsAddWorkflowModalShow(false);
    if (page === 1) {
      getWorkflowList({
        communityId: communityIds,
        organizationId,
        page: 1,
        size: 9,
        workflowName: workflowSearchName,
        sort,
      });
    } else {
      setPage(1);
    }
  };
  return (
    <>
      <DocumentTitle title="Simply Connect | Client Expenses">
        <div className={cn("Workflow_Client", className)}>
          <Breadcrumbs
            items={compact([
              {
                title: "Clients",
                href: "/clients",
                isEnabled: true,
              },
              client && { title: client.fullName || "", href: `/clients/${clientId}` },
              {
                title: "Workflow",
                href: `/clients/${clientId}/workflow`,
                isActive: true,
              },
            ])}
            className="margin-bottom-32"
          />

          {client && (
            <div className="Workflow-Header page-header">
              <div className="Workflow-HeaderItem page-header-item">
                <div className="Workflow-Title page-title">
                  <div className="Workflow-TitleText page-title-text">
                    <div className="page-title-main-text">Workflow&nbsp;</div>
                    <div className="page-title-second-text">/ {client.fullName}</div>
                  </div>
                  {totalCount > 0 && (
                    <Badge color="info" className="Badge Badge_place_top-right">
                      {totalCount}
                    </Badge>
                  )}
                </div>
              </div>
              <div className="Workflow-HeaderItem page-header-item">
                <div className="Workflow-Actions page-actions">
                  {canViewEventsAndNotes && !isQA && (
                    <Button
                      color="success"
                      // disabled={!user?.hieAgreement}
                      id="add-expense"
                      onClick={onAddWorkflow}
                      className="Workflow-Action"
                    >
                      Create Workflow
                    </Button>
                  )}
                </div>
              </div>
            </div>
          )}

          {state.error && <ErrorViewer isOpen error={state.error} onClose={clearError} />}

          <Table
            hasHover
            hasOptions
            hasPagination
            keyField="id"
            title="Workflow"
            noDataText="No data."
            isLoading={isFetching}
            className="WorkflowList"
            containerClass="ClientWorkflowListListContainer"
            data={workflowList}
            pagination={{ page: page, size: 12, totalCount: totalCount }}
            columns={columns}
            columnsMobile={["workflowName", "workflowStatus"]}
            onRefresh={onRefresh}
            renderCaption={(title) => (
              <Row>
                <Col xl={6} sm={12} md={6} lg={6}>
                  <TextField
                    name="workflowName"
                    value={workflowSearchName}
                    label="Workflow Name"
                    placeholder="Search by workflow name"
                    onChange={onChangeWorkflowSearchName}
                  />
                </Col>
              </Row>
            )}
          />

          <AddWorkflowModal
            isOpen={isAddWorkflowModalShow}
            onCancel={onAddWorkflowCancel}
            onConfirm={onAddWorkflowSuccess}
            onClose={onAddWorkflowCancel}
            communityIds={communityIds}
            organizationId={organizationId}
            adminClientId={clientId}
          />
        </div>
      </DocumentTitle>

      {
        <WarningDialog
          isOpen={waringDialog}
          toggle={() => setWaringDialog(!waringDialog)}
          title="Are you sure you want to delete this workflow?"
          buttons={[
            {
              text: "Cancel",
              color: "outline-success",
              onClick: () => {
                setWaringDialog(!waringDialog);
                setCurrentId("");
              },
            },
            {
              text: "OK",
              onClick: () => {
                setWaringDialog(!waringDialog);
                deleteWorkflow(currentId);
              },
            },
          ]}
          onCancel={() => setWaringDialog(!waringDialog)}
        />
      }
    </>
  );
};

Workflow.propTypes = {
  className: PTypes.string,
};

export default memo(Workflow);
