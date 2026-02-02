import React, { memo } from 'react'

import PTypes from 'prop-types'

import { range } from 'underscore'

import {
    DIMENSIONS,
    HOUR_GRADATIONS
} from '../../Constants'

import './DayTimeSegment.scss'

function DayTimeSegment({ title, height, gradation }) {
    return (
        <div className="DayTimeSegment" style={{ height }}>
            <div className="DayTimeSegment-Title">
                <div className="DayTimeSegment-TitleText">
                    {title}
                </div>
            </div>
            <div className="DayTimeSegment-Slots">
                {range(1 / gradation).map(v => (
                    <div key={v} className="DayTimeSegment-Slot"/>
                ))}
            </div>
        </div>
    )
}

DayTimeSegment.propTypes = {
    title: PTypes.string,
    height: PTypes.number,
    gradation: PTypes.number
}

DayTimeSegment.defaultProps = {
    title: '',
    height: DIMENSIONS.HOUR_SEGMENT_HEIGHT,
    gradation: HOUR_GRADATIONS.HALF
}

export default memo(DayTimeSegment)