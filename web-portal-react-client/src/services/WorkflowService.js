import BaseService from "./BaseService";

import { isEmpty } from "lib/utils/Utils";
import { PAGINATION } from "lib/Constants";

const { FIRST_PAGE } = PAGINATION;

export class WorkflowService extends BaseService {
  count(clientId) {
    return super.request({
      url: `/${clientId}/workflow/count`,
      response: { extractDataOnly: true },
    });
  }

  findWorkflowForAdmin({ name, organizationId, communityIds, page = FIRST_PAGE, size = 10, filter }) {
    return super.request({
      url: `/workflow/find`, // workflow list for admin
      params: { name, organizationId, communityIds, page: page - 1, size, ...filter },
    });
  }

  findWorkflowOptions(params) {
    return super.request({
      url: `/workflowTemplate/find`,
      params,
    });
  }

  /**
   *    查询社区下共存的工作流
   *     @param {Object} params - 查询参数
   *     @param {String} params.organizationId - 组织ID，用于指定要查询的组织
   *     @param {String[]} params.communityIds - 社区ID列表，包含要查询的社区ID数组
   */
  queryCoexistingWorkflowsUnderTheCommunity(params) {
    return super.request({
      url: `/workflowTemplate/trigger/find`,
      params,
    });
  }

  findWorkflowCategoryOptions(params) {
    return super.request({
      url: `/workflow/category/findCategoryTemplate`,
      params,
    });
  }

  addWorkflowForClient(body) {
    return super.request({
      url: `/workflow/clientWorkflow/add`,
      method: "POST",
      body,
    });
  }

  save(event, clientId) {
    const isNew = isEmpty(event.id);

    return super.request({
      method: isNew ? "POST" : "PUT",
      url: getUrl(clientId, `/events`),
      body: event,
      type: "json",
    });
  }

  //   copy
  adminWorkflowLibraryCopy(body) {
    return super.request({
      url: "/workflowTemplate/copy",
      method: "POST",
      body,
      type: "multipart/form-data",
    });
  }

  //   delete

  deleteWorkflowTemplate(clientWorkflowId) {
    return super.request({
      url: `/workflow/clientWorkflow/${clientWorkflowId}`,
      method: "DELETE",
    });
  }

  static addWorkflowForClient(body) {}
}

const workflowService = new WorkflowService();
export default workflowService;
