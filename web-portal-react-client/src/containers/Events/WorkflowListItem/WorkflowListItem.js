import "./WorkflowListItem.scss";
import cn from "classnames";
import React, { useState } from "react";
import { ReactComponent as Dropper } from "images/Event/drop.svg";
import { ReactComponent as Tips } from "images/Event/tip.svg";
import { Collapse } from "reactstrap";
import WorkflowShowDetailItem from "../WorkflowShowDetailItem/WorkflowShowDetailItem";
import WorkflowQuestionNumList from "../WorkflowQuestionNumList/WorkflowQuestionNumList";
import { useHistory } from "react-router-dom";
import { path } from "lib/utils/ContextUtils";
import Avatar from "../../Avatar/Avatar";
import { ReactComponent as DeleteImg } from "images/workflow/delete.svg";
import workflowService from "../../../services/WorkflowService";
import { WarningDialog } from "../../../components/dialogs";

const WorkflowListItem = (props) => {
  const { data, isRefreshData, setIsRefreshData, setIsFetching, isFetching } = props;

  const [currentId, setCurrentId] = useState("");

  const [waringDialog, setWaringDialog] = useState(false);

  const [dropperOpen, setDropperOpen] = useState(false);
  const history = useHistory();

  const onToggleDropper = () => {
    setDropperOpen(!dropperOpen);
  };

  const getSigningProgress = (all, hasSign) => {
    return Math.round((hasSign / all) * 10000) / 100 + "%";
  };

  const getWorkflowNameList = (workflows) => {
    return workflows.map((workflow) => workflow?.assessmentName).join("|");
  };

  const showWorkflowList = () => {
    setDropperOpen(!dropperOpen);
  };

  const goWorkflowDetail = (id, clientId) => {
    history.push(path(`/admin-events/workflowDetail/${id}/null/${clientId}/?FC=false`));
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
      <div className="WorkflowListItem-box">
        <div className="WorkflowListItem">
          <div className="WorkflowListItemLeft">
            <div className="WorkflowListItem-information">
              <Avatar name={data.fullName} id={data.avatarId} className={cn("WorkflowListItem-avatar")} />
              <div className="WorkflowListItem-fullname" style={{ cursor: data?.workflowNum > 1 ? "" : "pointer" }}>
                {data.fullName}
              </div>
            </div>
            {data.workflows?.length === 1 && (
              <div
                className="WorkflowListItem-workflowName"
                onClick={() => {
                  localStorage.setItem("triggerCurrentComId", data?.workflows[0].communityId);
                  goWorkflowDetail(data?.workflows[0].id, data?.workflows[0].clientId);
                }}
              >
                {getWorkflowNameList(data?.workflows)}
              </div>
            )}
            {data.workflows?.length > 1 && (
              <div className="WorkflowListItem-workflowName" title={getWorkflowNameList(data?.workflows)}>
                {getWorkflowNameList(data?.workflows)}
              </div>
            )}
          </div>

          {(data.workflowNum === 1 || data.workflows?.length === 1) && (
            <div className="WorkflowListItem-one-process">
              <div className="WorkflowListItem-one-process-box">
                <WorkflowQuestionNumList
                  data={data.workflows[0]?.questionAmount || 12}
                  endData={data.workflows[0]?.allQuestion?.save || 5}
                  workflowNameId={data.workflows[0]?.id}
                  list={data?.workflows[0]?.list}
                  workflowData={data.workflows[0]}
                />
              </div>
              {/* 删除按钮 */}

              <div
                style={{
                  width: 26,
                  height: 26,
                  minWidth: 26,
                  display: "flex",
                  justifyContent: "center",
                  cursor: "pointer",
                }}
                onClick={() => {
                  setCurrentId(data.workflows[0].id);
                  setWaringDialog(true);
                }}
              >
                {data.workflows[0].canDelete && <DeleteImg style={{ width: 26, height: 26 }} />}
              </div>
            </div>
          )}

          {(data?.workflowNum > 1 || data.workflows?.length > 1) && (
            <div className="WorkflowListItem-many-process">
              {data.hasFeedback && (
                <div className="feedbackSvg">
                  <Tips className={cn("Tips-Icon")} />
                </div>
              )}

              <div className="progress-container">
                <div
                  className="progress-bar"
                  style={{ width: getSigningProgress(data.workflows.length, data?.completeNum || 0) }}
                />
                <div
                  className="progress-bar-transparent"
                  style={{ width: getSigningProgress(data.workflows.length, data?.completeNum || 0) }}
                >
                  <div className="progress-text">
                    {data?.completeNum || 0}/{data.workflows.length}
                  </div>
                </div>
              </div>
              <div className="progress-percentage">
                {getSigningProgress(data.workflows.length, data?.completeNum || 0)}
              </div>
              <Dropper
                className={cn("Dropper-Icon", dropperOpen ? "Dropper-Icon_rotated_90" : "Dropper-Icon_rotated_0")}
                onClick={onToggleDropper}
              />
            </div>
          )}
        </div>
        <Collapse isOpen={dropperOpen}>
          <div className="workflow-detail-list">
            <WorkflowShowDetailItem
              workflowData={data.workflows}
              isRefreshData={isRefreshData}
              setIsRefreshData={setIsRefreshData}
              isFetching={isFetching}
              setIsFetching={setIsFetching}
            />
          </div>
        </Collapse>
      </div>

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

export default WorkflowListItem;
