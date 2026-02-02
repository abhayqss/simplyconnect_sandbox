import React, {
    memo,
    useState
} from 'react'

import {
    useEmergencyContactsQuery
} from 'hooks/business/Prospects'

import {
    ErrorViewer,
    DataLoadable
} from 'components'

import {
    EmergencyContactList as List
} from 'components/business/common'

import { SERVER_ERROR_CODES } from 'lib/Constants'

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function EmergencyContactList({ prospectId }) {
    const [error, setError] = useState(null)

    const {
        data = [],
        isFetching,
    } = useEmergencyContactsQuery({ prospectId }, {
        staleTime: 0,
        onError: setError
    })

    return (
        <div className="HouseholdMemberListContainer">
            <DataLoadable
                data={data}
                isLoading={isFetching}
                noDataText="No emergency contacts"
            >
                {data => <List data={data} />}
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

export default memo(EmergencyContactList)