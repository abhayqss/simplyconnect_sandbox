import React, { forwardRef, Component } from "react";

import cn from "classnames";
import { noop } from "underscore";
import PTypes from "prop-types";

import DatePicker from "react-datepicker";

import { FormGroup, Label } from "reactstrap";

import "./DateField.scss";
import "react-datepicker/dist/react-datepicker.css";

import { ReactComponent as Calendar } from "images/calendar.svg";

function getStartOfDay() {
  return new Date(new Date().setHours(0)).setMinutes(0);
}

function getEndOfDay() {
  return new Date(new Date().setHours(23)).setMinutes(59);
}

const CustomInput = forwardRef((props, ref) => <input type="text" ref={ref} {...props} />);

export default class DateField extends Component {
  static propTypes = {
    name: PTypes.string,
    label: PTypes.string,
    testId: PTypes.string,
    errorText: PTypes.oneOfType([PTypes.bool, PTypes.string]),
    className: PTypes.string,
    dateFormat: PTypes.string,
    timeFormat: PTypes.string,
    autoComplete: PTypes.string,

    timeCaption: PTypes.string,
    placeholder: PTypes.string,

    value: PTypes.number,
    display: PTypes.string,
    minTime: PTypes.number,
    maxTime: PTypes.number,
    minDate: PTypes.number,
    maxDate: PTypes.number,
    timeIntervals: PTypes.number,
    timeSelectViewMode: PTypes.oneOf(["base", "dropdown"]),

    hasError: PTypes.bool,
    isDisabled: PTypes.bool,
    hasTimeSelect: PTypes.bool,
    hasTimeSelectOnly: PTypes.bool,

    isPastDisabled: PTypes.bool,
    isFutureDisabled: PTypes.bool,

    onBlur: PTypes.func,
    onFocus: PTypes.func,
    onChange: PTypes.func,
    onOpenPicker: PTypes.func,
    onClickOutside: PTypes.func,
  };

  static defaultProps = {
    display: null,
    isDisabled: false,
    hasTimeSelect: false,
    hasTimeSelectOnly: false,
    timeSelectViewMode: "base",
    autoComplete: "off",

    minTime: getStartOfDay(),
    maxTime: getEndOfDay(),

    errorText: "",
    timeCaption: "Time",
    dateFormat: "MM/dd/yyyy",
    timeFormat: "HH:mm",

    timeIntervals: 15,

    onBlur: noop,
    onFocus: noop,
    onChange: noop,
    onOpenPicker: noop,
    onClickOutside: noop,
  };

  pickerRef = React.createRef();

  onBlur = () => {
    this.props.onBlur(this.props.name);
  };

  onFocus = () => {
    this.props.onFocus(this.props.name);
  };

  onChange = (value) => {
    this.props.onChange(this.props.name, value);
  };

  onOpenPicker = () => {
    this.props.onOpenPicker();
    this.pickerRef.current.onInputClick();
  };

  onClickOutside = () => {
    this.props.onClickOutside();
  };

  render() {
    const {
      name,
      label,
      value,
      testId,
      display,
      minTime,
      maxTime,
      minDate,
      maxDate,
      dateFormat,
      timeFormat,
      className,
      placeholder,
      timeCaption,
      timeSelectViewMode,
      timeIntervals,
      isDisabled,
      errorText,
      autoComplete,
      hasTimeSelect,
      hasTimeSelectOnly,
      isPastDisabled,
      isFutureDisabled,
      popperPlacement,
      onChangeRaw,
    } = this.props;

    const hasError = this.props.hasError || !!errorText;

    const limits = {};

    if (minTime) limits.minTime = new Date(minTime);
    if (minDate) limits.minDate = new Date(minDate);
    if (maxTime) limits.maxTime = new Date(maxTime);
    if (maxDate) limits.maxDate = new Date(maxDate);

    if (isPastDisabled) {
      limits.minTime = new Date();
      limits.minDate = new Date();
    }

    if (isFutureDisabled) {
      limits.maxTime = new Date();
      limits.maxDate = new Date();
    }

    return (
      <FormGroup
        data-testid={`${testId ?? name}_field`}
        className={cn(
          "DateField",
          className,
          { DateField_disabled: isDisabled },
          `DateField_timeSelectViewMode_${timeSelectViewMode}`,
        )}
      >
        {label ? (
          <>
            <Label className="DateField-Label" data-testid={`${testId ?? name}_field-label`}>
              {label}
            </Label>
          </>
        ) : null}
        <DatePicker
          ref={this.pickerRef}
          name={name}
          value={display}
          selected={value}
          dateFormat={dateFormat}
          timeFormat={timeFormat}
          dropdownMode="select"
          invalid={hasError}
          disabled={isDisabled}
          placeholderText={placeholder}
          timeCaption={timeCaption}
          timeIntervals={timeIntervals}
          showTimeSelect={hasTimeSelect}
          showTimeSelectOnly={hasTimeSelectOnly}
          autoComplete={autoComplete}
          showMonthDropdown
          showYearDropdown
          {...limits}
          customInput={<CustomInput data-testid={`${testId ?? name}_field-input`} />}
          className={cn("DateField-Input form-control", hasError && "is-invalid")}
          onBlur={this.onBlur}
          onFocus={this.onFocus}
          onChange={this.onChange}
          onChangeRaw={onChangeRaw}
          onClickOutside={this.onClickOutside}
          popperPlacement={popperPlacement}
        />
        <Calendar onClick={this.onOpenPicker} className="DateField-CalendarIcon" />
        {hasError ? <div className="DateField-Error">{errorText}</div> : null}
      </FormGroup>
    );
  }
}
