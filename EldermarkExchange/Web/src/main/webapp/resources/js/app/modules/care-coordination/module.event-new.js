ExchangeApp.modules.CareCoordinationNewEvent = (function () {

    // history of loaded segments
    var fragmentsMap = {};
    // root url
    var cFragmentUrl = {
        template: 'care-coordination/event-new'
    };
    var holderContentId = "newEventContent";
    // flag if true then reloaded every time
    var state = {
        reloadNeeded: false
    };
    // load root routers (map url to module)
    var router = ExchangeApp.routers.ModuleRouter;
    // Utility
    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('careCoordinationNewEvent');

    var pModule;


    /*-------------utils----------------*/

    function _prepare() {
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
        _ajaxify();
    }

    function _ajaxify($trgFragment, trgRandom) {
        var cFragment = _getFragment();

        if (!cFragment.ajaxMap) {
            cFragment.ajaxMap = {};
        }

        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        var urls = mdlUtils.findAjaxUrls($fragment, random);
        $.each(urls, function (i, url) {
            cFragment.ajaxMap[_stringifyUrl(url, {params: true})] = url;
        });

        mdlUtils.find($fragment, random, '[data-ajax-load="true"]').on('click', function () {
            var template = $(this).attr('data-ajax-url-tmpl');
            var vars = $(this).attr('data-ajax-url-vars');
            var params = $(this).attr('data-ajax-url-params');

            var url = mdlUtils.getUrl(template, vars, params);

            router.route(url);
            return false;
        });

        mdlUtils.find($fragment, random, '[data-ajax-anchor="true"]').on('click', function () {
            var $trg = $($(this).attr('href'));
            if ($trg.length) {
                $('html, body').animate({scrollTop: $trg.offset().top}, 200);
            }
            return false;
        });
    }

    function _getFragment() {
        return fragmentsMap[_stringifyUrl(cFragmentUrl, {params: true})];
    }

    function _getFragmentHolder() {
        return $('[id^="' + _getFragmentHolderId() + '"]');
    }

    function _getFragmentHolderId() {
        return holderContentId;
    }

    function _stringifyUrl(url, excludeOptions) {
        return mdlUtils.stringifyUrl(url, excludeOptions);
    }

    function $find(selector) {
        var fragment = _getFragment();
        return mdlUtils.find(fragment.$html, fragment.random, selector);
    }

    function _randomize($trgFragment, trgRandom) {
        var cFragment = _getFragment();
        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        $fragment = mdlUtils.randomize($fragment, random, false);
        return $fragment;
    }

    function _cleanId(id) {
        var fragment = _getFragment();
        if (id) return id.replace(fragment.random, '');
        return id;
    }


    //function _alert(message, type) {
    //    var options = {
    //        action: 'add',
    //        placeSelector: '#newEventForm',
    //        message: message,
    //        closable: {
    //            timer: 45000,
    //            btn: true
    //        }
    //    };
    //    if (type) options.type = type;
    //    var fragment = _getFragment();
    //    return wgtUtils.alert(fragment.$html, fragment.random, options);
    //}

    function _clearAlerts() {
        $find('.alert').remove();
    }

    function _get(id) {
        return $find('[id="' + id + _getFragment().random + '"]');
    }

    function _initCheckbox(checkBoxId, contentId) {
        $find('[id="' + checkBoxId + _getFragment().random + '"]').on('change', function (e) {
            if ($(this).context.checked) {
                $('[id="' + contentId + _getFragment().random + '"]').show();
            } else {
                $('[id="' + contentId + _getFragment().random + '"]').hide();
            }
        });
    }

    function _addStyler() {
        $find('input').styler();
    }

    function _addValidation() {
        return $find("#newEventForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    'employee.roleId': {required: true},

                    'manager.firstName': {required: true, minlength: 2, maxlength: 128},
                    'manager.lastName': {required: true, minlength: 2, maxlength: 128},
                    'manager.email': {required: false, email: true},
                    'manager.phone': {required: false, phone: true},
                    //
                    'responsible.firstName': {required: true, minlength: 2, maxlength: 128},
                    'responsible.lastName': {required: true, minlength: 2, maxlength: 128},
                    'responsible.address.street': {required: true, minlength: 2, maxlength: 255},
                    'responsible.address.city': {required: true, minlength: 2, maxlength: 128},
                    'responsible.address.state': {required: true},
                    'responsible.address.zip': {required: true, positiveInteger: true, lengthEqual: 5},
                    //
                    'eventDetails.eventDatetime': {required: true},
                    'eventDetails.followUpDetails': {required: true, maxlength: 5000},

                    'treatingPhysician.firstName': {required: true, minlength: 2, maxlength: 128},
                    'treatingPhysician.lastName': {required: true, minlength: 2, maxlength: 128},
                    'treatingPhysician.address.street': {required: true, minlength: 2, maxlength: 255},
                    'treatingPhysician.address.city': {required: true, minlength: 2, maxlength: 128},
                    'treatingPhysician.address.state': {required: true},
                    'treatingPhysician.address.zip': {required: true, positiveInteger: true, lengthEqual: 5},
                    'treatingPhysician.phone': {required: false, phone: true},
                    //
                    'treatingHospital.name': {required: true, minlength: 2, maxlength: 300},
                    'treatingHospital.address.street': {required: true, minlength: 2, maxlength: 255},
                    'treatingHospital.address.city': {required: true, minlength: 2, maxlength: 128},
                    'treatingHospital.address.state': {required: true},
                    'treatingHospital.address.zip': {required: true, positiveInteger: true, lengthEqual: 5},
                    'treatingHospital.phone': {required: false, phone: true}
                },
                messages: {
                    'manager.firstName': {
                        required: getErrorMessage("field.empty")
                    },
                    'manager.lastName': {
                        required: getErrorMessage("field.empty")
                    },
                    gender: {
                        required: getErrorMessage("field.empty")
                    },
                    dateOfBirth: {
                        dateExp: getErrorMessage("field.dateFormat"),
                        required: getErrorMessage("field.empty")
                    },
                    ssn: {
                        integer: getErrorMessage("field.integer"),
                        lengthEqual: getErrorMessage("field.lengthEqual.4"),
                        required: getErrorMessage("field.empty")
                    },
                    phone: {
                        phone: getErrorMessage("field.phone.format")
                    },
                    state: {
                        stateUS: getErrorMessage("field.state")
                    }
                }
            })
        );
    }


    return {
        init: function (url, parentModule) {
            cFragmentUrl = url;
            pModule = parentModule;
            /*this.renderHolder();*/

            this.loadFragment({
                onFragmentLoaded: function () {
                    _prepare();
                },
                onResourcesLoaded: function () {
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

        getFragment: function (url) {
            return fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function (url) {
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        loadFragment: function (callbacks) {
            var self = this;
            loader.load({
                url: cFragmentUrl,
                callbacks: {
                    onFragmentLoaded: function (fragment) {
                        state.reloadNeeded = false;

                        var $fragment = $(fragment).find('#content').children();

                        var urlTmpl = _stringifyUrl(cFragmentUrl, {params: true});
                        fragmentsMap[urlTmpl] = {
                            $html: $fragment,
                            url: mdlUtils.clone(cFragmentUrl)
                        };

                        callbacks.onFragmentLoaded.apply(self);
                    },
                    onResourcesLoaded: function () {
                        callbacks.onResourcesLoaded.apply(self);

                        _addValidation();
                        _addStyler();
                    }
                }
            })
        },

        render: function () {
            var $moduleContent = _getFragment().$html;
            _getFragmentHolder().empty();
            _getFragmentHolder().append($moduleContent);
        },

        /*     renderHolder: function () {
         $('id #'+_getFragmentHolderId())
         if (document.getElementById(_getFragmentHolderId())) {
         return;
         }
         },*/

        hide: function () {
            _getFragmentHolder().hide();
        },

        show: function () {
            $('a[data-target^="#' + holderContentId + '"]').parent('li').addClass('active');
            _getFragmentHolder().show();
        },


        setEvents: function () {

            _initCheckbox('includeManager', 'includeManagerContent');
            _initCheckbox('includeResponsible', 'includeResponsibleContent');
            _initCheckbox('responsible.includeAddress', 'includeResponsibleAddressContent');
            _initCheckbox('eventDetails.followUpExpected', 'followUpDetailsContent');
            _initCheckbox('includeTreatingPhysician', 'includeTreatingPhysicianContent');
            _initCheckbox('treatingPhysician.includeAddress', 'includeTreatingPhysicianAddressContent');
            _initCheckbox('includeHospital', 'includeHospitalContent');
            _initCheckbox('treatingHospital.includeAddress', 'includeHospitalAddressContent');
            _initCheckbox('includeResponsible', 'includeResponsibleContent');


            $find('[id="eventDetails.eventDatetime' + _getFragment().random + '"]').datetimepicker({
                defaultDate: new Date(),
                format: 'YYYY-MM-DD hh:mm A'
            });

            $find("#clear").on('click', function () {
                $find('#newEventForm').clearForm();
                return false;
            });

            $find("#submit").on('click', function () {
                var $form = $find("#newEventForm");
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
                        _clearAlerts();
                        alert('Event has been successfully created');
                    },
                    error: function (error) {
                        mdlUtils.onAjaxError(error, function () {
                            _clearAlerts();
                            _getFragment().state.step.keyStore = false;
                            alert(error.responseText);
                        });
                    }
                });
                return false;
            });

            return false;

        },

        reloadNeeded: function () {
            return state.reloadNeeded;
        },

        getParentModule: function () {
            return pModule;
        }

    };
})();