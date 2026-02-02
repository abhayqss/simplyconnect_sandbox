import React from 'react'

import {
    QueryClient,
    QueryClientProvider
} from '@tanstack/react-query'

import { Provider } from 'react-redux'
import { createBrowserHistory } from 'history'
import { CookiesProvider } from 'react-cookie'
import { ConnectedRouter } from 'connected-react-router'

import userEvent from '@testing-library/user-event'
import { render as baseRender } from '@testing-library/react'

import configureStore from 'redux/configureStore'

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 1000 * 60 * 5,
            refetchOnWindowFocus: false
        }
    }
})

const render = (ui, options = {}) => {
    const history = createBrowserHistory()

    const store = configureStore(history)

    const Wrapper = ({ children } = {}) => (
        <Provider store={store}>
            <ConnectedRouter history={history}>
                <CookiesProvider>
                    <QueryClientProvider client={queryClient}>
                        {children}
                    </QueryClientProvider>
                </CookiesProvider>
            </ConnectedRouter>
        </Provider>
    )

    return {
        store,
        userEvent: userEvent.setup({ delay: null }),
        ...baseRender(ui, { wrapper: Wrapper, ...options })
    }
}

export default render
