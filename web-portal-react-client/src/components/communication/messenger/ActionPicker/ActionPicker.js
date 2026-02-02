import React from 'react'

import cn from 'classnames'

import './ActionPicker.scss'

function ActionPicker({
    top,
    left,
    right,
    bottom,
    options,
    className,
    onClickOption
}) {
    return (
        <ul className={cn(className, 'ActionPicker')} style={{ top, bottom, left, right }}>
            {options.map((option, i) => {
                const onClick = option.onClick ? option.onClick : onClickOption.bind(null, option)

                return (
                    <li
                        key={i}
                        onClick={onClick}
                        className="ActionPickerItem"
                    >
                        {option.title}
                    </li>
                )
            })}
        </ul>
    )
}

export default ActionPicker
