import React, { useRef } from 'react'

import cn from 'classnames'

import { UncontrolledTooltip as Tooltip } from 'reactstrap'

function withTooltip({
    text,
    className,
    placement = "top",
    trigger = "click hover",
    boundariesElement = document.body,
}) {
    return function (Component) {
        return function ({ isTooltipEnabled, ...props }) {
            const ref = useRef()

            return (
                <>
                    <div
                        ref={ref}
                        className={cn('TooltipTarget', className, {
                            'TooltipTarget_disabled': props.disabled || props.isDisabled,
                            [`${className}_disabled`]: props.disabled || props.isDisabled,
                        })}
                    >
                        <Component {...props} />
                    </div>

                    {isTooltipEnabled && ref.current && (
                        <Tooltip
                            placement={placement}
                            target={ref.current}
                            trigger={trigger}
                            className="Tooltip"
                            modifiers={[
                                {
                                    name: 'offset',
                                    options: { offset: [0, 6] }
                                },
                                {
                                    name: 'preventOverflow',
                                    options: { boundary: document.body }
                                }
                            ]}
                        >
                            {text}
                        </Tooltip>
                    )}
                </>
            )
        }
    }
}

export default withTooltip