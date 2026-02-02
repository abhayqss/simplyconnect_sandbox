import Immutable from 'immutable'

import InitialState from './CareTeamMemberListInitialState'

import { getDataUrl } from 'lib/utils/Utils'
import Converter from 'lib/converters/Converter'
import factory from 'lib/converters/ConverterFactory'

import { ACTION_TYPES } from 'lib/Constants'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,

    CLEAR_CARE_TEAM_MEMBER_LIST_ERROR,

    CLEAR_CARE_TEAM_MEMBER_LIST,
    CLEAR_CARE_TEAM_MEMBER_LIST_FILTER,
    CHANGE_CARE_TEAM_MEMBER_LIST_FILTER,

    LOAD_CARE_TEAM_MEMBER_LIST_REQUEST,
    LOAD_CARE_TEAM_MEMBER_LIST_SUCCESS,
    LOAD_CARE_TEAM_MEMBER_LIST_FAILURE,

    SAVE_CARE_TEAM_MEMBER_SUCCESS,
    DOWNLOAD_CARE_TEAM_MEMBER_AVATAR_SUCCESS,
    LOAD_DELETE_TEAM_MEMBER_DOCUMENT_SUCCESS,
    CHANGE_CARE_TEAM_MEMBER_LIST_SORTING
} = ACTION_TYPES

const initialState = new InitialState()
const converter = factory.getConverter(Converter.types.BINARY_TO_BASE_64)

export default function careTeamMemberListReducer(state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_CARE_TEAM_MEMBER_LIST:
            return state.clear()

        case CLEAR_CARE_TEAM_MEMBER_LIST_ERROR:
            return state.removeIn(['error'])

        case CLEAR_CARE_TEAM_MEMBER_LIST_FILTER:
            return state
                .removeIn(['dataSource', 'filter', 'name'])
                .setIn(['shouldReload'], true)

        case CHANGE_CARE_TEAM_MEMBER_LIST_FILTER: {
            const { changes, shouldReload = true } = action.payload

            if (changes) {
                return state
                    .mergeIn(['dataSource', 'filter'], Immutable.fromJS(changes))
                    .setIn(['shouldReload'], shouldReload)
            }

            break
        }

        case SAVE_CARE_TEAM_MEMBER_SUCCESS: {
            return state.removeIn(['error'])
                .setIn(['shouldReload'], true)
        }

        case LOAD_CARE_TEAM_MEMBER_LIST_REQUEST:
            return state
                .setIn(['error'], null)
                .setIn(['isFetching'], true)
                .setIn(['shouldReload'], false)
                .setIn(['fetchCount'], state.fetchCount + 1)

        case LOAD_CARE_TEAM_MEMBER_LIST_SUCCESS: {
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

        case LOAD_CARE_TEAM_MEMBER_LIST_FAILURE:
            return state
                .setIn(['isFetching'], false)
                .setIn(['shouldReload'], false)
                .setIn(['error'], action.payload)

        case DOWNLOAD_CARE_TEAM_MEMBER_AVATAR_SUCCESS: {
            const {
                clientId,
                mediaType,
            } = action.payload

            const { data } = state.dataSource
            const targetTeamMember = data.find(o => o.id === clientId)
            const index = data.indexOf(targetTeamMember)

            if (targetTeamMember) {
                return state.setIn(
                    ['dataSource', 'data', index],
                    {
                        ...targetTeamMember,
                        avatarDataUrl: targetTeamMember.id === clientId
                            ? getDataUrl(
                                converter.convert(action.payload.data),
                                mediaType
                            )
                            : targetTeamMember.avatarDataUrl
                    }
                )
            }

            break
        }

        case LOAD_DELETE_TEAM_MEMBER_DOCUMENT_SUCCESS: {
            const { careTeamMemberId } = action.payload
            const { data } = state.dataSource

            return state.setIn(
                ['dataSource', 'data'],
                data.filter(member => member.id !== careTeamMemberId)
            )
        }

        case  CHANGE_CARE_TEAM_MEMBER_LIST_SORTING: {
            const {
                field,
                order,
                shouldReload = true
            } = action.payload

            return state
                .setIn(['shouldReload'], shouldReload)
                .setIn(['dataSource', 'sorting', 'field'], field)
                .setIn(['dataSource', 'sorting', 'order'], order)
        }

    }

    return state
}
