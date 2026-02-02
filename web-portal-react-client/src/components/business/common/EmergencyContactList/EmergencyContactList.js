import React from 'react'

import {
    ListGroup as List,
    ListGroupItem as ListItem
} from 'reactstrap'

import Avatar from 'containers/Avatar/Avatar'

import { map } from 'underscore'

import './EmergencyContactList.scss'

export default function EmergencyContactList({ data }) {
    return (
        <List className="EmergencyContactList">
            {map(data, o => (
                <ListItem
                    key={o.id}
                    className="EmergencyContactList-Item EmergencyContact"
                >
                    <div className="margin-right-15">
                        <Avatar
                            name={o.fullName}
                            id={o.avatarId}
                            className="EmergencyContact-Avatar"
                        />
                    </div>

                    <div className="EmergencyContact-Demographics">
                        <div className="EmergencyContact-FullName">{o.fullName}</div>
                        {o.relationship && (
                            <div className="EmergencyContact-Relationship">{o.relationship}</div>
                        )}
                        <div className="EmergencyContact-Phone">{o.phone}</div>
                        <div className="EmergencyContact-Email">{o.email || o.address}</div>
                    </div>
                </ListItem>
            ))}
        </List>
    )
}