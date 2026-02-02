import initialState from "./tableDataState";
import { SET_INBOUND_DATA, SET_PAGINATION, SET_SORT, SET_OUTBOUND_DATA } from "./actionTypes";

function dataReducer(state = initialState, action) {
  switch (action.type) {
    case SET_INBOUND_DATA:
      return {
        ...state,
        inboundData: action.payload,
      };
    case SET_PAGINATION:
      return {
        ...state,
        pagination: action.payload,
      };

    case SET_SORT:
      return {
        ...state,
        sorting: action.payload,
      };

    case SET_OUTBOUND_DATA:
      return {
        ...state,
        outboundData: action.payload,
      };

    default:
      return state;
  }
}

export default dataReducer;
