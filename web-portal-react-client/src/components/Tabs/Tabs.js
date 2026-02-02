import React, { Component } from 'react'

import _ from 'underscore'
import cn from 'classnames'
import PropTypes from 'prop-types'

import { Nav, NavItem, NavLink } from 'reactstrap'

import './Tabs.scss'

import {ReactComponent as Bookmark} from "images/bookmark.svg";

class Tab extends Component {

    static propTypes = {
        index: PropTypes.number,

        title: PropTypes.string,

        className: PropTypes.string,
        indicatorClassName: PropTypes.string,
        indicatorIconClassName: PropTypes.string,

        isActive: PropTypes.bool,
        hasError: PropTypes.bool,
        hasIndicator: PropTypes.bool,

        onClick: PropTypes.func,

        render: PropTypes.func,
        renderLink: PropTypes.func,
        renderIndicator: PropTypes.func
    }

    static defaultProps = {
        isActive: false,
        hasError: false,
    }

    onClick = () => {
        const {
            index,
            isDisabled,
            onClick: cb
        } = this.props

        !isDisabled && cb(index)
    }

    render () {
        const {
            title,

            isActive,
            hasError,
            isDisabled,
            hasIndicator,

            className,
            indicatorClassName,
            indicatorIconClassName,

            render,
            renderLink,
            renderIndicator,
        } = this.props

        return render ? render ({ title, isActive, hasError, isDisabled, className, renderLink }) : (
            <NavItem className="Tabs-Tab">
                {renderLink ? renderLink (title, isActive, className, isDisabled) : (
                    <NavLink
                        onClick={this.onClick}
                        className={cn(
                            'Tabs-TabLink',
                            { 'Tabs-TabError': hasError },
                            { 'Tabs-TabDisabled': isDisabled },
                            { active: isActive },
                            className,
                    )}>
                        {title}
                    </NavLink>
                )}
                {hasIndicator ? (
                    renderIndicator ? renderIndicator(indicatorClassName) : (
                        <div className={cn('Tabs-TabIndicator', indicatorClassName)}>
                            <Bookmark className={cn('Tabs-TabIndicatorIcon', indicatorIconClassName)}/>
                        </div>
                    )
                ) : null}
            </NavItem>
        )
    }
}

export default class Tabs extends Component {

    static propTypes = {
        items: PropTypes.array,
        className: PropTypes.string,
        containerClassName: PropTypes.string,
        onChange: PropTypes.func,
        isDisabled: PropTypes.bool,
    }

    static defaultProps = {
        items: [],
        isDisabled: false,
        onChange: function () {}
    }

    onChange = (index) => {
        !this.props.isDisabled && this.props.onChange(index)
    }

    render () {
        const {
            items,
            className,
            containerClassName
        } = this.props

        return (
            <div className={cn('TabsContainer', containerClassName)}>
                <Nav tabs className={cn('Tabs', className)}>
                    {_.map(items, ({
                                       title,
                                       isActive,
                                       className,
                                       render,
                                       renderLink,
                                       hasError,
                                       isDisabled,
                                       hasIndicator,
                                       renderIndicator,
                                       indicatorClassName,
                                       indicatorIconClassName
                                   }, i) => {
                        return (
                            <Tab
                                index={i}
                                title={title}

                                className={className}
                                indicatorClassName={indicatorClassName}
                                indicatorIconClassName={indicatorIconClassName}

                                hasError={hasError}
                                isActive={isActive}
                                isDisabled={isDisabled}
                                hasIndicator={hasIndicator}

                                onClick={this.onChange}

                                render={render}
                                renderLink={renderLink}
                                renderIndicator={renderIndicator}

                                key={title.split(' ').join('')}
                            />
                        )
                    })}
                </Nav>
            </div>
        )
    }
}