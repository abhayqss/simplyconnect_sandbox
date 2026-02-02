import { map, noop, some } from "underscore";

import { useCommunitiesQuery, useOrganizationsQuery } from "hooks/business/directory/query";

import { isEmpty, isInteger } from "lib/utils/Utils";

const NONE = "NONE";

function mapToIds(data) {
  return map(data, (o) => o.id);
}

export default function useWorkflowPrimaryFilterDirectory(
  { organizationId, communityIds } = {},
  { actions: { changeFilterFields = noop() } },
) {
  const { data: organizations = [] } = useOrganizationsQuery(null, {
    staleTime: 0,
  });

  const { data: communities = [] } = useCommunitiesQuery(
    { organizationId },
    {
      staleTime: 0,
      enabled: isInteger(organizationId),
      onSuccess: (data) => {
        changeFilterFields({
          includeWithoutCommunity: true,
          communityIds: isEmpty(communityIds) ? mapToIds(data) : communityIds,
        });
      },
    },
  );

  return {
    organizations,
    communities: [...communities],
  };
}
