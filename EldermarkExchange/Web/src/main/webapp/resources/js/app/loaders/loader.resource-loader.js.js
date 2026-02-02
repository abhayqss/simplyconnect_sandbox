ExchangeApp.loaders.ResourceLoader = (function($){

    var $head = $('head');

    function loadCss(hrefs, onLoaded) {
        $.each(hrefs, function (href) {
            var links = {};
            var old = $head.find('link[href*="' + href + '"]');

            if (!old.length) {
                var link = document.createElement("link");
                link.rel = "stylesheet";
                link.type = "text/css";
                link.href = href;
                link.onload = function(){
                    links[href] = true;
                };

                $('head')[0].appendChild(link);
                links[href] = false;
            }

            var interval = setInterval(function(){
                var all = true;
                for(var key in links){
                    all = all && links[key];
                }
                if(all){
                    clearInterval(interval);
                    $(window).trigger('allLinksLoaded');
                }
            }, 300);

            $(window).on('allLinksLoaded', onLoaded);
        });
    }

    function loadScripts(srcs, onLoaded) {
        var scripts = {};
        $.each(srcs, function (src) {
            var old = $head.find('script[src*="' + src + '"]');

            if (!old.length) {
                var script = document.createElement('script');
                script.src = src;
                script.async = false;
                script.onload = function(){
                    scripts[src] = true;
                };
                script.onerror = function(e) {
                    e.status = 401;
                    ExchangeApp.utils.module.onAjaxError(e);
                };

                $('head')[0].appendChild(script);
                scripts[src] = false;
            }
        });

        var interval = setInterval(function() {
            var all = true;
            for(var key in scripts){
                all = all && scripts[key];
            }
            if(all){
                clearInterval(interval);
                $(window).trigger('allScriptsLoaded');
            }
        }, 300);

        $(window).on('allScriptsLoaded', onLoaded);
    }

    return {
        loadCss: function(hrefs, onLoaded){
            loadCss(hrefs, onLoaded)
        },
        loadScripts: function(srcs, onLoaded){
            loadScripts(srcs, onLoaded)
        }
    }
})($);