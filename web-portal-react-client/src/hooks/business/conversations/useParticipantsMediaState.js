import { useReducer, useCallback } from 'react'

import { ifElse } from 'lib/utils/Utils'

const { Record, Map, List } = require('immutable')

const ENABLE_AUDIO = 'ENABLE_AUDIO'
const DISABLE_AUDIO = 'TOGGLE_AUDIO'
const ENABLE_VIDEO = 'ENABLE_VIDEO'
const DISABLE_VIDEO = 'TOGGLE_VIDEO'
const ADD_PARTICIPANT = 'ADD_IDENTITY'
const REMOVE_PARTICIPANT = 'REMOVE_IDENTITY'
const START_RECONNECTING = 'START_RECONNECTING'
const STOP_RECONNECTING = 'STOP_RECONNECTING'

const MediaState = Record({
    audio: false,
    video: false,
    isReconnecting: false,
})

const ParticipantMediaState = participant => {
    let state = {}

    participant.tracks.forEach(track => {
        state[track.kind] = track.isTrackEnabled
    })

    return MediaState(state)
}

const setAudio = value => (state, identity) => {
    return state.setIn([identity, 'audio'], value)
}

const setVideo = value => (state, identity) => {
    return state.setIn([identity, 'video'], value)
}

const addParticipant = ifElse(
    (state, payload) => state.has(payload.identity),
    state => state,
    (state, payload) => state.set(payload.identity, ParticipantMediaState(payload))
)

const removeParticipant = (state, payload) => {
    return state.delete(payload.identity)
}

const setReconnecting = value => (state, identity) => {
    return state.setIn([identity, 'isReconnecting'], value)
}

const ActionReducer = Map([
    [ENABLE_AUDIO, setAudio(true)],
    [DISABLE_AUDIO, setAudio(false)],
    [ENABLE_VIDEO, setVideo(true)],
    [DISABLE_VIDEO, setVideo(false)],
    [ADD_PARTICIPANT, addParticipant],
    [REMOVE_PARTICIPANT, removeParticipant],
    [START_RECONNECTING, setReconnecting(true)],
    [STOP_RECONNECTING, setReconnecting(false)],
])

const produceState = (state, action) => {
    const reducer = ActionReducer.get(action.type)

    return reducer(state, action.payload)
}

const reducer = ifElse(
    (_, action) => ActionReducer.has(action.type),
    produceState,
    (state) => state
)

const initialState = Map()

function init(participants) {
    return () => {
        return List(participants).reduce((map, participant) => {
            return map.set(participant.identity, ParticipantMediaState(participant))
        }, Map())
    }
}

function useParticipantsMediaState(participants) {
    const [state, dispatch] = useReducer(
        reducer,
        initialState,
        init(participants)
    )

    const enableAudio = useCallback((identity) => {
        dispatch({ type: ENABLE_AUDIO, payload: identity })
    }, [])

    const disableAudio = useCallback((identity) => {
        dispatch({ type: DISABLE_AUDIO, payload: identity })
    }, [])

    const enableVideo = useCallback((identity) => {
        dispatch({ type: ENABLE_VIDEO, payload: identity })
    }, [])

    const disableVideo = useCallback((identity) => {
        dispatch({ type: DISABLE_VIDEO, payload: identity })
    }, [])

    const addParticipant = useCallback((participant) => {
        dispatch({ type: ADD_PARTICIPANT, payload: participant })
    }, [])

    const removeParticipant = useCallback((participant) => {
        dispatch({ type: REMOVE_PARTICIPANT, payload: participant })
    }, [])

    const startReconnecting = useCallback((identity) => {
        dispatch({ type: START_RECONNECTING, payload: identity })
    }, [])

    const stopReconnecting = useCallback((identity) => {
        dispatch({ type: STOP_RECONNECTING, payload: identity })
    }, [])

    return {
        state,
        enableAudio,
        disableAudio,
        enableVideo,
        disableVideo,
        addParticipant,
        removeParticipant,
        startReconnecting,
        stopReconnecting,
    }
}

export default useParticipantsMediaState
