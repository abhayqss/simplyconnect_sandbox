import { useSelector } from "react-redux";

import { map, where } from "underscore";

import { useDirectoryData, useStore } from "hooks/common";

import { useBoundActions, usePrimaryFilter } from "hooks/common/redux";

import clientListActions from "redux/client/list/clientListActions";

import { NAME } from "containers/Clients/Clients/ClientPrimaryFilter/ClientPrimaryFilter";
import { NAME as SECOND_FILTER_NAME } from "containers/Clients/Clients/ClientFilter/ClientFilter";

import { isEmpty } from "lib/utils/Utils";

import { useCommunitiesQuery } from "./";

function filterCommunities(data) {
  return where(data, { canViewOrHasAccessibleClient: true });
}

export default function useClientPrimaryFilter() {
  const fields = useSelector((state) => state.client.list.dataSource.filter);

  const { organizationId, communityIds } = fields;

  const actions = useBoundActions(clientListActions);

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

  const communities = useSelector((state) => filterCommunities(state.client.community.list.dataSource.data));

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
