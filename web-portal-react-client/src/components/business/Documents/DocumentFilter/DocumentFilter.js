import React, {
    memo,
    useMemo,
    useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import { map } from 'underscore'

import {
    Row,
    Col,
    Button
} from 'reactstrap'

import {
    SearchField
} from 'components'

import {
    DateField,
    SelectField,
    CheckboxField
} from 'components/Form'

import { DateUtils as DU } from 'lib/utils/Utils'

import './DocumentFilter.scss'

function valueTextMapper({ id, name, label }) {
    return { value: id, text: label || name }
}

function DocumentFilter(
    {
        data,
        errors,
        
        categories,
    
        onClearField,
        onChangeField,
        onChangeFields,

        onBlur,
        onFocus,
        onReset,
        onApply,        

        className
    }
) {

    const mappedCategories = useMemo(
        () => [
            { text: 'Not categorized', value: 'NO' },
            ...map(categories, valueTextMapper)
        ], [categories]
    )

    const onChangeCategoryField = useCallback((name, value) => {
        onChangeFields({
            [name]: value,
            includeNotCategorized: value.includes('NO')
        })
    }, [onChangeFields])

    const onChangeDateField = useCallback((name, value) => {
        const f = name === 'fromDate' ? 'startOf' : 'endOf'
        onChangeField(name, value ? DU[f](value, 'day').getTime() : null)
    }, [onChangeField])

    return (
        <div className={cn('DocumentFilter', className)}>
            <Row>
                <Col lg={3} md={4}>
                    <SearchField
                        name="title"
                        value={data.title}
                        label="File Name"
                        onBlur={onBlur}
                        placeholder="Search by title"
                        onFocus={onFocus}
                        maxLength={256}
                        onChange={onChangeField}
                        onClear={onClearField}
                        className="DocumentFilter-TextField"
                    />
                </Col>
                <Col lg={6} md={4}>
                    <SearchField
                        name="description"
                        value={data.description}
                        label="Description"
                        onBlur={onBlur}
                        onFocus={onFocus}
                        maxLength={3950}
                        onChange={onChangeField}
                        onClear={onClearField}
                        placeholder="Search by description"
                        className="DocumentFilter-TextField"
                    />
                </Col>
                <Col lg={3} md={4}>
                    <SelectField
                        isMultiple
                        type="text"
                        name="categoryIds"
                        value={data.categoryIds}
                        options={mappedCategories}
                        label="Category"
                        placeholder="Select"
                        onChange={onChangeCategoryField}
                        className="DocumentFilter-SelectField"
                    />
                </Col>
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
                        className="DocumentFilter-DateField"
                    />
                </Col>
                <Col lg={3} md={4}>
                    <DateField
                        name="toDate"
                        value={data.toDate}
                        dateFormat="MM/dd/yyyy"
                        label="Date to*"
                        placeholder="Select date"
                        minDate={data.fromDate}
                        errorText={errors.toDate}
                        className="DocumentFilter-DateField"
                        onChange={onChangeDateField}
                    />
                </Col>
                <Col lg='auto' md={4}>
                    <CheckboxField
                        name="includeDeleted"
                        className="DocumentFilter-CheckboxField"
                        value={data.includeDeleted}
                        label="Show deleted"
                        onChange={onChangeField}
                    />
                </Col>
                <Col lg={3} md={12}>
                    <div className="DocumentFilter-Buttons">
                        <Button
                            outline
                            color='success'
                            className="DocumentFilter-Btn"
                            onClick={() => onReset()}
                        >
                            Clear
                        </Button>
                        <Button
                            color='success'
                            onClick={() => onApply()}
                            className="DocumentFilter-Btn"
                        >
                            Apply
                        </Button>
                    </div>
                </Col>
            </Row>
        </div>
    )
}

DocumentFilter.propTypes = {
    data: PTypes.object,
    errors: PTypes.object,

    categories: PTypes.arrayOf(PTypes.object),

    onClearField: PTypes.func,
    onChangeField: PTypes.func,
    onChangeFields: PTypes.func,

    onBlur: PTypes.func,
    onFocus: PTypes.func,
    onReset: PTypes.func,
    onApply: PTypes.func,

    className: PTypes.string
}

export default memo(DocumentFilter)