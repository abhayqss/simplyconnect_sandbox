ExchangeApp.loaders.ComponentLoader = (function($){

    var $head = $('head');

    var context = ExchangeApp.info.context;

    function getSrc (name) {
        return context + '/resources/js/app/components/' + name + '.js';
    }

    function load(names, onLoaded) {
        var result = {};

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
                        result[name] = true;
                    };
                })(name);

                script.onerror = function(e) {
                    e.status = 401;
                    ExchangeApp.utils.module.onAjaxError(e);
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
                $(window).trigger('allComponentsLoaded');
            }
        }, 300);

        $(window).on('allComponentsLoaded', onLoaded);
    }

    return {
        load: function(names, onLoaded){
            load(names, onLoaded)
        }
    }
})($);