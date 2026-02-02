import { Message } from 'factories'

import { SERVICE_MESSAGE_TYPES } from 'lib/Constants'

const { INITIATE_CALL } = SERVICE_MESSAGE_TYPES

function OnlineCallGetterStrategy(context) {
    const {
        messages,
        conversationSid
    } = context

    for (const message of messages) {
        const data = JSON.parse(message.text || null)

        if (data.conversationSid === conversationSid && data.type === INITIATE_CALL) {
            return data
        } else {
            continue
        }
    }
}

export default OnlineCallGetterStrategy