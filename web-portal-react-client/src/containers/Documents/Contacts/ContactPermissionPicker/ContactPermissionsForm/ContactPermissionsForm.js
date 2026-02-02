import React, {
    useState,
    useEffect
} from 'react'

import { Button } from 'reactstrap'

import { ErrorViewer } from 'components'

import { ContactPermissionList } from 'containers/Documents/Contacts'

import { useDocumentFolderContactsQuery } from 'hooks/business/documents'
import {
    useDocumentFolderPermissionLevelsQuery
} from 'hooks/business/directory/query'

import PermissionsDataSource from 'entities/PermissionsDataSource'

import { ifElse } from 'lib/utils/Utils'

import './ContactPermissionsForm.scss'

const { List } = require('immutable')

function toggleContact(contact, shouldAdd) {
    return ifElse(
        () => shouldAdd,
        (list) => list.push(contact),
        (list) => list.filter(o => o.contactId !== contact.contactId)
    )
}

function toggleAllContacts(contacts, shouldAdd) {
    return ifElse(
        () => shouldAdd,
        (list) => List(contacts),
        (list) => list.clear()
    )
}

function ContactPermissionsForm({
    folderId,
    communityId,
    parentFolderId,
    selectedPermissions,

    onSubmit,
    onCancel
}) {
    const [error, setError] = useState()
    const [selected, setSelected] = useState(List())
    const [dataSource, setDataSource] = useState(() => PermissionsDataSource())

    const {
        data = [],
        isFetching
    } = useDocumentFolderContactsQuery({
        folderId,
        communityId,
        parentFolderId
    }, {
        onError: setError
    })

    const {
        data: permissionLevels = []
    } = useDocumentFolderPermissionLevelsQuery()

    const viewerLevelId = permissionLevels.find(o => o.title.toLowerCase().includes('viewer'))?.id

    function changeDataSourcePermissions(contactId, value) {
        const index = dataSource.data.findIndex(o => o.contactId === contactId)

        setDataSource(dataSource => {
            return dataSource.setIn(['data', index, 'permissionLevelId'], value)
        })
    }

    function changeSelectedPermissions(contactId, value) {
        const index = selected.findIndex(o => o.contactId === contactId)

        if (~index) {
            setSelected(selected.setIn([index, 'permissionLevelId'], value))
        }
    }

    function changeContactPermission(contactId, value) {
        changeSelectedPermissions(contactId, value)
        changeDataSourcePermissions(contactId, value)
    }

    function setInitialData() {
        setDataSource(dataSource => {
            const list = List(
                data
                    .filter(o => !selectedPermissions.find((permission) => o.contactId === permission.contactId))
                    .map((o) => ({
                        ...o,
                        canEdit: true,
                        canDelete: true,
                        permissionLevelId: viewerLevelId
                    }))
            )

            return dataSource
                .set('data', List(list))
                .setIn(['pagination', 'totalCount'], list.size)
        })
    }

    function selectContact(contact, shouldAdd) {
        setSelected(toggleContact(contact, shouldAdd))
    }

    function selectAllContacts(shouldAdd) {
        setSelected(toggleAllContacts(dataSource.data, shouldAdd))
    }

    function setNameFilter(value) {
        setDataSource(dataSource => {
            return dataSource.setIn(['filter', 'name'], value)
        })
    }

    function changeSearch(_, value) {
        setNameFilter(value)
    }

    function clearSearch() {
        setNameFilter('')
    }

    function refresh(page) {
        setDataSource(dataSource => (
            dataSource.setIn(['pagination', 'page'], page)
        ))
    }

    function cancel() {
        onCancel(!!selected.size)
    }

    function save() {
        onSubmit(selected)
    }

    useEffect(setInitialData, [data, viewerLevelId, selectedPermissions])

    return (
        <>
            <div className="ContactPermissionsForm">
                <ContactPermissionList
                    hasSearch
                    hasCheckboxes
                    data={dataSource.getData().toJS()}
                    pagination={dataSource.pagination}
                    search={dataSource.filter.name}
                    isLoading={isFetching}
                    selected={selected.toJS()}
                    onSelect={selectContact}
                    onSelectAll={selectAllContacts}
                    onChangeSearch={changeSearch}
                    onClearSearch={clearSearch}
                    onRefresh={refresh}
                    className="ContactPermissionsForm-ContactPermissionsList"
                    onChangePermission={changeContactPermission}
                />

                <div className="ContactPermissionsForm-Buttons">
                    <Button
                        outline
                        color="success"
                        onClick={cancel}
                    >
                        Close
                    </Button>

                    <Button
                        color="success"
                        onClick={save}
                    >
                        Save
                    </Button>
                </div>
            </div>

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </>
    )
}

export default ContactPermissionsForm
