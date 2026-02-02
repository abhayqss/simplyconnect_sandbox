import React, { memo } from 'react'

import PropTypes from 'prop-types'

import cn from 'classnames'

import './ESignDocumentTemplateField.scss'

function ESignDocumentTemplateField(
    {
        title,
        Icon,
        style,
        iconBgColor,
        className
    }
) {
    return (
        <div style={style} className={cn('ESignDocumentTemplateField', className)}>
            {Icon && (
                <div
                    className="ESignDocumentTemplateField-IconWrapper"
                    style={{ backgroundColor: iconBgColor }}
                >
                    <Icon
                        className="ESignDocumentTemplateField-Icon"
                        style={{ fill: iconBgColor }}
                    />
                </div>
            )}
            <div className="ESignDocumentTemplateField-Title">{title}</div>
        </div>
    )
}

ESignDocumentTemplateField.propTypes = {
    title: PropTypes.string,
    Icon: PropTypes.object,
    style: PropTypes.object,
    className: PropTypes.string,
    iconBgColor: PropTypes.string
}

export default memo(ESignDocumentTemplateField)
