import React, { Component } from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { map } from 'underscore'

import {
    DropdownItem,
    DropdownMenu,
    DropdownToggle,
    Dropdown as BootstrapDropdown,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { ReactComponent as Bookmark } from 'images/bookmark.svg'
import { ReactComponent as TopChevron } from 'images/chevron-top.svg'
import { ReactComponent as BottomChevron } from 'images/chevron-bottom.svg'

import './Dropdown.scss'

class Item extends Component {
    static propTypes = {
        isActive: PTypes.bool,
        isDisabled: PTypes.bool,

        onClick: PTypes.func,
        className: PTypes.string,

        toggle: PTypes.bool,
        hasSeparator: PTypes.bool,
        hasIndicator: PTypes.bool,
        renderIndicator: PTypes.func,
        indicatorClassName: PTypes.string,
        indicatorIconClassName: PTypes.string
    }

    static defaultProps = {
        isActive: false,
        hasSeparator: false,
        hasIndicator: false,
        onClick: () => {}
    }

    onClick = () => {
        this.props.onClick(
            this.props.value
        )
    }

    render () {
        const {
            toggle,
            children,
            isActive,
            className,
            isDisabled,
            hasSeparator,
            hasIndicator,
            renderIndicator,
            indicatorClassName,
            indicatorIconClassName
        } = this.props

        return (
            <DropdownItem
                toggle={toggle}
                active={isActive}
                className={cn(
                    'Dropdown-Item',
                    { 'Dropdown-Item_disabled': isDisabled },
                    { 'Dropdown-Item_has_separator': hasSeparator },
                    className
                )}
                onClick={this.onClick}
                disabled={isDisabled}
            >
                <>
                    {hasIndicator && (
                        renderIndicator ? renderIndicator(indicatorClassName) : (
                            <div className={cn('Dropdown-ItemIndicator', indicatorClassName)}>
                                <Bookmark className={cn('Dropdown-ItemIndicatorIcon', indicatorIconClassName)}/>
                            </div>
                        )
                    )}
                    {children}
                </>
            </DropdownItem>
        );
    }
}

/*
* @items = [
*   {
*       value,
*       text,
*       onClick,
*       className,
*       hasIndicator,
*       renderIndicator,
*       indicatorClassName,
*       indicatorIconClassName
*   },
*   ...
* ]
* */
export default class Dropdown extends Component {
    static propTypes = {
        id: PTypes.oneOfType([PTypes.number, PTypes.string]),
        hasTip: PTypes.bool,
        renderTip: PTypes.func,
        tipText: PTypes.string,
        tipPlace: PTypes.string,
        tipTrigger: PTypes.string,
        tipClassName: PTypes.string,
        items: PTypes.array,
        onToggle: PTypes.func,
        isDisabled: PTypes.bool,
        toggleText: PTypes.string,
        isOpenByDefault: PTypes.bool,
        className: PTypes.string
    }

    static defaultProps = {
        hasTip: false,
        toggleText: "Select",
        tipText: '',
        tipPlace: 'top',
        onToggle: () => {},
        isOpenByDefault: false
    }

    state = {
        isOpen: this.props.isOpenByDefault
    }

    onToggle = () => {
        this.toggle()
    }

    toggle () {
        this.setState(s => (
            { isOpen: !s.isOpen }
        ))
    }

    render () {
        const {
            id,
            items,
            hasTip,
            tipText,
            tipPlace,
            renderTip,
            className,
            toggleText,
            isDisabled,
            tipTrigger,
            tipClassName,
        } = this.props

        const {
            isOpen
        } = this.state

        const Chevron = isOpen ? TopChevron : BottomChevron

        return (
            <BootstrapDropdown
                isOpen={isOpen}
                disabled={isDisabled}
                toggle={this.onToggle}
                className={cn(
                    'Dropdown',
                    { 'Dropdown_disabled': isDisabled },
                    className
                )}
            >
                <DropdownToggle
                    id={id}
                    outline
                    color="success"
                    className="Dropdown-Toggle"
                >
                    <span>{toggleText}&nbsp;</span>
                    <Chevron className='Dropdown-ToggleChevron'/>
                </DropdownToggle>
                {hasTip && id && (tipText || renderTip) && (
                    <Tooltip
                        placement={tipPlace}
                        target={id}
                        trigger={tipTrigger}
                        className={tipClassName}
                        modifiers={[
                            {
                                name: 'offset',
                                options: { offset: [0, 6] }
                            },
                            {
                                name: 'preventOverflow',
                                options: { boundary: document.body }
                            }
                        ]}
                    >
                        {tipText || renderTip()}
                    </Tooltip>
                )}
                <DropdownMenu>
                    {map(items, ({ id, tooltip, value, text, render, isDisabled, ...other }) => (
                        <div id={id}>
                            <Item key={value} value={value} isDisabled={isDisabled} {...other}>
                                {text ?? render()}
                            </Item>
                            {tooltip && isDisabled && (
                                <Tooltip
                                    target={id}
                                    placement={"left"}
                                    modifiers={[
                                        {
                                            name: 'offset',
                                            options: { offset: [0, 6] }
                                        },
                                        {
                                            name: 'preventOverflow',
                                            options: { boundary: document.body }
                                        }
                                    ]}
                                >
                                    {tooltip}
                                </Tooltip>
                            )}
                        </div>
                    ))}
                </DropdownMenu>
            </BootstrapDropdown>
        )
    }
}