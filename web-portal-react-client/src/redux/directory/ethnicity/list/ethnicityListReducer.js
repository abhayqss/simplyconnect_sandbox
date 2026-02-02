import InitialState from "./ethnicityListInitialState";

import { ACTION_TYPES } from "lib/Constants";

const {
  LOGOUT_SUCCESS,
  CLEAR_ALL_AUTH_DATA,

  CLEAR_ETHNICITY_LIST,
  LOAD_ETHNICITY_LIST_SUCCESS,
  LOAD_ETHNICITY_LIST_FAILURE,
} = ACTION_TYPES;

const initialState = new InitialState();

export default function ethnicityListReducer(state = initialState, action) {
  if (!(state instanceof InitialState)) {
    return initialState.mergeDeep(state);
  }

  switch (action.type) {
    case LOGOUT_SUCCESS:
    case CLEAR_ALL_AUTH_DATA:
    case CLEAR_ETHNICITY_LIST:
      return state.setIn(["dataSource", "data"], []).removeIn(["error"]);

    case LOAD_ETHNICITY_LIST_SUCCESS: {
      const { data } = action.payload;

      const existingData = state.getIn(["dataSource", "data"]);
      
      return state.setIn(["shouldReload"], !existingData).setIn(["dataSource", "data"], data);
    }

    case LOAD_ETHNICITY_LIST_FAILURE:
      return state.setIn(["error"], action.payload);
  }

  return state;
}
