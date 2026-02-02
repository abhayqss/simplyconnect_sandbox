import { isEqual } from "underscore";

import { useMemoEffect } from "hooks/common";

import { useFilterCombination } from "hooks/common/filter";

import { useWorkflowFilterDefaultDataCache } from "./";

let organizationId;

export default function useWorkflowFilterCombination(primary, custom, data) {
  const cache = useWorkflowFilterDefaultDataCache();

  const combination = useFilterCombination({
    name: "WORKFLOW_PRIMARY_FILTER",
    customFields: { includeWithoutCommunity: null },
    ...primary,
    onChange: (data) => {
      primary.onChange(data);
      organizationId = data.organizationId;
    },
  });

  const { apply } = combination.primary;

  useMemoEffect(
    (memo) => {
      const prev = memo();

      if (
        prev &&
        isEqual(data.communityIds, prev.communityIds) &&
        data.includeWithoutCommunity !== prev.includeWithoutCommunity
      ) {
        apply();
      }

      memo(data);
    },
    [data, apply],
  );
  return combination;
}
