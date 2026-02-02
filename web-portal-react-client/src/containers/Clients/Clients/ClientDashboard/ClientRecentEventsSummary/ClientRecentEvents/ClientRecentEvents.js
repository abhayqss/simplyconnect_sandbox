import React, { memo } from 'react'

import cn from 'classnames'
import { map } from 'underscore'

import { Link } from 'react-router-dom'

import {
    ListGroup as List,
    ListGroupItem as ListItem,
} from 'reactstrap'

import { EVENT_GROUP_COLORS } from 'lib/Constants'

import { path } from 'lib/utils/ContextUtils'
import { DateUtils as DU } from 'lib/utils/Utils'

import './ClientRecentEvents.scss'

const { format, formats } = DU

const TIME_FORMAT = formats.time
const DATE_FORMAT = formats.americanMediumDate

function ClientRecentEvents({ data, clientId, className }) {
    return (
        <div className={cn('ClientRecentEvents', className)}>
            <List className="ClientEventList">
                {map(data, o => (
                    <div key={o.id} className='ClientEventList-ItemWrapper'>
                        <ListItem className="ClientEventList-Item ClientEvent d-flex flex-row">
                            <div className="flex-1 margin-right-15">
                                <Link
                                    to={{
                                        pathname: path(`/clients/${clientId}/events`),
                                        state: { tab: 1, selected: o }
                                    }}
                                    className="ClientEvent-Type"
                                >
                                    {o.type}
                                </Link>
                                <div className="ClientEvent-SubmittedBy">
                                    Submitted by {o.author}
                                </div>
                                <div
                                    className="ClientEvent-Group"
                                    style={{ backgroundColor: EVENT_GROUP_COLORS[o.group.name] }}
                                >
                                    {o.group.title}
                                </div>
                            </div>
                            <div>
                                <div className='ClientEvent-Date'>
                                    {format(o.date, DATE_FORMAT)}
                                </div>
                                <div className='ClientEvent-Time'>
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

export default memo(ClientRecentEvents)