import React, { memo } from "react";

import PropTypes from "prop-types";

import cn from "classnames";

import { IconButton } from "components";

import { ReactComponent as DeleteIcon } from "images/templateBuilder/delete.svg";

import "./ESignDocumentTemplateFieldRule.scss";

function ESignDocumentTemplateFieldRule({ independentField, dependentField, className, onDelete }) {
  return (
    <div className={cn("ESignDocumentTemplateFieldRule", className)}>
      <div className="ESignDocumentTemplateFieldRule-Text">
        <span className="ESignDocumentTemplateFieldRule-Text_Bold">{dependentField}</span> visible if{" "}
        <span className="ESignDocumentTemplateFieldRule-Text_Bold">{independentField}</span> selected
      </div>
      <IconButton
        size={16}
        shouldHighLight={false}
        className="ESignDocumentTemplateFieldRule-DeleteAction"
        Icon={DeleteIcon}
        onClick={onDelete}
      />
    </div>
  );
}

ESignDocumentTemplateFieldRule.propTypes = {
  dependentField: PropTypes.string,
  independentField: PropTypes.string,
  className: PropTypes.string,
  onDelete: PropTypes.func
};

export default memo(ESignDocumentTemplateFieldRule);
