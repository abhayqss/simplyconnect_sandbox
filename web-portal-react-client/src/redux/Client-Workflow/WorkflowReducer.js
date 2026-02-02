import initialState from "./WorkflowState";
import actions from "./actionTypes";

const WorkflowReducer = (state = initialState, action) => {
  switch (action.type) {
    case actions.LOAD_CLIENT_WORKFLOW_COUNT_SUCCESS:
      return {
        ...state,
        workflowCount: action.payload.data,
      };

    default:
      return state;
  }
};

export default WorkflowReducer;
