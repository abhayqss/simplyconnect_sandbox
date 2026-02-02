import React from 'react'

import './ScoringInfo.scss'

export default function ScoringInfo() {
    return (
        <div className="ScoringInfo">
            <div className="ScoringInfo-Header">
                <div className='flex-1 text-center'>
                    <span className="ScoringInfo-HeaderText">
                        Score
                    </span>
                </div>
                <div className='flex-4'>
                    <span className="ScoringInfo-HeaderText">
                        Guidelines / Parameters
                    </span>
                </div>
            </div>
            <div className="ScoringInfo-Sections">
                <div className="ScoringInfo-Section">
                    <div className='flex-1 text-center'>
                        <div className="ScoringInfo-SectionNumber">
                            5
                        </div>
                    </div>
                    <div className='flex-4'>
                    <span className="ScoringInfo-SectionText">
                        Client has no capability or resource in the area;
                        requires immediate intervention or correction;
                        client is at serious risk if not addressed
                    </span>
                    </div>
                </div>
                <div className="ScoringInfo-Section">
                    <div className='flex-1 text-center'>
                        <div className="ScoringInfo-SectionNumber">
                            4
                        </div>
                    </div>
                    <div className='flex-4'>
                    <span className="ScoringInfo-SectionText">
                        Client has limited capabilities in this area or current
                        or historical resources are unreliable or inconsistent
                    </span>
                    </div>
                </div>
                <div className="ScoringInfo-Section">
                    <div className='flex-1 text-center'>
                        <div className="ScoringInfo-SectionNumber">
                            3
                        </div>
                    </div>
                    <div className='flex-4'>
                    <span className="ScoringInfo-SectionText">
                        Client some resources or capability in this area;
                        has developed workarounds or relies on caregivers/others
                        for some assistance on a regular or consistent basis;
                        could benefit from self-management development
                    </span>
                    </div>
                </div>
                <div className="ScoringInfo-Section">
                    <div className='flex-1 text-center'>
                        <div className="ScoringInfo-SectionNumber">
                            2
                        </div>
                    </div>
                    <div className='flex-4'>
                    <span className="ScoringInfo-SectionText">
                        Client is generally capable of self-management
                        or accomplishing issues but could benefit from
                        tweaking or additional services/education;
                        caregiver or external assistance is minimal
                        or minor
                    </span>
                    </div>
                </div>
                <div className="ScoringInfo-Section">
                    <div className='flex-1 text-center'>
                        <div className="ScoringInfo-SectionNumber">
                            1
                        </div>
                    </div>
                    <div className='flex-4'>
                    <span className="ScoringInfo-SectionText">
                        Does not apply or Client is fully capable of
                        self-management or can accomplish all issues
                        on their own
                    </span>
                    </div>
                </div>
            </div>
        </div>
    )
}