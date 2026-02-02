import { useQuery } from "@tanstack/react-query";
import adminWorkflowCreateService from "services/AdminWorkflowCreateService";

/**
 * Hook for fetching all service plan templates with TanStack Query
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
export const useServicePlanTemplatesQuery = (params, options = {}) => {
  const { enabled = true, ...queryOptions } = options;

  return useQuery({
    queryKey: [
      "service-plan-templates",
      {
        page: params?.page,
        size: params?.size,
        sort: params?.sort,
        organizationId: params?.organizationId,
        communityIds: params?.communityIds,
        name: params?.name,
      },
    ],
    queryFn: () => adminWorkflowCreateService.fetchAllServicePlanTemplates(params),
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
 * Hook for fetching service plan templates by client ID
 * @param {string} clientId - Client ID
 * @param {Object} options - Additional query options
 * @returns {Object} Query result with data, loading state, error, etc.
 */
export const useServicePlanTemplatesByClientQuery = (clientId, options = {}) => {
  const { enabled = true, ...queryOptions } = options;

  return useQuery({
    queryKey: ["service-plan-templates-by-client", clientId],
    queryFn: () => adminWorkflowCreateService.getAllServicePlanTemplates(clientId),
    enabled: enabled && !!clientId,
    staleTime: 1000 * 60 * 5, // 5 minutes
    ...queryOptions,
  });
};

/**
 * Hook for fetching service plan template detail by ID
 * @param {Object} params - Query parameters containing the template ID
 * @param {Object} options - Additional query options
 * @returns {Object} Query result with data, loading state, error, etc.
 */
export const useServicePlanTemplateDetailQuery = (params, options = {}) => {
  const { enabled = true, ...queryOptions } = options;

  return useQuery({
    queryKey: ["service-plan-template-detail", params],
    queryFn: () => adminWorkflowCreateService.getServicePlanDetail(params),
    enabled: enabled && !!params?.id,
    staleTime: 1000 * 60 * 5, // 5 minutes
    ...queryOptions,
  });
};
