import { useQuery } from "@tanstack/react-query";

import service from "services/CommunityService";

const fetch = (params) => service.findServices(params);

function useServicesQuery(params, options, isVendor) {
  return useQuery(
    ["Community.Service", params],
    () => {
      if (params.organizationId && params.communityId) {
        return fetch(params);
      }
      return new Promise((resolve) => resolve());
    },
    options,
  );
}

export default useServicesQuery;
