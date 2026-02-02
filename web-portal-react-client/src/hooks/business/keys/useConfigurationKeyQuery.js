import { useQuery } from "@tanstack/react-query";
import keyService from "services/KeyService";

/**
 * useConfigurationKeyQuery
 *
 * Queries a single configuration key from the backend (such as "docseal_key", "docseal_secret", "docseal_api_key").
 * Uses tanstack/react-query for data fetching and caching.
 *
 * @param {string} key - The name of the configuration key to fetch.
 * @param {object} options - Optional react-query options (enabled, refetchOnWindowFocus, etc.)
 * @returns {{
 *   data: object | undefined,
 *   isLoading: boolean,
 *   isError: boolean,
 *   error: any,
 *   refetch: Function,
 * }}
 *
 * @example
 * const { data, isLoading } = useConfigurationKeyQuery("docseal_key");
 * if (!isLoading && data) {
 *   // use data.docseal_key
 * }
 */
export function useConfigurationKeyQuery(key, options = {}) {
  return useQuery(["configurationKeys", key], () => keyService.getConfigurationKey(key), {
    enabled: !!key,
    staleTime: 1000 * 60 * 10, // 10 minutes; adjust as needed
    ...options,
  });
}

export default useConfigurationKeyQuery;
