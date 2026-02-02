import React from 'react'

import cn from 'classnames'

import { Modal, Button } from 'reactstrap'

import { ReactComponent as CloseButton } from 'images/close.svg'

import './ModalActionPicker.scss'

function ModalActionPicker({ options, className, onClose }) {
    const isMobile = window.innerWidth <= 667

    return (
        <Modal
            isOpen
            backdrop
            toggle={onClose}
            centered={!isMobile}
            modalClassName="ModalActionPicker-Container"
            modalTransition={{
                baseClass: cn('fade', { ModalActionPicker_fade: isMobile }),
                baseClassActive:cn('show', { ModalActionPicker_show: isMobile }),

            }}
            className={cn('ModalActionPicker', className)}
        >
            <CloseButton
                onClick={onClose}
                className="ModalActionPicker-CloseButton"
            />

            <div className="ModalActionPicker-Actions">
                {options.map((option, i) => (
                    <Button
                        key={i}
                        color="success"
                        onClick={option.onClick}
                        className="ModalActionPicker-Action"
                    >
                        {option.title}
                    </Button>
                ))}
            </div>
        </Modal>
    )
}

export default ModalActionPicker