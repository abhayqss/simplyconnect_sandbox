import React, { useCallback } from "react";

import cn from "classnames";

import { compact, without } from "underscore";

import { useAuthUser } from "hooks/common/redux";

import { PrimaryFilter } from "components";

import { isNotEmpty } from "lib/utils/Utils";

import { PROFESSIONAL_SYSTEM_ROLES } from "lib/Constants";

import "./WorkflowPrimaryFilter.scss";
import { useWorkflowPrimaryFilterDirectory } from "hooks/business/admin/workflow";

export const NAME = "WORKFLOW_PRIMARY_FILTER";

export default function WorkflowPrimaryFilter({ data, changeFields, changeOrganizationField, className }) {
  const { organizationId, includeWithoutCommunity } = data;

  const user = useAuthUser();

  const communityIds = compact([...data.communityIds, includeWithoutCommunity === true]);

  const { communities, organizations } = useWorkflowPrimaryFilterDirectory(data, {
    actions: { changeFilterFields: changeFields },
  });

  const onChangeCommunityField = useCallback(
    (value) => {
      changeFields(
        {
          communityIds: without(value),
          includeWithoutCommunity: isNotEmpty(value) || null,
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
        isCommunityMultiSelection
        onChangeOrganizationField={changeOrganizationField}
        onChangeCommunityField={onChangeCommunityField}
        className={cn("WorkflowPrimaryFilter", className)}
        data={{ organizationId, communityIds }}
      />
    )
  );
}
