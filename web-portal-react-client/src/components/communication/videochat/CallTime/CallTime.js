import React, {
    memo,
    useState,
    useEffect
} from 'react'

import { compose } from 'underscore'

const increment = count => ++count

const getHigherUnits = units => (units / 60).toFixed(2).split('.')
const getUnitsFromPercentage = percentage => (percentage * 0.01 * 60).toFixed(0)
const formatUnits = str => str.length > 1 ? str : '0' + str

const view = compose(formatUnits, getUnitsFromPercentage)

function format(seconds) {
    let result = ''

    const [minutes, minutesPercentage] = getHigherUnits(seconds)
    const [hours, hoursPercentage] = getHigherUnits(minutes)

    const secondsView = view(minutesPercentage)
    const minutesView = view(hoursPercentage)
    
    if (parseInt(hours)) {
        const hoursView = formatUnits(hours)

        result = `${hoursView}:`
    }
    
    return result + `${minutesView}:${secondsView}`
}

const ONE_SECOND = 1000

function CallTime() {
    const [seconds, setSeconds] = useState(0)

    useEffect(function start() {
        const timerId = setInterval(setSeconds, ONE_SECOND, increment)
        const stop = () => clearInterval(timerId)

        return stop
    }, [])

    const time = format(seconds)

    return (
        <>{time}</>
    )
}

export default memo(CallTime)