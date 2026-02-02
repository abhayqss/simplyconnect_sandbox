// import { initialState } from "./AssociationsState";
import initialState from "./AssociationsState";
import actions from "./actionTypes";

const AssociationsReducer = (state = initialState, action) => {
  switch (action.type) {
    case actions.LOAD_ASSOCIATIONS_LIST:
      return {
        ...state,
        AssociationsList: action.payload.data,
        AssociationsListTotal: {
          size: 10,
          totalCount: action.payload.totalCount,
        },
      };

    case actions.ADD_ASSOCIATIONS:
      return {
        ...state,
      };

    case actions.LOAD_ASSOCIATIONS_DETAIL:
      return {
        ...state,
        AssociationDetail: action.payload.data,
      };
    case actions.CLEAR_ASSOCIATIONS_DETAIL:
      return {
        ...state,
        AssociationDetail: [],
      };

    default:
      return state;
  }
};

export default AssociationsReducer;
