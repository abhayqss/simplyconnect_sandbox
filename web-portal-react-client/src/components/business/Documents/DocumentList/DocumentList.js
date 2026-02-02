import React, { memo, useCallback, useEffect, useMemo, useState } from "react";

import cn from "classnames";
import PTypes from "prop-types";
import { filesize } from "filesize";

import { Link } from "react-router-dom";

import { any, compact, pluck, reject } from "underscore";

import { Button, UncontrolledTooltip as Tooltip } from "reactstrap";

import { TDocumentList } from "types";

import { useAuthUser, useMutationWatch } from "hooks/common";

import { CollapsibleText, FileFormatIcon, Table } from "components";

import { CheckboxField } from "components/Form";

import {
  AddButton,
  DeleteButton,
  DownloadButton,
  EditButton,
  FolderButton,
  IconButton,
  RestoreButton,
} from "components/buttons";

import { DateUtils as DU, findIndexes, isEmpty, isNotEmpty, uc } from "lib/utils/Utils";

import { noop } from "lib/utils/FuncUtils";

import { path } from "lib/utils/ContextUtils";

import { CLIENT_DOCUMENT_TYPES } from "lib/Constants";

import { ReactComponent as Folder } from "images/folder-3.svg";
import { ReactComponent as Stylus } from "images/stylus.svg";
import { ReactComponent as LockedFolder } from "images/folder-locked.svg";

import { DocumentCategories, DocumentSignatureStatusIcon } from "../";

import "./DocumentList.scss";
import { ConfirmDialog } from "../../../dialogs";
import { ReactComponent as Warning } from "images/alert-yellow.svg";

const { format, formats } = DU;

const DATE_FORMAT = formats.americanMediumDate;

const { CCD } = CLIENT_DOCUMENT_TYPES;

const ALL = "ALL";
const PAGE = "PAGE";
const SELECTED = "SELECTED";

const DOCUMENT_TYPE = {
  FOLDER: "folder",
  TEMPLATE: "template",
  FILE: "file",
};

function getDeletionHint(row) {
  const prefix = row.isTemporarilyDeleted || row.type === "TEMPLATE" ? "Permanently" : "Temporarily";

  const typeText = DOCUMENT_TYPE[row.type] ?? DOCUMENT_TYPE["FILE"];

  return row.type === "TEMPLATE" ? `Delete template` : `${prefix} delete ${typeText}`;
}

