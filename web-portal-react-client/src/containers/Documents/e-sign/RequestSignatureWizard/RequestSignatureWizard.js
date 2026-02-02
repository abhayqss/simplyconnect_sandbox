import React, { memo, useCallback, useMemo, useState } from "react";

import cn from "classnames";

import { pluck } from "underscore";

import { Button } from "reactstrap";

import { SignatureRequestContextProvider } from "contexts";

import { withTooltip } from "hocs";

import { useDocumentTemplatesQuery } from "hooks/business/documents";

import { isArray, isNotEmpty } from "lib/utils/ArrayUtils";

import { setup as setupCanvas } from "lib/utils/CanvasUtils";

import { E_SIGN_REQUEST_STEPS } from "lib/Constants";

import SignatureRequestForm from "./SignatureRequestForm/SignatureRequestForm";
import DocumentTemplateForm from "./DocumentTemplateForm/DocumentTemplateForm";
import BulkSignatureRequestForm from "./BulkSignatureRequestForm/BulkSignatureRequestForm";
import DocumentTemplatePreviewForm from "./DocumentTemplatePreviewForm/DocumentTemplatePreviewForm";
import DocumentTemplatePreviewFormMultipleSignatures from "./DocumentTemplatePreviewFormMultipleSignatures/DocumentTemplatePreviewFormMultipleSignatures";

import "./RequestSignatureWizard.scss";
import MultipleSignaturesContext from "../../../../contexts/MultipleSignaturesContext";

setupCanvas();

const {
  SIGNATURE_REQUEST,
  DOCUMENT_TEMPLATE,
  DOCUMENT_TEMPLATE_PREVIEW,
  DOCUMENT_TEMPLATE_PREVIEW_MULTIPLE,
  MULTIPLE_SIGNATURE_REQUEST,
} = E_SIGN_REQUEST_STEPS;

const STEP = {
  [SIGNATURE_REQUEST]: 0,
  [MULTIPLE_SIGNATURE_REQUEST]: 0,
  [DOCUMENT_TEMPLATE]: 1,
  [DOCUMENT_TEMPLATE_PREVIEW]: 2,
  [DOCUMENT_TEMPLATE_PREVIEW_MULTIPLE]: 2,
};

const STEPS_STORY = [SIGNATURE_REQUEST, DOCUMENT_TEMPLATE, DOCUMENT_TEMPLATE_PREVIEW];
const MULTIPLE_REQUEST_STEPS_STORY = [MULTIPLE_SIGNATURE_REQUEST, DOCUMENT_TEMPLATE, DOCUMENT_TEMPLATE_PREVIEW];
const MULTIPLE_PEOPLE_CO_SIGN_A_DOCUMENT = [
  MULTIPLE_SIGNATURE_REQUEST,
  DOCUMENT_TEMPLATE,
  DOCUMENT_TEMPLATE_PREVIEW_MULTIPLE,
];
const FORMS = {
  [MULTIPLE_SIGNATURE_REQUEST]: BulkSignatureRequestForm,
  [SIGNATURE_REQUEST]: SignatureRequestForm,
  [DOCUMENT_TEMPLATE]: DocumentTemplateForm,
  [DOCUMENT_TEMPLATE_PREVIEW]: DocumentTemplatePreviewForm,
  [DOCUMENT_TEMPLATE_PREVIEW_MULTIPLE]: DocumentTemplatePreviewFormMultipleSignatures,
};

const SubmitBtn = withTooltip({
  className: "RequestSignatureWizard-SubmitBtn",
  text: "Please define a place for signature by clicking on an area of the document.",
})(Button);

function Actions({ next, update, submit }) {
  return {
    [SIGNATURE_REQUEST]: (...args) => {
      update(...args);
      next();
    },
    [DOCUMENT_TEMPLATE]: (...args) => {
      update(...args);
      next();
    },
    [DOCUMENT_TEMPLATE_PREVIEW]: (...args) => {
      submit(update(...args));
    },
    [DOCUMENT_TEMPLATE_PREVIEW_MULTIPLE]: (...args) => {
      submit(update(...args));
    },
    [MULTIPLE_SIGNATURE_REQUEST]: (...args) => {
      update(...args);
      next();
    },
  };
}

function isFirstStep(step) {
  return step === STEP[SIGNATURE_REQUEST] || step === STEP[MULTIPLE_SIGNATURE_REQUEST];
}

function isLastStep(step) {
  return step === STEP[DOCUMENT_TEMPLATE_PREVIEW_MULTIPLE] || step === STEP[DOCUMENT_TEMPLATE_PREVIEW];
}

