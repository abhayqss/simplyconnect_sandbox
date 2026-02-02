import React, { useMemo, useCallback } from 'react'

import cn from 'classnames'

import {
    Bar,
    Cell,
    YAxis,
    Legend,
    Tooltip,
    BarChart,
    CartesianGrid,
    ResponsiveContainer
} from 'recharts'

import './MedicationBarChart.scss'

const COLORS = [ "#97e0ff", "#d9aee1", "#edf385", "#fff" ]

const BarTooltip = ({ active, payload, total }) => {
    payload = payload ?? []

    if (active) {
        let { name, value } = payload[0]?.payload ?? {}

        return (
            <div className="recharts-default-tooltip">
                <p className="label">{`# of ${name} medications - ${value}`}</p>
                <p className="desc">{`Total # of medications - ${total}`}</p>
            </div>
        )
    }

    return null
}

export default function MedicationBarChart({
    data,
    className,
    onBarClick,
}) {
    const totalCount = data.reduce((accum, o) => {
        return accum += o.value
    }, 0)

    const legendData = useMemo(() => {
        let dataCopy = data.slice()

        dataCopy.push({
            name: 'Total',
            value: totalCount
        })

        return dataCopy
    }, [data, totalCount])

    const legend = useMemo(() => legendData.map((o, i) => ({
        value: `${o.name} - ${o.value}`,
        type: 'square',
        color: COLORS[i % COLORS.length]
    })), [legendData])

    const onSelectBar = useCallback(data => {
        onBarClick(data.payload)
    }, [onBarClick])

    return (
        <div className={cn("MedicationBarChart", className)}>
            <ResponsiveContainer width="100%" minHeight={350}>
                <BarChart
                    width={450}
                    height={350}
                    data={data}
                    barGap={0}
                    barCategoryGap={0}
                    maxBarSize={90}
                >
                    <CartesianGrid
                        stroke="#f1f1f1"
                        horizontal={true}
                        vertical={false}
                    />
                    <YAxis
                        axisLine={false}
                        tickLine={false}
                    />
                    <Legend
                        payload={legend}
                        wrapperStyle={{
                            paddingTop: 32,
                            paddingLeft: 20
                        }}
                    />
                     <Tooltip
                        cursor={false}
                        content={<BarTooltip total={totalCount} />}
                    />
                    <Bar
                        dataKey="value"
                        onClick={onSelectBar}
                    >
                        {data.map((o, index) => (
                            <Cell
                                key={index}
                                fill={COLORS[index]}
                            />
                        ))}
                    </Bar>
                </BarChart>
            </ResponsiveContainer>
        </div>
    )
}