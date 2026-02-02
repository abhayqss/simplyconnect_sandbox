import React, { Component } from 'react'

import PropTypes from 'prop-types'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Button } from 'reactstrap'

import Table from 'components/Table/Table'
import ErrorViewer from 'components/ErrorViewer/ErrorViewer'

import eventNoteListActions from 'redux/event/note/list/eventNoteListActions'

import { isEmpty, DateUtils as DU } from 'lib/utils/Utils'

import { PAGINATION, SERVER_ERROR_CODES } from 'lib/Constants'

import './EventNotes.scss'

import NoteViewer from '../../NoteViewer/NoteViewer'

const STATUS_COLORS = {
    UPDATED: '#d3dfe8',
    CREATED: '#d5f3b8'
}

const { FIRST_PAGE } = PAGINATION

const { format, formats } = DU

const DATE_TIME_FORMAT = formats.longDateMediumTime12

function isIgnoredError (e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps (state) {
    const { list } = state.event.note

    return {
        error: list.error,
        isFetching: list.isFetching,
        dataSource: list.dataSource,
        shouldReload: list.shouldReload
    }
}

function mapDispatchToProps (dispatch) {
    return {
        actions: bindActionCreators(eventNoteListActions, dispatch)
    }
}

class EventNotes extends Component {
    static propTypes = {
        eventId: PropTypes.number,
        clientId: PropTypes.number,
        organizationId: PropTypes.number
    }

    state = {
        selected: null,
        isViewerOpen: false
    }

    componentDidMount () {
        this.refresh()
    }

    componentDidUpdate () {
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

    onOpenViewer = ({ target }) => {
        this.setState({
            selected: target.id,
            isViewerOpen: true
        })
    }

    onCloseViewer = () => {
        this.setState({
            selected: null,
            isViewerOpen: false
        })
    }

    get actions () {
        return this.props.actions
    }

    update (isReload, page) {
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

            const { page: p, size } = ds.pagination

            this.actions.load({
                size,
                eventId,
                clientId,
                organizationId,
                page: page || p,
                ...ds.filter.toJS()
            })
        }
    }

    refresh (page) {
        this.update(true, page || FIRST_PAGE)
    }

    clear () {
        this.actions.clear()
    }

    render () {
        const {
            error,
            clientId,
            isFetching,
            organizationId,
            dataSource: ds
        } = this.props

        const {
            selected,
            isViewerOpen
        } = this.state

        return (
            <div className="EventNotes">
                <Table
                    hasPagination
                    keyField='id'
                    isLoading={isFetching}
                    className='EventNoteList'
                    containerClass='EventNoteListContainer'
                    data={ds.data}
                    pagination={ds.pagination}
                    columns={[
                        {
                            dataField: 'date',
                            text: 'Date',
                            align: 'right',
                            headerAlign: 'right',
                            formatter: v => format(v, DATE_TIME_FORMAT)
                        },
                        {
                            dataField: 'statusName',
                            text: 'Status',
                            formatter: (v, row) => (
                                <span
                                    className="EventNote-Status"
                                    style={{ backgroundColor: STATUS_COLORS[v] }}>
                                    {row.statusTitle}
                                </span>
                            )
                        },
                        {
                            dataField: 'subTypeTitle',
                            text: 'Type',
                        },
                        {
                            dataField: 'author',
                            text: 'Author',
                        },
                        {
                            dataField: 'authorRoleTitle',
                            text: 'Role',
                        },
                        {
                            dataField: '@actions',
                            text: 'Updates',
                            headerStyle: {
                                width: '150px'
                            },
                            formatter: (v, row) => (
                                <Button
                                    id={row.id}
                                    color="link"
                                    className="EventNote-ViewBtn"
                                    onClick={this.onOpenViewer}>
                                    View Details
                                </Button>
                            )
                        }
                    ]}
                    columnsMobile={['subTypeTitle']}
                    onRefresh={this.onRefresh}
                />
                {isViewerOpen && (
                    <NoteViewer
                        isOpen
                        clientId={clientId}
                        organizationId={organizationId}
                        noteId={selected}
                        onClose={this.onCloseViewer}
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

export default connect(mapStateToProps, mapDispatchToProps)(EventNotes)