import { ACTION_TYPES } from "lib/Constants";

import service from "services/DirectoryService";

const {
  CLEAR_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST,
  LOAD_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST_REQUEST,
  LOAD_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST_SUCCESS,
  LOAD_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST_FAILURE,
} = ACTION_TYPES;

export function clear() {
  return { type: CLEAR_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST };
}

export function load(params) {
  return (dispatch) => {
    dispatch({ type: LOAD_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST_REQUEST });

    return service
      .findCareTeamNotificationPreferences(params)
      .then((response) => {
        dispatch({
          type: LOAD_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST_SUCCESS,
          payload: response.data,
        });

        return response;
      })
      .catch((e) => {
        dispatch({ type: LOAD_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST_FAILURE, payload: e });
      });
  };
}
