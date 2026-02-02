import React, { useMemo } from 'react'

import PropTypes from 'prop-types'

import ReactRater from 'react-rater'

import cn from 'classnames'

import { ReactComponent as StarActive } from 'images/star-active.svg'
import { ReactComponent as StarInactive } from 'images/star-inactive.svg'

import './Rater.scss'

const ClassMap = {
    isDisabled: 'Star_isDisabled',
    isActive: 'Star_isActive',
    isActiveHalf: 'Star_isActiveHalf',
    willBeActive: 'Star_willBeActive'
}

function Star(props) {
    const starProps = Object.assign({}, props)

    const classNames = useMemo(() => (
        Object.keys(ClassMap)
            .filter(prop => (delete starProps[prop], props[prop]))
            .map(prop => ClassMap[prop])
    ), [props, starProps])

    const Star = (
        (props.isActive || props.willBeActive) ? StarActive : StarInactive
    )

    return (
        <Star
            className={cn('Star', ...classNames)}
            {...starProps}
        />
    )
}

function Rater({ className, withDigits, ...props }) {
    const rating = props.rating ?? 0.0

    return (
        <div className={cn('Rater', className)}>
            <ReactRater {...props} rating={rating}>
                <Star/>
            </ReactRater>

            {withDigits && (
                <span className="Rater-Digits">
                    {parseFloat(rating).toFixed(1)}
                </span>
            )}
        </div>
    )
}

Rater.propTypes = {
    total: PropTypes.number,
    rating: PropTypes.number,
    interactive: PropTypes.bool,
    children: PropTypes.any,
    onRate: PropTypes.func,
    onRating: PropTypes.func,
    onCancelRate: PropTypes.func,
    withDigits: PropTypes.bool,
}

Rater.defaultProps = {
    total: 5,
    rating: 0,
    interactive: false
}

export default Rater
