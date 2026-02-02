import React from 'react'

import { render } from 'lib/test-utils'
import { DateUtils as DU } from 'lib/utils/Utils'

import DocumentDetail from './DocumentDetail'

const { format, formats } = DU

const DATE_FORMAT = formats.americanMediumDate

describe('<DocumentDetail>:', () => {
    it('is visible on UI', async () => {
        const { findByTestId } = render(
            <DocumentDetail id="testData"/>
        )

        const node = await findByTestId('testData')

        expect(node).toBeInTheDocument()
        expect(node).toBeVisible()
    })

    it('Summary is displayed correctly', async () => {
        const now = Date.now()

        const { findByText } = render(
            <DocumentDetail
                id="testData"
                title="Test Data Document"
                date={now}
            />
        )

        expect(await findByText('Test Data Document')).toBeVisible()
        expect(await findByText(format(now, DATE_FORMAT))).toBeVisible()
    })

    it('View button is displayed correctly', async () => {
        const now = Date.now()

        const { findByTestId } = render(
            <DocumentDetail
                id="testData"
                title="Test Data Document"
                date={now}
                canView={true}
            />
        )

        expect(await findByTestId('doc-testData_view')).toBeVisible()
    })

    it('Download button is displayed correctly', async () => {
        const now = Date.now()

        const { findByTestId } = render(
            <DocumentDetail
                id="testData"
                title="Test Data Document"
                date={now}
                canDownload={true}
            />
        )

        expect(await findByTestId('doc-testData_download')).toBeVisible()
    })
})