import React from 'react'

import Highlighter from 'react-highlight-words'

import './ServicePrimaryInfo.scss'

function ServicePrimaryInfo({ title, categoryTitle, highlightedText }) {
	return (
		<div className="ServicePrimaryInfo">
			<div className="ServicePrimaryInfo-Title">
				<Highlighter
					textToHighlight={title}
					searchWords={[highlightedText]}
					highlightClassName="ServicePrimaryInfo-HighlightedText"
				/>
			</div>
			<div className="ServicePrimaryInfo-CategoryTitle">
				in {categoryTitle}
			</div>
		</div>
	)
}

export default ServicePrimaryInfo