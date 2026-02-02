import React, { useCallback } from "react";

import cn from "classnames";
import { noop } from "underscore";
import PropTypes from "prop-types";

import { UncontrolledTooltip as Tooltip } from "reactstrap";

import { ReactComponent as Add } from "images/add-item.svg";
import { ReactComponent as Cog } from "images/cog.svg";
import { ReactComponent as Asset } from "images/asset.svg";
import { ReactComponent as Pencil } from "images/pencil.svg";
import { ReactComponent as Delete } from "images/delete.svg";
import { ReactComponent as Info } from "images/actions-info.svg";
import { ReactComponent as Download } from "images/download.svg";
import { ReactComponent as VideoCall } from "images/videocall.svg";
import { ReactComponent as Remove } from "images/remove.svg";
import { ReactComponent as Confirm } from "images/confirm.svg";
import { ReactComponent as Archive } from "images/workflow/archive.svg";
import { ReactComponent as Unlink } from "images/workflow/unlink.svg";

import "./Actions.scss";

export default function Actions(props) {
  const {
    data,

    className,
    infoHintClassName,

    addHintMessage,
    infoHintMessage,
    editHintMessage,
    assetHintMessage,
    deleteHintMessage,
    archiveHitMessage,
    downloadHintMessage,
    videoCallHintMessage,
    configureHintMessage,
    removeMessage,
    unlinkMessage,
    confirmMessage,

    hasAddAction,
    hasInfoAction,
    hasEditAction,
    hasAssetAction,
    hasDeleteAction,
    hasDownloadAction,
    hasConfigureAction,
    hasVideoCallAction,
    hasConfirm,
    hasRemove,
    hasUnlink,
    cannotDownload,
    renderInfoHint,
    disabled,
    hasArchive,
  } = props;

  const onAdd = useCallback(() => {
    props.onAdd(data);
  }, [data]);
  const onInfo = useCallback(() => {
    props.onInfo(data);
  }, [data]);
  const onEdit = useCallback(() => {
    props.onEdit(data);
  }, [data]);
  const onAsset = useCallback(() => {
    props.onAsset(data);
  }, [data]);
  const onDelete = useCallback(() => {
    props.onDelete(data);
  }, [data]);
  const onArchive = useCallback(() => {
    props.onArchive(data);
  }, [data]);
  const onDownload = useCallback(() => {
    props.onDownload(data);
  }, [data]);
  const onConfigure = useCallback(() => {
    props.onConfigure(data);
  }, [data]);
  const onVideoCall = useCallback(() => {
    props.onVideoCall(data);
  }, [data]);
  const onConfirm = useCallback(() => {
    props.onConfirm(data);
  }, [data]);
  const onRemove = useCallback(() => {
    props.onRemove(data);
  }, [data]);
  const onUnlink = useCallback(() => {
    props.onUnlink(data);
  }, [data]);

  return (
    <div className={cn("Actions", className)}>
      {hasAddAction && (
        <>
          <Add disabled={disabled} id={"add" + data.id} className="Actions-Item" onClick={onAdd} />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"add" + data.id}
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
            {addHintMessage}
          </Tooltip>
        </>
      )}
      {hasInfoAction && (
        <>
          <Info id={"info" + data.id} className="Actions-Item" onClick={onInfo} />
          <Tooltip
            className={infoHintClassName}
            placement="left-start"
            target={"info" + data.id}
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
            {renderInfoHint && renderInfoHint()}
          </Tooltip>
        </>
      )}
      {hasConfigureAction && (
        <>
          <Cog id={"configure" + data.id} className="Actions-Item" onClick={onConfigure} />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"configure" + data.id}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
            ]}
          >
            {configureHintMessage}
          </Tooltip>
        </>
      )}
      {hasVideoCallAction && (
        <>
          <VideoCall
            id={"video-call" + data.id}
            style={{ marginTop: 4 }}
            className="Actions-Item"
            onClick={onVideoCall}
          />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"video-call" + data.id}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
            ]}
          >
            {videoCallHintMessage}
          </Tooltip>
        </>
      )}
      {hasDownloadAction && (
        <>
          <Download
            id={"download" + data.id}
            className={cannotDownload ? "notAllowed" : "Actions-Item"}
            onClick={onDownload}
          />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"download" + data.id}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
            ]}
          >
            {downloadHintMessage}
          </Tooltip>
        </>
      )}
      {hasEditAction && (
        <>
          <Pencil id={"edit" + data.id} className={"Actions-Item"} onClick={onEdit} />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"edit" + data.id}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
            ]}
          >
            {editHintMessage}
          </Tooltip>
        </>
      )}

      {hasAssetAction && (
        <>
          <Asset id={"asset" + data.id} className="Actions-Item" onClick={onAsset} />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"asset" + data.id}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
            ]}
          >
            {assetHintMessage}
          </Tooltip>
        </>
      )}
      {hasDeleteAction && (
        <>
          <Delete id={"delete" + data.id} className="Actions-Item" onClick={onDelete} />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"delete" + data.id}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
            ]}
          >
            {deleteHintMessage}
          </Tooltip>
        </>
      )}

      {hasArchive && (
        <>
          <Archive id={"archive" + data.id} className="Actions-Item-archive" onClick={onArchive} />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"archive" + data.id}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
            ]}
          >
            {archiveHitMessage}
          </Tooltip>
        </>
      )}

      {hasRemove && (
        <>
          <Remove id={"remove" + data.id} className="Actions-Item-1" onClick={onRemove} />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"remove" + data.id}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
            ]}
          >
            {removeMessage}
          </Tooltip>
        </>
      )}

      {hasUnlink && (
        <>
          <Unlink id={"unlink" + data.id} className="Actions-Item Actions-Item-Unlink" onClick={onUnlink} />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"unlink" + data.id}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
            ]}
          >
            {unlinkMessage}
          </Tooltip>
        </>
      )}

      {hasConfirm && (
        <>
          <Confirm id={"confirm" + data.id} className="Actions-Item-1" onClick={onConfirm} />
          <Tooltip
            placement="top"
            trigger="click hover"
            target={"confirm" + data.id}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
            ]}
          >
            {confirmMessage}
          </Tooltip>
        </>
      )}
    </div>
  );
}

