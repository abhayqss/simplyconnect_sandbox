import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    map,
    noop
} from 'underscore'

import {
    Loader,
    ErrorViewer
} from 'components'

import {
    DocumentForm as Form
} from 'components/business/Documents'

import {
    useForm,
    useScrollToFormError
} from 'hooks/common'

import {
    useDocumentCategoriesQuery
} from 'hooks/business/directory/query'

import { isNotEmpty } from 'lib/utils/ArrayUtils'

import './DocumentForm.scss'

function mapToIds(data) {
    return map(data, o => o.id)
}

function DocumentForm(
    {
        children,
        documentId,
        documentCategoryIds,
        organizationId,
        organizationName,
        submitButtonText,
        onSubmitSuccess,
        className,
        submit,
        entity,
        validator,
        initialData: document,
        onCancel: onCancelCb,
        hasSharingSection,
        renderParentFolderSection
    }
) {
    const [error, setError] = useState(false)
    const [scroll, setScroll] = useState(noop)
    const [isFetching, setFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const isEditing = Boolean(documentId)

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField,
        changeFields
    } = useForm('DOCUMENT', entity, validator)

    const data = useMemo(() => fields.toJS(), [fields])

    const {
        data: categories = []
    } = useDocumentCategoriesQuery(
        { organizationId },
        {
            enabled: Boolean(organizationId),
            staleTime: 0
        }
    )

    function init() {
        if (document) {
            const {
                title,
                categories,
                description
            } = document

            changeFields({
                title,
                description
            }, true)

            changeField('categoryIds', mapToIds(categories), true)
        }

        if (!documentId && isNotEmpty(documentCategoryIds)) {
            changeField('categoryIds', documentCategoryIds, true)
        }
    }

    function validateIf() {
        if (needValidation) {
            validate({ included: { isEditing } })
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function tryToSubmit() {
        setFetching(true)

        validate({ included: { isEditing } })
            .then(async () => {
                setNeedValidation(false)

                try {
                    await submit({
                        ...data,
                        id: documentId
                    })
                } catch (e) {
                    setError(e)
                }
            })
            .catch(() => {
                scrollToError()
                setNeedValidation(true)
            })
            .finally(() => {
                setFetching(false)
            })
    }

    const scrollToError = useScrollToFormError(
        '.DocumentForm', scroll
    )

    const onLayout = useCallback(o => {
        setScroll(() => o.scroll)
    }, [])

    const onCancel = useCallback(
        () => onCancelCb(isChanged),
        [onCancelCb, isChanged]
    )

    const onSubmit = useCallback(
        tryToSubmit,
        [
            data,
            submit,
            validate,
            documentId,
            scrollToError
        ]
    )

    useEffect(init, [
        document,
        documentId,
        changeFields,
        documentCategoryIds
    ])

    useEffect(validateIf, [
        validate,
        scrollToError,
        needValidation
    ])

    return (
        <>
            <div className="DocumentFormContainer">
                {isFetching && (<Loader hasBackdrop/>)}
                <Form
                    data={data}
                    errors={errors}
                    categories={categories}

                    isEditing={!!documentId}
                    hasSharingSection={hasSharingSection}
                    isSubmitDisabled={isFetching || !isChanged}

                    documentTitle={document?.title}
                    organizationName={organizationName}
                    submitButtonText={submitButtonText}
                    className={className}

                    onLayout={onLayout}
                    onCancel={onCancel}
                    onSubmit={onSubmit}
                    onChangeField={changeField}

                    renderParentFolderSection={renderParentFolderSection}
                >
                    {children}
                </Form>
            </div>
            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </>
    )
}

export default memo(DocumentForm)
