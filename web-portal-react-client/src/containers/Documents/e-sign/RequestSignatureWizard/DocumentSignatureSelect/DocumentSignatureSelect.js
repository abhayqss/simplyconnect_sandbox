import {Modal} from "components";
import React, {useEffect, useState} from "react";
import SelectField from "components/Form/SelectField/SelectField";
import './DocumentSignatureSelect.scss'
import {Button} from "reactstrap";
const DocumentSignatureSelect = (props) =>{
    const { isOpen, onClose, signId , options, selectItem,changeSelect,onConfirm} = props

 const changeModalSelect =(name,value,text)=>{
     changeSelect(name, value, text)
     setModalItem(value)
 }

 const [modalItem, setModalItem] = useState()
    useEffect(()=>{
        setModalItem(selectItem)
    }, [selectItem])

    return <>
        <Modal
            title={'Select Recipient'}
            isOpen={isOpen}
            hasFooter={false}
            hasCloseBtn={false}
            className='DocumentSignatureSelect'>
            <div className="DocumentSignatureSelect-modal-content">
                <SelectField
                    name="Recipient"
                    value={modalItem}
                    options={options}
                    isMultiple={false}
                    label="Recipient*"
                    onChange={changeModalSelect}
                />
            </div>
            <div className="ModalForm-Buttons">
                <Button
                    outline
                    color="success"
                    onClick={onClose}
                >
                    Close
                </Button>
                <Button
                    color="success"
                    onClick={onConfirm}
                >
                    Confirm
                </Button>
            </div>
        </Modal>
    </>
}
export default DocumentSignatureSelect
