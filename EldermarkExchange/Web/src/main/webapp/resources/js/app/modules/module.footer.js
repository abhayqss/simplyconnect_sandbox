ExchangeApp.modules.Footer = (function () {

    var fragmentsMap = {};

    var cFragmentUrl = {
        template: 'footer'
    };

    var state = {
        reloadNeeded: false
    };

    var router = ExchangeApp.routers.ModuleRouter;

    var mdlUtils = ExchangeApp.utils.module;

    var loader = ExchangeApp.loaders.FragmentLoader.init('footer');

    /*-------------utils----------------*/

    function _prepare(){
        var cFragment = _getFragment();
        cFragment.$holder = $('#footer');
        cFragment.random = mdlUtils.rand();
        _randomize();
        _ajaxify();
    }

    function _ajaxify($trgFragment, trgRandom){
        var cFragment = _getFragment();

        if(!cFragment.ajaxMap){
            cFragment.ajaxMap = {};
        }

        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        var urls = mdlUtils.findAjaxUrls($fragment, random);
        $.each(urls, function(i, url){
            cFragment.ajaxMap[_stringifyUrl(url, {params: true})] = url;
        });

        mdlUtils.find($fragment, random, '[data-ajax-load="true"]').on('click', function(){
            var template = $(this).attr('data-ajax-url-tmpl');
            var vars = $(this).attr('data-ajax-url-vars');
            var params = $(this).attr('data-ajax-url-params');

            var url = mdlUtils.getUrl(template, vars, params);
            router.route(url);
            return false;
        });

        mdlUtils.find($fragment, random, '[data-ajax-anchor="true"]').on('click', function(){
            var $trg = $($(this).attr('href'));
            if($trg.length){
                $('html, body').animate({ scrollTop: $trg.offset().top}, 200);
            }
            return false;
        });
    }

    function _getFragment(){
        return fragmentsMap[_stringifyUrl(cFragmentUrl, {params: true})];
    }

    function _stringifyUrl(url, excludeOptions){
        return mdlUtils.stringifyUrl(url, excludeOptions);
    }

    function _randomize($trgFragment, trgRandom){
        var cFragment = _getFragment();
        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        $fragment = mdlUtils.randomize($fragment, random, false);
        return $fragment;
    }

    return {
        init: function (url) {
            cFragmentUrl = url;

            this.loadFragment({
                onFragmentLoaded: function () {
                    state.reloadNeeded = false;

                    _prepare();
                },
                onResourcesLoaded: function(){
                    var fragment = _getFragment();

                    fragment.widgets = {};

                    this.setEvents();

                    fragment.inited = true;

                    this.render();
                }
            });
        },


        update: function (url) {
            cFragmentUrl = url;
        },

        getFragment: function(url){
            return  fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function(url){
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        loadFragment: function(callbacks){
            var self = this;
            loader.load({
                url: cFragmentUrl,
                callbacks:{
                    onFragmentLoaded: function(fragment){
                        var $fragment = $(fragment).find('#footer .markup-frg');

                        var urlTmpl = _stringifyUrl(cFragmentUrl, {params: true});
                        fragmentsMap[urlTmpl] = {
                            $html: $fragment,
                            url: mdlUtils.clone(cFragmentUrl)
                        };

                        callbacks.onFragmentLoaded.apply(self);
                    },
                    onResourcesLoaded: function(){
                        callbacks.onResourcesLoaded.apply(self);
                    }
                }
            });
        },

        render: function () {
            var fragment = _getFragment();
            if (fragment && fragment.$holder) {
                fragment.$holder.append(fragment.$html);
            }
        },

        hide: function () {
            var fragment = _getFragment();
            if(fragment){
                fragment.$holder.hide();
            }
        },

        show: function () {
            var fragment = _getFragment();
            if (fragment && fragment.$holder) {
                fragment.$holder.show();
            }
        },

        setEvents: function(){

        },

        reloadNeeded: function() {
            return state.reloadNeeded;
        }
    };
})();