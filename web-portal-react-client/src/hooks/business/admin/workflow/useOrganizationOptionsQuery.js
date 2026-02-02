import { useQuery } from "@tanstack/react-query";
import service from "services/DirectoryService";

/**
 * Hook for fetching organization options with TanStack Query
 * @param {Object} options - Additional query options
 * @returns {Object} Query result with data, loading state, error, etc.
 */
export const useOrganizationOptionsQuery = (options = {}) => {
  return useQuery({
    queryKey: ["organization-options"],
    queryFn: async () => {
      const res = await service.findOrganizations();
      // Transform data to match the expected format
      const dataOption = res?.data?.map((item) => ({
        text: item.label,
        value: item.id,
      }));
      return {
        ...res,
        data: dataOption,
      };
    },
    staleTime: 1000 * 60 * 10, // 10 minutes - organizations don't change often
    ...options,
  });
};

export default useOrganizationOptionsQuery;
