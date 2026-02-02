import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    useDocumentFilterDirectory,
    useDocumentFilterDefaultDataCache
} from 'hooks/business/documents'

import { DocumentFilter as Filter } from 'components/business/Documents'

function DocumentFilter(
    {
        data,
        blur,
        focus,
        reset,
        apply,
        errors,
        isSaved,

        communityId,
        organizationId,

        validate,
        changeField,
        changeFields,
        className
    }
) {
    const [shouldValidate, setShouldValidate] = useState(false)

    const {
        update: updateDefaultData
    } = useDocumentFilterDefaultDataCache({ organizationId, communityId })

    const {
        categories
    } = useDocumentFilterDirectory(
        { organizationId, communityId },
        {
            actions: {
                isFilterSaved: isSaved,
                changeFilterField: changeField,
                changeFilterFields: changeFields,
                updateFilterDefaultData: updateDefaultData
            }
        }
    )

    const onClearField = useCallback(name => {
        changeField(name, '')
    }, [changeField])

    const applyIfValid = useCallback(() => {
        validate()
            .then(apply)
            .catch(() => setShouldValidate(true))
    }, [apply, validate])

    function validateIf() {
        if (shouldValidate) {
            validate()
                .then(() => setShouldValidate(false))
                .catch(() => setShouldValidate(true))
        }
    }

    useEffect(validateIf, [validate, shouldValidate])

    return (
        <Filter
            data={data}
            categories={categories}
            className={className}

            onClearField={onClearField}
            onChangeField={changeField}
            onChangeFields={changeFields}

            onBlur={blur}
            onFocus={focus}
            onApply={applyIfValid}
            onReset={reset}
            errors={errors}
        />
    )
}

export default memo(DocumentFilter)