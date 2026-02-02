import { SERVICE_MESSAGE_TYPES } from 'lib/Constants'

const ImmediateStopSearchTypes = [
    SERVICE_MESSAGE_TYPES.CALL_END,
    SERVICE_MESSAGE_TYPES.CALL_MEMBERS_TIMEOUT,
]

const StopSearchTypes = [
    SERVICE_MESSAGE_TYPES.CALL_MEMBER_DECLINED,
    SERVICE_MESSAGE_TYPES.CALL_MEMBER_JOINED,
    SERVICE_MESSAGE_TYPES.CALL_MEMBER_LEFT,
]

const { INITIATE_CALL } = SERVICE_MESSAGE_TYPES

function IncomingCallSearchStrategy(context) {
    const { userIdentity, messages } = context

    for (const message of messages) {
        const data = JSON.parse(message.text || null)
        
        if (data.type === INITIATE_CALL && data?.caller?.identity !== userIdentity) {
            return data
        } else if (ImmediateStopSearchTypes.includes(data.type)) {
            break
        } else if (
            StopSearchTypes.includes(data.type)
            && data.identity === userIdentity
        ) {
            break
        } else {
            continue
        }
    }
}

export default IncomingCallSearchStrategy
