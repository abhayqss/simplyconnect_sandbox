import {
    Message,
    TwilioMessageProvider
} from 'entities/conversation'

import { conversations } from 'config'

const getMessage = service => {
    let provider = null

    if (conversations.provider === 'twilio') {
        provider = new TwilioMessageProvider(service)
    }

    return new Message(provider)
}

export default function (service) {
    return getMessage(service)
}