import '@testing-library/jest-dom'

jest.useFakeTimers()
jest.spyOn(global, 'setTimeout')
jest.setTimeout(15000)