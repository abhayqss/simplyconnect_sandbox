import React from 'react'

import Truncate from 'react-truncate'

import { Button } from 'reactstrap'

import {
    Rater,
    Loader
} from 'components'

import {
    useCommunityLocation
} from 'hooks/business/community'

import {
    isInteger,
    isNotEmpty,
    PhoneNumberUtils
} from 'lib/utils/Utils'

import { ReactComponent as Phone } from 'images/phone.svg'
import { ReactComponent as Location } from 'images/location.svg'

import './CommunityDescription.scss'

const { formatPhoneNumber } = PhoneNumberUtils

function CommunityDescription(
    {
        communityId,
        onViewDetails,
        onScheduleAppointment
    }
) {
    const {
        data,
        isFetching
    } = useCommunityLocation(
        { communityId },
        {
            staleTime: 0,
            cacheTime: 500
        }
    )

    const {
        communityName,
        organizationName,

        allowAppointments,
        serviceCategories,

        rating,
        address,
        phone
    } = data ?? {}

    return data ? (
        <div className='MarketplaceCommunityDescription'>
            {isFetching && (
                <Loader isCentered/>
            )}

            <div className='MarketplaceCommunityDescription-Title'>
                <Truncate lines={3}>
                    {organizationName}, {communityName}
                </Truncate>
            </div>

            {isNotEmpty(serviceCategories) && (
                <div className="MarketplaceCommunityDescription-Categories">
                    <Truncate lines={2}>
                        {serviceCategories.map(o => o.label ?? o.title).join(', ')}
                    </Truncate>
                </div>
            )}

            <div className='MarketplaceCommunityDescription-Address'>
                <div>
                    <Location className="MarketplaceCommunityDescription-Icon" />
                </div>
                {address}
            </div>

            {phone && (
                <div className='MarketplaceCommunityDescription-Phone'>
                    <div>
                        <Phone className="MarketplaceCommunityDescription-Icon" />
                    </div>
                    {formatPhoneNumber(phone)}
                </div>
            )}

            {isInteger(rating) && (
                <div className="MarketplaceCommunityDescription-Rating">
                    <Rater
                        total={5}
                        withDigits
                        rating={rating}
                        interactive={false}
                    />
                </div>
            )}

            <div>
                {allowAppointments ? (
                    <Button
                        color='success'
                        className='MarketplaceCommunityDescription-AppointmentBtn'
                        onClick={() => onScheduleAppointment(data)}
                    >
                        Request an Appointment
                    </Button>
                ) : <div />}

                <Button
                    outline
                    color='success'
                    onClick={() => onViewDetails(data)}
                >
                    Details
                </Button>
            </div>
        </div>
    ) : (
        <div className='MarketplaceCommunityDescription'>
            <Loader />
        </div>
    )
}

export default CommunityDescription
