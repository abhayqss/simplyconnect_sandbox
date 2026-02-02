ExchangeApp.loaders.ModuleLoader = (function(){

    var moduleManager = ExchangeApp.managers.ModuleManager;

    return {
        load: function(options){
            var script = document.createElement('script');
            script.src = options.url;
            script.async = false;
            script.onload = function(){
                options.callback && options.callback.apply(moduleManager, options);
                options.onSuccess && options.onSuccess.apply(null);
            };
            script.onerror = function(e) {
                e.status = 401;
                ExchangeApp.utils.module.onAjaxError(e);
            };
            $('head')[0].appendChild(script);
        },

        setManager: function(){
            moduleManager = ExchangeApp.managers.ModuleManager;
        }
    }
})();

ExchangeApp.managers.ModuleManager.setLoader();