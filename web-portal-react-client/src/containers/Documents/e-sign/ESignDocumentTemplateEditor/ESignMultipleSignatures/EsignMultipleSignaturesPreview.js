import { Modal } from "components";
import { DocusealBuilder } from "@docuseal/react";
import React, { useEffect, useState } from "react";
import service from "services/EsignService";
import { WarningDialog } from "../../../../../components/dialogs";

/**
 *
 * @param props
 * @param {boolean} props.isOpen
 * @param {params} props.editData
 * @param {string} props.editData.title
 * @param {string} props.editData.documentUrl
 * @param {string} props.editData.applicationKey
 * @param {function} props.onClose
 * @return {JSX.Element}
 * @constructor
 */
const EsignMultipleSignaturesPreview = (props) => {
  const { isOpen, editData, onClose } = props;

  const [esToken, setEstoken] = useState("");
  const [showWaring, setSowWaring] = useState(false);
  useEffect(() => {
    // 编辑模版

    const params = {
      userEmail: process.env.REACT_APP_DOCSEAL_USER_EMAIL,
      integrationEmail: "",
      name: editData.title.split(".")[0],
      documentUrls: [editData.documentUrl],
      applicationKey: editData.applicationKey,
      folderName: process.env.REACT_APP_SENTRY_ENVIRONMENT,
    };

    if (!params.documentUrls[0] || !params.applicationKey) {
      setSowWaring(true);
      return;
    }

    service.getEsignToken(params).then((res) => {
      setEstoken(res.data.token);
    });
  }, []);

  return (
    <>
      {isOpen && (
        <Modal
          hasCloseBtn
          isOpen={isOpen}
          onClose={onClose}
          className="ESignDocumentTemplateEditor"
          title={"Preview E-sign Template"}
          hasFooter={false}
        >
          {esToken && (
            <>
              <DocusealBuilder
                token={esToken}
                host={process.env.REACT_APP_DOCSEAL_HOST}
                withRecipientsButton={false}
                withSignYourselfButton={false}
                preview={true}
              />
            </>
          )}

          {setSowWaring && (
            <WarningDialog
              isOpen={showWaring}
              title="Due to program updates, this template cannot be previewed. Please upload it again."
              buttons={[
                {
                  text: "Ok",
                  onClick: () => {
                    setSowWaring(false);
                    onClose();
                  },
                },
              ]}
            />
          )}
        </Modal>
      )}
    </>
  );
};

export default EsignMultipleSignaturesPreview;