Actions.propTypes = {
  data: PropTypes.object,
  hasAddAction: PropTypes.bool,
  hasInfoAction: PropTypes.bool,
  hasEditAction: PropTypes.bool,
  hasAssetAction: PropTypes.bool,
  hasDeleteAction: PropTypes.bool,
  hasArchive: PropTypes.bool,
  hasDownloadAction: PropTypes.bool,
  hasVideoCallAction: PropTypes.bool,
  hasConfigureAction: PropTypes.bool,
  hasConfirm: PropTypes.bool,
  hasRemove: PropTypes.bool,
  hasUnlink: PropTypes.bool,

  className: PropTypes.string,
  infoHintClassName: PropTypes.string,

  addHintMessage: PropTypes.string,
  infoHintMessage: PropTypes.string,
  editHintMessage: PropTypes.string,
  assetHintMessage: PropTypes.string,
  deleteHintMessage: PropTypes.string,
  archiveHitMessage: PropTypes.string,
  downloadHintMessage: PropTypes.string,
  videoCallHintMessage: PropTypes.string,
  configureHintMessage: PropTypes.string,

  onAdd: PropTypes.func,
  onInfo: PropTypes.func,
  onEdit: PropTypes.func,
  onAsset: PropTypes.func,
  onDelete: PropTypes.func,
  onArchive: PropTypes.func,
  onDownload: PropTypes.func,
  onVideoCall: PropTypes.func,
  onConfigure: PropTypes.func,
  renderInfoHint: PropTypes.func,
  onConfirm: PropTypes.func,
  onRemove: PropTypes.func,
  onUnlink: PropTypes.func,
};

Actions.defaultProps = {
  data: {},

  addHintMessage: "Add",
  infoHintMessage: "Info",
  editHintMessage: "Edit",
  assetHintMessage: "Asset",
  deleteHintMessage: "Delete",
  archiveHitMessage: "Archive",
  downloadHintMessage: "Download",
  videoCallHintMessage: "Video Call",
  configureHintMessage: "Configure",
  confirmHintMessage: "Confirm",
  removeHintMessage: "Remove",
  unlinkMessage: "Unlink",

  onAdd: noop,
  onInfo: noop,
  onEdit: noop,
  onAsset: noop,
  onDelete: noop,
  onArchive: noop,
  onDownload: noop,
  onVideoCall: noop,
  onConfigure: noop,
  renderInfoHint: noop,
  onConfirm: noop,
  onRemove: noop,
  onUnlink: noop,
};
