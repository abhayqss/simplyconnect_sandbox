import React, { memo, useCallback, useEffect, useMemo, useRef, useState } from "react";

import { any, compact, contains, filter, findWhere, flatten, map, noop, reject } from "underscore";

import $ from "jquery";
import "jquery.scrollto";

import cn from "classnames";
import PTypes from "prop-types";

import { UncontrolledTooltip as Tooltip } from "reactstrap";

import Highlighter from "react-highlight-words";

import { Loader } from "components";

import Tags from "./Tags/Tags";

import { allAreNotEmpty, containsIgnoreCase, isEmpty, isNotEmpty } from "lib/utils/Utils";

import { ReactComponent as Cross } from "images/cross.svg";
import { ReactComponent as Search } from "images/search.svg";
import { ReactComponent as TopChevron } from "images/chevron-top.svg";
import { ReactComponent as BottomChevron } from "images/chevron-bottom.svg";

import "./MultiSelect.scss";

const ALL = "ALL";
const NONE = "NONE";

const DEFAULT_OPTION_VALUE = null;
const DEFAULT_OPTION_TEXT = "Select";

const VALID_SEARCH_SYMBOLS = "abcdefghijklmnopqrstuvwxyz0123456789_+-(){}[]!@#$%^&* ";

const SECTION_INDICATOR_COLORS = ["#ffd3c0", "#fff1ca", "#d5f3b8", "#d1ebfe", "#e7ccfe", "#fec7ee"];

function isValidSearchSymbol(s) {
  return VALID_SEARCH_SYMBOLS.includes(s.toLowerCase());
}

const Option = memo(function Option({
  tag,
  type,
  text,
  value,
  data,

  style,
  tooltip,
  highlightedText,

  className,

  isSelected,
  isDisabled,
  hasSeparator,

  formatOptionText,

  onClick,
}) {
  /*
   * 'ref' is used in 'target' prop because
   * a string selector crashes UI
   * */
  const ref = useRef();

  const cb = useCallback(() => {
    if (!isDisabled) {
      onClick(isSelected, value);
    }
  }, [value, onClick, isDisabled, isSelected]);

  return (
    <>
      <div
        style={style}
        ref={ref}
        onClick={cb}
        data-value={value}
        data-testid={`${tag}_${value}-option`}
        className={cn(
          "MultiSelect-Option",
          { "MultiSelect-Option_selected": isSelected },
          { "MultiSelect-Option_disabled": isDisabled },
          type === "tick" ? "MultiSelect-Option_tick" : "MultiSelect-Option_checkbox",
          className,
        )}
      >
        {type === "checkbox" && (
          <div className="MultiSelect-Checkbox">
            {isSelected && <span className="MultiSelect-CheckMark" data-testid={`${tag}_${value}-option-сheck-mark`} />}
          </div>
        )}
        <div title={!tooltip ? text : null} className="MultiSelect-OptionText">
          {formatOptionText ? (
            formatOptionText({ text, value, data, highlightedText })
          ) : highlightedText ? (
            <Highlighter
              textToHighlight={text}
              searchWords={[highlightedText]}
              highlightClassName="MultiSelect-OptionHighlightedText"
            />
          ) : (
            text
          )}
          {type === "tick" && isSelected && (
            <span className="MultiSelect-Tick" data-testid={`${tag}_${value}-option-tick`} />
          )}
        </div>
      </div>
      {tooltip && ref.current && (
        <Tooltip
          target={ref.current}
          flip={false}
          modifiers={[
            {
              name: "offset",
              options: { offset: [0, 6] },
            },
            {
              name: "preventOverflow",
              options: { boundary: document.body },
            },
          ]}
        >
          {tooltip}
        </Tooltip>
      )}
      {hasSeparator && <div className="MultiSelect-OptionSeparator" />}
    </>
  );
});

