import React, { useState } from "react";
import "./SpinnerInput.scss";

const SpinnerInput = ({ min = 1, max = 10000000000000, step = 1, initialValue = 1, onChange }) => {
  const [value, setValue] = useState(initialValue);

  const increment = () => {
    setValue((prev) => {
      const newValue = Math.min(prev + step, max);
      if (onChange) onChange(newValue);
      return newValue;
    });
  };

  const decrement = () => {
    setValue((prev) => {
      const newValue = Math.max(prev - step, min);
      if (onChange) onChange(newValue);
      return newValue;
    });
  };

  const handleChange = (e) => {
    const newValue = parseInt(e.target.value, 10);
    if (!isNaN(newValue) && newValue >= min && newValue <= max) {
      setValue(newValue);
      if (onChange) onChange(newValue);
    }
  };

  return (
    <div className="spinner-input">
      <input
        type="number"
        value={value}
        onChange={handleChange}
        className="spinner-field"
        min={min}
        max={max}
        step={step}
      />

      <div className="spinner-controls">
        <button onClick={increment} className="spinner-btn spinner-btn-up" disabled={value >= max}>
          ^
        </button>
        <button onClick={decrement} className="spinner-btn spinner-btn-down" disabled={value <= min}>
          ^
        </button>
      </div>
    </div>
  );
};

export default SpinnerInput;
