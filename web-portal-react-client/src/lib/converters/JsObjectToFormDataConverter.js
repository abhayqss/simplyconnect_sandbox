import Converter from './Converter'
import { isArray, isObject } from "underscore";

function arrayToFormData(arr, formData = new FormData(), namespace) {
    arr.forEach((item, index) => {
        let key = `${namespace}[${index}]`

        if (item instanceof File) {
            formData.append(key, item)
        } else {
            toFormData(item, formData, key)
        }
    })
}

function objectToFormData(o, formData = new FormData(), namespace) {
    for (let [key, value] of Object.entries(o)) {
        toFormData(value, formData, namespace ? `${namespace}.${key}` : key)
    }
}

function toFormData(value, formData = new FormData(), namespace) {
    switch (true) {
        case isObject(value) && !(isArray(value) || value instanceof File):
            objectToFormData(value, formData, namespace)
            break
    
        case isArray(value):
            arrayToFormData(value, formData, namespace)
            break

        case value != null:
            formData.append(namespace, value)
            break
    }

    return formData
}

export default class JsObjectToFormDataConverter extends Converter {
    convert(o) {
        return toFormData(o)
    }
}