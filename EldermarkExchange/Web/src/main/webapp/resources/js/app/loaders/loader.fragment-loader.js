ExchangeApp.loaders.FragmentLoader = (function(){

    var utils = ExchangeApp.utils.module;

    function _loadResources(module, ajaxData, callback){

        var loadedLinksEvent = module + 'LinksLoaded';
        var loadedScriptsEvent = module + 'ScriptsLoaded';
        var loadedResourcesEvent = module + 'ResourcesLoaded';

        $(window).off(loadedLinksEvent + ' ' + loadedScriptsEvent + ' ' + loadedResourcesEvent);

        _loadCss(module, ajaxData);
        _loadScripts(module, ajaxData);

        var rscLoaded = {
            links: false,
            scripts: false
        };

        $(window).on(loadedLinksEvent + ' ' + loadedScriptsEvent, function(e){
            if(e.type == loadedLinksEvent) rscLoaded.links = true;
            if(e.type == loadedScriptsEvent) rscLoaded.scripts = true;

            if(rscLoaded.links && rscLoaded.scripts){
                $(window).trigger(loadedResourcesEvent);
            }
        });

        $(window).on(loadedResourcesEvent, function(e){
            if(callback) callback(e);
        });
    }

    function _loadCss(module, ajaxData) {
        var links = $(ajaxData).find('link');

        if (links.size()) {
            var result = {};

            links.each(function (i, item) {
                var href = $(item).attr('href');

                if (!$('head').find('link[href*="' + href + '"]').length) {
                    var link = document.createElement("link");
                    link.rel = "stylesheet";
                    link.type = "text/css";
                    link.href = href;
                    link.onload = function () {
                        result[href] = true;
                    };
                    $('head')[0].appendChild(link);
                    result[href] = false;
                }
            });

            var interval = setInterval(function () {
                var all = true;
                for (var key in result) {
                    all = all && result[key];
                }
                if (all) {
                    clearInterval(interval);
                    $(window).trigger(module + 'LinksLoaded');
                }
            }, 100);
        }

        else setTimeout(function () {
            $(window).trigger(module + 'LinksLoaded');
        });
    }

    function _loadScripts(module, ajaxData) {
        var scripts = $(ajaxData).find('script');

        if (scripts.size()) {
            var result = {};

            scripts.each(function (i, item) {
                var src = $(item).attr('src');

                if(src) {
                    var oldScript = $('head').find('script[src*="' + src + '"]');
                    var exists = oldScript.length;

                    if (!exists){
                        var script = document.createElement('script');
                        script.src = src;
                        script.async = false;
                        script.onload = function(){
                            result[src] = true;
                        };
                        script.onerror = function(e) {
                            e.status = 401;
                            ExchangeApp.utils.module.onAjaxError(e);
                        };

                        var data = $(item).data();

                        for (var k in data) {
                            script.dataset[k] = data[k];
                        }

                        $('head')[0].appendChild(script);
                        result[src] = false;
                    }
                }
            });

            var interval = setInterval(function(){
                var all = true;
                for(var key in result){
                    all = all && result[key];
                }
                if(all){
                    clearInterval(interval);
                    $(window).trigger(module + 'ScriptsLoaded');
                }
            }, 100);
        }

        else setTimeout(function () {
            $(window).trigger(module + 'ScriptsLoaded');
        });
    }

    return {
        _module: '',
        load: function(options){
            var self = this;
            $.ajax({
                    type: 'GET',
                    headers: {'X-Content-Compressing': 'enabled'},
                    url: utils.stringifyUrl(options.url),
                    success: function (fragment) {
                        _loadResources(self._module, fragment, options.callbacks.onResourcesLoaded);
                        options.callbacks.onFragmentLoaded(fragment);
                    },
                    error: function (error) {
                        ExchangeApp.utils.module.onAjaxError(error)
                    }
                }
            );
        },
        init: function(module){
            this._module = module;
            return utils.clone(this);
        }
    }
})();