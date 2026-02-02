import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import { connect } from 'react-redux'
import { compose, bindActionCreators } from 'redux'

import { Link } from 'react-router-dom'

import {
    Badge,
    Collapse,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import DocumentTitle from 'react-document-title'

import {
    useResponse,
    useRefCurrent,
    useDownloadingStatusInfoToast
} from 'hooks/common'

import { useListDataFetch } from 'hooks/common/redux'

import {
    Table,
    Footer,
    IconButton,
    ErrorViewer
} from 'components'

import { ConfirmDialog } from 'components/dialogs'

import Avatar from 'containers/Avatar/Avatar'

import { UpdateSideBarAction } from 'actions/admin'

import listActions from 'redux/incident/report/list/incidentReportListActions'
import detailsActions from 'redux/incident/report/details/incidentReportDetailsActions'
import deletionActions from 'redux/incident/report/deletion/incidentReportDeletionActions'

import { path } from 'lib/utils/ContextUtils'

import {
    ALLOWED_FILE_FORMATS,
    INCIDENT_REPORT_STATUS_COLORS
} from 'lib/Constants'

import {
    isInteger,
    DateUtils as DU
} from 'lib/utils/Utils'

import { ReactComponent as Filter } from 'images/filters.svg'

import { ReactComponent as Pencil } from 'images/pencil.svg'
import { ReactComponent as Delete } from 'images/delete.svg'
import { ReactComponent as Download } from 'images/download.svg'
import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import IncidentReportEditor from '../IncidentReportEditor/IncidentReportEditor'
import IncidentReportFilter from '../IncidentReportFilter/IncidentReportFilter'
import IncidentReportPrimaryFilter from '../IncidentReportPrimaryFilter/IncidentReportPrimaryFilter'

import './IncidentReports.scss'

const DATE_FORMAT = DU.formats.americanMediumDate

const { PDF } = ALLOWED_FILE_FORMATS

function mapStateToProps(state) {
    return {
        state: state.incident.report.list,
        count: state.incident.report.count.value,
        canView: state.incident.report.can.view.value
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(listActions, dispatch),
            details: bindActionCreators(detailsActions, dispatch),
            deletion: bindActionCreators(deletionActions, dispatch)
        }
    }
}

