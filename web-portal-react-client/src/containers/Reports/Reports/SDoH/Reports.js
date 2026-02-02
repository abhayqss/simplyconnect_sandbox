import React, { useState, useEffect, useCallback } from 'react'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Badge } from 'reactstrap'

import DocumentTitle from 'react-document-title'

import {
    Table,
    Footer,
    IconButton,
} from 'components'

import SideBar from 'containers/SideBar/SideBar'

import {
    useDownloadingStatusInfoToast
} from 'hooks/common'

import {
    useSideBarUpdate,
    useListDataFetch
} from 'hooks/common/redux'

import {
    useCanMarkAsSentSDoHReportQuery
} from 'hooks/business/reports'

import {
    ErrorDialog,
    ConfirmDialog
} from 'components/dialogs'

import Loader from 'components/Loader/Loader'

import listActions from 'redux/report/sdoh/list/sDoHReportListActions'
import sendActions from 'redux/report/sdoh/send/sendSDoHReportActions'
import detailsActions from 'redux/report/sdoh/details/sDoHReportDetailsActions'
import canMarkAsSentActions from 'redux/report/sdoh/can/mark-as-sent/canMarkAsSentSDoHReportActions'

import {
    SERVER_ERROR_CODES,
    ALLOWED_FILE_FORMATS,
    SDOH_REPORT_STATUS_COLORS
} from 'lib/Constants'

import {
    isInteger,
    DateUtils as DU
} from 'lib/utils/Utils'

import { ReactComponent as Checkbox } from 'images/checkbox.svg'
import { ReactComponent as Download } from 'images/download.svg'
import { ReactComponent as DownloadZip } from 'images/zip-mono.svg'
import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import { getSideBarItems } from '../../SideBarItems'

import SDoHReportPrimaryFilter from './SDoHReportPrimaryFilter/SDoHReportPrimaryFilter'

import './Reports.scss'

const { format, formats } = DU

const DATE_FORMAT = formats.americanMediumDate

const { XLSX, ZIP } = ALLOWED_FILE_FORMATS

const {
    SDOH_DATA_ERROR,
    ACCOUNT_INACTIVE
} = SERVER_ERROR_CODES

function isIgnoredError(e = {}) {
    return e.code === ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    return {
        state: state.report.sdoh.list,
        send: state.report.sdoh.send,
        details: state.report.sdoh.details,
        can: { markAsSent: state.report.sdoh.can.markAsSent }
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(listActions, dispatch),
            send: bindActionCreators(sendActions, dispatch),
            details: bindActionCreators(detailsActions, dispatch),
            can: { markAsSent: bindActionCreators(canMarkAsSentActions, dispatch) }
        }
    }
}

