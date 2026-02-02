export default class Response {
    static success = function(data, extraBodyProps = {}, extraProps = {}) {
        let body = { success: true, ...extraBodyProps }

        if (data) {
            body.data = data
        }

        let resp = {
            body,
            ...extraProps,
            statusCode: 200,
            header: {},
            headers: {}
        }

        resp.text = JSON.stringify(resp)

        return resp
    }

    static failure = function(code = 'error', message = 'Error', statusCode = 500) {
        let resp = {
            body: { success: false, error: { code, message } },
            statusCode: statusCode
        }

        resp.text = JSON.stringify(resp)

        return resp
    }
}