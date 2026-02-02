ExchangeApp.loaders.ServiceLoader = (function($){

    var $head = $('head');

    var context = ExchangeApp.info.context;

    function getSrc (name) {
        return context + '/resources/js/app/services/' + name + '.js';
    }

    function random () {
        return Math.random() * 100000000000000000000;
    }

    function load(names, onSuccess, onError) {
        var result = {};
        var eventName = 'allServicesLoaded' + random();

        for (var i = 0; i < names.length; i++) {
            var name = names[i];

            var src = getSrc(name);
            var old = $head.find('script[src*="' + src + '"]');

            if (!old.length) {
                var script = document.createElement('script');
                script.src = src;
                script.async = false;

                script.onload = (function (name) {
                    return function () {
                        setTimeout(function () {
                            result[name] = true;
                        }, 200);
                    };
                })(name);

                script.onerror = function(e) {
                    e.status = 401;
                    onError && onError(e);
                };

                $('head')[0].appendChild(script);
                result[name] = false;
            }
        }

        var interval = setInterval(function() {
            var isAll = true;
            for(var key in result){
                isAll = isAll && result[key];
            }
            if(isAll){
                clearInterval(interval);
                $(window).trigger(eventName);
            }
        }, 300);

        $(window).on(eventName, function () {
            onSuccess && onSuccess()
        });
    }

    return {
        load: function(names, onSuccess, onError){
            load(names, onSuccess, onError)
        }
    }
})($);