function IncidentReports(
    {
        state,
        actions,
        deletionError
    }
) {
    const {
        isFetching,
        shouldReload,
        isFilterOpen,
        dataSource: ds
    } = state

    const error = (
        state.error || deletionError
    )

    const { organizationId } = ds.filter

    const defaultState = useRefCurrent({ isFilterOpen })

    const [selected, setSelected] = useState(null)
    const [shouldRefresh, toggleRefresh] = useState(false)

    const [isEditorOpen, toggleEditor] = useState(false)
    const [isDeleteConfirmDialogOpen, toggleDeleteConfirmDialog] = useState(false)

    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    const {
        fetch,
        fetchIf
    } = useListDataFetch(
        state, actions, { organizationId }
    )

    function refresh() {
        toggleRefresh(true)
    }

    function refreshIfNeed() {
        toggleRefresh(false)
        fetchIf(
            isInteger(organizationId)
            && (shouldRefresh || shouldReload)
        )
    }

    function closeEditor() {
        setSelected(false)
        toggleEditor(false)
    }

    function closeDeleteConfirmDialog() {
        setSelected(false)
        toggleDeleteConfirmDialog(false)
    }

    function editDraft(data) {
        setSelected(data)
        toggleEditor(true)
    }

    const onToggleFilter = useCallback(() => {
        actions.toggleFilter(!isFilterOpen)
    }, [actions, isFilterOpen])

    function onDownload({ id }) {
        withDownloadingStatusInfoToast(() => actions.details.download(id, { format: PDF }))
    }

    function onEdit(o) {
        setSelected(o)
        toggleEditor(true)
    }

    function onDelete(o) {
        setSelected(o)
        toggleDeleteConfirmDialog(true)
    }

    const onCloseEditor = useCallback(closeEditor, [])

    const onSaveSuccess = useCallback(refresh, [])

    const onEditDraft = useCallback(editDraft, [])

    const onDeleteResponse = useResponse({
        onSuccess: () => toggleRefresh(true)
    })

    function onConfirmDelete() {
        closeDeleteConfirmDialog()
        actions.deletion.delete(selected.id).then(onDeleteResponse)
    }

    const onClearError = useCallback(() => {
        actions.clearError()
        actions.deletion.clearError()
    }, [actions])

    useEffect(
        refreshIfNeed,
        [
            fetchIf,
            shouldReload,
            shouldRefresh
        ]
    )

    useEffect(() => () => {
        actions.clear(defaultState)
    }, [actions, defaultState])

    return (
        <DocumentTitle title="Simply Connect | Incident Reports">
            <>
                <UpdateSideBarAction />
                <div className="IncidentReports">
                    <IncidentReportPrimaryFilter className="margin-bottom-30" />
                    <div className="IncidentReports-Header">
                        <div className="IncidentReports-HeaderItem">
                            <div className="IncidentReports-Title">
                                <div className="IncidentReports-TitleText">
                                    Incident Reports
                                </div>
                                {(ds.pagination.totalCount > 0) ? (
                                    <Badge color='info' className="Badge Badge_place_top-right">
                                        {ds.pagination.totalCount}
                                    </Badge>
                                ) : null}
                            </div>
                        </div>
                        <div className="IncidentReports-HeaderItem">
                            <div className="IncidentReports-Actions">
                                <Filter
                                    className={cn(
                                        'IncidentReportFilter-Icon',
                                        isFilterOpen
                                            ? 'IncidentReportFilter-Icon_rotated_90'
                                            : 'IncidentReportFilter-Icon_rotated_0',
                                    )}
                                    onClick={onToggleFilter}
                                />
                            </div>
                        </div>
                    </div>
                    <Collapse isOpen={isFilterOpen}>
                        <IncidentReportFilter className="margin-bottom-50" />
                    </Collapse>
                    <Table
                        hasHover
                        hasOptions
                        hasPagination
                        keyField="id"
                        title="Incident Reports"
                        noDataText="No Incident Reports."
                        isLoading={isFetching}
                        className='IncidentReportList'
                        containerClass='IncidentReportListContainer'
                        data={ds.data}
                        pagination={ds.pagination}
                        columns={[
                            {
                                dataField: 'clientName',
                                text: 'Client Name',
                                headerClasses: 'IncidentReportList-ClientHeader',
                                sort: true,
                                onSort: actions.sort,
                                formatter: (v, row, index, formatExtraData, isMobile) => (
                                    <div className="d-flex flex-row align-items-center">
                                        <Avatar
                                            id={row.clientAvatarId}
                                            name={row.clientName}
                                            className="IncidentReportList-ClientAvatar"
                                        />
                                        <div className='text-trim'>
                                            <Link
                                                id={`${isMobile ? 'm-' : ''}order-${row.id}`}
                                                className='IncidentReportList-Client cursor-pointer'
                                                to={path(`/incident-reports/${row.id}`)}
                                                onClick={() => { }}
                                            >
                                                {v}
                                            </Link>
                                        </div>
                                        <Tooltip
                                            target={`${isMobile ? 'm-' : ''}order-${row.id}`}
                                            trigger="hover"
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
                                            Click to view the incident report
                                        </Tooltip>
                                    </div>
                                )
                            },
                            {
                                dataField: 'eventType',
                                text: 'Event type',
                                sort: true,
                                headerStyle: { width: '12%' },
                                onSort: actions.sort
                            },
                            {
                                dataField: 'statusName',
                                text: 'Status',
                                sort: true,
                                onSort: actions.sort,
                                headerStyle: { width: '10%' },
                                formatter: (v, row) => (
                                    <div
                                        className="IncidentReport-Status"
                                        style={{ backgroundColor: INCIDENT_REPORT_STATUS_COLORS[v] }}
                                    >
                                        {row.statusTitle}
                                    </div>
                                )
                            },
                            {
                                dataField: 'incidentDate',
                                text: 'Incident Date',
                                sort: true,
                                headerAlign: 'right',
                                align: 'right',
                                onSort: actions.sort,
                                headerStyle: { width: '14%' },
                                formatter: v => v ? DU.format(v, DATE_FORMAT) : ''
                            },
                            {
                                dataField: 'id',
                                text: '',
                                align: 'right',
                                headerStyle: { width: '12%' },
                                formatter: (v, row) => (
                                    <>
                                        <IconButton
                                            Icon={Download}
                                            name={`ir-${v}-download`}
                                            onClick={() => onDownload(row)}
                                            tipText="Download Pdf"
                                            className="IncidentReportList-ActionBtn"
                                        />
                                        {row.clientActive && <IconButton
                                            Icon={Pencil}
                                            name={`ir-${v}-edit`}
                                            onClick={() => onEdit(row)}
                                            tipText="Edit Incident Report"
                                            className="IncidentReportList-ActionBtn"
                                        />}
                                        {row.canDelete && row.clientActive && (
                                            <IconButton
                                                Icon={Delete}
                                                name={`ir-${v}-delete`}
                                                onClick={() => onDelete(row)}
                                                tipText="Delete Incident Report"
                                                className="IncidentReportList-ActionBtn"
                                            />
                                        )}
                                    </>
                                )
                            }
                        ]}
                        hasCaption={false}
                        columnsMobile={['clientName', 'incidentDate']}
                        onRefresh={fetch}
                    />
                </div>
                <IncidentReportEditor
                    reportId={selected?.id}
                    clientId={selected?.clientId}
                    eventId={selected?.eventId}
                    isOpen={isEditorOpen}
                    onClose={onCloseEditor}
                    onEditDraft={onEditDraft}
                    onSaveSuccess={onSaveSuccess}
                />
                {isDeleteConfirmDialogOpen && (
                    <ConfirmDialog
                        isOpen
                        icon={Warning}
                        confirmBtnText="Confirm"
                        title="The incident report will be deleted"
                        onConfirm={onConfirmDelete}
                        onCancel={closeDeleteConfirmDialog}
                    />
                )}

                {error && (
                    <ErrorViewer
                        isOpen
                        error={error}
                        onClose={onClearError}
                    />
                )}
                <Footer theme='gray'/>
            </>
        </DocumentTitle>
    )
}

export default compose(
    memo,
    connect(mapStateToProps, mapDispatchToProps)
)(IncidentReports)