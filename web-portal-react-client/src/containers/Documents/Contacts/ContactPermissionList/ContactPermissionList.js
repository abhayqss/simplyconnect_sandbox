import React, {
    useMemo,
    useState
} from 'react'

import cn from 'classnames'

import {
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { pluck } from 'underscore'

import {
    Table,
    MultiSelect,
    SearchField
} from 'components'
import { IconButton } from 'components/buttons'
import { CheckboxField } from 'components/Form'

import { ContactPermissionsInfo } from 'components/business/Documents'

import {
    useSelectOptions
} from 'hooks/common'
import {
    useDocumentFolderPermissionLevelsQuery
} from 'hooks/business/directory/query'

import { ReactComponent as Info } from 'images/info.svg'
import { ReactComponent as Delete } from 'images/delete.svg'

import './ContactPermissionList.scss'

function ContactPermissionList({
    data,
    search,
    selected,
    className,

    isLoading,
    pagination,

    hasSearch,
    hasCheckboxes,
    hasRemoveButtons,

    onDelete,
    onRefresh,
    onSelect,
    onSelectAll,
    onClearSearch,
    onChangeSearch,
    onChangePermission
}) {
    const {
        data: permissionLevels = []
    } = useDocumentFolderPermissionLevelsQuery()

    const permissionLevelOptions = useSelectOptions(permissionLevels)

    const columns = useMemo(() => {
        const columns = [
            {
                dataField: 'contactFullName',
                text: 'Name',
                classes: 'text-break'
            },
            {
                dataField: 'contactLogin',
                text: 'Login',
                classes: 'text-break'
            },
            {
                dataField: 'contactSystemRoleTitle',
                text: 'Role'
            },
            {
                dataField: 'permissionLevelId',
                text: 'Permission Level',
                formatter: (v, row, i, extraData) => {
                    function changePermission(value) {
                        extraData.onChangePermission(row.contactId, value)
                    }

                    return (
                        <MultiSelect
                            value={v}
                            hasValueTooltip
                            className="ContactPermissionList-MultiSelect"
                            isDisabled={!row.canEdit}
                            options={extraData.options}
                            onChange={changePermission}
                        />
                    )
                },
                formatExtraData: {
                    onChangePermission,
                    options: permissionLevelOptions,
                },
                headerFormatter: () => {
                    return (
                        <span className="Table-ColumnTitle">
                            <span>Permission Level</span>

                            <Info
                                id={'contact-permissions-info'}
                                className="ContactPermissionList-InfoIcon"
                            />

                            <Tooltip
                                trigger="hover click"
                                boundariesElement={document.body}
                                className="ContactPermissionList-PermissionsInfoTooltip"
                                target="contact-permissions-info"
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
                                <ContactPermissionsInfo className="ContactPermissionList-PermissionsInfo" />
                            </Tooltip>
                        </span>
                    )
                }
            },
        ]

        if (hasRemoveButtons) {
            columns.push({
                dataField: '@actions',
                text: '',
                headerStyle: {
                    width: '60px'
                },
                formatter: (v, row, i, { onDelete }) => {
                    return (
                        <div className="ContactPermissionList-Actions">
                            {row.canDelete && (
                                <IconButton
                                    size={36}
                                    Icon={Delete}
                                    name={`contacts_${row.id}__delete_action`}
                                    tipText="Remove access to folder"
                                    onClick={() => onDelete(row)}
                                    className="ContactPermissionList-Action DeleteActionBtn"
                                />
                            )}
                        </div>
                    )
                },
                formatExtraData: {
                    onDelete
                }
            })
        }

        return columns
    }, [
        onDelete,
        hasRemoveButtons,
        onChangePermission,
        permissionLevelOptions
    ])

    return (
        <div className={cn('ContactPermissionList', className)}>
            {hasSearch && (
                <SearchField
                    name="name"
                    className="ContactPermissionList-Field"
                    placeholder="Search by name"
                    value={search}
                    onChange={onChangeSearch}
                    onClear={onClearSearch}
                />
            )}

            <Table
                hasHover
                hasOptions
                hasPagination
                keyField="contactId"
                hasCaption={false}
                title="Contacts"
                noDataText="No results"
                isLoading={isLoading}
                containerClass="ContactPermissionList"
                data={data}
                pagination={pagination}
                columns={columns}
                selectedRows={hasCheckboxes && {
                    mode: 'checkbox',
                    clickToSelect: false,
                    selected: pluck(selected, 'contactId'),
                    onSelect,
                    onSelectAll,
                    style: { backgroundColor: '#edf4f5' },
                    selectionRenderer: ({ checked }) => (
                        <CheckboxField value={checked} />
                    ),
                    selectionHeaderRenderer: () => (
                        <CheckboxField
                            value={(
                                selected.length > 0
                                && selected.length === data.length
                            )}
                        />
                    )
                }}
                columnsMobile={['contactFullName']}
                onRefresh={onRefresh}
            />
        </div>
    )
}

export default ContactPermissionList