function RequestSignatureWizard({
  clients,
  clientId,
  clientIds,
  templateId,
  documentId,
  communityIds,
  organizationId,
  isMultipleRequest,

  onCancel,
  onSubmitSuccess,
}) {
  const signatureStep = !isMultipleRequest ? SIGNATURE_REQUEST : MULTIPLE_SIGNATURE_REQUEST;
  const [data, setData] = useState({});
  const [step, setStep] = useState(STEP[signatureStep]);

  const [whetherMultiplePeopleNeedToSign, setWhetherMultiplePeopleNeedToSign] = useState(1);
  const [showWaySelect, setShowWaySelect] = useState(true);
  const selectedCommunityIds = useMemo(() => {
    if (!isMultipleRequest) return communityIds;

    const selectedCommunities = data[MULTIPLE_SIGNATURE_REQUEST]?.originalData?.communities;

    return pluck(selectedCommunities, "communityId");
  }, [data, communityIds, isMultipleRequest]);

  const stepName = useMemo(() => {
    if (!isMultipleRequest) return STEPS_STORY[step];
    if (isMultipleRequest && whetherMultiplePeopleNeedToSign === 1) return MULTIPLE_PEOPLE_CO_SIGN_A_DOCUMENT[step];
    if (isMultipleRequest && whetherMultiplePeopleNeedToSign === 0) return MULTIPLE_REQUEST_STEPS_STORY[step];
  }, [step, isMultipleRequest, whetherMultiplePeopleNeedToSign]);

  const hasNoSecondStep = Boolean(documentId) && Boolean(templateId);

  const actions = Actions({
    next,
    update: updateData,
    submit: onSubmitSuccess,
  });

  const cancel = useCallback(
    (isChanged) => {
      return onCancel(isChanged || step !== signatureStep);
    },
    [step, onCancel],
  );

  const Form = FORMS[stepName];

  function back(steps) {
    setStep((step) => (steps ? step - steps : --step));
  }

  function next() {
    setStep((step) => step + (hasNoSecondStep ? 2 : 1));
  }

  function updateData(o) {
    let merged = {
      ...data,
      ...{ [isArray(o) ? "ids" : stepName]: o },
    };

    setData(merged);

    return merged;
  }

  function submit(...args) {
    actions[stepName](...args);
  }

  useDocumentTemplatesQuery(
    { communityIds: selectedCommunityIds },
    {
      enabled: isNotEmpty(selectedCommunityIds),
    },
  );

  return (
    <>
      {signatureStep === "SIGNATURE_REQUEST" ? (
        <div className="RequestSignatureWizard">
          <SignatureRequestContextProvider
            step={stepName}
            requestData={data[signatureStep]}
            templateIds={data[signatureStep]?.templateIds}
            templateData={data[DOCUMENT_TEMPLATE]}
          >
            <Form
              clients={clients}
              clientId={clientId}
              clientIds={clientIds}
              documentId={documentId}
              templateId={templateId}
              communityIds={selectedCommunityIds}
              organizationId={organizationId}
              defaultData={data[stepName]}
              onCancel={cancel}
              onBack={back}
              onSubmitSuccess={submit}
            >
              {({ back, cancel, isValidToSubmit }) => (
                <div className="RequestSignatureWizard-FormFooter">
                  <div className="RequestSignatureWizard-Buttons">
                    {!isFirstStep(step) && (
                      <Button
                        outline
                        color="success"
                        id="back-action"
                        className="margin-right-25 width-100"
                        onClick={() => {
                          back && back();
                        }}
                      >
                        Back
                      </Button>
                    )}

                    <Button
                      outline
                      color="success"
                      id="cancel-action"
                      className="margin-right-25 width-100"
                      onClick={cancel}
                    >
                      Close
                    </Button>

                    <SubmitBtn
                      color="success"
                      id="submit-action"
                      disabled={!isValidToSubmit}
                      className={cn(!isLastStep(step) && "width-100")}
                      isTooltipEnabled={isLastStep(step) && !isValidToSubmit}
                    >
                      {isLastStep(step) ? "Send request" : "Next"}
                    </SubmitBtn>
                  </div>
                </div>
              )}
            </Form>
          </SignatureRequestContextProvider>
        </div>
      ) : (
        <>
          {!showWaySelect && whetherMultiplePeopleNeedToSign === 0 && (
            <div className="RequestSignatureWizard">
              <SignatureRequestContextProvider
                step={stepName}
                requestData={data[signatureStep]}
                templateIds={data[signatureStep]?.templateIds}
                templateData={data[DOCUMENT_TEMPLATE]}
              >
                <Form
                  clients={clients}
                  clientId={clientId}
                  clientIds={clientIds}
                  documentId={documentId}
                  templateId={templateId}
                  communityIds={selectedCommunityIds}
                  organizationId={organizationId}
                  defaultData={data[stepName]}
                  onCancel={cancel}
                  onBack={back}
                  onSubmitSuccess={submit}
                >
                  {({ back, cancel, isValidToSubmit }) => (
                    <div className="RequestSignatureWizard-FormFooter">
                      <div className="RequestSignatureWizard-Buttons">
                        {!isFirstStep(step) && (
                          <Button
                            outline
                            color="success"
                            id="back-action"
                            className="margin-right-25 width-100"
                            onClick={() => {
                              back && back();
                            }}
                          >
                            Back
                          </Button>
                        )}

                        <Button
                          outline
                          color="success"
                          id="cancel-action"
                          className="margin-right-25 width-100"
                          onClick={cancel}
                        >
                          Close
                        </Button>

                        <SubmitBtn
                          color="success"
                          id="submit-action"
                          disabled={!isValidToSubmit}
                          className={cn(!isLastStep(step) && "width-100")}
                          isTooltipEnabled={isLastStep(step) && !isValidToSubmit}
                        >
                          {isLastStep(step) ? "Send request" : "Next"}
                        </SubmitBtn>
                      </div>
                    </div>
                  )}
                </Form>
              </SignatureRequestContextProvider>
            </div>
          )}
        </>
      )}

      {signatureStep !== "SIGNATURE_REQUEST" && (
        <MultipleSignaturesContext
          onClose={cancel}
          onSubmitSuccess={onSubmitSuccess}
          defaultOrganizationId={organizationId}
        />
      )}
    </>
  );
}

export default memo(RequestSignatureWizard);
