import React, {
    memo,
    useCallback,
    useEffect,
} from 'react'

import PropTypes from 'prop-types'

import {
    UncontrolledTooltip,
    Tooltip as TooltipComp,
} from "reactstrap";

import { useToggle } from 'hooks/common';

import { OutsideClickListener } from 'components';

import { noop } from 'lib/utils/FuncUtils';

function Tooltip({
    children,
    onToggle,
    clickOutside,
    trigger = "click",
    ...tooltipProps
}) {

    const [isOpen, toggleTooltip] = useToggle();

    const closeTooltip = useCallback(() => {
        if (clickOutside) {
            toggleTooltip(false)
        }
    }, [clickOutside, toggleTooltip])

    useEffect(() => {
        onToggle(isOpen)
    }, [isOpen, onToggle])

    return (
        <TooltipComp
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
            {...tooltipProps}

            isOpen={isOpen}
            trigger={trigger}
            toggle={() => toggleTooltip()}
        >
            <OutsideClickListener
                onClick={closeTooltip}
                containerSelector=".modal"
            >
                {children?.(closeTooltip) ?? children}
            </OutsideClickListener>

        </TooltipComp>

    )
}

Tooltip.propTypes = {
    onToggle: PropTypes.func,
    clickOutside: PropTypes.bool,
    ...UncontrolledTooltip.propTypes
}

Tooltip.defaultProps = {
    ...UncontrolledTooltip.defaultProps,
    onToggle: noop,
    clickOutside: false
}

export default memo(Tooltip);