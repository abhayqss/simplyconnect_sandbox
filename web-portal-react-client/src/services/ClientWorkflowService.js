import BaseService from "./BaseService";
import { PAGINATION } from "lib/Constants";

const { FIRST_PAGE } = PAGINATION;

export class ClientWorkflowService extends BaseService {
  findWorkflowForClient({ name, organizationId, communityId, page = FIRST_PAGE, size = 10, filter }) {
    return super.request({
      url: `/workflow/client/find`, // workflow list for admin
      params: { name, organizationId, communityId, page: page - 1, size, ...filter },
    });
  }

  findClientWorkflowDetail(params) {
    return super.request({
      url: `/workflow/client/findById`,
      params,
    });
  }

  submitClientWorkflowResult(body) {
    return super.request({
      url: `/workflow/result/submit`,
      body,
      method: "POST",
    });
  }

  saveDraftClientWorkflowResult(body) {
    return super.request({
      url: `/workflow/result/draft`,
      body,
      method: "POST",
    });
  }

  //login in role admin for client-detail-workflow
  ///16/workflow/find?page=0&size=2&workflowName=te
  adminFindClientWorkflow({ clientId, page = FIRST_PAGE, size, workflowName, sort }) {
    return super.request({
      url: `/${clientId}/workflow/find`,
      params: { page: page - 1, size, workflowName: workflowName, sort: sort },
      method: "GET",
    });
  }

  adminFindClientWorkflowFeedback({ clientId, clientWorkflowId }) {
    return super.request({
      url: `/${clientId}/workflow/lastCommitAndFeedback`,
      params: { clientWorkflowId: clientWorkflowId },
      method: "GET",
    });
  }

  getPreFillMessage({ clientId, answerContent }) {
    return super.request({
      url: `/workflow/preFillContent`,
      body: { answerContent, clientId },
      method: "POST",
    });
  }

  getLastCommitHistory(code, clientId) {
    return super.request({
      url: `/workflow/lastCommitHistory?templateCode=${code}&clientId=${clientId}`,
      method: "Get",
    });
  }
}

const clientWorkflowService = new ClientWorkflowService();
export default clientWorkflowService;
