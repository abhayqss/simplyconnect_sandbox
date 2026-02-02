import React from 'react'

import cn from 'classnames'

import { map } from 'underscore'

import {
    ListGroup as List,
    ListGroupItem as ListItem
} from 'reactstrap'

import { PhoneNumberUtils } from 'lib/utils/Utils'

import { ReactComponent as Indicator } from 'images/dot.svg'

import './HouseholdMemberList.scss'

const { formatPhoneNumber } = PhoneNumberUtils

export default function HouseholdMemberList({ data }) {
    return (
        <List className="HouseholdMemberList">
            {map(data, o => (
                <ListItem
                    key={o.id}
                    className={cn(
                        "HouseholdMemberList-Item",
                        "HouseholdMember",
                        `HouseholdMember_status_${o.isActive ? 'active' : 'inactive'}`
                    )}
                >
                    <div className="HouseholdMember-Demographics">
                        <div className="h-flexbox align-items-center">
                            <Indicator className="HouseholdMember-Indicator margin-right-7"/>
                            <div className="HouseholdMember-FullName">{o.firstName} {o.middleName} {o.lastName}</div>
                        </div>

                        <div className="h-flexbox">
                            <div className="HouseholdMember-Relationship">{o.relationship}</div>
                            <div className="HouseholdMember-BirthDate">, {o.birthDate}</div>
                        </div>

                        {o.phone && (
                            <div className="EmergencyContact-Phone">
                                +{formatPhoneNumber(o.phone)}
                            </div>
                        )}
                    </div>
                </ListItem>
            ))}
        </List>
    )
}