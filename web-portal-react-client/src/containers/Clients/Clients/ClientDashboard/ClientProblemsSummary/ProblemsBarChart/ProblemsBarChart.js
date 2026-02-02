import React, { memo, useCallback } from 'react'

import cn from 'classnames'


import { ProblemsBarChart as BarChart } from 'components/charts'


function ProblemsBarChart({ data, className, onPickBar }) {
    data = data.map(o => ({
        name: o.title,
        value: o.value,
    }))

    const onBarClick = useCallback(data => {
        onPickBar(data.name.toUpperCase(), data.value)
    }, [onPickBar])

    return (
        <>
            <BarChart
                data={data}
                onBarClick={onBarClick}
                className={cn('ClientProblemsBarChart', className)}
            />
        </>
    )
}

export default memo(ProblemsBarChart)