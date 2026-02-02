import {
    Conversation,
    TwilioConversationProvider
} from 'entities/conversation'

import { conversations } from 'config'

const getConversation = service => {
    if (service instanceof Conversation) {
        return service
    }

    let provider = null

    if (conversations.provider === 'twilio') {
        provider = new TwilioConversationProvider(service)
    }

    return new Conversation(provider)
}

export default function (service) {
    return getConversation(service)
}