function DocumentList({
  data,
  pagination,
  isFetching,
  isDownloading,
  noDataText,
  defaultSelected = [],
  selectedMaxCount,

  hasTypeCol,
  hasStatusCol,
  hasAuthorCol,
  hasCreatedDateCol,
  hasSizeCol = true,
  hasActionsCol = true,
  hasSignatureStatusCol,
  hasLastModifiedDateCol,
  hasDescriptionCol = true,

  isEditEnabled = false,
  isESignEnabled = false,

  renderCaption,

  getPath,
  onSort,
  onView,
  onEdit,
  onCopy,
  onSign,
  onAssign,
  onFolder,
  onDownloadSingle,
  onDownloadMultiple,
  onDownloadAll,
  onDelete,
  onRestore,
  onRefresh,
  onSelect: onSelectCb = noop,

  className,
  captionClass,

  editMultopleSignatures, // Edited template data

  onMultopleDelete,

  setShowMultipleSignatures, // Display multiplayer template.
  setShowMultipleSignaturesPreview, // Multiplayer template preview settings
  setShowCopyModel, //copy model
}) {
  const [selected, setSelected] = useState(defaultSelected);
  const [nonSelectable, setNonSelectable] = useState([]);
  const [downloadMode, setDownloadMode] = useState(PAGE);
  const [showWaring, setShowWaring] = useState(false);
  const [oldEsignData, setOldEsignData] = useState();
  const user = useAuthUser();

  const { page } = pagination;

  const columns = useMemo(
    () =>
      compact([
        {
          dataField: "title",
          text: "Title",
          sort: true,
          onSort,
          formatter: (v, row) => {
            return (
              <div className="d-flex flex-row align-items-center overflow-hidden">
                {["FOLDER", "TEMPLATE_FOLDER"].includes(row.type) ? (
                  <>
                    <div className="position-relative">
                      {row.isSecurityEnabled ? (
                        <LockedFolder className="DocumentList-FolderIcon" />
                      ) : (
                        <Folder className="DocumentList-FolderIcon" />
                      )}
                    </div>
                    <Link
                      id={row.id}
                      target="_blank"
                      to={path(`/document-folders/${row.folderId}`)}
                      className={cn("DocumentList-FolderTitleText", "DocumentList-Link")}
                      onClick={(e) => {
                        e.preventDefault();
                        onFolder(row);
                      }}
                    >
                      {v}
                    </Link>
                    <Tooltip
                      target={row.id}
                      boundariesElement={document.body}
                      modifiers={[
                        {
                          name: "offset",
                          options: { offset: [0, 6] },
                        },
                        {
                          name: "preventOverflow",
                          options: { boundary: document.body },
                        },
                      ]}
                    >
                      Open the folder
                    </Tooltip>
                  </>
                ) : (
                  <>
                    <FileFormatIcon mimeType={row.mimeType} className="DocumentList-DocFormatIcon" />
                    <div className="DocumentList-DocTitle">
                      {row.multi ? (
                        <div
                          style={{ fontWeight: 900, color: "#0064ad", cursor: "pointer" }}
                          onClick={() => {
                            setShowMultipleSignaturesPreview(true);
                            editMultopleSignatures(row);
                          }}
                        >
                          {v}
                        </div>
                      ) : (
                        <div
                          onClick={() => {
                            setShowWaring(true);
                            setOldEsignData(row);
                          }}
                        >
                          {v}
                        </div>
                      )}
                    </div>
                  </>
                )}
              </div>
            );
          },
        },
        hasStatusCol && {
          sort: false,
          text: "Status",
          dataField: "statusTitle",
          classes: "hide-on-tablet",
          headerStyle: { width: "10%" },
          headerClasses: "hide-on-tablet",
        },
        {
          dataField: "categories",
          text: "Category",
          classes: "hide-on-mobile ",
          headerStyle: hasLastModifiedDateCol ? { width: "200px" } : {},
          headerClasses: "hide-on-mobile DocumentList-CategoryCol",
          formatter: (categories) => {
            return <DocumentCategories data={categories} />;
          },
        },
        hasTypeCol && {
          dataField: "type",
          text: "Type",
          sort: true,
          onSort,
          headerStyle: { width: "7%" },
          classes: "hide-on-tablet DocumentList-TypeCol",
          headerClasses: "hide-on-tablet DocumentList-TypeCol",
        },
        hasSizeCol && {
          dataField: "size",
          text: "Size",
          sort: true,
          align: "right",
          headerAlign: "right",
          onSort,
          formatter: (v) => (v ? uc(filesize(v, { round: 2 })) : ""),
          classes: "hide-on-tablet",
          headerClasses: "hide-on-tablet DocumentList-SizeCol",
        },
        hasLastModifiedDateCol && {
          dataField: "lastModifiedDate",
          text: "Last edited",
          sort: true,
          align: "right",
          headerAlign: "right",
          onSort,
          headerStyle: { width: "12%" },
          classes: "hide-on-tablet",
          headerClasses: "hide-on-tablet",
          formatter: (v) => format(v, DATE_FORMAT),
        },
        hasCreatedDateCol && {
          dataField: "createdDate",
          text: "Created",
          sort: true,
          align: "right",
          headerAlign: "right",
          onSort,
          classes: "hide-on-tablet DocumentList-CreatedCol",
          headerClasses: "hide-on-tablet DocumentList-CreatedCol",
          formatter: (v) => format(v, DATE_FORMAT),
        },
        hasAuthorCol && {
          dataField: "author",
          text: "Author",
          sort: true,
          onSort,
          headerStyle: { width: "10%" },
          classes: "hide-on-tablet DocumentList-AuthorCol",
          headerClasses: "hide-on-tablet DocumentList-AuthorCol",
        },
        hasDescriptionCol && {
          dataField: "description",
          text: "Description",
          classes: `hide-on-mobile DocumentList-DescriptionCol${hasLastModifiedDateCol ? "1" : "2"}`,
          headerClasses: `hide-on-mobile DocumentList-DescriptionCol${hasLastModifiedDateCol ? "1" : "2"}`,
          formatter: (description) => {
            return <CollapsibleText>{description}</CollapsibleText>;
          },
        },
        hasSignatureStatusCol && {
          dataField: "signature",
          text: "Signature",
          classes: "hide-on-mobile",
          align: "center",
          headerStyle: { width: "100px" },
          headerClasses: "hide-on-mobile",
          formatter: (v) => {
            return v ? <DocumentSignatureStatusIcon statusName={v.statusName} statusTitle={v.statusTitle} /> : "";
          },
        },
        hasActionsCol && {
          dataField: "@actions",
          text: "",
          headerStyle: { width: "300px" },
          classes: "DocumentList-ActionsCell",
          formatter: (v, row) => {
            return (
              <div className="DocumentList-Actions">
                {!row.multi && row.isTemporarilyDeleted && row.canDelete && (
                  <RestoreButton
                    id={`restore-document-${row.id}-btn`}
                    tipText={`Restore ${["FOLDER", "TEMPLATE_FOLDER"].includes(row.type) ? "folder with files and subfolders if any" : "file"}`}
                    className="DocumentList-Action"
                    onClick={() => onRestore(row)}
                  />
                )}

                {row.multi && row.type === "Custom" && (
                  <>
                    <IconButton
                      size={36}
                      Icon={Stylus}
                      disabled={!row.signature?.canSign} //&& !row.signature?.canSign
                      onClick={() => {
                        if (!row.signature?.canSign) {
                          return;
                        }

                        window.open(`https://docseal.simplyconnect.me/s/${row.slug}`, "_blank");
                      }}
                    />
                  </>
                )}
                {/* 下载按钮 */}

                {!row.multi && isESignEnabled && !row.isTemporarilyDeleted && (
                  <IconButton
                    size={36}
                    Icon={Stylus}
                    disabled={!row.signature?.canSign}
                    tooltip={row.signature?.canSign && "Sign Document"}
                    onClick={() => row.signature?.canSign && onSign(row)}
                  />
                )}
                {/* Single person template copy */}
                {row.multi && row.type === "TEMPLATE" && row.canCopy && (
                  <AddButton
                    id={`document-${row.id}-copy-btn`}
                    tipText="Create a copy of E-sign template"
                    className="DocumentList-Action"
                    onClick={() => {
                      setShowCopyModel(true);
                      editMultopleSignatures(row);
                    }}
                  />
                )}

                {/* Multi-person template copy */}
                {/* Click to select storage location, whether to change the name. */}
                {/*         {!row.multi && row.type === 'TEMPLATE' && row.canCopy && (
                <AddButton
                  id={`document-${row.id}-copy-btn`}
                  tipText="Create a copy of E-sign template"
                  className="DocumentList-Action"
                  onClick={() => onCopy(row)}
                />
              )}*/}

                {/* Single-person template move folder button */}
                {/*        {!row.multi && row.canAssign && (
                <FolderButton
                  className="DocumentList-Action"
                  tipText="Assign template to folder"
                  id={`assign-document-${row.id}-btn`}

                  onClick={() =>{
                    console.log(row)
                    onAssign(row)
                  }}
                />
              )}*/}

                {row.multi && row.canAssign && (
                  <FolderButton
                    className="DocumentList-Action"
                    tipText="Assign template to folder"
                    id={`assign-document-${row.id}-btn`}
                    onClick={() => {
                      onAssign(row);
                    }}
                  />
                )}

                {!row.multi &&
                  !(row.type === "TEMPLATE_FOLDER") &&
                  !(row.type === "FOLDER") &&
                  !(row.type === "FOLDER" && (!row.canView || row.isTemporarilyDeleted)) && (
                    <DownloadButton
                      id={`document-${row.id}_download-btn`}
                      tipText={`Download ${["FOLDER", "TEMPLATE_FOLDER"].includes(row.type) ? "folder contents" : "file"}`}
                      className="DocumentList-Action"
                      onClick={() => {
                        onDownloadSingle(row);
                      }}
                    />
                  )}

                {/* 单人发送记录多人模版下载按钮 */}
                {row.eventType === "complete_form" && row.multi && row.type === "Custom" && (
                  <>
                    <DownloadButton
                      id={`document-${row.id}_download-btn`}
                      className="DocumentList-Action-Multi"
                      onClick={() => downloadMultiplePdf(row)}
                    />
                  </>
                )}

                {row.applicationKey && row.multi && row.type === "TEMPLATE" && (
                  <DownloadButton
                    id={`document-${row.id}_download-btn`}
                    className="DocumentList-Action-Multi"
                    onClick={() => downloadMultiplePdf(row)}
                  />
                )}

                {/*       {
                row.applicationKey && row.multi && row.type === 'TEMPLATE' && (
                  <IconButton
                    size={36}
                    Icon={Priview}
                    className="DocumentList-Action-Multi"
                    onClick={() => {
                      setShowMultipleSignaturesPreview(true)
                      editMultopleSignatures(row)
                    }}
                  />
                )}*/}

                {/* 目录的修改按钮  */}
                {!row.multi &&
                  isEditEnabled &&
                  row.canEdit &&
                  ["FOLDER", "TEMPLATE_FOLDER"].includes(row.type) &&
                  (!row.multi || false) && (
                    <EditButton
                      id={`edit-${row.id}-btn`}
                      tipText={`Edit ${row.type === "FOLDER" ? "folder" : "template"}`}
                      className="DocumentList-Action"
                      onClick={() => onEdit(row)}
                    />
                  )}

                {/* !多人模版编辑 */}
                {row.applicationKey && isEditEnabled && row.canEdit && row.multi && (
                  <>
                    <EditButton
                      id={`edit-${row.id}-btn`}
                      tipText={`Edit ${row.type === "FOLDER" ? "folder" : "template"}`}
                      className="DocumentList-Action"
                      onClick={() => {
                        setShowMultipleSignatures(true);
                        editMultopleSignatures(row);
                      }}
                    />
                  </>
                )}

                {/* 旧单人模版删除按钮 */}
                {!row.multi && row.canDelete && (
                  <DeleteButton
                    id={`delete-document-${row.id}-btn`}
                    tipText={getDeletionHint(row)}
                    className="DocumentList-Action"
                    onClick={() => onDelete(row)}
                  />
                )}

                {row.applicationKey && row.multi && row.canDelete && row.type === "TEMPLATE" && (
                  <DeleteButton
                    id={`delete-document-${row.id}-btn`}
                    tipText={getDeletionHint(row)}
                    className="DocumentList-Action"
                    onClick={() => onMultopleDelete(row)}
                  />
                )}
              </div>
            );
          },
        },
      ]),
    [
      onSign,
      onSort,
      onView,
      onEdit,
      onCopy,
      onFolder,
      onDelete,
      onAssign,
      onRestore,
      onDownloadSingle,
      getPath,
      isEditEnabled,
      isESignEnabled,
      hasTypeCol,
      hasActionsCol,
      hasSizeCol,
      hasDescriptionCol,
      hasStatusCol,
      hasAuthorCol,
      hasCreatedDateCol,
      hasSignatureStatusCol,
      hasLastModifiedDateCol,
    ],
  );

  const dropdownOptions = useMemo(
    () =>
      compact([
        isNotEmpty(selected)
          ? {
              text: "Selected items",
              value: SELECTED,
              onClick: setDownloadMode,
              hasIndicator: true,
              toggle: false,
              className: "DocumentList-DownloadMode",
              renderIndicator: () => (
                <CheckboxField
                  isRadio
                  value={downloadMode === SELECTED}
                  className="DocumentList-DownloadModeCheckbox"
                />
              ),
            }
          : null,
        {
          text: "This page",
          value: PAGE,
          onClick: setDownloadMode,
          hasIndicator: true,
          toggle: false,
          className: "DocumentList-DownloadMode",
          renderIndicator: () => (
            <CheckboxField isRadio value={downloadMode === PAGE} className="DocumentList-DownloadModeCheckbox" />
          ),
        },
        pagination.totalCount > data?.length
          ? {
              text: "All pages",
              value: ALL,
              onClick: setDownloadMode,
              hasIndicator: true,
              toggle: false,
              className: "DocumentList-DownloadMode",
              renderIndicator: () => (
                <CheckboxField isRadio value={downloadMode === ALL} className="DocumentList-DownloadModeCheckbox" />
              ),
            }
          : null,
        {
          key: "download",
          toggle: true,
          render: () => (
            <div className="padding-5">
              <Button
                tag="a"
                color="success"
                disabled={isDownloading}
                className="DocumentList-DownloadAction"
                onClick={() => {
                  if (downloadMode === ALL) onDownloadAll();
                  else if (downloadMode === SELECTED) onDownloadMultiple(selected);
                  else onDownloadMultiple(data);
                }}
              >
                Download
              </Button>
            </div>
          ),
        },
      ]),
    [data, selected, downloadMode, onDownloadAll, isDownloading, onDownloadMultiple, pagination.totalCount],
  );

  const dropdown = <></>;

  const selectionExcludedRowIndexes = useMemo(
    () =>
      findIndexes(
        data,
        (o) =>
          (o.isSecurityEnabled && !o.canView) ||
          (o.isTemporarilyDeleted && ["FOLDER", "TEMPLATE_FOLDER"].includes(o.type)),
      ),
    [data],
  );

  const allNonSelectableRowIndexes = useMemo(
    () => [...nonSelectable, ...selectionExcludedRowIndexes],
    [nonSelectable, selectionExcludedRowIndexes],
  );

  const onSelect = useCallback(
    (row, shouldSelect, i) => {
      if (!selectionExcludedRowIndexes.includes(i)) {
        if (shouldSelect && selected.length === selectedMaxCount) {
          setNonSelectable(reject(data, (o) => any(selected, ({ id }) => id === o.id)));
        } else {
          const next = shouldSelect ? [...selected, row] : reject(selected, (o) => o.id === row.id);

          setSelected(next);
          setDownloadMode(isEmpty(next) ? PAGE : SELECTED);

          if (next.length === selectedMaxCount) {
            setNonSelectable(reject(data, (o) => any(next, ({ id }) => id === o.id)));
          } else setNonSelectable([]);
        }
      }
    },
    [data, selected, selectedMaxCount, selectionExcludedRowIndexes],
  );

  const onSelectAll = useCallback(
    (shouldSelect, rows) => {
      const selectableRows = shouldSelect ? rows.filter((o, i) => !allNonSelectableRowIndexes.includes(i)) : rows;

      //react-next-table lib bug fix
      if (!shouldSelect && isEmpty(selectableRows)) return;

      if (shouldSelect) {
        const next = [...selected, ...selectableRows];

        if (selectedMaxCount < next.length) {
          setNonSelectable(next.slice(selectedMaxCount));
        }

        setSelected(next.slice(0, selectedMaxCount));
      } else {
        setNonSelectable([]);
        setSelected(reject(selected, (o1) => any(selectableRows, (o2) => o2.id === o1.id)));
      }

      setDownloadMode(shouldSelect && selectableRows.length ? SELECTED : PAGE);
    },
    [selected, selectedMaxCount, allNonSelectableRowIndexes],
  );

  useMutationWatch(data, () => {
    if (page === 1) {
      setDownloadMode(PAGE);
    }
  });

  useEffect(() => {
    if (selected.length !== defaultSelected.length) {
      onSelectCb(selected);
    }
  }, [selected, defaultSelected, onSelectCb]);

  const downloadMultiplePdf = (row) => {
    // const pdfUrl = row.documentUrl;

    let pdfUrl;

    if (row.type === "TEMPLATE") {
      pdfUrl = row.documentUrl;
    } else {
      pdfUrl = row.downloadDocumentUrl;
    }

    // 请求PDF文件并将其转换为Blob流
    fetch(pdfUrl)
      .then((response) => {
        // 检查响应是否成功
        if (!response.ok) {
          throw new Error("Network request failed");
        }
        // 将响应数据转换为Blob
        return response.blob();
      })
      .then((pdfBlob) => {
        // 创建Blob链接
        const blobUrl = URL.createObjectURL(pdfBlob);

        // 创建一个虚拟的 <a> 元素
        const link = document.createElement("a");
        link.href = blobUrl; // 设置链接的 href 属性为Blob链接
        link.target = "_blank"; // 打开新窗口/标签页下载文件
        link.download = row.title; // 设置下载文件的名称

        // 添加到DOM中才能触发下载
        document.body.appendChild(link);

        // 模拟点击 <a> 元素来触发下载
        link.click();

        // 删除Blob链接和 <a> 元素以避免页面上的残留
        URL.revokeObjectURL(blobUrl);
        document.body.removeChild(link); // 下载完成后移除元素
      })
      .catch((error) => {
        // 处理请求失败的情况
        console.log(error);
        // throw new Error(error);
      });
  };

  return (
    <>
      <Table
        hasHover
        hasOptions
        hasPagination
        keyField="id"
        title="Documents"
        noDataText={noDataText}
        isLoading={isFetching}
        className={cn("DocumentList", className)}
        containerClass="DocumentListContainer"
        data={data}
        pagination={pagination}
        columns={columns}
        columnsMobile={["title", "author"]}
        getRowClass={(row, i) =>
          cn(
            i % 2 === 0 ? "odd" : "even",
            "DocumentList-Document",
            { "DocumentList-Document_security": row.isSecurityEnabled },
            { "DocumentList-Document_deleted": row.isTemporarilyDeleted },
          )
        }
        selectedRows={{
          mode: "checkbox",
          clickToSelect: false,
          selected: pluck(selected, "id"),
          onSelect,
          onSelectAll,
          nonSelectable: allNonSelectableRowIndexes,
          style: { backgroundColor: "#edf4f5" },
          selectionRenderer: ({ checked, rowIndex }) => (
            <CheckboxField value={checked && !allNonSelectableRowIndexes.includes(rowIndex)} />
          ),
          selectionHeaderRenderer: () => (
            <CheckboxField value={selected.length > 0 && selected.length === data.length} />
          ),
        }}
        renderCaption={() => (
          <div className={cn("DocumentList-Caption", captionClass)}>
            {renderCaption ? renderCaption(dropdown) : dropdown}
          </div>
        )}
        onRefresh={onRefresh}
      />

      <ConfirmDialog
        isOpen={showWaring}
        icon={Warning}
        confirmBtnText="Download"
        title={`The signature module has been updated. Please upload the template again.`}
        onConfirm={() => {
          onDownloadSingle(oldEsignData);
          setShowWaring(false);
        }}
        onCancel={() => setShowWaring(false)}
      />
    </>
  );
}

DocumentList.propTypes = {
  ...TDocumentList,
  defaultSelected: PTypes.shape({ id: PTypes.number }),
  selectedMaxCount: PTypes.number,
  onSelect: PTypes.func,
  onCopy: PTypes.func,
  onFolder: PTypes.func,
  onAssign: PTypes.func,
  renderCaption: PTypes.func,
};

DocumentList.defaultProps = {
  data: [],
  onCopy: noop,
  noDataText: "No documents",
};

export default memo(DocumentList);
