import {
    VideoRoom,
    TwilioVideoRoom
} from 'entities/conversation'

import { conversations } from 'config'

const SERVICE_PROVIDER = {
    'twilio': TwilioVideoRoom
}

export default function (service) {
    if (service instanceof VideoRoom) {
        return service
    }

    const Provider = SERVICE_PROVIDER[conversations.provider]

    return new VideoRoom(new Provider(service))
}