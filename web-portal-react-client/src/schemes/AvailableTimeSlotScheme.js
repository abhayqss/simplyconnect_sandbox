import {
    each
} from 'underscore'

import service from 'services/AppointmentService'

import {
    isNotEmpty
} from 'lib/utils/Utils'

import {
    format,
    formats,
} from 'lib/utils/DateUtils'

import { Shape } from './types'

const DATE_FORMAT = formats.americanMediumDate
const TIME_FORMAT = formats.time2

export default Shape().test({
    name: 'availableTimeSlot',
    test: async function (params) {
        try {
            const response = await service.checkTimeSLotAvailability(params)

            const {
                client,
                creator,
                serviceProviders,
                suggestedTimeSlots,
            } = response

            const messages = []

            if (isNotEmpty(serviceProviders)) {
                messages.push(`${serviceProviders.join(', ')} ${serviceProviders.length > 1 ? 'are' : 'is'} not available`)
            }

            if (client) {
                messages.push(`${client} is not available`)
            }

            if (creator) {
                messages.push(`You are not available`)
            }

            if (isNotEmpty(suggestedTimeSlots)) {
                messages.push('Suggested time slots:')

                each(suggestedTimeSlots, o => {
                    messages.push(`${format(o.startDate, DATE_FORMAT)}, ${format(o.startDate, TIME_FORMAT)} - ${format(o.endDate, TIME_FORMAT)}`)
                })
            }

            return isNotEmpty(messages) ? this.createError({ message: messages.join('\n') }) : true
        } catch (error) {
            return false
        }
    },
    exclusive: true
})
