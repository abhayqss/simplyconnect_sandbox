import { useSelector } from 'react-redux'

const selectError = state => state.videoChat.error
const selectRoom = state => state.videoChat.room
const selectCurrentCall = state => state.videoChat.currentCall
const selectIsOnCall = state => state.videoChat.isOnCall
const selectIsConnecting = state => state.videoChat.isConnecting
const selectIsIncomingCall = state => state.videoChat.isIncomingCall
const selectIsOutgoingCall = state => state.videoChat.isOutgoingCall
const selectIncidentReport = state => state.videoChat.incidentReport

function useVideoState() {
    const room = useSelector(selectRoom)
    const error = useSelector(selectError)
    const currentCall = useSelector(selectCurrentCall)
    const isOnCall = useSelector(selectIsOnCall)
    const isConnecting = useSelector(selectIsConnecting)
    const isIncomingCall = useSelector(selectIsIncomingCall)
    const isOutgoingCall = useSelector(selectIsOutgoingCall)
    const incidentReport = useSelector(selectIncidentReport)

    return {
        room,
        error,
        isOnCall,
        currentCall,
        isConnecting,
        isIncomingCall,
        isOutgoingCall,
        incidentReport,
    }
}

export default useVideoState
