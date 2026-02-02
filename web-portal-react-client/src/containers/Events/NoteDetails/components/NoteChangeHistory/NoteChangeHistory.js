import React, { memo, useEffect, useState } from 'react'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Button } from 'reactstrap'

import Table from 'components/Table/Table'

import NoteViewer from '../../../NoteViewer/NoteViewer'

import { useListDataFetch } from 'hooks/common/redux'

import noteHistoryActions from 'redux/note/history/list/noteHistoryListActions'

import { DateUtils as DU } from 'lib/utils/Utils'

import './NoteChangeHistory.scss'

const { format, formats } = DU
const DATE_AND_TIME_FORMAT = formats.longDateMediumTime12

const NOTE_STATUS_COLORS = {
    UPDATED: '#d3dfe8',
    CREATED: '#d5f3b8',
}

function mapStateToProps(state) {
    return {
        state: state.note.history.list
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(noteHistoryActions, dispatch)
    }
}

function NoteChangeHistory({
    state,
    noteId,
    actions,
    clientId,
    organizationId,
}) {
    const params = { noteId, clientId, organizationId }
    const { isFetching, dataSource } = state

    const [selected, setSelected] = useState(null)
    const [isViewerOpen, setIsViewerOpen] = useState()
    const [shouldRefresh, setShouldRefresh] = useState(true)

    const { fetch } = useListDataFetch(state, actions, params)

    const onOpenViewer = ({ target }) => {
        setSelected(+target.id)
        setIsViewerOpen(true)
    }
    const onCloseViewer = () => setIsViewerOpen(false)

    useEffect(() => {
        if(shouldRefresh){
            fetch()
            setShouldRefresh(false)
        }
    }, [fetch, shouldRefresh])

    return (
        <>
            <Table
                hasPagination
                keyField='id'
                isLoading={isFetching}
                className='NoteChangeHistory'
                containerClass='NoteChangeHistoryContainer'
                data={dataSource.data}
                pagination={dataSource.pagination}
                columns={[
                    {
                        dataField: 'modifiedDate',
                        text: 'Date',
                        align: 'right',
                        headerAlign: 'right',
                        formatter: v => `${format(v, DATE_AND_TIME_FORMAT)}`
                    },
                    {
                        dataField: 'statusName',
                        text: 'Status',
                        formatter: (v, row) => (
                            <span
                                className="NoteChange-Status"
                                style={{ backgroundColor: NOTE_STATUS_COLORS[v] }}>
                                {row.statusTitle}
                            </span>
                        )
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
                        formatter: (v, row) => row.archived ? (
                            <Button
                                color="link"
                                id={row.id}
                                onClick={onOpenViewer}
                                className="NoteChange-ViewBtn">
                                View Details
                            </Button>
                        ) : null
                    }
                ]}
                columnsMobile={['modifiedDate']}
                onRefresh={fetch}
            />

            {isViewerOpen && (
                <NoteViewer
                    isOpen
                    noteId={selected}
                    clientId={clientId}
                    onClose={onCloseViewer}
                    organizationId={organizationId}
                />
            )}
        </>
    )
}

export default memo(
    connect(mapStateToProps, mapDispatchToProps)(NoteChangeHistory)
)