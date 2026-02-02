// reducers/vendorReducer.js
import { SET_ORGANIZATION, SET_COMMUNITY } from "./vendorActions";
import { initialState } from "./vendorState";

const vendorReducer = (state = initialState, action) => {
  switch (action.type) {
    case SET_ORGANIZATION:
      return { ...state, organizationId: action.payload };
    case SET_COMMUNITY:
      return { ...state, communityId: action.payload };
    default:
      return state;
  }
};

export default vendorReducer;
