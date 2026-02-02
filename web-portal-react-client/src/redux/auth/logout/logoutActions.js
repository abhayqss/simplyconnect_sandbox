import storage from "local-storage";

import authService from "services/AuthService";

import { primaryOrganizationStore } from "lib/stores";

import { ACTION_TYPES } from "lib/Constants";

import { fireAbortAllEvent } from "lib/utils/AjaxUtils";
import { QueryClient } from "@tanstack/react-query";

const { LOGOUT_REQUEST, LOGOUT_SUCCESS, LOGOUT_FAILURE, CLEAR_LOGOUT_ERROR, CLEAR_ALL_AUTH_DATA } = ACTION_TYPES;

export function clearError() {
  return { type: CLEAR_LOGOUT_ERROR };
}

export function logout() {
  const queryClient = new QueryClient();

  return (dispatch) => {
    dispatch({ type: LOGOUT_REQUEST });
    return authService
      .logout()
      .then((response) => {
        dispatch({ type: LOGOUT_SUCCESS });
        dispatch({ type: CLEAR_ALL_AUTH_DATA });

        // === 清除所有 react-query 缓存 ===
        queryClient.clear();

        const organization = primaryOrganizationStore.get();

        storage.clear();

        primaryOrganizationStore.save(organization);

        fireAbortAllEvent();

        return response;
      })
      .catch((e) => {
        dispatch({ type: CLEAR_ALL_AUTH_DATA });
        dispatch({ type: LOGOUT_FAILURE, payload: e });
      });
  };
}
