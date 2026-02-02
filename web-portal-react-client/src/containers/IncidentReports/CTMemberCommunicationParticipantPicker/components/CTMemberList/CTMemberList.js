import React, { useMemo } from 'react'

import {
    map,
    where,
    sortBy
} from 'underscore'

import { Checkable } from 'components'

import CTMember from '../CTMember/CTMember'

import './CTMemberList.scss'

export default function CTMemberList(
    {
        data = [],
        onCheck,
        onCheckAll
    }
) {
    const checkedCount = useMemo(
        () => where(data, { isChecked: true }).length,
        [data]
    )

    const filteredData = useMemo(
        () => where(data, { isDisabled: false }),
        [data]
    )

    const sortedData = sortBy(data, 'fullName')

    return (
        <>
            <Checkable
                isControlled
                data={filteredData}
                isChecked={data.length === checkedCount}
                className="CTMemberList-CheckAll"
                onCheck={onCheckAll}
            >
                All
            </Checkable>
            {map(sortedData, (m, i) => {
                return (
                    <>
                        <CTMember
                            data={m}
                            key={m.id ?? i}
                            isChecked={m.isChecked}
                            isDisabled={m.isDisabled}
                            onCheck={onCheck}
                        />
                    </>
                )
            })}
        </>
    )
}