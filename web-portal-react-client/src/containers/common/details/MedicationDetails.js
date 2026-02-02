import React, {
    useState,
    useCallback
} from 'react'

import { connect } from 'react-redux'

import {
    ErrorViewer,
    DataLoadable
} from 'components'

import { MedicationDetails as Content } from 'components/business/common'

import { useMedicationQuery } from 'hooks/business/client'

import { SERVER_ERROR_CODES } from 'lib/Constants'

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function MedicationDetails({ clientId, medicationId }) {
    const [ isErrorViewerOpen, toggleErrorViewer ] = useState(false)

    const {
        data, error, isFetching
    } = useMedicationQuery({ clientId, medicationId })

    const onCloseErrorViewer = useCallback(
        () => toggleErrorViewer(false), []
    )

    return (
        <>
            <DataLoadable
                data={data}
                isLoading={isFetching}
            >
                {data => <Content data={data} />}
            </DataLoadable>

            {isErrorViewerOpen && !isIgnoredError(error) && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={onCloseErrorViewer}
                />
            )}
        </>
    )
}

export default connect()(MedicationDetails)