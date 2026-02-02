import React, { useCallback,} from "react";
import {isNumber, } from "underscore";
import Modal from 'components/Modal/Modal'
import './AssociationEditor.scss'
import AssociationForm from "../AssociationForm/AssociationForm";
import {useCancelConfirmDialog} from "../../../../hooks/common";

function AssociationEditor ({
    isOpen,
    associationId,
    onClose,
    onSaveSuccess}) {
    const [CancelConfirmDialog, toggleCancelConfirmDialog] = useCancelConfirmDialog()

    const isEditMode=()=> {
        return isNumber(associationId)
    }

  const onCancel = useCallback(isChanged => {
        if (isChanged) toggleCancelConfirmDialog(true)
        else onClose()
    }, [onClose, toggleCancelConfirmDialog])

        return (
            <>
                <CancelConfirmDialog onConfirm={onClose} />
                <Modal
                    isOpen={isOpen}
                    className='AssociationEditor'
                    hasCloseBtn={true}
                    hasFooter={false}
                    onClose={onClose}
                    title={ isEditMode()? 'Edit Association' : 'Add Association'}
                >
                    <AssociationForm
                        associationId={associationId}
                        isEdit={isEditMode()}
                        onClose={onClose}
                        onCancel={onCancel}
                        onSaveSuccess={onSaveSuccess}
                    />
                </Modal>
            </>
        );
}

export  default AssociationEditor