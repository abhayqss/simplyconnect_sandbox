import React, {
	memo,
	useMemo
} from 'react'

import { filesize } from 'filesize'
import { compact } from 'underscore'

import {
	Table,
	FileFormatIcon,
	CollapsibleText
} from 'components'

import {
	IconButton
} from 'components/buttons'

import {
	DocumentCategories,
	DocumentSignatureStatusIcon
} from 'components/business/Documents'

import {
	uc
} from 'lib/utils/Utils'

import {
	format, formats
} from 'lib/utils/DateUtils'

import { ReactComponent as Stylus } from 'images/stylus.svg'

import './ESignRequestedDocumentList.scss'

const DATE_FORMAT = formats.americanMediumDate

function ESignRequestedDocumentList(
	{
		data,
		isFetching,
		onSign
	}
) {
	const columns = useMemo(() => compact(
		[
			{
				dataField: 'title',
				text: 'Title',
				formatter: (v, row) => {
					return (
						<div className="d-flex flex-row align-items-center overflow-hidden">
							<FileFormatIcon
								mimeType={row.mimeType}
								className="ESignRequestedDocumentList-DocFormatIcon"
							/>
							<div className="ESignRequestedDocumentList-DocTitle">
								{v}
							</div>
						</div>
					)
				}
			},
			{
				dataField: 'categories',
				text: 'Category',
				classes: 'hide-on-mobile ',
				headerClasses: 'hide-on-mobile ESignRequestedDocumentList-CategoryCol',
				formatter: categories => {
					return (
						<DocumentCategories categories={categories}/>
					)
				}
			},
			{
				dataField: 'type',
				text: 'Type',
				headerStyle: { width: '7%' },
				classes: 'hide-on-tablet DocumentList-TypeCol',
				headerClasses: 'hide-on-tablet DocumentList-TypeCol'
			},
			{
				dataField: 'size',
				text: 'Size',
				align: 'right',
				headerAlign: 'right',
				formatter: v => v ? uc(filesize(
					v, { round: 2 }
				)) : '',
				headerClasses: 'ESignRequestedDocumentList-SizeCol'
			},
			{
				dataField: 'createdDate',
				text: 'Created',
				align: 'right',
				headerAlign: 'right',
				classes: 'hide-on-tablet ESignRequestedDocumentList-CreatedCol',
				headerClasses: 'hide-on-tablet ESignRequestedDocumentList-CreatedCol',
				formatter: v => format(v, DATE_FORMAT)
			},
			{
				dataField: 'author',
				text: 'Author',
				headerStyle: { width: '15%' },
				classes: 'hide-on-mobile ESignRequestedDocumentList-AuthorCol',
				headerClasses: 'hide-on-mobile ESignRequestedDocumentList-AuthorCol'
			},
			{
				dataField: 'description',
				text: 'Description',
				classes: 'hide-on-mobile ESignRequestedDocumentList-DescriptionCol',
				headerClasses: 'hide-on-mobile ESignRequestedDocumentList-DescriptionCol',
				formatter: description => {
					return (
						<CollapsibleText>
							{description}
						</CollapsibleText>
					)
				}
			},
			{
				dataField: 'signature',
				text: 'Signature',
				classes: 'hide-on-mobile',
				align: 'center',
				headerStyle: { width: '100px' },
				headerClasses: 'hide-on-mobile',
				formatter: v => {
					return v ? (
						<DocumentSignatureStatusIcon
							statusName={v.statusName}
							statusTitle={v.statusTitle}
						/>
					) : ''
				}
			},
			{
				dataField: '@actions',
				text: '',
				headerStyle: { width: '210px' },
				classes: 'ESignRequestedDocumentList-ActionsCell',
				formatter: (v, row) => {
					return (
						<div className="ESignRequestedDocumentList-Actions">
							<IconButton
								size={36}
								Icon={Stylus}
								disabled={!row.signature.canSign}
								tooltip={row.signature.canSign && "Sign Document"}
								className="ESignRequestedDocumentList-Action"
								onClick={() => row.signature.canSign && onSign(row)}
							/>
						</div>
					)
				}
			}
		]
	), [onSign])

	return (
		<Table
			hasHover
			hasOptions
			keyField="id"
			title="Documents"
			noDataText="No documents."
			isLoading={isFetching}
			className="ESignRequestedDocumentList"
			containerClass="ESignRequestedDocumentListContainer"
			data={data}
			columns={columns}
			hasCaption={false}
			columnsMobile={['title', 'author', 'signature']}
		/>
	)
}

export default memo(ESignRequestedDocumentList)