function Reports({ state, actions, can, send, details }) {
    const {
        isFetching,
        shouldReload,
        dataSource: ds
    } = state

    const error = state.error || can.markAsSent.error || details.error || send.error

    const { organizationId, communityIds } = ds.filter

    const [selected, setSelected] = useState({})
    const [shouldRefresh, setShouldRefresh] = useState(false)
    const [isDownloading, setIsDownloading] = useState(false)
    const [isSendingToUHCConfirmOpen, setIsSendingToUHCConfirmOpen] = useState(false)

    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    const update = useSideBarUpdate()

    const { fetch, fetchIf } = useListDataFetch(state, actions, { organizationId })

    function refresh() {
        setShouldRefresh(true)
    }

    function refreshIfNeed() {
        setShouldRefresh(false)
        fetchIf(
            isInteger(organizationId)
            && (shouldRefresh || shouldReload)
        )
    }

    async function download(id, format) {
        setIsDownloading(true)

        try {
            withDownloadingStatusInfoToast(async () => await actions.details.download(id, { format }))

            setIsDownloading(false)
        } catch {
            setIsDownloading(false)
        }
    }

    function resetErrors() {
        actions.clearError()
        actions.send.clearError()
        actions.details.clearError()
        actions.can.markAsSent.clearError()
    }

    const onResetErrors = useCallback(() => {
        resetErrors()
    }, [actions])

    const onDownload = useCallback(id => {
        download(id, XLSX)
    }, [actions])

    const onDownloadZip = useCallback(id => {
        download(id, ZIP)
    }, [actions])

    const onSendToUHC = useCallback(id => {
        setSelected({ id })
    }, [])

    const onConfirmSendingToUHC = useCallback(() => {
        setSelected({})
        setIsSendingToUHCConfirmOpen(false)
        actions.send.load(selected.id).then(refresh)
    }, [actions, selected])

    const onCancelSendingToUHC = useCallback(() => {
        setSelected({})
        setIsSendingToUHCConfirmOpen(false)
    }, [])

    useCanMarkAsSentSDoHReportQuery({ reportId: selected.id }, {
        onSuccess: ({ data: can }) => {
            if (can) {
                setIsSendingToUHCConfirmOpen(true)
            }
        },
        onFailure: () => {
            setSelected({})
        }
    })

    useEffect(() => {
        update({
            isHidden: false,
            items: getSideBarItems()
        })
    }, [update])

    useEffect(
        refreshIfNeed,
        [
            fetchIf,
            communityIds,
            shouldRefresh,
            shouldReload
        ]
    )

    useEffect(() => actions.clear, [actions.clear])

    return (
        <DocumentTitle title="Simply Connect | SDoH Reports">
            <SideBar>
                <div className="SDoHReports">
                    {isDownloading && (
                        <Loader hasBackdrop/>
                    )}

                    <SDoHReportPrimaryFilter/>
                    <Table
                        hasHover
                        hasOptions
                        hasPagination
                        keyField="id"
                        title="SDoH Reports"
                        noDataText="No reports."
                        isLoading={isFetching}
                        className="SDoHReportList"
                        containerClass="SDoHReportListContainer"
                        data={ds.data}
                        pagination={ds.pagination}
                        columnsMobile={['periodStart', 'statusName']}
                        columns={[
                            {
                                dataField: 'periodStart',
                                text: 'Reporting Period',
                                headerStyle: {
                                    width: '130px'
                                },
                                sort: true,
                                onSort: actions.sort,
                                formatter: (v, { periodEnd }) => (
                                    <span>{format(v, DATE_FORMAT)} - {format(periodEnd, DATE_FORMAT)}</span>
                                )
                            },
                            {
                                dataField: 'statusName',
                                text: 'Status',
                                sort: true,
                                headerStyle: {
                                    width: '80px'
                                },
                                onSort: actions.sort,
                                formatter: (v, row) => (
                                    <div
                                        className="SDoHReport-Status"
                                        style={{ backgroundColor: SDOH_REPORT_STATUS_COLORS[v] }}
                                    >
                                        {row.statusTitle}
                                    </div>
                                )
                            },
                            {
                                dataField: 'id',
                                text: '',
                                headerAlign: 'right',
                                align: 'right',
                                headerStyle: {
                                    width: '170px'
                                },
                                formatter: (id, row) => (
                                    <>
                                        <IconButton
                                            Icon={Download}
                                            name={`report-${id}_download`}
                                            tipText="Download excel file"
                                            tipPlace="top"
                                            onClick={() => onDownload(id)}
                                            className="SDoHReportList-ActionBtn"
                                        />
                                        <IconButton
                                            Icon={DownloadZip}
                                            name={`report-${id}_download-zip`}
                                            tipText="Download UHC Zip file"
                                            tipPlace="top"
                                            onClick={() => onDownloadZip(id)}
                                            className="SDoHReportList-ActionBtn"
                                        />
                                        {row.statusName === 'PENDING_REVIEW' && (
                                            <IconButton
                                                Icon={Checkbox}
                                                name={`report-${id}_review`}
                                                tipText="Mark report as sent to UHC"
                                                tipPlace="top"
                                                onClick={() => onSendToUHC(id)}
                                                className="SDoHReportList-ActionBtn"
                                            />
                                        )}
                                    </>
                                )
                            }
                        ]}
                        renderCaption={title => {
                            return (
                                <>
                                    <div className="SDoHReportList-Caption">
                                        <div className="SDoHReportList-Title">
                                            <span className="SDoHReportList-TitleText">
                                                {title}
                                            </span>
                                            {(ds.pagination.totalCount > 0) && (
                                                <Badge color="info" className="Badge Badge_place_top-right">
                                                    {ds.pagination.totalCount}
                                                </Badge>
                                            )}
                                        </div>
                                    </div>
                                </>
                            )
                        }}
                        onRefresh={fetch}
                    />
                    {isSendingToUHCConfirmOpen && (
                        <ConfirmDialog
                            isOpen
                            icon={Warning}
                            confirmBtnText="OK"
                            title="The report will be marked as Sent to UHC"
                            onConfirm={onConfirmSendingToUHC}
                            onCancel={onCancelSendingToUHC}
                        />
                    )}
                    {error && !isIgnoredError(error) && (
                        <ErrorDialog
                            isOpen
                            title={error.message}
                            {...error.code === SDOH_DATA_ERROR && {
                                text: "Please download the Excel report to see the data issues. Update the client(s)' service plan(s) or clients' profile(s) to resolve the data issues."
                            }}
                            buttons={[
                                {
                                    text: 'Close',
                                    onClick: onResetErrors
                                }
                            ]}
                        />
                    )}
                </div>
                <Footer theme="gray"/>
            </SideBar>
        </DocumentTitle>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(Reports)