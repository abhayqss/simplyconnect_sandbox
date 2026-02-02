import "./WorkflowQuestionNumList.scss";
import cn from "classnames";
import React, { useId } from "react";
import { ReactComponent as Tips } from "images/Event/tip.svg";
import { path } from "../../../lib/utils/ContextUtils";
import { useHistory } from "react-router-dom";

const WorkflowQuestionNumList = (props) => {
  const id = useId();
  const history = useHistory();
  const { data = 15, endData = 2, workflowNameId, list = [], workflowData } = props;

  const numbers = Array.from({ length: data }, (_, index) => index + 1);
  const firstPendingIndex = list.indexOf(false);

  const goWorkflowDetail = (id, hasFeedback, name) => {
    if (hasFeedback) {
      history.push(path(`/admin-events/workflowDetail/${id}/null/${name}?FC=false`));
    }
  };

  return list.map((item, index) => {
    return (
      <div className={"question-item-box"} key={"QItem" + id + index}>
        <div
          className={cn(
            "question-item",
            item.hasResult && "item-hasDone",
            item.hasFeedback && "item-hasFeedback",
            index === firstPendingIndex && "item-pending",
          )}
          onClick={() => goWorkflowDetail(workflowNameId, item.hasFeedback, item.name)}
        >
          {index + 1}
          {item.hasFeedback && (
            <Tips
              className="tips-icon-question"
              id={`question-item-${index}-${workflowNameId}-tip`}
              title={"Feedback"}
            />
          )}
        </div>
      </div>
    );
  });
};

export default WorkflowQuestionNumList;
