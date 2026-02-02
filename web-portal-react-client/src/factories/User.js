import {
    User,
    TwilioUserProvider
} from 'entities/conversation'

import { conversations } from 'config'

const SERVICE_PROVIDER = {
    'twilio': TwilioUserProvider
}

export default function (service) {
    if (service instanceof User) {
        return service
    }

    const Provider = SERVICE_PROVIDER[conversations.provider]

    return new User(new Provider(service))
}