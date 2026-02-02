define([path('./WebError')], function (WebError) {

    function ServerError () {
        WebError.apply(this, arguments);
    }

    ServerError.prototype = Object.create(WebError.prototype);
    ServerError.prototype.constructor = ServerError;

    return ServerError;
});