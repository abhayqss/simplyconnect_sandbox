import React, { memo, useCallback, useEffect, useMemo, useState } from "react";

import cn from "classnames";

import { any, chain } from "underscore";

import { Badge, Button, Collapse } from "reactstrap";

import { Breadcrumbs, ErrorViewer, Footer } from "components";

import { ConfirmDialog, Dialog, ErrorDialog, SuccessDialog, WarningDialog } from "components/dialogs";

import { DocumentList } from "components/business/Documents";

import services from "services/EsignService";

import {
  useAuthUser,
  useDocumentTitle,
  useDownloadingStatusInfoToast,
  useLocationState,
  useQueryInvalidation,
  useRefCurrent,
  useToggle,
} from "hooks/common";

import {
  useCanAddDocumentFoldersQuery,
  useCanAddDocumentsQuery,
  useCanViewDocumentFolderQuery,
  useDocumentCountQuery,
  useDocumentDeletion,
  useDocumentDownload,
  useDocumentFilterCombination,
  useDocumentFolderDeletion,
  useDocumentFolderDownload,
  useDocumentFolderRestoration,
  useDocumentListState,
  useDocumentMultiDownload,
  useDocumentRestoration,
  useDocumentsQuery,
  useDocumentTemplateDownload,
  useDocumentTemplateMultiDownload,
  usePreparedDocumentFilterData,
} from "hooks/business/documents";

import {
  useCanAddESignDocumentTemplateQuery,
  useESignDocumentTemplateAssignment,
  useESignDocumentTemplateDeletion,
} from "hooks/business/documents/e-sign/template";

import { defer, ifElse, interpolate } from "lib/utils/Utils";

import { first, last } from "lib/utils/ArrayUtils";

import { path } from "lib/utils/ContextUtils";

import { SERVER_ERROR_CODES } from "lib/Constants";

import { ReactComponent as Filter } from "images/filters.svg";
import { ReactComponent as Arrow } from "images/arrow-right.svg";
import { ReactComponent as Warning } from "images/alert-yellow.svg";

import {
  DocumentEditor,
  DocumentFilter,
  DocumentFolderEditor,
  DocumentManager,
  ESignDocumentTemplateEditor,
  ESignDocumentTemplateFolderAssigner,
} from "./";

import "./Documents.scss";
import { useCommunityQuery } from "hooks/business/community";
import EsignMultipleSignaturesPreview from "./e-sign/ESignDocumentTemplateEditor/ESignMultipleSignatures/EsignMultipleSignaturesPreview";
import ESignMultipleSignatures from "./e-sign/ESignDocumentTemplateEditor/ESignMultipleSignatures/ESignMultipleSignatures";
import DocumentPrimaryFilter from "./DocumentPrimaryFilter/DocumentPrimaryFilter";
import EsignCopy from "./e-sign/ESignDocumentTemplateEditor/EsignCopy/EsignCopy";
import useDocsealKeysQuery from "hooks/business/keys/useDocsealKeysQuery";

import { docsealKeysToObject, decryptAES } from "lib/utils/KeysUtils";

const ROOT = {
  folderId: null,
  title: "Root",
  categories: [],
  isSecurityEnabled: false,
};

const DELETE_TEMPLATE_TEXT = "$1 template will be deleted.";
const DELETE_DOC_CONFIRM_TEXT = "The document will be $0 deleted";
const DELETE_FOLDER_CONFIRM_TEXT = "The folder with files and subfolders if any will be $0 deleted";

const RESTORE_DOC_CONFIRM_TEXT = "The document will be restored";
const RESTORE_FOLDER_CONFIRM_TEXT = "The folder with files and subfolders if any will be restored";

function isIgnoredError(e = {}) {
  return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE;
}

function isFolder(o) {
  return ["FOLDER", "TEMPLATE_FOLDER"].includes(o?.type);
}

function isRootFolder(folder) {
  return folder.folderId === null;
}

function isTemplateFolder(folder) {
  return folder?.type === "TEMPLATE_FOLDER";
}

const idMapper = (o) => o.id;

