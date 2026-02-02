import React from 'react'

import {
    map,
    omit,
    reject,
    isEmpty,
} from 'underscore'

import {
    ListGroup as List,
    ListGroupItem as ListItem
} from 'reactstrap'

import {
    Detail as BaseDetail
} from 'components/business/common'

import './BillingDetails.scss'

function Detail({ children, ...props }) {
    return (
        <BaseDetail
            {...props}
            className="BillingDetail"
            titleClassName="BillingDetail-Title"
            valueClassName="BillingDetail-Value"
        >
            {children}
        </BaseDetail>
    )
}

function SubDetail({ children, ...props }) {
    return (
        <BaseDetail
            {...props}
            className="BillingItemDetail"
            titleClassName="BillingItemDetail-Title"
            valueClassName="BillingItemDetail-Value"
        >
            {children}
        </BaseDetail>
    )
}

export default function BillingDetails({ data = {} }) {
    return (
        <>
            <Detail title="MEDICARE #">
                {data.medicareNumber}
            </Detail>

            <Detail title="MEDICAID #">
                {data.medicaidNumber}
            </Detail>

            <List className="ClientBilling">
                {map(reject(data.items, o => isEmpty(omit(o, v => !v))), o => (
                    <ListItem
                        key={`${o.groupNumber}.${o.policyNumber}`}
                        className="ClientBilling-Item ClientEmergencyContact"
                    >
                        <SubDetail title="INSURANCE">
                            {o.insurance}
                        </SubDetail>

                        <SubDetail title="PLAN">
                            {o.plan}
                        </SubDetail>

                        <SubDetail title="GROUP #">
                            {o.groupNumber}
                        </SubDetail>

                        <SubDetail title="POLICY #">
                            {o.policyNumber}
                        </SubDetail>
                    </ListItem>
                ))}
            </List>
        </>
    )
}