Option.propTypes = {
  type: PTypes.oneOf(["checkbox", "tick", null]),

  tag: PTypes.string,
  text: PTypes.string,
  data: PTypes.object,
  tooltip: PTypes.string,
  numberOfLines: PTypes.number,
  highlightedText: PTypes.string,
  value: PTypes.oneOfType([PTypes.number, PTypes.string]),

  style: PTypes.object,
  className: PTypes.string,

  isDisabled: PTypes.bool,
  isSelected: PTypes.bool,
  hasSeparator: PTypes.bool,

  onClick: PTypes.func,
};

Option.defaultProps = {
  type: "checkbox",
  isDisabled: false,
  isSelected: false,
  text: DEFAULT_OPTION_TEXT,
  value: DEFAULT_OPTION_VALUE,
  onClick: noop,
};

const Options = memo(function ({
  tag,
  options,
  optionType,
  formatOptionText,
  highlightedText,
  selectedOptions,
  onSelectOption,
}) {
  return (
    <>
      {map(
        options,
        ({ text, value, data, tooltip, isSelected, isDisabled, hasSeparator, numberOfLines, className }) => {
          return (
            <Option
              tag={tag}
              key={value}
              text={text}
              value={value}
              data={data}
              type={optionType}
              tooltip={tooltip}
              hasSeparator={hasSeparator}
              numberOfLines={numberOfLines}
              highlightedText={highlightedText}
              onClick={onSelectOption}
              isSelected={isSelected || any(selectedOptions, (o) => o.value === value)}
              isDisabled={isDisabled}
              formatOptionText={formatOptionText}
              className={className}
            />
          );
        },
      )}
    </>
  );
});

const Section = memo(function Section({
  id,
  name,
  title,
  options,
  hasTitle,
  hasSearch,
  searchText,
  optionType,
  hasIndicator,
  hasSeparator,
  indicatorColor,
  onSelectOption,
}) {
  const filteredOptions = useMemo(
    () => (hasSearch ? filter(options, (o) => containsIgnoreCase(o.text, searchText)) : options),
    [hasSearch, searchText, options],
  );

  const defaultIndicatorColor = SECTION_INDICATOR_COLORS[id % 6];

  return (
    <div
      className="MultiSelect-Section"
      style={
        hasIndicator
          ? {
              borderLeftWidth: 8,
              borderLeftStyle: "solid",
              borderLeftColor: indicatorColor
                ? indicatorColor({ id, name }) || defaultIndicatorColor
                : defaultIndicatorColor,
            }
          : null
      }
    >
      {hasTitle && <div className="MultiSelect-SectionTitle">{title}</div>}
      {map(filteredOptions, ({ text, value, tooltip, isSelected, isDisabled, numberOfLines }, j) => (
        <Option
          key={String(value)}
          text={text}
          value={value}
          type={optionType}
          tooltip={tooltip}
          isSelected={isSelected}
          isDisabled={isDisabled}
          onClick={onSelectOption}
          highlightedText={searchText}
          numberOfLines={numberOfLines}
          hasSeparator={hasSeparator && j === filteredOptions.length - 1}
        />
      ))}
    </div>
  );
});

const Sections = memo(function ({
  value,
  sections,
  searchText,
  optionType,
  selectedOptions,
  sectionIndicatorColor,

  renderSection,

  hasSearch,
  hasSectionTitle,
  hasSectionSeparator,
  hasSectionIndicator,

  onChange,
  onSelectOption,
}) {
  return (
    <>
      {map(sections, ({ id, name, title, hasTitle, options }, i) => {
        const filteredOptions = hasSearch ? filter(options, (o) => containsIgnoreCase(o.text, searchText)) : options;

        return filteredOptions.length > 0 ? (
          renderSection ? (
            renderSection({
              id,
              name,
              title,
              hasTitle: hasTitle ?? hasSectionTitle,
              options,
              value,
              onChange,
              onSelectOption,
              index: i,
            })
          ) : (
            <Section
              id={id}
              key={id}
              name={name}
              title={title}
              optionType={optionType}
              hasTitle={hasTitle ?? hasSectionTitle}
              hasSeparator={hasSectionSeparator && i < sections.length - 1}
              hasIndicator={hasSectionIndicator}
              indicatorColor={sectionIndicatorColor}
              onSelectOption={onSelectOption}
              searchText={searchText}
              hasSearch={hasSearch}
              options={map(options, (o) => ({
                ...o,
                isSelected: any(selectedOptions, (so) => so.value === o.value),
              }))}
            />
          )
        ) : null;
      })}
    </>
  );
});

