import React, { PureComponent } from 'react'

import PropTypes from 'prop-types'

import {
    noop,
    isNumber
} from 'underscore'

import Modal from 'components/Modal/Modal'

import './OrganizationEditor.scss'

import OrganizationForm from '../OrganizationForm/OrganizationForm'

class OrganizationEditor extends PureComponent {

    static propTypes = {
        isOpen: PropTypes.bool,
        organizationId: PropTypes.number,

        onClose: PropTypes.func,
        onSaveSuccess: PropTypes.func
    }

    static defaultProps = {
        onClose: noop,
        onSaveSuccess: noop
    }

    isEditMode () {
        return isNumber(this.props.organizationId)
    }

    render () {
        const {
            isOpen,
            organizationId
        } = this.props

        return (
            <Modal
                isOpen={isOpen}
                className='OrganizationEditor'
                hasCloseBtn={false}
                hasFooter={false}
                title={this.isEditMode() ? 'Edit organization details' : 'Create organization'}
            >
                <OrganizationForm
                    organizationId={organizationId}
                    onClose={this.props.onClose}
                    onSaveSuccess={this.props.onSaveSuccess}
                />
            </Modal>
        )
    }
}

export default OrganizationEditor
