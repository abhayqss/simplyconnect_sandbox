import { useCallback } from 'react'

function useCustomFormFieldChange(changeField) {
    const changeDateField = useCallback((name, date) => {
        changeField(name, date ? date.getTime() : null)
    }, [changeField])

    const changeSelectField = useCallback((name, value) => {
        changeField(name, value)
    }, [changeField])


    return {
        changeDateField,
        changeSelectField,
    }
}

export default useCustomFormFieldChange