import React, { memo, useMemo, useCallback } from 'react'

import cn from 'classnames'

import './ProblemsBarChart.scss'

import {
    Bar,
    Cell,
    YAxis,
    Legend,
    Tooltip,
    BarChart,
    CartesianGrid,
    ResponsiveContainer
} from "recharts";

const COLORS = ["#FD9139", "#B8E986", "#FAD52E", '#fff']

const BarTooltip = ({ active, payload, total }) => {
    payload = payload ?? []

    if (active) {
        let { name, value } = payload[0]?.payload ?? {}

        return (
            <div className="recharts-default-tooltip">
                <p className="label">{`# of ${name} problems - ${value}`}</p>
                <p className="desc">{`Total # of problems - ${total}`}</p>
            </div>
        )
    }

    return null
}

function ProblemsBarChart({ data = [], className, onBarClick }) {
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
        <div className={cn('ProblemsBarChart', className)}>
            <ResponsiveContainer
                width="100%"
                minHeight={350}
            >
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
                    <Tooltip
                        cursor={false}
                        content={<BarTooltip total={totalCount} />}
                    />
                    <Legend
                        payload={legend}
                        wrapperStyle={{
                            paddingTop: 32,
                            paddingLeft: 20
                        }}
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

export default memo(ProblemsBarChart)