/*
 * <MultiSelect
 *   options={[
 *       {text: 'Apple', value: 0},
 *       {text: 'Strawberry', value: 1},
 *       {text: 'Banana', value: 2},
 *   ]}
 *   onChange={value => {}}
 * />
 * */

function MultiSelect({
  name,
  value,
  sections,
  tooltipText,
  defaultValue,

  className,

  isInvalid,
  isDisabled,
  isMultiple,
  hasValueTooltip,
  isAutoCollapsible,

  optionType,

  isSectioned,
  renderSection,
  hasSectionTitle,
  hasSectionSeparator,
  hasSectionIndicator,
  sectionIndicatorColor,
  renderSelectedText,
  formatOptionText,

  hasDropdownHeader,
  renderDropdownHeader,

  isFetchingOptions,

  hasAutoScroll,

  hasTags,

  hasSearchBox,
  hasSearchIcon,
  hasKeyboardSearch,
  hasKeyboardSearchText,

  hasCustomValueBox,
  onBlurCustomValueBox,
  onChangeCustomValue,

  hasAllOption,
  hasNoneOption,

  hasEmptyValue,

  placeholder,

  onBlur,
  onChange,
  onExpand,
  onCollapse,
  onClearSearchText,
  onChangeSearchText,

  ...props
}) {
  const ref = useRef();
  const searchInputRef = useRef();
  const customValueInputRef = useRef();

  const optionsRef = useRef();

  const options = useMemo(
    () => (isSectioned ? flatten(map(sections, (o) => o.options)) : props.options),
    [sections, isSectioned, props.options],
  );

  const defaultSelected = useMemo(
    () => (isMultiple ? filter(options, (o) => contains(value, o.value)) : compact([findWhere(options, { value })])),
    [value, options, isMultiple],
  );

  const [selected, setSelected] = useState(isNotEmpty(defaultValue) ? defaultValue : defaultSelected);

  const [prevSelected, setPrevSelected] = useState([]);

  const [isExpanded, setExpanded] = useState(false);

  const defaultSearchText = useMemo(
    () =>
      !isMultiple && hasSearchBox && allAreNotEmpty(value, options)
        ? map(defaultSelected, (o) => o.text).join(", ")
        : "",
    [value, options, defaultSelected, isMultiple, hasSearchBox],
  );

  const [searchText, setSearchText] = useState(defaultSearchText);

  const [isNoneOptionSelected, setNoneOptionSelected] = useState(false);

  const Chevron = isExpanded ? TopChevron : BottomChevron;

  const areAllSelected = options.length > 1 && defaultSelected.length === options.length;

  const filteredOptions = useMemo(
    () =>
      hasKeyboardSearch || hasSearchBox ? filter(options, (o) => containsIgnoreCase(o.text, searchText)) : options,
    [options, searchText, hasSearchBox, hasKeyboardSearch],
  );

  /*const valueTooltipText = useMemo(() => (
        (
            map(
                filter(
                    filteredOptions,
                    o => any(selected, so => so.value === o.value)
                ), o => o.text
            ) || []
        ).join(', ')
    ), [selected, filteredOptions])*/

  const selectedText = useMemo(
    () =>
      hasTags && isMultiple && hasSearchBox
        ? ""
        : defaultSelected.length
          ? hasAllOption && areAllSelected
            ? "All"
            : map(defaultSelected, (o) => o.text).join(", ")
          : hasNoneOption && options.length > 1 && isNoneOptionSelected
            ? "None"
            : "",
    [
      hasTags,
      options,
      isMultiple,
      hasSearchBox,
      hasAllOption,
      hasNoneOption,
      areAllSelected,
      defaultSelected,
      isNoneOptionSelected,
    ],
  );

  const defaultCustomValue = useMemo(() => {
    return !isMultiple && hasCustomValueBox
      ? isNotEmpty(value)
        ? isEmpty(defaultSelected)
          ? value
          : selectedText
        : ""
      : "";
  }, [value, isMultiple, selectedText, defaultSelected, hasCustomValueBox]);

  const [customValue, setCustomValue] = useState(defaultCustomValue);

  const focusSearchInput = useCallback(() => {
    searchInputRef.current?.focus() || noop();
  }, []);

  const onMouseEvent = useCallback(
    (e) => {
      if (isExpanded) {
        const shouldCollapse = !(ref.current?.contains(e.target) || searchInputRef.current?.contains(e.target));

        if (shouldCollapse) {
          onCollapse();
          setExpanded(false);
          setSearchText(hasSearchBox ? searchText : "");
        }
      }
    },
    [searchText, isExpanded, onCollapse, hasSearchBox],
  );

  const onKeydownEvent = useCallback(
    (e) => {
      if (isExpanded) {
        const isValid = isValidSearchSymbol(e.key);

        if (hasSearchBox && isMultiple && selectedText) {
          if (isValid) {
            e.preventDefault();
            setSearchText(searchText + e.key);
          }

          if (e.keyCode === 8) {
            setSearchText(searchText.slice(0, -1));
          }
        }

        if (hasKeyboardSearch) {
          if (hasKeyboardSearchText) {
            if (isValid && !searchText && selectedText) {
              setSearchText(e.key);
            }
          } else {
            if (isValid) {
              e.preventDefault();
              setSearchText(searchText + e.key);
            }

            if (e.keyCode === 8) {
              setSearchText(searchText.slice(0, -1));
            }
          }
        }
      }
    },
    [searchText, isExpanded, isMultiple, selectedText, hasSearchBox, hasKeyboardSearch, hasKeyboardSearchText],
  );

  const onBlurToggle = useCallback(
    (e) => {
      e.preventDefault();
      onBlur(e);
    },
    [onBlur],
  );

  const onToggle = useCallback(
    (e) => {
      e.preventDefault();
      e.stopPropagation();

      const shouldToggle = !(isDisabled || (isExpanded && searchInputRef.current?.contains(e.target)));

      if (shouldToggle) {
        if (isExpanded) {
          onCollapse();
        } else onExpand();

        setExpanded(!isExpanded);
      }
    },
    [onExpand, isExpanded, isDisabled, onCollapse],
  );

  const onChangeSearchInput = useCallback(
    (e) => {
      e.stopPropagation();

      const val = e.target.value;

      setExpanded(true);
      setSearchText(val);

      onChangeSearchText(val);
    },
    [onChangeSearchText],
  );

  const onChangeCustomValueInput = useCallback(
    (e) => {
      e.stopPropagation();

      const val = e.target.value;

      setExpanded(true);
      setCustomValue(val);

      onChangeCustomValue(val);
    },
    [onChangeCustomValue],
  );

  const onBlurCustomValueInput = useCallback(
    (e) => {
      const val = e.target.value;

      setCustomValue(val);

      onBlurCustomValueBox(customValue);
    },
    [customValue, onBlurCustomValueBox],
  );

  const onClearSearchInput = useCallback(
    (e) => {
      e.stopPropagation();

      setSearchText("");
      setExpanded(false);

      onChange(isMultiple ? [] : null);
      if (onClearSearchText) onClearSearchText();
    },
    [onChange, isMultiple, onClearSearchText],
  );

  const onCancelSelect = useCallback(() => {
    setSelected(prevSelected);
  }, [prevSelected]);

  const onSelectOption = useCallback(
    (isSelected, val) => {
      setSearchText("");
      setCustomValue("");

      if (isMultiple) {
        if (hasAllOption && val === ALL) {
          const nextSelected = isSelected
            ? []
            : map(options, (o) => ({
                ...o,
                isSelected: true,
              }));

          setSelected(nextSelected);
          setPrevSelected(selected);
          setNoneOptionSelected(!nextSelected.length);

          onChange(
            map(nextSelected, (o) => o.value),
            onCancelSelect,
          );
        } else if (hasNoneOption && val === NONE) {
          setSelected([]);
          setPrevSelected(selected);
          setNoneOptionSelected(!isSelected);

          onChange([], onCancelSelect);
        } else {
          const option = findWhere(options, { value: val });

          const nextSelected = isSelected
            ? reject(selected, (o) => o.value === option.value)
            : [...selected, { ...option, isSelected: true }];

          setSelected(nextSelected);
          setPrevSelected(selected);
          setNoneOptionSelected(!nextSelected.length);

          onChange(
            map(nextSelected, (o) => o.value),
            onCancelSelect,
          );
        }
      } else if (hasNoneOption && val === NONE) {
        setSelected([]);
        setPrevSelected(selected);
        setNoneOptionSelected(!isSelected);

        onChange(null, onCancelSelect);
      } else {
        const option = findWhere(options, { value: val });
        const isEmptyValue = isSelected && hasEmptyValue;

        const nextSelected = isEmptyValue ? [] : [{ ...option, isSelected: true }];

        setPrevSelected(selected);
        setSelected(nextSelected);
        setExpanded(isSelected && isAutoCollapsible);

        onChange(isEmptyValue ? null : val, onCancelSelect);
      }
    },
    [
      options,
      selected,
      onChange,
      isMultiple,
      hasAllOption,
      hasNoneOption,
      hasEmptyValue,
      onCancelSelect,
      isAutoCollapsible,
    ],
  );

  const onRemoveTag = useCallback(
    (value) => {
      onSelectOption(true, value);
    },
    [onSelectOption],
  );

  /*
   * To prevent the issue in time
   * of the form auto-filling
   * */
  const onChangeInput = useCallback((e) => {
    e.stopPropagation();
  }, []);

  useEffect(() => {
    if (hasKeyboardSearchText && searchText.length === 1) {
      focusSearchInput();
    }
  }, [searchText, focusSearchInput, hasKeyboardSearchText]);

  useEffect(() => {
    setSelected(defaultSelected);

    if (hasTags && isMultiple && hasSearchBox && defaultSelected.length > 0) {
      focusSearchInput();
    }
  }, [hasTags, isMultiple, hasSearchBox, defaultSelected, focusSearchInput]);

  useEffect(() => {
    if (isNotEmpty(defaultValue)) {
      onChange(isMultiple ? map(defaultValue, (o) => o.value) : defaultValue);
    }
  }, [onChange, isMultiple, defaultValue]);

  useEffect(() => {
    if (isExpanded && hasAutoScroll) {
      const $options = $(optionsRef.current);

      const $option = $options.find(`.MultiSelect-Option_selected`);

      if ($option) {
        $options.scrollTo($option, 500, { offset: -100 });
      }
    }
  }, [isExpanded, hasAutoScroll]);

  useEffect(() => {
    if (hasKeyboardSearch && !isExpanded) {
      setSearchText("");
    }
  }, [isExpanded, hasKeyboardSearch]);

  useEffect(() => {
    if (isExpanded && isEmpty(value) && (hasCustomValueBox || hasKeyboardSearchText)) {
      focusSearchInput();
    }
  }, [value, isExpanded, focusSearchInput, hasCustomValueBox, hasKeyboardSearchText]);

  useEffect(() => {
    if (hasCustomValueBox) {
      setCustomValue(isEmpty(defaultSelected) ? value ?? "" : "");
    }
  }, [value, defaultSelected, hasCustomValueBox]);

  useEffect(() => {
    document.addEventListener("mousedown", onMouseEvent);
    return () => document.removeEventListener("mousedown", onMouseEvent);
  }, [onMouseEvent]);

  useEffect(() => {
    document.addEventListener("keydown", onKeydownEvent);
    return () => document.removeEventListener("keydown", onKeydownEvent);
  }, [onKeydownEvent]);

  return (
    <div className="MultiSelect-Container" data-testid={`${name}_multi-select`}>
      {hasTags && isMultiple && (
        <Tags name={name} isDisabled={isDisabled} items={defaultSelected} onRemove={onRemoveTag} />
      )}
      <div
        ref={ref}
        className={cn(
          "MultiSelect",
          className,
          isInvalid && "is-invalid",
          isDisabled && "MultiSelect_disabled",
          isExpanded ? "MultiSelect_expanded" : "MultiSelect_collapsed",
          isMultiple && isDisabled && hasSearchBox && "MultiSelect_hidden",
        )}
        style={hasSearchBox ? { paddingRight: 30 } : {}}
      >
        <input name={name} onChange={onChangeInput} className="MultiSelect-Input" />
        {hasSearchBox ? (
          <>
            {searchText || !selectedText ? (
              <input
                ref={searchInputRef}
                className="MultiSelect-SearchInput"
                type="text"
                value={selectedText || searchText}
                disabled={isDisabled}
                placeholder={placeholder}
                onClick={onToggle}
                onChange={onChangeSearchInput}
                data-testid={`${name || "multi-select"}_search-input`}
              />
            ) : (
              <div
                onClick={onToggle}
                className={cn(
                  "MultiSelect-SelectedText",
                  isEmpty(defaultSelected) && "MultiSelect-SelectedText_placeholder",
                )}
                data-testid={`${name || "multi-select"}_selected-text`}
              >
                {isNotEmpty(defaultSelected) ? selectedText : placeholder}
              </div>
            )}
            {hasSearchIcon &&
              (searchText || isNotEmpty(value) ? (
                <Cross className="MultiSelect-CrossBtn" onClick={onClearSearchInput} />
              ) : (
                hasSearchIcon && <Search className="MultiSelect-SearchBtn" />
              ))}
          </>
        ) : (
          <button
            type="button"
            id={`MultiSelect_Toggle__${name}`}
            className="MultiSelect-Toggle"
            onBlur={onBlurToggle}
            data-testid={`${name || "multi-select"}_toggle`}
          >
            {(() => {
              if (hasCustomValueBox && isExpanded) {
                return (
                  <input
                    ref={customValueInputRef}
                    type="text"
                    value={customValue}
                    placeholder={placeholder}
                    onClick={onToggle}
                    onChange={onChangeCustomValueInput}
                    disabled={isDisabled}
                    onBlur={onBlurCustomValueInput}
                    className="MultiSelect-CustomValueInput"
                  />
                );
              } else if (hasKeyboardSearchText && (searchText || !selectedText)) {
                return (
                  <input
                    ref={searchInputRef}
                    type="text"
                    value={searchText}
                    placeholder={placeholder}
                    onClick={onToggle}
                    onChange={onChangeSearchInput}
                    disabled={isDisabled}
                    className="MultiSelect-KeyboardSearchInput"
                    data-testid={`${name || "multi-select"}_keyboard-search-input`}
                  />
                );
              }

              return (
                <div
                  onClick={onToggle}
                  className={cn(
                    "MultiSelect-SelectedText",
                    isEmpty(defaultSelected) && "MultiSelect-SelectedText_placeholder",
                  )}
                  data-testid={`${name || "multi-select"}_selected-text`}
                >
                  {isNotEmpty(defaultSelected)
                    ? renderSelectedText
                      ? renderSelectedText(selectedText)
                      : selectedText
                    : placeholder}
                </div>
              );
            })()}
            <div style={{ lineHeight: 1 }} onClick={onToggle}>
              <Chevron className="MultiSelect-ToggleChevron" />
            </div>
          </button>
        )}
        <div ref={optionsRef} className="MultiSelect-Options" data-testid={`${name || "multi-select"}_options`}>
          {isFetchingOptions && <Loader hasBackdrop />}
          {hasDropdownHeader && renderDropdownHeader()}
          {isMultiple && hasAllOption && options.length > 1 && (
            <Option
              text="All"
              value="ALL"
              type={optionType}
              onClick={onSelectOption}
              className="MultiSelect-AllOption"
              isSelected={areAllSelected}
              style={
                hasSectionIndicator
                  ? {
                      borderLeft: "8px solid white",
                    }
                  : null
              }
            />
          )}
          {isSectioned ? (
            <Sections
              value={value}
              sections={sections}
              optionType={optionType}
              searchText={searchText}
              selectedOptions={selected}
              renderSection={renderSection}
              sectionIndicatorColor={sectionIndicatorColor}
              hasSearch={hasSearchBox || hasKeyboardSearch}
              hasSectionIndicator={hasSectionIndicator}
              hasSectionSeparator={hasSectionSeparator}
              hasSectionTitle={hasSectionTitle}
              onChange={onChange}
              onSelectOption={onSelectOption}
            />
          ) : (
            <Options
              tag={name}
              optionType={optionType}
              options={filteredOptions}
              selectedOptions={selected}
              highlightedText={searchText}
              formatOptionText={formatOptionText}
              onSelectOption={onSelectOption}
            />
          )}
          {isMultiple && hasNoneOption && options.length > 1 && (
            <Option
              text="None"
              value="NONE"
              type={optionType}
              className="MultiSelect-NoneOption"
              onClick={onSelectOption}
              isSelected={isNoneOptionSelected}
              style={
                hasSectionIndicator
                  ? {
                      borderLeft: "8px solid white",
                    }
                  : null
              }
            />
          )}
        </div>
      </div>
    </div>
  );
}

