import React, { Component } from 'react'

import cn from 'classnames'

import PropTypes from 'prop-types'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import {
    Col,
    Row,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import Table from 'components/Table/Table'
import ErrorViewer from 'components/ErrorViewer/ErrorViewer'

import Avatar from 'containers/Avatar/Avatar'
import ContactViewer from 'containers/Admin/Contacts/ContactViewer/ContactViewer'

import eventNotificationListActions from 'redux/event/notification/list/eventNotificationListActions'

import { isEmpty, DateUtils as DU } from 'lib/utils/Utils'

import { PAGINATION, SERVER_ERROR_CODES } from 'lib/Constants'

import './EventNotifications.scss'

const { FIRST_PAGE } = PAGINATION

const { format, formats } = DU

const DATE_TIME_FORMAT = formats.longDateMediumTime12

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    const { list } = state.event.notification

    return {
        error: list.error,
        isFetching: list.isFetching,
        dataSource: list.dataSource,
        shouldReload: list.shouldReload
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(eventNotificationListActions, dispatch)
    }
}

class EventNotifications extends Component {
    static propTypes = {
        eventId: PropTypes.number,
        clientId: PropTypes.number,
        organizationId: PropTypes.number
    }

    state = {
        selected: null,
        isViewerOpen: false
    }

    componentDidMount() {
        this.refresh()
    }

    componentDidUpdate() {
        if (this.props.shouldReload) {
            this.refresh()
        }
    }

    onRefresh = page => {
        this.refresh(page)
    }

    onSort = (field, order) => {
        this.actions.sort(field, order)
    }

    onResetError = () => {
        this.actions.clearError()
    }

    onViewContact = contact => {
        this.setState({
            selected: contact,
            isViewerOpen: true
        })
    }

    onCloseContactViewer = () => {
        this.setState({
            isViewerOpen: false
        })
    }

    get actions() {
        return this.props.actions
    }

    update(isReload, page) {
        const {
            isFetching,
            shouldReload,
            dataSource: ds
        } = this.props

        if (isReload || shouldReload || (!isFetching && isEmpty(ds.data))) {
            const {
                eventId,
                clientId,
                organizationId
            } = this.props

            const { field, order } = ds.sorting
            const { page: p, size } = ds.pagination

            this.actions.load({
                size,
                eventId,
                clientId,
                organizationId,
                page: page || p,
                ...ds.filter.toJS(),
                sort: field ? `${field},${order}` : null
            })
        }
    }

    refresh(page) {
        this.update(true, page || FIRST_PAGE)
    }

    clear() {
        this.actions.clear()
    }

