import React from 'react'

import cn from 'classnames'

import { Rater } from 'components'

import { Button } from 'components/buttons'

import {
    isInteger,
    isNotEmpty,
    PhoneNumberUtils,
    getStandardFormattedAddress
} from 'lib/utils/Utils'

import {
    map
} from 'lib/utils/ArrayUtils'

import { ReactComponent as Phone } from 'images/phone.svg'
import { ReactComponent as Location } from 'images/location.svg'

import './CommunitySummary.scss'

const { formatPhoneNumber } = PhoneNumberUtils

function CommunitySummary(
    {
        data,
        className,
        onDelete,
        onViewDetails,
        onCreateReferral
    }
) {
    return (
        <div className={cn('CommunitySummary', className)}>
            <div className="CommunitySummary-Head">
                <div className="CommunitySummary-Title">
                    {data?.organizationName}, {data?.communityName}
                </div>
                {isInteger(data.rating) && (
                    <Rater
                        withDigits
                        interactive={false}
                        rating={data.rating}
                        className="CommunitySummary-Rater margin-bottom-6"
                    />
                )}
                <div className="CommunitySummary-SubTitle">
                    {map(data.serviceCategories, o => (o.label ?? o.title)).join(', ')}
                </div>
            </div>

            <div className="CommunitySummary-Body">
                {data.address && (
                    <div className="CommunitySummary-Detail">
                        <Location className="CommunitySummary-Icon"/>
                        <div className="CommunitySummary-Address">
                            {data.address}
                        </div>
                    </div>
                )}
                {data.phone && (
                    <div className="CommunitySummary-Detail">
                        <Phone className="CommunitySummary-Icon"/>
                        <div className="CommunitySummary-Phone">
                            {formatPhoneNumber(data.phone)}
                        </div>
                    </div>
                )}
            </div>

            <div className="CommunitySummary-Footer">
                <Button
                    outline
                    color="success"
                    className="CommunitySummary-Button"
                    onClick={() => onViewDetails(data)}
                >
                    View Details
                </Button>
                <Button
                    outline
                    color="success"
                    className="CommunitySummary-Button"
                    onClick={() => onDelete(data)}
                >
                    Delete
                </Button>
                {data.isReferralEnabled && (
                    <Button
                        color="success"
                        disabled={!(data.hasReferralEmails && data.canAddReferral)}
                        className="CommunitySummary-Button CommunitySummary-CreateReferralButton"
                        onClick={() => onCreateReferral(data)}
                        tooltip={!(data.hasReferralEmails && data.canAddReferral) && {
                            trigger: 'click hover',
                            render: () => (
                                <>
                                    {!data.hasReferralEmails && (
                                        <div>The community doesn't have email for receiving referral requests. Please contact Simply Connect support team.</div>
                                    )}
                                    {!data.canAddReferral && (
                                        <div>You can't refer a client to this community.</div>
                                    )}
                                </>
                            )
                        }}
                    >
                        Create Referral
                    </Button>
                )}
            </div>
        </div>
    )
}

export default CommunitySummary
