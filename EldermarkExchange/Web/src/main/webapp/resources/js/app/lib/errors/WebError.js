define([path('./BaseError')], function (BaseError) {

    function WebError () {
        BaseError.apply(this, arguments);
    }

    WebError.prototype = Object.create(BaseError.prototype);
    WebError.prototype.constructor = WebError;

    return WebError;
});