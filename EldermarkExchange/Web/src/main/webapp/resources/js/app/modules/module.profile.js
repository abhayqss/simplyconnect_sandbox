ExchangeApp.modules.Profile = (function () {
    var fragmentsMap = {};

    var cFragmentUrl = {
        template: 'profile/show'
    };

    var state = {
        reloadNeeded: false
    };

    var router = ExchangeApp.routers.ModuleRouter;

    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('profile');

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
            // deselect all other tabs
            /*$(this).parent('li').parent('ul').children('li').each(function () {
                $(this).removeClass('active');
            });*/
            var template = $(this).attr('data-ajax-url-tmpl');
            var vars = $(this).attr('data-ajax-url-vars');
            var params = $(this).attr('data-ajax-url-params');
            var target = $(this).attr('data-target');

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

    function _grid(options) {
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }

    function $find(selector) {
        var fragment = _getFragment();
        return mdlUtils.find(fragment.$html, fragment.random, selector);
    }

    function _getFragmentHolder() {
        return $(document.getElementById(_getFragmentHolderId()));
    }

    function _getFragmentHolderId() {
        return _stringifyUrl(cFragmentUrl, {params: true}).replace(/[/&\?=]/g, '_');
    }

    function _stringifyUrl(url, excludeOptions) {
        return mdlUtils.stringifyUrl(url, excludeOptions);
    }

    function _randomize($trgFragment, trgRandom) {
        var cFragment = _getFragment();
        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        $fragment = mdlUtils.randomize($fragment, random, false);
        return $fragment;
    }

    function _initLinkedAccountsMasonry(){
        $find('.guardians').masonry({
            itemSelector: '.item',
            percentPosition: true,
            columnWidth: '.item'
        });

        $find('.ccdHeaderItem .commonInfo').masonry({
            itemSelector: '.item',
            percentPosition: true,
            columnWidth: '.item'
        });
    }

    function _linkAccounts($container, $modal) {
        var $form = $container.find("#linkAccountsForm");
        if (!$form.valid()) {
            return false;
        }
        $.ajax({
            url: 'profile/add',
            type: 'POST',
            data: $form.serialize(),
            beforeSend: function(xhr){
                mdlUtils.csrf(xhr);
            },
        }).success(function (data) {
            if (data == "Success") {
                _getFragment().widgets.linkedEmployeeList.api().ajax.reload();
                _loadLinkedEmployeesDetails($find('#linkedEmployeesDetails'));
                $('[id^=createdLinkedAccount]').show();
                $modal.modal('hide');
                $('.modal-backdrop').remove();
            } else {
                $("#formError").text(data);
            }
        }).fail(function (response) {
            mdlUtils.onAjaxError(response, function () {
                $("#formError").text(response.responseText);
            });
        });
        return false;
    }

    function _linkNewAccount($container, $modal) {
        var $form = $container.find("#linkAccountsForm");
        if (!$form.valid()) {
            return false;
        }
        $.ajax({
            url: 'profile/validatePasswordComplexity',
            type: 'POST',
            data: $form.serialize()
        }).success(function (data) {
            if (data==true) {
                $.ajax({
                    url: 'profile/createandlink',
                    type: 'POST',
                    data: $form.serialize(),
                    beforeSend: function(xhr){
                        mdlUtils.csrf(xhr);
                    },
                }).success(function (data) {
                    if (data == "Success") {
                        _getFragment().widgets.linkedEmployeeList.api().ajax.reload();
                        _loadLinkedEmployeesDetails($find('#linkedEmployeesDetails'));
                        $('[id^=createdLinkedAccount]').show();
                        $modal.modal('hide');
                        $('.modal-backdrop').remove();
                    } else {
                        $("#formError").text(data);
                    }
                }).fail(function (response) {
                    mdlUtils.onAjaxError(response, function () {
                        $("#formError").text(response.responseText);
                    });
                });
            } else {
                $("#formError").text('Password does not meet the password requirements');
            }
        }).fail(function (response) {
            $("#formError").text('Internal server error');
        });
        return false;
    }

    function _initLinkAccountsDialog($container) {
        $.ajax('profile/link')
            .success(function (data) {
                $container.empty();
                $container.append(data);
                $('[id^=createdLinkedAccount]').hide();
                $('[id^=unLinkedAccount]').hide();

                var $modal = $container.find('#linkAccountsModal');
                var $saveButton = $container.find("#linkAccounts");
                var $cancelButton = $container.find("#cancelLinkAccounts");

                $('#linkAccountsForm input').on('keydown', function(event) {
                    if (event.keyCode === 13) {
                        _linkAccounts($container, $modal);
                        event.preventDefault();
                        return false;
                    }
                });

                // ============      Save button =========================
                $saveButton.on('click', function () {
                    _linkAccounts($container, $modal);
                });

                $cancelButton.on('click', function () {
                    return false;
                });

                var $employeeListContainer = $container.find("#employeeListContainer");
                $employeeListContainer.empty();

                var $linkedEmployeesGrid = $('[id^=linkedEmployeeListMain]').clone();
                $linkedEmployeesGrid.attr('id', 'linkedEmployeeList-modal');

                $linkedEmployeesGrid.removeClass('hidden');
                $employeeListContainer.append($linkedEmployeesGrid);

                _addLinkFormValidation();

                $modal.modal('show');
                $modal.on('hidden.bs.modal', function () {
                    $(this).remove();
                });

            })
            .fail(function (data, data2) {
                console.log(data, data2);
                alert('Internal server error. Please contact administrator.');
            });

        return false;
    }

    function _initLinkNewAccountDialog($container) {
        $.ajax('profile/linkcreated')
            .success(function (data) {
                $container.empty();
                $container.append(data);
                $('[id^=createdLinkedAccount]').hide();
                $('[id^=unLinkedAccount]').hide();

                var $modal = $container.find('#linkAccountsModal');
                var $saveButton = $container.find("#linkCreateAccounts");
                var $cancelButton = $container.find("#cancelLinkAccounts");

                $('#linkAccountsForm input').on('keydown', function(event) {
                    if (event.keyCode === 13) {
                        _linkNewAccount($container, $modal);
                        event.preventDefault();
                        return false;
                    }
                });

                var $passwordPopoverContent = $('#passwordHelpTemplate').clone();
                $passwordPopoverContent.removeClass('hidden');
                $("#passwordHelp").popover({
                    html: true,
                    content: $passwordPopoverContent[0],
                    trigger: 'hover',
                    placement: function () {
                        return 'bottom';
                    }
                });

                // ============      Save button =========================
                $saveButton.on('click', function () {
                    _linkNewAccount($container, $modal);
                });

                $cancelButton.on('click', function () {
                    return false;
                });

                var $employeeListContainer = $container.find("#employeeListContainer");
                $employeeListContainer.empty();

                var $linkedEmployeesGrid = $('[id^=linkedEmployeeListMain]').clone();
                $linkedEmployeesGrid.attr('id', 'linkedEmployeeList-modal');

                $linkedEmployeesGrid.removeClass('hidden');
                $employeeListContainer.append($linkedEmployeesGrid);

                _addPasswordFormValidation();

                $modal.modal('show');
                $modal.on('hidden.bs.modal', function () {
                    $(this).remove();
                });

            })
            .fail(function (data, data2) {
                console.log(data, data2);
                alert('Internal server error. Please contact administrator.');
            });

        return false;
    }

    function _initLinkedEmployeeList() {
        _getFragment().widgets.linkedEmployeeList = _grid({
            tableId: "linkedEmployeeList",
            totalDisplayRows: 100,
            paginate: false,
            sort: false,
            callbacks: {
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
        _getFragment().widgets.linkedEmployeeList.api().ajax.reload();
    }

    function _loadLinkedEmployeesDetails($container) {
        $.ajax({
            type: 'GET',
            async: false,
            url: 'profile/linkeddetails',
            success: function (data) {
                $container.empty();
                $container.append(data);
                _initLinkedAccountsMasonry();
            },
            fail: function (data, data2) {
                console.log(data, data2);
                alert('Internal server error. Please contact administrator.');
            }
        });
    }

    function _loadCommonProfileInfo($container) {
        $.ajax({
            type: 'GET',
            async: false,
            url: 'profile/common-profile-info',
            success: function (data) {
                $find('#contactEmail').empty();
                $find('#contactEmail').append(data.email);
                $find('#contactSecureEmail').empty();
                $find('#contactSecureEmail').append(data.secureMessaging);
                $find('#contactPhone').empty();
                $find('#contactPhone').append(data.phone);
                $find('#contactFax').empty();
                $find('#contactFax').append(data.fax);
                $find('#contactAddress').empty();
                $find('#contactAddress').append(data.address.displayAddress);
                $find('#contactRole').empty();
                $find('#contactRole').append(data.role.label);
                $find('#contactCompanyId').empty();
                $find('#contactCompanyId').append(data.companyId);
                $find('#contactOrganization').empty();
                $find('#contactOrganization').append(data.organization.label);
                $find('#contactCommunity').empty();
                $find('#contactCommunity').append(data.communityName);
            },
            error: function (error) {
                mdlUtils.onAjaxError(error, function () {
                    alert(error.responseText);
                });
            },
            fail: function (data, data2) {
                console.log(data, data2);
                alert('Internal server error. Please contact administrator.');
            }
        });
    }

    function _unlinkAccount(id) {
        $.ajax({
            type: 'DELETE',
            url: 'profile/unlink/' + id,
            beforeSend: function(xhr){
                mdlUtils.csrf(xhr);
            },
            success: function (data) {
                $('[id^=createdLinkedAccount]').hide();
                _loadLinkedEmployeesDetails($find('#linkedEmployeesDetails'));
                $('[id^=unLinkedAccount]').show();
                _getFragment().widgets.linkedEmployeeList.api().ajax.reload();
            },
            error: function(error, ajaxOptions, thrownError) {
                mdlUtils.onAjaxError(error, function (data) {
                    console.log(data);
                    alert('Internal server error. Please contact administrator.');
                });
            }
        });
    }

    function _addPasswordFormValidation() {
        return $("#linkAccountsForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    password: {required: true},
                    confirmPassword: {required: true, equalTo: '#password'}

                },
                messages: {
                    password: {
                        required: getErrorMessage("field.empty")
                    },
                    confirmPassword: {
                        required: getErrorMessage("field.empty")
                    }
                }
            })
        );
    }
    function _addLinkFormValidation() {
        return $("#linkAccountsForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    company: {required: true},
                    password: {required: true},
                    username: {required: true}
                },
                messages: {
                    company: {
                        required: getErrorMessage("field.empty")
                    },
                    username: {
                        required: getErrorMessage("field.empty")
                    },
                    password: {
                        required: getErrorMessage("field.empty")
                    }
                }
            })
        );
    }
    function _addCreateContactValidation($container) {
        return $container.find("#contactForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    //
                    'email': {required: true, maxlength: 255, email: true},
                    'firstName': {required: true, minlength: 2, maxlength: 128},
                    'lastName': {required: true, minlength: 2, maxlength: 128},
                    'role.id': {required: true},
                    'address.street': {required: true, minlength: 2, maxlength: 255},
                    'address.city': {required: true, minlength: 2, maxlength: 128},
                    'address.state.id': {required: true},
                    'address.zip': {required: true, positiveInteger: true, lengthEqual: 5},
                    'phone': {required: true, phone:true},
                    'fax': {phone:true}
                },
                messages: {
                    'firstName': {
                        required: getErrorMessage("field.empty")
                    },
                    'lastName': {
                        required: getErrorMessage("field.empty")
                    },
                    phone: {
                        phone: getErrorMessage("field.phone.format")
                    },
                    fax: {
                        phone: getErrorMessage("field.phone.format")
                    },
                    state: {
                        stateUS: getErrorMessage("field.state")
                    }
                }
            })
        );
    }

    function _initCreateEditContactDialog(contactId, $button) {
        var $container = $find('#editContactContainer');
        if ($($button).hasClass("pending")) {
            return;
        }
        $($button).addClass("pending");
        $.ajax('care-coordination/contacts/contact/' + contactId)
            .success(function (data) {
                $container.empty();
                $container.append(data);
                var $modal = $container.find('#createContactModal');
                var headerTitle;

                _addCreateContactValidation($container);
                headerTitle = (contactId ? 'Edit' : 'Create') + ' Contact';
                var $saveButton = $container.find("#saveContact");

                // ============      Save button =========================
                $saveButton.html(contactId ? "SUBMIT" : "SEND INVITE");
                $saveButton.on('click', function () {

                    var $form = $container.find("#contactForm");
                    if (!$form.valid()) {
                        return false;
                    }

                    $.ajax({
                        url: 'care-coordination/contacts/contact/' + contactId,
                        type: 'POST',
                        data: $form.serialize(),
                        beforeSend: function(xhr){
                            mdlUtils.csrf(xhr);
                        },
                    }).success(function (data) {
                        var $currentContactId = $find('#currentUserId').val();
                        if ($currentContactId == contactId) {
                            _loadCommonProfileInfo($find('#profileCommonInfo'));
                        } else {
                            _loadLinkedEmployeesDetails($find('#linkedEmployeesDetails'));
                        }
                        _getFragment().widgets.linkedEmployeeList.api().ajax.reload();
                        $modal.modal('hide');
                        $('.modal-backdrop').remove();
                    }).fail(function (response) {
                        mdlUtils.onAjaxError(response, function () {
                            $("#formError").text(response.responseText);
                        });
                    });
                    return false;
                });

                var $orgSelector = $("#contact\\.organization");
                $orgSelector.change(function () {
                    $.ajax({
                        url: 'care-coordination/communities/byorg/' + $orgSelector.val(),
                        type: 'GET'
                    }).success(function (data) {
                        var $communitySelector = $("#contact\\.community");
                        $communitySelector.empty();
                        $.each(data.content, function (i, e) {
                            $communitySelector.append('<option value="' + e.id + '">' + e.name + '</option>');
                        });
                    }).fail(function (response) {
                        $("#formError").text(response.responseText);
                    });
                });

                $("#faxHelp").popover();
                $modal.find("#secureMessagingHelp1").popover();
                $modal.find("#secureMessagingHelp2").popover();


                $container.find("#contactHeader").html(headerTitle);

                $modal.find("#secureMessagingError1").popover({
                    html : true,
                    content : function() {
                        return $("#secureMessagingError1-content").html();
                    }
                });
                $modal.find("#secureMessagingError2").popover();
                $container.find('#createContactModal').modal({backdrop: 'static'});
                $container.find('#createContactModal').on('hidden.bs.modal', function () {
                    $(this).remove();
                });
                $($button).removeClass("pending");
            })
            .fail(function () {
                $($button).removeClass("pending");
                alert('Internal server error. Please contact administrator.');
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
                    var fragment = _getFragment();

                    fragment.widgets = {};
                    _initLinkedEmployeeList();
                    this.setEvents();
                    fragment.inited = true;
                    this.render();
                    this.show();
                    // masonry can calculate block height properly only after it has been already rendered
                    _loadLinkedEmployeesDetails($find('#linkedEmployeesDetails'));
                    this.loaded();
                }
            });
        },

        loaded: function() {
            $('.baseHeader a.ldr-head-lnk.active').each(function(){
                $(this).removeClass('active');
                $(this).removeClass('bottom');
                $('[id^=createdLinkedAccount]').hide();
                $('[id^=unLinkedAccount]').hide();
            });
            var newUserToLink = $('[id^=newUserToLink]').val();
            if (newUserToLink=='true') {
                _initLinkNewAccountDialog($find('#linkAccountsContainer'));
            }
        },

        update: function (url) {
            if (url) {
                cFragmentUrl = url;
            }
            this.loaded();
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
                }
            })
        },

        render: function () {
            var $moduleContent = _getFragment().$html;
            _getFragmentHolder().empty();
            _getFragmentHolder().append($moduleContent);
        },

        renderHolder: function () {
            if (document.getElementById(_getFragmentHolderId()))
                return;

            var $holder = $('<div/>');
            $holder.attr('id', _getFragmentHolderId());
            $holder.hide();

            $('#content').append($holder);
        },

        hide: function () {
            _getFragmentHolder().hide();
            $('#content').addClass('loading');
        },

        show: function () {
            _getFragmentHolder().show();
            $('#content').removeClass('loading');
        },

        setEvents: function () {
            var $currentContactId = $find('#currentUserId').val();
            $find("#linkAccounts").on('click', function () {
                _initLinkAccountsDialog($find('#linkAccountsContainer'));
            });
            $find("#editProfile").on('click', function () {
                _initCreateEditContactDialog($currentContactId,$(this));
            });
            mdlUtils.find(_getFragment().$html, '', ".backToPrevious").on('click', function () {
                parent.history.back();
                return false;
            });
            return false;
        },

        reloadNeeded: function () {
            return state.reloadNeeded;
        },

        unlinkAccount: function(id) {
            _unlinkAccount(id);
        },

        editLinkedContact: function(id, button) {
            _initCreateEditContactDialog(id,button);
        }
    };

})();

