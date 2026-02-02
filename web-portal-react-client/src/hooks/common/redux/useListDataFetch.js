import { useCallback } from "react";

import useRefCurrent from "../useRefCurrent";

function useListDataFetch(state, actions, params = {}) {
  params = useRefCurrent(params);

  const { filter, sorting, pagination } = state.dataSource;

  const { field = null, order = null } = sorting ?? {};

  const { page: p, size } = pagination ?? {};
  const { type } = params;
  const filterData =
    type === "INBOUND"
      ? {
          serviceIds: filter?.toJS()?.serviceIds,
          priorityIds: filter?.toJS()?.priorityIds,
          statuses: filter?.toJS()?.statuses,
          referredBy: filter?.toJS()?.referredBy,
          organizationId: filter?.toJS()?.organizationId,
          communityIds: filter?.toJS()?.communityIds,
          includeWithEmptyPriority: filter?.toJS()?.includeWithEmptyPriority,
          includeWithEmptyReferredBy: filter?.toJS()?.includeWithEmptyReferredBy,
          includeWithEmptyService: filter?.toJS()?.includeWithEmptyService,
          assignedTo: filter?.toJS()?.assignedTo,
        }
      : {
          serviceIds: filter?.toJS()?.serviceIds,
          priorityIds: filter?.toJS()?.priorityIds,
          statuses: filter?.toJS()?.statuses,
          referredTo: filter?.toJS()?.referredTo,
          communityIds: filter?.toJS()?.communityIds,
          organizationId: filter?.toJS()?.organizationId,
          includeWithEmptyPriority: filter?.toJS().includeWithEmptyPriority,
          includeWithEmptyService: filter?.toJS().includeWithEmptyService,
        };

  const fetch = useCallback(
    (page) => {
      return actions.load({
        size,
        page: page || p,
        ...filterData,
        ...(field ? { sort: `${field},${order}` } : null),
        ...params,
      });
    },
    [actions, size, p, filter, field, order, params],
  );

  const fetchIf = useCallback((condition, page) => (condition ? fetch(page) : Promise.resolve()), [fetch]);

  return { fetch, fetchIf };
}

export default useListDataFetch;
