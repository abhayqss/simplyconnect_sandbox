import { ACTION_TYPES } from "lib/Constants";

import service from "services/CareTeamMemberService";
import careTeamFormValidator from "validators/CareTeamFormValidator";

const {
  CLEAR_CARE_TEAM_MEMBER_FORM,
  CLEAR_CARE_TEAM_MEMBER_FORM_ERROR,

  CHANGE_CARE_TEAM_MEMBER_FORM_FIELD,
  CHANGE_CARE_TEAM_MEMBER_FORM_FIELDS,

  VALIDATE_CARE_TEAM_MEMBER_FORM_SUCCESS,
  VALIDATE_CARE_TEAM_MEMBER_FORM_FAIL,

  SAVE_CARE_TEAM_MEMBER_REQUEST,
  SAVE_CARE_TEAM_MEMBER_SUCCESS,
  CHANGE_CARE_TEAM_MEMBER_NOTIFICATION_PREFERENCE,
  CHANGE_CARE_TEAM_MEMBER_NOTIFICATION_PREFERENCES,
  CHANGE_CARE_TEAM_MEMBER_ALL_NOTIFICATION_RESPONSIBILITIES,
  CHANGE_CARE_TEAM_MEMBER_ALL_NOTIFICATION_CHANNELS,
  CHANGE_ERROR,
} = ACTION_TYPES;

export function clear() {
  return { type: CLEAR_CARE_TEAM_MEMBER_FORM };
}

export function clearError() {
  return { type: CLEAR_CARE_TEAM_MEMBER_FORM_ERROR };
}

export function changeField(field, value, shouldUpdateHashCode = false) {
  return (dispatch) => {
    dispatch({
      type: CHANGE_CARE_TEAM_MEMBER_FORM_FIELD,
      payload: { field, value, shouldUpdateHashCode },
    });
  };
}

export function changeFields(changes, shouldUpdateHashCode = false) {
  return (dispatch) => {
    dispatch({
      type: CHANGE_CARE_TEAM_MEMBER_FORM_FIELDS,
      payload: { changes, shouldUpdateHashCode },
    });
  };
}

export function changeNotificationPreferences(changes) {
  return (dispatch) => {
    dispatch({
      type: CHANGE_CARE_TEAM_MEMBER_NOTIFICATION_PREFERENCES,
      payload: changes,
    });
  };
}

export function changeNotificationPreference(property, eventTypeId, value) {
  return (dispatch) => {
    return dispatch({
      type: CHANGE_CARE_TEAM_MEMBER_NOTIFICATION_PREFERENCE,
      payload: { property, eventTypeId, value },
    });
  };
}

export function changeAllNotificationResponsibilities(value) {
  return (dispatch) => {
    return dispatch({
      type: CHANGE_CARE_TEAM_MEMBER_ALL_NOTIFICATION_RESPONSIBILITIES,
      payload: { value },
    });
  };
}

export function changeAllNotificationChannels(channels, missedMedicationReminderId, options) {
  return (dispatch) => {
    return dispatch({
      type: CHANGE_CARE_TEAM_MEMBER_ALL_NOTIFICATION_CHANNELS,
      payload: { channels, missedMedicationReminderId, options },
    });
  };
}

export function validate(data) {
  return (dispatch) => {
    return careTeamFormValidator
      .validate(data)
      .then((success) => {
        dispatch({ type: VALIDATE_CARE_TEAM_MEMBER_FORM_SUCCESS });

        return success;
      })
      .catch((errors) => {
        dispatch({
          type: VALIDATE_CARE_TEAM_MEMBER_FORM_FAIL,
          payload: { errors },
        });
      });
  };
}

export function submit(member) {
  return (dispatch) => {
    dispatch({ type: SAVE_CARE_TEAM_MEMBER_REQUEST });

    return service
      .save(member)
      .then((response) => {
        dispatch({ type: SAVE_CARE_TEAM_MEMBER_SUCCESS, payload: response });

        return response;
      })
      .catch((e) => {
        dispatch({ type: CHANGE_ERROR, payload: e });

        return e;
      });
  };
}
