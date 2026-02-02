/**
 * Created by stsiushkevich on 14.09.18.
 */

var ServiceProvider = (function ($) {

    var loader = ExchangeApp.loaders.ServiceLoader;

    var services = {};

    function ServiceProvider () {}

    ServiceProvider.getService = function (name) {
        var deferred = $.Deferred();

        if (services[name]) {
            setTimeout(function () {
                deferred.resolve(services[name]);
            }, 100);
        }

        (function (name) {
            loader.load([name], function () {
                var Service = window[name];

                if (Service) {
                    var service = new Service();
                    services[name] = service;
                    deferred.resolve(service);
                }

                else deferred.reject(
                    new Error('Cannot load service "' + name + '"')
                );

            }, function (error) {
                deferred.reject(error);
            });
        })(name);

        return deferred;
    };

    return ServiceProvider;
})($);