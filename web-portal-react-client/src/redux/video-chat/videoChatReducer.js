import { VideoRoom } from 'factories'
import { Call } from 'entities/conversation'

import { ACTION_TYPES } from 'lib/Constants'

import actionTypes from './videoChatActionTypes'
import InitialState from './VideoChatInitialState'

const { List, Set } = require('immutable')

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,
} = ACTION_TYPES

const {
    CLEAR_ERROR,
    INITIATE_CALL_REQUEST,
    INITIATE_CALL_SUCCESS,
    INITIATE_CALL_FAILURE,

    JOIN_CALL,
    ACCEPT_CALL,
    RECEIVE_CALL,
    SET_ON_CALL,
    UPDATE_CALL,
    CLOSE_CONNECTION,
    SET_INCOMING_CALL,
    SET_OUTGOING_CALL,

    CONNECT_REQUEST,
    CONNECT_SUCCESS,
    CONNECT_FAILURE,

    CANCEL_CALL,

    DECLINE_CALL_SUCCESS,
    DECLINE_CALL_FAILURE,

    CALL_WAS_DECLINED,
    ADD_PENDING_PARTICIPANTS,
    ADD_ON_CALL_PARTICIPANTS,
    REMOVE_ON_CALL_PARTICIPANTS,
    ADD_DECLINED_PARTICIPANT,
    REMOVE_DECLINED_PARTICIPANTS,
    ADD_TIMEOUT_PARTICIPANTS,
    REMOVE_TIMEOUT_PARTICIPANTS,

    ADD_PARTICIPANTS_REQUEST,
    ADD_PARTICIPANTS_SUCCESS,
    ADD_PARTICIPANTS_FAILURE,

    ADD_INCIDENT_REPORT,
} = actionTypes

const initialState = new InitialState()

export default function conversationsReducer(state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case CLOSE_CONNECTION:
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
            state = state.clear()
            break

        case CLEAR_ERROR:
            state = state.set('error', null)
            break

        case DECLINE_CALL_SUCCESS:
            state = state.set('isIncomingCall', false)
            break

        case CANCEL_CALL:
            state = state
                .set('isOutgoingCall', false)
                .set('room', null)
            break

        case JOIN_CALL: {
            state = state
                .set('currentCall', Call(action.payload))
                .set('isOnCall', true)
            break
        }

        case INITIATE_CALL_SUCCESS: {
            state = state.set('currentCall', Call(action.payload))
                .set('isOutgoingCall', true)
            break
        }

        case RECEIVE_CALL:
            state = state
                .set('currentCall', Call(action.payload))
                .set('isIncomingCall', true)
            break

        case INITIATE_CALL_REQUEST:
        case CONNECT_REQUEST:
            state = state.set('isConnecting', true)
            break

        case CONNECT_SUCCESS:
            state = state
                .set('room', VideoRoom(action.payload))
                .set('isConnecting', false)
            break

        case INITIATE_CALL_FAILURE:
        case CONNECT_FAILURE:
            state = state
                .set('error', action.payload)
                .set('isConnecting', false)
            break

        case DECLINE_CALL_FAILURE:
            state = state.set('error', action.payload)
            break

        case CALL_WAS_DECLINED:
            state = state.currentCall
                ? state
                    .setIn(['currentCall', 'isDeclined'], true)
                    .set('isConnecting', false)
                : state.set('isConnecting', false)
            break

        case ACCEPT_CALL:
            state = state
                .set('isOnCall', true)
                .set('isIncomingCall', false)
                .setIn(['currentCall', 'isVideoCall'], action.payload)
            break

        case SET_ON_CALL:
            state = state.set('isOnCall', action.payload)
            break

        case UPDATE_CALL:
            state = state.mergeDeepIn(['currentCall'], action.payload)
            break

        case SET_INCOMING_CALL:
            state = state.set('isIncomingCall', action.payload)
            break

        case SET_OUTGOING_CALL:
            state = state.set('isOutgoingCall', action.payload)
            break

        case ADD_ON_CALL_PARTICIPANTS: {
            const identities = List(action.payload.map(o => o.identity))
            const filterParticipants = list => list.filterNot(
                identity => identities.includes(identity)
            )

            state = state
                .setIn(['currentCall', 'onCallIdentities'], identities)
                .updateIn(['currentCall', 'pendingIdentities'], filterParticipants)
                .updateIn(['currentCall', 'timeoutIdentities'], filterParticipants)
                .updateIn(['currentCall', 'declinedIdentities'], filterParticipants)
            break
        }

        case REMOVE_ON_CALL_PARTICIPANTS:
            state = state.updateIn(
                ['currentCall', 'onCallIdentities'],
                list => list.filter(o => !action.payload.includes(o.identity))
            )
            break

        case ADD_PENDING_PARTICIPANTS: {
            const payloadIdentities = action.payload.map(o => o.identity)

            state = state
                .updateIn(
                    ['currentCall', 'pendingIdentities'],
                    list => {
                        return payloadIdentities.reduce((list, value) => {
                            return list.add(value)
                        }, list)
                    }
                )
                .updateIn(
                    ['currentCall', 'callees'],
                    list => {
                        return action.payload.reduce((list, o) => {
                            return list.find(callee => callee.identity === o.identity) ? list : list.push(o)
                        }, list)
                    }
                )
            break
        }

        case ADD_DECLINED_PARTICIPANT:
            state = state.updateIn(
                ['currentCall', 'declinedIdentities'],
                list => list.add(action.payload)
            )
            break

        case REMOVE_DECLINED_PARTICIPANTS:
            state = state.updateIn(
                ['currentCall', 'declinedIdentities'],
                list => list.filter(o => !action.payload.includes(o))
            )
            break

        case ADD_TIMEOUT_PARTICIPANTS:
            state = state.setIn(
                ['currentCall', 'timeoutIdentities'],
                Set(action.payload)
            )
                .updateIn(
                    ['currentCall', 'pendingIdentities'],
                    list => list.filterNot(
                        identity => action.payload.includes(identity)
                    )
                )
            break

        case REMOVE_TIMEOUT_PARTICIPANTS:
            state = state.updateIn(
                ['currentCall', 'timeoutIdentities'],
                list => list.filter(o => !action.payload.includes(o))
            )
            break

        case ADD_PARTICIPANTS_REQUEST:
            state = state.setIn(
                ['currentCall', 'loadingParticipantsCount'],
                action.payload
            )
            break

        case ADD_PARTICIPANTS_SUCCESS:
            state = state.setIn(['currentCall', 'loadingParticipantsCount'], 0)
            break

        case ADD_PARTICIPANTS_FAILURE:
            state = state
                .setIn(['currentCall', 'loadingParticipantsCount'], 0)
                .set('error', action.payload)
            break

        case ADD_INCIDENT_REPORT:
            state = state.set('incidentReport', action.payload)
    }

    return state
}
