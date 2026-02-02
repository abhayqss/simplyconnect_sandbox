/**
 * Created by stsiushkevich on 01.11.18.
 */
(function ($) {
    var messages = {};

    var context = ExchangeApp.info.context;
    var auth = ExchangeApp.utils.auth;

    $.ajax({
        url: context + '/error-messages',
        type: 'GET',
        contentType: 'application/json',
        headers: {
            'X-CSRF-TOKEN': auth.getToken()
        }
    }).then(function (data) {
        messages = data;
    });

    window.getErrorMessage = function (key) {
        return key ? messages[key] : null;
    }
})($);