import React, {
    memo,
    useRef,
    useState,
    useEffect,
    useCallback,
    forwardRef
} from 'react'

import PTypes from 'prop-types'

import locale from 'date-fns/locale/en-GB'

import {
    year,
    month
} from 'date-arithmetic'

import BaseDatePicker from 'react-datepicker'

import {
    format,
    isDate,
    getStartOfWeek,
    getEndOfWeek,
    getStartOfMonth,
    getEndOfMonth
} from 'lib/utils/DateUtils'

import {
    noop
} from 'lib/utils/FuncUtils'

import {
    last,
    first,
    isArray
} from 'lib/utils/ArrayUtils'

import {
    getYearsPeriod
} from './utils/DateUtils'

import {
    FIRST_DAY_OF_WEEK
} from '../Constants'

import { ReactComponent as NavLeft } from './images/nav-left.svg'
import { ReactComponent as NavRight } from './images/nav-right.svg'
import { ReactComponent as CalendarOpen } from 'images/chevron-top-2.svg'
import { ReactComponent as CalendarClosed } from 'images/chevron-bottom-2.svg'

import 'react-datepicker/dist/react-datepicker.css'
import './DatePicker.scss'

const MODE = {
    DATE: 'date',
    DATE_RANGE: 'date-range',
    MONTH: 'month'
}

const { DATE, DATE_RANGE, MONTH } = MODE

const WEEK_DAYS = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun', 'Mon']

locale.code = 'en-GB'
locale.localize.day = i => WEEK_DAYS[i]

export const SelectedDate = memo(forwardRef(({ mode, val, onClick }, ref) => {
    let dateText = ''

    if (mode === DATE) {
        dateText = format(new Date(val), 'MMMM dd, YYYY')
    } else if (mode === DATE_RANGE) {
        const startDate = new Date(val[0])
        const endDate = val[1] && new Date(val[1])

        if (!endDate) dateText = format(startDate, 'MMMM dd, YYYY') + ' - '
        else if (year(startDate) !== year(endDate)) {
            dateText = `${format(startDate, 'MMMM dd, YYYY')} - ${format(endDate, 'MMMM dd, YYYY')}`
        } else if (month(startDate) !== month(endDate)) {
            dateText = `${format(startDate, 'MMMM dd')} - ${format(endDate, 'MMMM dd, YYYY')}`
        } else {
            dateText = `${format(startDate, 'MMMM dd')} - ${format(endDate, 'dd, YYYY')}`
        }
    } else if (mode === MONTH) {
        const startDate = getStartOfMonth(val)
        const endDate = getEndOfMonth(val)
        dateText = `${format(startDate, 'MMMM dd')} - ${format(endDate, 'dd, YYYY')}`
    } else {
        dateText = format(new Date(val), 'MMMM, YYYY')
    }

    return (
        <div
            ref={ref}
            className="DatePicker-SelectedDate"
            data-testid="date-picker_selected-date"
            onClick={onClick}
        >
            {dateText}
        </div>
    )
}))

/*
*     value: inputValue,
      onBlur: this.handleBlur,
      onChange: this.handleChange,
      onClick: this.onInputClick,
      onFocus: this.handleFocus,
      onKeyDown: this.onInputKeyDown,
      id: this.props.id,
      name: this.props.name,
      autoFocus: this.props.autoFocus,
      placeholder: this.props.placeholderText,
      disabled: this.props.disabled,
      autoComplete: this.props.autoComplete,
      className: classnames(customInput.props.className, className),
      title: this.props.title,
      readOnly: this.props.readOnly,
      required: this.props.required,
      tabIndex: this.props.tabIndex
* */
SelectedDate.propTypes = {
    mode: PTypes.oneOf([DATE, DATE_RANGE, MONTH])
}

