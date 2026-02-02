import React, {Component} from 'react'

import PropTypes from 'prop-types'

import './ConfirmDialog.scss'

import Dialog from '../Dialog/Dialog'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

export default class ConfirmDialog extends Component {

    static propTypes = {
        icon: PropTypes.object,
        text: PropTypes.string,
        isOpen: PropTypes.bool,
        title: PropTypes.string,
        renderIcon: PropTypes.func,
        className: PropTypes.string,

        cancelBtnText: PropTypes.string,
        isCancelBtnDisabled: PropTypes.bool,

        confirmBtnText: PropTypes.string,
        isConfirmBtnDisabled: PropTypes.bool,

        onCancel: PropTypes.func,
        onConfirm: PropTypes.func
    }

    static defaultProps = {
        icon: Warning,
        cancelBtnText: 'Cancel',
        confirmBtnText: 'Confirm',
        onCancel: () => {},
        onConfirm: () => {}
    }

    onConfirm = () => {
        this.props.onConfirm()
    }

    onCancel = () => {
        this.props.onCancel()
    }

    render () {
        const {
            text,
            title,
            icon,
            isOpen,
            className,
            renderIcon,
            cancelBtnText,
            confirmBtnText,
            isCancelBtnDisabled,
            isConfirmBtnDisabled,
        } = this.props

        return (
            <Dialog
                icon={icon}
                text={text}
                title={title}
                isOpen={isOpen}
                className={className}
                renderIcon={renderIcon}
                buttons={[
                    {
                        text: cancelBtnText,
                        color: 'outline-success',
                        onClick: this.onCancel,
                        disabled: isCancelBtnDisabled,
                    },
                    {
                        text: confirmBtnText,
                        onClick: this.onConfirm,
                        disabled: isConfirmBtnDisabled
                    }
                ]}
            />
        )
    }
}