    render() {
        const {
            error,
            isFetching,
            dataSource: ds
        } = this.props

        const {
            selected,
            isViewerOpen
        } = this.state

        return (
            <div className="EventNotifications">
                <Table
                    hasHover
                    keyField="contactId"
                    hasPagination
                    isLoading={isFetching}
                    className="EventNotificationList"
                    data={ds.data}
                    pagination={ds.pagination}
                    columns={[
                        {
                            dataField: 'contactFullName',
                            text: 'Contact',
                            sort: true,
                            headerClasses: 'EventNotificationList-ContactColHeader',
                            onSort: this.onSort,
                            formatter: (v, row, index, formatExtraData, isMobile) => {
                                return (
                                    <div
                                        className={cn(
                                            "d-flex flex-row align-items-center",
                                            !row.isContactActive || row.isContactMarkedForDeletion || row.isContactDeleted
                                                ? "EventNotification-Contact_disabled" : ""
                                        )}
                                        id={`${isMobile ? 'm-' : ''}EventNotificationHint-${row.contactId}`}
                                    >
                                        <>
                                            {
                                                row.hint
                                                && row.contactId
                                                && row.isContactActive
                                                && !row.isContactDeleted
                                                && !row.isContactMarkedForDeletion && (
                                                    <Tooltip
                                                        placement='top-start'
                                                        className="EventNotification-Hint"
                                                        target={`${isMobile ? 'm-' : ''}EventNotificationHint-${row.contactId}`}
                                                        modifiers={[
                                                            {
                                                                name: 'offset',
                                                                options: { offset: [0, 6] }
                                                            },
                                                            {
                                                                name: 'preventOverflow',
                                                                options: { boundary: document.body }
                                                            }
                                                        ]}
                                                    >
                                                        <div>
                                                            <div className="font-weight-bold text-left">
                                                                {row.hint.split('\n')[0]}

                                                            </div>
                                                            {row.hint.split('\n')[1]}
                                                        </div>
                                                    </Tooltip>
                                                )
                                            }
                                            {!row.isContactActive && !row.isContactMarkedForDeletion && !row.isContactDeleted && (
                                                <Tooltip
                                                    placement='top-start'
                                                    className="EventNotification-Hint"
                                                    target={`${isMobile ? 'm-' : ''}EventNotificationHint-${row?.contactId}`}
                                                    modifiers={[
                                                        {
                                                            name: 'offset',
                                                            options: { offset: [0, 6] }
                                                        },
                                                        {
                                                            name: 'preventOverflow',
                                                            options: { boundary: document.body }
                                                        }
                                                    ]}
                                                >
                                                    <div>
                                                        <div className="font-weight-bold text-left">
                                                            The user is inactive
                                                        </div>
                                                    </div>
                                                </Tooltip>
                                            )}
                                            {(row.isContactMarkedForDeletion || row.isContactDeleted) && (
                                                <Tooltip
                                                    placement='top-start'
                                                    className="EventNotification-Hint"
                                                    target={`${isMobile ? 'm-' : ''}EventNotificationHint-${row?.contactId}`}
                                                    modifiers={[
                                                        {
                                                            name: 'offset',
                                                            options: { offset: [0, 6] }
                                                        },
                                                        {
                                                            name: 'preventOverflow',
                                                            options: { boundary: document.body }
                                                        }
                                                    ]}
                                                >
                                                    <div>
                                                        <div className="font-weight-bold text-left">
                                                            A user account was deleted
                                                        </div>
                                                    </div>
                                                </Tooltip>
                                            )}
                                            <Avatar
                                                id={row.contactAvatarId}
                                                name={row.contactFullName}
                                                className="EventNotification-ContactAvatar"
                                            />
                                        </>
                                        <div className={cn(
                                            'EventNotification-ContactButton',
                                            { 'EventNotification-ContactButton_Disabled': !row.canViewContact }
                                        )}
                                             onClick={() => {
                                                 if (row.canViewContact) {
                                                     this.onViewContact({
                                                         id: row.contactId
                                                     })
                                                 }
                                             }}
                                        >
                                            <div className="EventNotification-ContactFullName">
                                                {row.contactFullName}
                                            </div>
                                            <div className="EventNotification-ContactRole">
                                                {row.careTeamMemberRole}
                                            </div>
                                        </div>
                                    </div>
                                )
                            },
                        },
                        {
                            dataField: 'responsibility',
                            text: 'Responsibility',
                            headerClasses: 'EventNotificationList-ResponsibilityColHeader',
                            sort: true,
                            onSort: this.onSort,
                        },
                        {
                            dataField: 'organization',
                            text: 'Organization',
                            headerClasses: 'EventNotificationList-OrganizationColHeader',
                            sort: true,
                            onSort: this.onSort,
                        },
                        {
                            dataField: 'channels',
                            text: 'Channel',
                            classes: 'hide-on-tablet',
                            headerClasses: 'EventNotificationList-ChannelColHeader hide-on-tablet',
                        },
                        {
                            dataField: 'dateCreated',
                            text: 'Date',
                            headerAlign: 'right',
                            align: 'right',
                            headerClasses: 'EventNotificationList-DateColHeader',
                            formatter: v => format(v, DATE_TIME_FORMAT)
                        },
                    ]}
                    columnsMobile={['contactFullName']}
                    onRefresh={this.onRefresh}
                />
                {isViewerOpen && (
                    <ContactViewer
                        isOpen
                        contactId={selected.id}
                        onClose={this.onCloseContactViewer}
                    />
                )}
                {error && !isIgnoredError(error) && (
                    <ErrorViewer
                        isOpen
                        error={error}
                        onClose={this.onResetError}
                    />
                )}
            </div>
        )
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(EventNotifications)