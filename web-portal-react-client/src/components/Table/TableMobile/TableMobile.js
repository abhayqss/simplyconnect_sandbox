import React from 'react'

import cn from 'classnames'

import {
    each,
    first,
    last,
    isNumber
} from 'underscore'

import BootstrapTable from 'react-bootstrap-table-next'

import { Detail as BaseDetail } from 'components/business/common'

import { ReactComponent as BottomChevron } from 'images/chevron-bottom.svg'

import './TableMobile.scss'

const ACTIONS_KEY = '@actions'

function Detail({
    data,
    text,
    classes,
    rowIndex,
    dataField,
    formatter = v => v,
}) {
    let value = data[dataField]
    let isValid = value != null || isNumber(value)

    return isValid ? (
        <BaseDetail
            className={cn('TableMobile-Detail', classes)}
            titleClassName="TableMobile-Detail-Title"
            valueClassName="TableMobile-Detail-Value"
            title={text}
        >
            {formatter(value, data, rowIndex)}
        </BaseDetail>
    ) : null
}

function Actions({ data, columns }) {
    let actionsColumn = columns.find(o => o.dataField === ACTIONS_KEY)
    let value = actionsColumn?.formatter(undefined, data)

    return value ? (
        <div className="TableMobile-Actions">
            {value}
        </div>
    ) : null
}

const getDetailsRow = columns => {
    return ({
        onlyOneExpanding: true,
        showExpandColumn: true,
        expandColumnPosition: 'right',
        expandHeaderColumnRenderer: () => null,
        expandColumnRenderer: ({ expanded }) => (
            <BottomChevron className={cn('TableMobile-Expander', {
                'Opened': expanded
            })} />
        ),
        renderer: row => (
            <div className="TableMobile-Details">
                <div className="TableMobile-Details-List">
                    {columns.map((column, index) => (
                        <Detail
                            data={row}
                            rowIndex={index}
                            key={column.dataField || index}
                            {...column}
                        />
                    ))}
                </div>

                <Actions
                    data={row}
                    columns={columns}
                />
            </div>
        )
    })

}
function TableMobile({
    classes,
    expandRow,
    columns = [],
    columnsMobile = [],
    ...props
}) {
    let [reducedColumns, details] = columns.reduce((columnGroups, column) => {
        if (columnsMobile.includes(column.dataField)) {
            first(columnGroups).push({
                ...column,
                formatter: column.formatter ? (...args) => column.formatter(...args, true) : undefined
            })
        } else {
            last(columnGroups).push(column)
        }

        return columnGroups
    }, [[], []])

    return reducedColumns.length > 0 ? (
        <BootstrapTable
            expandRow={getDetailsRow(details)}
            columns={reducedColumns}
            classes={cn('TableMobile', classes)}
            {...props}
        />
    ) : null
}

export default TableMobile
