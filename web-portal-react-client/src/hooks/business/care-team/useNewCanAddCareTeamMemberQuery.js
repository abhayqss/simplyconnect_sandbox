import { useQuery } from "@tanstack/react-query";

import service from "services/CareTeamMemberService";

const fetch = (params) =>
  service.canNewAdd(params, {
    response: { extractDataOnly: true },
  });

function useNewCanAddCareTeamMemberQuery(params) {
  return useQuery(["NewCanAddCareTeamMember"], () => fetch(params));
}

export default useNewCanAddCareTeamMemberQuery;
