import { useMutation, useQueryClient, useQuery } from "@tanstack/react-query";
import adminWorkflowCreateService from "services/AdminWorkflowCreateService";

/**
 * Hook for deleting workflow template
 * @param {Object} options - Additional mutation options
 * @returns {Object} Mutation object with mutate function, loading state, error, etc.
 */
export const useDeleteWorkflowTemplateMutation = (options = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (workflowTemplateId) => adminWorkflowCreateService.deleteWorkflowTemplate(workflowTemplateId),
    onSuccess: () => {
      // Invalidate and refetch workflow-related queries
      queryClient.invalidateQueries({ queryKey: ["workflow-templates"] });
      queryClient.invalidateQueries({ queryKey: ["coexisting-workflows"] });
    },
    ...options,
  });
};

/**
 * Hook for deleting service plan template
 * @param {Object} options - Additional mutation options
 * @returns {Object} Mutation object with mutate function, loading state, error, etc.
 */
export const useDeleteServicePlanTemplateMutation = (options = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (servicePlanTemplateId) => adminWorkflowCreateService.deleteServicePlanTemplate(servicePlanTemplateId),
    onSuccess: () => {
      // Invalidate and refetch service plan-related queries
      queryClient.invalidateQueries({ queryKey: ["service-plan-templates"] });
      queryClient.invalidateQueries({ queryKey: ["service-plan-templates-by-client"] });
    },
    ...options,
  });
};

/**
 * Hook for removing published workflow template (archive)
 * @param {Object} options - Additional mutation options
 * @returns {Object} Mutation object with mutate function, loading state, error, etc.
 */
export const useRemovePublishedWorkflowTemplateMutation = (options = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (workflowId) => adminWorkflowCreateService.removePublishedWorkflowTemplate(workflowId),
    onSuccess: () => {
      // Invalidate and refetch workflow-related queries
      queryClient.invalidateQueries({ queryKey: ["workflow-templates"] });
      queryClient.invalidateQueries({ queryKey: ["coexisting-workflows"] });
    },
    ...options,
  });
};

/**
 * Hook for removing published service plan template (archive)
 * @param {Object} options - Additional mutation options
 * @returns {Object} Mutation object with mutate function, loading state, error, etc.
 */
export const useRemovePublishedServicePlanTemplateMutation = (options = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (servicePlanId) => adminWorkflowCreateService.removePublishedServicePlanTemplate(servicePlanId),
    onSuccess: () => {
      // Invalidate and refetch service plan-related queries
      queryClient.invalidateQueries({ queryKey: ["service-plan-templates"] });
      queryClient.invalidateQueries({ queryKey: ["service-plan-templates-by-client"] });
    },
    ...options,
  });
};

/**
 * Hook for fetching workflow detail
 * @param {Object} options - Additional query options
 * @returns {Object} Query object with data, loading state, error, etc.
 */
export const useFetchWorkflowDetail = (workflowId, options = {}) => {
  return useQuery(
    ["workflowDetail", workflowId],
    () => adminWorkflowCreateService.getWorkflowDetail({ templateId: workflowId }),
    options,
  );
};

/**
 * Hook for creating/updating workflow template
 * @param {Object} options - Additional mutation options
 * @returns {Object} Mutation object with mutate function, loading state, error, etc.
 */
export const useCreateWorkflowMutation = (options = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data) => adminWorkflowCreateService.createWorkflow(data),
    onSuccess: () => {
      // Invalidate and refetch workflow-related queries
      queryClient.invalidateQueries({ queryKey: ["workflow-templates"] });
      queryClient.invalidateQueries({ queryKey: ["coexisting-workflows"] });
    },
    ...options,
  });
};

/**
 * Hook for saving service plan template
 * @param {Object} options - Additional mutation options
 * @returns {Object} Mutation object with mutate function, loading state, error, etc.
 */
export const useSaveServicePlanTemplateMutation = (options = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data) => adminWorkflowCreateService.saveServicePlanTemplate(data),
    onSuccess: () => {
      // Invalidate and refetch service plan-related queries
      queryClient.invalidateQueries({ queryKey: ["service-plan-templates"] });
      queryClient.invalidateQueries({ queryKey: ["service-plan-templates-by-client"] });
    },
    ...options,
  });
};
