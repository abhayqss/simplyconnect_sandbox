import React, { useMemo } from 'react'

import cn from 'classnames'
import { map } from 'underscore'

import {
    Pie,
    Cell,
    Legend,
    Tooltip,
    PieChart,
    ResponsiveContainer
} from 'recharts'

import './AssessmentPieChart.scss'

const COLORS = ['#fc913a', '#fffe6f', '#b8e986', '#a99cd7', '#97deba']

const PieTooltip = ({ active, payload, total }) => {
    payload = payload ?? []

    if (active) {
        let { name, value } = payload[0]?.payload ?? {}

        return (
            <div className="recharts-default-tooltip">
                <p className="label">{`# of ${name} assessments - ${value}`}</p>
                <p className="label">{`${name} - ${Math.floor((value / total) * 100)}%`}</p>
                <p className="desc">{`Total # of assessments - ${total}`}</p>
            </div>
        )
    }

    return null
}

function getLegendPosition() {
    const w = window.innerWidth

    return w >= 1200 || (w >= 650 && w < 850) ? {
        align: 'right', verticalAlign: 'middle'
    } : {
        align: 'center', verticalAlign: 'bottom'
    }
}

export default function AssessmentPieChart({ title, data, className }) {
    const chartData = useMemo(() => map(data, o => ({
            name: o.status, value: o.count
        })), [data]
    )

    const totalCount = (
        chartData.reduce((accum, o) => (
            accum += o.value
        ), 0)
    )

    const legendData = useMemo(() => (
        [...chartData, { name: 'Total', value: totalCount }]
    ), [data, totalCount])

    const legend = useMemo(() => map(legendData, (o, i) => ({
        value: `${o.name} - ${o.value}`,
        type: 'square',
        color: COLORS[i % COLORS.length]
    })), [legendData])

    return (
        <div className={cn('Chart PieChart AssessmentPieChart', className)}>
            {title && (
                <div className='Chart-Title'>
                    {title}
                </div>
            )}
            <ResponsiveContainer>
                <PieChart>
                    <Legend
                        layout='vertical'
                        iconType='square'
                        iconSize={20}
                        payload={legend}
                        className="PieChart-Legend"
                        {...getLegendPosition()}
                    />
                    <Tooltip
                        cursor={false}
                        content={<PieTooltip total={totalCount}/>}
                    />
                    <Pie
                        dataKey='value'
                        data={chartData}
                        fill="#8884d8"
                    >
                        {
                            data.map((entry, index) => (
                                <Cell key={index} fill={COLORS[index % COLORS.length]}/>
                            ))
                        }
                    </Pie>
                </PieChart>
            </ResponsiveContainer>
        </div>
    )
}