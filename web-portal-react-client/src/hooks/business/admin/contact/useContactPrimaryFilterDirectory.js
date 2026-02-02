import { map, noop, some } from "underscore";

import { useCommunitiesQuery, useOrganizationsQuery } from "hooks/business/directory/query";

import { isEmpty, isInteger } from "lib/utils/Utils";
import { useAuthUser } from "../../../common";
import { VENDOR_SYSTEM_ROLES } from "../../../../lib/Constants";

const NONE = "NONE";

function mapToIds(data) {
  return map(data, (o) => o.id);
}

export default function useContactPrimaryFilterDirectory(
  { organizationId, communityIds } = {},
  { actions: { changeFilterFields = noop() } },
) {
  const user = useAuthUser();
  const isOnlyDirect = VENDOR_SYSTEM_ROLES.includes(user.roleName);
  const { data: organizations = [] } = useOrganizationsQuery(
    { onlyDirect: isOnlyDirect },
    {
      staleTime: 0,
      includeAffiliated: true,
    },
  );

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
    communities: [...communities, { id: NONE, name: "None" }],
  };
}
