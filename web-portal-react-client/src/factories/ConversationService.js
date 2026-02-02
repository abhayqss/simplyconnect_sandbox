import {
    ConversationService,
    twilioConversationService
} from 'services'

import memoize from 'memoize-one'

import { conversations } from 'config'

const getService = memoize(() => {
    let service = null

    if (conversations.provider === 'twilio') {
        service = twilioConversationService
    }

    return new ConversationService(service)
})

export default function () {
    return getService()
}