import React from 'react'

import { filter } from 'underscore'

import { interpolate } from 'lib/utils/Utils'
import { CLIENT_SECTIONS } from 'lib/Constants'

import { ReactComponent as Rides } from "images/rides.svg"
import { ReactComponent as Events } from "images/client-events.svg"
import { ReactComponent as Details } from "images/client-details.svg"
import { ReactComponent as CareTeam } from "images/care-team.svg"
import { ReactComponent as Documents } from "images/document-list.svg"
import { ReactComponent as CallHistory } from "images/history.svg"

const {
    DASHBOARD,
    CARE_TEAM_MEMBER,
    EVENTS_AND_NOTES,
    CALL_HISTORY,
    RIDES
} = CLIENT_SECTIONS

const NO_PERMISSIONS_ERROR_TEXT = `You don't have permissions to see the $0`

export function getSideBarItems(params) {
    const {
        prospectId,

        documentCount,
        careTeamCount,
        eventNoteComposedCount,

        excluded = [],
        permissions: {
            canRequestRide,
            canViewCallHistory,
            canViewRideHistory,
            canViewCareTeamMembers
        }
    } = params

    const canViewRides = canRequestRide || canViewRideHistory

    const path = `/prospects/${prospectId}`

    return filter([
        {
            title: 'Dashboard',
            href: `${path}/dashboard`,
            isExact: true,
            name: DASHBOARD,
            hintText: 'Dashboard',
            renderIcon: (className) => <Details className={className} />
        },
        {
            title: 'Care Team',
            name: CARE_TEAM_MEMBER,
            extraText: careTeamCount,
            href: `${path}/care-team`,
            isDisabled: !canViewCareTeamMembers,
            hintText: !canViewCareTeamMembers ? interpolate(
                NO_PERMISSIONS_ERROR_TEXT, 'client care team'
            ) : 'Care Team Listing',
            renderIcon: (className) => <CareTeam className={className} />
        },
        {
            title: 'Events & Notes',
            name: EVENTS_AND_NOTES,
            extraText: eventNoteComposedCount,
            href: `${path}/events`,
            hintText: 'Event and Note Listing',
            renderIcon: (className) => <Events className={className} />
        },
        {
            title: 'Documents',
            extraText: documentCount,
            href: `${path}/documents`,
            hintText: 'Document Listing',
            renderIcon: (className) => <Documents className={className} />
        },
        {
            title: 'Rides',
            name: RIDES,
            href: `${path}/rides`,
            isDisabled: !canViewRides,
            hintText: !canViewRides ? interpolate(
                NO_PERMISSIONS_ERROR_TEXT, 'client rides'
            ) : 'Rides Listing',
            renderIcon: (className) => <Rides className={className} />
        },
        {
            title: 'Call History',
            name: CALL_HISTORY,
            href: `${path}/call-history`,
            isDisabled: !canViewCallHistory,
            hintText: !canViewCallHistory ? interpolate(
                NO_PERMISSIONS_ERROR_TEXT, 'call history'
            ) : 'Call History',
            renderIcon: (className) => <CallHistory className={className} />
        },
    ], o => !excluded.includes(o.name))
}