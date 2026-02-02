import React, {
    memo,
    useState,
    useEffect
} from 'react'

import { Button } from 'reactstrap'

import { Table } from 'components'
import Modal from 'components/Modal/Modal'

import { useIncidentReportChangeHistory } from 'hooks/business/incident-report'

import { DateUtils as DU } from 'lib/utils/Utils'

import IncidentReportViewer from '../IncidentReportViewer/IncidentReportViewer'

import './IncidentReportChangeHistory.scss'

const { format, formats } = DU
const DATE_AND_TIME_FORMAT = formats.longDateMediumTime12

const IR_STATUS_COLORS = {
    UPDATED: '#d3dfe8',
    CREATED: '#d5f3b8',
}

function IncidentReportChangeHistory(
    { isOpen, reportId, onClose }
) {
    const [selected, setSelected] = useState(null)
    const [isViewerOpen, setViewerOpen] = useState(false)

    const {
        state: {
            isFetching,
            dataSource,
        },
        fetch
    } = useIncidentReportChangeHistory(reportId)

    const onOpenViewer = (o) => {
        setSelected(o)
        setViewerOpen(true)
    }

    const onCloseViewer = () => setViewerOpen(false)

    useEffect(() => { fetch() }, [fetch])

    return (
        <>
            <Modal
                isOpen={isOpen}
                onClose={onClose}
                hasCloseBtn={false}
                title="Change History"
                className="IncidentReportViewer"
                renderFooter={() => (
                    <Button
                        color="success"
                        onClick={onClose}
                    >
                        Close
                    </Button>
                )}
            >
                <Table
                    hasPagination
                    keyField="id"
                    isLoading={isFetching}
                    className="IncidentReportChangeHistory"
                    containerClass="IncidentReportHistoryContainer"
                    data={dataSource.data}
                    pagination={dataSource.pagination}
                    columns={[
                        {
                            dataField: 'date',
                            text: 'Date',
                            align: 'right',
                            headerAlign: 'right',
                            formatter: v => `${format(v, DATE_AND_TIME_FORMAT)}`
                        },
                        {
                            dataField: 'status',
                            text: 'Status'
                        },
                        {
                            dataField: 'author',
                            text: 'Author',
                            formatter: (v, row) => (
                                <>
                                    <div>{v}</div>
                                    <div>{row.authorRole}</div>
                                </>
                            )
                        },
                        {
                            dataField: '@actions',
                            text: 'Updates',
                            formatter: (v, row) => row.isArchived ? (
                                <Button
                                    color="link"
                                    id={row.reportId}
                                    onClick={() => onOpenViewer(row)}
                                    className="IncidentReport-ViewBtn"
                                >
                                    View Details
                                </Button>
                            ) : null
                        }
                    ]}
                    columnsMobile={['modifiedDate']}
                    onRefresh={fetch}
                />
            </Modal>
            {isViewerOpen && (
                <IncidentReportViewer
                    isOpen
                    reportId={selected.reportId}
                    onClose={onCloseViewer}
                />
            )}
        </>
    )
}

export default memo(IncidentReportChangeHistory)