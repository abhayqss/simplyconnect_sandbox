import React from 'react'

import cn from 'classnames'
import { map } from 'underscore'

import PropTypes from 'prop-types'

import './SelectSection.scss'

const COLORS = [
    '#ffd3c0',
    '#fff1ca',
    '#d5f3b8',
    '#d1ebfe',
    '#e7ccfe'
]

export default function SelectSection({id, name, title, options, value, onChange, className}) {
    return (
        <div
            key={id}
            className={cn("SelectSection", className)}
            style={{ borderLeftColor: COLORS[id % 5] }}>
            <div className="SelectSection-Title">
                {title}
            </div>
            {map(options, o => (
                <div
                    onClick={() => { onChange(o.value) }}
                    style={(value === o.value) ? {
                        backgroundColor: '#f9f9f9',
                        borderTop: '1px solid #bfbdbd',
                        borderBottom: '1px solid #bfbdbd'
                    } : {}}
                    className="SelectSection-Item"
                >
                    {o.text}
                    {(value === o.value) && (
                        <span className="SelectSection-CheckMark"/>
                    )}
                </div>
            ))}
        </div>
    )
}

SelectSection.propTypes = {
    id: PropTypes.number,
    name: PropTypes.string,
    title: PropTypes.string,
    className: PropTypes.string,
    onChange: PropTypes.func
}