import React from 'react'

import {
    ListGroup as List,
    ListGroupItem as ListItem
} from 'reactstrap'

import Avatar from 'containers/Avatar/Avatar'

import { map } from 'underscore'

import './FreshEmergencyContactList.scss'
import {getAddress} from "lib/utils/Utils";

export default function FreshEmergencyContactList({ data }) {
    return (
        <List className="EmergencyContactList">
            {map(data, o => {
                return (
                    <ListItem
                        key={o.id}
                        className="EmergencyContactList-Item EmergencyContact"
                    >
                        <div className="margin-right-15">
                            <Avatar
                                name={`${o.firstName} ${o.lastName}`}
                                id={o.avatarId}
                                className="EmergencyContact-Avatar"
                            />
                        </div>
                        <div className="EmergencyContact-Demographics">
                            <div className="EmergencyContact-FullName">{`${o.firstName} ${o.lastName}`}</div>
                                    {o.type === 0 && <div className="EmergencyContact-Relationship">Emergency</div>}
                                    {o.type === 1 &&<div className="EmergencyContact-Relationship">Family</div>}
                                    {o.type === 2 &&<div className="EmergencyContact-Relationship">Spouse</div>}
                                    {o.type === 3 &&<div className="EmergencyContact-Relationship">Children</div>}
                                    {o.type === 4 &&<div className="EmergencyContact-Relationship">Others</div>}
                            <div className="EmergencyContact-Phone">{o.phone}</div>
                            <div className="EmergencyContact-Email">{o.email}</div>
                            <div className="EmergencyContact-Email">{getAddress({
                                city: o.city,
                                state: o.state,
                                stateTitle: o.stateTitle,
                                street: o.street,
                                zip: o.zipCode
                            },',')}</div>
                        </div>
                    </ListItem>
                )
                }

            )}
        </List>
    )
}