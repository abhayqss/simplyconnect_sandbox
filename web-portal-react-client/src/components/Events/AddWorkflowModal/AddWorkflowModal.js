import Modal from "components/Modal/Modal";
import React, { memo } from "react";
import "./AddWorkflowModal.scss";
import AddWorkflowForm from "../AddWorkflowForm/AddWorkflowForm";

const AddWorkflowModal = (props) => {
  const { isOpen, onClose, adminClientId, onCancel, onConfirm, communityIds, organizationId, workflowData } = props;
  return (
    <>
      <Modal
        isOpen={isOpen}
        title={"Add workflow for client"}
        className="workflow-modal"
        hasFooter={false}
        hasCloseBtn={true}
        onClose={onClose}
      >
        <AddWorkflowForm
          adminClientId={adminClientId}
          onConfirm={onConfirm}
          onClose={onClose}
          onCancel={onCancel}
          communityIds={communityIds}
          organizationId={organizationId}
        />
      </Modal>
    </>
  );
};

export default memo(AddWorkflowModal);
