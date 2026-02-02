import React, {
    useMemo,
    useEffect,
    useCallback
} from 'react'

import { 
    withCookies
} from 'react-cookie'

import {
    noop
} from 'underscore'

import { Timer, DateUtils } from 'lib/utils/Utils'

import { environment } from '../config'

export const AUTO_SAVE_TYPES = {
    BY_INTERVAL: 'interval',
    WHEN_SESSION_EXPIRES: 'sessionExpires'
}

const SECONDS_BEFORE_SESSION_EXPIRES = 30 * DateUtils.MILLI.second

const getExpirationTime = cookies => {
    const token = cookies.get('jwtHeaderAndPayload')

    const { exp } = JSON.parse(
        atob(token.split('.')[1])
    )

    return exp * 1000 - Date.now()
}

const withAutoSave = ({
    type = AUTO_SAVE_TYPES.WHEN_SESSION_EXPIRES,
    interval = DateUtils.MILLI.minute * 3 //3 minutes
} = {}) => {
    return function(Component) {
        return withCookies(function WithAutoSave(props) {
            const { cookies } = props ?? {}

            const timer = useMemo(() => {
                if (
                    type === AUTO_SAVE_TYPES.WHEN_SESSION_EXPIRES &&
                    environment === 'production'
                ) {
                    return new Timer({
                        step: getExpirationTime(cookies) - SECONDS_BEFORE_SESSION_EXPIRES,
                        maxTime: 0 //when the Timer stops, onTimeChange will be called
                    })
                } else return new Timer({
                    step: interval,
                    maxTime: Infinity
                })
            }, [cookies])

            const onStop = useCallback(() => {
                timer.onTimeChange = noop
                timer.stop()
            }, [timer])
    
            useEffect(() => {
                timer.countdown(noop)
                return () => onStop()
            }, [timer, onStop])

            const onInit = ({
                onSave
            }) => {
                timer.onTimeChange = () => {
                    if (type === AUTO_SAVE_TYPES.WHEN_SESSION_EXPIRES) {
                        onSave()

                        onStop()
                    } 
                    else onSave()
                }
            }

            return (
                <Component 
                    {...props} 
                    autoSaveAdapter={{
                        init: options => onInit(options),
                        stop: () => onStop
                    }}                 
                />
            )
        })
    }
}

export default withAutoSave