import React, { Component } from "react";

import $ from "jquery";
import cn from "classnames";
import PropTypes from "prop-types";

import { Button, FormGroup, Input, Label, UncontrolledTooltip as Tooltip } from "reactstrap";

import "./FileField.scss";
import Modal from "../../Modal/Modal";
import ImageCrop from "./ImageCrop";
import getCroppedImg from "./cropImage";

const ALLOWED_MIME_TYPES = ["image/jpeg", "image/jpg", "image/png", "image/gif"];

function getRandom() {
  return Math.random() * 1000000000000;
}

export default class FileField extends Component {
  static propTypes = {
    name: PropTypes.string,
    label: PropTypes.string,
    isDisabled: PropTypes.bool,
    allowedTypes: PropTypes.arrayOf(PropTypes.string),

    tooltip: PropTypes.object,

    className: PropTypes.string,
    placeholder: PropTypes.string,

    hasHint: PropTypes.bool,
    hintText: PropTypes.string,

    hasError: PropTypes.bool,
    errorText: PropTypes.string,

    onChange: PropTypes.func,
    renderLabelIcon: PropTypes.func,
    aspect: PropTypes.number,
  };

  static defaultProps = {
    value: "",
    isDisabled: false,
    placeholder: "File not chosen",
    allowedTypes: ALLOWED_MIME_TYPES,

    hasHint: false,
    hintText: "",

    hasError: false,
    errorText: "",

    onChange: function () {},
  };

  isBrowsingEnabled = false;

  inputRef = React.createRef();

  state = {
    value: this.props.value || "",
    cropModelShow: false,
    fileToCrop: "",
  };

  componentDidUpdate(prevProps) {
    const { value } = this.props;

    if (value !== prevProps.value) {
      this.setState({ value });
    }
  }

  onInputClick = (e) => {
    if (this.isBrowsingEnabled) {
      this.disableBrowsing();
    } else e.preventDefault();
  };

  onChange = () => {
    const file = this.getInputNode().files[0];

    if (file) {
      this.setState({
        value: file.name,
        cropModelShow: true, // 打开裁剪弹窗
        fileToCrop: file, // 存储选择的文件
      });
    }
  };

  onCropComplete = async (file) => {
    this.setState({ cropModelShow: false });
    const { name, onChange: cb } = this.props;
    // 调用回调函数通知父组件文件变化
    cb(name, file);
  };

  onBrowse = () => {
    this.browse();
  };

  onRemove = (e) => {
    if (e.detail) {
      this.remove();
    }
  };

  browse() {
    this.enableBrowsing(true);
    $(this.getInputNode()).trigger("click");
  }

  remove() {
    const { onChange: cb, name } = this.props;

    this.getInputNode().value = null;
    this.setState({ value: null });

    cb(name, null);
  }

  enableBrowsing() {
    this.isBrowsingEnabled = true;
  }

  disableBrowsing() {
    this.isBrowsingEnabled = false;
  }

  getInputNode() {
    return this.inputRef.current || {};
  }

  changeCropModelShow = (status) => {
    this.setState({ cropModelShow: status });
  };

  render() {
    const {
      name,
      label,
      allowedTypes,
      isDisabled,

      className,
      placeholder,
      renderLabelIcon,

      tooltip,
      hasHint,
      hintText,

      errorText,
      aspect,
    } = this.props;

    const { value, cropModelShow, fileToCrop } = this.state;

    const hasError = this.props.hasError || !!errorText;

    return (
      <FormGroup
        data-testid={`${name}_field`}
        className={cn("FileField", { FileField_disabled: isDisabled }, className)}
      >
        {label ? (
          <>
            <Label className="FileField-Label">{label}</Label>
            {renderLabelIcon && renderLabelIcon()}
            {tooltip && (
              <Tooltip
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
                {...tooltip}
              >
                {tooltip.text || tooltip.render()}
              </Tooltip>
            )}
          </>
        ) : null}
        <div className="d-flex flex-row">
          <Input
            innerRef={this.inputRef}
            key={!value && getRandom()}
            accept={allowedTypes.toString()}
            type="file"
            name={name}
            disabled={isDisabled}
            placeholder={placeholder}
            className="FileField-Input"
            onClick={this.onInputClick}
            onChange={this.onChange}
          />
          <div
            data-testid={`${name}_field-selected-file`}
            className={cn(
              "form-control FileField-SelectedFile flex-1",
              { "FileField-Placeholder": !value },
              { "is-invalid": hasError },
            )}
          >
            {value || placeholder}
          </div>
          {value ? (
            <Button
              disabled={isDisabled}
              onClick={this.onRemove}
              color={isDisabled ? "secondary" : "success"}
              className="FileField-RemoveBtn"
            >
              Remove
            </Button>
          ) : (
            <Button
              disabled={isDisabled}
              onClick={this.onBrowse}
              className="FileField-BrowseBtn"
              color={isDisabled ? "secondary" : "success"}
            >
              Browse
            </Button>
          )}
        </div>
        {!hasError && hasHint && <div className={`FileField-Hint`}>{hintText}</div>}
        {hasError && <div className={`FileField-ErrorHint`}>{errorText}</div>}

        <Modal
          title={"Image crop"}
          isOpen={cropModelShow}
          hasFooter={false}
          hasCloseBtn={false}
          className={"FileFieldModal"}
        >
          {fileToCrop && (
            <ImageCrop
              file={fileToCrop}
              changeCropModelShow={this.changeCropModelShow}
              onCropComplete={this.onCropComplete}
              aspect={aspect}
            />
          )}
        </Modal>
      </FormGroup>
    );
  }
}