function Documents() {
  const user = useAuthUser();
  const [{ isInstructionNeed } = {}, clearLocationState] = useLocationState();

  const [selected, setSelected] = useState(null);
  const [isCopying, setCopying] = useState(false);
  const [folderPath, setFolderPath] = useState([ROOT]);

  const [isFilterOpen, toggleFilter] = useToggle(true);

  const [isEditorOpen, toggleEditor] = useToggle();
  const [isManagerOpen, toggleManager] = useToggle();
  const [isFolderEditorOpen, toggleFolderEditor] = useToggle();

  const [isConfirmDeleteDialogOpen, toggleConfirmDeleteDialog] = useToggle();
  const [isConfirmRestoreDialogOpen, toggleConfirmRestoreDialog] = useToggle();

  const [isDeleteMultipleDialogOpen, setIsDeleteMultipleDialogOpen] = useState(false);
  const [deleteMultipleData, setDeleteMultipleData] = useState(null);
  const [isMultiplePreview, setIsMultiplePreview] = useState(false);

  const [isNoFolderAccessDialogOpen, toggleNoFolderAccessDialog] = useToggle();
  const [isDeletedFolderErrorDialogOpen, toggleDeletedFolderErrorDialog] = useToggle();
  const [isNoLongerFolderAccessDialogOpen, toggleNoLongerFolderAccessDialog] = useToggle();

  const [isESignDocumentTemplateEditorOpen, toggleESignDocumentTemplateEditor] = useToggle(false);

  //  Display multi-person signature template model.
  const [showMultipleSignatures, setShowMultipleSignatures] = useState(false);
  const [showMultipleSignaturesPreview, setShowMultipleSignaturesPreview] = useState(false);

  const [isESignDocTemplateFolderAssignerOpen, toggleESignDocTemplateFolderAssigner] = useToggle(false);

  const [isInstructionDialogOpen, toggleInstructionDialog] = useState(isInstructionNeed);

  const [isReassignTemplatesDialogOpen, toggleReassignTemplatesDialog] = useState(false);
  const [isTemplateReassignedDialogOpen, toggleTemplateReassignedDialog] = useState(false);

  const [isSaveMulTemplateSuccessDialog, setIsSaveMulTemplateSuccessDialog] = useState(false);
  const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast();

  const [editMultipleSignaturesRowData, setEditMultipleSignaturesRowData] = useState();

  // Copy dialog box, used to copy templates to other places.
  const [showCopyModel, setShowCopyModel] = useState(false);

  const parentFolder = last(folderPath);

  const parentFolderCategoryIds = useMemo(() => {
    return isRootFolder(parentFolder) ? [] : parentFolder.categories?.map(idMapper);
  }, [parentFolder]);

  useDocumentTitle("Simply Connect | Company Documents");

  const { state, setError, clearError, changeFilter } = useDocumentListState();

  const { error } = state;

  const filter = useRefCurrent(usePreparedDocumentFilterData(state.filter.toJS()));

  const { isSecurityEnabled } = state.filter;

  const { folderId, communityId, organizationId } = filter;
  const {
    sort,
    fetch,
    reset,
    refresh,
    isFetching,
    pagination,
    data: { data } = {},
  } = useDocumentsQuery(
    { ...filter },
    {
      onError: (error, newTodo, context) => {
        setError(null);
      },
      staleTime: 0,
    },
  );

  const {
    isFetching: isFetchingDocumentCount,
    refetch: refetchDocumentCount,
    data: selectedFolderDocumentsCount,
  } = useDocumentCountQuery(
    {
      communityId,
      organizationId,
      folderId: selected?.folderId,
      folderName: selected?.folderName,
    },
    {
      staleTime: 0,
      enabled: false,
      onError: setError,
    },
  );

  const { data: community, isFetching: isFetchingCommunity } = useCommunityQuery(
    {
      communityId,
      organizationId,
    },
    {
      enabled: !!filter.communityId,
    },
  );

  const assignedFolders = useMemo(() => {
    return [{ folderId, communityId }];
  }, [folderId, communityId]);

  const communityOptions = useMemo(() => {
    return [{ text: community?.name, value: communityId }];
  }, [community, communityId]);

  const deletionText = useMemo(() => {
    let text = DELETE_DOC_CONFIRM_TEXT;
    if (selected?.type === "TEMPLATE") {
      text = DELETE_TEMPLATE_TEXT;
    } else if (isFolder(selected)) {
      text = DELETE_FOLDER_CONFIRM_TEXT;
    }

    return interpolate(text, selected?.isTemporarilyDeleted ? "permanently" : "temporarily", selected?.title);
  }, [selected]);

  const shouldClear = organizationId && !communityId && !!data;

  const { primary, custom } = useDocumentFilterCombination(
    {
      onChange: (data) => {
        changeFilter(data);
      },
    },
    {
      onChange: (data) => {
        changeFilter(data);
      },
      onApply: () => {
        if (communityId) refresh();
      },
      onReset: (isSaved) => {
        if (isSaved && communityId) fetch();
      },
    },
    filter,
  );

  const { data: canAdd } = useCanAddDocumentsQuery(
    {
      folderId,
      communityId,
    },
    {
      enabled: Boolean(communityId),
    },
  );

  // 获取文件列表
  useCanViewDocumentFolderQuery(
    {
      folderId,
      communityId,
    },
    {
      enabled: Boolean(folderId) && Boolean(communityId),
      // refetchInterval: 3000,
      refetchIntervalInBackground: true,
      onSuccess: (canView) => {
        if (!canView) {
          toggleNoLongerFolderAccessDialog();
          defer(2000).then(() => {
            clearError();
            navigateToFolder(ROOT);
            toggleNoLongerFolderAccessDialog();
          });
        }
      },
    },
  );

  const { data: canAddFolders } = useCanAddDocumentFoldersQuery(
    {
      communityId,
      parentFolderId: folderId,
    },
    {
      enabled: Boolean(communityId),
    },
  );

  const { data: canCreateTemplate } = useCanAddESignDocumentTemplateQuery(
    {
      organizationId,
      communityId,
    },
    {
      staleTime: 0,
      enabled: Boolean(organizationId) && Boolean(communityId),
    },
  );

  const invalidate = useQueryInvalidation();

  function invalidateParentFolders() {
    invalidate("DocumentFolders", { canUpload: true, communityId });
  }

  const { mutateAsync: download, isLoading: isDownloading } = useDocumentDownload({ onError: setError });

  const { mutateAsync: downloadTemplate, isLoading: isDownloadingTemplate } = useDocumentTemplateDownload({
    onError: setError,
  });

  const { mutateAsync: downloadFolder, isLoading: isDownloadingFolder } = useDocumentFolderDownload({
    onError: setError,
  });

  const { mutateAsync: downloadMultiple, isLoading: isDownloadingMultiple } = useDocumentMultiDownload({
    onError: setError,
  });

  const { mutateAsync: downloadMultipleTemplates, isLoading: isDownloadingMultipleTemplates } =
    useDocumentTemplateMultiDownload({ onError: setError });

  const { isLoading: isDeleting, mutateAsync: removeDocument } = useDocumentDeletion({
    onError: setError,
    onSuccess: () => fetch(),
  });

  const { mutateAsync: removeTemplate, isLoading: isDeletingTemplate } = useESignDocumentTemplateDeletion({
    onError: setError,
    onSuccess: () => fetch(),
  });

  const { mutateAsync: removeFolder, isLoading: isDeletingFolder } = useDocumentFolderDeletion({
    onError: setError,
    onSuccess: async () => {
      invalidateParentFolders();
      fetch();
    },
  });

  const { isLoading: isRestoring, mutateAsync: restoreDocument } = useDocumentRestoration({ onError: setError });

  const { mutateAsync: restoreFolder, isLoading: isRestoringRegularFolder } = useDocumentFolderRestoration({
    onError: setError,
    onSuccess: invalidateParentFolders,
  });

  const { mutateAsync: assignTemplate, isLoading: isAssigningTemplate } = useESignDocumentTemplateAssignment(
    {},
    {
      onError: setError,
      onSuccess: () => {
        invalidateParentFolders();
        invalidate("ESignDocumentTemplate", {
          templateId: selected.templateId,
        });
        setSelected(null);
        fetch();
        toggleTemplateReassignedDialog(true);
      },
    },
  );

  function openConfirmRestoreDialog(document) {
    setSelected(document);
    toggleConfirmRestoreDialog(true);
  }

  function changeFolderPath(folder) {
    const i = folderPath.findIndex((o) => o.folderId === folder.folderId);

    if (i >= 0) {
      setFolderPath(folderPath.slice(0, i + 1));
    } else {
      setFolderPath([...folderPath, folder]);
    }
  }

  function navigateToFolder(folder) {
    changeFolderPath(folder);

    const { folderId, isSecurityEnabled } = folder;
    changeFilter({ folderId, isSecurityEnabled });

    defer().then(isRootFolder(folder) ? refresh : fetch);
  }

  function onFolder(data) {
    if (data.canView || isRootFolder(data)) {
      navigateToFolder(data);
    } else if (data.isTemporarilyDeleted) {
      setSelected(data);
      toggleDeletedFolderErrorDialog();
    } else {
      toggleNoFolderAccessDialog();
    }
  }

  function onAssign(o) {
    if (isTemplateFolder(parentFolder)) {
      setSelected(o);
      toggleESignDocTemplateFolderAssigner(true);
    }
  }

  const onDownloadSingle = useCallback(
    (o) => {
      if (o.type === "FOLDER") {
        withDownloadingStatusInfoToast(() => downloadFolder({ folderId: o.folderId }));
      } else if (isTemplateFolder(o)) {
        withDownloadingStatusInfoToast(() => downloadMultiple({ ids: o.id }));
      } else if (o.type === "TEMPLATE") {
        withDownloadingStatusInfoToast(() =>
          downloadTemplate({
            communityId,
            mimeType: o.mimeType,
            templateId: o.templateId,
          }),
        );
      } else {
        withDownloadingStatusInfoToast(() =>
          download({
            documentId: o.id,
            mimeType: o.mimeType,
          }),
        );
      }
    },
    [download, communityId, downloadFolder, downloadTemplate, downloadMultiple, withDownloadingStatusInfoToast],
  );

  const onDownloadMultiple = useCallback(
    (items) => {
      if (items.length === 1) {
        const item = first(items);

        if (isTemplateFolder(item)) {
          withDownloadingStatusInfoToast(() => downloadMultiple({ ids: item.id }));
        } else {
          withDownloadingStatusInfoToast(() => onDownloadSingle(item));
        }
      } else if (isTemplateFolder(parentFolder)) {
        withDownloadingStatusInfoToast(() =>
          downloadMultipleTemplates({
            communityId,
            ids: chain(items)
              .filter((o1) => any(data, (o2) => o1.id === o2.id))
              .pluck("templateId")
              .value(),
          }),
        );
      } else {
        withDownloadingStatusInfoToast(() =>
          downloadMultiple({
            ids: chain(items)
              .filter((o1) => any(data, (o2) => o1.id === o2.id))
              .pluck("id")
              .value(),
          }),
        );
      }
    },
    [
      data,
      communityId,
      parentFolder,
      onDownloadSingle,
      downloadMultiple,
      downloadMultipleTemplates,
      withDownloadingStatusInfoToast,
    ],
  );

  const onDownloadAll = useCallback(() => {
    downloadMultiple(filter);
  }, [filter, downloadMultiple]);

  const closeReassignTemplateDialog = useCallback(() => {
    toggleReassignTemplatesDialog(false);
    setSelected(null);
  }, []);

  const canDeleteTemplateFolder = useCallback(
    async ({ isTemporarilyDeleted }) => {
      if (isTemporarilyDeleted) return true;

      const { data: documentCount } = await refetchDocumentCount();

      return documentCount === 0;
    },
    [refetchDocumentCount],
  );

  async function onConfirmDeletion() {
    toggleConfirmDeleteDialog(false);

    const { id, type, folderId, templateId } = selected;
    const isTemporaryDeletion = !selected.isTemporarilyDeleted;
    try {
      if (isFolder({ type })) {
        await removeFolder({ folderId, isTemporaryDeletion });
      } else if (type === "TEMPLATE") {
        await removeTemplate({ templateId, isTemporaryDeletion });
      } else {
        await removeDocument({ documentId: id, isTemporaryDeletion });
      }
    } finally {
      setSelected(null);
    }
  }

  async function onRestore() {
    setSelected(null);
    toggleConfirmRestoreDialog(false);

    const { id, folderId } = selected;

    if (isFolder(selected)) {
      await restoreFolder({ folderId });
    } else {
      await restoreDocument({ documentId: id });
    }

    fetch();
  }

  const onDeleteSuccess = useCallback(
    (isTemporarily) => {
      fetch();

      if (!isTemporarily) {
        toggleManager();
        setSelected(null);
      }
    },
    [fetch, toggleManager],
  );

  const onCloseConfirmDeleteDialog = useCallback(() => {
    setSelected(null);
    toggleConfirmDeleteDialog(false);
  }, [toggleConfirmDeleteDialog]);

  function onCloseConfirmRestoreDialog() {
    setSelected(null);
    toggleConfirmRestoreDialog(false);
  }

  const openManager = useCallback(
    (o) => {
      setSelected(o);
      toggleManager(true);
    },
    [toggleManager],
  );

  const edit = useCallback(
    ifElse(
      (o) => isFolder(o),
      (folder) => {
        toggleFolderEditor(true);
        setSelected(folder);
      },
      (template) => {
        setSelected(template);
        toggleESignDocumentTemplateEditor(true);
      },
    ),
    [toggleFolderEditor],
  );

  /*  const displayMultipleSignatures = (show) => {
      setDisplayMultipleSignaturesModel(show);
    }*/

  const editMultopleSignatures = (rowData) => {
    setEditMultipleSignaturesRowData(rowData);
  };

  const onEditTemplate = useCallback(() => {
    toggleManager(false);
    toggleESignDocumentTemplateEditor(true);
  }, [toggleManager, toggleESignDocumentTemplateEditor]);

  const onCloseTemplateEditor = useCallback(() => {
    setSelected(null);
    setCopying(false);
    toggleESignDocumentTemplateEditor(false);
  }, [toggleESignDocumentTemplateEditor]);

  const onCopyTemplate = useCallback(
    (o) => {
      setSelected(o);
      setCopying(true);
      toggleESignDocumentTemplateEditor(true);
    },
    [toggleESignDocumentTemplateEditor],
  );

  const onAssignESignDocTemplateToFolder = useCallback(
    ([{ folderId }]) => {
      toggleESignDocTemplateFolderAssigner(false);
      assignTemplate({ templateId: selected.templateId, folderId });
    },
    [toggleESignDocTemplateFolderAssigner, assignTemplate, selected],
  );

  const onCloseESignDocTemplateFolderAssigner = useCallback(() => {
    toggleESignDocTemplateFolderAssigner(false);
    setSelected(null);
  }, [toggleESignDocTemplateFolderAssigner]);

  const onCloseManager = useCallback(() => {
    toggleManager();
    setSelected(null);
  }, [toggleManager]);

  const onCloseEditor = useCallback(() => {
    toggleEditor();
    setSelected(null);
  }, [toggleEditor]);

  const onCloseFolderEditor = useCallback(() => {
    setSelected(null);
    toggleFolderEditor(false);
  }, [toggleFolderEditor]);

  const onSaveFolderSuccess = useCallback(() => {
    fetch();
    setSelected(null);
  }, [fetch]);

  const onUploadSuccess = useCallback(() => {
    fetch();
    setSelected(null);
  }, [fetch]);

  const onDelete = useCallback(
    async (document) => {
      await setSelected(document);

      if (isTemplateFolder(document)) {
        const canDelete = await canDeleteTemplateFolder(document);
        canDelete ? toggleConfirmDeleteDialog(true) : toggleReassignTemplatesDialog(true);
      } else {
        toggleConfirmDeleteDialog(true);
      }
    },
    [canDeleteTemplateFolder, toggleConfirmDeleteDialog],
  );

  const onMultopleDelete = (deleteRowData) => {
    setIsDeleteMultipleDialogOpen(true);
    setDeleteMultipleData(deleteRowData);
    // onConfirmMultipleDeletion(deleteRowData);
  };

  const multiplePreview = (isPreview) => {
    setIsMultiplePreview(isPreview);
  };

  const onConfirmMultipleDeletion = () => {
    const params = {
      templateId: deleteMultipleData.templateId,
      applicationKey: deleteMultipleData.applicationKey,
    };

    services.archivingTemplates(params).then((res) => {
      if (res.data) {
        onCloseConfirmMultipleDeleteDialog();
        onUploadESignDocumentTemplateSuccess();
      }
    });
  };

  const onCloseConfirmMultipleDeleteDialog = () => {
    setIsDeleteMultipleDialogOpen(false);
    setDeleteMultipleData(null);
  };

  const onUploadESignDocumentTemplateSuccess = useCallback(
    (data) => {
      toggleESignDocumentTemplateEditor(false);

      fetch();
    },
    [fetch, toggleESignDocumentTemplateEditor],
  );

  function onSaveSuccess() {
    fetch();
  }

  function onRestoreSuccess() {
    fetch();
  }

  /*reset a folder by changing
  the organization or the community*/
  useEffect(() => {
    setFolderPath([ROOT]);
    changeFilter({ folderId: null, folderName: null });
  }, [organizationId, communityId]);

  useEffect(() => {
    if (communityId) defer(600).then(fetch);
  }, [fetch, communityId]);

  const saveMulTemplateEsign = (params) => {
    if (editMultipleSignaturesRowData) {
      services.multipleSignatureTemplateEdit(params).then((res) => {
        if (res.success) {
          setEditMultipleSignaturesRowData(null);
          onUploadESignDocumentTemplateSuccess();
        }
      });
      return;
    }

    if (!params?.applicationKey) {
      setEditMultipleSignaturesRowData(null);
      return;
    }
    services.multipleSignatureTemplateAdded(params).then((res) => {
      if (res.success) {
        setEditMultipleSignaturesRowData(null);
        onUploadESignDocumentTemplateSuccess();
      }
    });
  };

  const multipleSignaturesClose = (isShowDialog) => {
    setShowMultipleSignatures(false);
    setIsSaveMulTemplateSuccessDialog(isShowDialog);
  };

  const { data: docsealKeys, isLoading: docsealKeysLoading, error: docsealKeysError } = useDocsealKeysQuery();

  const docsealKeysObj = docsealKeysToObject(docsealKeys?.data);

  return (
    <>
      <div className="Documents">
        <DocumentPrimaryFilter {...primary} className="margin-bottom-30" />
        <div className="Documents-Header">
          <div className="Documents-HeaderItem">
            <div className="Documents-Title">
              <span className="Documents-TitleText">Company Documents</span>
              <div className="d-inline-block text-nowrap">
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
              <Filter
                className={cn(
                  "FilterIcon",
                  "margin-right-24",
                  isFilterOpen ? "FilterIcon_rotated_90" : "FilterIcon_rotated_0",
                )}
                onClick={toggleFilter}
              />
              {canAddFolders && (
                <Button
                  color="success"
                  className="Documents-Action"
                  onClick={toggleFolderEditor}
                  // disabled={!user?.hieAgreement}
                >
                  Add Folder
                </Button>
              )}
              {canAdd && (
                <Button color="success" className="Documents-Action" onClick={toggleEditor}>
                  Upload Document
                </Button>
              )}

              {canCreateTemplate && (
                <Button color="success" className="Documents-Action" onClick={() => setShowMultipleSignatures(true)}>
                  Create Template
                </Button>
              )}
            </div>
          </div>
        </div>
        <Collapse isOpen={isFilterOpen}>
          <DocumentFilter
            {...custom}
            communityId={communityId}
            organizationId={organizationId}
            className="margin-bottom-20"
          />
        </Collapse>
        <DocumentList
          data={data}
          isEditEnabled
          hasLastModifiedDateCol
          pagination={pagination}
          noDataText="No Company documents"
          className="CommunityDocumentList"
          hasStatusCol={isTemplateFolder(parentFolder)}
          isFetching={
            isFetching ||
            isDeleting ||
            isRestoring ||
            isDeletingFolder ||
            isRestoringRegularFolder ||
            isDeletingTemplate ||
            isAssigningTemplate ||
            isFetchingDocumentCount
          }
          isDownloading={
            isDownloading ||
            isDownloadingFolder ||
            isDownloadingMultiple ||
            isDownloadingTemplate ||
            isDownloadingMultipleTemplates
          }
          getPath={(data) => path(`/documents/${data.id}-${data.title}`)}
          renderCaption={(children) => (
            <>
              {folderPath.length > 1 ? (
                <Breadcrumbs
                  className="Documents-FolderBreadcrumbs"
                  items={folderPath.map((o, i) => ({
                    title: o.title,
                    isEnabled: true,
                    isActive: i === folderPath.length - 1,
                    onClick: (e) => {
                      e.preventDefault();
                      onFolder(o);
                    },
                  }))}
                  renderSeparatorIcon={() => <Arrow className="Documents-BreadcrumbsSeparator" />}
                />
              ) : (
                <div>&nbsp;</div>
              )}
              {children}
            </>
          )}
          onEdit={edit}
          onSort={sort}
          onCopy={onCopyTemplate}
          onRefresh={refresh}
          onFolder={onFolder}
          onAssign={onAssign}
          onView={openManager}
          onDownloadAll={onDownloadAll}
          onDelete={onDelete}
          onDownloadSingle={onDownloadSingle}
          onRestore={openConfirmRestoreDialog}
          onDownloadMultiple={onDownloadMultiple}
          displayMultipleSignatures={() => setShowMultipleSignatures(true)}
          editMultopleSignatures={editMultopleSignatures}
          onMultopleDelete={onMultopleDelete}
          setShowMultipleSignaturesPreview={setShowMultipleSignaturesPreview}
          setShowMultipleSignatures={setShowMultipleSignatures}
          setShowCopyModel={setShowCopyModel}
        />
        <DocumentManager
          isOpen={isManagerOpen}
          documentId={selected?.id}
          documentName={selected?.title}
          templateId={selected?.templateId}
          documentMimeType={selected?.mimeType}
          folderId={parentFolder.folderId}
          onClose={onCloseManager}
          onSaveSuccess={onSaveSuccess}
          onEditTemplate={onEditTemplate}
          onRestoreSuccess={onRestoreSuccess}
          communityId={communityId}
          organizationId={organizationId}
          onDeleteSuccess={onDeleteSuccess}
        />
        <DocumentEditor
          isOpen={isEditorOpen}
          documentId={selected?.id}
          communityId={communityId}
          organizationId={organizationId}
          onClose={onCloseEditor}
          folderId={parentFolder.folderId}
          folderCategoryIds={selected ? [] : parentFolderCategoryIds}
          onUploadSuccess={onUploadSuccess}
        />
        <ESignDocumentTemplateEditor
          isOpen={isESignDocumentTemplateEditorOpen}
          isCopying={isCopying}
          templateId={selected?.templateId}
          documentId={selected?.id}
          communityId={communityId}
          organizationId={organizationId}
          onClose={onCloseTemplateEditor}
          onUploadSuccess={onUploadESignDocumentTemplateSuccess}
        />
        <ESignDocumentTemplateFolderAssigner
          isOpen={isESignDocTemplateFolderAssignerOpen}
          assignedFolders={assignedFolders}
          communityOptions={communityOptions}
          onSubmitSuccess={onAssignESignDocTemplateToFolder}
          onClose={onCloseESignDocTemplateFolderAssigner}
        />

        {/* !多人模版 */}
        {showMultipleSignatures && (
          <ESignMultipleSignatures
            docsealKeysObj={docsealKeysObj}
            docsealKeysLoading={docsealKeysLoading}
            docsealKeysError={docsealKeysError}
            isOpen={showMultipleSignatures}
            onClose={multipleSignaturesClose}
            organizationId={organizationId}
            communityId={communityId}
            folderId={folderId}
            saveMulTemplateEsign={saveMulTemplateEsign}
            editRowData={editMultipleSignaturesRowData}
            setEditMultipleSignaturesRowData={setEditMultipleSignaturesRowData}
          />
        )}

        {/* !多人模版预览 */}

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

        {/* Multi-person template copy pop-up window */}
        {showCopyModel && (
          <EsignCopy
            isOpen={showCopyModel}
            onClose={() => {
              setShowCopyModel(false);
              setEditMultipleSignaturesRowData(null);
            }}
            organizationId={organizationId}
            editMultipleSignaturesRowData={editMultipleSignaturesRowData}
          />
        )}

        {isSaveMulTemplateSuccessDialog && (
          <SuccessDialog
            isOpen={isSaveMulTemplateSuccessDialog}
            title={`The template has been created.`}
            buttons={[
              {
                text: "Close",
                onClick: () => {
                  setIsSaveMulTemplateSuccessDialog(false);
                },
              },
            ]}
          />
        )}

        {isFolderEditorOpen && (
          <DocumentFolderEditor
            communityId={communityId}
            isOpen={isFolderEditorOpen}
            folderId={selected?.folderId}
            onClose={onCloseFolderEditor}
            organizationId={organizationId}
            isSecurityEnabled={isSecurityEnabled}
            parentFolderId={parentFolder.folderId}
            canEditSecurity={!isTemplateFolder(parentFolder)}
            onSaveSuccess={onSaveFolderSuccess}
          />
        )}
        {isConfirmDeleteDialogOpen && (
          <ConfirmDialog
            isOpen
            icon={Warning}
            confirmBtnText="Delete"
            title={deletionText}
            onConfirm={onConfirmDeletion}
            onCancel={onCloseConfirmDeleteDialog}
          />
        )}

        {isDeleteMultipleDialogOpen && (
          <ConfirmDialog
            isOpen
            icon={Warning}
            confirmBtnText="Delete"
            title={deletionText}
            onConfirm={onConfirmMultipleDeletion}
            onCancel={onCloseConfirmMultipleDeleteDialog}
          />
        )}
        {isConfirmRestoreDialogOpen && (
          <ConfirmDialog
            isOpen
            icon={Warning}
            confirmBtnText="Restore"
            title={isFolder(selected) ? RESTORE_FOLDER_CONFIRM_TEXT : RESTORE_DOC_CONFIRM_TEXT}
            onConfirm={onRestore}
            onCancel={onCloseConfirmRestoreDialog}
          />
        )}
        {isDeletedFolderErrorDialogOpen && (
          <ErrorDialog
            isOpen
            title="Can't open this folder."
            text="The folder has been temporarily deleted. To access the folder contents, restore the folder."
            buttons={[
              {
                color: "success",
                outline: true,
                text: "Close",
                onClick: () => {
                  setSelected(null);
                  toggleDeletedFolderErrorDialog(false);
                },
              },
              {
                color: "success",
                text: "Restore",
                onClick: () => {
                  onRestore();
                  toggleDeletedFolderErrorDialog(false);
                },
              },
            ].filter((buttons) => {
              return buttons.text !== "Restore" || selected.canDelete;
            })}
          />
        )}

        {isReassignTemplatesDialogOpen && (
          <WarningDialog
            isOpen
            title={`${selectedFolderDocumentsCount} template(s) found in the folder. Please reassign template(s) to another folder.`}
            buttons={[{ text: "Close", onClick: closeReassignTemplateDialog }]}
          />
        )}

        {isTemplateReassignedDialogOpen && (
          <SuccessDialog
            isOpen
            title="The updates have been saved"
            buttons={[
              {
                text: "Close",
                onClick: () => toggleTemplateReassignedDialog(false),
              },
            ]}
          />
        )}

        {isInstructionDialogOpen && (
          <Dialog
            isOpen
            title="Paperless experience."
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
            The Company Documents feature will allow you to:
            <ul>
              <li>Store, manage and track community documents.</li>
              <li>Add tags (categories) to organize documents and folders.</li>
              <li>Create folders with different permissions: Admin, Read-only, Upload-only.</li>
            </ul>
            <p>
              If you use Electronic Signature feature, open Templates folder to review the templates available for your
              community.
            </p>
            <p>
              To request a signature from Staff, Client or Parent/Guardian or Client Legal Representative, select a
              Client on Clients screen and navigate to Client Documents screen.
            </p>
          </Dialog>
        )}

        {isNoFolderAccessDialogOpen && (
          <ErrorDialog
            isOpen
            title="No access to the folder"
            buttons={[
              {
                color: "success",
                text: "Close",
                onClick: () => toggleNoFolderAccessDialog(),
              },
            ]}
          />
        )}
        {isNoLongerFolderAccessDialogOpen && <ErrorDialog isOpen title="You have no longer access to this folder" />}
        {error && !isIgnoredError(error) && <ErrorViewer isOpen error={error} onClose={clearError} />}
      </div>
      <Footer theme="gray" />

      {/*      {
        displayMultipleSignaturesModel
        &&
        <ESignMultipleSignaturesEdit
          editRowData={editMultipleSignaturesRowData}
          isOpen={displayMultipleSignaturesModel}
          onClose={() => {
            setDisplayMultipleSignaturesModel(false)
            setEditMultipleSignaturesRowData(null);
          }}/>
      }*/}
    </>
  );
}

export default memo(Documents);