const Header = memo(function Header(
    {
        date,
        showYearPicker,
        showMonthYearPicker,
        decreaseMonth,
        increaseMonth,
        decreaseYear,
        increaseYear,
        onClickMonth,
        onClickYear,
        prevMonthButtonDisabled,
        nextMonthButtonDisabled
    }) {

    const {
        startPeriod, endPeriod
    } = getYearsPeriod(date);

    const onDecrease = useCallback(e => {
        if (!prevMonthButtonDisabled) {
            if (showMonthYearPicker || showYearPicker) {
                decreaseYear(e)
            } else decreaseMonth(e)
        }
    }, [
        decreaseYear,
        decreaseMonth,
        showYearPicker,
        showMonthYearPicker,
        prevMonthButtonDisabled
    ])

    const onIncrease = useCallback(e => {
        if (!nextMonthButtonDisabled) {
            if (showMonthYearPicker || showYearPicker) {
                increaseYear(e)
            } else increaseMonth(e)
        }
    }, [
        increaseYear,
        increaseMonth,
        showYearPicker,
        showMonthYearPicker,
        nextMonthButtonDisabled
    ])

    return (
        <div className="DatePicker-Header">
            <NavLeft className="DatePicker-NavLeft" onClick={onDecrease}/>
            <div className="DatePicker-Title">
                {showMonthYearPicker && (
                    <div onClick={onClickYear} className="DatePicker-Year">
                        {year(date)}
                    </div>
                )}
                {showYearPicker && (
                    <div className="DatePicker-Year">
                        {startPeriod} - {endPeriod}
                    </div>
                )}
                {!(showMonthYearPicker || showYearPicker) && (
                    <>
                        <div onClick={onClickMonth} className="DatePicker-Month">
                            {format(date, 'MMMM')}
                        </div>
                        &nbsp;
                        <div onClick={onClickYear} className="DatePicker-Year">
                            {format(date, 'YYYY')}
                        </div>
                    </>
                )}
            </div>
            <NavRight className="DatePicker-NavRight" onClick={onIncrease}/>
        </div>
    )
})

function DatePicker(
    {
        mode,
        value,
        firstDayOfWeek,
        onChange
    }
) {
    const ref = useRef()

    const [isOpen, setOpen] = useState(false)
    const [isMonthPicker, setMonthPicker] = useState(false)
    const [isYearPicker, setYearPicker] = useState(false)

    let startDate = isArray(value) ? (
        first(value) || getStartOfWeek(Date.now(), firstDayOfWeek)
    ) : value

    let endDate = isArray(value) ? (
        last(value) === undefined ? getEndOfWeek(startDate, firstDayOfWeek) : last(value)
    ) : undefined

    if (startDate && !isDate(startDate)) startDate = new Date(startDate)

    const onOpened = useCallback(() => setOpen(true), [])
    const onClosed = useCallback(() => setOpen(false), [])

    const open = useCallback(() => {
        ref.current.onInputClick()
    }, [])

    const onOpenMonthPicker = useCallback(() => {
        setMonthPicker(true)
        setYearPicker(false)
    }, [])

    const onOpenYearPicker = useCallback(() => {
        setYearPicker(true)
        setMonthPicker(false)
    }, [])

    const _onChange = useCallback(date => {
        onChange(date)
        setYearPicker(false)
        setMonthPicker(mode === MONTH)
    }, [mode, onChange])

    useEffect(() => {
        setYearPicker(false)
        setMonthPicker(mode === MONTH)
    }, [mode])

    return (
        <div className="DatePicker">
            <BaseDatePicker
                ref={ref}
                selected={startDate}
                startDate={startDate}
                endDate={endDate}
                locale={locale}
                showMonthYearPicker={isMonthPicker}
                showYearPicker={isYearPicker}
                selectsRange={mode === DATE_RANGE}
                customInput={(
                    <SelectedDate
                        mode={mode}
                        val={isArray(value) ? [startDate, endDate] : startDate}
                    />
                )}
                renderCustomHeader={props => (
                    <Header
                        {...props}
                        showMonthYearPicker={isMonthPicker}
                        showYearPicker={isYearPicker}
                        onClickYear={onOpenYearPicker}
                        onClickMonth={onOpenMonthPicker}
                    />
                )}
                onChange={_onChange}
                onCalendarOpen={onOpened}
                onCalendarClose={onClosed}
            />
            {isOpen ? (
                <CalendarOpen
                    onClick={open}
                    className='DatePicker-Icon'
                />
            ) : (
                <CalendarClosed
                    onClick={open}
                    className='DatePicker-Icon'
                />
            )}
        </div>
    )
}

DatePicker.propTypes = {
    mode: PTypes.oneOf([DATE, DATE_RANGE, MONTH]),
    firstDayOfWeek: PTypes.number,
    onChange: noop
}

DatePicker.defaultProps = {
    firstDayOfWeek: FIRST_DAY_OF_WEEK
}

export default memo(DatePicker)