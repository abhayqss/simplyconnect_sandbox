import {
    useMemo,
    useReducer
} from 'react'

import {
    map,
} from 'underscore'

import mirror from 'key-mirror'

import {
    Message,
    Conversation,
    ConversationService,
} from 'factories'

import ListDataSource from 'entities/ListDataSource'

const { Record } = require('immutable')

const service = ConversationService()

const ACTION_TYPES = mirror({
    SET_ERROR: null,
    CLEAR_ERROR: null,
    ADD_MESSAGE: null,
    UPDATE_MESSAGE: null,
    CLEAR_MESSAGES: null,
    FETCH_MESSAGES_REQUEST: null,
    FETCH_MESSAGES_SUCCESS: null,
    FETCH_MESSAGES_FAILURE: null,
    LEAVE_CONVERSATION_SUCCESS: null,
    LEAVE_CONVERSATION_FAILURE: null,
    FETCH_CONVERSATION_REQUEST: null,
    FETCH_CONVERSATION_SUCCESS: null,
    FETCH_CONVERSATION_FAILURE: null,
})

function Actions(dispatch) {
    const {
        SET_ERROR,
        CLEAR_ERROR,
        ADD_MESSAGE,
        UPDATE_MESSAGE,
        CLEAR_MESSAGES,
        FETCH_MESSAGES_REQUEST,
        FETCH_MESSAGES_SUCCESS,
        FETCH_MESSAGES_FAILURE,
        FETCH_CONVERSATION_REQUEST,
        FETCH_CONVERSATION_SUCCESS,
        FETCH_CONVERSATION_FAILURE,
    } = ACTION_TYPES

    return {
        setError: (e) => {
            dispatch({ type: SET_ERROR, payload: e })
        },
        clearError: () => {
            dispatch({ type: CLEAR_ERROR })
        },

        fetch: sid => {
            dispatch({ type: FETCH_CONVERSATION_REQUEST })

            return service.getBySid(sid)
                .then((cv) => {
                    dispatch({ type: FETCH_CONVERSATION_SUCCESS, payload: cv })

                    return cv
                })
                .catch((error) => {
                    dispatch({ type: FETCH_CONVERSATION_FAILURE, payload: error })
                })
        },
        messages: {
            clear: () => {
                dispatch({ type: CLEAR_MESSAGES })
            },
            add: message => {
                dispatch({ type: ADD_MESSAGE, payload: message })
            },
            update: message => {
                dispatch({ type: UPDATE_MESSAGE, payload: message })
            },
            fetch: (conversation, params, options) => {
                dispatch({ type: FETCH_MESSAGES_REQUEST })

                let isAborted = false

                return new Promise((resolve) => {
                    options.signal.onabort = () => {
                        isAborted = true
                        resolve(null)
                    }

                    service.getMessages(conversation, params)
                        .then(messages => {
                            if (!isAborted) {
                                resolve(messages)
                                dispatch({ type: FETCH_MESSAGES_SUCCESS, payload: messages })
                            } else {
                                resolve(null)
                            }
                        })
                        .catch(e => {
                            dispatch({ type: FETCH_MESSAGES_FAILURE, payload: e })
                        })
                })
            }
        },
    }
}

const initialState = Record({
    error: null,
    data: null,
    isFetching: false,
    messages: ListDataSource(),
})()

function reducer(state, action) {
    const {
        SET_ERROR,
        CLEAR_ERROR,
        ADD_MESSAGE,
        UPDATE_MESSAGE,
        CLEAR_MESSAGES,
        FETCH_MESSAGES_REQUEST,
        FETCH_MESSAGES_SUCCESS,
        FETCH_MESSAGES_FAILURE,
        FETCH_CONVERSATION_REQUEST,
        FETCH_CONVERSATION_SUCCESS,
        FETCH_CONVERSATION_FAILURE,
    } = ACTION_TYPES

    switch (action.type) {
        case SET_ERROR:
            return state.set(['error'], action.payload)

        case CLEAR_ERROR:
            return state.set(['error'], null)

        case CLEAR_MESSAGES:
            return state.setIn(['messages'], state.messages.clear())

        case ADD_MESSAGE:
            return state.setIn(['messages', 'data'],
                state.messages.data.push(Message(action.payload))
            )

        case UPDATE_MESSAGE: {
            let index = state.messages.data.findIndex(o => o.sid === action.payload.sid)

            return state.setIn(
                ['messages', 'data', index],
                Message(action.payload)
            )
        }

        case FETCH_MESSAGES_REQUEST:
            return state.setIn(['messages', 'isFetching'], true)

        case FETCH_MESSAGES_SUCCESS:
            return state.mergeIn(['messages'], {
                isFetching: false,
                data: state.messages.data.unshift(
                    ...map(action?.payload, o => Message(o))
                )
            })

        case FETCH_MESSAGES_FAILURE:
            return state.mergeIn(['messages'], {
                isFetching: false,
                error: action.payload
            })

        case FETCH_CONVERSATION_REQUEST: {
            return state.setIn(['isFetching'], true)
        }

        case FETCH_CONVERSATION_SUCCESS: {
            return state
                .setIn(['isFetching'], false)
                .setIn(['data'], Conversation(action.payload))
        }

        case FETCH_CONVERSATION_FAILURE:
            return state
                .setIn(['isFetching'], false)
                .setIn(['error'], action.payload)

        default:
            return state
    }
}

export default function useConversationState() {
    const [state, dispatch] = useReducer(reducer, initialState)

    const actions = useMemo(() => Actions(dispatch), [])

    return { state, actions }
}