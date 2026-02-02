import { isNumber } from "underscore";
import Modal from "components/Modal/Modal";
import CreateContactForm from "../CreateContactForm/CreateContactForm";
import "./CreateContactModal.scss";
import React from "react";

const AssociationCreateContactModal = (props) => {
  const {
    isOpen,
    contactId,
    associationId,
    onClose,
    onSubmit,
    editContactSuccess,
    associationDetailData,
    isPendingContact,
    isExpiredContact,
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
        associationId={associationId}
        contactId={contactId}
        onClose={onClose}
        canEditRole={false}
        onSubmitSuccess={onSubmit}
        editContactSuccess={editContactSuccess}
        isPendingContact={isPendingContact}
        isExpiredContact={isExpiredContact}
        associationDetailData={associationDetailData}
      />
    </Modal>
  );
};
export default AssociationCreateContactModal;
