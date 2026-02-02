import { saveAs } from 'file-saver'
import { isFunction } from 'underscore'

export default function Actions({ actionTypes = {}, doLoad, doDownload }) {
    const {
        REFRESH,
        LOAD_REQUEST,
        LOAD_SUCCESS,
        LOAD_FAILURE,
        DOWNLOAD_FAILURE
    } = actionTypes

    return {
        clear: () => ({ type: actionTypes.CLEAR }),
        clearError: () => ({ type: actionTypes.CLEAR_ERROR }),
        ...isFunction(doLoad) && {
            load: (...args) => {
                return dispatch => {
                    dispatch({ type: LOAD_REQUEST })
                    return doLoad(...args).then(response => {
                        dispatch({ type: LOAD_SUCCESS, payload: { data: response.data ?? response } })
                        return response
                    }).catch(error => {
                        dispatch({ type: LOAD_FAILURE, payload: error })
                        return { error }
                    })
                }
            },
            refresh: () => ({ type: REFRESH }),
        },
        ...isFunction(doDownload) && {
            download: (...args) => {
                return dispatch => (
                    doDownload(...args).then(({ name, data }) => {
                        saveAs(data, name)
                        return { success: true, name }
                    }).catch(error => {
                        dispatch({ type: DOWNLOAD_FAILURE, payload: error })
                        return { error }
                    })
                )
            }
        }
    }
}