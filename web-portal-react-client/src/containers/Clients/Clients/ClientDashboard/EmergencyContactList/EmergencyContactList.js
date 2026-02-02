import React, {
    useMemo,
    useEffect,
    useCallback
} from 'react'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { isEmpty } from 'underscore'

import { 
    ErrorViewer,
    DataLoadable
} from 'components'

import { EmergencyContactList as List } from 'components/business/common'

import * as actions from 'redux/client/emergency/contact/list/clientEmergencyContactListActions'

import { PAGINATION, SERVER_ERROR_CODES } from 'lib/Constants'

const { MAX_SIZE } = PAGINATION

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    return { state: state.client.emergency.contact.list }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch)
    }
}

function EmergencyContactList({ clientId, state, actions }) {
    const {
        error,
        isFetching,
        dataSource: { data }
    } = state

    const preparedData = useMemo(() => (
        data ? ([...data.contacts, ...data.attorneys]) : []
    ), [data])

    const { clear, load } = actions

    const fetch = useCallback(() => {
        load({ clientId, size: MAX_SIZE })
    }, [clientId, load])

    useEffect(() => {
        fetch()

        return clear
    }, [fetch, clear])

    return (
        <div className="EmergencyContactListContainer">
            <DataLoadable
                data={data}
                isLoading={isFetching}
                isNoData={isEmpty(preparedData)}
                noDataText="No POA"
            >
                {data => <List data={data.contacts}/>}
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

export default connect(mapStateToProps, mapDispatchToProps)(EmergencyContactList)