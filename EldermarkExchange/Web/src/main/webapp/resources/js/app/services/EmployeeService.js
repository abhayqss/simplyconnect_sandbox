/**
 * Created by stsiushkevich on 13.09.18.
 */

var EmployeeService = (function ($) {

    var context = ExchangeApp.info.context;

    var auth = ExchangeApp.utils.auth;

    function EmployeeService () {
        CURDService.apply(this);
    }

    EmployeeService.prototype = Object.create(CURDService.prototype);
    EmployeeService.prototype.constructor = EmployeeService;

    EmployeeService.prototype.find = function () {
        return $.ajax({
            url: context + '/employee',
            type: 'GET',
            contentType: 'json',
            headers: {
                'X-CSRF-TOKEN': auth.getToken()
            }
        })
    };

    return EmployeeService;
})($);