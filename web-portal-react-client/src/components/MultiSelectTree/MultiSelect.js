import React, { memo, useCallback, useEffect, useMemo, useRef, useState } from "react";

import { compact, contains, filter, findWhere, flatten, map, noop, reject } from "underscore";

import $ from "jquery";
import "jquery.scrollto";

import cn from "classnames";
import PTypes from "prop-types";

import { UncontrolledTooltip as Tooltip } from "reactstrap";

import Highlighter from "react-highlight-words";

import { FileFormatIcon, Loader } from "components";

import Tags from "./Tags/Tags";

import { allAreNotEmpty, isEmpty, isNotEmpty } from "lib/utils/Utils";

import { ReactComponent as TopChevron } from "images/chevron-top.svg";
import { ReactComponent as BottomChevron } from "images/chevron-bottom.svg";
import { ReactComponent as RightChevron } from "images/chevron-right.svg";

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
  isFolder,
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
    if (isFolder) {
      this.options = ["1233", "2334"];
    } else if (!isDisabled) onClick(isSelected, value);
  }, [value, onClick, isDisabled, isSelected, isFolder]);

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
  type: PTypes.oneOf(["checkbox", "tick", "folder", null]),

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
  isFolder: PTypes.bool,

  onClick: PTypes.func,
};

Option.defaultProps = {
  type: "checkbox",
  isDisabled: false,
  isSelected: false,
  isFolder: false,
  text: DEFAULT_OPTION_TEXT,
  value: DEFAULT_OPTION_VALUE,
  onClick: noop,
};

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

