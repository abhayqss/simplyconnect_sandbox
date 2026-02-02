import React, { useRef, useEffect } from 'react'

import $ from 'jquery'

import cn from 'classnames'

function OutsideClickListener({
    children,
    className,
    containerSelector,

    onClick: cb
}) {
    let ref = useRef()

    useEffect(function subscribe() {
        function onClick(event) {
            if (!ref.current || ref.current.contains(event.target)) {
                return
            }

            cb(event)
        }
        const containerElement = containerSelector ? $(containerSelector) : $(window);

        containerElement.on('click', onClick)

        return function unsubscribe() {
            $(containerElement).off('click', onClick)
        }
    }, [cb, containerSelector])

    return (
        <div className={cn('OutsideClickListener', className)} ref={ref}>
            {children}
        </div>
    )
}

export default OutsideClickListener
