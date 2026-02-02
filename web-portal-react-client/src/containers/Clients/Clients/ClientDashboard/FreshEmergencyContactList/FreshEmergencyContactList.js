import React, {
    useMemo,
    useEffect,
    useCallback, useState
} from 'react'
import service from 'services/ClientService'
import { isEmpty } from 'underscore'

import { 
    ErrorViewer,
    DataLoadable
} from 'components'

import { FreshEmergencyContactList as List } from 'components/business/common'

import * as actions from 'redux/client/emergency/contact/list/clientEmergencyContactListActions'

import { PAGINATION, SERVER_ERROR_CODES } from 'lib/Constants'

const { MAX_SIZE } = PAGINATION

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}
const FreshEmergencyContactList =({ clientId }) => {
  const [isFetching, setIsFetching] = useState(false)
  const [error, setError] = useState(null)
  const [data, setData] = useState(null)
    const getEmergency = (id) =>{
        service.findNewContacts(id).then((res) =>{
            if(res.success) {
                setData(res.data)
                setIsFetching(false)
            }else {
                setError(res.data)
            }
        })
    }
    useEffect(() => {
      setIsFetching(true)
      getEmergency(clientId)
    }, [clientId])
    return (
        <div className="EmergencyContactListContainer">
            <DataLoadable
                data={data}
                isLoading={isFetching}
                // isNoData={isEmpty(preparedData)}
                noDataText="No emergency contacts"
            >
                {data => <List data={data}/>}
            </DataLoadable>
            {error && !isIgnoredError(error) && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={actions.clearError}
                />
            )}
        </div>
    )
}

export default FreshEmergencyContactList