import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
    map
} from 'underscore'

import {
    noop
} from 'lib/utils/FuncUtils'

import './TestComponent.scss'

function TestComponent({ className }) {

    return (
        <div className={cn("TestComponent", className)}>

        </div>
    )
}

TestComponent.propTypes = {
    className: PTypes.string
}

TestComponent.defaultProps = {

}

export default memo(TestComponent)