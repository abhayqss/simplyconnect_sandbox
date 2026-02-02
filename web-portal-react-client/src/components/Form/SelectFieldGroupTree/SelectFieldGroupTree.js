import React, { PureComponent } from 'react'

import cn from 'classnames'
import { noop } from 'underscore'
import PropTypes from 'prop-types'
import { FormGroup, Label } from 'reactstrap'

import MultiSelect from '../../MultiSelect/MultiSelect'

import './SelectFieldGroupTree.scss'
import MultiSelectGroup from "../../MultiSelectGroup/MultiSelectGroup";

export default class SelectFieldGroupTree extends PureComponent {

  static propTypes = {
    name: PropTypes.string,
    label: PropTypes.string,
    options: PropTypes.array,
    sections: PropTypes.array,
    isMultiple: PropTypes.bool,
    isDisabled: PropTypes.bool,
    hasTooltip: PropTypes.bool,
    hasValueTooltip: PropTypes.bool,
    isSectioned: PropTypes.bool,
    hasTags: PropTypes.bool,
    hasAutoScroll: PropTypes.bool,
    hasSearchBox: PropTypes.bool,
    hasEmptyValue: PropTypes.bool,
    hasSectionTitle: PropTypes.bool,
    isFetchingOptions: PropTypes.bool,
    hasCustomValueBox: PropTypes.bool,
    hasKeyboardSearch: PropTypes.bool,
    hasDropdownHeader: PropTypes.bool,
    hasKeyboardSearchText: PropTypes.bool,
    optionType: PropTypes.oneOf(['checkbox', 'tick']),
    hasSectionSeparator: PropTypes.bool,
    hasSectionIndicator: PropTypes.bool,
    hasAllOption: PropTypes.bool,
    hasNoneOption: PropTypes.bool,
    tooltipText: PropTypes.string,
    className: PropTypes.string,
    placeholder: PropTypes.string,
    hasError: PropTypes.any,
    errorText: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),
    onBlur: PropTypes.func,
    onChange: PropTypes.func,
    onExpand: PropTypes.func,
    onCollapse: PropTypes.func,
    renderIcon: PropTypes.func,
    formatOption: PropTypes.func,
    formatOptionText: PropTypes.func,
    renderSection: PropTypes.func,
    renderLabelIcon: PropTypes.func,
    onClearSearchText: PropTypes.func,
    onChangeSearchText: PropTypes.func,
    onChangeCustomValue: PropTypes.func,
    onBlurCustomValueBox: PropTypes.func,
    renderDropdownHeader: PropTypes.func,
    sectionIndicatorColor: PropTypes.func,
  }

  static defaultProps = {
    errorText: '',
    onBlur: () => {},
    onChangeSearchText: noop,
    onChangeCustomValue: noop,
    onBlurCustomValueBox: noop,
  }

  onBlur = (e) => {
    const value = e.target.value
    const { name, onBlur: cb } = this.props

    cb(name, value)
  }

  onChange = (value, onCancelSelect) => {
    this.props.onChange(
      this.props.name, value, onCancelSelect
    )
  }

  onChangeSearchText = (value, onCancelSelect) => {
    this.props.onChangeSearchText(
      this.props.name, value, onCancelSelect
    )
  }

  onChangeCustomValue = (value, onCancelSelect) => {
    this.props.onChangeCustomValue(
      this.props.name, value, onCancelSelect
    )
  }

  onBlurCustomValueBox = (value, onCancelSelect) => {
    this.props.onBlurCustomValueBox(
      this.props.name, value, onCancelSelect
    )
  }

  render () {
    let {
      name,
      label,
      value,
      options,
      hasError,
      sections,
      className,
      optionType,
      tooltipText,
      isMultiple,
      isDisabled,
      hasTooltip,
      isSectioned,
      hasTags,
      hasValueTooltip,
      hasAutoScroll,
      hasEmptyValue,
      renderSection,
      hasSectionTitle,
      hasSectionSeparator,
      hasSectionIndicator,
      sectionIndicatorColor,
      hasDropdownHeader,
      renderDropdownHeader,
      isFetchingOptions,
      hasKeyboardSearch,
      hasKeyboardSearchText,
      hasCustomValueBox,
      renderIcon,
      placeholder,
      hasSearchBox,
      hasAllOption,
      hasNoneOption,
      renderLabelIcon,
      errorText,
      onExpand,
      onCollapse,
      onClearSearchText,
      onChangeSearchText,
      onChangeCustomValue,
      onBlurCustomValueBox,
      renderSelectedText,
      formatOption,
      formatOptionText,
      ...restProps
    } = this.props

    hasError = hasError || !!errorText

    return (
      <FormGroup
        className={cn(
          'SelectField',
          { 'SelectField_disabled': isDisabled },
          className
        )}
        data-testid={`${name}_field`}
        {...restProps}
      >
        {label && (
          <>
            <Label
              data-testid={`${name}_field-label`}
              className='SelectField-Label'
            >
              {label}
            </Label>
            {renderLabelIcon && renderLabelIcon()}
          </>
        )}
        <MultiSelectGroup
          name={name}
          value={value}
          options={options}
          sections={sections}
          optionType={optionType}
          isInvalid={!!hasError}
          isMultiple={isMultiple}
          isDisabled={isDisabled}
          hasTags={hasTags}
          hasTooltip={hasTooltip}
          isSectioned={isSectioned}
          hasValueTooltip={hasValueTooltip}
          hasEmptyValue={hasEmptyValue}
          hasAutoScroll={hasAutoScroll}
          hasKeyboardSearch={hasKeyboardSearch}
          hasKeyboardSearchText={hasKeyboardSearchText}
          hasCustomValueBox={hasCustomValueBox}
          hasSectionTitle={hasSectionTitle}
          hasSectionSeparator={hasSectionSeparator}
          hasSectionIndicator={hasSectionIndicator}
          sectionIndicatorColor={sectionIndicatorColor}
          hasDropdownHeader={hasDropdownHeader}
          renderDropdownHeader={renderDropdownHeader}
          isFetchingOptions={isFetchingOptions}
          hasSearchBox={hasSearchBox}
          hasAllOption={hasAllOption}
          hasNoneOption={hasNoneOption}
          tooltipText={tooltipText}
          placeholder={placeholder}
          className='SelectField-MultiSelect form-control'
          renderSection={renderSection}
          onBlur={this.onBlur}
          onChange={this.onChange}
          onExpand={onExpand}
          onCollapse={onCollapse}
          onClearSearchText={onClearSearchText}
          onChangeSearchText={this.onChangeSearchText}
          renderSelectedText={renderSelectedText}
          formatOptionText={formatOptionText}
          onChangeCustomValue={this.onChangeCustomValue}
          onBlurCustomValueBox={this.onBlurCustomValueBox}
        />
        {hasError ? (
          <div className='SelectField-Error'>
            {errorText}
          </div>
        ) : null}
        {renderIcon && renderIcon()}
      </FormGroup>
    )
  }
}