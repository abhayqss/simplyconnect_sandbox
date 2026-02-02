import React, {Component} from 'react'

import PropTypes from 'prop-types'

import { noop } from 'underscore'

import {connect} from 'react-redux'

import Modal from 'components/Modal/Modal'
import ConfirmDialog from 'components/dialogs/ConfirmDialog/ConfirmDialog'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import EventForm from '../EventForm/EventForm'

import './EventEditor.scss'

function mapStateToProps (state) {
    return {
        form: state.event.form
    }
}

class EventEditor extends Component {
    static propTypes = {
        isOpen: PropTypes.bool,
        clientId: PropTypes.number,

        onClose: PropTypes.func,
        onSaveSuccess: PropTypes.func
    }

    static defaultProps = {
        onClose: noop,
        onSaveSuccess: noop
    }

    state = {
        isConfirmDialogOpen: false
    }

    onClose = () => {
        if (this.props.form.isChanged()) {
            this.setState({
                isConfirmDialogOpen: true
            })
        }

        else this.props.onClose()
    }

    onCloseConfirmDialog = () => {
        this.setState({
            isConfirmDialogOpen: false
        })
    }

    render () {
        const {
            isOpen,
            clientId,
            onClose,
            onSaveSuccess
        } = this.props

        const {
            isConfirmDialogOpen
        } = this.state

        return (
            <>
                <Modal
                    isOpen={isOpen}
                    hasFooter={false}
                    hasCloseBtn={false}
                    title="Create Event"
                    className="EventEditor"
                >
                    <EventForm
                        clientId={clientId}
                        onCancel={this.onClose}
                        onSubmitSuccess={onSaveSuccess}
                    />
                </Modal>
                {isConfirmDialogOpen && (
                    <ConfirmDialog
                        isOpen
                        icon={Warning}
                        confirmBtnText='OK'
                        title='The updates will not be saved'
                        onConfirm={onClose}
                        onCancel={this.onCloseConfirmDialog}
                    />
                )}
            </>
        )
    }
}

export default connect(mapStateToProps)(EventEditor)