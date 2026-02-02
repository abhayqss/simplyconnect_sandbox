const { Record, List, Set } = require('immutable')

const Call = Record({
    name: '',
    token: '',
    friendlyName: '',
    isVideoCall: false,
    isDeclined: false,
    conversationSid: '',
    caller: null,
    callees: List(),
    companionAvatarId: null,

    onCallIdentities: Set(),
    pendingIdentities: Set(),
    declinedIdentities: Set(),
    timeoutIdentities: Set(),
})

export default function (data) {
    return Call({
        ...data,
        name: data.roomSid,
        token: data.roomAccessToken,
        callees: List(data.callees),
        onCallIdentities: List(data.onCall),
        pendingIdentities: data.pendingIdentities ? Set(data.pendingIdentities) : Set(),
        friendlyName: data.friendlyName || data.conversationFriendlyName
    })
}