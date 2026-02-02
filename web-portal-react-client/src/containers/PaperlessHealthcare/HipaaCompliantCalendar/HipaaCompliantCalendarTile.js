import React, {
    memo,
    useCallback
} from 'react'

import {
    useHistory
} from 'react-router-dom'

import {
	useOrganizationsQuery
} from 'hooks/business/directory/query'

import {
    useCanViewAppointmentsQuery
} from 'hooks/business/appointments'

import {
    isEmpty
} from 'lib/utils/Utils'

import {
    path
} from 'lib/utils/ContextUtils'

import {
    FEATURES
} from 'lib/Constants'

import imageSrc from 'images/hws/hipaa-calendar.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const { HIPAA_COMPLIANT_CALENDAR } = FEATURES

function HipaaCompliantCalendarTile({ theme, onClick }) {
    const history = useHistory()

    const {
        data: organizations = []
    } = useOrganizationsQuery({
        areAppointmentsEnabled: true
    }, {
        staleTime: 0
    })

    const {
        data: canViewAppointments
    } = useCanViewAppointmentsQuery({}, { staleTime: 0 })

    const _onClick = useCallback(name => {
        if (!isEmpty(organizations) && canViewAppointments) {
            history.push(path('/appointments'))
        } else onClick(name)
    }, [
        organizations,
        canViewAppointments
    ])

    return (
        <SectionTile
            name={HIPAA_COMPLIANT_CALENDAR}
            title="HIPAA-Compliant Calendar"
            imageSrc={imageSrc}
            theme={theme}
            onClick={_onClick}
        />
    )
}

export default memo(HipaaCompliantCalendarTile)