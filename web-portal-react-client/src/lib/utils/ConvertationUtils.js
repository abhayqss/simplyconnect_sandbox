import {
    noop,
    isArray,
    isObject
} from 'underscore'

export function convertBase64ToFile(base64, fileName, mimeType) {
    const bstr = atob(base64)

    let n = bstr.length
    const buffer = new Uint8Array(n)

    while (n--) {
        buffer[n] = bstr.charCodeAt(n)
    }

    return new File(
        [buffer],
        fileName,
        { type: mimeType }
    )
}

export function convertBinaryToBase64(bytes) {
    return bytes
}

export function convertDataUrlToFile(dataUrl, fileName) {
    const [other, data] = dataUrl.split(',')
    const mime = other.match(/:(.*?);/)[1]
    return convertBase64ToFile(data, fileName, mime)
}

export function convertToFormData(value, formData = new FormData(), namespace) {
    switch (true) {
        case isObject(value) && !(isArray(value) || value instanceof File):
            convertObjectToFormData(value, formData, namespace)
            break

        case isArray(value):
            convertArrayToFormData(value, formData, namespace)
            break

        case value != null:
            formData.append(namespace, value)
            break
    }

    return formData
}

export function convertArrayToFormData(arr, formData = new FormData(), namespace) {
    arr.forEach((item, index) => {
        let key = `${namespace}[${index}]`

        if (item instanceof File) {
            formData.append(key, item)
        } else {
            convertToFormData(item, formData, key)
        }
    })
}

export function convertObjectToFormData(o, formData = new FormData(), namespace) {
    for (let [key, value] of Object.entries(o)) {
        convertToFormData(value, formData, namespace ? `${namespace}.${key}` : key)
    }
}

export function convertImageToBlob(url, {
	onSuccess = noop,
	onError = noop
}) {
	const image = document.createElement('img')

	image.crossOrigin = 'anonymous'
	image.src = url

	image.onload = () => {
		const canvas = document.createElement('canvas')
		const context = canvas.getContext('2d')

		canvas.width = image.width
		canvas.height = image.height

		context.drawImage(image, 0, 0, image.width, image.height)

		const result = canvas.toBlob(onSuccess)

		onSuccess(result)
	}

	image.onerror = () => onError()
}