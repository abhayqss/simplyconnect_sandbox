import React, { Component } from 'react'

import cn from 'classnames'
import PropTypes from 'prop-types'

import {Link} from 'react-router-dom'

import {Image} from 'react-bootstrap'

import {
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import Table from 'components/Table/Table'
import Avatar from 'components/Avatar/Avatar'

import Actions from 'components/Table/Actions/Actions'

import './ClientMatches.scss'

import {path} from 'lib/utils/ContextUtils'
import {DateUtils as DU} from 'lib/utils/Utils'

const {format, formats} = DU

const ACTION_ICON_SIZE = 36

const DATE_FORMAT = formats.americanMediumDate

export default class ClientMatches extends Component {

    static propTypes = {
        isOpen: PropTypes.bool,
        onEdit: PropTypes.func
    }

    static defaultProps = {
        onEdit: () => {}
    }

    onEdit = (client) => {
        this.props.onEdit(client)
    }

    render () {
        const { data, isFetching } = this.props

        return (
            <div className='MatchedClients'>
                <Table
                    hasHover
                    keyField="id"
                    isLoading={isFetching}
                    className="MatchedClientList"
                    data={data}
                    columns={[
                        {
                            dataField: 'fullName',
                            text: 'Name',
                            headerClasses: 'MatchedClientList-ClientNameHeader',
                            style: (cell, row) => !row.isActive && {
                                opacity: '0.5'
                            },
                            formatter: (v, row, index, formatExtraData, isMobile) => {
                                return (
                                    <div className="d-flex align-items-center">
                                        {row.avatarDataUrl ? (
                                            <Image
                                                src={row.avatarDataUrl}
                                                className={cn(
                                                    'MatchedClientList-ClientAvatar',
                                                    !row.isActive && 'MatchedClientList-ClientAvatar_black-white'
                                                )}
                                            />
                                        ) : (
                                            <Avatar
                                                name={v}
                                                {...!row.isActive && { nameColor: '#e0e0e0' }}
                                            />
                                        )}
                                        {row.canView ? (
                                            <>
                                                <Link
                                                    id={`${isMobile ? 'm-' : ''}matched-client-${row.id}`}
                                                    to={path(`/clients/${row.id}`)}
                                                    className={cn('MatchedClientList-ClientName', row.avatarDataUrl && 'margin-left-10')}>
                                                    {v}
                                                </Link>
                                                <Tooltip
                                                    placement="top"
                                                    target={`${isMobile ? 'm-' : ''}matched-client-${row.id}`}
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
                                                    View client details
                                                </Tooltip>
                                            </>
                                        ) : (
                                            <span
                                                title={v}
                                                id={`matched-client-${row.id}`}
                                                className='MatchedClientList-ClientName'>
                                                    {v}
                                            </span>
                                        )}
                                    </div>
                                )
                            }
                        },
                        {
                            dataField: 'gender',
                            text: 'Gender',
                            style: (cell, row) => !row.isActive && {
                                opacity: '0.5'
                            }
                        },
                        {
                            dataField: 'birthDate',
                            text: 'Birthday',
                            style: (cell, row) => !row.isActive && {
                                opacity: '0.5'
                            },
                            formatter: v => v && format(v, DATE_FORMAT),
                        },
                        {
                            dataField: 'ssnLastFourDigits',
                            text: 'SSN',
                            headerAlign:'right',
                            align:'right',
                            style: (cell, row) => !row.isActive && {
                                opacity: '0.5'
                            },
                            formatter: v => v && `###-##-${v}`
                        },
                        {
                            dataField: 'events',
                            text: 'Events',
                            align:'right',
                            headerStyle: {
                                width: '10%',
                            },
                            style: (cell, row) => !row.isActive && {
                                opacity: '0.5'
                            }
                        },
                        {
                            dataField: 'community',
                            text: 'Community',
                            style: (cell, row) => !row.isActive && {
                                opacity: '0.5'
                            }
                        },
                        {
                            dataField: 'createdDate',
                            text: 'Created',
                            style: (cell, row) => !row.isActive && {
                                opacity: '0.5'
                            },
                            formatter: v => v && format(v, DATE_FORMAT)
                        },
                        {
                            dataField: '',
                            text: '',
                            headerStyle: {
                                width: '60px',
                            },
                            align: 'right',
                            formatter: (v, row) => {
                                return (
                                    <Actions
                                        data={row}
                                        hasEditAction={row.canEdit}
                                        iconSize={ACTION_ICON_SIZE}
                                        editHintMessage="Edit client details"
                                        onEdit={this.onEdit}
                                    />
                                )
                            }
                        }
                    ]}
                    onRefresh={this.onRefresh}
                />
            </div>
        )
    }
}