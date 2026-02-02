import { useQuery } from "@tanstack/react-query";
import workflowService from "services/WorkflowService";

/**
 * Hook for fetching workflow options with TanStack Query
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number (0-based)
 * @param {number} params.size - Page size
 * @param {string} params.sort - Sort parameter
 * @param {string} params.organizationId - Organization ID
 * @param {Array} params.communityIds - Community IDs
 * @param {string} params.name - Search name
 * @param {Object} options - Additional query options
 * @returns {Object} Query result with data, loading state, error, etc.
 */
export const useWorkflowOptionsQuery = (params, options = {}) => {
  const { enabled = true, ...queryOptions } = options;

  return useQuery({
    queryKey: [
      "workflow-templates",
      {
        page: params?.page,
        size: params?.size,
        sort: params?.sort,
        organizationId: params?.organizationId,
        communityIds: params?.communityIds,
        name: params?.name,
      },
    ],
    queryFn: () => workflowService.findWorkflowOptions(params),
    enabled:
      enabled &&
      !!(
        params?.organizationId &&
        params?.communityIds &&
        Array.isArray(params.communityIds) &&
        params.communityIds.length > 0
      ),
    staleTime: 1000 * 60 * 5, // 5 minutes
    ...queryOptions,
  });
};

/**
 * Hook for fetching workflow categories
 * @param {Object} params - Query parameters
 * @param {Object} options - Additional query options
 * @returns {Object} Query result with data, loading state, error, etc.
 */
export const useWorkflowCategoriesQuery = (params = {}, options = {}) => {
  return useQuery({
    queryKey: ["workflow-categories", params],
    queryFn: () => workflowService.findWorkflowCategoryOptions(params),
    staleTime: 1000 * 60 * 10, // 10 minutes - categories don't change often
    ...options,
  });
};

/**
 * Hook for fetching coexisting workflows under community
 * @param {Object} params - Query parameters
 * @param {string} params.organizationId - Organization ID
 * @param {Array} params.communityIds - Community IDs
 * @param {Object} options - Additional query options
 * @returns {Object} Query result with data, loading state, error, etc.
 */
export const useCoexistingWorkflowsQuery = (params, options = {}) => {
  const { enabled = true, ...queryOptions } = options;

  return useQuery({
    queryKey: [
      "coexisting-workflows",
      {
        organizationId: params?.organizationId,
        communityIds: params?.communityIds,
      },
    ],
    queryFn: () => workflowService.queryCoexistingWorkflowsUnderTheCommunity(params),
    enabled:
      enabled &&
      !!(
        params?.organizationId &&
        params?.communityIds &&
        Array.isArray(params.communityIds) &&
        params.communityIds.length > 0
      ),
    staleTime: 1000 * 60 * 5, // 5 minutes
    ...queryOptions,
  });
};
