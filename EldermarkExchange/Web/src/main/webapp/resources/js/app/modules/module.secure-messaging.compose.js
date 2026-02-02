ExchangeApp.modules.SecureMessagingComposeMsg = (function () {

    var fragmentsMap = {};

    var cFragmentUrl = {
        template: 'secure-messaging/compose'
    };

    var state = {
        reloadNeeded: false
    };

    var router = ExchangeApp.routers.ModuleRouter;

    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('secureMessagingComposeMsg');

    /*-------------utils----------------*/

    function _prepare(){
        var cFragment = _getFragment();
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

    function _grid(options){
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }

    function _alert(options){
        var fragment = _getFragment();
        return wgtUtils.alert(fragment.$html, fragment.random, options);
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

    function _updateAttachments() {
        var index = 0;
        $find('[name*="customDocumentIds"]').each(function (i, attachment) {
            $(attachment).attr('name', 'customDocumentIds[' + index++ + ']');
        });
        index = 0;
        $find('[name*="files"]').each(function (i, attachment) {
            $(attachment).attr('name', 'files[' + index + ']');
        });
    }

    function _removeAttachments() {
        $find('.lnkItem').remove();
    }

    function _removeAlerts() {
        $find('.alert').remove();
    }

    /**
     * @param fileName: CCD.XML, FACESHEET.PDF, documentName
     * @param type: files, reportTypes, customDocumentIds
     * @param docId: ccd, facesheet, id
     */
    function _renderAttachedFile(fileName, $input, type, docId) {
        var $img = $('<img class="lnk-img">');
        $img.attr('src', ExchangeApp.info.context + '/resources/images/sheet.png');

        var $imgLink = $('<a class="ldr-ui-label attachedFileLnk pointer-none">');
        $imgLink.append($img);
        $imgLink.append(fileName);

        var $lnkItem = $('<div class="lnkItem col-xs-12 col-sm-10 col-sm-push-2">');

        var $removeLink = $('<a>').addClass('removeLnk');
        var $removeIcon = $('<span>').addClass('glyphicon glyphicon-remove');
        $removeIcon.attr('aria-hidden', true);
        $removeLink.append($removeIcon);
        $removeLink.on('click', function (e) {
            $lnkItem.remove();
            _updateAttachments();
        });

        var total = $find('[name*="' + type +'"]').length;
        $input.attr('id', null);
        $input.attr('name', type + '[' + total + ']');
        $input.addClass('hidden');
        $input.attr('value', docId);

        $lnkItem.append($imgLink);
        $lnkItem.append($input);
        $lnkItem.append($removeLink);

        if (!$find('#attachments').length) {
            var $attachmentsDiv = $('<div/>');
            $attachmentsDiv.attr('id', 'attachments' + _getFragment().random);
            $attachmentsDiv.attr('class', 'form-group');
            $find('.msgDetails').append($attachmentsDiv);
        }

        $find('#attachments').append($lnkItem);
    }

    function _initChooseFileWgt() {
        var cFragment = _getFragment();
        cFragment.widgets.chosenFile = $find('#chosenFile');
        var text = cFragment.widgets.chosenFile.attr('data-buttonText');
        cFragment.widgets.chosenFile.filestyle({icon: false, buttonText: text});
    }

    function _initFormStyler(){
        $find("input[type='radio']").styler();
    }

    function _initAttachedResidentsFiles() {
        var reportTypes = cFragmentUrl.params.clinical;
        if (reportTypes) {
            var reportTypesArray = reportTypes.split(',');

            for (var i = 0; i < reportTypesArray.length; i++) {
                var reportType = reportTypesArray[i];
                var docName = (reportType == 'ccd') ? 'CCD.XML' : 'FACESHEET.PDF';

                var $input = $('<input type="text">');
                _renderAttachedFile(docName, $input, 'reportTypes', reportType);
            }
        }

        var customDocIds = cFragmentUrl.params.documentIds;
        if (customDocIds) {
            var customDocIdsArray = customDocIds.split(',');

            for (var i = 0; i < customDocIdsArray.length; i++) {
                (function (docId) {
                    var residentId = cFragmentUrl.variables.residentId;
                    var databaseId = cFragmentUrl.params.databaseId;
                    var hashKey = cFragmentUrl.params.hashKey;

                    var patientInfoPrefix = cFragmentUrl.template.replace('{residentId}/compose', '');

                    var url = patientInfoPrefix + residentId + '/documents/custom/' + docId + '/meta?hashKey=' + hashKey + '&databaseId=' + databaseId;

                    $.ajax({
                        type: 'GET',
                        contentType: 'json',
                        url: url,
                        success: function (docName) {
                            var $input = $('<input type="text">');
                            _renderAttachedFile(docName, $input, 'customDocumentIds', docId);
                        },
                        error: function (error) {
                            mdlUtils.onAjaxError(error, function(e){
                                _alert({
                                    action: 'add',
                                    placeSelector: '.msgDetails',
                                    message: e,
                                    closable: {
                                        timer: 45000,
                                        btn: true
                                    }
                                });
                            });
                        }
                    });
                }) (customDocIdsArray[i]);
            }
        }
    }

    function _initAddressBookWgt() {
        _getFragment().widgets.accountsDirectory = _grid({
            tableId: "accountsDirectory",
            searchFormId: 'addressBookFilter',
            totalDisplayRows: 15,
            colSettings: {
                name: {bSortable: false},
                email: {bSortable: false},
                speciality: { bSortable: false},
                stateLicences: {
                    bSortable: false,
                    customRender: function(data) {
                        if (!data)
                            return '';
                        return data.join(', ');
                    }
                },
                npiNumbers: {
                    bSortable: false,
                    customRender: function(data) {
                        if (!data)
                            return '';
                        return data.join(', ');
                    }
                },
                registrationType: {bSortable: false}
            },
            selectable: {
                type: 'multiple',
                boxForStore: {
                    dataColumn : "email"
                }
            },
            scroll: {
                scrollY: '45vh',
                scrollCollapse: true
            },
            language: {
                paginate: {
                    next: "<div/>",
                    previous: "<div/>"
                },
                sZeroRecords: 'No results found.',
                sProcessing: "<img class='addressBookAjaxLoader' src='" + ExchangeApp.info.context + "/resources/images/ajax-loader.gif'>",
                info: ""
            },
            callbacks: {
                errorCallback: function (error) {
                    mdlUtils.onAjaxError(error, function(e){
                        _alert({
                            action: 'add',
                            placeSelector: '.addressBookFilter',
                            message: e.responseText,
                            closable: {
                                timer: 45000,
                                btn: true
                            }
                        });
                    });
                },
                successCallback: function (response) {
                    if (response.totalElements == 0 && response.firstPage) {
                        _alert({
                            action: 'add',
                            type: 'alert alert-info',
                            placeSelector: '.addressBookFilter',
                            message: 'Note: Accounts added recently will not be available until the next business week.',
                        });
                    }
                    if (response.totalElements == 200 && response.firstPage) {
                        _alert({
                            action: 'add',
                            type: 'alert alert-info',
                            placeSelector: '.addressBookFilter',
                            message: 'Your search returned too many results, please narrow your search and try again.',
                            closable: {
                                timer: 45000,
                                btn: true
                            }
                        });
                    }
                },
                footerCallback: function( tfoot, data, start, end, display) {
                    $(tfoot).show();

                    var isHidden = data.length < 20;
                    if(isHidden){
                        $(tfoot).hide();
                    }
                }
            }
        });
    }

    function _initActivateSes() {
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

    /*-------------validation----------------*/
    function _setFileInputChangeTrigger(formId) {
        $find('#' + formId + ' input').on('change', function () {
            $(this).trigger('focusout');
        });
    }

    function _addUploadFileFormValidation() {
        _setFileInputChangeTrigger('chooseFileForm');

        return $find("#chooseFileForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    chosenFile: {required: true, filesize: 1000000000}
                },
                messages: {
                    chosenFile: {
                        required: getErrorMessage("field.empty"),
                        filesize: getErrorMessage('field.file.greather.1gb')
                    }
                }
            })
        );
    }

    function _addMsgDetailsFormValidation() {
        return $find("#msgDetailsForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    to: {required: true, emails: true}
                },
                messages: {
                    to: {
                        required: getErrorMessage("field.emails.empty"),
                        emails: getErrorMessage("field.emails.format")
                    }
                }
            })
        );
    }

    function _addAddressBookFormValidation() {
        var validationSettings = new ExchangeApp.utils.wgt.Validation({
            rules: {
                secureEmail: {
                    required: function(element) {
                        return $find("input[name='addressBookSource']:checked").val() === 'PUBLIC_DIRECTORY';
                    }
                },
                addressBookSource: {required: true}
            },
            messages: {
                secureEmail: {
                    required: getErrorMessage("field.empty")
                }
            }
        });
        $.extend(validationSettings, {ignore: ""});

        return $find("#addressBookFilter").validate(
            validationSettings
        );
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

                    _addMsgDetailsFormValidation();
                    _addUploadFileFormValidation();
                    _addAddressBookFormValidation();

                    // initialize widgets
                    cFrg.widgets = {};
                    _initChooseFileWgt();
                    _initFormStyler();
                    _initAddressBookWgt();
                    _initActivateSes();

                    if (cFragmentUrl.params) {
                        _initAttachedResidentsFiles();
                    }

                    this.setEvents();

                    this.render();
                    this.show();
                    cFrg.inited = true;
                }
            });
        },

        update: function (url) {
            cFragmentUrl = url;

            var $msgDetailsForm = $find('#msgDetailsForm')[0];
            if ($msgDetailsForm)
                $msgDetailsForm.reset();

            _removeAttachments();
            _removeAlerts();

            if (cFragmentUrl.params) {
                _initAttachedResidentsFiles();
            }
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
            $('#content').addClass('loading');
            var fragment = _getFragment();
            if (fragment) {
                var modals = $find('.modal');
                if(modals){
                    modals.modal('hide');
                }
            }
        },

        show: function () {
            _getFragmentHolder().show();
            $('#content').removeClass('loading');
        },

        getFragment: function(url){
            return  fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function(url){
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        setEvents: function () {
            // MAIN FORM

            $find('#msgDetailsForm').submit(function () {
                if (!$(this).valid()) return false;

                $('.composingMsg').hide();
                $('#content').addClass('loading');
                $find('#sendMsgBtn').prop('disabled', true);

                $.ajax({
                    url: $(this).attr('action'),
                    type: 'POST',
                    dataType: 'json',
                    data: new FormData(this),
                    enctype: 'multipart/form-data',
                    processData: false,
                    contentType: false,
                    beforeSend: function(xhr){
                        mdlUtils.csrf(xhr);
                    },
                    success: function (errorList) {
                        $find('#sendMsgBtn').removeProp('disabled');
                        $('#content').removeClass('loading');
                        $('.composingMsg').show();
                        var hasErrors = false;
                        $.each(errorList, function (i, error) {
                            hasErrors = true;
                            _alert({
                                action: 'add',
                                placeSelector: '.msgDetails',
                                message: error,
                                closable: {
                                    timer: 45000,
                                    btn: true
                                }
                            });
                        });
                        if (!hasErrors) {
                            ExchangeApp.managers.EventManager.publish('inbox_count_changed');

                            _alert({
                                action: 'add',
                                type: 'alert alert-info',
                                placeSelector: '.msgDetails',
                                message: 'Your message has been sent successfully.',
                                closable: {
                                    btn: true
                                }
                            });

                            window.setTimeout(function () {
                                var $stepBack = $find('#oneStepBackword');

                                var template = $stepBack.attr('data-ajax-url-tmpl');
                                var vars = $stepBack.attr('data-ajax-url-vars');
                                var params = $stepBack.attr('data-ajax-url-params');

                                if (template) {
                                    var url = mdlUtils.getUrl(template, vars, params);
                                    router.route(url);
                                } else {
                                    history.back();
                                }
                            }, 2000);
                        }
                    },
                    error: function (error) {
                        $('#content').removeClass('loading');
                        $('.composingMsg').show();
                        mdlUtils.onAjaxError(error, function(e){
                            $find('#sendMsgBtn').removeProp('disabled');
                            _alert({
                                action: 'add',
                                placeSelector: '.msgDetails',
                                message: e.responseText,
                                closable: {
                                    timer: 45000,
                                    btn: true
                                }
                            });
                        });
                    }
                });
                return false;
            });

            // ATTACH FILE event handlers

            $find('#chooseFileBtn').on('click', function (e) {
                if (!$find('#chooseFileForm').valid()) return false;

                // render a file name in compose message div
                var fileName = _getFragment().widgets.chosenFile.val().match(/[^\\\/]+\.\w+/)[0];

                var $input = _getFragment().widgets.chosenFile;
                _renderAttachedFile(fileName, $input, 'files', null);

                $find('#uploadFileModal').modal('hide');
            });

            $find('#uploadFileModal').on('hide.bs.modal', function() {
                // reset input of file type
                var cFragment = _getFragment();

                $find('.chosenFileBox').empty();

                var $oldFileInput = cFragment.widgets.chosenFile;

                var $newFileInput = $('<input type="file" name="chosenFile" class="filestyle form-control">');
                $newFileInput.attr('id', 'chosenFile'+cFragment.random);
                var buttonText = $oldFileInput.attr('data-buttonText');
                $newFileInput.attr('data-buttonText', buttonText);

                $find('.chosenFileBox').append($newFileInput);
                cFragment.widgets.chosenFile = $newFileInput;
                cFragment.widgets.chosenFile.filestyle({icon: false, buttonText: buttonText});

            });

            $find('#cancelBtn').on('click', function(){
                var $prevStep = $find('.crumb').eq(-2);

                var template = $prevStep.attr('data-ajax-url-tmpl');
                var vars = $prevStep.attr('data-ajax-url-vars');
                var params = $prevStep.attr('data-ajax-url-params');

                if (template) {
                    var url = mdlUtils.getUrl(template, vars, params);
                    router.route(url);
                } else {
                    history.back();
                }
            });

            // ADDRESS BOOK event handlers

            $find('#addressBookSearchBtn').on('click', function () {
                if ($find("#addressBookFilter").valid()) {
                    _getFragment().widgets.accountsDirectory.api().ajax.reload();
                } else {
                    _getFragment().widgets.accountsDirectory.api().clear().draw();
                }
                return false;
            });

            $find("input[name='addressBookSource']:radio").change(function () {
                if ($find("#addressBookFilter").valid()) {
                    _getFragment().widgets.accountsDirectory.api().ajax.reload();
                } else {
                    _getFragment().widgets.accountsDirectory.api().clear().draw();
                }
            });

            $find('#addressBookModal').on('show.bs.modal', function() {
                if ($find("#addressBookFilter").valid()) {
                    _getFragment().widgets.accountsDirectory.api().ajax.reload();
                } else {
                    _getFragment().widgets.accountsDirectory.api().clear().draw();
                }
            });

            $find('#addressBookSubmit').on('click', function () {
                var a = $find('#to').val();

                var b = $find('.chosed-row>span').map(function() {
                    return $(this).text();
                }).get();

                var bExcludedA = b.filter(function(item) {
                    return a.indexOf(item) == -1;
                });

                if (a.length && bExcludedA.length) a += '; ';

                var aUnionB = a + bExcludedA.join('; ');
                $find('#to').val(aUnionB);

                $find("#msgDetailsForm").valid();

                $find('#addressBookModal').modal('hide');
            });

            $find('#addressBookCancel').on('click', function(){
                $find('#addressBookModal').modal('hide');
            });

            $find('#addressBookModal').on('hide.bs.modal', function() {
                _getFragment().widgets.accountsDirectory.fnSettings().oInit.store.removeAllItems();
                var $filter = $find('#secureEmail');
                $filter.val($filter.attr('value'));
                $find('.chose-rows-pnl').empty();
            });

            ExchangeApp.managers.EventManager.subscribe('messaging_setup_changed', function() {
                state.reloadNeeded = true;
            });
        },

        reloadNeeded: function() {
            return state.reloadNeeded;
        }
    };
})();