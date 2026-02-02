import {
    useMemo,
    useReducer
} from 'react'

import memoize from 'memoize-one'

import mirror from 'key-mirror'

import {
    Participant,
    Conversation,
    ConversationService
} from 'factories'

const { Record, List } = require('immutable')

const service = ConversationService()

const ACTION_TYPES = mirror({
    FETCH_CONVERSATIONS_REQUEST: null,
    FETCH_CONVERSATIONS_SUCCESS: null,
    FETCH_CONVERSATIONS_FAILURE: null,

    ADD_CONVERSATION: null,
    UPDATE_CONVERSATION: null,
    REMOVE_CONVERSATION: null,
    SELECT_CONVERSATION: null,

    SET_ERROR: null,
    CLEAR_ERROR: null,
    SET_IS_FETCHING: null,
})

function Actions(dispatch) {
    const {
        SET_ERROR,
        CLEAR_ERROR,
        SET_IS_FETCHING,

        FETCH_CONVERSATIONS_REQUEST,
        FETCH_CONVERSATIONS_SUCCESS,
        FETCH_CONVERSATIONS_FAILURE,

        ADD_CONVERSATION,
        UPDATE_CONVERSATION,
        REMOVE_CONVERSATION,
        SELECT_CONVERSATION,
    } = ACTION_TYPES

    return {
        setError: e => {
            dispatch({ type: SET_ERROR, payload: e })
        },
        clearError: () => {
            dispatch({ type: CLEAR_ERROR })
        },
        fetch: () => {
            dispatch({ type: FETCH_CONVERSATIONS_REQUEST })

            return service.getAll()
                .then(conversations => {
                    dispatch({ type: FETCH_CONVERSATIONS_SUCCESS, payload: conversations })
                    return conversations
                })
                .catch(e => {
                    dispatch({ type: FETCH_CONVERSATIONS_FAILURE, payload: e })
                })
        },
        add: conversation => {
            dispatch({ type: ADD_CONVERSATION, payload: conversation })
        },
        update: conversation => {
            dispatch({ type: UPDATE_CONVERSATION, payload: conversation })
        },
        remove: conversation => {
            dispatch({ type: REMOVE_CONVERSATION, payload: conversation })
        },
        create: params => {
            dispatch({ type: SET_IS_FETCHING, payload: true })

            return service.create(params)
                .then(({ data }) => {
                    dispatch({ type: SET_IS_FETCHING, payload: false })

                    return data
                })
                .catch(e => {
                    dispatch({ type: SET_IS_FETCHING, payload: false })
                })
        },
        select: conversation => {
            dispatch({ type: SELECT_CONVERSATION, payload: conversation })
        }
    }
}

export const selectors = {
    onlyPersonal: memoize(
        data => data.filter(o => o.participantIdentities.length === 2)
    ),
    sortByUpdated: memoize(
        data => data.sortBy(
            o => (o.lastMessage?.dateCreated ?? o.dateUpdated).getTime()
        ).reverse()
    ),
    filterUserDataOut: memoize(
        (user, data) => data.filter(o => o.sid !== user.serviceConversationSid)
    ),
    sortByLive: memoize(
        (liveConversationIds, data) => data.sortBy(
            o => o.sid,
            (a, b) => {
                let result = 0
                let aIsLive = liveConversationIds.includes(a)
                let bIsLive = liveConversationIds.includes(b)

                if (aIsLive) {
                    result = -1
                }

                if (bIsLive) {
                    result = 1
                }

                if (aIsLive && bIsLive) {
                    result = 0
                }

                return result
            }
        ),
    )
}

const initialState = Record({
    error: null,
    isFetching: false,
    wasFetched: false,
    selected: null,
    data: List(),
})()

function reducer(state, action) {
    const {
        FETCH_CONVERSATIONS_REQUEST,
        FETCH_CONVERSATIONS_SUCCESS,
        FETCH_CONVERSATIONS_FAILURE,
        ADD_CONVERSATION,
        UPDATE_CONVERSATION,
        REMOVE_CONVERSATION,
        SELECT_CONVERSATION,
        SET_ERROR,
        CLEAR_ERROR,
        SET_IS_FETCHING,
    } = ACTION_TYPES

    switch (action.type) {
        case SET_ERROR: {
            return state.setIn(['error'], action.payload)
        }
        case CLEAR_ERROR: {
            return state.setIn(['error'], null)
        }
        case FETCH_CONVERSATIONS_REQUEST:
            return state.setIn(['isFetching'], true)

        case SET_IS_FETCHING:
            return state.setIn(['isFetching'], action.payload)

        case FETCH_CONVERSATIONS_SUCCESS:
            return state.merge({
                isFetching: false,
                wasFetched: true,
                data: List(action.payload).map(Conversation)
            })
        case FETCH_CONVERSATIONS_FAILURE:
            return state.merge({
                isFetching: false,
                error: action.payload
            })
        case ADD_CONVERSATION:
            return state.setIn(['data'],
                state.data.unshift(Conversation(action.payload))
            )
        case UPDATE_CONVERSATION: {
            let index = state.data.findIndex(o => o.sid === action.payload.sid)
            let cv = Conversation(action.payload)

            if (cv.sid === state.selected?.sid) {
                state = state.setIn(['selected'], cv)
            }

            return state.setIn(['data', index], cv)
        }

        case REMOVE_CONVERSATION: {
            if (action.payload.sid === state.selected?.sid) {
                state = state.setIn(['selected'], null)
            }

            return state.updateIn(['data'], data => (
                data.filter(o => o.sid !== action.payload.sid)
            ))
        }

        case SELECT_CONVERSATION: {
            return state.setIn(['selected'], action.payload)
        }

        default:
            return state
    }
}

export default function useConversationsState() {
    const [state, dispatch] = useReducer(reducer, initialState)

    const actions = useMemo(() => Actions(dispatch), [])

    return {
        state,
        actions,
        selectors,
    }
}
