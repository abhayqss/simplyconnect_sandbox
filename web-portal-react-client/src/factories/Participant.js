import {
    Participant,
    TwilioParticipantProvider
} from 'entities/conversation'

import memoize from 'memoize-one'

import { conversations } from 'config'

const getParticipant = memoize(service => {
    let provider = null

    if (conversations.provider === 'twilio') {
        provider = new TwilioParticipantProvider(service)
    }

    return new Participant(provider)
})

export default function (service) {
    return getParticipant(service)
}