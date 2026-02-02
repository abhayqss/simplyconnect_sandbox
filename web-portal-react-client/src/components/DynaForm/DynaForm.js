import React, {
    useRef,
    useState,
    useEffect,
    useCallback
} from 'react'

import $ from 'jquery'
import 'jquery.scrollto'
import cn from 'classnames'
import Form from '@rjsf/core'
import PTypes from 'prop-types'
import { throttle } from 'underscore'
import validatorAjv6 from '@rjsf/validator-ajv6'
import { customizeValidator } from '@rjsf/validator-ajv8'

import { useScrollToFormError } from 'hooks/common'

import TextField from './TextField/TextField'
import DateField from './DateField/DateField'
import AlertPanel from './AlertPanel/AlertPanel'
import PhoneField from './PhoneField/PhoneField'
import SelectField from './SelectField/SelectField'
import CheckboxField from './CheckboxField/CheckboxField'
import RadioGroupField from './RadioGroupField/RadioGroupField'
import CheckboxGroupField from './CheckboxGroupField/CheckboxGroupField'
import CurrencyField from './CurrencyField/CurrencyField'

import { interpolate } from 'lib/utils/Utils'

import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import { ReactComponent as ArrowTop } from 'images/arrowtop.svg'

import ObjectFieldTemplate from './ObjectFieldTemplate/ObjectFieldTemplate'

import './DynaForm.scss'

const MIN_SCROLL_OFFSET_TOP = 150
const MIN_SCROLL_OFFSET_CHANGE_TIME = 200

const {
    EMPTY_FIELD,
    LENGTH_MINIMUM,
    LENGTH_MAXIMUM
} = VALIDATION_ERROR_TEXTS

const VALIDATION_ERROR_NAME_TEXTS = {
    "required": EMPTY_FIELD,
    "minLength": LENGTH_MINIMUM,
    "maxLength": LENGTH_MAXIMUM,
}

const fields = {
    DateField: DateField,
    StringField: TextField,
    PhoneField: PhoneField,
    AlertPanel: AlertPanel,
    SelectField: SelectField,
    BooleanField: CheckboxField,
    RadioGroupField: RadioGroupField,
    CheckboxGroupField: CheckboxGroupField,
    CurrencyField: CurrencyField,
}

const validatorAjv8 = customizeValidator({ ajvFormatOptions: false })

const validator = process.env.NODE_ENV !== 'production' ? validatorAjv8 : validatorAjv6

function scrollTo(formNode, ...args) {
    $(formNode)
        .find('> .form-group.field.field-object:eq(0)')
        .scrollTo(...args)
}

function transformErrors(errors) {
    return errors.map(error => {
        let text = VALIDATION_ERROR_NAME_TEXTS[error.name]

        if (["minLength", "maxLength"].includes(error.name)) {
            text = interpolate(text, error.params.limit)
        }

        if (error.name === 'minItems') {
            text = VALIDATION_ERROR_NAME_TEXTS.required
        }

        if (text) error.message = text

        return error
    })
}

export default function DynaForm(
    {
        children,
        className,
        defaultData,
        hasScrollTopBtn = true,
        onChange,
        ...props
    }
) {
    const ref = useRef()

    const [data, setData] = useState(defaultData)
    const [isLiveValidation, setLiveValidation] = useState(false)
    const [isScrollTopBtnVisible, setScrollTopBtnVisible] = useState(false)

    function scroll(...args) {
        scrollTo(ref.current.formElement, ...args)
    }

    function scrollTop(duration = 500) {
        scroll(0, duration)
    }    

    const scrollToError = useScrollToFormError(
        ref.current?.formElement, scroll
    )

    const onScroll = throttle(e => {
        const offset = e?.target?.scrollTop

        if (!isScrollTopBtnVisible && offset > MIN_SCROLL_OFFSET_TOP) {
            setScrollTopBtnVisible(true)
        }

        if (isScrollTopBtnVisible && offset < MIN_SCROLL_OFFSET_TOP) {
            setTimeout(() => {
                setScrollTopBtnVisible(false)
            }, 250)
        }
    }, MIN_SCROLL_OFFSET_CHANGE_TIME)

    const _onChange = useCallback(({ formData }) => {
        setData(formData)
        onChange(formData)
    }, [onChange])

    const onError = useCallback(() => {
        scrollToError()
        setLiveValidation(true)
    }, [scrollToError])

    useEffect(() => {
        const formNode = ref.current.formElement

        const $scrollableNode = (
            $(formNode).find('> .form-group.field.field-object:eq(0)')
        )

        $scrollableNode.on('scroll', onScroll)

        return () => {
            $scrollableNode.off('scroll', onScroll)
        }
    }, [onScroll])

    return (
        <Form
            ref={ref}
            {...props}
            fields={fields}
            showErrorList={false}
            liveValidate={isLiveValidation}
            onError={onError}
            formData={data}
            validator={validator}
            onChange={_onChange}
            transformErrors={transformErrors}
            className={cn('DynaForm', className)}
            ObjectFieldTemplate={ObjectFieldTemplate}
        >
            {hasScrollTopBtn && isScrollTopBtnVisible && (
                <div
                    title='Back to Top'
                    className="ScrollTopBtn"
                    onClick={() => scrollTop()}
                >
                    <ArrowTop className="ScrollTopBtn-Icon" />
                </div>
            )}
            {children && (
                <div className="DynaForm-Buttons">
                    {children}
                </div>
            )}
        </Form>
    )
}

DynaForm.propTypes = {
    schema: PTypes.object.isRequired,
    uiSchema: PTypes.object,
    formData: PTypes.any,
    disabled: PTypes.bool,
    readonly: PTypes.bool,
    widgets: PTypes.objectOf(PTypes.oneOfType([PTypes.func, PTypes.object])),
    fields: PTypes.objectOf(PTypes.elementType),
    ArrayFieldTemplate: PTypes.elementType,
    ObjectFieldTemplate: PTypes.elementType,
    FieldTemplate: PTypes.elementType,
    ErrorList: PTypes.func,
    onChange: PTypes.func,
    onError: PTypes.func,
    showErrorList: PTypes.bool,
    onSubmit: PTypes.func,
    id: PTypes.string,
    className: PTypes.string,
    tagName: PTypes.elementType,
    name: PTypes.string,
    method: PTypes.string,
    target: PTypes.string,
    action: PTypes.string,
    autocomplete: PTypes.string,
    autoComplete: PTypes.string,
    enctype: PTypes.string,
    acceptcharset: PTypes.string,
    noValidate: PTypes.bool,
    noHtml5Validate: PTypes.bool,
    liveValidate: PTypes.bool,
    hasScrollTopBtn: PTypes.bool,
    validate: PTypes.func,
    transformErrors: PTypes.func,
    formContext: PTypes.object,
    customFormats: PTypes.object,
    additionalMetaSchemas: PTypes.arrayOf(PTypes.object),
    omitExtraData: PTypes.bool,
    extraErrors: PTypes.object,
    ...Form.propTypes
}