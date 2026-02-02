import { useSharedCache } from "hooks/common";

const NAME = "WORKFLOW_FILTER_DEFAULT_DATA";

export default function useWorkflowFilterDefaultDataCache(params) {
  const cache = useSharedCache([NAME, params]);

  function get(o) {
    return cache.get([NAME, o ?? params]);
  }

  return { ...cache, get };
}
