import BaseService from "./BaseService";
import { PAGINATION } from "lib/Constants";

const { FIRST_PAGE } = PAGINATION;

// admin vendor
export class AdminWorkflowCreateService extends BaseService {
  // findVendorList
  createWorkflow(data) {
    return super.request({
      url: `/workflowTemplate`,
      method: "PUT",
      body: data,
      type: "multipart/form-data",
    });
  }

  getWorkflowDetail(params) {
    return super.request({
      url: `/workflowTemplate/findById`,
      params,
    });
  }

  // 删除暂存模版
  deleteWorkflowTemplate(workflowTemplateId) {
    return super.request({
      url: `/workflowTemplate/${workflowTemplateId}/delete`,
      method: "delete",
    });
  }

  //   service plan 模版保存
  saveServicePlanTemplate(data) {
    return super.request({
      url: `/servicePlanTemplate`,
      method: "POST",
      body: data,
      type: "multipart/form-data",
    });
  }

  // 查询当前client 下的 service plan
  getAllServicePlanTemplates(clientId) {
    return super.request({
      url: `/servicePlanTemplate/find?clientId=${clientId}&status=PUBLISHED`,
    });
  }

  fetchAllServicePlanTemplates(params) {
    return super.request({
      url: `/servicePlanTemplate/find`,
      params,
    });
  }

  /**
   *    查询社区下共存的 service plan
   *     @param {Object} params - 查询参数
   *     @param {String} params.organizationId - 组织ID，用于指定要查询的组织
   *     @param {String[]} params.communityIds - 社区ID列表，包含要查询的社区ID数组
   */
  queryCoexistingServicePlansUnderTheCommunity(params) {
    return super.request({
      url: `/servicePlanTemplate/trigger/find`,
      params,
    });
  }

  getServicePlanDetail(params) {
    return super.request({
      url: `/servicePlanTemplate/findById`,
      params,
    });
  }

  //   获取当前service plan 详情
  getDetailOfCurrentServicePlanTemplates(templateId) {
    return super.request({
      url: `/servicePlanTemplate/findById?templateId=${templateId}`,
    });
  }

  //   保存当前service plan 数据
  saveServicePlanDetail(data) {
    return super.request({
      url: `/servicePlanTemplate/submit`,
      method: "POST",
      body: data,
      // type: "multipart/form-data",
    });
  }

  //   删除servicePlan 草稿
  deleteServicePlanTemplate(templateId) {
    return super.request({
      url: `/servicePlanTemplate/${templateId}/delete`,
      method: "delete",
    });
  }

  copyServicePlanTemplate(params) {
    return super.request({
      method: "POST",
      url: `/servicePlanTemplate/copy`,
      body: params,
      type: "multipart/form-data",
    });
  }

  //   移除发布的workflow模板
  removePublishedWorkflowTemplate(templateId) {
    return super.request({
      url: `/workflowTemplate/${templateId}/disable`,
      method: "POST",
    });
  }

  // 移除发布的service plan 模板
  removePublishedServicePlanTemplate(templateId) {
    return super.request({
      url: `/servicePlanTemplate/${templateId}/disable`,
      method: "POST",
    });
  }
}

const adminWorkflowCreateService = new AdminWorkflowCreateService();
export default adminWorkflowCreateService;
