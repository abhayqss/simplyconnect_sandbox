import actionTypes from './conversationsActionTypes'

import Service from 'factories/ConversationService'

const service = Service()

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

export default {
    init: (options) => {
        return dispatch => {
            dispatch({ type: INIT_CONVERSATIONS_REQUEST })
            return service.init(options).then((user) => {
                dispatch({ type: INIT_CONVERSATIONS_SUCCESS, payload: user })
            }).catch(e => {
                dispatch({ type: INIT_CONVERSATIONS_FAILURE, payload: e })
            })
        }
    },
    destroy: () => {
        return dispatch => {
            dispatch({ type: DESTROY_CONVERSATIONS_REQUEST })
            return service.shutdown().then(() => {
                dispatch({ type: DESTROY_CONVERSATIONS_SUCCESS })
            }).catch(e => {
                dispatch({ type: DESTROY_CONVERSATIONS_FAILURE, payload: e })
            })
        }
    },
    setConnectionStatus: (status) => {
        return { type: SET_CONVERSATIONS_CONNECTION_STATUS, payload: status }
    },
    setUserOnline: (identity, isOnline) => {
        return { type: SET_CONVERSATION_USER_ONLINE, payload: { identity, isOnline } }
    },
    loadUsers: (params) => {
        return dispatch => {
            dispatch({ type: LOAD_CONVERSATION_USERS_REQUEST })
            return service.getUserProfiles(params).then(data => {
                dispatch({ type: LOAD_CONVERSATION_USERS_SUCCESS, payload: data })
                return data
            }).catch(e => {
                dispatch({ type: LOAD_CONVERSATION_USERS_FAILURE, payload: e })
            })
        }
    },
    setLastSelected: (sid) => {
        return { type: SET_LAST_SELECTED_CONVERSATION, payload: sid }
    },
    setLastMessage: (message) => {
        return { type: SET_LAST_CONVERSATION_MESSAGE, payload: message }
    },
    addUnsentMessage: (sid, message) => {
        return { type: ADD_UNSENT_CONVERSATION_MESSAGE, payload: { sid, message } }
    },
    removeUnsentMessage: (sid, index) => {
        return { type: REMOVE_UNSENT_CONVERSATION_MESSAGE, payload: { sid, index } }
    },
    updateLiveConversations: (conversationSids) => {
        return { type: UPDATE_LIVE_CONVERSATIONS, payload: conversationSids }
    }
}