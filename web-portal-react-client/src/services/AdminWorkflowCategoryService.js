// AdminWorkflowCategory
import BaseService from "./BaseService";
import { PAGINATION } from "lib/Constants";
import { isEmpty } from "lib/utils/Utils";

const { FIRST_PAGE } = PAGINATION;

export class AdminWorkflowCategoryService extends BaseService {
  saveCategory(params) {
    const isNew = isEmpty(params.categoryId);
    return super.request({
      method: "POST",
      url: `/workflow/category/save`,
      body: params,
    });
  }
  getAllCategory(params) {
    return super.request({
      url: `/workflow/category/find`,
      params,
    });
  }

  getAllCategoryByOrg(params) {
    return super.request({
      url: `/workflow/category/findAll`,
      params,
    });
  }
}

const adminWorkflowCategoryService = new AdminWorkflowCategoryService();
export default adminWorkflowCategoryService;
