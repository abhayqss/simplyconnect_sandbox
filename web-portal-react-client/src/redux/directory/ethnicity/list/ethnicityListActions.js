import { ACTION_TYPES } from "lib/Constants";

import service from "services/DirectoryService";

const { LOAD_ETHNICITY_LIST_SUCCESS, CLEAR_ETHNICITY_LIST, LOAD_ETHNICITY_LIST_FAILURE } = ACTION_TYPES;

export function clear() {
  return { type: CLEAR_ETHNICITY_LIST };
}

export function load(params) {
  return (dispatch) => {
    return service
      .findEthnicity(params)
      .then((response) => {
        const { data } = response;

        dispatch({
          type: LOAD_ETHNICITY_LIST_SUCCESS,
          payload: { data },
        });
      })
      .catch((e) => {
        dispatch({ type: LOAD_ETHNICITY_LIST_FAILURE, payload: e });
      });
  };
}
