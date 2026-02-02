import React, { useEffect, useState } from "react";
import { ReactComponent as Filter } from "images/filters.svg";
import { Button, Col, Collapse, Row, UncontrolledTooltip as Tooltip } from "reactstrap";
import cn from "classnames";
import TextField from "components/Form/TextField/TextField";
import SelectField from "components/Form/SelectField/SelectField";
import DateField from "components/Form/DateField/DateField";
import Table from "components/Table/Table";
import "./WorkflowClientLeft.scss";
import clientWorkflowService from "../../../../services/ClientWorkflowService";
import { useSelector } from "react-redux";
import { ReactComponent as Timer } from "images/Event/timer.svg";
import { useHistory } from "react-router-dom";
import { path } from "lib/utils/ContextUtils";
import moment from "moment";
import { useAuthUser } from "hooks/common/redux";
import { EditButton } from "components/buttons";

const WorkflowClientLeft = (props) => {
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [isFetching, setIsFetching] = useState(false);
  const [workflowData, setWorkflowData] = useState([]);
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(10);
  const [dataTotal, setDataTotal] = useState(0);
  const [workflowSearchName, setWorkflowSearchName] = useState(null);
  const [resubmissionDate, setResubmissionDate] = useState(null);
  const [createdDate, setCreatedDate] = useState(null);
  const [searchStatus, setSearchStatus] = useState(null);
  const [approvalDate, setApprovalDate] = useState(null);
  const [filterParams, setFilterParams] = useState({});
  const history = useHistory();
  const user = useAuthUser();
  const isPOA = user?.roleName === "ROLE_POA_CODE";

  const { communityIds, organizationId } = useSelector((state) => state.event.note.composed.list.dataSource.filter);

  const statusOptions = [
    {
      value: "SUBMITTED",
      text: "Submitted",
    },
    {
      value: "INPROCESS",
      text: "In Process",
    },
    {
      value: "FEEDBACK",
      text: "Feedback",
    },
    {
      value: "APPROVED",
      text: "Approved",
    },
  ];

  const clientColumns = [
    {
      dataField: "createTime",
      text: "Created Date",
      sort: true,
      formatter: (v) => {
        return (
          <div className={"workflow-date workflow-table-text"}>{v ? moment(v)?.format("MM/DD/YYYY HH:mm") : "-"}</div>
        );
      },
    },
    {
      dataField: "workflowName",
      text: "Workflow",
      sort: true,
      formatter: (v, row) => {
        return (
          <>
            {row.workflowStatus === "PENDING" && (
              <div
                title={v}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 1)}
                className={"workflow-name workflow-table-text"}
              >
                {v}
              </div>
            )}
            {row.workflowStatus === "INPROCESS" && (
              <div
                title={v}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 1)}
                className={"workflow-name workflow-table-text"}
              >
                {v}
              </div>
            )}
            {row.workflowStatus === "SUBMITTED" && (
              <div
                title={v}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 0)}
                className={"workflow-name  workflow-table-text"}
              >
                {v}
              </div>
            )}
            {row.workflowStatus === "FEEDBACK" && (
              <div
                title={v}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 1)}
                className={"workflow-name workflow-table-text"}
              >
                {v}
              </div>
            )}
            {row.workflowStatus === "APPROVED" && (
              <div
                title={v}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 0)}
                className={"workflow-name workflow-table-text"}
              >
                {v}
              </div>
            )}
          </>
        );
      },
    },
    {
      dataField: "score",
      text: "Score",
      headerStyle: {
        width: "80px",
      },
      align: "right",
      headerAlign: "right",
    },
    {
      dataField: "submissionTime",
      text: "Resubmission Date",
      // headerStyle: {
      //   width: "150px",
      // },
      formatter: (v) => {
        return (
          <div className={"workflow-table-text workflow-date"}>{v ? moment(v)?.format("MM/DD/YYYY HH:mm") : "-"}</div>
        );
      },
    },
    {
      dataField: "submissionRecordSize",
      text: "Submit Times",
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
                  className={"DropzoneField-BrowserPopup"}
                  target={`workflow_line_timer_${row.id}`}
                >
                  {row.stepRecorder?.map((item, index) => {
                    return (
                      <div className="workflow-date" key={index}>
                        {item.stepName}
                        &nbsp;{moment(item.operationTime).format("MM/DD HH:mm")}
                        &nbsp;({item.contactName})
                      </div>
                    );
                  })}
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
      // headerStyle: {
      //   width: "160px",
      // },
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
  ];
  const POAColumns = [
    {
      dataField: "createTime",
      text: "Created Date",
      sort: true,
      formatter: (v) => {
        return (
          <div className={"workflow-date workflow-table-text"}>{v ? moment(v)?.format("MM/DD/YYYY HH:mm") : "-"}</div>
        );
      },
    },
    {
      dataField: "workflowName",
      text: "Workflow",
      sort: true,
      formatter: (v, row) => {
        return (
          <>
            {row.workflowStatus === "PENDING" && (
              <div
                title={v}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 0)}
                className={"workflow-name workflow-table-text"}
              >
                {v}
              </div>
            )}
            {row.workflowStatus === "INPROCESS" && (
              <div
                title={v}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 0)}
                className={"workflow-name workflow-table-text"}
              >
                {v}
              </div>
            )}
            {row.workflowStatus === "SUBMITTED" && (
              <div
                title={v}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 0)}
                className={"workflow-name  workflow-table-text"}
              >
                {v}
              </div>
            )}
            {row.workflowStatus === "FEEDBACK" && (
              <div
                title={v}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 0)}
                className={"workflow-name workflow-table-text"}
              >
                {v}
              </div>
            )}
            {row.workflowStatus === "APPROVED" && (
              <div
                title={v}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 0)}
                className={"workflow-name workflow-table-text"}
              >
                {v}
              </div>
            )}
          </>
        );
      },
    },
    {
      dataField: "score",
      text: "Score",
      headerStyle: {
        width: "80px",
      },
      align: "right",
      headerAlign: "right",
    },
    {
      dataField: "submissionTime",
      text: "Resubmission Date",
      formatter: (v) => {
        return (
          <div className={"workflow-table-text workflow-date"}>{v ? moment(v)?.format("MM/DD/YYYY HH:mm") : "-"}</div>
        );
      },
    },
    {
      dataField: "submissionRecordSize",
      text: "Submit Times",
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
                  className={"DropzoneField-BrowserPopup"}
                  target={`workflow_line_timer_${row.id}`}
                >
                  {row.stepRecorder?.map((item, index) => {
                    return (
                      <div className="workflow-date" key={index}>
                        {item.stepName}
                        &nbsp;{moment(item.operationTime).format("MM/DD HH:mm")}
                        &nbsp;({item.contactName})
                      </div>
                    );
                  })}
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
      headerStyle: {
        width: "60px",
      },
      align: "right",
      formatter: (v, row) => {
        return (
          <>
            {row?.canFill && (
              <EditButton
                id={`edit-workflow-${row.id}-btn`}
                onClick={() => goWorkflowDetail(row.id, row.clientId, 1)}
                tipText="Edit"
                className="ContactList-Action"
              />
            )}
          </>
        );
      },
    },
  ];

  useEffect(() => {
    setIsFetching(true);

    if (communityIds.length === 0 || !organizationId) {
      setIsFetching(false);
      return;
    }
    getWorkflowList({
      page: page,
      size: 10,
      organizationId,
      communityId: communityIds,
      filter: filterParams,
    });
  }, [organizationId, communityIds, filterParams, page]);
  const getWorkflowList = (params) => {
    clientWorkflowService.findWorkflowForClient(params).then((res) => {
      if (res.success) {
        setIsFetching(false);
        setWorkflowData(res.data);
        setDataTotal(res.totalCount);
      } else {
        setIsFetching(false);
      }
    });
  };

  const goWorkflowDetail = (id, clientId, canEdit) => {
    history.push(path(`/cl/workflow/${id}/${clientId}/${canEdit}`));
  };

  const onToggleFilter = () => {
    setIsFilterOpen(!isFilterOpen);
  };

  const onChangeWorkflowName = (name, value) => {
    setWorkflowSearchName(value);
  };

  const onChangeCreateDate = (name, value) => {
    if (value) {
      setCreatedDate(new Date(value).getTime());
    } else {
      setCreatedDate(null);
    }
  };

  const onChangeResubmissionDate = (name, value) => {
    if (value) {
      setResubmissionDate(new Date(value).getTime());
    } else {
      setResubmissionDate(value);
    }
  };

  const onChangeStatus = (name, value) => {
    setSearchStatus(value);
  };

  const onChangeApprovalDate = (name, value) => {
    if (value) {
      setApprovalDate(new Date(value).getTime());
    } else {
      setApprovalDate(value);
    }
  };

  const onClearFilter = () => {
    setCreatedDate(null);
    setSearchStatus(null);
    setApprovalDate(null);
    setResubmissionDate(null);
    setWorkflowSearchName(null);
    setFilterParams({});
  };

  const onApplyFilter = () => {
    if (!workflowSearchName && !createdDate && !resubmissionDate && !searchStatus && !approvalDate) {
    } else {
      setFilterParams({
        workflowName: workflowSearchName,
        createdDate: createdDate,
        resubmissionDate: resubmissionDate,
        status: searchStatus,
        approvalDate: approvalDate,
      });
    }
  };

  return (
    <>
      <div className="Workflow">
        <Table
          hasHover
          hasOptions
          hasPagination
          keyField="id"
          title="Workflow"
          noDataText={isFilterOpen ? "No data." : "No workflow found"}
          isLoading={isFetching}
          className="ClientList"
          containerClass="ClientListContainer"
          data={workflowData}
          pagination={{ page, size, totalCount: dataTotal }}
          columns={isPOA ? POAColumns : clientColumns}
          columnsMobile={["workflowName", "status"]}
          onRefresh={(num) => {
            setPage(num);
          }}
          renderCaption={(title) => {
            return (
              <>
                <div className="Workflow-Title">
                  <div className="">
                    <div className="Workflow-TitleText">{title}</div>
                  </div>
                  <div className="text-right">
                    <Filter
                      className={cn(
                        "Workflow-FilterSwitcher",
                        isFilterOpen ? "WorkflowFilter-Expanded" : "WorkflowFilter-Collapsed",
                      )}
                      onClick={onToggleFilter}
                    />
                  </div>
                </div>
                <Collapse isOpen={isFilterOpen}>
                  <div className="WorkflowFilter">
                    <Row>
                      <Col md={4}>
                        <TextField
                          name="workflowName"
                          value={workflowSearchName}
                          label="Workflow Name"
                          placeholder="Search by workflow name"
                          onChange={onChangeWorkflowName}
                        />
                      </Col>
                      <Col md={4}>
                        <DateField
                          name="createdDate"
                          value={createdDate}
                          label="Created date"
                          onChange={onChangeCreateDate}
                          maxDate={Date.now()}
                        />
                      </Col>
                      <Col md={4}>
                        <DateField
                          name="resubmissionDate"
                          value={resubmissionDate}
                          label="Resubmission Date"
                          onChange={onChangeResubmissionDate}
                          maxDate={Date.now()}
                        />
                      </Col>
                      <Col md={4}>
                        <SelectField
                          name="status"
                          value={searchStatus}
                          options={statusOptions}
                          label="Status"
                          placeholder="Please select status"
                          isMultiple={true}
                          onChange={onChangeStatus}
                        />
                      </Col>
                      <Col md={4}>
                        <DateField
                          name="approvalDate"
                          value={approvalDate}
                          label="Approval Date"
                          onChange={onChangeApprovalDate}
                          maxDate={Date.now()}
                        />
                      </Col>
                      <Col md={4}>
                        <div style={{ height: "30px" }} />
                        <Button outline color="success" size="sm" onClick={onClearFilter}>
                          Clear
                        </Button>{" "}
                        <Button color="success" size="sm" onClick={onApplyFilter}>
                          Apply
                        </Button>
                      </Col>
                    </Row>
                  </div>
                </Collapse>
              </>
            );
          }}
        />
      </div>
    </>
  );
};

export default WorkflowClientLeft;
