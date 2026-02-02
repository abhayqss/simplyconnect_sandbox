import React, { useEffect, useState } from "react";
import { ReactComponent as Filter } from "images/filters.svg";
import { Button, Col, Collapse, Row, UncontrolledTooltip as Tooltip } from "reactstrap";
import cn from "classnames";
import TextField from "components/Form/TextField/TextField";
import SelectField from "components/Form/SelectField/SelectField";
import DateField from "components/Form/DateField/DateField";
import Table from "components/Table/Table";
import "./QAWorkflow.scss";
import EventPrimaryFilter from "../EventNotePrimaryFilter/EventNotePrimaryFilter";
import { useSelector } from "react-redux";
import service from "services/QAEventsService";
import downloadImg from "images/workflow/download.svg";
import feedbackImg from "images/workflow/feedback.svg";
import { ReactComponent as Timer } from "images/workflow/times.svg";
import { Link, useHistory, useParams } from "react-router-dom";
import { path } from "lib/utils/ContextUtils";
import moment from "moment/moment";
import clientWorkflowService from "services/ClientWorkflowService";
import { SurveyPDF } from "survey-pdf";
import { useAuthUser } from "../../../hooks/common";

const QAWorkflow = () => {
  const [isFilterOpen, setIsFilterOpen] = useState(true);
  const [isFetching, setIsFetching] = useState(false);
  const [qaWorkflowData, setQaWorkflowData] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [sort, setSort] = useState("");
  const [isFilter, setIsFilter] = useState(false);
  const history = useHistory();
  const { from } = useParams();
  const user = useAuthUser();
  const isQA = user.roleName === "ROLE_QUALITY_ASSURANCE_CODE";

  const { communityIds, organizationId } = useSelector((state) => state.event.note.composed.list.dataSource.filter);

  localStorage.setItem("triggerCurrentOrgId", organizationId);

  const [filterOptions, setFilterOptions] = useState({
    name: "",
    clientName: "",
    submitTime: "",
    status: "",
  });

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

  useEffect(() => {
    if (communityIds.length === 0 || !organizationId) {
      return;
    }

    setIsFetching(true);

    const params = {
      ...filterOptions,
      organizationId,
      communityIds,
      page,
      size,
      sort,
    };

    service.findAllQaEvents(params).then((res) => {
      setQaWorkflowData(res.data);
      setTotalCount(res.totalCount);
      setIsFetching(false);
    });
  }, [communityIds, organizationId, isFilter, page, sort]);

  const changeFilterOptions = (name, value) => {
    let newValue = value;
    if (name === "submitTime") {
      newValue = moment(value).valueOf();
    }
    let oldObj = { ...filterOptions };
    oldObj[name] = newValue;
    setFilterOptions(oldObj);
  };

  const onClearFilter = () => {
    setFilterOptions({
      name: "",
      clientName: "",
      submitTime: "",
      status: "",
    });
  };

  const onApplyFilter = () => {
    setIsFetching(true);
    setIsFilter(!isFilter);
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

  return (
    <>
      <EventPrimaryFilter />
      <div className="Workflow">
        <div className="Events-Title">
          <div className="flex-2">
            <div className="Events-TitleText">Workflow</div>
          </div>
          <Filter
            className={cn("Events-FilterSwitcher", isFilterOpen ? "EventFilter-Expanded" : "EventFilter-Collapsed")}
            onClick={() => setIsFilterOpen(!isFilterOpen)}
          />
          {!isQA && (
            <Button
              color={"success"}
              style={{ marginLeft: 15 }}
              onClick={() => {
                history.goBack();
              }}
            >
              Go Back
            </Button>
          )}
        </div>

        <Collapse isOpen={isFilterOpen}>
          <div className="EventFilter">
            <Row>
              <Col md={4}>
                <TextField
                  name="name"
                  value={filterOptions.name}
                  label="Workflow Name"
                  placeholder="Search by workflow name"
                  onChange={changeFilterOptions}
                />
              </Col>
              <Col md={4}>
                <TextField
                  name="clientName"
                  value={filterOptions.clientName}
                  label="Client"
                  placeholder="Search by description"
                  onChange={changeFilterOptions}
                />
              </Col>

              <Col md={4}>
                <DateField
                  name="submitTime"
                  value={Number(filterOptions.submitTime)}
                  label="Submission date*"
                  onChange={changeFilterOptions}
                />
              </Col>
              <Col md={4}>
                <SelectField
                  name="status"
                  value={filterOptions.status}
                  options={statusOptions}
                  label="Status"
                  placeholder="Please select status"
                  isMultiple={true}
                  onChange={changeFilterOptions}
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

        <div className="content">
          <Table
            hasHover
            hasPagination
            keyField="id"
            noDataText={isFilterOpen ? "No results." : "No records found"}
            isLoading={isFetching}
            className="ClientList"
            containerClass="ClientListContainer"
            data={qaWorkflowData}
            pagination={{ page: page + 1, size, totalCount }}
            onRefresh={(num) => {
              setPage(num - 1);
            }}
            columnsMobile={["workflowName", "workflowStatus"]}
            columns={[
              {
                dataField: "submissionTime",
                text: "Resubmission Date",
                sort: true,
                onSort,
                headerStyle: {
                  width: "150px",
                },
                formatter: (v) => {
                  return (
                    <div className={"workflow-table-text workflow-date workflow-name-overflow"}>
                      {v ? moment(v).format("MM/DD/YYYY HH:mm") : "-"}
                    </div>
                  );
                },
              },
              {
                dataField: "workflowName",
                text: "Workflow",
                sort: true,
                onSort,
                headerStyle: {
                  width: "240px",
                },
                formatter: (v, row) => {
                  return (
                    <div
                      id={row.id}
                      title={v}
                      className={"workflow-name workflow-name-overflow"}
                      onClick={() => {
                        localStorage.setItem("triggerCurrentComId", JSON.stringify(row.communityId));
                        if (from === "admin") {
                          history.push(path(`/admin-events/qa/feedback/${row.id}`));
                        } else {
                          history.push(path(`/qa/feedback/${row.id}`));
                        }
                      }}
                    >
                      {row.workflowName}
                    </div>
                  );
                },
              },
              {
                dataField: "clientName",
                text: "Client Name",
                headerStyle: {
                  width: "120px",
                },
                formatter: (v, row) => {
                  return <div className={" workflow-client-name workflow-name-overflow"}>{v}</div>;
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
                formatter: (v, row) => {
                  return <div className={"workflow-name-overflow"}>{v}</div>;
                },
              },
              {
                dataField: "communityName",
                text: "Communities",
                sort: true,
                onSort,
                headerStyle: {
                  width: "180px",
                },
                formatter: (v, row) => {
                  return (
                    <div title={v} className={"workflow-name-overflow workflow-community"}>
                      {v}
                    </div>
                  );
                },
              },
              {
                dataField: "submissionRecordSize",
                text: "Submit Times",
                headerStyle: {
                  width: "140px",
                },
                sort: true,
                onSort,
                formatter: (v, row) => {
                  return (
                    <>
                      <span>{row.submissionRecordSize}</span>
                      {row.submissionRecordSize ? (
                        <>
                          <Timer style={{ marginLeft: 10 }} id={`submit-${row.id}`}></Timer>

                          <Tooltip
                            trigger="focus"
                            placement="top"
                            target={`submit-${row.id}`}
                            className={"DropzoneField-BrowserPopup"}
                            innerClassName={"tooltips-box-show-inner"}
                          >
                            <div className="tooltips-box-show">
                              {row.submissionRecord.map((item, index) => {
                                return (
                                  <div className={"qa-workflow-tooltip-item"} key={index}>
                                    {item.workflowStatus} date: {moment(item.lastModifiedDate).format("MM/DD HH:mm")}
                                  </div>
                                );
                              })}
                            </div>
                          </Tooltip>
                        </>
                      ) : (
                        ""
                      )}
                    </>
                  );
                },
              },
              {
                dataField: "createdBy",
                text: "Created By",
                headerStyle: {
                  width: "160px",
                },
                formatter: (v) => {
                  return (
                    <div className={"workflow-table-text  workflow-create-name"} title={v?.fullName}>
                      {v?.fullName || "-"}
                    </div>
                  );
                },
              },
              {
                dataField: "approvalTime",
                text: "Approval Date",
                sort: true,
                headerStyle: {
                  width: "180px",
                },
                onSort,
                formatter: (v) => {
                  return (
                    <div className={"workflow-table-text workflow-date"}>
                      {v ? moment(v).format("MM/DD/YYYY HH:mm") : "-"}
                    </div>
                  );
                },
              },
              {
                dataField: "workflowStatus",
                text: "Status",
                headerStyle: {
                  width: "140px",
                },
                formatter: (v) => {
                  return (
                    <>
                      {v === "PENDING" && <div className="line-workflow-status pending-status">Pending</div>}
                      {v === "SUBMITTED" && <div className="line-workflow-status submit-status">Submitted</div>}
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
                  width: "100px",
                },
                align: "right",
                formatter: (v, row) => {
                  return (
                    <div className={"qaWorkflowAction"}>
                      {row.workflowStatus !== "PENDING" &&
                        row.workflowStatus !== "INPROCESS" &&
                        row.workflowStatus !== "FEEDBACK" && (
                          <img src={downloadImg || ""} alt="" onClick={() => downloadWorkflow(row)} />
                        )}

                      {row?.canApprove && (
                        <Link
                          id={row.id}
                          to={path(`/qa/feedback/${row.id}`)}
                          className={cn("DocumentList-FolderTitleText", "DocumentList-Link")}
                        >
                          <img src={feedbackImg || ""} alt="" />
                        </Link>
                      )}
                    </div>
                  );
                },
              },
            ]}
          />
        </div>
      </div>
    </>
  );
};

export default QAWorkflow;
