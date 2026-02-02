ExchangeApp.modules.SecureMessagingConfigWarning = (function () {

    var fragmentsMap = {};

    var cFragmentUrl = {
        template: 'secure-messaging/config-warning'
    };

    var state = {
        reloadNeeded: false
    };

    var mdlUtils = ExchangeApp.utils.module;

    var loader = ExchangeApp.loaders.FragmentLoader.init('secureMessagingConfigWarning');

    /*-------------utils----------------*/

    function _prepare(){
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
    }

    function _getFragment(){
        return fragmentsMap[_stringifyUrl(cFragmentUrl, {params: true})];
    }

    function _getFragmentHolderId(){
        return _stringifyUrl(cFragmentUrl, {params: true}).replace(/[/&\?=]/g, '_');
    }

    function _getFragmentHolder(){
        return $(document.getElementById(_getFragmentHolderId()));
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

    function _alert(options){
        var fragment = _getFragment();
        return mdlUtils.alert(fragment.$html, fragment.random, options);
    }

    function $find(selector) {
        var fragment = _getFragment();
        return mdlUtils.find(fragment.$html, fragment.random, selector);
    }

    function _setEvents() {
        $find('#activateSesBtn').on('click', function(event) {
            $(event.target).prop('disabled', true);

            $.ajax({
                url: 'secure-messaging/activate',
                type: 'POST',
                beforeSend: function(xhr){
                    mdlUtils.csrf(xhr);
                },
                success: function (data) {
                    $(event.target).prop('disabled', false);

                    _alert({
                        action: 'add',
                        type: 'alert alert-info',
                        placeSelector: '.msgConfigWarningBox .boxBody',
                        message: 'Your Secure Messaging account was activated successfully.',
                        closable: {
                            btn: true
                        }
                    });

                    window.setTimeout(function () {
                        var router = ExchangeApp.routers.ModuleRouter;

                        var url = mdlUtils.getUrl('secure-messaging', null, null);
                        router.route(url);
                        router.reload();
                    }, 2000);
                },
                error: function (error) {
                    $(event.target).prop('disabled', false);

                    mdlUtils.onAjaxError(error, function(e){
                        _alert({
                            action: 'add',
                            placeSelector: '.msgConfigWarningBox .boxBody',
                            message: e.responseText,
                            closable: {
                                timer: 45000,
                                btn: true
                            }
                        });
                    });
                }
            });
        });
    }

    return {
        init: function (url) {
            cFragmentUrl = url;

            this.renderHolder();

            this.loadFragment({
                onFragmentLoaded: function () {
                    _prepare();
                },
                onResourcesLoaded: function () {
                    var cFrg = _getFragment();

                    // initialize widgets
                    cFrg.widgets = {};
                    _setEvents();

                    this.setEvents();

                    this.render();

                    cFrg.inited = true;
                }
            });
        },

        update: function (url) {
            cFragmentUrl = url;
        },

        loadFragment: function(callbacks){
            var self = this;
            loader.load({
                url: cFragmentUrl,
                callbacks: {
                    onFragmentLoaded: function (fragment) {
                        state.reloadNeeded = false;

                        var $fragment = $(fragment).find('#content .markup-frg');

                        var urlTmpl = _stringifyUrl(cFragmentUrl, {params: true});
                        fragmentsMap[urlTmpl] = {
                            $html: $fragment,
                            url: mdlUtils.clone(cFragmentUrl)
                        };

                        callbacks.onFragmentLoaded.apply(self);
                    },
                    onResourcesLoaded: function () {
                        callbacks.onResourcesLoaded.apply(self);
                    }
                }})
        },

        render: function () {
            var $moduleContent = _getFragment().$html;
            _getFragmentHolder().empty();
            _getFragmentHolder().append($moduleContent);
        },

        renderHolder: function() {
            if(document.getElementById(_getFragmentHolderId()))
                return;

            var $holder = $('<div/>');
            $holder.attr('id', _getFragmentHolderId());
            $holder.hide();

            $('#content').append($holder);
        },

        hide: function () {
            _getFragmentHolder().hide();
        },

        show: function () {
            _getFragmentHolder().show();
        },

        getFragment: function(url){
            return  fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function(url){
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        setEvents: function () {

        },

        reloadNeeded: function() {
            return state.reloadNeeded;
        }
    };
})();