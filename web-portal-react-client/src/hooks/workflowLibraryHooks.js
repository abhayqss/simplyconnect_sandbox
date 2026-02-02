import { useQuery, useMutation } from "@tanstack/react-query";
import directoryService from "../services/DirectoryService";
import workflowService from "../services/WorkflowService";
import adminWorkflowCategoryService from "../services/AdminWorkflowCategoryService";
import adminWorkflowCreateService from "../services/AdminWorkflowCreateService";

/**
 * getWorkflowLibraryHooks
 * 集成组织、社区、工作流分类下拉选项加载，以及工作流模板相关的 mutations。
 *
 * 用法：
 * const { useOrganizationOptions, useCommunityOptions, useWorkflowCategoryOptions, useCopyWorkflowLibraryTemplate, useCreateWorkflowTemplate, useUpdateWorkflowTemplate } = getWorkflowLibraryHooks();
 * const { data: orgOptions } = useOrganizationOptions();
 * const { data: communityOptions } = useCommunityOptions(orgId);
 * const { data: categoryOptions } = useWorkflowCategoryOptions(orgId);
 * const copyMutation = useCopyWorkflowLibraryTemplate();
 * const createMutation = useCreateWorkflowTemplate();
 * const updateMutation = useUpdateWorkflowTemplate();
 */
const getWorkflowLibraryHooks = () => {
  // 获取组织机构下拉
  const useOrganizationOptions = (options = {}) =>
    useQuery({
      queryKey: ["organizations"],
      queryFn: async () => {
        const res = await directoryService.findOrganizations();
        if (Array.isArray(res.data)) {
          return res.data.map((item) => ({
            value: item.id,
            text: item.label,
          }));
        }
        return [];
      },
      staleTime: 5 * 60 * 1000,
      ...options,
    });

  // 获取社区下拉
  const useCommunityOptions = (organizationId, options = {}) =>
    useQuery({
      queryKey: ["communities", organizationId],
      queryFn: async () => {
        if (!organizationId) return [];
        const res = await directoryService.findCommunities({ organizationId });
        if (res.success && Array.isArray(res.data)) {
          return res.data.map((item) => ({
            value: item.id,
            text: item.name,
          }));
        }
        return [];
      },
      enabled: !!organizationId,
      staleTime: 5 * 60 * 1000,
      ...options,
    });

  // 获取工作流分类下拉
  const useWorkflowCategoryOptions = (organizationId, options = {}) =>
    useQuery({
      queryKey: ["workflow-category", organizationId],
      queryFn: async () => {
        if (!organizationId) return [];
        const res = await adminWorkflowCategoryService.getAllCategoryByOrg({ organizationId });
        if (res && res.success && Array.isArray(res.data)) {
          return res.data.map((item) => ({
            value: item.id,
            text: item.categoryName,
          }));
        }
        return [];
      },
      enabled: !!organizationId,
      staleTime: 5 * 60 * 1000,
      ...options,
    });

  // 工作流模板拷贝 mutation
  const useCopyWorkflowLibraryTemplate = (options = {}) =>
    useMutation({
      mutationFn: (params) => workflowService.adminWorkflowLibraryCopy(params),
      ...options,
    });

  // 创建工作流模板 mutation
  const useCreateWorkflowTemplate = (options = {}) =>
    useMutation({
      mutationFn: (params) => adminWorkflowCreateService.createWorkflow(params),
      ...options,
    });

  // 更新工作流模板 mutation
  const useUpdateWorkflowTemplate = (options = {}) =>
    useMutation({
      mutationFn: (params) => adminWorkflowCreateService.createWorkflow(params),
      ...options,
    });

  // 创建服务计划模板 mutation
  const useCreateServicePlanTemplate = (options = {}) =>
    useMutation({
      mutationFn: (params) => adminWorkflowCreateService.saveServicePlanTemplate(params),
      ...options,
    });

  return {
    useOrganizationOptions,
    useCommunityOptions,
    useWorkflowCategoryOptions,
    useCopyWorkflowLibraryTemplate,
    useCreateWorkflowTemplate,
    useUpdateWorkflowTemplate,
    useCreateServicePlanTemplate,
  };
};

export default getWorkflowLibraryHooks;
