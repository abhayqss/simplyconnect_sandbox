import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    map, without
} from 'underscore'

import {
    Row,
    Col,
    Button
} from 'reactstrap'

import {
    useProspectDocumentFilter,
    useProspectDocumentFilterDirectory,
    useProspectDocumentFilterDefaultDataCache
} from 'hooks/business/Prospects/Documents'

import {
    SearchField
} from 'components'

import {
    DateField,
    SelectField,
    CheckboxField
} from 'components/Form'

import { DateUtils as DU } from 'lib/utils/Utils'

import { DocumentFilterValidator as Validator } from 'validators'

import './DocumentFilter.scss'

function mapToValueText(data) {
    return map(data, ({ id, name, title }) => ({
        text: title ?? name, value: id ?? name
    }))
}

function purifyData(data) {
    return {
        ...data,
        categoryIds: without(
            data.categoryIds, 'NO'
        ),
        signatureStatusNames: without(
            data.signatureStatusNames, 'NO'
        )
    }
}

function DocumentFilter(
    {
        prospectId,
        onChange,
        onApply,
        onReset
    }
) {
    const [shouldValidate, setShouldValidate] = useState(false)

    const {
        get: getDefaultData,
        update: updateDefaultData
    } = useProspectDocumentFilterDefaultDataCache({ prospectId })

    const {
        data,
        blur,
        focus,
        reset,
        apply,
        errors,
        isSaved,
        validate,
        changeField,
        changeFields,
        changeDateField,
        changeSelectField
    } = useProspectDocumentFilter({
        getDefaultData,
        onChange: data => {
            onChange(purifyData(data))
        },
        Validator,
        onApply,
        onReset
    })

    const {
        categories,
        signatureStatuses
    } = useProspectDocumentFilterDirectory(
        { prospectId },
        {
            actions: {
                isFilterSaved: isSaved,
                changeFilterField: changeField,
                changeFilterFields: changeFields,
                updateFilterDefaultData: updateDefaultData
            }
        }
    )

    const mappedCategories = useMemo(
        () => [
            { text: 'Not categorized', value: 'NO' },
            ...mapToValueText(categories)
        ], [categories]
    )

    // const mappedSignatureStatuses = useMemo(
    //     () => [
    //         ...mapToValueText(signatureStatuses),
    //         { text: 'No signature', value: 'NO' }
    //     ],
    //     [signatureStatuses]
    // )

    const onChangeCategoryField = useCallback((name, value) => {
        changeFields({
            [name]: value,
            includeNotCategorized: value.includes('NO')
        })
    }, [changeFields])

    const onChangeSignatureStatusField = useCallback((name, value) => {
        changeFields({
            [name]: value,
            includeWithoutSignature: value.includes('NO')
        })
    }, [changeFields])

    const onChangeDateField = useCallback((name, value) => {
        const f = name === 'fromDate' ? 'startOf' : 'endOf'
        changeDateField(name, value && DU[f](value, 'day'))
    }, [changeDateField])

    function clearField(name) {
        changeField(name, '')
    }

    function validateIf() {
        if (shouldValidate) {
            validate()
                .then(() => setShouldValidate(false))
                .catch(() => setShouldValidate(true))
        }
    }

    function applyIfValid() {
        validate()
            .then(apply)
            .catch(() => setShouldValidate(true))
    }

    useEffect(validateIf, [validate, shouldValidate])

    return (
        <div className="ClientDocumentFilter">
            <Row>
                <Col lg={3} md={4}>
                    <SearchField
                        name="title"
                        value={data.title}
                        label="File Name"
                        onBlur={blur}
                        placeholder="Search by title"
                        onFocus={focus}
                        maxLength={256}
                        onChange={changeField}
                        onClear={clearField}
                        className="ClientDocumentFilter-TextField"
                    />
                </Col>
                <Col lg={3} md={4}>
                    <SearchField
                        name="description"
                        value={data.description}
                        label="Description"
                        onBlur={blur}
                        onFocus={focus}
                        maxLength={3950}
                        onChange={changeField}
                        onClear={clearField}
                        placeholder="Search by description"
                        className="ClientDocumentFilter-TextField"
                    />
                </Col>
                <Col lg={6} md={8}>
                    <SelectField
                        isMultiple
                        type="text"
                        name="categoryIds"
                        value={data.categoryIds}
                        options={mappedCategories}
                        label="Category"
                        placeholder="Select"
                        onChange={onChangeCategoryField}
                        className="ClientDocumentFilter-SelectField"
                    />
                </Col>
                {/* <Col lg={3} md={4}>
                    <SelectField
                        isMultiple
                        type="text"
                        name="signatureStatusNames"
                        value={data.signatureStatusNames}
                        options={mappedSignatureStatuses}
                        label="Signature Status"
                        placeholder="Select"
                        onChange={onChangeSignatureStatusField}
                        className="ClientDocumentFilter-SelectField"
                    />
                </Col> */}
            </Row>
            <Row>
                <Col lg={3} md={4}>
                    <DateField
                        name="fromDate"
                        value={data.fromDate}
                        dateFormat="MM/dd/yyyy"
                        label="Date from*"
                        placeholder="Select date"
                        maxDate={data.toDate}
                        onChange={onChangeDateField}
                        errorText={errors.fromDate}
                        className="ClientDocumentFilter-DateField"
                    />
                </Col>
                <Col lg={6} md={8}>
                    <Row>
                        <Col md={6}>
                            <DateField
                                name="toDate"
                                value={data.toDate}
                                dateFormat="MM/dd/yyyy"
                                label="Date to*"
                                placeholder="Select date"
                                minDate={data.fromDate}
                                errorText={errors.toDate}
                                className="ClientDocumentFilter-DateField"
                                onChange={onChangeDateField}
                            />
                        </Col>
                        <Col md={6}>
                            <CheckboxField
                                name="includeDeleted"
                                className="ClientDocumentFilter-CheckboxField"
                                value={data.includeDeleted}
                                label="Show deleted"
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                </Col>
                <Col lg={3} md={12}>
                    <div className="ClientDocumentFilter-Buttons">
                        <Button
                            outline
                            color='success'
                            className="ClientDocumentFilter-Btn"
                            onClick={reset}
                        >
                            Clear
                        </Button>
                        <Button
                            color='success'
                            onClick={applyIfValid}
                            className="ClientDocumentFilter-Btn"
                        >
                            Apply
                        </Button>
                    </div>
                </Col>
            </Row>
        </div>
    )
}

export default memo(DocumentFilter)