import React, { useCallback } from "react";

import cn from "classnames";

import { compact, without } from "underscore";

import { useAuthUser } from "hooks/common/redux";

import { useContactPrimaryFilterDirectory } from "hooks/business/admin/contact";

import { PrimaryFilter } from "components";

import { isNotEmpty } from "lib/utils/Utils";

import { PROFESSIONAL_SYSTEM_ROLES } from "lib/Constants";

import "./ContactPrimaryFilter.scss";

export const NAME = "CONTACT_PRIMARY_FILTER";

const NONE = "NONE";
//includeVendor  包含vendor
export default function ContactPrimaryFilter({ data, changeFields, changeOrganizationField, className }) {
  const { organizationId, includeWithoutCommunity } = data;

  const user = useAuthUser();

  const communityIds = compact([...data.communityIds, includeWithoutCommunity === true && NONE]);

  const { communities, organizations } = useContactPrimaryFilterDirectory(data, {
    actions: { changeFilterFields: changeFields },
  });

  const onChangeCommunityField = useCallback(
    (value) => {
      changeFields(
        {
          communityIds: without(value, NONE),
          includeWithoutCommunity: isNotEmpty(value) ? value.includes(NONE) : null,
        },
        true,
      );
    },
    [changeFields],
  );

  return (
    user &&
    PROFESSIONAL_SYSTEM_ROLES.includes(user.roleName) && (
      <PrimaryFilter
        communities={communities}
        organizations={organizations}
        onChangeOrganizationField={changeOrganizationField}
        onChangeCommunityField={onChangeCommunityField}
        className={cn("ContactPrimaryFilter", className)}
        data={{ organizationId, communityIds }}
      />
    )
  );
}
