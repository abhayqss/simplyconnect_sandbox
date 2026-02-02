import { readFileSync } from 'fs'
import { pathToFileURL, fileURLToPath } from 'url'

import path from 'path'
import sass from 'sass'

import '@testing-library/jest-dom'

export function getStyles(...paths) {
    const result = sass.compile(path.resolve(...paths), {
        importers: [{
            canonicalize(url) {
                //@TODO remove hardcoded url replacement
                const _url = pathToFileURL(path.resolve('src', `${url}.scss`))

                return new URL(_url)
            },
            load(canonicalUrl) {
                return {
                    contents: readFileSync(fileURLToPath(canonicalUrl.href), 'utf8'),
                    syntax: 'scss',
                    sourceMapUrl: canonicalUrl,
                };
            }
        }]
    })

    return result.css
}

export function appendStyles(styles) {
    const style = document.createElement('style')

    style.innerHTML = styles
    document.body.appendChild(style)
}
