import React, {
    memo,
    useMemo,
} from 'react'

import { Table } from 'components'

import {
    EditButton,
    DeleteButton
} from 'components/buttons'

import { ReactComponent as Indicatior } from 'images/dot.svg'

import './OrganizationCategoryList.scss'

function OrganizationCategoryList(
    {
        data,
        pagination,
        isFetching,
        onSort,
        onEdit,
        onDelete,
        onRefresh
    }
) {
    const columns = useMemo(() => (
        [
            {
                dataField: 'name',
                text: 'Category',
                sort: true,
                onSort,
                formatter: (v, row,) => {
                    return (
                        <div
                            className="OrganizationCategoryList-Category"
                            style={{ borderColor: row.color || '#000000' }}
                        >
                            <Indicatior
                                style={{ fill: row.color || '#000000' }}
                                className="OrganizationCategoryList-CategoryIndicator"
                            />
                            <div className="OrganizationCategoryList-CategoryName">
                                {v}
                            </div>
                        </div>
                    )
                }
            },
            {
                dataField: '@actions',
                text: '',
                headerStyle: {
                    width: '120px',
                },
                formatter: (v, row) => {
                    return (
                        <div className="OrganizationCategoryList-Actions">
                            {row.canEdit && (
                                <EditButton
                                    id={`edit-category-${row.id}-btn`}
                                    onClick={() => onEdit(row)}
                                    tipText="Edit category"
                                    className="OrganizationCategoryList-Action"
                                />
                            )}
                            {row.canDelete && (
                                <DeleteButton
                                    id={`delete-category-${row.id}-btn`}
                                    onClick={() => onDelete(row)}
                                    tipText="Delete category"
                                    className="OrganizationCategoryList-Action"
                                />
                            )}
                        </div>
                    )
                }
            }
        ]
    ) , [onSort, onEdit, onDelete])

    return (
        <Table
            hasHover
            hasOptions
            hasPagination
            keyField="id"
            hasCaption={false}
            title="Categories"
            noDataText="No Categories"
            isLoading={isFetching}
            className='OrganizationCategoryList'
            containerClass='OrganizationCategoryListContainer'
            data={data}
            pagination={pagination}
            columns={columns}
            columnsMobile={['name']}
            onRefresh={onRefresh}
        />
    )
}

export default memo(OrganizationCategoryList)