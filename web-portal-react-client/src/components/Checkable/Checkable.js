import React, {
    useState,
    useCallback
} from 'react'

import cn from 'classnames'

import './Checkable.scss'

export default function Checkable(
    {
        data,
        isChecked,
        isDisabled,
        isControlled,
        isDefaultChecked,
        onCheck,
        children,
        className
    }
) {
    const [_isChecked, setChecked] = useState(isDefaultChecked)

    const onClick = useCallback(() => {
        if (!isDisabled) {
            setChecked(!_isChecked)
            onCheck(!(isControlled ? isChecked : _isChecked), data)
        }
    }, [
        data,
        onCheck,
        isChecked,
        _isChecked,
        isDisabled,
        isControlled
    ])

    return (
        <div
            className={cn(
                'Checkable',
                { 'Checkable_disabled': isDisabled },
                className)
            }
        >
            <div
                onClick={onClick}
                className="Checkable-Checkbox"
            >
                {(isControlled ? isChecked : _isChecked) && (
                    <span className='Checkable-CheckMark'/>
                )}
            </div>
            {children}
        </div>
    )
}