import { useQuery } from "@tanstack/react-query";

import service from "services/ReferralService";

const fetch = (params) => service.findClientCommunities(params);

function useClientCommunitiesQuery(params, options, isFromVendor) {
  return useQuery(["ReferralRequest.ClientCommunities", params], () => fetch(params), options);
}

export default useClientCommunitiesQuery;
