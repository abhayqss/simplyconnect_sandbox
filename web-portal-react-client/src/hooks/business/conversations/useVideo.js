import { useCallback } from 'react'

import { useBoundActions } from 'hooks/common/redux'
import { useVideoState } from './index'

import videoChatActions from 'redux/video-chat/videoChatActions'

function useVideo() {
    const { room, currentCall } = useVideoState()

    const actions = useBoundActions(videoChatActions)

    const connect = useCallback((data) => {
        const { token, name, isVideoCall } = data || currentCall

        actions.connect({ token, name, isVideoCall })
    }, [actions, currentCall])

    const acceptCall = useCallback((isVideoCall) => {
        const { token, name } = currentCall

        return actions.acceptCall({ token, name, isVideoCall })
    }, [actions, currentCall])

    const disconnect = useCallback(() => {
        actions.disconnect(room)
    }, [room, actions])

    return {
        ...actions,
        connect,
        acceptCall,
        disconnect,
    }
}

export default useVideo
