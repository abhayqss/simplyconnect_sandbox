import { useQuery } from "hooks/common/redux";

import * as actions from "redux/directory/ethnicity/list/ethnicityListActions";

export default function useEthnicityQuery(params = null, options) {
  useQuery(actions, params, options);
}
