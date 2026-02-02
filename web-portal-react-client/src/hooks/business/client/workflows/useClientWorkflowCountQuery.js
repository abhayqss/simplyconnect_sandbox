import { useQuery } from "@tanstack/react-query";
import workflowService from "services/WorkflowService";

const fetch = (params) => {
  return workflowService.count(params.clientId);
};

function useClientWorkflowCountQuery(params) {
  return useQuery(["Client.Workflow.Count", params], () => fetch(params));
}

export default useClientWorkflowCountQuery;
