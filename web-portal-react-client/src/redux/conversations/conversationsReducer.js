import { any, reject } from 'underscore'

import { Message, User } from 'factories'
import { Message as MessageEntity } from 'entities/conversation'

import { ACTION_TYPES } from 'lib/Constants'

import actionTypes from './conversationsActionTypes'
import InitialState from './ConversationsInitialState'

const { Map, List } = require('immutable')

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,
} = ACTION_TYPES

const {
    SET_CONVERSATIONS_CONNECTION_STATUS,

    INIT_CONVERSATIONS_REQUEST,
    INIT_CONVERSATIONS_SUCCESS,
    INIT_CONVERSATIONS_FAILURE,

    SET_LAST_SELECTED_CONVERSATION,
    SET_LAST_CONVERSATION_MESSAGE,

    ADD_UNSENT_CONVERSATION_MESSAGE,
    REMOVE_UNSENT_CONVERSATION_MESSAGE,

    SET_CONVERSATION_USER_ONLINE,
    LOAD_CONVERSATION_USERS_REQUEST,
    LOAD_CONVERSATION_USERS_SUCCESS,
    LOAD_CONVERSATION_USERS_FAILURE,

    DESTROY_CONVERSATIONS_REQUEST,
    DESTROY_CONVERSATIONS_SUCCESS,
    DESTROY_CONVERSATIONS_FAILURE,

    UPDATE_LIVE_CONVERSATIONS,
} = actionTypes

const initialState = new InitialState()

export default function conversationsReducer (state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
            return state.clear()

        case INIT_CONVERSATIONS_REQUEST:
            return state.set('isInitializing', true)

        case INIT_CONVERSATIONS_SUCCESS:
            return state
                .set('isReady', true)
                .set('isInitializing', false)
                .set('connectionStatus', 'connected')
                .set('currentUser', User(action.payload))

        case INIT_CONVERSATIONS_FAILURE:
            return state
                .set('isReady', false)
                .set('isInitializing', false)

        case SET_CONVERSATIONS_CONNECTION_STATUS:
            return state.set('connectionStatus', action.payload)

        case SET_LAST_SELECTED_CONVERSATION:
            return state.set(['sidOfLastSelected'], action.payload)

        case SET_LAST_CONVERSATION_MESSAGE: {
            const message = Message(action.payload)
            return state.setIn(['lastMessages', message.conversationSid], message)
        }

        case ADD_UNSENT_CONVERSATION_MESSAGE: {
            const { sid } = action.payload
            const messages = state.unsentMessages.get(sid)
            const message = new MessageEntity(action.payload.message)

            return !messages ? (
                state.setIn(['unsentMessages', sid], List([message]))
            ) : (
                state.setIn(['unsentMessages', sid, messages.size], message)
            )
        }

        case REMOVE_UNSENT_CONVERSATION_MESSAGE: {
            const { sid } = action.payload
            const index = state.unsentMessages.get(sid).findIndex(
                o => o.index === action.payload.index
            )

            return state.removeIn(['unsentMessages', sid, index])
        }

        case LOAD_CONVERSATION_USERS_REQUEST: {
            return state.setIn(['users', 'isFetching'], true)
        }

        case SET_CONVERSATION_USER_ONLINE: {
            const {
                identity, isOnline
            } = action.payload

            const identities = state.onlineUserIdentities
            const includes = identities.includes(identity)

            if (isOnline && !includes) {
                return state.set('onlineUserIdentities', identities.push(identity))
            }

            if (!isOnline && includes) {
                return state.removeIn(['onlineUserIdentities', identities.indexOf(identity)])
            }

            return state
        }

        case LOAD_CONVERSATION_USERS_SUCCESS: {
            let map = Map(action.payload.reduce((result, o) => ({
                ...result,
                [o.identity]: o
            }), {}))

            return state
                .setIn(['users', 'isFetching'], false)
                .mergeIn(['users', 'data'], map)
        }

        case LOAD_CONVERSATION_USERS_FAILURE: {
            return state
                .setIn(['users', 'isFetching'], false)
                .setIn(['users', 'error'], action.payload)
        }

        case DESTROY_CONVERSATIONS_REQUEST:
            return state.set('isDestroying', true)

        case DESTROY_CONVERSATIONS_SUCCESS:
        case DESTROY_CONVERSATIONS_FAILURE:
            return state
                .set('isReady', false)
                .set('isDestroying', false)

        case UPDATE_LIVE_CONVERSATIONS:
            return state.set('liveConversationSids', List(action.payload))
    }

    return state
}
