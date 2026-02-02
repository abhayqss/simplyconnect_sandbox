import React, { memo, useEffect, useState } from 'react'

import { Row, Col } from 'reactstrap'

import { Table } from 'components'

import Avatar from 'containers/Avatar/Avatar'

import { useEventNotificationList } from 'hooks/business/event'

import { allAreInteger, DateUtils as DU, isInteger } from 'lib/utils/Utils'

import './EventNotifications.scss'

const { format, formats } = DU

const TIME_FORMAT = formats.time
const DATE_FORMAT = formats.americanMediumDate

const formatDate = value => value ? format(value, DATE_FORMAT) : null
const formatTime = value => value ? format(value, TIME_FORMAT) : null

function EventNotifications({
    eventId,
    clientId,
    organizationId,
}) {
    const {
        state: {
            isFetching,
            fetchCount,
            shouldReload,
            dataSource: {
                data,
                pagination,
            }
        },
        sort,
        fetch,
        fetchIf,
    } = useEventNotificationList({
        eventId,
        clientId,
        organizationId
    })

    function refreshIfNeed() {
        fetchIf(
            !isFetching
            && (
                isInteger(organizationId)
                || allAreInteger(eventId, clientId)
            )
            && (shouldReload || fetchCount === 0)
        )
    }

    useEffect(
        refreshIfNeed,
        [
            eventId,
            clientId,
            organizationId,
            isFetching,
            fetchCount,
            shouldReload
        ]
    )

    return (
        <div className="EventNotifications">
            <Table
                hasHover
                hasOptions
                hasPagination
                keyField="contactId"
                title="Event"
                noDataText="No care team members."
                isLoading={isFetching}
                className="EventNotificationList"
                containerClass="EventNotificationListContainer"
                data={data}
                pagination={pagination}
                columns={[
                    {
                        dataField: 'contactFullName',
                        text: 'Person',
                        headerClasses: 'EventNotificationList-ContactColHeader',
                        sort: true,
                        onSort: sort,
                        formatter: (v, row) => (
                            <div className="d-flex align-items-center">
                                <Avatar
                                    id={row.contactAvatarId}
                                    name={v}
                                    className="EventNotificationList-MemberAvatar"
                                />

                                <div className="EventNotificationList-Member margin-left-10">
                                    <div className="EventNotificationList-MemberName">
                                        {v}
                                    </div>

                                    <div className="EventNotificationList-MemberRelation">
                                        {row.careTeamMemberRole}
                                    </div>
                                </div>
                            </div>
                        )
                    },
                    {
                        dataField: 'organization',
                        text: 'Organization',
                        headerClasses: 'EventNotificationList-OrganizationColHeader',
                        sort: true,
                        onSort: sort
                    },
                    {
                        dataField: 'channels',
                        classes: 'hide-on-tablet',
                        headerClasses: 'EventNotificationList-ChannelColHeader hide-on-tablet',
                        text: 'Channel'
                    },
                    {
                        dataField: 'contactPhone',
                        text: 'Contact',
                        headerClasses: 'EventNotificationList-PhoneColHeader',
                        formatter: (v, row) => {
                            return (
                                <Row>
                                    <Col md={12}>
                                        <div className="EventNotificationList-Label">{v}</div>
                                        <div className="EventNotificationList-Label EventNotificationList-Email">{row.contactEmail}</div>
                                    </Col>
                                </Row>
                            )
                        }
                    },
                    {
                        dataField: 'dateCreated',
                        text: 'Date',
                        headerAlign: 'right',
                        align: 'right',
                        headerClasses: 'EventNotificationList-DateColHeader',
                        formatter: v => (
                            <Row>
                                <Col md={12}>
                                    <div className="EventNotificationList-Date">{formatDate(v)}</div>
                                    <div className="EventNotificationList-Time">{formatTime(v)}</div>
                                </Col>
                            </Row>
                        )
                    }
                ]}
                hasCaption={false}
                columnsMobile={['contactFullName', 'dateCreated']}
                onRefresh={fetch}
            />
        </div>
    )
}

export default memo(EventNotifications)
