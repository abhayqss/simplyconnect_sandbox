ExchangeApp.modules.SecureMessagingSetup = (function () {

    var fragmentsMap = {};

    var cFragmentUrl = {
        template: 'secure-messaging/setup'
    };

    var state = {
        reloadNeeded: false
    };

    var router = ExchangeApp.routers.ModuleRouter;

    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('secureMessagingSetup');

    /*-------------utils----------------*/

    function _prepare(){
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
        _ajaxify();
    }

    function _wizard(options){
        var cFragment = _getFragment();
        return wgtUtils.wizard(cFragment.$html, cFragment.random, options);
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

    function _alert(message, type){
        var options = {
            action: 'add',
            placeSelector: '.msgSetupBody',
            message: message,
            closable: {
                timer: 45000,
                btn: true
            }
        };
        if(type) options.type = type;
        var fragment = _getFragment();
        return wgtUtils.alert(fragment.$html, fragment.random, options);
    }

    function _clearAlerts(){
        $find('.alert').remove();
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

    function $find(selector) {
        var fragment = _getFragment();
        return mdlUtils.find(fragment.$html, fragment.random, selector);
    }

    function _randomize($trgFragment, trgRandom){
        var cFragment = _getFragment();
        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        $fragment = mdlUtils.randomize($fragment, random, false);
        return $fragment;
    }


    /*-------------widgets----------------*/

    function _initChooseKeyStoreWgt(){
        var $chooseKeyStore = $find("input[type='file']");
        _getFragment().widgets.chooseKeyStore = $chooseKeyStore;
        var buttonText = $chooseKeyStore.attr('data-buttonText');
        $find("input[type='file']").filestyle({icon: false, buttonText: buttonText});
    }

    function _initWizardWgt(){
        var cFragment = _getFragment();
        cFragment.widgets.setupMsgWizard = _wizard(
            {
                wizardId: 'msgSetupWzd',
                tabClass: 'nav nav-tabs',
                onNext: function (tab, navigation, index) {
                    switch (index){
                        case 1: return _doKeyStoreStep(tab, index);
                        case 2: return _doPinStep(tab, index);
                        default: return false;
                    }
                },
                onFirst: function (tab, navigation, index) {
                    _getFragment().state.step.pin = false;
                    _getFragment().state.step.keyStore = false;

                    $find('.nav>li:eq(2)').removeClass('wz-done');
                    $find('.testConfigDetails').removeClass('hidden');
                    $find('.testConfigResult').addClass('hidden');

                    _updateBtns(index);
                },
                onTabClick: function () {
                    return false
                },
                nextSelector: $find('.wzBtns .next'),
                firstSelector: $find('.wzBtns .first')
            }
        );
        _setActiveStep(cFragment.widgets.setupMsgWizard.dataset.activeStep);
    }

    function _updateBtns(index) {
        var stepCss = ['.keyStoreStep', '.pinStep', '.testStep', '.resultStep'];

        _clearAlerts();
        $find('.btn-lt').addClass('hidden');
        $find(stepCss[index]).removeClass('hidden');
    }

    function _setActiveStep(index) {
        $find('.nav>li').removeClass('active');
        if (index !== 3) $find('.nav>li:eq(' + index + ')').addClass('active');
        $find('.nav>li:lt(' + index + ')').addClass('wz-done');

        $find('.tab-pane').removeClass('active');
        $find('.tab-pane:eq(' + index + ')').addClass('active');

        if (index == 3) {
            $find('.tab-pane:eq(2)').addClass('active');
            $find('.testConfigDetails').addClass('hidden');
            $find('.testConfigResult').removeClass('hidden');
        }
        _updateBtns(index);
    }

    function _doKeyStoreStep(tab, index){
        if(_getFragment().state.step.keyStore) return true;

        var $form = $find("#keyStoreForm");
        if (!$form.valid()) return false;

        $.ajax({
            url: $form.attr('action'),
            data: new FormData($form[0]),
            type: 'POST',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            beforeSend: function(xhr){
                mdlUtils.csrf(xhr);
            },
            success: function (data) {
                _getFragment().state.step.keyStore = true;

                var fullPath = $find('#keyStore').val();
                var fileName = fullPath.split(/\\|\//).pop();
                $find('#certName').text(fileName);

                _updateBtns(1);
                tab.addClass('wz-done');

                $find('#uploadBtn').trigger('click');
            },
            error: function (error) {
                mdlUtils.onAjaxError(error, function() {
                    _getFragment().state.step.keyStore = false;
                    _alert(error.responseText);
                });
            }
        });
        return false;
    }

    function _doPinStep(tab, index){
        if(_getFragment().state.step.pin) return true;

        var $form = $find("#pinForm");
        if (!$form.valid()) return false;

        $.ajax({
            url: $form.attr('action'),
            data: new FormData($form[0]),
            type: 'POST',
            processData: false,
            contentType: false,
            beforeSend: function(xhr){
                mdlUtils.csrf(xhr);
            },
            success: function(data){
                _getFragment().state.step.pin = true;
                $find('#pinValue').text($find('#pin').val());

                _updateBtns(2);
                tab.addClass('wz-done');

                $find('#uploadBtn').trigger('click');
            },
            error: function(error){
                mdlUtils.onAjaxError(error, function() {
                    _getFragment().state.step.pin = false;
                    _alert(error.responseText);
                });
            }
        });
        return false;
    }

    /*-------------validation----------------*/

    function _addKeyStoreFormValidation() {
        return $find("#keyStoreForm").validate({
            rules: {
                keystore: {required: true}
            },
            messages: {
                keystore: {
                    required: getErrorMessage("field.empty")
                }
            },
            showErrors: function (errorMap, errorList) {
                for (var formControlName in errorMap) {
                    var errMessage = errorMap[formControlName];
                    _clearAlerts();
                    _alert(errMessage);
                }
                this.defaultShowErrors();
                $find('label[class="error"]').remove();
            },
            onfocusout: function (elem, event) {
                _clearAlerts();
            },
            onkeyup: function (elem, event) {
                _clearAlerts();
            }
        });
    }

    function _addPinFormValidation() {
        return $find("#pinForm").validate({
            rules: {
                pin: {required: true}
            },
            messages: {
                pin: {
                    required: getErrorMessage("field.empty")
                }
            },
            showErrors: function (errorMap, errorList) {
                for (var formControlName in errorMap) {
                    var errMessage = errorMap[formControlName];
                    _clearAlerts();
                    _alert(errMessage);
                }
                this.defaultShowErrors();
                $find('label[class="error"]').remove();
            },
            onfocusout: function (elem, event) {
                _clearAlerts();
            },
            onkeyup: function (elem, event) {
                _clearAlerts();
            }
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

                    _addKeyStoreFormValidation();
                    _addPinFormValidation();

                    // initialize widgets
                    cFrg.widgets = {};
                    cFrg.state = {step: {keyStore: false, pin: false}};

                    _initWizardWgt();
                    _initChooseKeyStoreWgt();

                    this.setEvents();

                    this.render();
                    this.show();

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
            $('#content').addClass('loading')
        },

        show: function () {
            _getFragmentHolder().show();
            $('#content').removeClass('loading')
        },

        getFragment: function(url){
            return  fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function(url){
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        setEvents: function () {
            $find('#testBtn').on('click', function () {
                $.ajax({
                    type: "GET",
                    url: "secure-messaging/setup/verify",
                    success: function (response) {
                        _setActiveStep(3);

                        ExchangeApp.managers.EventManager.publish('messaging_setup_changed');
                    },
                    error: function (error) {
                        mdlUtils.onAjaxError(error, function() {
                            _alert(error.responseText);
                            $find('.wz-done').removeClass('wz-done');

                            ExchangeApp.managers.EventManager.publish('messaging_setup_changed');
                        });
                    }
                });
            });
        },

        reloadNeeded: function() {
            return state.reloadNeeded;
        }
    };
})();