import InitialState from './ServicePlanListInitialState'

import { ACTION_TYPES, PAGINATION } from 'lib/Constants'
import Immutable from 'immutable'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,

    CLEAR_SERVICE_PLAN_LIST_ERROR,

    CLEAR_SERVICE_PLAN_LIST,
    CLEAR_SERVICE_PLAN_LIST_FILTER,
    CHANGE_SERVICE_PLAN_LIST_FILTER,
    CHANGE_SERVICE_PLAN_LIST_FILTER_FIELD,

    LOAD_SERVICE_PLAN_LIST_REQUEST,
    LOAD_SERVICE_PLAN_LIST_SUCCESS,
    LOAD_SERVICE_PLAN_LIST_FAILURE,
    SAVE_SERVICE_PLAN_SUCCESS,

    CHANGE_SERVICE_PLAN_LIST_SORTING
} = ACTION_TYPES

const { FIRST_PAGE } = PAGINATION

const initialState = new InitialState()

export default function organizationListReducer (state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_SERVICE_PLAN_LIST:
            return state.clear()

        case CLEAR_SERVICE_PLAN_LIST_ERROR:
            return state.removeIn(['error'])

        case CLEAR_SERVICE_PLAN_LIST_FILTER: {
            const path = ['dataSource', 'filter']

            return state.setIn(
                path, state.getIn(path).clear()
            ).setIn(['shouldReload'], action.payload ?? true)
        }

        case CHANGE_SERVICE_PLAN_LIST_FILTER: {
            const {
                changes = {}, shouldReload = true
            } = action.payload

            return state
                .setIn(['shouldReload'], shouldReload)
                .mergeIn(['dataSource', 'filter'], changes)
        }

        case CHANGE_SERVICE_PLAN_LIST_FILTER_FIELD: {
            const {
                name, value, shouldReload = true
            } = action.payload

            return state
                .setIn(['shouldReload'], shouldReload)
                .setIn(['dataSource', 'filter', name], value)
        }

        case SAVE_SERVICE_PLAN_SUCCESS: {
            return state.removeIn(['error'])
                .setIn(['shouldReload'], true)
        }

        case  CHANGE_SERVICE_PLAN_LIST_SORTING: {
            const {
                field, order, shouldReload = true
            } = action.payload

            return state
                .setIn(['shouldReload'], shouldReload)
                .setIn(['dataSource', 'sorting', 'field'], field)
                .setIn(['dataSource', 'sorting', 'order'], order)
        }

        case LOAD_SERVICE_PLAN_LIST_REQUEST:
            return state
                .setIn(['error'], null)
                .setIn(['isFetching'], true)
                .setIn(['shouldReload'], false)
                .setIn(['fetchCount'], state.fetchCount + 1)

        case LOAD_SERVICE_PLAN_LIST_SUCCESS: {
            const {
                data,
                page,
                size,
                totalCount
            } = action.payload

            return state
                .setIn(['isFetching'], false)
                .setIn(['shouldReload'], false)
                .setIn(['dataSource', 'data'], data)
                .setIn(['dataSource', 'pagination', 'page'], page)
                .setIn(['dataSource', 'pagination', 'size'], size)
                .setIn(['dataSource', 'pagination', 'totalCount'], totalCount)
        }

        case LOAD_SERVICE_PLAN_LIST_FAILURE:
            return state
                .setIn(['isFetching'], false)
                .setIn(['shouldReload'], false)
                .setIn(['error'], action.payload)
    }

    return state
}
