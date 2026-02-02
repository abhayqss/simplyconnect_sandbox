import React, {
    useMemo
} from 'react'

import cn from 'classnames'

import { Link } from 'react-router-dom'

import {
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { Table } from 'components'

import Avatar from 'containers/Avatar/Avatar'

import { DateUtils as DU } from 'lib/utils/Utils'
import { path } from 'lib/utils/ContextUtils'

import { ReactComponent as Pencil } from 'images/pencil.svg'

import './ProspectList.scss'

const { format, formats } = DU

const DATE_FORMAT = formats.americanMediumDate

export default function ProspectList(
    {
        data,
        // selected,
        pagination,
        isFetching,
        noDataText,
        // isSelectable,
        // areAllSelected,
        organizationId,

        onSort,
        onEdit,
        // onSelect,
        onRefresh,
        // onSelectAll
    }
) {
    const columns = useMemo(() => (
        [{
            dataField: 'fullName',
            text: 'Name',
            sort: true,
            onSort,
            formatter: (v, row, index, formatExtraData, isMobile) => {
                return (
                    <div className="d-flex align-items-center">
                        <Avatar
                            name={v}
                            id={row.avatarId}
                        />
                        {row.canView ? (
                            <>
                                <Link
                                    id={`${isMobile ? 'm-' : ''}prospect-${row.id}`}
                                    to={path(`/prospects/${row.id}/dashboard`)}
                                    className={cn('ProspectList-ProspectName', row.avatarDataUrl && 'margin-left-10')}>
                                    {v}
                                </Link>
                                <Tooltip
                                    placement="top"
                                    target={`${isMobile ? 'm-' : ''}prospect-${row.id}`}
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
                                id={`${isMobile ? 'm-' : ''}prospect-${row.id}`}
                                className='ProspectList-ProspectName'>
                                {v}
                            </span>
                        )}
                    </div>
                )
            }
        }, {
            dataField: 'gender',
            text: 'Gender',
            sort: true,
            onSort,
        }, {
            dataField: 'birthDate',
            text: 'Date of Birth',
            sort: true,
            onSort
        }, {
            dataField: 'documentSent',
            text: 'Documents Sent',
            sort: true,
            onSort
        }, {
            dataField: 'documentReceived',
            text: 'Documents Received',
            sort: true,
            onSort
        }, {
            dataField: 'communityName',
            text: 'Community',
            sort: true,
            onSort,
            formatter: (v, row) => (
                <div
                    title={v}
                    className="text-trim"
                >
                    {v}
                </div>
            )
        }, {
            dataField: 'createdDate',
            text: 'Created',
            sort: true,
            onSort,
            formatter: v => v && format(v, DATE_FORMAT)
        }, {
            dataField: '@actions',
            headerStyle: {
                width: '80px'
            },
            align: 'right',
            formatter: (v, row) => (
                <>
                    {row.canEdit && row.isActive && (
                        <>
                            <Pencil
                                id={`prospect-edit_${row.id}`}
                                className="ProspectList-EditIcon"
                                onClick={() => onEdit(row)}
                            />
                            <Tooltip
                                placement="top"
                                trigger="click hover"
                                target={`prospect-edit_${row.id}`}
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
                                Edit prospect details
                            </Tooltip>
                        </>
                    )}
                </>
            )
        }]
    ), [onSort, onEdit, organizationId])

    return (
        <Table
            data={data}
            title="Prospects"
            columns={columns}
            hasHover
            keyField="id"
            hasPagination
            onRefresh={onRefresh}
            isLoading={isFetching}
            hasCaption={false}
            pagination={pagination}
            noDataText={noDataText}
            getRowStyle={row => ({
                ...!row.isActive && { opacity: '0.5' }
            })}
            columnsMobile={['fullName', 'communityName']}
            // selectedRows={{
            //     mode: 'checkbox',
            //     hideSelectColumn: !isSelectable,
            //     selected: map(selected, c => c.id),
            //     onSelect: onSelect,
            //     onSelectAll: onSelectAll,
            //     nonSelectable: chain(data).where({ isActive: false }).pluck('id').value(),
            //     selectionRenderer: ({ checked, disabled }) => isSelectable && (
            //         <CheckboxField value={checked} isDisabled={disabled} />
            //     ),
            //     selectionHeaderRenderer: () => isSelectable && (
            //         <CheckboxField
            //             value={!isFetching && isNotEmpty(data) && areAllSelected}
            //         />
            //     )
            // }}
        />
    )
}