function MultiSelectTree({
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
  isShowFolderIcon,
  isShowRightChevron,
  ...props
}) {
  const ref = useRef();
  const searchInputRef = useRef();

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
  const [optionsList, setOptionsList] = useState(props.options); // starting value
  const [selectFolderOptionsList, setSelectFolderOptionsList] = useState([]);
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [selectedOptionsId, setSelectedOptionsId] = useState([]);
  const [selectedOptionsTitle, setSelectedOptionsTitle] = useState([]);
  const [expandedFoldersName, setExpandedFoldersName] = useState([]);

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

  const onSelectFolder = (index) => {
    setOptionsList(optionsList.folders[index]);
    const array1 = selectFolderOptionsList;
    array1.push(optionsList.folders[index]);
    setSelectFolderOptionsList(array1);
    let array = expandedFoldersName;
    array.push({ title: optionsList.folders[index].title, folderId: optionsList.folders[index].folderId ?? 0 });
    setExpandedFoldersName(array); // folder name
  };

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

  const handleOptionToggle = (option) => {
    const isOptionSelected = selectedOptions.includes(option);
    if (isMultiple) {
      // isMultiple
      if (isOptionSelected) {
        setSelectedOptions(selectedOptions.filter((selected) => selected !== option));
        setSelectedOptionsId(selectedOptionsId.filter((selected) => selected !== option.id));
        setSelectedOptionsTitle(selectedOptionsTitle.filter((selected) => selected !== option.title));
        onChange(selectedOptionsId.filter((selected) => selected !== option.id));
      } else {
        setSelectedOptions([...selectedOptions, option]);
        setSelectedOptionsId([...selectedOptionsId, option.id]);
        setSelectedOptionsTitle([...selectedOptionsTitle, option.title]);
        onChange([...selectedOptionsId, option.id]);
      }
    } else {
      // single selection
      if (isOptionSelected) {
        return;
      } else {
        setSelectedOptions([option]);
        setSelectedOptionsId([option.id]);
        setSelectedOptionsTitle([option.title]);
        onChange([option.id]);
      }
    }
  };

  const clickGoBack = () => {
    if (expandedFoldersName.length === 0) {
      return;
    }
    if (expandedFoldersName.length === 1) {
      setOptionsList(options);
      setExpandedFoldersName([]);
      setSelectFolderOptionsList([]);
    } else {
      findFolder(options, optionsList.folderId);
      setSelectFolderOptionsList(selectFolderOptionsList.slice(0, -1));
      setOptionsList(selectFolderOptionsList[selectFolderOptionsList.length - 2]);
      setExpandedFoldersName(expandedFoldersName.slice(0, -1));
    }
  };

  //  递归
  const findFolder = (data, folderId) => {
    if (folderId === null || 0) {
      return data;
    }
    if (data.folders) {
      for (let i = 0; i < data.folders.length; i++) {
        if (data.folders[i].folderId === folderId) {
          return data.folders[i];
        } else {
          let result = findFolder(data.folders[i], folderId);
          if (result !== null) {
            return result;
          }
        }
      }
    }
    return null;
  };
  useEffect(() => {
    setOptionsList(options);
    setExpandedFoldersName([]);
    // eslint-disable-next-line
  }, [isExpanded]);
  useEffect(() => {
    if (value?.length === 0) {
      setSelectFolderOptionsList([]);
      setSelectedOptions([]);
      setSelectedOptionsId([]);
      setSelectedOptionsTitle([]);
      setExpandedFoldersName([]);
    }
  }, [value]);

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
        {/* no search */}
        <button
          type="button"
          id={`MultiSelect_Toggle__${name}`}
          className="MultiSelect-Toggle"
          onBlur={onBlurToggle}
          onClick={onToggle}
          data-testid={`${name || "multi-select"}_toggle`}
        >
          <div className={"selected-options-text"} data-testid={`${name || "multi-select"}_selected-text`}>
            {selectedOptionsTitle.length === 0 ? (
              <div style={{ color: "#909090" }}>Select</div>
            ) : (
              selectedOptionsTitle.join(", ")
            )}
          </div>
          <div style={{ lineHeight: 1 }} onClick={onToggle}>
            <Chevron className="MultiSelect-ToggleChevron" />
          </div>
        </button>

        <div ref={optionsRef} className="MultiSelect-Options" data-testid={`${name || "multi-select"}_options`}>
          {isFetchingOptions && <Loader hasBackdrop />}
          {expandedFoldersName.length > 0 && (
            <div className={"MultiSelect-Directory-Top"}>
              <div className={"MultiSelect-Directory-Back"} onClick={clickGoBack}>
                Go Back
              </div>
              {expandedFoldersName.map((item, index) => {
                return (
                  <div
                    key={index}
                    className={
                      expandedFoldersName.length === index + 1
                        ? "MultiSelect-Directory-List MultiSelect-Directory-Current"
                        : "MultiSelect-Directory-List"
                    }
                  >
                    {item.title}
                  </div>
                );
              })}
            </div>
          )}
          {hasDropdownHeader && renderDropdownHeader()}

          {/*  Drop-down lists of folders and files */}
          {map(optionsList.folders ?? [], ({ title, folderId }, index) => {
            return (
              <div
                className={"MultiSelect-Folder"}
                key={folderId + index + "-folder"}
                id={folderId + "-folder-" + index}
                onClick={() => onSelectFolder(index)}
              >
                {isShowFolderIcon && <FileFormatIcon format={"FOLDER"} className={"MultiSelect-Directory-Img"} />}
                <option className={"MultiSelect-Directory-Img-Title"}>{title}</option>
                {isShowRightChevron && <RightChevron className={"MultiSelect-ToggleChevron"} />}
              </div>
            );
          })}
          {map(optionsList.templates ?? [], (item, index) => {
            const isChecked = selectedOptions.includes(item);
            return (
              <div
                className={"MultiSelect-Template"}
                onClick={(e) => {
                  e.stopPropagation();
                  handleOptionToggle(item);
                }}
              >
                <label
                  key={item.id}
                  className={"MultiSelect-Template-Label"}
                  onClick={(e) => {
                    handleOptionToggle(item);
                    e.stopPropagation();
                  }}
                >
                  <div className={"MultiSelect-Template-Box"}>
                    {isChecked && <div className={"MultiSelect-Template-Box-Icon"}></div>}
                  </div>
                  <span className={"MultiSelect-Template-Text"} onClick={(e) => handleOptionToggle(item)}>
                    {item.title}
                  </span>
                </label>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

export default memo(MultiSelectTree);

MultiSelectTree.propTypes = {
  name: PTypes.string,
  value: PTypes.oneOfType([PTypes.array, PTypes.number, PTypes.string]),
  defaultValue: PTypes.oneOfType([PTypes.array, PTypes.number, PTypes.string]),
  options: PTypes.array,
  sections: PTypes.array,

  optionType: PTypes.oneOf(["checkbox", "tick", "folder"]),

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

MultiSelectTree.defaultProps = {
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
