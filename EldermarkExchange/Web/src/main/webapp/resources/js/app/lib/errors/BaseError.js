define([], function () {

    function BaseError (params) {
        Error.apply(this);

        this.code = params.code;
        this.message = params.message || params.status;
        this.status = params.status;
        this.body = params.body;
    }

    BaseError.prototype = Object.create(Error.prototype);
    BaseError.prototype.constructor = BaseError;

    return BaseError;
});