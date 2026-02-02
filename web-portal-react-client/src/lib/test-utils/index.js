import { fireEvent } from '@testing-library/react'
import userEvent from '@testing-library/user-event'

export * from '@testing-library/jest-dom'
export * from '@testing-library/react'
export * from './styles'

export { userEvent }

export { default as render } from './render'

export function doManualInput(text, input) {
    text.split('').reduce((result, letter) => {
        let value = result + letter

        fireEvent.change(input, { target: { value } })

        return value
    }, '')
}
