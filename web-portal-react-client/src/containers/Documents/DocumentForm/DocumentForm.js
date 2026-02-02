import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import { Row, Col } from 'reactstrap'

import {
    ErrorViewer
} from 'components'

import { SelectField } from 'components/Form'

import {
    useSelectOptions,
    useQueryInvalidation
} from 'hooks/common'

import {
    DocumentForm as Form
} from 'containers/common/forms'

import {
    useDocumentQuery,
    useDocumentSubmit,
    useDocumentFoldersQuery
} from 'hooks/business/documents'

import {
    FOLDER_TYPES
} from 'lib/Constants'

import { isInteger } from 'lib/utils/Utils'

import DocumentEntity from 'entities/Document'
import DocumentFormValidator from 'validators/DocumentFormValidator'

const {
    REGULAR
} = FOLDER_TYPES

function ParentFolderSection(
    {
        value,
        folderId,
        errorText,
        communityId,
        onChangeField,
    }
) {
    const {
        data: parentFolders
    } = useDocumentFoldersQuery({
        canUpload: true,
        types: [REGULAR],
        communityIds: [communityId]
    }, {
        staleTime: 0,
        enabled: isInteger(communityId)
    })

    const parentFolderOptions = useSelectOptions(
        parentFolders, { textProp: 'name' }
    )

    const onChangeSelectField = useCallback(
        (name, value) => onChangeField(name, value),
        [onChangeField]
    )

    useEffect(function setParentFolderId() {
        onChangeField('folderId', folderId)
    }, [folderId, onChangeField])

    return (
        <Row>
            <Col>
                <SelectField
                    hasValueTooltip
                    hasKeyboardSearch
                    hasKeyboardSearchText
                    name="folderId"
                    value={value}
                    options={parentFolderOptions}
                    label="Parent folder Name"
                    placeholder="Select Value"
                    className="DocumentForm-SelectField"
                    isDisabled={!parentFolderOptions.length}
                    errorText={errorText}
                    onChange={onChangeSelectField}
                />
            </Col>
        </Row>
    )
}

function DocumentForm(
    {
        children,
        folderId,
        documentId,
        communityId,
        organizationId,
        organizationName,
        folderCategoryIds,
        submitButtonText,
        onSubmitSuccess,
        onCancel
    }
) {
    const [error, setError] = useState(null)

    const {
        data: document,
        refetch: refetchDocument
    } = useDocumentQuery({ documentId }, {
        onError: setError,
        enabled: Boolean(documentId)
    })

    const invalidate = useQueryInvalidation()

    const { mutateAsync: submit } = useDocumentSubmit({ communityId }, {
        throwOnError: true,
        onSuccess: ({ data }) => {
            if (documentId) refetchDocument()
            invalidate('ParentDocumentFolders', {
                canUpload: true, communityId
            })
            onSubmitSuccess(data)
        }
    })

    return (
        <>
            <Form
                initialData={document}
                documentId={documentId}
                documentCategoryIds={folderCategoryIds}
                organizationId={organizationId}
                organizationName={organizationName}
                submitButtonText={submitButtonText}
                entity={DocumentEntity}
                validator={DocumentFormValidator}
                submit={submit}
                onCancel={onCancel}
                renderParentFolderSection={({ data, errors, onChangeField }) => {
                    return (
                        <ParentFolderSection
                            value={data.folderId}
                            communityId={communityId}
                            folderId={folderId}
                            errorText={errors.folderId}
                            onChangeField={onChangeField}
                        />
                    )
                }}
            >
                {children}
            </Form>

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
