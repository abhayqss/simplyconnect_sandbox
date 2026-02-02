import { useEffect } from "react";

import { filter, first, map } from "underscore";

import { useSelector } from "react-redux";

import { useLocation } from "react-router-dom";

import { useAuthUser, useDirectoryData, useStore } from "hooks/common";
import { useExternalProviderRoleCheck } from "hooks/business/external";
import { useBoundActions, usePrimaryFilter } from "hooks/common/redux";

import referralListActions from "redux/referral/list/referralListActions";

import { NAME } from "containers/Referrals/ReferralPrimaryFilter/ReferralPrimaryFilter";
import { NAME as SECOND_FILTER_NAME } from "containers/Referrals/ReferralFilter/ReferralFilter";

import { isEmpty, isNotEmpty } from "lib/utils/Utils";
import { REFERRAL_TYPES, VENDOR_SYSTEM_ROLES } from "lib/Constants";

import { useCommunitiesQuery } from "./index";
import { useOrganizationsQuery } from "../../directory/query";

const { INBOUND, OUTBOUND } = REFERRAL_TYPES;

export default function useReferralPrimaryFilter() {
  const { pathname } = useLocation();

  const type = pathname.includes("inbound") ? INBOUND : OUTBOUND;

  const name = `${type}_${NAME}`;

  const fields = useSelector((state) => state.referral.list.dataSource.filter);

  const { organizationId, communityIds } = fields;

  const actions = useBoundActions(referralListActions);

  const { changeFilter: change, changeFilterField: changeField } = actions;

  const store = useStore();

  const isSecondSaved = !!store.get(`${type}_${SECOND_FILTER_NAME}`);

  const isExternalProvider = useExternalProviderRoleCheck();

  const { isSaved, ...config } = usePrimaryFilter(name, fields, actions, {
    onRestored: () => {
      !isSecondSaved && change({});
    },
  });
  const user = useAuthUser();
  const isOnlyDirect = VENDOR_SYSTEM_ROLES.includes(user.roleName);
  const { data: organizations = [] } = useOrganizationsQuery(
    { onlyDirect: isOnlyDirect },
    {
      staleTime: 0,
    },
  );

  function criteria(o) {
    return type === INBOUND ? o?.canViewInboundReferrals : o?.canViewOutboundReferrals;
  }

  const communities = useSelector((state) => filter(state.referral.community.list.dataSource.data, criteria));

  useCommunitiesQuery(
    { organizationId },
    {
      onSuccess: ({ data }) => {
        changeField(
          "communityIds",
          isEmpty(communityIds) ? map(filter(data, criteria), (o) => o.id) : communityIds,
          false,
          true,
        );
      },
    },
  );

  useEffect(() => {
    if (isExternalProvider && !isSaved() && isNotEmpty(organizations)) {
      const organizationId = first(organizations).id;

      change({ organizationId }, true, false);
    }
  }, [change, isSaved, organizations, isExternalProvider]);

  return {
    isSaved,
    ...config,
    communities,
    organizations,
  };
}
