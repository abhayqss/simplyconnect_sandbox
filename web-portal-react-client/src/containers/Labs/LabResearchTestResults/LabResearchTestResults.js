import React, { useEffect } from 'react'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { useListDataFetch } from 'hooks/common/redux'

import { Table, ErrorViewer } from 'components'

import actions from 'redux/lab/research/order/test/result/list/labResearchOrderTestResultListActions'

import { SERVER_ERROR_CODES } from 'lib/Constants'

import './LabResearchTestResults.scss'

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    return { state: state.lab.research.order.test.result.list }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch)
    }
}

function LabResearchTestResults({ state, actions, orderId }) {
    const {
        error,
        isFetching,
        dataSource: ds
    } = state

    const { fetch } = useListDataFetch(
        state, actions, { orderId }
    )

    useEffect(() => {
        fetch()
        return actions.clear
    }, [ actions ])

    return (
        <div className="ReferralRequests">
            <Table
                hasHover
                hasPagination
                keyField='id'
                title=""
                noDataText="No results."
                isLoading={isFetching}
                className='LabResearchTestResultList'
                containerClass='LabResearchTestResultListContainer'
                data={ds.data}
                pagination={ds.pagination}
                onRefresh={fetch}
                columns={[
                    {
                        dataField: 'name',
                        text: 'Name',
                    },
                    {
                        dataField: 'value',
                        text: 'Value',
                    },
                    {
                        dataField: 'units',
                        text: 'Units',
                    },
                    {
                        dataField: 'refRange',
                        text: 'References Range',
                    },
                    {
                        dataField: 'abnormalFlags',
                        text: 'Abnormal Flags',
                    }
                ]}
                columnsMobile={['name', 'value']}
            />
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

export default connect(mapStateToProps, mapDispatchToProps)(LabResearchTestResults)