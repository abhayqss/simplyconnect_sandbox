import React from 'react'

import {
    Detail
} from 'components/business/common'

import './MedicalContactList.scss'

export default function MedicalContactList({ data = {} }) {
    return (
        data.map(o => (
            <Detail
                title={o.role.toUpperCase()}
                className="MedicalContactDetail"
                titleClassName="MedicalContactDetail-Title"
                valueClassName="MedicalContactDetail-Value"
            >
                {o.data}
            </Detail>
        ))
    )
}