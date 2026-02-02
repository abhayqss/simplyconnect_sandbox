import { each, map, reject } from 'underscore'

import { ACTION_TYPES } from 'lib/Constants'

import { updateFieldErrors } from '../../../utils/Form'

import InitialState from './ServicePlanFormInitialState'
import NeedSectionInitialState from './NeedSectionInitialState'
import GoalSectionInitialState from './GoalSectionInitialState'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,

    CLEAR_SERVICE_PLAN_FORM,
    CLEAR_SERVICE_PLAN_FORM_ERROR,

    CHANGE_SERVICE_PLAN_FORM_TAB,

    CHANGE_SERVICE_PLAN_FORM_FIELD,
    CHANGE_SERVICE_PLAN_FORM_FIELDS,

    VALIDATE_SERVICE_PLAN_FORM,

    LOAD_SERVICE_PLAN_DETAILS_SUCCESS,

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

function changeFields ({ changes, shouldUpdateHashCode }, state) {
    const needs = []

    const need = NeedSectionInitialState()
    const goal = GoalSectionInitialState()

    each(changes.needs, (n, i) => {
        const goals = []

        each(n.fields.goals, (g, j) => {
            goals.push(goal.mergeDeep({
                index: j, needIndex: i, ...g
            }))
        })

        needs.push(need.mergeDeep({
            index: i, fields: { ...n.fields, goals }
        }))
    })

    return state.mergeDeep({
        fields: { ...changes, needs }
    }).updateHashCodeIf(shouldUpdateHashCode)
}

const initialState = new InitialState()

export default function servicePlanFormReducer(state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_SERVICE_PLAN_FORM:
            return state.clear()

        case CLEAR_SERVICE_PLAN_FORM_ERROR:
            return state.removeIn(['error'])

        case CHANGE_SERVICE_PLAN_FORM_TAB:
            return state.setIn(['tab'], action.payload)

        case CHANGE_SERVICE_PLAN_FORM_FIELD: {
            const { name, value } = action.payload
            return state.setIn(['fields', ...name.split('.')], value)
        }

        case CHANGE_SERVICE_PLAN_FORM_FIELDS: {
            return changeFields(action.payload, state)
        }

        case ADD_SERVICE_PLAN_FORM_NEED: {
            const path = ['fields', 'needs']
            const needs = state.getIn(path)

            const need = NeedSectionInitialState({
                index: needs.size
            })

            return state.setIn(path, needs.push(need))
        }

        case CLEAR_SERVICE_PLAN_FORM_NEED: {
            const index = action.payload
            const path = ['fields', 'needs', index]

            return state.setIn(path, state.getIn(path).clear())
        }

        case REMOVE_SERVICE_PLAN_FORM_NEED: {
            const index = action.payload

            const path = ['fields', 'needs']
            let needs = state.getIn(path).removeIn([index])

            each(needs, (n, i) => {
                needs = needs.setIn([i, 'index'], i)
            })

            return state.setIn(path, needs)
        }

        case CHANGE_SERVICE_PLAN_FORM_NEED_FIELD: {
            const { index, name, value } = action.payload

            return state.setIn(
                ['fields', 'needs', index, 'fields', name], value
            )
        }

        case CHANGE_SERVICE_PLAN_FORM_NEED_FIELDS: {
            const { index, changes } = action.payload

            return state.mergeDeepIn(
                ['fields', 'needs', index, 'fields'], changes
            )
        }

        case CHANGE_SERVICE_PLAN_FORM_SCORE: {
            const {
                domainId = 0, score = 0
            } = action.payload

            return state.setIn(
                ['fields', 'scoring'],
                [...reject(
                    state.fields.scoring,
                    o => o.domainId === domainId
                ), { domainId, score }]
            )
        }

        case ADD_SERVICE_PLAN_FORM_GOAL: {
            const { needIndex } = action.payload

            const needsPath = ['fields', 'needs']
            const needs = state.getIn(needsPath)

            const goalsPath = [needIndex, 'fields', 'goals']
            let goals = needs.getIn(goalsPath)

            const goal = GoalSectionInitialState({
                index: goals.size,
                needIndex: needIndex
            })

            return state.setIn(
                needsPath, needs.setIn(goalsPath, goals.push(goal))
            )
        }

        case REMOVE_SERVICE_PLAN_FORM_GOAL: {
            const { index, needIndex } = action.payload

            const needsPath = ['fields', 'needs']
            const needs = state.getIn(needsPath)

            const goalsPath = [needIndex, 'fields', 'goals']
            let goals = needs.getIn(goalsPath).removeIn([index])

            each(goals, (g, i) => {
                goals = goals.setIn([i, 'index'], i)
            })

            return state.setIn(needsPath, needs.setIn(goalsPath, goals))
        }

        case CHANGE_SERVICE_PLAN_FORM_GOAL_FIELD: {
            const {
                index, needIndex, name, value
            } = action.payload

            return state.setIn(
                [
                    'fields',
                    'needs',
                    needIndex,
                    'fields',
                    'goals',
                    index,
                    'fields',
                    name
                ],
                value
            )
        }

        case CHANGE_SERVICE_PLAN_FORM_GOAL_FIELDS: {
            const { index, needIndex, changes } = action.payload

            return state.mergeDeepIn(
                [
                    'fields',
                    'needs',
                    needIndex,
                    'fields',
                    'goals',
                    index,
                    'fields'
                ],
                changes
            )
        }

        case LOAD_SERVICE_PLAN_DETAILS_SUCCESS: {
            return changeFields({
                changes: {
                    ...action.payload,
                    needs: map(action.payload.needs, (need, i) => ({
                        index: i,
                        fields: {
                            ...need,
                            goals: map(need.goals, (goal, j) => ({
                                index: j,
                                needIndex: i,
                                fields: goal
                            }))
                        }
                    }))
                },
                shouldUpdateHashCode: true
            }, state)
        }

        case VALIDATE_SERVICE_PLAN_FORM: {
            const { success, errors = {} } = action.payload

            return state
                .setIn(['isValid'], success)
                .setIn(['error'], !success ? errors : null)
                .setIn(['fields'], updateFieldErrors(state.fields, errors))
        }

        case SAVE_SERVICE_PLAN_REQUEST:
            return state.removeIn(['error'])
                        .setIn(['isFetching'], true)


        case SAVE_SERVICE_PLAN_SUCCESS:
            return state.setIn(['isFetching'], false)


        case SAVE_SERVICE_PLAN_FAILURE:
            return state.setIn(['error'], action.payload)
                        .setIn(['isFetching'], false)

    }

    return state
}
