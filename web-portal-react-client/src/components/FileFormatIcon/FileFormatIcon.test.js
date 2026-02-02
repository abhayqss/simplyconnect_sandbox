import React from 'react'

import { render } from 'lib/test-utils'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import FileFormatIcon from './FileFormatIcon'

describe('<FileFormatIcon>:', () => {
    it('is visible on UI', async () => {
        const { findByTestId } = render(
            <FileFormatIcon name="pngTestFileFormatIcon" format="PNG"/>
        )

        const node = await findByTestId('pngTestFileFormatIcon')

        expect(node).toBeInTheDocument()
        expect(node).toBeVisible()
    })

    for (let format in ALLOWED_FILE_FORMATS) {
        it(`${format} format icon is displayed correctly`, async () => {
            const { findByTestId } = render(
                <FileFormatIcon name={`${format}TestFileFormatIcon`} format={format}/>
            )

            const node = await findByTestId(`${format}TestFileFormatIcon`)

            expect(node).toBeVisible()
        })
    }
})