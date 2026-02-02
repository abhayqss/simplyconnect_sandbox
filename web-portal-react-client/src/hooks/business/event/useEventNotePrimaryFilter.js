import { map, where } from "underscore";

import { useSelector } from "react-redux";

import { useDirectoryData, useStore } from "hooks/common";
import { useBoundActions, usePrimaryFilter } from "hooks/common/redux";

import eventNoteComposedListActions from "redux/event/note/composed/list/eventNoteComposedListActions";

import { NAME } from "containers/Events/EventNotePrimaryFilter/EventNotePrimaryFilter";
import { NAME as SECOND_FILTER_NAME } from "containers/Events/EventNoteFilter/EventNoteFilter";

import { isEmpty } from "lib/utils/Utils";

import { useCommunitiesQuery } from "./index";

function filterCommunities(data) {
  return where(data, { canViewOrHasAccessibleClient: true });
}

export default function useEventNotePrimaryFilter() {
  const fields = useSelector((state) => state.event.note.composed.list.dataSource.filter);

  const { organizationId, communityIds } = fields;

  const actions = useBoundActions(eventNoteComposedListActions);

  const { changeFilter: change, changeFilterField: changeField } = actions;

  const store = useStore();

  const isSaved = !!store.get(SECOND_FILTER_NAME);

  const config = usePrimaryFilter(NAME, fields, actions, {
    onRestored: () => {
      !isSaved && change({});
    },
  });

  const { organizations } = useDirectoryData({
    organizations: ["organization"],
  });

  const communities = useSelector((state) => filterCommunities(state.event.community.list.dataSource.data));

  useCommunitiesQuery(
    { organizationId },
    {
      onSuccess: ({ data }) => {
        changeField(
          "communityIds",
          isEmpty(communityIds) ? map(filterCommunities(data), (o) => o.id) : communityIds,
          false,
          true,
        );
      },
    },
  );

  return {
    ...config,
    communities,
    organizations,
  };
}
