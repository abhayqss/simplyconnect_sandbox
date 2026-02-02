import { shape, string, number, bool, arrayOf } from 'prop-types'

export const TEventTypes = shape({
    id: number,
    title: string
})

export const TResponsibility = shape({
    assignable: bool,
    canBeChangedIfInitiallyAssigned: bool,
    name: string,
    title: string
})

export const TChannel = shape({
    name: string,
    title: string
})

export const TNotificationPreference = shape({
    channels: arrayOf(string),
    eventTypeId: number,
    responsibilityName: string
})
