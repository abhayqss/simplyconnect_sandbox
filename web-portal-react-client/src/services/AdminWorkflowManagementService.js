// AdminWorkflowManagementService
import BaseService from "./BaseService";
import { PAGINATION } from "lib/Constants";

const { FIRST_PAGE } = PAGINATION;

export class AdminWorkflowManagementService extends BaseService {
  findSuperAdminData({ page = FIRST_PAGE, size = 10, sort }, filter) {
    return super.request({
      url: `/workflow/superAdmin/find`,
      params: { page: page - 1, size, sort, ...filter },
    });
  }

  findAdminData({ page = FIRST_PAGE, size = 10, sort }, filter) {
    return super.request({
      url: `/workflow/admin/find`,
      params: { page: page - 1, size, sort, ...filter },
    });
  }

  findById(alertId) {
    return super.request({
      url: `/alerts/${alertId}`,
    });
  }

  count(type) {
    return super.request({
      url: `/alerts/count`,
      params: { type },
    });
  }

  /*save (alert) {
      const isNew = isEmpty(alert.id)

      return super.request({
          method: isNew ? 'PUT' : 'POST',
          url: `/notify/active-alerts`,
          body: alert,
          type: 'json'
      })
  }*/
}

const adminWorkflowManagementService = new AdminWorkflowManagementService();
export default adminWorkflowManagementService;
