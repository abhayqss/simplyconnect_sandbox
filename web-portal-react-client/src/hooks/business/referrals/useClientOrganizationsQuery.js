import { useQuery } from "@tanstack/react-query";

import service from "services/ReferralService";

const fetch = (params) => service.findClientOrganizations(params);

function useClientOrganizationsQuery(params, options) {
  return useQuery(["ReferralRequest.ClientOrganizations", params], () => fetch(params), options);
}

export default useClientOrganizationsQuery;
