import React, { useEffect } from 'react'

import cn from 'classnames'

import { connect } from 'react-redux'
import { useHistory } from 'react-router-dom'

import { useQueryParams } from 'hooks/common'
import { useLabOrdersPrimaryFilter } from 'hooks/business/labs'

import { PrimaryFilter } from 'components'

import { isInteger } from 'lib/utils/Utils'

import './LabOrderPrimaryFilter.scss'

export const NAME = 'LAB_ORDER_PRIMARY_FILTER'

function mapStateToProps(state) {
    return { fields: state.lab.research.order.list.dataSource.filter }
}

function LabOrderPrimaryFilter(
    { fields: { organizationId, communityIds }, onHandledQueryParams, className }
) {
    const history = useHistory()

    const params = useQueryParams()

    const {
        changeField,
        communities,
        organizations
    } = useLabOrdersPrimaryFilter({
        getInitialData: data => {
            const organizationId = parseInt(params.organizationId)

            return ({
                ...data, ...isInteger(organizationId) && ({
                    organizationId
                })
            })
        }
    })

    useEffect(() => {
        const organizationId = parseInt(params.organizationId)

        if (isInteger(organizationId)) {
            history.replace()
            onHandledQueryParams({ organizationId })
        }
    }, [history, onHandledQueryParams, params.organizationId])

    return (
        <PrimaryFilter
            communities={communities}
            organizations={organizations}
            onChangeField={changeField}
            data={{ organizationId, communityIds }}
            className={cn('LabOrderPrimaryFilter', className)}
            getOrganizationFieldOption={({ id, label, areLabsEnabled }) => ({
                value: id,
                text: label,
                ...!areLabsEnabled && {
                    isDisabled: true,
                    tooltip: 'The Labs feature is turned off for this organization.'
                }
            })}
        />
    )
}

export default connect(mapStateToProps)(LabOrderPrimaryFilter)