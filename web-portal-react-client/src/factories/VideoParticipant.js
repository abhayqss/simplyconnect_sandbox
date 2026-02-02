import {
    VideoParticipant,
    TwilioVideoParticipant
} from 'entities/conversation'

import { conversations } from 'config'

const SERVICE_PROVIDER = {
    'twilio': TwilioVideoParticipant
}

export default function (service) {
    const Provider = SERVICE_PROVIDER[conversations.provider]

    return new VideoParticipant(new Provider(service))
}