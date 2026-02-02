import React, { memo, useMemo, useState, useEffect, useCallback } from "react";

import cn from "classnames";

import { pluck, compact } from "underscore";

import { useParams, useLocation } from "react-router-dom";

import DocumentTitle from "react-document-title";

import { Badge, Collapse, UncontrolledTooltip as Tooltip } from "reactstrap";

import copyToClipboard from "copy-to-clipboard";

import { Dropdown, Breadcrumbs, ErrorViewer } from "components";

import { Button } from "components/buttons";

import { Dialog, ConfirmDialog, SuccessDialog, WarningDialog } from "components/dialogs";

import { DocumentList, DocumentReadyToSignDialog } from "components/business/Documents";

import { RequestSignatureEditor } from "containers/Documents";

import {
  useToggle,
  useLocationState,
  useQueryInvalidation,
  useLocationSearchParams,
  useDownloadingStatusInfoToast,
} from "hooks/common";

import { useSideBarUpdate } from "hooks/business/client";

import { useClientQuery } from "hooks/business/client/queries";

import { useDocumentTemplateCountQuery } from "hooks/business/documents";

import { useESignRequestQuery, useCanAddESignRequestQuery } from "hooks/business/documents/e-sign";

import {
  useCanDownloadCCDQuery,
  useClientDocumentQuery,
  useClientDocumentsQuery,
  useCanViewDocumentsQuery,
  useClientDocumentDownload,
  useClientDocumentDeletion,
  useClientDocumentListState,
  useCanDownloadFacesheetQuery,
  useClientDocumentRestoration,
  useCanAddClientDocumentsQuery,
  useClientDocumentMultiDownload,
  usePreparedClientDocumentFilterData,
} from "hooks/business/client/documents";

import { lc, allAreInteger, toNumberExcept } from "lib/utils/Utils";

import { getQueryParams } from "lib/utils/UrlUtils";

import { path } from "lib/utils/ContextUtils";

import {
  SERVER_ERROR_CODES,
  ALLOWED_FILE_FORMATS,
  CLIENT_DOCUMENT_TYPES,
  E_SIGN_STATUSES,
  ALLOWED_FILE_FORMAT_MIME_TYPES,
  E_SIGN_REQUEST_STEPS,
  E_SIGN_REQUEST_RECIPIENT_TYPES,
  E_SIGN_REQUEST_NOTIFICATION_METHODS,
} from "lib/Constants";

import { ReactComponent as Close } from "images/delete.svg";
import { ReactComponent as Filter } from "images/filters.svg";
import { ReactComponent as Warning } from "images/alert-yellow.svg";

import DocumentFilter from "./DocumentFilter/DocumentFilter";
import DocumentManager from "./DocumentManager/DocumentManager";
import DocumentEditor from "./DocumentEditor/DocumentEditor";

import "./Documents.scss";
import ClientSignature from "../../../Documents/e-sign/ClientSignature/ClientSignature";
import EsignMultipleSignaturesPreview from "../../../Documents/e-sign/ESignDocumentTemplateEditor/ESignMultipleSignatures/EsignMultipleSignaturesPreview";

import { docsealKeysToObject, decryptAES } from "lib/utils/KeysUtils";
import useDocsealKeysQuery from "hooks/business/keys/useDocsealKeysQuery";

const { PDF, XML } = ALLOWED_FILE_FORMATS;

const { CCD, FACESHEET } = CLIENT_DOCUMENT_TYPES;

const { SENT, SIGNED, RECEIVED, REQUESTED, REQUEST_EXPIRED } = E_SIGN_STATUSES;

const { SIGNATURE_REQUEST } = E_SIGN_REQUEST_STEPS;

const { SELF } = E_SIGN_REQUEST_RECIPIENT_TYPES;

const { SIGN_NOW } = E_SIGN_REQUEST_NOTIFICATION_METHODS;

function isIgnoredError(e = {}) {
  return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE;
}

