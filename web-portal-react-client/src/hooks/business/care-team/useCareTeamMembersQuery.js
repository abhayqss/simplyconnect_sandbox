import { useManualPaginatedQuery } from "hooks/common";

import service from "services/CareTeamMemberService";

const fetchName = (params) => service.find(params);
function useCareTeamMembersQuery(params, options) {
  const { name } = params;
  const {
    sort,
    fetch,
    refresh,
    pagination,
    isFetching,
    data: { data } = {},
  } = useManualPaginatedQuery(
    {
      ...params,
      name,
      size: 15,
    },
    fetchName,
    {
      ...options,
      staleTime: 0,
    },
  );

  return {
    sort,
    fetch,
    refresh,
    pagination,
    isFetching,
    data: { data },
  };
}

export default useCareTeamMembersQuery;
