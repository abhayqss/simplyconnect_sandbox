import React, { memo } from 'react'

import cn from 'classnames'

import { Link } from 'react-router-dom'

import { Button } from 'components/buttons'

import './SectionSummary.scss'

function SectionSummary(
    {
        title,
        imageSrc,
        children,
        className,
        actions,
        moreInfoPath,
        onClose,
        onDemo
    }
) {
    return (
        <div className={cn('SectionSummary', className)}>
            <div className="SectionSummary-Title">
                {title}
            </div>
            <div className="h-flexbox justify-content-between">
                <div className="padding-top-12 padding-right-12">
                    <img src={imageSrc} className="SectionSummary-Image"/>
                </div>
                <div className="padding-top-12 padding-left-12">
                    <div className="SectionSummary-Description margin-bottom-20">
                        {children}
                    </div>
                    <div className="SectionSummary-Actions">
                        <div>
                            {actions ? actions() : (
                                <>
                                    {
                                        /*<Link
                                            to={moreInfoPath}
                                            className="btn btn-outline-success btn-size-regular SectionSummary-Action"
                                        >
                                            Learn More
                                        </Link>*/
                                    }
                                    <Button
                                        color="success"
                                        onClick={onDemo}
                                        className="btn-size-regular SectionSummary-Action"
                                    >
                                        Get a Demo
                                    </Button>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default memo(SectionSummary)