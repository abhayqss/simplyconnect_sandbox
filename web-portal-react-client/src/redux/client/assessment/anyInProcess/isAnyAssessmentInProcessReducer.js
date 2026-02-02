import { ACTION_TYPES } from "lib/Constants";

import InitialState from "./IsAnyAssessmentInProcessInitialState";

const {
  LOGOUT_SUCCESS,
  CLEAR_ALL_AUTH_DATA,

  CLEAR_IS_ANY_ASSESSMENT_IN_PROCESS,
  CLEAR_IS_ANY_ASSESSMENT_IN_PROCESS_ERROR,
  LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_REQUEST,
  LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_SUCCESS,
  LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_FAILURE,
} = ACTION_TYPES;

const initialState = new InitialState();

export default function isAnyAssessmentInProcessReducer(state = initialState, action) {
  if (!(state instanceof InitialState)) {
    return initialState.mergeDeep(state);
  }

  switch (action.type) {
    case LOGOUT_SUCCESS:
    case CLEAR_ALL_AUTH_DATA:
    case CLEAR_IS_ANY_ASSESSMENT_IN_PROCESS:
      return state
        .removeIn(["error"])
        .setIn(["isFetching"], false)
        .setIn(["shouldReload"], action.payload || false)
        .removeIn(["value"]);

    case CLEAR_IS_ANY_ASSESSMENT_IN_PROCESS_ERROR:
      return state.removeIn(["error"]);

    case LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_REQUEST: {
      return state.setIn(["error"], null).setIn(["shouldReload"], false);
    }

    case LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_SUCCESS:
      return state.removeIn(["error"]).setIn(["value"], action.payload);

    case LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_FAILURE:
      return state.setIn(["error"], action.payload).setIn(["shouldReload"], false);
  }

  return state;
}
