import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import PropTypes from 'prop-types'

import {
    Row,
    Col,
    Form,
    Button
} from 'reactstrap'

import {
    map,
    chain,
    findWhere
} from 'underscore'

import {
    Loader,
    ErrorViewer
} from 'components'

import { SelectField } from 'components/Form'

import {
    useForm,
    useCustomFormFieldChange
} from 'hooks/common'

import {
    useDocumentFoldersQuery
} from 'hooks/business/documents'

import Entity from "entities/ESignDocumentTemplateFolderAssigner"
import Validator from "validators/ESignDocumentTemplateFolderAssignerValidator"

import { FOLDER_TYPES } from 'lib/Constants'

import {
    isNotEmpty
} from 'lib/utils/Utils'

import './ESignDocumentTemplateFolderAssignerForm.scss'

const {
    TEMPLATE
} = FOLDER_TYPES

function valueTextMapper(o) {
    const text = o.title || o.text || o.name

    return { value: o.id, text }
}
function ESignDocumentTemplateFolderAssignerForm(
    {
        assignedFolders,
        communityOptions,

        onCancel,
        onChanged,
        onSubmitSuccess
    }
) {
    const [error, setError] = useState(false)
    const [isFetching, setFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        errors,
        isValid,
        validate,
        isChanged,
        changeField,
        fields: formFields
    } = useForm('ESignDocumentTemplateFolderAssignerForm', Entity, Validator)

    const {
        changeSelectField
    } = useCustomFormFieldChange(changeField)

    const communityIds = useMemo(() => map(communityOptions, o => o.value), [communityOptions])

    const {
        data: folders = [],
        isFetching: isFetchingTemplateFolders
    } = useDocumentFoldersQuery({
        communityIds,
        types: [TEMPLATE],
    }, {
        staleTime: 0,
        enabled: isNotEmpty(communityIds)
    })

    const groupedFolderOptions = useMemo(() =>
        chain(folders)
            .groupBy('communityId')
            .mapObject(o => map(o, valueTextMapper))
            .value(),
        [folders])

    const assignedFoldersValue = useMemo(() => {
        const value = formFields.value

        return Array.isArray(value) ? value : value.toJS()
    }, [formFields])

    function init() {
        const mappedData = map(communityOptions, ({ value: communityId }) => {
            const assignedFolder = findWhere(assignedFolders, { communityId })
            const folderId = assignedFolder?.folderId

            return {
                folderId,
                communityId,
            }
        })

        changeField("value", mappedData, true)
    }

    function validateIf() {
        if (needValidation) {
            validate()
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function tryToSubmit(e) {
        e.preventDefault()

        setFetching(true)

        validate()
            .then(async () => {
                setNeedValidation(false)

                try {
                    onSubmitSuccess(assignedFoldersValue)
                } catch (e) {
                    setError(e)
                }
            })
            .catch((e) => {
                setNeedValidation(true)
            })
            .finally(() => {
                setFetching(false)
            })
    }

    const _onCancel = useCallback(
        () => onCancel(isChanged),
        [onCancel, isChanged]
    )

    const onSubmit = useCallback(
        tryToSubmit,
        [
            validate,
            assignedFoldersValue
        ]
    )

    useEffect(init, [assignedFolders, communityOptions, changeField])

    useEffect(validateIf, [
        validate,
        needValidation
    ])


    useEffect(() => {
        onChanged(isChanged)
    }, [isChanged, onChanged])

    return (
        <>
            <Form className="ESignDocumentTemplateFolderAssignerForm" onSubmit={onSubmit}>
                {(isFetching || isFetchingTemplateFolders) && (
                    <Loader hasBackdrop />
                )}
                <div className="ESignDocumentTemplateFolderAssignerForm-Body">
                    {map(communityOptions, ({ value: communityId }, idx) => {
                        const folderOptions = groupedFolderOptions[communityId]

                        return folderOptions ? (
                            <Row key={communityId}>
                                <Col md={6}>
                                    <SelectField
                                        isDisabled
                                        label="Community*"
                                        options={communityOptions}
                                        name={`value.${idx}.communityId`}
                                        hasError={errors?.value?.[idx]?.communityId}
                                        errorText={errors?.value?.[idx]?.communityId}
                                        value={assignedFoldersValue?.[idx]?.communityId}
                                        className="ESignDocumentTemplateFolderAssignerForm-SelectField"

                                        onChange={changeSelectField}
                                    />
                                </Col>
                                <Col md={6}>
                                    <SelectField
                                        label="Folder*"
                                        name={`value.${idx}.folderId`}
                                        value={assignedFoldersValue?.[idx]?.folderId}
                                        options={folderOptions}
                                        isDisabled={folderOptions.length === 1}
                                        hasError={errors?.value?.[idx]?.folderId}
                                        errorText={errors?.value?.[idx]?.folderId}
                                        className="ESignDocumentTemplateFolderAssignerForm-SelectField"

                                        onChange={changeSelectField}
                                    />
                                </Col>
                            </Row>
                        ) : null
                    })}
                </div>
                <div className="ESignDocumentTemplateFolderAssignerForm-Buttons">
                    <Button
                        outline
                        color="success"
                        onClick={_onCancel}
                    >
                        Cancel
                    </Button>

                    <Button
                        color="success"
                        disabled={!isValid}
                    >
                        Save
                    </Button>
                </div>
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

ESignDocumentTemplateFolderAssignerForm.propTypes = {
    communityOptions: PropTypes.arrayOf(
        PropTypes.shape({ text: PropTypes.string, value: PropTypes.number })),
    assignedFolders: PropTypes.arrayOf(
        PropTypes.shape({ communityId: PropTypes.number, folderId: PropTypes.number })),

    onCancel: PropTypes.func,
    onChanged: PropTypes.func,
    onSubmitSuccess: PropTypes.func
}


export default memo(ESignDocumentTemplateFolderAssignerForm)