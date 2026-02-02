/**
 * Created by stsiushkevich on 13.09.18.
 */

define(function () {
    var context = ExchangeApp.info.context;

    var auth = ExchangeApp.utils.auth;

    function EventService () {
        CURDService.apply(this);
    }

    EventService.prototype = Object.create(CURDService.prototype);
    EventService.prototype.constructor = EventService;

    EventService.prototype.findById = function (id) {
        return $.ajax({
            url: context + '/events/' + id,
            type: 'GET',
            contentType: 'json',
            headers: {
                'X-CSRF-TOKEN': auth.getToken()
            }
        })
    };

    return EventService;
});

