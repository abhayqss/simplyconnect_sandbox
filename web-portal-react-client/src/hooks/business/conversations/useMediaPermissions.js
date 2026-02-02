import {
    useReducer,
    useCallback,
} from 'react'

import store from 'lib/stores/BaseStore'

const initialState = { audio: false }

function init() {
    return {
        audio: store.get('audio-permissions')
    }
}

function reducer(state, action) {
    switch (action.type) {
        case 'audio-permissions':
            store.save('audio-permissions', action.payload)

            return {
                ...state,
                audio: action.payload
            }
    
        default:
            return state
    }
}

export default function () {
    let [state, dispatch] = useReducer(reducer, initialState, init)

    const setAudioPermissions = useCallback(value => {
        dispatch({ type: 'audio-permissions', payload: value })
    }, [])

    const requestAudioPermissions = useCallback(async () => {
        try {
            let stream = await navigator.mediaDevices.getUserMedia({ audio: true })

            stream.getTracks().forEach(track => track.stop())

            setAudioPermissions(true)
        } catch (error) {
            setAudioPermissions(false)
        }
    }, [setAudioPermissions])

    return {
        requestAudioPermissions,
        hasAudioPermissions: state.audio,
    }
}
