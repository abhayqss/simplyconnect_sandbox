import React from 'react'

import cn from 'classnames'
import { Col } from 'reactstrap'

export default function Column({ xxl, xxxl, className, ...other }){
    return (
        <Col
            {...other}
            className={cn(
                className,
                xxl && `col-xxl-${xxl}`,
                xxxl && `col-xxxl-${xxxl}`
            )}
        />
    )
}