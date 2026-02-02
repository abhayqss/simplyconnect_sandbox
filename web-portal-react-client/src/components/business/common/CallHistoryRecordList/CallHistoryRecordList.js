import React, {
    useMemo
} from 'react'

import cn from 'classnames'

import { Table } from 'components'

import {
    DateUtils as DU
} from 'lib/utils/Utils'

import './CallHistoryRecordList.scss'

const DATE_FORMAT = DU.formats.longDateMediumTime12

export default function CallHistoryRecordList(
    {
        data,
        pagination,
        isFetching,
        onSort,
        onRefresh,
        className
    }
) {
    const columns = useMemo(() =>
        [
            {
                dataField: 'name',
                text: 'Name',
                align: 'left',
                headerAlign: 'left'
            },
            {
                dataField: 'roleTitle',
                text: 'Role',
                align: 'left',
                headerAlign: 'left'
            },
            {
                dataField: 'typeTitle',
                text: 'Type',
                align: 'left',
                headerAlign: 'left'
            },
            {
                dataField: 'duration',
                text: 'Duration',
                headerAlign: 'left',
                align: 'left',
                formatter: v => DU.formatDuration(v)
            },
            {
                dataField: 'date',
                text: 'Date',
                align: 'right',
                headerAlign: 'right',
                sort: true,
                onSort,
                formatter: v => DU.format(v, DATE_FORMAT)
            }
        ], [onSort])

    return (
        <Table
            hasHover
            hasOptions
            hasPagination
            hasCaption={false}
            keyField="id"
            title="Call History"
            noDataText="No calls."
            isLoading={isFetching}
            className={cn('CallHistoryRecordList', className)}
            containerClass='CallHistoryRecordListContainer'
            data={data}
            pagination={pagination}
            columns={columns}
            columnsMobile={['name', 'date']}
            onRefresh={onRefresh}
        />
    )
}