import React, { Component } from 'react'

import PropTypes from 'prop-types'

import ErrorDialog from '../dialogs/ErrorDialog/ErrorDialog'

export default class ErrorViewer extends Component {
    static propTypes = {
        error: PropTypes.object,
        
        isOpen: PropTypes.bool,
        closeBtnText: PropTypes.string,

        onClose: PropTypes.func,
        onClosed: PropTypes.func
    }

    static defaultProps = {
        error: null,
        closeBtnText: 'Close',

        onClose: () => {},
        onClosed: () => {}
    }

    onClose = () => {
        this.props.onClose(
            this.props.error
        )
    }


    onClosed = () => {
        this.props.onClosed(
            this.props.error
        )
    }

    render () {
        const {
            error,
            isOpen,
            closeBtnText
        } = this.props

        return error && (
            <ErrorDialog
                isOpen={isOpen}
                title={error?.message?.title || error?.message}
                text={error?.message?.text}
                onClosed={this.onClosed}
                buttons={[
                    {
                        text: closeBtnText,
                        onClick: this.onClose
                    }
                ]}
            />
        )
    }
}