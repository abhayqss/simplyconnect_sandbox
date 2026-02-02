import React, {
    memo,
    useMemo,
    useCallback
} from 'react'

import cn from 'classnames'

import { map } from 'underscore'

import { MedicationBarChart } from 'components/charts'

import './ClientMedicationBarChart.scss'

function ClientMedicationBarChart({ data, className, onPickBar }) {
    const chartData = useMemo(() => map(data, o => ({
        name: o.title, value: o.value,
    })), [data])

    const onBarClick = useCallback(data => {
        onPickBar(data.name.toUpperCase(), data.value)
    }, [onPickBar])

    return (
        <>
            <MedicationBarChart
                data={chartData}
                onBarClick={onBarClick}
                className={cn('ClientMedicationBarChart', className)}
            />
        </>
    )
}

export default memo(ClientMedicationBarChart)