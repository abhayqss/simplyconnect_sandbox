import { ACTION_TYPES } from "lib/Constants";

import service from "services/CareTeamMemberService";

const {
  CLEAR_CARE_TEAM_MEMBER_DETAILS,
  CLEAR_CARE_TEAM_MEMBER_DETAILS_ERROR,

  LOAD_CARE_TEAM_MEMBER_DETAILS_REQUEST,
  LOAD_CARE_TEAM_MEMBER_DETAILS_SUCCESS,
  LOAD_CARE_TEAM_MEMBER_DETAILS_FAILURE,
} = ACTION_TYPES;

export function clear() {
  return {
    type: CLEAR_CARE_TEAM_MEMBER_DETAILS,
  };
}

export function clearError() {
  return {
    type: CLEAR_CARE_TEAM_MEMBER_DETAILS_ERROR,
  };
}
export function load(careTeamMemberId, missedMedicationReminderId, params) {
  return (dispatch) => {
    dispatch({ type: LOAD_CARE_TEAM_MEMBER_DETAILS_REQUEST });
    if (missedMedicationReminderId) {
      return service
        .findById(careTeamMemberId, params)
        .then((response) => {
          let { data } = response;
          // 如果 notificationsPreferences 存在，进行处理
          if (Array.isArray(data.notificationsPreferences)) {
            data.notificationsPreferences = data.notificationsPreferences.map((notification) => {
              if (notification.eventTypeId === missedMedicationReminderId) {
                // 移除 channels 中的 SMS
                notification.channels = notification.channels.filter((channel) => channel !== "SMS");
              }
              return notification;
            });
          }

          dispatch({ type: LOAD_CARE_TEAM_MEMBER_DETAILS_SUCCESS, payload: data });
          return data;
        })
        .catch((e) => {
          dispatch({ type: LOAD_CARE_TEAM_MEMBER_DETAILS_FAILURE, payload: e });
        });
    } else return {};
  };
}
