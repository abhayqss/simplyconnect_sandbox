import React from "react";

import cn from "classnames";

import { Label } from "reactstrap";

import { CheckboxField } from "components/Form";

import "./CheckboxGroupField.scss";

function CheckboxGroupField({
  view,
  name,
  label,
  value,
  options,

  errorText,
  renderTitleIcon,

  className,
  containerClass,

  onChange,
}) {
  function onClickCheckbox(index, isCheckedOut) {
    function mapOption(option, i) {
      return index === i
        ? {
            ...option,
            value: isCheckedOut,
          }
        : option;
    }

    const result = value.map(mapOption);
    onChange(name, result);
  }

  return (
    <div className={cn("CheckboxGroupField", { CheckboxGroupField_row: view === "row" }, containerClass)}>
      {label && (
        <>
          <Label className="CheckboxGroupField-Title">{label}</Label>
          {renderTitleIcon && renderTitleIcon()}
        </>
      )}

      <div className={cn("CheckboxGroupField-Body form-control", { "is-invalid": !!errorText }, className)}>
        {options.map((option, i) => {
          return (
            <CheckboxField
              name={i}
              key={`${name}.${i}`}
              label={option.label}
              hasError={!!errorText}
              value={value?.[i].value}
              onChange={onClickCheckbox}
              className="CheckboxGroupField-CheckboxField"
            />
          );
        })}
      </div>

      {errorText && <div className="CheckboxGroupField-Error">{errorText}</div>}
    </div>
  );
}

export default CheckboxGroupField;
