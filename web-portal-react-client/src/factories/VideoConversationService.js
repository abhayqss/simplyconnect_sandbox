import {
    VideoConversationService,
    TwilioVideoConversationService
} from 'services'

import memoize from 'memoize-one'

import { conversations } from 'config'

const SERVICE_PROVIDER = {
    'twilio': TwilioVideoConversationService
}

const getService = memoize(() => {
    const Provider = SERVICE_PROVIDER[conversations.provider]

    return new VideoConversationService(new Provider())
})

export default function () {
    return getService()
}