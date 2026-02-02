define([path('./WebError')], function (WebError) {

    function NetworkError () {
        WebError.apply(this, arguments);
    }

    NetworkError.prototype = Object.create(WebError.prototype);
    NetworkError.prototype.constructor = NetworkError;

    return NetworkError;
});