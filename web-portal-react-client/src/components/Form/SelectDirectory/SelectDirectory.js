import React, {useEffect, useState} from 'react';
import {FormGroup} from "reactstrap";
import {map} from "lodash";
import { FileFormatIcon } from 'components'
import cn from "classnames";
import './SelectDirectory.scss'
import { ReactComponent as TopChevron } from 'images/chevron-top.svg'
import { ReactComponent as BottomChevron } from 'images/chevron-bottom.svg'
import {Label} from "recharts";

const SelectDirectory = (props) => {
    const {options,
        renderLabelIcon,
        name,
        className,
        value,
        onChange,
        hasError = hasError || !!errorText,
        errorText,
        label,
        ...restProps
    } = props
    const [optionsList, setOptionsList] = useState(options)
    const [expandedFoldersName, setExpandedFoldersName] = useState([]);
    const [isExpanded, setIsExpanded] = useState(false)
    const Chevron = isExpanded ? TopChevron : BottomChevron
    const [selectedOptions, setSelectedOptions] = useState([]);
    const [selectedOptionsId, setSelectedOptionsId] = useState([]);
    const [selectedOptionsTitle, setSelectedOptionsTitle] = useState([]);

  useEffect(()=>{
        const newArrayId = selectedOptions.map(item => item.id)
        setSelectedOptionsId(newArrayId)
        const newArrayName = selectedOptions.map(item => item.title)
        setSelectedOptionsTitle(newArrayName)
    },[selectedOptions])

    const onSelectFolder =(index) =>{
            setOptionsList(optionsList.folders[index])
            let array = expandedFoldersName
            array.push({title:optionsList.folders[index].title,folderId:optionsList.folders[index].folderId?? 0})
            setExpandedFoldersName(array)  // folder name
    }

    const toggleDropdown = (e) => {
        e.preventDefault()
        e.stopPropagation()
        setIsExpanded(!isExpanded);
    };

    const onBlur =(e) =>{
        e.preventDefault()
        e.stopPropagation()
        setIsExpanded(!isExpanded);
    }

    const handleOptionToggle = (option) => {
        const isOptionSelected = selectedOptions.includes(option);
        if (isOptionSelected) {
            setSelectedOptions(selectedOptions.filter((selected) => selected !== option));
            setSelectedOptionsId(selectedOptionsId.filter((selected)=>selected!==option.id))
            setSelectedOptionsTitle(selectedOptionsTitle.filter((selected)=>selected!==option.title))
            onChange(selectedOptionsId.filter((selected) => selected !== option.id))
        } else {
            setSelectedOptions([...selectedOptions, option]);
            setSelectedOptionsId([...selectedOptionsId, option.id])
            setSelectedOptionsTitle([...selectedOptionsTitle, option.title])
            onChange([...selectedOptionsId, option.id])
        }
    };

    const clickGoBack = () =>{
        setOptionsList(options)
        setExpandedFoldersName([])
    }

    useEffect(()=>{
        setOptionsList(options)
        setExpandedFoldersName([])
    },[isExpanded])

    return (
        <FormGroup
            className={cn(
                'SelectField',
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
                    <div
                        className='SelectField-Label'
                        data-testid={`${name}_field-label`}
                    >
                        {label}
                    </div>
            {/* select multi*/}
            <div className="multi-select-dropdown"
                 data-testid={`${name}_multi-select`}
                 id={`MultiSelect_Toggle__${name}`}
            >
                <div className="selected-options"
                     onClick={toggleDropdown}
                     data-testid={`${name || 'multi-select'}_toggle`}
                     id={`MultiSelect_Toggle__${name}`}
                >
                    <div className={"selected-options-text"}
                         data-testid={`${name || 'multi-select'}_selected-text`}
                    >
                        {selectedOptionsTitle.length === 0
                            ? 'Select'
                            : selectedOptionsTitle.join(', ')}
                    </div>
                    <div
                        style={{ lineHeight: 1 }}
                        onClick={toggleDropdown}
                    >
                        <Chevron className='MultiSelect-ToggleChevron'/>
                    </div>
                </div>
                {isExpanded && (
                    <>
                    <div className={"MultiSelect-Drop-Down"}>
                        <div className={'MultiSelect-Directory-Top'}>
                            <div className={'MultiSelect-Directory-Back'} onClick={clickGoBack}>Go Back</div>
                            {
                                expandedFoldersName.map((item, index) => {
                                    return (
                                        <div key={item.folderId}
                                             className={expandedFoldersName.length === index + 1 ? 'MultiSelect-Directory-List MultiSelect-Directory-Current' : 'MultiSelect-Directory-List'}>
                                            {item.title}
                                        </div>
                                    )
                                })
                            }
                        </div>
                        <div className={'MultiSelect-Directory-bottom'}
                             data-testid={`${name || 'multi-select'}_options`}
                        >
                            { map(optionsList.folders??[],
                                ({
                                                                title,
                                                                folderId,
                                                            },index) => {
                                return (
                                    <div className={'MultiSelect-Folder'}  key={folderId+'-folder'}
                                         id={folderId+'-folder-'+index}
                                         onClick={()=>onSelectFolder(index)}>
                                        <FileFormatIcon format={'FOLDER'} className={'MultiSelect-Directory-Img'}/>
                                        <option className={'MultiSelect-Directory-Img-Title'}
                                        >
                                            {title}
                                        </option>
                                    </div>

                                )
                            })}
                            { map(optionsList.templates?? [], (item,index) => {
                                const isChecked = selectedOptions.includes(item)
                                return (
                                    <div className={'MultiSelect-Template'}
                                         onClick={(e) => {
                                             e.stopPropagation()
                                             handleOptionToggle(item)
                                         }}>
                                        <label key={item.id} className={'MultiSelect-Template-Label'}
                                               onClick={(e) => {
                                                   handleOptionToggle(item);
                                                   e.stopPropagation()}}
                                        >
                                           <div className={'MultiSelect-Template-Box'}>
                                               {
                                                   isChecked && <div className={'MultiSelect-Template-Box-Icon'}></div>
                                               }
                                           </div>
                                            <span className={'MultiSelect-Template-Text'}
                                                  onClick={(e) => handleOptionToggle(item)} >
                                                {item.title}
                                            </span>
                                        </label>
                                    </div>
                                )
                            })}
                        </div>
                     </div>
                        <div className={"MultiSelect-Drop-Down-Mask"} onClick={onBlur}>
                        </div>

                    </>
                )}
            </div>
            {hasError ? (
                <div className='SelectField-Error'>
                    {errorText}
                </div>
            ) : null}
            </FormGroup>
    );
};

export default SelectDirectory;
