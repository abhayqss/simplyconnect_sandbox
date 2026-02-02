import { VideoConversationService, VideoRoom } from 'factories'
import { Call } from 'entities/conversation'

import { ifElse, cancelable } from 'lib/utils/Utils'

import actionTypes from './videoChatActionTypes'

const service = VideoConversationService()

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

    DISCONNECT,
    CANCEL_CALL,

    DECLINE_CALL_REQUEST,
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

function stopRoom(room) {
    if (room) {
        service.stopAllTracks(room)
        service.disconnect(room)
    }
}

function connect({ name, token, isVideoCall }) {
    return dispatch => {
        dispatch({ type: CONNECT_REQUEST })

        return service.connect({ token, name, isVideoCall })
            .then(room => {
                dispatch({ type: CONNECT_SUCCESS, payload: room })

                return room
            })
            .catch(e => {
                dispatch({ type: CONNECT_FAILURE, payload: e })
            })
    }
}

function disconnect(room) {
    return dispatch => {
        stopRoom(room)

        dispatch({ type: DISCONNECT })
    }
}

const [initiateCall, cancelInitialCall] = cancelable(async ({
    dispatch,
    isVideoCall,
    employeeIds,
    incidentReport,
    conversationSid,
}) => {
    const data = await service.initiateCall({ conversationSid, employeeIds, incidentReportId: incidentReport?.id })

    const callData = { ...data, isVideoCall }

    dispatch({ type: INITIATE_CALL_SUCCESS, payload: callData })

    if (incidentReport) {
        dispatch({ type: ADD_INCIDENT_REPORT, payload: incidentReport })
    }

    if (data.areCalleesBusy) {
        data.callees.forEach(callee => {
            dispatch({ type: ADD_DECLINED_PARTICIPANT, payload: callee.identity })
        })
    } else {
        return await connect(Call(callData))(dispatch)
    }
})

export default {
    connect,
    disconnect,
    clearError: () => {
        return dispatch => {
            dispatch({ type: CLEAR_ERROR })
        }
    },
    initiateCall: params => {
        return async dispatch => {
            dispatch({ type: INITIATE_CALL_REQUEST })

            const onError = ifElse(
                error => error?.message === 'Cancelled',
                error => {
                    stopRoom(error.body)
                    setTimeout(() => dispatch({ type: CANCEL_CALL }))
                },
                error => dispatch({ type: INITIATE_CALL_FAILURE, payload: error })
            )

            return initiateCall({
                dispatch,
                ...params,
            }).catch(onError)
        }
    },
    dismissCallInitiation: () => {
        return () => cancelInitialCall()
    },
    addParticipants: (params) => {
        return async dispatch => {
            dispatch({ type: ADD_PARTICIPANTS_REQUEST, payload: { count: params.employeeIds.length } })
            
            try {
                const { data } = await service.addParticipants(params)

                const busyEmployeeIds = data
                    .filter(o => o.isBusy)
                    .map(o => o.id)

                if (busyEmployeeIds.length !== params.employeeIds.length) {
                    dispatch({ type: ADD_PARTICIPANTS_SUCCESS })
                }

                return { busyEmployeeIds }
            } catch (error) {
                dispatch({ type: ADD_PARTICIPANTS_FAILURE, payload: error })
            }
        }
    },
    declineCall: ({ roomSid }) => {
        return dispatch => {
            dispatch({ type: DECLINE_CALL_REQUEST })

            return service.declineCall(roomSid)
                .then(() => {
                    dispatch({ type: DECLINE_CALL_SUCCESS })
                })
                .catch(e => {
                    dispatch({ type: DECLINE_CALL_FAILURE })
                })
        }
    },
    cancelCall: () => {
        return dispatch => {
            dispatch({ type: CANCEL_CALL })
        }
    },
    callWasDeclined: () => {
        return dispatch => {
            dispatch({ type: CALL_WAS_DECLINED })
        }
    },
    receiveCall: (call) => {
        return dispatch => {
            dispatch({ type: RECEIVE_CALL, payload: call })
        }
    },
    joinCall: (call) => {
        return dispatch => {
            dispatch({ type: JOIN_CALL, payload: call })

            connect(Call(call))(dispatch)
        }
    },
    acceptCall: ({ name, token, isVideoCall }) => {
        return dispatch => {
            dispatch({ type: ACCEPT_CALL, payload: isVideoCall })

            return connect({ name, token, isVideoCall })(dispatch)
        }
    },
    setOnCall: value => {
        return dispatch => {
            dispatch({ type: SET_ON_CALL, payload: value })
        }
    },
    updateCurrentCall: value => {
        return dispatch => {
            dispatch({ type: UPDATE_CALL, payload: value })
        }
    },
    setIncomingCall: value => {
        return dispatch => {
            dispatch({ type: SET_INCOMING_CALL, payload: value })
        }
    },
    setOutgoingCall: value => {
        return dispatch => {
            dispatch({ type: SET_OUTGOING_CALL, payload: value })
        }
    },
    closeConnection: () => {
        return dispatch => {
            dispatch({ type: CLOSE_CONNECTION })
        }
    },
    addPendingParticipants: participants => {
        return dispatch => {
            dispatch({ type: ADD_PENDING_PARTICIPANTS, payload: participants })
        }
    },
    addOnCallParticipants: (participants) => {
        return dispatch => {
            dispatch({ type: ADD_ON_CALL_PARTICIPANTS, payload: participants })
        }
    },
    removeOnCallParticipants: (identities) => {
        return dispatch => {
            dispatch({ type: REMOVE_ON_CALL_PARTICIPANTS, payload: identities })
        }
    },
    addDeclinedParticipant: (identity) => {
        return dispatch => {
            dispatch({ type: ADD_DECLINED_PARTICIPANT, payload: identity })
        }
    },
    removeDeclinedParticipants: (participants) => {
        const identities = participants.map(o => o.identity)

        return dispatch => {
            dispatch({ type: REMOVE_DECLINED_PARTICIPANTS, payload: identities })
        }
    },
    addTimeoutParticipants: (identities) => {
        return dispatch => {
            dispatch({ type: ADD_TIMEOUT_PARTICIPANTS, payload: identities })
        }
    },
    removeTimeoutParticipants: (participants) => {
        const identities = participants.map(o => o.identity)

        return dispatch => {
            dispatch({ type: REMOVE_TIMEOUT_PARTICIPANTS, payload: identities })
        }
    },
}
