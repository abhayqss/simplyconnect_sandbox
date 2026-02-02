const { Record } = require('immutable')

export default Record({
    error: null,
    room: null,
    isConnecting: false,
    currentCall: null,

    isOnCall: false,
    isIncomingCall: false,
    isOutgoingCall: false,
    loadingParticipantsCount: 0,

    incidentReport: null,
})
