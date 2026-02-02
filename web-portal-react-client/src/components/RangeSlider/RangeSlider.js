import React, {
    memo,
    useMemo,
    useCallback
} from 'react'

import cn from 'classnames'
import PropTypes from 'prop-types'

import { range } from 'underscore'

import Slider from 'react-bootstrap-slider'

import { map } from 'lib/utils/ArrayUtils'
import { noop } from 'lib/utils/FuncUtils'

import './RangeSlider.scss'

const Range = memo(function ({ min, max }) {
    const _range = useMemo(
        () => range(min, max + 1),
        [min, max]
    )

    return (
        <div className="RangeSlider-Range">
            {map(_range, v => (
                <span className="RangeSlider-RangeNumber" key={v}>
                    {v}
                </span>
            ))}
        </div>
    )
})

Range.propTypes = {
    min: PropTypes.number,
    max: PropTypes.number
}

Range.defaultProps = {
    min: 0,
    max: 5
}

function RangeSlider(
    {
        min,
        max,
        step,
        value,
        onChange,
        className,
        isDisabled
    }
) {
    const _onChange = useCallback(e => {
        onChange(e.target.value)
    }, [onChange])

    return (
        <div className={cn('RangeSlider', className)}>
            <Slider
                max={max}
                min={min}
                step={step}
                value={value}
                tooltip="hide"
                disabled={isDisabled && 'disabled'}
                change={_onChange}
            />
            <Range min={min} max={max}/>
        </div>
    )
}

RangeSlider.propTypes = {
    min: PropTypes.number,
    max: PropTypes.number,
    step: PropTypes.number,
    name: PropTypes.string,
    value: PropTypes.number,
    isDisabled: PropTypes.bool,
    className: PropTypes.string,
    onChange: PropTypes.func
}

RangeSlider.defaultProps = {
    step: 1,
    value: 0,
    onChange: noop
}

export default memo(RangeSlider)