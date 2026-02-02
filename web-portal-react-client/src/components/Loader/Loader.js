import React from 'react'

import cn from 'classnames'

import './Loader.scss'

import { ReactComponent as LoaderImg } from 'images/loader.svg'

function Loader ({ style, isCentered, hasBackdrop = false, className }) {
    return (
        <>
            <div
                style={hasBackdrop ? { margin: 0 } : style}
                className={cn(
                    'Loader',
                    className,
                    { 'Loader_has_backdrop': hasBackdrop },
                    { 'Loader_centered': isCentered }
                )}
            >
                <LoaderImg style={hasBackdrop ? style : null} className='LoaderImg'/>
            </div>
            {hasBackdrop && (
                <div className='Loader-Backdrop'/>
            )}
        </>
    )
}

export default Loader;