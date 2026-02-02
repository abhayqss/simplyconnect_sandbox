import { useListState } from "hooks/common";

import Entity from "entities/WorkflowCategory";

export default function useWorkflowListState() {
  return useListState({ filterEntity: Entity });
}
