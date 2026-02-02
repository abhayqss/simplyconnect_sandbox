import { ACTION_TYPES } from 'lib/Constants'

import { promise } from 'lib/utils/Utils'

import service from 'services/ServicePlanService'
import validator from 'validators/ServicePlanFormValidator'

const {
    CLEAR_SERVICE_PLAN_FORM,
    CLEAR_SERVICE_PLAN_FORM_ERROR,

    CHANGE_SERVICE_PLAN_FORM_TAB,

    CHANGE_SERVICE_PLAN_FORM_FIELD,
    CHANGE_SERVICE_PLAN_FORM_FIELDS,

    VALIDATE_SERVICE_PLAN_FORM,

    SAVE_SERVICE_PLAN_REQUEST,
    SAVE_SERVICE_PLAN_SUCCESS,
    SAVE_SERVICE_PLAN_FAILURE,

    ADD_SERVICE_PLAN_FORM_NEED,
    CLEAR_SERVICE_PLAN_FORM_NEED,
    REMOVE_SERVICE_PLAN_FORM_NEED,
    CHANGE_SERVICE_PLAN_FORM_NEED_FIELD,
    CHANGE_SERVICE_PLAN_FORM_NEED_FIELDS,

    ADD_SERVICE_PLAN_FORM_GOAL,
    REMOVE_SERVICE_PLAN_FORM_GOAL,
    CHANGE_SERVICE_PLAN_FORM_GOAL_FIELD,
    CHANGE_SERVICE_PLAN_FORM_GOAL_FIELDS,

    CHANGE_SERVICE_PLAN_FORM_SCORE
} = ACTION_TYPES

export function clear() {
    return { type: CLEAR_SERVICE_PLAN_FORM }
}

export function clearError() {
    return { type: CLEAR_SERVICE_PLAN_FORM_ERROR }
}

export function changeTab(tab) {
    return {
        type: CHANGE_SERVICE_PLAN_FORM_TAB,
        payload: tab
    }
}

export function changeField(name, value) {
    return dispatch => {
        return promise(
            dispatch({
                type: CHANGE_SERVICE_PLAN_FORM_FIELD,
                payload: { name, value }
            })
        )
    }
}

export function changeFields(changes, shouldUpdateHashCode) {
    return dispatch => {
        return promise(
            dispatch({
                type: CHANGE_SERVICE_PLAN_FORM_FIELDS,
                payload: { changes, shouldUpdateHashCode }
            })
        )
    }
}

export function addNeed(index) {
    return dispatch => {
        return promise(
            dispatch({
                type: ADD_SERVICE_PLAN_FORM_NEED,
                payload: index
            })
        )
    }
}

export function clearNeed(index) {
    return dispatch => {
        return promise(
            dispatch({
                type: CLEAR_SERVICE_PLAN_FORM_NEED,
                payload: index
            })
        )
    }
}

export function removeNeed(index) {
    return dispatch => {
        return promise(
            dispatch({
                type: REMOVE_SERVICE_PLAN_FORM_NEED,
                payload: index
            })
        )
    }
}

export function changeNeedField(index, name, value) {
    return dispatch => {
        return promise(
            dispatch({
                type: CHANGE_SERVICE_PLAN_FORM_NEED_FIELD,
                payload: { index, name, value }
            })
        )
    }
}

export function changeNeedFields(index, changes) {
    return dispatch => {
        return promise(
            dispatch({
                type: CHANGE_SERVICE_PLAN_FORM_NEED_FIELDS,
                payload: { index, changes }
            })
        )
    }
}

export function addGoal(index, needIndex) {
    return dispatch => {
        return promise(
            dispatch({
                type: ADD_SERVICE_PLAN_FORM_GOAL,
                payload: { needIndex, index }
            })
        )
    }
}

export function removeGoal(index, needIndex) {
    return {
        type: REMOVE_SERVICE_PLAN_FORM_GOAL,
        payload: { index, needIndex }
    }
}

export function changeGoalField(index, needIndex, name, value) {
    return dispatch => {
        return promise(
            dispatch({
                type: CHANGE_SERVICE_PLAN_FORM_GOAL_FIELD,
                payload: { index, needIndex, name, value }
            })
        )
    }
}

export function changeGoalFields(index, needIndex, changes) {
    return dispatch => {
        return promise(
            dispatch({
                type: CHANGE_SERVICE_PLAN_FORM_GOAL_FIELDS,
                payload: { index, needIndex, changes }
            })
        )
    }
}

export function changeScore(domainId, score) {
    return {
        type: CHANGE_SERVICE_PLAN_FORM_SCORE,
        payload: { domainId, score }
    }
}

export function validate(data, options) {
    return dispatch => {
        return validator.validate(data, options).then(() => {
            dispatch({ type: VALIDATE_SERVICE_PLAN_FORM, payload: { success: true } })
            return true
        }).catch(errors => {
            dispatch({ type: VALIDATE_SERVICE_PLAN_FORM, payload: { success: false, errors } })
            return false
        })
    }
}

export function submit(data, clientId) {
    return dispatch => {
        dispatch({ type: SAVE_SERVICE_PLAN_REQUEST })
        return service.save(data, clientId).then(response => {
            dispatch({ type: SAVE_SERVICE_PLAN_SUCCESS, payload: response })
            return response
        }).catch(e => {
            dispatch({ type: SAVE_SERVICE_PLAN_FAILURE, payload: e })
        })
    }
}
