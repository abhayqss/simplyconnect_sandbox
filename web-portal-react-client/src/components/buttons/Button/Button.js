import React, {
    useRef,
    useState,
    useEffect
} from 'react'

import PTypes from 'prop-types'

import {
    UncontrolledTooltip,
    Button as BaseButton
} from 'reactstrap'

function Tooltip(props) {
    return (
        <UncontrolledTooltip
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
            {...props}
        />
    )
}

function Button(
    {
        id,
        hasTip,
        renderTip,
        tipText, //@deprecated
        tipPlace, //@deprecated
        tipTrigger, //@deprecated
        tipClassName, //@deprecated
        tooltip,
        ...props
    }
) {
    const ref = useRef()

    const [, setTooltipReady] = useState(false)

    useEffect(() => setTooltipReady(true), [])

    return (
        <>
            <BaseButton innerRef={ref} id={id} tag={'a'} {...props}/>

            {tooltip && ref.current && typeof tooltip === 'string' && (
                <Tooltip target={ref.current}>
                    {tooltip}
                </Tooltip>
            )}

            {tooltip && ref.current && typeof tooltip === 'object' && (
                <Tooltip target={ref.current} {...tooltip}>
                    {tooltip.text ?? tooltip.render()}
                </Tooltip>
            )}

            {hasTip && ref.current && (tipText || renderTip) && (
                <Tooltip
                    target={ref.current}
                    placement={tipPlace}
                    trigger={tipTrigger}
                    className={tipClassName}
                >
                    {tipText || renderTip()}
                </Tooltip>
            )}
        </>
    )
}

export default Button

Button.propTypes = {
    active: PTypes.bool,
    block: PTypes.bool,
    color: PTypes.string,
    disabled: PTypes.bool,
    outline: PTypes.bool,
    innerRef: PTypes.oneOfType([PTypes.object, PTypes.func, PTypes.string]),
    onClick: PTypes.func,
    size: PTypes.string,
    children: PTypes.node,
    className: PTypes.string,
    cssModule: PTypes.object,
    close: PTypes.bool,
    id: PTypes.string,
    hasTip: PTypes.bool, //@deprecated
    tipText: PTypes.string, //@deprecated
    tipPlace: PTypes.string, //@deprecated
    renderTip: PTypes.func, //@deprecated
    tipTrigger: PTypes.string, //@deprecated
    tipClassName: PTypes.string, //@deprecated,
    tooltip: PTypes.oneOfType([PTypes.string, PTypes.object])
}

Button.defaultProps = {
    hasTip: true,
    tipText: '',
    tipPlace: 'top',
    tipTrigger: 'click hover'
}