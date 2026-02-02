import {Modal} from "../../../../components";
import React from "react";
import MultipleSignaturesContext from "../../../../contexts/MultipleSignaturesContext";

const ClientSignature = (props) => {
  const {isOpen, onClose, clientId, organizationId,clientEmail,onSubmitSuccess,setIsClientSignatureRequestSuccessDialogOpen} = props;
  return (
    <>
      {isOpen && (
        <Modal
          isOpen={isOpen}
          onClose={onClose}
          className="RequestSignatureEditor"
          title="Request Signature"
          bodyClassName="RequestSignatureEditor-Body"
          hasFooter={false}
          hasCloseBtn={false}
        >

          <MultipleSignaturesContext
            defaultOrganizationId={organizationId} onClose={onClose} isSingleSignature={true}
            singleClientId={clientId}
            singleClientEmail={clientEmail}
            onSubmitSuccess={onSubmitSuccess}
            setIsClientSignatureRequestSuccessDialogOpen={setIsClientSignatureRequestSuccessDialogOpen}
          />
        </Modal>
      )}
    </>
  )
};

export default ClientSignature;