import React, {
    useEffect,
    useCallback
} from 'react'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { any, omit } from 'underscore'

import {
    ErrorViewer,
    DataLoadable
} from 'components'

import { BillingDetails as Content } from 'components/business/Clients'

import * as actions from 'redux/client/billing/details/clientBillingDetailsActions'

import { SERVER_ERROR_CODES } from 'lib/Constants'

import {
    isEmpty,
    isNotEmpty,
    allAreEmpty
} from 'lib/utils/Utils'

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    return { state: state.client.billing.details }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch)
    }
}

function BillingDetails({ clientId, state, actions }) {
    const { data, error, isFetching } = state

    const { clear, load } = actions

    const isNoBillInfo = allAreEmpty(
        data?.medicareNumber,
        data?.medicaidNumber
    ) && !(isNotEmpty(data?.items) && any(
        data?.items, o => isNotEmpty(
            omit(o, v => isEmpty(v))
        )
    ))

    const fetch = useCallback(() => {
        load(clientId)
    }, [clientId, load])

    useEffect(() => {
        fetch()

        return clear
    }, [fetch, clear])

    return (
        <div className="BillingDetailsContainer">
            <DataLoadable
                data={data}
                isLoading={isFetching}
                isNoData={isNoBillInfo}
                noDataText="No billing info"
            >
                {data => <Content data={data} />}
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

export default connect(mapStateToProps, mapDispatchToProps)(BillingDetails)