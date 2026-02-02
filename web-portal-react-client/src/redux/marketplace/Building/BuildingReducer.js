import actions from "./actionTypes";
import { initialState } from "./BuildingState";

// const initialState = initialState;

const buildingReducer = (state = initialState, action) => {
  switch (action.type) {
    case actions.LOAD_REQUEST:
      return {
        ...state,
        isFetching: true,
      };

    case actions.LOAD_SUCCESS:
      return {
        ...state,
        isFetching: false,
        data: action.payload.data,
        totalCount: action.payload.totalCount,
      };

    case actions.LOAD_ERROR:
      return {
        ...state,
        isFetching: false,
        error: action.payload,
      };

    case actions.LOAD_DETAIL_REQUEST:
      return {
        ...state,
        isFetching: true,
      };

    case actions.LOAD_DETAIL_SUCESS:
      return {
        ...state,
        isFetching: false,
        detailData: action.payload.data,
      };

    case actions.CLEAR_BUILDING_DETAIL:
      return {
        ...state,
        detailData: action.payload.data,
      };

    case actions.LOAD_DETAIL_ERROR:
      return {
        ...state,
        isFetching: true,
      };

    case actions.IS_LIST_TO_DETAIL:
      return {
        ...state,
        isListToDetail: action.payload,
      };

    default:
      return state;
  }
};

export default buildingReducer;
