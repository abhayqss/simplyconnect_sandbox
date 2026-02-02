import React, { memo } from "react";
import { UncontrolledTooltip as Tooltip } from "reactstrap";

import cn from "classnames";

import PropTypes from "prop-types";

import { noop } from 'underscore'

import { ReactComponent as Info } from "images/info.svg";

import "./ESignDocumentTemplateFieldEditorSection.scss";

function ESignDocumentTemplateFieldEditorSection({
  name,
  hint,
  title,
  children,
  className,
  hasBorder,
  onClick = noop,
  indicatorColor,
  hasIcon = true,
  isIconBtnDisabled,
  renderTitle = noop,
  icon: IconComponent = Info,
}) {
  const tooltipId = `${name}-tooltip`;

  function _onClick() {
    if (!isIconBtnDisabled) {
      onClick()
    }
  }

  return (
    <div className={cn("ESignDocumentTemplateFieldEditorSection", className)}>
      <h3 className="ESignDocumentTemplateFieldEditorSection-Title">
        {title}
        {renderTitle && renderTitle()}
        {hasIcon && (
          <IconComponent
            id={tooltipId}
            onClick={_onClick}
            className={cn("ESignDocumentTemplateFieldEditorSection-Icon", { "ESignDocumentTemplateFieldEditorSection-Icon_Disabled": isIconBtnDisabled })}
          />
        )}
        {hint && (
          <Tooltip
            placement="right"
            trigger={`focus ${isIconBtnDisabled ? "hover" : ""}`}
            target={tooltipId}
            modifiers={[
              {
                name: 'offset',
                options: { offset: [0, 6] }
              },
              {
                name: 'preventOverflow',
                options: { boundary: document.body }
              }
            ]}
          >
            {hint}
          </Tooltip>
        )}
      </h3>
      <div className="position-relative w-100">
        {hasBorder && (
          <div
            className="ESignDocumentTemplateFieldEditorSection-Indicator"
            style={{ backgroundColor: indicatorColor }}
          ></div>
        )}
        {children}
      </div>
    </div>
  );
}

ESignDocumentTemplateFieldEditorSection.propTypes = {
  name: PropTypes.string,
  icon: PropTypes.object,
  hint: PropTypes.string,
  onClick: PropTypes.func,
  title: PropTypes.string,
  hasBorder: PropTypes.bool,
  renderTitle: PropTypes.func,
  className: PropTypes.string,
  indicatorColor: PropTypes.string,
  isIconBtnDisabled: PropTypes.bool,
};

export default memo(ESignDocumentTemplateFieldEditorSection);
