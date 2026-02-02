import React, { Component } from 'react'

import PTypes from 'prop-types'

import $ from 'jquery'
import 'jquery.scrollto'

import cn from 'classnames'

import {
    noop,
    throttle,
} from 'underscore'

import {
    ModalHeader,
    ModalFooter,
    Modal as BootstrapModal,
} from 'reactstrap'

import { ReactComponent as Cross } from 'images/cross-3.svg'
import { ReactComponent as ArrowTop } from 'images/arrowtop.svg'

import './Modal.scss'

const DEFAULT_SCROLL_DURATION = 500

const MIN_SCROLL_OFFSET_TOP = 300
const MIN_SCROLL_OFFSET_CHANGE_TIME = 200

export default class Modal extends Component {

    static propTypes = {
        title: PTypes.string,

        isOpen: PTypes.bool,
        isCentered: PTypes.bool,
        hasFooter: PTypes.bool,
        isLightBackdrop: PTypes.bool,
        hasCloseBtn: PTypes.bool,
        isCloseBtnDisabled: PTypes.bool,

        className: PTypes.string,
        scrollOffset: PTypes.number,
        headerClassName: PTypes.string,
        bodyClassName: PTypes.string,
        footerClassName: PTypes.string,
        backdropClassName: PTypes.string,
        renderHeader: PTypes.func,
        renderFooter: PTypes.func,
        renderHeaderButtons: PTypes.func,
        onClose: PTypes.func
    }

    static defaultProps = {
        hasFooter: true,
        scrollOffset: 0,
        hasCloseBtn: true,
        onClose: noop
    }

    ref = React.createRef()

    bodyRef = React.createRef()

    state = {
        isScrollTopBtnShowed: false,
    }

    componentDidMount() {
        const {
            scrollOffset
        } = this.props

        this.scroll(scrollOffset)
    }

    onOpened = () => {
        this.addScrollEventListener()
    }

    onClose = () => {
        this.removeScrollEventListener()
        this.props.onClose()
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

    addScrollEventListener() {
        this
            .bodyRef
            .current
            .addEventListener('scroll', this.onScroll)
    }

    removeScrollEventListener() {
        this
            .bodyRef
            .current
            .removeEventListener('scroll', this.onScroll)
    }

    scroll(target, duration = DEFAULT_SCROLL_DURATION, opts) {
        const node = this.bodyRef.current
        node && $(node).scrollTo(target, duration, opts)
    }

    scrollTop(duration, opts) {
        this.scroll(0, duration, opts)
    }

    scrollBottom(duration, opts) {
        this.scroll('max', duration, opts)
    }

    toggleScrollTopBtn(toggle = false) {
        this.setState({
            isScrollTopBtnShowed: toggle
        })
    }

    render() {
        const {
            isScrollTopBtnShowed
        } = this.state

        const {
            title,

            isOpen,

            hasHeader=true,
            hasFooter,
            isCentered,
            hasCloseBtn,
            isLightBackdrop,
            isCloseBtnDisabled,

            className,
            headerClassName,
            bodyClassName,
            footerClassName,
            backdropClassName,

            renderHeader,
            renderFooter,
            renderHeaderButtons
        } = this.props

        return (
            <BootstrapModal
                ref={this.ref}

                isOpen={isOpen}
                backdrop='static'
                centered={isCentered}

                toggle={this.onClose}
                onOpened={this.onOpened}

                className={cn('Modal', className)}
                backdropClassName={cn({ 'modal-backdrop_tone_light': isLightBackdrop }, backdropClassName)}
            >

                {
                    hasHeader &&  <ModalHeader
                    toggle={hasCloseBtn ? this.onClose : null}
                    className={cn('Modal-Header', headerClassName)}
                    close={hasCloseBtn ? (
                      renderHeaderButtons ? renderHeaderButtons() : (
                        <div
                          className={cn(
                            'btn',
                            'Modal-CloseBtn',
                            isCloseBtnDisabled && 'disabled'
                          )}
                          onClick={this.onClose}>
                            <Cross className='Modal-CloseIcon' />
                        </div>
                      )
                    ) : null}
                  >
                      {renderHeader ? renderHeader(title) : title}
                  </ModalHeader>
                }


                <div
                    ref={this.bodyRef}
                    className={cn('modal-body Modal-Body', bodyClassName)}
                >
                    {this.props.children}
                </div>

                {hasFooter && (
                    <div className='Modal-FooterWrapper'>
                        <ModalFooter className={cn('Modal-Footer', footerClassName)}>
                            {renderFooter ? renderFooter() : null}
                        </ModalFooter>

                        {isScrollTopBtnShowed && (
                            <span
                                className="Modal-ScrollTopBtn"
                                title="Back to Top"
                                onClick={this.onScrollTop}
                            >
                                <ArrowTop className="Modal-ScrollTopBtnIcon" />
                            </span>
                        )}
                    </div>
                )}
            </BootstrapModal>
        )
    }
}
