import React, {
    useEffect,
    useCallback
} from 'react'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import {
    ErrorViewer,
    DataLoadable
} from 'components'

import { AllergyDetails as Content } from 'components/business/common'

import actions from 'redux/client/allergy/details/clientAllergyDetailsActions'

import { SERVER_ERROR_CODES } from 'lib/Constants'

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    return { state: state.client.allergy.details }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch)
    }
}

function AllergyDetails({ clientId, allergyId, state, actions }) {
    const { data, error, isFetching } = state

    const { clear, load } = actions

    const fetch = useCallback(() => {
        load(allergyId, { clientId })
    }, [ clientId, allergyId, load ])

    useEffect(() => {
        fetch()

        return clear
    }, [ fetch, clear ])

    return (
        <>
            <DataLoadable
                data={data}
                isLoading={isFetching}
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
        </>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(AllergyDetails)