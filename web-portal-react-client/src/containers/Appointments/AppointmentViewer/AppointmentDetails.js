import React, {
    memo,
    useMemo,
    useState
} from 'react'

import {
    ErrorViewer,
    DataLoadable
} from 'components'

import {
    AppointmentDetails as BaseDetails
} from 'components/business/Clients'

import {
    useAppointmentQuery
} from 'hooks/business/appointments'

import {
    isNotEmpty
} from 'lib/utils/Utils'

import {
    format,
    formats,
    getStartOfDayTime
} from 'lib/utils/DateUtils'

import { SERVER_ERROR_CODES } from 'lib/Constants'

function formatTime(date) {
    return format(date, formats.time2)
}

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function AppointmentDetails({ id }) {
    const [error, setError] = useState(null)

    const {
        data,
        isFetching
    } = useAppointmentQuery({ id }, {
        onError: setError,
        enabled: isNotEmpty(id),
        staleTime: 0
    })

    const preparedData = useMemo(() => (data && {
        ...data,
        date: getStartOfDayTime(data.dateFrom),
        time: `${formatTime(data.dateFrom)} - ${formatTime(data.dateTo)}`
    }), [data])

    return (
        <div className="Details AppointmentDetailsContainer">
            <DataLoadable
                data={preparedData}
                isLoading={isFetching}
                noDataText="No appointment info"
            >
                {data => (
                    <BaseDetails data={data}/>
                )}
            </DataLoadable>

            {error && !isIgnoredError(error) && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </div>
    )
}

export default memo(AppointmentDetails)