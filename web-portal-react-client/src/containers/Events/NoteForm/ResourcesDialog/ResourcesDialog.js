import React, { memo, useEffect } from 'react'

import { compose, bindActionCreators } from 'redux'
import { connect } from 'react-redux'

import { first } from 'underscore'

import { Button } from 'reactstrap'

import Modal from 'components/Modal/Modal'
import Table from 'components/Table/Table'

import resourceNameListActions from 'redux/client/servicePlan/resource-name/list/resourceNameListActions'

import { isInteger } from 'lib/utils/Utils'

import './ResourcesDialog.scss'

function mapStateToProps(state) {
    return {
        state: state.client.servicePlan.resourceName.list
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(resourceNameListActions, dispatch)
    }
}

function ResourcesDialog({ state, actions, isOpen, clientId, onClose, onSelect }) {
    const { isFetching, dataSource: { data } } = state

    function setDefaultValue() {
        if (data.length === 1) {
            onSelect(first(data))
        }
    }

    useEffect(() => {
        if (isInteger(clientId)) {
            actions.load(clientId)
        }
    }, [actions, clientId])

    useEffect(setDefaultValue, [data, onSelect])

    return (
        isOpen ? (
            <Modal
                isOpen={isOpen}
                hasCloseBtn={false}
                onClose={onClose}
                title="Select a Resource"
                className='ResourceNamesDialog'
                renderFooter={() => (
                    <Button
                        outline
                        color="success"
                        onClick={onClose}
                    >
                        Cancel
                </Button>
                )}
            >
                <Table
                    data={data}
                    isLoading={isFetching}
                    className='ResourceNameList'
                    containerClass='ResourceNameListContainer'
                    noDataText="There are no resources/providers in the service plan that you selected."
                    columns={[
                        {
                            dataField: 'resourceName',
                            text: 'Resource Name',
                        },
                        {
                            dataField: 'providerName',
                            text: 'Provider Name',
                        },
                        {
                            text: '',
                            align: 'right',
                            headerAlign: 'right',
                            formatter: (v, row) => (
                                <Button
                                    color="success"
                                    onClick={() => onSelect(row)}
                                    className="ResourceNameList-SelectBtn"
                                >
                                    Select
                                </Button>
                            )
                        }
                    ]}
                />
            </Modal>
        ) : null
    )
}

export default compose(
    memo,
    connect(mapStateToProps, mapDispatchToProps)
)(ResourcesDialog)
