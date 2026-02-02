import React, { Component } from 'react'

import PropTypes from 'prop-types'

import $ from 'jquery'
import 'jquery.scrollto'

import cn from 'classnames'

import { noop, throttle } from 'underscore'

import { ReactComponent as ArrowTop } from 'images/arrowtop.svg'

import './Scrollable.scss'

const DURATION = 500

const MIN_SCROLL_OFFSET_TOP = 300
const MIN_SCROLL_OFFSET_CHANGE_TIME = 200

export default class Scrollable extends Component {

    static propTypes = {
        offset: PropTypes.number,
        duration: PropTypes.number,
        className: PropTypes.string,
        hasScrollTopBtn: PropTypes.bool
    }

    static defaultProps = {
        offset: 0,
        duration: DURATION,
        hasScrollTopBtn: true
    }

    ref = React.createRef()

    state = {
        isScrollTopBtnShowed: false
    }

    componentDidMount() {
        this.scroll(this.props.offset)

        this.ref.current?.addEventListener(
            'scroll', this.onScroll
        ) || noop()
    }

    componentDidUpdate(prevProps) {
        const { offset } = this.props

        if (offset !== prevProps.offset) {
            this.scroll(offset)
        }
    }

    componentWillUnmount() {
        this.ref.current?.removeEventListener(
            'scroll', this.onScroll
        ) || noop()
    }

    onScroll = throttle(e => {
        const offset = e.target.scrollTop
        const isShowed = this.state.isScrollTopBtnShowed

        if (!isShowed && offset > MIN_SCROLL_OFFSET_TOP) {
            this.toggleScrollTopBtn(true)
        }

        if (isShowed && offset < MIN_SCROLL_OFFSET_TOP) {
            setTimeout(() => {
                this.toggleScrollTopBtn()
            }, 250)
        }
    }, MIN_SCROLL_OFFSET_CHANGE_TIME)

    onScrollTop = () => {
        this.scrollTop()
    }

    scroll(target, duration, opts) {
        $(this.ref.current).scrollTo(
            target, duration ?? this.props.duration, opts
        ) || noop()
    }

    scrollTop(duration) {
        this.scroll(0, duration)
    }

    scrollBottom(opts) {
        this.scroll('max', opts)
    }

    toggleScrollTopBtn(toggle = false) {
        this.setState({
            isScrollTopBtnShowed: toggle
        })
    }

    render() {
        const { style, className, hasScrollTopBtn } = this.props

        return (
            <div
                style={style}
                ref={this.ref}
                className={cn('Scrollable', className)}
            >
                {this.props.children}
                {hasScrollTopBtn && this.state.isScrollTopBtnShowed && (
                    <a
                        title='Back to Top'
                        className="Scrollable-ScrollTopBtn"
                        onClick={this.onScrollTop}>
                        <ArrowTop className="Scrollable-ScrollTopBtnIcon"/>
                    </a>
                )}
            </div>
        )
    }
}