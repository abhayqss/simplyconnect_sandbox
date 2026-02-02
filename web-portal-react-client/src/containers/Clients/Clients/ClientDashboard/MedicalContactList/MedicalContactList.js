import React, {
    memo,
    useEffect,
    useCallback
} from 'react'

import { useMedicalContactList } from 'hooks/business/client'

import {
    ErrorViewer,
    DataLoadable
} from 'components'

import {
    MedicalContactList as List
} from 'components/business/common'

import { SERVER_ERROR_CODES } from 'lib/Constants'

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function MedicalContactList({ clientId }) {
    const { state, fetch, clearError } = useMedicalContactList({ clientId })

    const {
        error,
        isFetching,
        dataSource: {
            data,
        }
    } = state

    const onFetch = useCallback(fetch, [])

    useEffect(() => { onFetch() }, [onFetch])

    return (
        <div className="MedicalContactListContainer">
            <DataLoadable
                data={data}
                isLoading={isFetching}
                noDataText="No medical contacts"
            >
                {data => <List data={data} />}
            </DataLoadable>
            {error && !isIgnoredError(error) && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={clearError}
                />
            )}
        </div>
    )
}

export default memo(MedicalContactList)