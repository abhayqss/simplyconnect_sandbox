import React, { useState } from "react";
import Select from 'react-select';

const SelectOnly = (props) => {
  const {
    options,
    isDisabled = false,
    isLoading = false,
    isClearable = false,
    isRtl = false,
    isSearchable = false,
    defaultValue=null,
    selectedChange,
    selectedOption = null,
    className,
    name,
  } = props;

  return (<>
      <Select
        className={className}
        classNamePrefix="select"
        defaultValue={defaultValue}
        isDisabled={isDisabled}
        isLoading={isLoading}
        isClearable={isClearable}
        isRtl={isRtl}
        isSearchable={isSearchable}
        name={name}
        options={options}
        value={selectedOption || null}
        onChange={selectedChange}
        getOptionValue={option => option.id}
      />
    </>

  );
};

export default SelectOnly;
