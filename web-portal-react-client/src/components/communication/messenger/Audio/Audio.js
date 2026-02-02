import React, {
    memo,
    useRef,
    useState,
    useEffect,
} from 'react'

import { Progress } from 'reactstrap'

import { Timer, measure } from 'lib/utils/Utils'

import { ReactComponent as Play } from 'images/play-record.svg'
import { ReactComponent as Stop } from 'images/stop-playing.svg'
import { ReactComponent as Delete } from 'images/delete-file.svg'

import cn from 'classnames'

import './Audio.scss'

const MAX_PROGRESS = 100
const UNITS_IN_PERIOD = 60
const MAX_RECORD_DURATION = 120

const timer = new Timer({
    maxTime: MAX_RECORD_DURATION,
})

function formatTime(seconds) {
    const addZeroIfNeeded = number => ('0' + number).slice(-2)

    seconds = Math.floor(seconds)

    let minutes = Math.floor(seconds / UNITS_IN_PERIOD)
    seconds = seconds % UNITS_IN_PERIOD

    minutes = minutes % UNITS_IN_PERIOD

    return `${addZeroIfNeeded(minutes)}:${addZeroIfNeeded(seconds)}`
}

export const AudioTrack = memo(({ url, className }) => {
    const audioRef = useRef()
    const progressRef = useRef()

    let [isPaused, setIsPaused] = useState(true)
    let [currentTime, setCurrentTime] = useState(0)

    let progress = currentTime / audioRef.current?.duration * 100

    function play() {
        let audioElement = audioRef.current

        if (isPaused) {
            audioElement.play()
        } else {
            audioElement.pause()
        }

        setIsPaused(audioElement.paused)
    }

    function reset() {
        setCurrentTime(0)
        setIsPaused(true)
    }

    function onClickProgress(event) {
        let audioElement = audioRef.current
        let progressWrapper = progressRef.current

        let { left } = measure(progressWrapper)

        let x = event.pageX - left
        let percentage = (x / progressWrapper.offsetWidth).toFixed(5)

        audioElement.currentTime = percentage * audioElement.duration
    }

    function onTimeUpdate(event) {
        setCurrentTime(event.target.currentTime)
    }

    const Icon = isPaused ? Play : Stop

    return (
        <div className={cn('Audio', className)}>
            <Icon className="Audio-PlayIcon" onClick={play} />

            <div className="Audio-Duration">{formatTime(currentTime)}</div>

            <div ref={progressRef} className="Audio-ProgressWrapper">
                <Progress
                    animated={false}
                    className="Audio-Progress"
                    barClassName="Audio-ProgressBar"
                    onClick={onClickProgress}
                    value={progress}
                    max={MAX_PROGRESS}
                />
            </div>

            <audio
                preload="auto"
                ref={audioRef}
                onEnded={reset}
                onTimeUpdate={onTimeUpdate}
                src={url}
            />
        </div>
    )
})

export function AudioProgress({ className, onCancel, onFinish }) {
    let [currentTime, setCurrentTime] = useState(0)

    let progress = currentTime.toFixed(5) / MAX_RECORD_DURATION * 100

    timer.onTimeChange = setCurrentTime

    function cancel() {
        timer.stop()
        onCancel()
    }

    useEffect(function countdown() {
        timer.countdown(onFinish)

        return () => timer.stop()
    }, [onFinish])

    return (
        <div className={cn('Audio', className)}>
            <div className="Audio-Duration">{formatTime(currentTime)}</div>

            <Progress
                animated={false}
                className="Audio-Progress flex-1"
                barClassName="Audio-ProgressBar"
                value={progress}
                max={MAX_PROGRESS}
            />

            <Delete className="Audio-CancelButton margin-left-10" onClick={cancel} />
        </div>
    )
}