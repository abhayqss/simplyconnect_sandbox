import React from 'react'

import Loader from 'components/Loader/Loader'

import { isEmpty } from 'underscore'

function DataLoadable(
	{
		children,
		data = {},
		loaderStyle,
		loaderClassName,
		isLoading = false,
		noDataText = 'No Data',
		isNoData = isEmpty(data)
	}
) {
	let content

	if (isLoading) {
		content = <Loader style={loaderStyle} className={loaderClassName}/>
	} else if (isNoData) {
		content = <div className="DataLoadable-Fallback text-center">{noDataText}</div>
	} else {
		content = children(data)
	}

	return content
}

export default DataLoadable