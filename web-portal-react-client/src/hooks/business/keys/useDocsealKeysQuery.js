import { useQuery } from "@tanstack/react-query";
import keyService from "services/KeyService";

/**
 * useDocsealKeysQuery
 *
 * Queries all Docseal-related configuration keys: docseal_key, docseal_secret, docseal_api_key.
 * Uses tanstack/react-query for data fetching and caching.
 *
 * @returns {{
 *   data: {
 *     docseal_key?: string,
 *     docseal_secret?: string,
 *     docseal_api_key?: string,
 *   } | undefined,
 *   isLoading: boolean,
 *   isError: boolean,
 *   error: any,
 *   refetch: Function,
 * }}
 *
 * @example
 * const { data, isLoading } = useDocsealKeysQuery();
 * if (!isLoading && data) {
 *   // use data.docseal_key, etc.
 * }
 */
export function useDocsealKeysQuery(options = {}) {
  return useQuery(["configurationKeys"], () => keyService.getDocsealKeys(), {
    staleTime: 1000 * 60 * 10, // 10 minutes; customize as needed
    ...options,
  });
}

export default useDocsealKeysQuery;
