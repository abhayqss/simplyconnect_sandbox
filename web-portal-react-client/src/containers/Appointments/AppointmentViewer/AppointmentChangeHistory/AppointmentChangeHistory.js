import React, {
    memo,
    useState,
    useCallback
} from 'react'

import { Button } from 'reactstrap'

import { Table } from 'components'

import AppointmentViewer from '../AppointmentViewer'

import { useAppointmentHistoryQuery } from 'hooks/business/appointments'

import { DateUtils as DU, isNotEmpty } from 'lib/utils/Utils'

import './AppointmentChangeHistory.scss'

const { format, formats } = DU
const DATE_AND_TIME_FORMAT = formats.longDateMediumTime12

const APPOINTMENT_STATUS_COLORS = {
    UPDATED: '#d3dfe8',
    CREATED: '#d5f3b8',
}

function AppointmentChangeHistory(
    {
        readOnly,
        appointmentId,
        onDuplicateAppointment
    }
) {
    const {
        refresh,
        isFetching,
        pagination,
        data: { data } = {}
    } = useAppointmentHistoryQuery({ appointmentId }, {
        enabled: isNotEmpty(appointmentId)
    })

    const [selectedId, setSelectedId] = useState(null)
    const [isViewerOpen, setIsViewerOpen] = useState()

    const onOpenViewer = ({ target }) => {
        setIsViewerOpen(true)
        setSelectedId(+target.id)
    }

    return (
        <>
            {data &&
                <Table
                    hasPagination
                    keyField="id"
                    isLoading={isFetching}
                    className="AppointmentChangeHistory"
                    containerClass="AppointmentChangeHistoryContainer"
                    data={data}
                    pagination={pagination}
                    columns={[
                        {
                            dataField: 'modifiedDate',
                            text: 'Date',
                            align: 'right',
                            headerAlign: 'right',
                            formatter: v => `${format(v, DATE_AND_TIME_FORMAT)}`
                        },
                        {
                            dataField: 'status',
                            text: 'Status',
                            formatter: (v, row) => (
                                <span
                                    className="AppointmentChange-Status"
                                    style={{ backgroundColor: APPOINTMENT_STATUS_COLORS[v] }}>
                                    {row.status}
                                </span>
                            )
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
                            formatter: (v, row) => row.archived ? (
                                <Button
                                    color="link"
                                    id={row.id}
                                    onClick={onOpenViewer}
                                    className="AppointmentChange-ViewBtn"
                                >
                                    View Details
                                </Button>
                            ) : null
                        }
                    ]}
                    columnsMobile={['modifiedDate']}
                    renderCaption={title => {
                        return (
                            <div className='AppointmentChangeHistory-Caption'>
                                <div className='AppointmentChangeHistory-Title'>
                                    {title}
                                </div>
                            </div>
                        )
                    }}
                    onRefresh={refresh}
                />}

            <AppointmentViewer
                isOpen={isViewerOpen}
                readOnly={readOnly}
                appointmentId={selectedId}
                historyEnabled={false}
                
                onClose={useCallback(() => setIsViewerOpen(false), [])}
                onDuplicate={onDuplicateAppointment}
            />
        </>
    )
}

export default memo(AppointmentChangeHistory)
