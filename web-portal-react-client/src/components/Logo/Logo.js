import React from 'react'

import cn from 'classnames'
import PropTypes from 'prop-types'

import './Logo.scss'

import {ReactComponent as LogoIcon} from 'images/simplyconnect-logo.svg'
import {ReactComponent as LogoMonoColorImage} from 'images/simplyconnect-logo-mono-color.svg'

function Logo({ isMonoColor, className, iconSize }) {
    return (
        <div className={cn('Logo', { 'Logo_color_mono': isMonoColor }, className)}>
            <LogoIcon className='Logo-Icon' style={{height: iconSize}} />
        </div>
    )
}

Logo.propTypes = {
    isMonoColor: PropTypes.bool,
    iconSize: PropTypes.number,
    className: PropTypes.string
}

Logo.defaultProps = {
    iconSize: 50,
    isMonoColor: false
}

export default Logo