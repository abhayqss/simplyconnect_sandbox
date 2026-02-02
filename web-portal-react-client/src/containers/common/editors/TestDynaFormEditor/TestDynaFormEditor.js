import React, { memo } from 'react'

import { Modal } from 'components'

import TestDynaForm from '../../forms/TestDynaForm/TestDynaForm'

import './TestDynaFormEditor.scss'

function TestDynaFormEditor({ isOpen, onClose }) {
    return (
        <>
            {isOpen && (
                <Modal
                    isOpen={isOpen}
                    onClose={onClose}
                    className="TestDynaFormEditor"
                    title="Test Dyna Form Editor"
                    bodyClassName="TestDynaFormEditor-Body"
                    hasFooter={false}
                >
                    <TestDynaForm/>
                </Modal>
            )}
        </>
    )
}

export default memo(TestDynaFormEditor)