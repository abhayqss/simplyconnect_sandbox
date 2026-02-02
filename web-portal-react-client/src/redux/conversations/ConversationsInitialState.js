const { Map, List, Record } = require('immutable')

export default Record({
    error: null,
    isReady: false,
    isInitializing: false,
    isDestroying: false,
    connectionStatus: 'disconnected',

    lastMessages: Map(),
    unsentMessages: Map(),
    users: Record({
        data: Map(),
        error: null,
        isFetching: false,
    })(),
    currentUser: null,
    sidOfLastSelected: null,
    onlineUserIdentities: List(),
    liveConversationSids: List(),
})
