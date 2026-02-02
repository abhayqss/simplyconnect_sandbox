import { isNumber } from "underscore";
import Modal from "components/Modal/Modal";
import CreateContactForm from "../CreateContactForm/CreateContactForm";
import "./CreateContactModal.scss";
import React from "react";

const CreateContactModal = (props) => {
  const {
    isOpen,
    contactId,
    vendorId,
    onClose,
    onSubmit,
    editContactSuccess,
    vendorDetailData,
    isPendingContact,
    isExpiredContact,
    hieAgreement,
    isShowRoleSelect = false,
  } = props;
  const isEditMode = () => {
    return isNumber(contactId);
  };
  return (
    <Modal
      isOpen={isOpen}
      className="CreateContactFormEditor"
      hasCloseBtn={true}
      hasFooter={false}
      onClose={onClose}
      title={isEditMode() ? "Edit Contact" : "Create Contact"}
    >
      <CreateContactForm
        vendorId={vendorId}
        contactId={contactId}
        onClose={onClose}
        canEditRole={false}
        hieAgreement={hieAgreement}
        onSubmitSuccess={onSubmit}
        editContactSuccess={editContactSuccess}
        isPendingContact={isPendingContact}
        isExpiredContact={isExpiredContact}
        vendorDetailData={vendorDetailData}
        isShowRoleSelect={isShowRoleSelect}
      />
    </Modal>
  );
};
export default CreateContactModal;
