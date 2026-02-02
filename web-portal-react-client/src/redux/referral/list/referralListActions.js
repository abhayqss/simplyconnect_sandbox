import { Actions } from "redux/utils/List";

import actionTypes from "./actionTypes";

import service from "services/ReferralService";
import { SET_OUTBOUND_TOTAL } from "../../ReferralData/actionTypes";

export default Actions({
  actionTypes,
  doLoad: (params, options) => service.find(params, options),
});