function Documents() {
  const params = useParams();

  const clientId = toNumberExcept(params.clientId, [null, undefined]);

  const [{ isInstructionNeed, isManagementInstructionNeed } = {}, clearLocationState] = useLocationState();

  const [selected, setSelected] = useState(null);
  const [isFilterOpen, toggleFilter] = useState(true);
  const [isEditorOpen, toggleEditor] = useState(false);
  const [isManagerOpen, toggleManager] = useState(false);
  const [signatureRequestId, setSignatureRequestId] = useState(null);
  const [lastSignatureRequest, setLastSignatureRequest] = useState(null);

  const [isReadyToSignDialogOpen, toggleReadyToSignDialog] = useToggle(false);
  const [isReadyToSignByRequestDialogOpen, toggleReadyToSignByRequestDialog] = useToggle(false);

  const [isAlreadySignedDialogOpen, toggleAlreadySignedDialog] = useToggle(false);
  const [isSignatureRequestSuccessDialogOpen, toggleSignatureRequestSuccessDialog] = useToggle(false);
  const [isSignatureRequestExpiredDialogOpen, toggleSignatureRequestExpiredDialog] = useToggle(false);

  const [isSignatureRequestEditorOpen, toggleSignatureRequestEditor] = useToggle(false);
  const [isSignatureSuccessDialogOpen, toggleSignatureSuccessDialog] = useToggle(false);

  const [isRequestSuccessDialogOpen, toggleRequestSuccessDialog] = useToggle(false);
  const [isBulkRequestSuccessDialogOpen, toggleBulkRequestSuccessDialog] = useToggle(false);

  const [isConfirmDeleteDialogOpen, setIsConfirmDeleteDialogOpen] = useState(false);
  const [isConfirmRestoreDialogOpen, setIsConfirmRestoreDialogOpen] = useState(false);
  const [isClientSignatureRequestSuccessDialogOpen, setIsClientSignatureRequestSuccessDialogOpen] = useState(false);

  const [isInstructionDialogOpen, toggleInstructionDialog] = useState(isInstructionNeed);
  const [isManagementInstructionDialogOpen, toggleManagementInstructionDialog] = useState(isManagementInstructionNeed);

  const location = useLocation();

  const invalidate = useQueryInvalidation();

  const [searchParams, setSearchParams] = useLocationSearchParams();

  const [showMultipleSignaturesPreview, setShowMultipleSignaturesPreview] = useState(false);
  const [editMultipleSignaturesRowData, setEditMultipleSignaturesRowData] = useState();

  let { signedDocumentId } = getQueryParams(location.search);

  const { state, setError, clearError, changeFilter } = useClientDocumentListState();

  const { error } = state;

  const filter = usePreparedClientDocumentFilterData({ clientId, ...state.filter.toJS() });

  const {
    sort,
    fetch,
    refresh,
    pagination,
    isFetching,
    data: { data = [] } = {},
  } = useClientDocumentsQuery({ clientId, ...filter }, { onError: setError });

  const { data: client } = useClientQuery({ clientId });

  const { data: canAdd } = useCanAddClientDocumentsQuery({ clientId });

  const { data: templateCount = 0 } = useDocumentTemplateCountQuery(
    {
      communityId: client?.communityId,
    },
    { enabled: Boolean(client) },
  );

  const { data: signedDocument, isFetching: isFetchingSignedDocument } = useClientDocumentQuery(
    {
      clientId,
      documentId: signedDocumentId,
    },
    {
      onError: setError,
      enabled: allAreInteger(clientId, signedDocumentId),
    },
  );

  const { data: canRequestSignature } = useCanAddESignRequestQuery(
    { clientId },
    {
      staleTime: 0,
    },
  );

  const { data: signatureRequest, isFetching: isFetchingSignatureRequest } = useESignRequestQuery(
    { requestId: signatureRequestId },
    { enabled: Boolean(signatureRequestId) },
  );

  const { mutateAsync: download, isLoading: isDownloading } = useClientDocumentDownload({
    onError: setError,
  });

  const { mutateAsync: downloadMultiple, isLoading: isDownloadingMultiple } = useClientDocumentMultiDownload({
    onError: setError,
  });

  const { mutateAsync: remove, isLoading: isDeleting } = useClientDocumentDeletion(
    { clientId },
    {
      onError: setError,
    },
  );

  const { mutateAsync: restore, isLoading: isRestoring } = useClientDocumentRestoration(
    {},
    {
      onError: setError,
    },
  );

  const { data: canViewDocuments } = useCanViewDocumentsQuery({ clientId });

  const { data: canDownloadCCD } = useCanDownloadCCDQuery({ clientId });

  const { data: canDownloadFacesheet } = useCanDownloadFacesheetQuery({ clientId });

  const clientName = client?.fullName;
  const isClientActive = client?.isActive;

  const preparedData = useMemo(
    () =>
      data.map(({ canDelete, canEdit, ...o }) => ({
        ...o,
        canDelete: canDelete && isClientActive,
        canEdit: canEdit && isClientActive,
      })),
    [data, isClientActive],
  );

  const updateSideBar = useSideBarUpdate({ clientId });

  function deleteSearchParamFromLocation(name) {
    searchParams.delete(name);
    setSearchParams(searchParams.toString());
  }

  const onViewCCD = useCallback(() => {
    window.open(path(`/clients/${clientId}/documents/ccd-${clientName.replace(" ", "_")}.xml`));
  }, [clientId, clientName]);

  const invalidateCount = useCallback(() => {
    invalidate("Client.Document.Count", { clientId, includeDeleted: false });
  }, [invalidate, clientId]);

  const sign = useCallback(() => {
    window.open(selected.signature.pdcFlowLink);
  }, [selected]);

  const signByRequest = useCallback(() => {
    if (signatureRequest.pinCode) copyToClipboard(signatureRequest.pinCode);

    if (signatureRequest.recipientType !== SELF) {
      toggleReadyToSignByRequestDialog();
    }

    window.open(signatureRequest.pdcFlowLink);
  }, [signatureRequest, toggleReadyToSignByRequestDialog]);

  const onView = useCallback((o) => {
    setSelected(o);
    toggleManager(true);
  }, []);

  const onCloseViewer = useCallback(() => {
    toggleManager(false);
  }, []);

  const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast();

  const onDownloadCCD = useCallback(() => {
    download({
      clientId,
      documentId: lc(CCD),
      mimeType: ALLOWED_FILE_FORMAT_MIME_TYPES[XML],
    });
  }, [clientId, download]);

  const onDownloadFacesheet = useCallback(() => {
    download({
      clientId,
      documentId: lc(FACESHEET),
      mimeType: ALLOWED_FILE_FORMAT_MIME_TYPES[PDF],
    });
  }, [clientId, download]);

  const onDownloadSingle = useCallback(
    (o) => {
      withDownloadingStatusInfoToast(() =>
        download({
          clientId,
          documentId: o.id,
          mimeType: o.mimeType,
        }),
      );
    },
    [download, clientId, withDownloadingStatusInfoToast],
  );

  const onDownloadMultiple = useCallback(
    (items) => {
      if (items.length === 1) {
        onDownloadSingle(items[0]);
      } else {
        withDownloadingStatusInfoToast(() =>
          downloadMultiple({
            clientId,
            documentIds: pluck(items, "id"),
          }),
        );
      }
    },
    [clientId, onDownloadSingle, downloadMultiple, withDownloadingStatusInfoToast],
  );

  const onDownloadAll = useCallback(() => {
    withDownloadingStatusInfoToast(() => downloadMultiple({ clientId, ...filter }));
  }, [filter, clientId, downloadMultiple, withDownloadingStatusInfoToast]);

  function openConfirmDeleteDialog(document) {
    setSelected(document);
    setIsConfirmDeleteDialogOpen(true);
  }

  function openConfirmRestoreDialog(document) {
    setSelected(document);
    setIsConfirmRestoreDialogOpen(true);
  }

  const onRefresh = useCallback(() => {
    fetch();
    updateSideBar();
    invalidateCount();
  }, [fetch, updateSideBar, invalidateCount]);

  const { data: docsealKeys } = useDocsealKeysQuery();

  const docsealKeysObj = docsealKeysToObject(docsealKeys?.data);

  const AES_KEY = process.env.REACT_APP_PRIVATE_KEY;

  /// delete template
  function onDelete() {
    setSelected(null);
    setIsConfirmDeleteDialogOpen(false);
    console.log("Deleting...", selected);
    if (selected.multi) {
      //  判断字段 是否是多人的
      // 先删除 再归档
      remove({
        documentId: selected.id,
        isTemporaryDeletion: !selected.isTemporarilyDeleted,
      }).then(
        fetch("https://docseal.simplyconnect.me/api/templates", {
          method: "GET",
          headers: {
            "X-Auth-Token": decryptAES(docsealKeysObj.docseal_api_key, AES_KEY),
          },
        }).then((res) => {
          console.log(data);
        }),
      );
    } else {
      remove({
        documentId: selected.id,
        isTemporaryDeletion: !selected.isTemporarilyDeleted,
      }).then(onRefresh);
    }
  }

  // function getAllMulTemplate() {
  //   fetch("https://docseal.simplyconnect.me/api/templates", {
  //     method: "GET",
  //     headers: {
  //       "X-Auth-Token": process.env.REACT_APP_DOCSEAL_API_KEY,
  //     },
  //   }).then((res) => {});
  // }

  function onRestore() {
    setSelected(null);
    setIsConfirmRestoreDialogOpen(false);

    restore({
      clientId,
      documentId: selected.id,
    }).then(onRefresh);
  }

  const onDeleteSuccess = useCallback(
    (isTemporarily) => {
      onRefresh();
      if (!isTemporarily) toggleManager(false);
    },
    [onRefresh],
  );

  const onCloseConfirmDeleteDialog = useCallback(() => {
    setSelected(null);
    setIsConfirmDeleteDialogOpen(false);
  }, []);

  function onCloseConfirmRestoreDialog() {
    setSelected(null);
    setIsConfirmRestoreDialogOpen(false);
  }

  const onRequestSignature = useCallback(
    (document) => {
      setSignatureRequestId(null);
      toggleSignatureRequestEditor(true);
      if (document) setSelected(document);
    },
    [toggleSignatureRequestEditor],
  );

  const onSign = useCallback(
    (o) => {
      if (o.signature.pdcFlowPinCode) {
        setSelected(o);
        toggleReadyToSignDialog();
      } else {
        window.open(o.signature.pdcFlowLink);
      }
    },
    [toggleReadyToSignDialog],
  );

  const onCancelBulkRequestSuccess = useCallback(() => {
    refresh();
  }, [refresh]);

  const onRenewBulkRequestSuccess = useCallback(() => {
    toggleBulkRequestSuccessDialog();
  }, [toggleBulkRequestSuccessDialog]);

  const onCancelSignatureRequestSuccess = useCallback(() => {
    refresh();
  }, [refresh]);

  const onRequestSignatureSuccess = useCallback(() => {
    toggleRequestSuccessDialog();
  }, [toggleRequestSuccessDialog]);

  const onUploadSignatureRequestSuccess = useCallback(
    (data) => {
      refresh();
      updateSideBar();
      invalidateCount();
      toggleSignatureRequestSuccessDialog();

      /*  const requestData = {
        ids: data.ids, ...data[SIGNATURE_REQUEST]
      }

      if (requestData.documentId) {
        invalidate('ClientDocument', {
          clientId,
          documentId: requestData.documentId
        })
      }

      setLastSignatureRequest(requestData)

      if (requestData.ids.length === 1) {
        setSignatureRequestId(requestData.ids[0])
      }

      if (!(requestData.recipientType === SELF
        || requestData.notificationMethod === SIGN_NOW)) {
        toggleSignatureRequestSuccessDialog()
      } else if (requestData.ids.length > 1) {
        toggleReadyToSignByRequestDialog()
      }*/
    },
    [
      refresh,
      clientId,
      invalidate,
      updateSideBar,
      invalidateCount,
      toggleReadyToSignByRequestDialog,
      toggleSignatureRequestSuccessDialog,
    ],
  );

  const sendSignatureSuccess = () => {};

  useEffect(() => {
    let { documentId, signatureRequestId } = getQueryParams(location.search);

    if (documentId) {
      onView({ id: documentId });
    }

    if (signatureRequestId) {
      setSignatureRequestId(signatureRequestId);
    }
  }, [onView, location.search]);

  useEffect(() => {
    if (!signatureRequest) return;

    if (
      !lastSignatureRequest ||
      signatureRequest.recipientType === SELF ||
      signatureRequest.notificationMethod === SIGN_NOW
    ) {
      switch (signatureRequest.statusName) {
        case SENT:
        case REQUESTED:
          toggleReadyToSignByRequestDialog();
          break;
        case SIGNED:
        case RECEIVED:
          toggleAlreadySignedDialog();
          break;
        case REQUEST_EXPIRED:
          toggleSignatureRequestExpiredDialog();
          break;
      }
    }
  }, [
    signatureRequest,
    lastSignatureRequest,
    toggleAlreadySignedDialog,
    toggleReadyToSignByRequestDialog,
    toggleSignatureRequestExpiredDialog,
    toggleSignatureRequestSuccessDialog,
  ]);

  useEffect(() => {
    if (signedDocument) {
      toggleSignatureSuccessDialog();
    }
  }, [signedDocument, toggleSignatureSuccessDialog]);

  useEffect(() => {
    updateSideBar();
  }, [updateSideBar]);

  useEffect(() => {
    fetch();
  }, [fetch]);

  const editMultopleSignatures = (rowData) => {
    setEditMultipleSignaturesRowData(rowData);
  };

  return (
    <DocumentTitle title="Simply Connect | Clients | Client documents">
      <div className="Documents">
        <Breadcrumbs
          items={compact([
            { title: "Clients", href: "/clients", isEnabled: true },
            clientName && { title: clientName || "", href: `/clients/${clientId}` },
            { title: "Documents", href: `/clients/${clientId}/documents`, isActive: true },
          ])}
          className="margin-bottom-32"
        />
        <div className="Documents-Header">
          <div className="Documents-HeaderItem">
            <div className="Documents-Title">
              <span className="Documents-TitleText">Documents</span>
              <div className="d-inline-block text-nowrap">
                {clientName && <span className="Documents-ClientName">&nbsp;/&nbsp;{clientName}</span>}
                {pagination.totalCount ? (
                  <Badge color="info" className="Badge Badge_place_top-right">
                    {pagination.totalCount}
                  </Badge>
                ) : null}
              </div>
            </div>
          </div>
          <div className="Documents-HeaderItem">
            <div className="Documents-Actions">
              {canRequestSignature && isClientActive && canViewDocuments && (
                <Button
                  id="request-signature-btn"
                  color="success"
                  hasTip={templateCount === 0}
                  tipText="No templates uploaded to the client's community. Please contact Administrator."
                  disabled={templateCount === 0}
                  className="Documents-Action margin-right-20"
                  onClick={onRequestSignature}
                >
                  Request Signature
                </Button>
              )}
              {client && (
                <>
                  <Dropdown
                    items={[
                      { text: "View CCD", value: 0, onClick: onViewCCD },
                      { text: "Download CCD", value: 1, onClick: onDownloadCCD, isDisabled: !canDownloadCCD },
                      {
                        text: "Download Facesheet",
                        value: 2,
                        onClick: onDownloadFacesheet,
                        isDisabled: !canDownloadFacesheet,
                      },
                    ]}
                    id="CcdFacesheetActions"
                    toggleText="CCD/Facesheet"
                    className="Documents-CcdFacesheetActions"
                    isDisabled={!(canDownloadCCD && canDownloadFacesheet)}
                  />
                  {!(canDownloadCCD && canDownloadFacesheet) && (
                    <Tooltip placement="top" target="CcdFacesheetActions">
                      You don't have permissions to see Client's CCD and Facesheet
                    </Tooltip>
                  )}
                </>
              )}
              {canAdd && isClientActive && canViewDocuments && (
                <Button
                  color="success"
                  className="Documents-UploadAction margin-left-20"
                  onClick={() => toggleEditor(true)}
                >
                  Upload Document
                </Button>
              )}
              <Filter
                className={cn(
                  "DocumentFilter-Icon margin-left-20",
                  isFilterOpen ? "DocumentFilter-Icon_rotated_90" : "DocumentFilter-Icon_rotated_0",
                )}
                onClick={() => toggleFilter(!isFilterOpen)}
              />
            </div>
          </div>
        </div>
        <Collapse isOpen={isFilterOpen}>
          <DocumentFilter
            clientId={clientId}
            onChange={changeFilter}
            onReset={(isSaved) => isSaved && fetch()}
            onApply={fetch}
          />
        </Collapse>

        <DocumentList
          data={preparedData}
          hasTypeCol
          hasAuthorCol
          isESignEnabled
          hasCreatedDateCol
          hasSignatureStatusCol
          pagination={pagination}
          noDataText="No results found."
          isFetching={isFetching || isDeleting || isRestoring || isFetchingSignedDocument || isFetchingSignatureRequest}
          isDownloading={isDownloading || isDownloadingMultiple}
          getPath={(data) => path(`/clients/${clientId}/documents/${data.id}-${data.title}`)}
          className="ClientDocumentList"
          onSort={sort}
          onView={onView}
          onSign={onSign}
          onRefresh={refresh}
          onDownloadSingle={onDownloadSingle}
          onDownloadMultiple={onDownloadMultiple}
          onDownloadAll={onDownloadAll}
          onDelete={openConfirmDeleteDialog}
          onRestore={openConfirmRestoreDialog}
          setShowMultipleSignaturesPreview={setShowMultipleSignaturesPreview}
          editMultipleSignaturesRowData={editMultipleSignaturesRowData}
          editMultopleSignatures={editMultopleSignatures}
        />

        {showMultipleSignaturesPreview && (
          <EsignMultipleSignaturesPreview
            isOpen={showMultipleSignaturesPreview}
            onClose={() => {
              setShowMultipleSignaturesPreview(false);
              setEditMultipleSignaturesRowData(null);
            }}
            editData={editMultipleSignaturesRowData}
          />
        )}

        <DocumentManager
          clientId={clientId}
          isOpen={isManagerOpen}
          documentId={selected?.id}
          documentName={selected?.title}
          documentMimeType={selected?.mimeType}
          onClose={onCloseViewer}
          onSaveSuccess={onRefresh}
          onRestoreSuccess={onRefresh}
          onDeleteSuccess={onDeleteSuccess}
          onRequestSignature={onRequestSignature}
          onRequestSignatureSuccess={onRequestSignatureSuccess}
          onRenewBulkRequestSuccess={onRenewBulkRequestSuccess}
          onCancelBulkRequestSuccess={onCancelBulkRequestSuccess}
          onCancelSignatureRequestSuccess={onCancelSignatureRequestSuccess}
        />

        <DocumentEditor
          clientId={clientId}
          organization={client?.organization}
          organizationId={client?.organizationId}
          isOpen={isEditorOpen}
          onClose={useCallback(() => toggleEditor(false), [])}
          onUploadSuccess={useCallback(() => {
            fetch();
            updateSideBar();
            invalidateCount();
          }, [fetch, updateSideBar, invalidateCount])}
        />

        <ClientSignature
          isOpen={isSignatureRequestEditorOpen}
          onClose={() => toggleSignatureRequestEditor(false)}
          clientId={clientId}
          organizationId={client?.organizationId}
          clientEmail={client?.email}
          onSubmitSuccess={sendSignatureSuccess}
          setIsClientSignatureRequestSuccessDialogOpen={setIsClientSignatureRequestSuccessDialogOpen}
        />

        {isClientSignatureRequestSuccessDialogOpen && (
          <SuccessDialog
            isOpen
            title="The signature requests/documents have been sent to clients"
            buttons={[
              {
                text: "Close",
                onClick: () => {
                  toggleSignatureRequestEditor(false);
                  setIsClientSignatureRequestSuccessDialogOpen(false);
                  refresh();
                },
              },
            ]}
          />
        )}

        {isConfirmDeleteDialogOpen && (
          <ConfirmDialog
            isOpen
            icon={Warning}
            confirmBtnText="Delete"
            title={`The document will be ${selected.isTemporarilyDeleted ? "permanently" : "temporarily"} deleted`}
            onConfirm={onDelete}
            onCancel={onCloseConfirmDeleteDialog}
          />
        )}

        {isConfirmRestoreDialogOpen && (
          <ConfirmDialog
            isOpen
            icon={Warning}
            confirmBtnText="Restore"
            title={`The document will be restored`}
            onConfirm={onRestore}
            onCancel={onCloseConfirmRestoreDialog}
          />
        )}

        {isSignatureRequestSuccessDialogOpen && (
          <SuccessDialog
            isOpen
            title={
              lastSignatureRequest?.ids?.length > 1
                ? `The signature requests/documents have been sent to ${lastSignatureRequest?.recipientFullName}`
                : `The ${(lastSignatureRequest?.ids?.[0] ?? lastSignatureRequest).statusName === SENT ? "document" : "signature request"} has been sent to ${lastSignatureRequest?.recipientFullName}`
            }
            buttons={[{ text: "Close", onClick: () => toggleSignatureRequestSuccessDialog() }]}
          />
        )}

        {isReadyToSignByRequestDialogOpen && lastSignatureRequest?.ids?.length > 1 && (
          <SuccessDialog
            isOpen
            title={`The documents are ready to sign/review`}
            buttons={[{ text: "Close", onClick: () => toggleReadyToSignByRequestDialog() }]}
          />
        )}

        {isReadyToSignByRequestDialogOpen &&
          signatureRequest &&
          (signatureRequest.recipientType === SELF || signatureRequest.notificationMethod === SIGN_NOW) && (
            <DocumentReadyToSignDialog
              isOpen
              pinCode={signatureRequest.pinCode}
              signBtnText={signatureRequest.statusName === SENT ? "Review" : "Sign"}
              title={`The document is ready ${signatureRequest.statusName === SENT ? "for review" : "to sign"}.`}
              buttons={
                !signatureRequest.pinCode && [
                  { text: "Close", outline: true, onClick: () => toggleReadyToSignByRequestDialog() },
                  { text: signatureRequest.statusName === SENT ? "Review" : "Sign", onClick: signByRequest },
                ]
              }
              onClose={toggleReadyToSignByRequestDialog}
              onSign={signByRequest}
            />
          )}

        {isReadyToSignByRequestDialogOpen &&
          signatureRequest &&
          !(signatureRequest.recipientType === SELF || signatureRequest.notificationMethod === SIGN_NOW) && (
            <DocumentReadyToSignDialog
              isOpen
              text={signatureRequest.message}
              pinCode={signatureRequest.pinCode}
              signBtnText={signatureRequest.statusName === SENT ? "Review" : "Sign"}
              title={`${signatureRequest.templateName} is ready ${signatureRequest.statusName === SENT ? "for review" : "and available to sign"}.`}
              buttons={
                !signatureRequest.pinCode && [
                  {
                    text: signatureRequest.statusName === SENT ? "Review Document" : "Review and Sign Document",
                    onClick: signByRequest,
                  },
                ]
              }
              onClose={toggleReadyToSignByRequestDialog}
              onSign={signByRequest}
            />
          )}

        {isReadyToSignDialogOpen && selected?.signature?.canSign && (
          <DocumentReadyToSignDialog
            isOpen
            pinCode={selected.signature.pdcFlowPinCode}
            signBtnText={selected.signature.statusName === SENT ? "Review" : "Sign"}
            title={`${selected.title} is ready ${selected.signature.statusName === SENT ? "for review" : "and available to sign"}.`}
            onClose={toggleReadyToSignDialog}
            onSign={sign}
          />
        )}

        {isSignatureSuccessDialogOpen && (
          <SuccessDialog
            isOpen
            title={`The document ${signedDocument?.signature?.statusName === RECEIVED ? "received" : "signed"}.`}
            buttons={[
              {
                text: "Close",
                outline: true,
                onClick: () => {
                  toggleSignatureSuccessDialog();
                  deleteSearchParamFromLocation("signedDocumentId");
                },
              },
              {
                text: "View Document",
                onClick: () => {
                  toggleSignatureSuccessDialog();
                  onView({ id: signedDocumentId });
                  deleteSearchParamFromLocation("signedDocumentId");
                },
              },
            ]}
          />
        )}

        {isRequestSuccessDialogOpen && (
          <SuccessDialog
            isOpen
            title="The request has been sent"
            buttons={[
              {
                text: "Close",
                onClick: () => {
                  toggleRequestSuccessDialog();

                  refresh();
                },
              },
            ]}
          />
        )}

        {isBulkRequestSuccessDialogOpen && (
          <SuccessDialog
            isOpen
            title="The requests have been sent"
            buttons={[
              {
                text: "Close",
                onClick: () => {
                  toggleBulkRequestSuccessDialog();

                  refresh();
                },
              },
            ]}
          />
        )}

        {isAlreadySignedDialogOpen && (
          <WarningDialog
            isOpen
            title={`The document has already ${signatureRequest.statusName === RECEIVED ? "reviewed" : "signed"}.`}
            buttons={[{ text: "Close", onClick: () => toggleAlreadySignedDialog() }]}
          />
        )}

        {isSignatureRequestExpiredDialogOpen && (
          <WarningDialog
            isOpen
            title={`The request has expired. Please contact ${signatureRequest.authorFullName}. Email: ${signatureRequest.authorEmail}`}
            buttons={[{ text: "Close", onClick: () => toggleSignatureRequestExpiredDialog() }]}
          />
        )}

        {isInstructionDialogOpen && (
          <Dialog
            isOpen
            title="Access and sign documents electronically."
            buttons={[
              {
                text: "Close",
                color: "success",
                onClick: () => {
                  clearLocationState();
                  toggleInstructionDialog(false);
                },
              },
            ]}
          >
            <p>
              You can access Facesheet, Continuity of Care Document (CCD) and other documents uploaded by your health
              care providers.
            </p>
            <p>
              If a document requires your signature, you will receive a notification and will be able to sign it from
              here.
            </p>
          </Dialog>
        )}

        {isManagementInstructionDialogOpen && (
          <Dialog
            isOpen
            title="Paperless experience."
            buttons={[
              {
                text: "Close",
                color: "success",
                onClick: () => {
                  clearLocationState();
                  toggleManagementInstructionDialog(false);
                },
              },
            ]}
          >
            <p>
              You can access Facesheet, Continuity of Care Document (CCD) and other documents uploaded by your health
              care providers.
            </p>
            <p>
              If a document requires your signature, you will receive a notification and will be able to sign it from
              here.
            </p>
            <p>
              You can request a signature from Staff, Client or Parent/Guardian or Client Legal Representative if you
              have appropriate permission.
            </p>
          </Dialog>
        )}

        {error && !isIgnoredError(error) && <ErrorViewer isOpen error={error} onClose={clearError} />}
      </div>
    </DocumentTitle>
  );
}

export default memo(Documents);
