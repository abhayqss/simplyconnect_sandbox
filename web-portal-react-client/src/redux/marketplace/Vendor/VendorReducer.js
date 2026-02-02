import { VendorState } from "./VendorState";
import actions from "./actionTypes";

const vendorReducer = (state = VendorState, action) => {
  switch (action.type) {
    case actions.LOAD_REQUEST:
      return {
        ...state,
        isFetching: true,
      };

    case actions.LOAD_SUCCESS:
      return {
        ...state,
        vendorList: action.payload.data,
        vendorTotal: action.payload.totalCount,
      };

    case actions.LOAD_ERROR:
      return {
        ...state,
        isFetching: false,
        error: action.payload,
      };

    case actions.LOAD_TYPE_SUCESS:
      return {
        ...state,
        vendorTypeData: action.payload.data,
      };

    case actions.LOAD_VENDOR_DETAIL_SUCESS:
      return {
        ...state,
        vendorDetail: action.payload.data,
      };

    case actions.CLEAR_VENDOR_DETAIL:
      return {
        ...state,
        vendorDetail: action.payload.data,
      };

    case actions.LOAD_STATE_SUCCESS:
      return {
        ...state,
        stateOptions: action.payload,
      };

    case actions.LOAD_SIMPLY_NEXUS_SUCESS:
      return {
        ...state,
        simplyNexus: action.payload.data,
      };

    case actions.LOAD_CATEGORY_SUCCESS:
      return {
        ...state,
        category: action.payload,
      };
    case actions.LOAD_ALL_VENDOR_SUCCESS:
      return {
        ...state,
        allVendor: action.payload,
      };

    default:
      return state;
  }
};

export default vendorReducer;
