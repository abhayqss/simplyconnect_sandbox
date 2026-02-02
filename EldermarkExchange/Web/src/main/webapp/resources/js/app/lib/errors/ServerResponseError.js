define([path('./WebError')], function (WebError) {

    function ServerResponseError () {
        WebError.apply(this, arguments);
    }

    ServerResponseError.prototype = Object.create(WebError.prototype);
    ServerResponseError.prototype.constructor = ServerResponseError;

    return ServerResponseError;
});