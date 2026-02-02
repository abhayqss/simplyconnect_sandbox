import React, { memo } from 'react'

import cn from 'classnames'

import { Link } from 'react-router-dom'

import {
    ListGroup as List,
    ListGroupItem as ListItem,
} from 'reactstrap'

import { NOTE_TYPES } from 'lib/Constants'

import { path } from 'lib/utils/ContextUtils'
import { DateUtils as DU } from 'lib/utils/Utils'

import './ClientRecentNotes.scss'

const { format, formats } = DU

const TIME_FORMAT = formats.time
const DATE_FORMAT = formats.americanMediumDate

const {
    EVENT_NOTE,
    GROUP_NOTE,
    CLIENT_NOTE,
    PATIENT_NOTE
} = NOTE_TYPES

const NOTE_TYPE_COLORS = {
    [EVENT_NOTE]: '#d5f3b8',
    [CLIENT_NOTE]: '#e7ccfe',
    [PATIENT_NOTE]: '#e7ccfe',
    [GROUP_NOTE]: '#c9e5ff',
}

function ClientRecentNotes({ data, clientId, className }) {
    return (
        <div className={cn('ClientRecentNotes', className)}>
            <List className="ClientNoteList">
                {data.map(o => (
                    <div key={o.id} className='ClientNoteList-ItemWrapper'>
                        <ListItem className="ClientNoteList-Item ClientNote d-flex flex-row">
                            <div
                                className="flex-1 margin-right-15"
                                style={{ maxWidth: `calc(100% - ${o.date ? '89' : '15'}px)` }}
                            >
                                <Link
                                    to={{
                                        pathname: path(`/clients/${clientId}/events`),
                                        state: { tab: 2, selected: o }
                                    }}
                                    className="ClientNote-SubType"
                                >
                                    {o.subType.title}
                                </Link>
                                <div className='ClientNote-Description'>
                                    {o.text}
                                </div>
                                <div
                                    className='ClientNote-Type'
                                    style={{ backgroundColor: NOTE_TYPE_COLORS[o.type.name] }}
                                >
                                    {o.type.title}
                                </div>
                            </div>
                            <div>
                                <div className='ClientNote-Date'>
                                    {format(o.date, DATE_FORMAT)}
                                </div>
                                <div className='ClientNote-Time'>
                                    {format(o.date, TIME_FORMAT)}
                                </div>
                            </div>
                        </ListItem>
                    </div>
                ))}
            </List>
        </div>
    )
}

export default memo(ClientRecentNotes)