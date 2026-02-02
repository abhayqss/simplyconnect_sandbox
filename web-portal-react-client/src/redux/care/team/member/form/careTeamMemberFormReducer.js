import { omit } from 'underscore'

import { isNotEmpty } from 'lib/utils/Utils'

import {
    ACTION_TYPES,
    NOTIFICATION_RESPONSIBILITY_TYPES
} from 'lib/Constants'

import InitialState from './CareTeamMemberFormInitialState'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,
    CHANGE_ERROR,

    CLEAR_CARE_TEAM_MEMBER_FORM,
    CLEAR_CARE_TEAM_MEMBER_FORM_ERROR,

    CHANGE_CARE_TEAM_MEMBER_FORM_FIELD,
    CHANGE_CARE_TEAM_MEMBER_FORM_FIELDS,

    VALIDATE_CARE_TEAM_MEMBER_FORM_SUCCESS,
    VALIDATE_CARE_TEAM_MEMBER_FORM_FAIL,

    LOAD_CARE_TEAM_MEMBER_DETAILS_REQUEST,
    LOAD_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST_REQUEST,

    SAVE_CARE_TEAM_MEMBER_REQUEST,
    SAVE_CARE_TEAM_MEMBER_SUCCESS,
    SAVE_CARE_TEAM_MEMBER_FAILURE,
    CHANGE_CARE_TEAM_MEMBER_NOTIFICATION_PREFERENCE,
    CHANGE_CARE_TEAM_MEMBER_NOTIFICATION_PREFERENCES,
    CHANGE_CARE_TEAM_MEMBER_ALL_NOTIFICATION_RESPONSIBILITIES,
    CHANGE_CARE_TEAM_MEMBER_ALL_NOTIFICATION_CHANNELS,
    LOAD_CARE_TEAM_MEMBER_DETAILS_SUCCESS,
    LOAD_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST_SUCCESS
} = ACTION_TYPES

const {
    VIEWABLE,
    RESPONSIBLE,
    NOT_VIEWABLE
} = NOTIFICATION_RESPONSIBILITY_TYPES

const RESPONSIBILITY_CHANGE_PERMISSIONS = {
    [VIEWABLE]: true,
    [NOT_VIEWABLE]: false,
    [RESPONSIBLE]: false
}

const initialState = new InitialState()

export default function memberFormReducer(state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_CARE_TEAM_MEMBER_FORM:
            return state.clear()

        case CLEAR_CARE_TEAM_MEMBER_FORM_ERROR:
            return state.setError(null)

        case CHANGE_CARE_TEAM_MEMBER_FORM_FIELD: {
            const { field, value, shouldUpdateHashCode } = action.payload

            return state
                .changeField(field, value)
                .updateHashCodeIf(shouldUpdateHashCode)
        }

        case LOAD_CARE_TEAM_MEMBER_DETAILS_SUCCESS: {
            const data = action.payload.notificationsPreferences
            if (data) state = state.changeNotificationPreferences(data, true)

            return state
                .mergeDeep({
                    fields: omit(
                        action.payload,
                        'notificationsPreferences'
                    )
                })
                .setFetching(false)
                .updateHashCodeIf(true)
        }

        case CHANGE_CARE_TEAM_MEMBER_FORM_FIELDS: {
            const { changes, shouldUpdateHashCode } = action.payload

            return state.mergeDeep({
                fields: omit(changes, 'notificationsPreferences')
            }).updateHashCodeIf(shouldUpdateHashCode)
        }

        case CHANGE_CARE_TEAM_MEMBER_NOTIFICATION_PREFERENCES:
            return state.changeNotificationPreferences(action.payload)

        case LOAD_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST_SUCCESS:
            return state
                .clearValidation()
                .setFetching(false)

        case CHANGE_CARE_TEAM_MEMBER_NOTIFICATION_PREFERENCE: {
            const { property, eventTypeId, value } = action.payload
            const { notificationsPreferences } = state.fields

            const index = notificationsPreferences.findIndex(
                np => np.eventTypeId === eventTypeId
            )

            return state.changeNotificationPreferences(
                notificationsPreferences.update(index, np => {
                    return np.set(property, value)
                }).toJS()
            )
        }

        case CHANGE_CARE_TEAM_MEMBER_ALL_NOTIFICATION_RESPONSIBILITIES: {
            return state.changeNotificationPreferences(
                state.fields.notificationsPreferences.map(np => ({
                    ...np.toJS(),
                    responsibilityName: (
                        np.canEdit || RESPONSIBILITY_CHANGE_PERMISSIONS[np.responsibilityName]
                    ) ? action.payload.value : np.responsibilityName
                })).toJS()
            )
        }

        case CHANGE_CARE_TEAM_MEMBER_ALL_NOTIFICATION_CHANNELS: {
            const {
                excludeDisabled,
                excludeEditableAndWithChannels
            } = action.payload.options || {}

            return state.changeNotificationPreferences(
                state.fields.notificationsPreferences.map(np => {
                    const isNoChannels = (
                        [VIEWABLE, NOT_VIEWABLE].includes(
                            np.responsibilityName
                        )
                    )

                    return ({
                        ...np.toJS(),
                        channels: (
                            np.canEdit ? !(
                                excludeEditableAndWithChannels
                                && isNotEmpty(np.channels)
                            ) : !excludeDisabled
                        ) ? (
                            !isNoChannels ? action.payload.channels : []
                        ) : np.channels
                    })
                }).toJS()
            )
        }

        case VALIDATE_CARE_TEAM_MEMBER_FORM_FAIL: {
            const { errors } = action.payload

            return state
                .setValid(false)
                .setFieldErrors(errors)
        }

        case VALIDATE_CARE_TEAM_MEMBER_FORM_SUCCESS:
            return state
                .setValid(true)
                .clearValidation()

        case LOAD_CARE_TEAM_MEMBER_DETAILS_REQUEST:
        case LOAD_CARE_TEAM_NOTIFICATION_PREFERENCE_LIST_REQUEST:
        case SAVE_CARE_TEAM_MEMBER_REQUEST: {
            return state.setFetching(true)
        }

        case SAVE_CARE_TEAM_MEMBER_SUCCESS: {
            return state.setFetching(false)
        }

        case SAVE_CARE_TEAM_MEMBER_FAILURE: {
            return state
                .setError(action.payload)
                .setFetching(false)
        }

        case CHANGE_ERROR:
            return state.setFetching(false)
    }

    return state
}