export default memo(MultiSelect);

MultiSelect.propTypes = {
  name: PTypes.string,
  value: PTypes.oneOfType([PTypes.array, PTypes.number, PTypes.string]),
  defaultValue: PTypes.oneOfType([PTypes.array, PTypes.number, PTypes.string]),
  options: PTypes.array,
  sections: PTypes.array,

  optionType: PTypes.oneOf(["checkbox", "tick"]),

  isInvalid: PTypes.bool,
  isMultiple: PTypes.bool,
  isDisabled: PTypes.bool,
  hasEmptyValue: PTypes.bool,
  hasAutoScroll: PTypes.bool,
  hasValueTooltip: PTypes.bool,
  isAutoCollapsible: PTypes.bool,
  isFetchingOptions: PTypes.bool,

  isSectioned: PTypes.bool,
  hasSectionTitle: PTypes.bool,
  hasSectionSeparator: PTypes.bool,
  hasSectionIndicator: PTypes.bool,
  sectionIndicatorColor: PTypes.func,

  hasDropdownHeader: PTypes.bool,
  renderDropdownHeader: PTypes.func,

  hasTags: PTypes.bool,

  hasSearchBox: PTypes.bool,
  hasSearchIcon: PTypes.bool,
  hasKeyboardSearch: PTypes.bool,
  hasKeyboardSearchText: PTypes.bool,

  hasCustomValueBox: PTypes.bool,

  hasAllOption: PTypes.bool,
  hasNoneOption: PTypes.bool,

  formatOptionText: PTypes.func,

  tooltipText: PTypes.string,
  className: PTypes.string,
  placeholder: PTypes.string,
  onBlur: PTypes.func,
  onChange: PTypes.func,
  onExpand: PTypes.func,
  onCollapse: PTypes.func,
  renderSection: PTypes.func,
  onClearSearchText: PTypes.func,
  onChangeSearchText: PTypes.func,
  onBlurCustomValueBox: PTypes.func,
  onChangeCustomValue: PTypes.func,
};

MultiSelect.defaultProps = {
  value: [],
  options: [],
  sections: [],
  isInvalid: false,
  isDisabled: false,
  hasEmptyValue: true,
  hasAutoScroll: true,
  hasValueTooltip: false,
  isAutoCollapsible: true,
  isFetchingOptions: false,

  isSectioned: false,
  hasSectionTitle: true,
  hasSectionSeparator: false,
  hasSectionIndicator: false,

  hasTags: false,

  hasSearchBox: false,
  hasSearchIcon: true,
  hasKeyboardSearch: false,
  hasKeyboardSearchText: false,

  hasDropdownHeader: false,
  renderDropdownHeader: noop,

  hasNoneOption: false,
  hasAllOption: true,
  placeholder: "Select",

  onBlur: noop,
  onChange: noop,
  onExpand: noop,
  onCollapse: noop,
  onChangeSearchText: noop,
  onChangeCustomValue: noop,
  onBlurCustomValueBox: noop,
};
