import BaseService from "./BaseService";
import { MarketplaceService } from "./Marketplace";

/**
 * @typedef {Object} MyParams
 * @property {string} [name] - The name property.
 * @property {string} [clientName] - The clientName property.
 * @property {number} communityId - The communityId property.
 * @property {number} organizationId - The organizationId property.
 * @property {Date|string} [submitTime] - The submitTime property.
 * @property {string} [status] - The status property.
 * @property {string} [page] - 0
 * @property {string} [size] - 10
 */

export class QAEventsService extends BaseService {
  /**
   *  QA events list
   * @param {{organizationId, submitTime: string, size: number, clientName: string, name: string, page: number, communityIds, status: string}} params - Object containing the parameters.
   */
  findAllQaEvents(params) {
    return super.request({
      url: "/qa/workflow/find",
      params: params,
      // response: { extractDataOnly: false },
    });
  }

  findWorkflowDetail(clientWorkflowId) {
    return super.request({
      url: "/workflow/client/findById",
      params: { clientWorkflowId },
      response: { extractDataOnly: true },
    });
  }

  //    通过接口
  approveFeedback(clientWorkflowId) {
    return super.request({
      url: "/qa/workflow/approve",
      body: {
        clientWorkflowId,
      },
      response: { extractDataOnly: true },
      method: "POST",
    });
  }

  //    不通过的接口
  notPassThroughFeedback(clientWorkflowId, workflowResultId, operatorId, feedbackJson) {
    return super.request({
      url: "/workflow/feedback/add",
      body: {
        clientWorkflowId,
        workflowResultId,
        operatorId,
        feedbackJson,
      },
      method: "POST",
    });
  }
}

const service = new QAEventsService();
export default service;
