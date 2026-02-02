import React from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { isString } from 'underscore'

import { Row, Col } from 'reactstrap'

import {
    isEmpty
} from 'lib/utils/Utils'

import './Detail.scss'

/**
 * @return {null}
 */
function Detail(
    {
        title,
        layout,
        children,
        className,
        titleClassName,
        valueClassName,
        renderIcon
    }
) {
    const value = isString(children) ? children.trim() : children

    if (isEmpty(value)) return null

    return layout === 'h' ? (
        <Row className={cn('Detail', 'Detail_layout_h', className)} data-testid="Detail">
            <Col sm={6} md={5} lg={3}>
                <div className={cn('Detail-Title', titleClassName)}>{title}</div>
            </Col>
            <Col sm={6} md={7} lg={9}>
                <div className={cn('Detail-Value', valueClassName)}>
                    {value}
                    {renderIcon && renderIcon()}
                </div>
            </Col>
        </Row>
    ) : (
        <div className={cn('Detail', 'Detail_layout_v', className)} data-testid="Detail">
            <div className={cn('Detail-Title', titleClassName)}>{title}</div>
            <div className={cn('Detail-Value', valueClassName)}>
                {value}
                {renderIcon && renderIcon()}
            </div>
        </div>
    )
}

export default Detail

Detail.propTypes = {
    title: PTypes.string,
    layout: PTypes.oneOf(['h', 'v']), // horizontal, vertical
    className: PTypes.string,
    titleClassName: PTypes.string,
    valueClassName: PTypes.string,
    renderIcon: PTypes.func
}

Detail.defaultProps = {
    layout: 'h'
}