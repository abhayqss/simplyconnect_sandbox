import React, { Component } from 'react'

import PropTypes from 'prop-types'

import $ from 'jquery'
import 'jquery.scrollto'

import {
    noop,
    throttle
} from 'underscore'

import cn from 'classnames'

import { ReactComponent as ArrowTop } from 'images/arrowtop.svg'

import './ScrollTop.scss'

const DURATION = 500

const MIN_SCROLL_OFFSET_TOP = 300
const MIN_SCROLL_OFFSET_CHANGE_TIME = 200

export default class ScrollTop extends Component {
    static propTypes = {
        duration: PropTypes.number,
        scrollTopBtnClass: PropTypes.string
    }

    static defaultProps = {
        duration: DURATION
    }

    state = {
        isBtnShowed: false
    }

    componentDidMount() {
        $(this.props.scrollable).on(
            'scroll', this.onScroll
        ) || noop()
    }
    
    componentWillUnmount() {
        $(this.props.scrollable).off(
            'scroll', this.onScroll
        ) || noop()
    }

    onScroll = throttle(e => {
        const offset = e.target.scrollTop
        const { isBtnShowed } = this.state

        if (!isBtnShowed && offset > MIN_SCROLL_OFFSET_TOP) {
            this.toggleScrollTopBtn(true)
        }

        if (isBtnShowed && offset < MIN_SCROLL_OFFSET_TOP) {
            setTimeout(() => {
                this.toggleScrollTopBtn()
            }, 250)
        }
    }, MIN_SCROLL_OFFSET_CHANGE_TIME)

    onScrollTop = () => {
        this.scrollTop()
    }

    scroll(target, duration, opts) {
        $(this.props?.scrollable).scrollTo(
            target, duration ?? this.props.duration, opts
        ) || noop()
    }

    scrollTop(duration) {
        this.scroll(0, duration)
    }

    toggleScrollTopBtn(toggle = false) {
        this.setState({
            isBtnShowed: toggle
        })
    }

    render() {
        return this.state.isBtnShowed && (
            <a
                title='Back to Top'
                className={cn(
                    "ScrollTopBtn",
                    this.props.scrollTopBtnClass
                )}
                onClick={this.onScrollTop}>
                <ArrowTop className="ScrollTopBtn-Icon"/>
            </a>
        )
    }
}