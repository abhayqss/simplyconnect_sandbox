import "./WorkflowShowDetailItem.scss";
import moment from "moment/moment";
import { ReactComponent as Timer } from "images/Event/timer.svg";
import React, { useState } from "react";
import WorkflowQuestionNumList from "../WorkflowQuestionNumList/WorkflowQuestionNumList";
import { UncontrolledTooltip as Tooltip } from "reactstrap";
import { path } from "lib/utils/ContextUtils";
import { useHistory } from "react-router-dom";
import { ReactComponent as DeleteImg } from "images/workflow/delete.svg";
import workflowService from "../../../services/WorkflowService";
import { WarningDialog } from "../../../components/dialogs";

const data = {
  fullName: "!Artem Mironov",
  workflowNum: 1,
  workFlowName: "Beneficiary Colorectal Cancer Screening",
  process: [
    {
      allQuestion: 14,
      save: 3,
      status: "pending",
    },
  ],
};
const WorkflowShowDetailItem = (props) => {
  const { workflowData, setIsRefreshData, isRefreshData, isFetching, setIsFetching } = props;
  const [currentId, setCurrentId] = useState("");

  const [waringDialog, setWaringDialog] = useState(false);

  const history = useHistory();
  const goDetailPage = (id, clientId) => {
    history.push(path(`/admin-events/workflowDetail/${id}/null/${clientId}?FC=false`));
  };

  const getWorkflowStatus = (status) => {
    switch (status) {
      case "SUBMITTED":
        return "Submitted";
      case "APPROVED":
        return "Approved";
      case "FEEDBACK":
        return "Feedback";
      case "PENDING":
        return "Pending";
      case "INPROCESS":
        return "In Process";
    }
  };

  const getFeedbackNumber = (list) => {
    return list.filter((item) => item.hasFeedback === true).length;
  };

  const deleteWorkflow = (clientWorkflowId) => {
    setIsFetching(true);
    workflowService
      .deleteWorkflowTemplate(clientWorkflowId)
      .then(() => {
        setIsFetching(false);
        setIsRefreshData(!isRefreshData);
      })
      .catch(() => {
        setIsFetching(false);
      });
  };

  return (
    <>
      {workflowData.map((workflowItem) => {
        return (
          <div className="submitted line-item" key={workflowItem.id}>
            <div className={"line-center"}>
              <div className="lineCenterLeft">
                <div
                  className="line-workflow-name-box"
                  title={workflowItem.assessmentName}
                  onClick={() => {
                    localStorage.setItem("triggerCurrentComId", workflowItem.communityId);
                    goDetailPage(workflowItem.id, workflowItem.clientId);
                  }}
                  style={{ cursor: "pointer" }}
                >
                  <div className={"line-workflow-name"}> {workflowItem.assessmentName}</div>
                  <div className={"line-workflow-created-by"}>
                    Created by: {workflowItem?.createdBy?.fullName || "_"}
                  </div>
                </div>

                <div className="lineCenterRight">
                  <div className="line-workflow-timer">
                    {moment(workflowItem.submissionTime)?.format("MM/DD HH:mm") === "Invalid date"
                      ? "-"
                      : moment(workflowItem.submissionTime)?.format("MM/DD HH:mm")}
                    {workflowItem.workflowStatus !== "PENDING" && workflowItem?.stepRecorder && (
                      <Timer className="timer-Icon" id={`workflowName_timer_${workflowItem.id}`} />
                    )}

                    {workflowItem.workflowStatus !== "PENDING" && workflowItem?.stepRecorder && (
                      <Tooltip
                        trigger="focus"
                        placement="top"
                        className={"DropzoneField-BrowserPopup"}
                        innerClassName={"tooltips-box-show-inner"}
                        target={`workflowName_timer_${workflowItem.id}`}
                      >
                        <div className="tooltips-box-show">
                          {workflowItem?.stepRecorder?.map((item, index) => {
                            return (
                              <div className="timer-item" key={index}>
                                {item.stepName}&nbsp;
                                {moment(item.operationTime)?.format("MM/DD HH:mm")}
                                &nbsp;({item.contactName})
                              </div>
                            );
                          })}
                        </div>
                      </Tooltip>
                    )}
                  </div>

                  <div className="status">
                    <div className={`line-workflow-status submit-status-${workflowItem.workflowStatus}`}>
                      {getWorkflowStatus(workflowItem.workflowStatus)}
                      {getFeedbackNumber(workflowItem.list) !== 0 && (
                        <span className={"feedbackNum"}>({getFeedbackNumber(workflowItem.list)})</span>
                      )}
                    </div>
                  </div>
                </div>
              </div>

              <div className="line-workflow-question-list">
                <div className="line-workflow-question-list-box">
                  <WorkflowQuestionNumList
                    data={workflowItem?.questionAmount}
                    endData={data.process[0].allQuestion.save}
                    workflowNameId={workflowItem.id}
                    list={workflowItem?.list}
                    workflowData={workflowItem}
                  />
                </div>
                {workflowItem.canDelete && (
                  <div
                    style={{
                      width: 26,
                      height: 26,
                      minWidth: 26,
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                      cursor: "pointer",
                    }}
                    onClick={() => {
                      setCurrentId(workflowItem.id);
                      setWaringDialog(true);
                    }}
                  >
                    {workflowItem.canDelete && <DeleteImg style={{ width: 26, height: 26 }} />}
                  </div>
                )}
              </div>
            </div>
          </div>
        );
      })}

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

export default WorkflowShowDetailItem;
