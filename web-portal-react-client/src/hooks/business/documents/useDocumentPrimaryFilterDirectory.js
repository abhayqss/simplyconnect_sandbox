import { useCommunitiesQuery, useOrganizationsQuery } from "hooks/business/directory/query";

import { isInteger } from "lib/utils/Utils";
import { useAuthUser } from "../../common";
import { VENDOR_SYSTEM_ROLES } from "../../../lib/Constants";

export default function useDocumentPrimaryFilterDirectory({ organizationId } = {}) {
  const user = useAuthUser();
  const isOnlyDirect = VENDOR_SYSTEM_ROLES.includes(user.roleName);
  const { data: organizations = [] } = useOrganizationsQuery(
    { onlyDirect: isOnlyDirect },
    {
      staleTime: 0,
    },
  );

  const { data: communities = [] } = useCommunitiesQuery(
    { organizationId },
    {
      staleTime: 0,
      enabled: isInteger(organizationId),
    },
  );

  return { organizations, communities };
}
