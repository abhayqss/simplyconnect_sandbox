import BaseService from "./BaseService";

export class WorkflowService extends BaseService {
  findAllCreatedServicePlans(clientId) {
    return super.request({
      url: `/clients/${clientId}/service-plans/servicePlanNeeds`,
      method: "get",
    });
  }
}

const workflowService = new WorkflowService();
export default workflowService;
