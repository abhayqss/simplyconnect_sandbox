import React, {
    useRef,
    useCallback
} from 'react'

import $ from 'jquery'

const NOTIFY_APP_LOGIN_URL = 'https://notifydemo.notifync.com/account/login'

const NOTIFY_TEST_USER_LOGIN = 'Admin@notify.com'
const NOTIFY_TEST_USER_PASSWORD = '111111'

export default function useNotifyLoginForm() {
    const formRef = useRef()

    const submit = useCallback(() => {
        const form = (
            formRef.current
        )

        $(form).attr('action', NOTIFY_APP_LOGIN_URL)

        $(form).find('[name="identity"]').val(NOTIFY_TEST_USER_LOGIN)
        $(form).find('[name="credential"]').val(NOTIFY_TEST_USER_PASSWORD)

        form.submit()
    }, [])

    const form = (
        <form
            method="POST"
            target="_blank"
            className="d-none"
            ref={formRef}
        >
            <input name="identity" />
            <input name="credential" />
        </form>
    )

    return [submit, form]
}