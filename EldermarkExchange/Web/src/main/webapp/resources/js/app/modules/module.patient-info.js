ExchangeApp.modules.PatientInfo = (function () {

    var fragmentsMap = {};

    var cFragmentUrl = {
        template: 'patient-info/{residentId}'
    };

    var state = {
        reloadNeeded: false
    };

    var router = ExchangeApp.routers.ModuleRouter;

    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var patientTabUtils;

    var loader = ExchangeApp.loaders.FragmentLoader.init('patientInfo');

    var sectionInitMap = {
        allergies:                      _initAllergiesListWgt,
        medications:                    _initMedicationsListWgt,
        problems:                       _initProblemsListWgt,
        procedures:                     _initProceduresListWgt,
        results:                        _initResultsListWgt,
        encounters:                     _initEncountersListWgt,
        advanceDirectives:              _initAdvanceDirectivesListWgt,
        familyHistory:                  _initFamilyHistoryListWgt,
        vitalSigns:                     _initVitalSignsListWgt,
        immunizations:                  _initImmunizationsListWgt,
        payerProviders:                 _initPayerProvidersListWgt,
        medicalEquipment:               _initMedicalEquipmentListWgt,
        socialHistory:                  _initSocialHistoryListWgt,
        planOfCare:                     _initPlanOfCareListWgt,

        documents:                      _initDocumentListSection
    };

    var ccdModalFunctions = {
        problems:                       _initProblemsCcdModal
    };

    var idsMap = {
        problems: 'problemObservationId',
        planOfCare: 'planOfCareId'
    };

    function _prepare(){
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
        _ajaxify();
    }

    function _grid(options){
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }

    function _alert(options){
        var fragment = _getFragment();
        return wgtUtils.alert(fragment.$html, fragment.random, options);
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
            var anchor = $(this).attr('data-ajax-anchor');
            if(anchor) return;

            var template = $(this).attr('data-ajax-url-tmpl');
            var vars = $(this).attr('data-ajax-url-vars');
            var params = $(this).attr('data-ajax-url-params');

            var url = mdlUtils.getUrl(template, vars, params);

            if (template == "patient-search" && params && params.indexOf("firstName") !== -1) {
                router.reload(url);
            }
            else {
                router.route(url);
            }
            return false;
        });

        mdlUtils.find($fragment, random, '[data-ajax-anchor="true"]').on('click', function(){
            var $trg = $($(this).attr('href')).parent();
            if($trg.length){
                $('html, body').animate({ scrollTop: $trg.offset().top}, 200);
            }
            return false;
        });
    }

    function _randomize($trgFragment, trgRandom){
        var cFragment = _getFragment();
        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        $fragment = mdlUtils.randomize($fragment, random, false);
        return $fragment;
    }

    function _stringifyUrl(url, excludeOptions){
        return mdlUtils.stringifyUrl(url, excludeOptions);
    }

    function $find(selector) {      
        var fragment = _getFragment();
        return mdlUtils.find(fragment.$html, fragment.random, selector);
    }

    function _cleanId(id){
        var fragment = _getFragment();
        if(id) return id.replace(fragment.random, '');
        return id;
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

    function _initCcdHeaderMasonry(){
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

    function _updateDocListBadgeValue(addend) {
        var $badge = $find('#documentsTab .badge');
        var newValue = parseInt($badge.find('.badgeValue').html()) + addend;
        if (newValue > 0) {
            $badge.find('.badgeValue').html(newValue);
        } else {
            $badge.find('.badgeValue').html(0);
            $badge.addClass('hidden');
        }
    }


    function showDataSourceDetailsPopup(element, orgName, orgOid, commName, commOid) {
        if (!element) {
            return;
        }
        var $trPopoverContent = $find('#dataSourceDetailsTemplate').clone();

        var popoverId = 'dataSourceDetailsTemplate-' + element.id;
        $trPopoverContent.attr('id', popoverId);


        $trPopoverContent.removeClass('hidden');
        $(element).popover({
            html: true,
            content: $trPopoverContent[0],
            trigger: 'hover',
            placement: function () {
                return 'bottom';
            }
        });
        var data = {
            'dataSourceName' : orgName,
            'dataSourceOID' : orgOid,
            'communityName' : commName,
            'communityOID' : commOid,
        };

        for (var key in data) {
            if (data[key]) {
                mdlUtils.find($trPopoverContent, _getFragment().random, '#'+key).text(data[key]);
            } else {
                mdlUtils.find($trPopoverContent, _getFragment().random, '#'+key+'Layout').hide();
            }
        }


    }

    function _initCcdSectionsTotal(){
        var sectionNames =
            ['allergies', 'medications', 'problems', 'procedures', 'results', 'encounters',
             'advanceDirectives', 'familyHistory', 'vitalSigns', 'immunizations', 'payerProviders',
             'medicalEquipment', 'socialHistory', 'planOfCare'];

        for (var i = 0; i < sectionNames.length; i++) {
            _initCcdSectionTotal(sectionNames[i]);
        }
    }

    function _initCcdSectionTotal(sectionName) {
        var residentId = cFragmentUrl.variables.residentId;
        var aggregated = $find('#aggregatedRecord').val();
        var hashKey = cFragmentUrl.params.hashKey;
        var $this = $(this)
        var url = $find('#patientInfoUrl').attr('href') + residentId +  '/ccd/' + sectionName + '/' + aggregated +  '/total?hashKey=' + hashKey;

        if (url) {
            var id = $this.attr('id');
            $.ajax({
                type: 'GET',
                contentType: 'json',
                url: url,
                success: function (totalCount) {
                    var isEmpty = (totalCount <= 0);

                    var $badge = $find('#' + sectionName + 'CollapsedPanel' + ' .badge');
                    $badge.find('.badgeValue').html(isEmpty ? 0 : totalCount);

                    $badge.toggleClass('hidden', isEmpty);
                    $find('#' + sectionName + 'CollapsedPanel').toggleClass('disabled', isEmpty);


                    if (data.viewable === true && !data.isFreeText === true) {
                        $row.on('click', function (e) {
                            initCcdModal.call(this, {
                                sectionName: id,
                                mode: 'view',
                                residentId: residentId,
                                hashKey: hashKey,
                                id: data[idsMap[id]]
                            });
                            if (e) {
                                e.preventDefault();
                            }
                        });
                    }
                },
                error: function () {
                    $find('#' + sectionName + 'CollapsedPanel' + ' .badge').addClass('hidden');
                }
            });

        }
    }

    function _initDocumentsTotal(){
        var residentId = cFragmentUrl.variables.residentId;
        var databaseId = cFragmentUrl.params.databaseId;
        var aggregated = $find('#aggregatedRecord').val();
        var hashKey = cFragmentUrl.params.hashKey;

        var url = $find('#patientInfoUrl').attr('href') + residentId + '/documents/' + aggregated + '/total?hashKey=' + hashKey + '&databaseId=' + databaseId;

        $.ajax({
            type: 'GET',
            contentType: 'json',
            url: url,
            success: function (totalCount) {
                var isEmpty = (totalCount <= 0);
                _setDocListBadgeValue(isEmpty, totalCount);
            },
            error: function () {
                $find('#documentsTab .badge').addClass('hidden');
            }
        });
    }

    function _setDocListBadgeValue(isEmpty, totalCount) {
        var $badge = $find('#documentsTab .badge');
        $badge.find('.badgeValue').html(isEmpty ? 0 : totalCount);
        $badge.toggleClass('hidden', isEmpty);
    }

    function _initAllergiesListWgt() {
        _getFragment().widgets.allergiesList = _grid({
            tableId: 'allergiesList',
            colSettings: {
                reactions: {bSortable: false},
                status: {width: '100px'},
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="allergy-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            totalDisplayRows: 15,
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#allergy-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initMedicationsListWgt() {
        _getFragment().widgets.medicationsList = _grid({
            tableId: 'medicationsList',
            colSettings: {
                indications: {bSortable: false},
                startDate: {width: '70px'},
                endDate: {width: '70px'},
                status: {width: '100px'},
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="meds-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }

            },
            totalDisplayRows: 15,
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#meds-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initProblemsListWgt() {
        var sectionName='problems';
        var residentId = cFragmentUrl.variables.residentId;
        var hashKey = cFragmentUrl.params.hashKey;
        _getFragment().widgets.problemsList = _grid({
            tableId: sectionName + 'List',
            totalDisplayRows: 15,
            colSettings: {
                name: {
                    bSortable: true,
                    customRender: function (data, type, row) {
                        if (!data) return '';
                        if (row.viewable) {
                            return '<a href="#" id="problems-name-' + row.id + '" data-observation-id="' + row.problemObservationId + '">' + data + '</a>'
                        }
                        return data;
                    }
                },
                dataSource: {
                    //bSortable: false,
                    customRender: function (data, type, row) {
                        if (!data) return '';
                        if (row.manual === true) {
                            return '<span>Simply Connect HIE</span>';
                        }
                        return '<a href="#" id="problems-source-' + row.id + '" onclick="return false" class="dataSourceCol">' + data + '</a>';
                    }
                },
                supportingDocuments: {
                    bSortable: false,
                    customRender: function (data) {
                        if (!data)
                            return '';
                        return data.split('|').reduce(function (sum, docUrl, index) {
                            return sum + (index == 0 ? '' : ', ') + '<a href="' + docUrl + '">Advance Directive</a>';
                        }, '');
                    }
                },
                actions: {width: '100px', 'className': 'actionsColumn'}

            },
            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    showDataSourceDetailsPopup($row.find('#problems-source-' + data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);

                    var actionsColumn = 'td.actionsColumn';
                    var nameColumn = 'td:first-child';

                    $row.children(actionsColumn).empty();
                    var $tdRowActions = $find('#ccdRowActions').clone();

                    if (data.editable === true || data.deletable === true) {
                        var tdRowActionsId = 'ccdRowActions-' + index;
                        $tdRowActions.attr('id', tdRowActionsId);
                        $tdRowActions.removeClass('hidden');
                        $row.children(actionsColumn).append($tdRowActions);

                        if (data.editable === true) {
                            var $editBtn = $tdRowActions.find('.editCcd');
                            $editBtn.removeClass('hidden');
                            $editBtn.on('click', function () {
                                initCcdModal.call(this, {
                                    sectionName: sectionName,
                                    mode: 'edit',
                                    residentId: residentId,
                                    hashKey: hashKey,
                                    id: data.problemObservationId
                                });
                                return false;
                            })

                        }

                        if (data.deletable === true) {
                            var $deleteBtn = $tdRowActions.find('.deleteCcd');
                            $deleteBtn.removeClass('hidden');
                            $deleteBtn.on('click', function () {
                                bootbox.confirm({
                                    title: "Delete the record?",
                                    message: "The record will be deleted.",
                                    buttons: {
                                        cancel: {
                                            label: 'Cancel'
                                        },
                                        confirm: {
                                            label: 'Ok'
                                        }
                                    },
                                    callback: function (result) {
                                        if (result) {
                                            $.ajax({
                                                url: 'patient-info/' + residentId + '/ccd/' + sectionName + '/delete/' + data.problemObservationId + '?hashKey=' + hashKey,
                                                data: '',
                                                type: 'POST',
                                                enctype: 'multipart/form-data',
                                                processData: false,
                                                contentType: false,
                                                beforeSend: function (xhr) {
                                                    mdlUtils.csrf(xhr);
                                                },
                                                success: function (data) {
                                                    bootbox.alert('The record has been deleted');
                                                },
                                                error: function (error) {
                                                    mdlUtils.onAjaxError(error, function () {
                                                        alert(error.responseText);
                                                    });
                                                    // $th.removeClass("pending");
                                                }
                                            });
                                        }
                                    }
                                });
                                return false;
                            })
                        }
                    }

                    if (data.viewable === true) {
                        $row.on('click', function (e) {
                            initCcdModal.call(this, {
                                sectionName: sectionName,
                                mode: 'view',
                                residentId: residentId,
                                hashKey: hashKey,
                                id: data.problemObservationId
                            });
                            if (e) {
                                e.preventDefault();
                            }
                        });
                    }


                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });

        $find('.addCcdLink').on('click', function (e) {
            initCcdModal.call(this, {
                sectionName: sectionName,
                mode: 'add',
                residentId: residentId,
                hashKey: hashKey
            });
            if (e) {
                e.preventDefault();
            }
        })
    }

    function _initProceduresListWgt() {
        _getFragment().widgets.proceduresList = _grid({
            tableId: 'proceduresList',
            colSettings: {
                startDate: {width: '70px'},
                endDate: {width: '70px'},
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="proc-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            totalDisplayRows: 15,
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#proc-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function initCcdModal(options) {
        if (ccdModalFunctions[options.sectionName] === undefined) {
            return;
        }
        var $ccdContainer = $find('#ccdContainer');
        var $button = $(this);
        if ($button.hasClass("pending")) {
            return;
        }
        $button.addClass("pending");
        var endpoint = 'patient-info/' + options.residentId + '/ccd/' + options.sectionName + '/' + options.mode;
        if (options.mode === 'view' || options.mode === 'edit') {
            endpoint += '/' + options.id;
        }
        endpoint += '?hashKey=' + options.hashKey;
        $.ajax(endpoint)
            .success(function (data) {
                $ccdContainer.empty();
                $ccdContainer.append(data);

                ccdModalFunctions[options.sectionName]($ccdContainer, options);

                $button.removeClass("pending");
                $(window).resize(function () {
                    $ccdContainer.find('.modal-body').css({'max-height': $(window).height() - 2 * (30 + 80)});
                });
                $(window).trigger('resize');
                $ccdContainer.find('#' + options.sectionName + 'CcdModal').modal('show');
            })
            .fail(function (response) {
                $button.removeClass("pending");
                alert(response.responseText); // TODO
            });
        return false;
    }

    function _initProblemsCcdModal($container, options) {
        var $endDate = $container.find('#problem\\.endDate').datetimepicker({
            format: 'MM/DD/YYYY hh:mm A (Z)',
            maxDate: new Date()
        });

        $container.find('#problem\\.startDate').datetimepicker({
            format: 'MM/DD/YYYY hh:mm A (Z)',
            maxDate: new Date()
        });

        $container.find('#problem\\.onSetDate').datetimepicker({
            format: 'MM/DD/YYYY',
            maxDate: new Date()
        });

        $container.find('#problem\\.recordedDate').datetimepicker({
            defaultDate: new Date(),
            format: 'MM/DD/YYYY hh:mm A (Z)',
            maxDate: new Date()
        });

        var validator = _addProblemsValidation($container);

        var $statusDropdown = $container.find("#problem\\.status\\.id");
        $statusDropdown.on('change', function () {
            var $selectedOption = $(this).find(':selected');

            // 413322009 - 'Resolved' status code
            if ($selectedOption.attr('data-code') === "413322009") {
                $endDate.removeAttr('disabled');
            } else {
                $endDate.attr('disabled', 'disabled');
                $endDate.val('')
            }
        });


        var $primaryRadio = $container.find('[name="primary"]');
        var $primaryRadioNo = $primaryRadio.filter(function () {
            return this.value === 'false'
        });
        var $primaryRadioYes = $primaryRadio.filter(function () {
            return this.value === 'true'
        });

        var alreadyPrimary = $primaryRadioYes.is(':checked');

        if (!alreadyPrimary) {
            //todo click area
            $primaryRadioNo.on('click', function () {
                alreadyPrimary = false;
            });
            $primaryRadioYes.on('click', function () {
                if ($primaryRadioYes.hasClass('pending') || alreadyPrimary) {
                    return;
                }
                $primaryRadioYes.addClass('pending');
                $.ajax('patient-info/' + options.residentId + '/ccd/problems/primary?hashKey=' + options.hashKey)
                    .success(function (data) {
                        if (data !== -1) {
                            bootbox.confirm({
                                title: "Change primary diagnosis?",
                                message: "Primary diagnosis is already set. Do you want to change the selection?",
                                buttons: {
                                    cancel: {
                                        label: 'No'
                                    },
                                    confirm: {
                                        label: 'Yes'
                                    }
                                },
                                callback: function (result) {
                                    $primaryRadioNo.prop('checked', !result);
                                    $primaryRadioYes.prop('checked', result);
                                    $primaryRadioYes.removeClass('pending');
                                    alreadyPrimary = result;

                                    //bootbox acts like modal and bootstrap doesn't support multiple modals
                                    //opened at the same time and removes this class from body which breaks
                                    //scrolling inside modal
                                    $(this).on('hidden.bs.modal', function () {
                                        $('body').addClass('modal-open')
                                    });
                                }
                            });

                        } else {
                            $primaryRadioYes.prop('checked', true);
                            $primaryRadioYes.removeClass('pending');
                        }
                        $primaryRadio.blur();
                    });
                return false;
            });
        }

        var problemValueDropdownToggle = wgtUtils.lifeSearch($container, '', {
            select: '#problem\\.value\\.id',
            searchInput: '#problem\\.value\\.id_searchInput',
            notEmptyKeyUpCallBack: function (value) {
                _getFragment().widgets.problemValueDropdown.api().ajax.reload();
            },
            searchInputNotEmptyClickCallback: function (value) {
                _getFragment().widgets.problemValueDropdown.api().ajax.reload();
            }
        });
        _getFragment().widgets.problemValueDropdown = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "problemValueDropdown",
            totalDisplayRows: 7,
            selectable: {
                type: 'single'
            },
            searchFormId: 'problem\\.value\\.id_searchInput',

            language: {
                sZeroRecords: 'No data found. Please contact your Administrator to solve the issue.'
            },

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    $row.click(function () {
                        problemValueDropdownToggle.setSelectValue(data.id, data.displayName);
                        problemValueDropdownToggle.close();
                    })
                },
                "drawCallback": function (settings) {
                },
                errorCallback: function (error) {
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                },
                headerCallback: function (thead, data, start, end, display) {
                    //$(thead).hide();
                }
            }
        });

        _getFragment().widgets.diagnosisInformation = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "problemDiagnosisInformation",
            paginate: false,
            selectable: {
                type: 'single'
            },
            searchFormId: 'problem\\.value\\.id',

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                },
                "drawCallback": function (settings) {
                },
                errorCallback: function (error) {
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                },
                headerCallback: function (thead, data, start, end, display) {
                    // $(thead).hide();
                }
            }
        });

        var $problemName = $container.find("#problem\\.value\\.id");

        var $diagnosisInformationWrapper = $container.find('#diagnosisInformationWrapper');
        $diagnosisInformationWrapper.hide();

        $problemName.on('change', function () {
            if ($(this).val() === '' || $(this).val() === null || $(this).val() === undefined) {
                $diagnosisInformationWrapper.hide();
            } else {
                $diagnosisInformationWrapper.show();
                _getFragment().widgets.diagnosisInformation.api().ajax.reload();
            }
        });

        $container.find("#submitNoteBtn").on('click', function () {
            var $form = $container.find("#problemsCcdForm");
            if (!$form.valid()) {
                validator.focusInvalid();
                return false;
            }
            var $th = $(this);
            if (!$th.hasClass("pending")) {
                $th.addClass("pending");
                $.ajax({
                    url: $form.attr('action'),
                    data: new FormData($form[0]),
                    type: 'POST',
                    enctype: 'multipart/form-data',
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        mdlUtils.csrf(xhr);
                    },
                    success: function (data) {
                        $container.find("#problemsCcdModal").modal('hide');
                        _initCcdSectionTotal(options.sectionName);
                        _getFragment().widgets.problemsList.api().ajax.reload();
                        if (options.mode === 'add') {
                            _getFragment().widgets.patientEventList.api().ajax.reload();
                            bootbox.alert('Problem has been added');
                        } else if (options.mode === 'edit') {
                            bootbox.alert('Problem has been edited');
                        }
                    },
                    error: function (error) {
                        // $container.find("#problemsCcdModal").modal('hide');
                        mdlUtils.onAjaxError(error, function () {
                            alert(error.responseText);
                        });
                        $th.removeClass("pending");
                    }
                });
            }
            return false;
        });

        $container.find('.cancelBtn').on('click', function () {
            $container.find("#problemsCcdModal").modal('hide');
        });
    }

    function _addProblemsValidation($container) {
        return $container.find("#problemsCcdForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    'value.id': {
                        required: true
                    },
                    'value.id_searchInput': {
                        required: true
                    },
                    primary: {required: true},
                    'type.id': {required: true},
                    'status.id': {required: true},
                    endDate: {required: true},
                    recordedDate: {required: true},
                    comments: {maxlength: 20000}
                },
                messages: {
                    'value.id': {
                        required: getErrorMessage("field.empty")
                    },
                    'value.id_searchInput': {
                        required: getErrorMessage("field.empty")
                    },
                    primary: {
                        required: getErrorMessage("field.empty")
                    },
                    'type.id': {required: getErrorMessage("field.empty")},
                    'status.id': {required: getErrorMessage("field.empty")},
                    endDate: {required: getErrorMessage("field.empty")},
                    recordedDate: {required: getErrorMessage("field.empty")}
                }
            })
        )
    }

    function _initResultsListWgt() {
        _getFragment().widgets.resultsList = _grid({
            tableId: 'resultsList',
            totalDisplayRows: 15,
            colSettings: {
                date: {width: '80px'},
                type: {width: '100px'},
                statusCode: {width: '100px'},
                referenceRanges: {bSortable: false},
                value: {bSortable: false},
                interpretations: {bSortable: false},
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="results-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#results-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initEncountersListWgt() {
        _getFragment().widgets.encountersList = _grid({
            tableId: 'encountersList',
            totalDisplayRows: 15,
            colSettings: {
                date: {width: '80px'},
                providerCodes: {bSortable: false},
                serviceDeliveryLocations: {bSortable: false},
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="enc-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#enc-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initAdvanceDirectivesListWgt() {
        _getFragment().widgets.advanceDirectivesList = _grid({
            tableId: 'advanceDirectivesList',
            totalDisplayRows: 15,
            colSettings: {
                type: {width: '100px'},
                verification: {bSortable: false},
                supportingDocuments: {
                    bSortable: false,
                    customRender: function(data) {
                        if (!data)
                            return '';
                        return data.split('|').reduce(function(sum, docUrl, index) {
                            return sum + (index == 0 ? '' : ', ')+ '<a href="'+ docUrl + '">Advance Directive</a>';
                        }, '');
                    }
                },
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="adv-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#adv-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initFamilyHistoryListWgt() {
        _getFragment().widgets.advanceDirectivesList = _grid({
            tableId: 'familyHistoryList',
            colSettings: {
                ageAtOnset: {width: '100px'},
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="fam-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            totalDisplayRows: 15,
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#fam-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initVitalSignsListWgt() {
        _getFragment().widgets.vitalSignsList = _grid({
            tableId: 'vitalSignsList',
            colSettings: {
                date: {width: '80px'},
                type: {width: '300px'},
                value: {bSortable: false},
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="vital-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            totalDisplayRows: 15,
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#vital-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initImmunizationsListWgt() {
        _getFragment().widgets.immunizationsList = _grid({
            tableId: 'immunizationsList',
            colSettings: {
                startDate: {width: '80px'},
                endDate: {width: '80px'},
                status: {width: '100px'},
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="immun-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            totalDisplayRows: 15,
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#immun-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initPayerProvidersListWgt() {
        _getFragment().widgets.payerProvidersList = _grid({
            tableId: 'payerProvidersList',
            colSettings: {
                coverageDateStart: {width: '80px'},
                coverageDateEnd: {width: '80px'},
                insuranceType: {width: '150px'},
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="payer-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            totalDisplayRows: 15,
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#payer-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initMedicalEquipmentListWgt() {
        _getFragment().widgets.medicalEquipmentList = _grid({
            tableId: 'medicalEquipmentList',
            colSettings: {
                suppliedDate: {width: '100px'},
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="equip-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            totalDisplayRows: 15,
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#equip-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initSocialHistoryListWgt() {
        _getFragment().widgets.socialHistoryList = _grid( {
            tableId: 'socialHistoryList',
            totalDisplayRows: 15,
            colSettings: {
                dataSource: {
                    bSortable: false,
                    customRender: function(data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="social-source-'+row.id+'" onclick="return false">'+data+'</a>';
                    }
                }
            },
            callbacks: {
                rowCallback: function (row, data, index) {
                    showDataSourceDetailsPopup($(row).find('#social-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                }
            }
        });
    }

    function _initPlanOfCareListWgt() {
         _getFragment().widgets.planOfCareList = _grid( {
             tableId: 'planOfCareList',
             colSettings: {
                 plannedActivity: {
                     bSortable: true,
                     customRender: function (data, type, row) {
                         if (!data)
                             return '';
                         if (row.isFreeText) {
                             var id = "planOfCare";
                             return '<a href="narrative/ccd/'+ id + '/' + row[idsMap[id]] +'/' + data + '" id="planOfCare-name-free-text' + row.id + '" data-observation-id="' + row.planOfCareId + '" target="_blank" rel="nofollow noopener">' + data + '</a>'
                         }
                         return data;
                     }
                 },
                 plannedDate: {width: '100px'},
                 dataSource: {
                     bSortable: false,
                     customRender: function(data, type, row) {
                         if (!data) return '';
                         return '<a href="#" id="care-source-'+row.id+'" onclick="return false">'+data+'</a>';
                     }
                 }
             },
             totalDisplayRows: 15,
             callbacks: {
                 rowCallback: function (row, data, index) {
                     showDataSourceDetailsPopup($(row).find('#care-source-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                 }
             }
        });
    }

    function _initDocumentListSection(){
        var residentId = cFragmentUrl.variables.residentId;
        var databaseId = cFragmentUrl.params.databaseId;
        var hashKey = cFragmentUrl.params.hashKey;
        var aggregated = $find('#aggregatedRecord').val();
        var downloadDeleteBasicUrl = $find('#patientInfoUrl').attr('href') + residentId + '/documents/';
        patientTabUtils.initDocumentList(residentId, databaseId, hashKey, aggregated, downloadDeleteBasicUrl, _grid);
        patientTabUtils.setDocumentListPanelEvents(residentId, databaseId, hashKey, downloadDeleteBasicUrl, aggregated, _updateDocListBadgeValue, false, 'composeMsgBtn');
    }

    function _initPatientCcdDocumentsTabUtils() {
        patientTabUtils = ExchangeApp.utils.patientccd;
        patientTabUtils.init(_getFragment, _grid, _alert, cFragmentUrl, false, $find);
    }

    function _initPatientDetailsMasonryWgt() {
        $find('.participants, .authors, .dataEnterer, .custodian, .informants, .authenticators, .documentationOf').masonry({
            itemSelector: '.item',
            percentPosition: true,
            columnWidth: '.item'
        });
    }

    return {
        init: function (url) {
            cFragmentUrl = url;

            //$('#content').addClass('loading');

            this.renderHolder();

            this.loadFragment({
                onFragmentLoaded: function () {
                    _prepare();
                },
                onResourcesLoaded: function(){
                    var cFrg = _getFragment();

                    // initialize widgets
                    cFrg.widgets = {};

                    _initPatientCcdDocumentsTabUtils();
                    patientTabUtils.initCompanyName();
                    _initCcdSectionsTotal();
                    _initDocumentsTotal();

                    this.setEvents();

                    //$('#content').removeClass('loading');
                    this.render();
                    this.show();

                    // masonry can calculate block height properly only after it has been already rendered
                    _initCcdHeaderMasonry();

                    cFrg.inited = true;
                }
            });
        },

        update: function (url) {
            cFragmentUrl = url;
            _initCcdSectionsTotal();
            _initDocumentsTotal();
        },

        getFragment: function(url){
            return fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function(url){
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
        },

        show: function () {
            _getFragmentHolder().show();
            $('#content').removeClass('loading')
        },

        setEvents: function () {
            $find('[data-toggle="ajaxtab"]').on('click', function (e) {
                var $this = $(this),
                    url = $this.attr('href'),
                    target = $this.attr('data-target');

                $find(_cleanId(target)).children().remove();
                $find(_cleanId(target)).addClass('tab-loading');

                var id = $find(_cleanId(target)).attr('id');

                $.get(url, function (data, code, xhr) {
                    var $fragment = $(data);
                    var cFragment = _getFragment();

                    _randomize($fragment, cFragment.random);

                    $find(_cleanId(target)).removeClass('tab-loading').html($fragment);

                    if (sectionInitMap[_cleanId(id)]) {
                        sectionInitMap[_cleanId(id)]();
                    }

                    _ajaxify($fragment, cFragment.random);
                });

                $this.tab('show');
                return false;
            });

            /*dynamic panels loading*/
            $find('.ccdDetailItem .collapse, .ccdHeaderDetails .collapse').on('show.bs.collapse', function () {
                var $this = $(this),
                    url = $this.attr('href'),
                    $content = $(this).find('.clp-pnl-content');

                var disabled = $find('[href*="' + $this.attr('id') + '"]').hasClass('disabled');
                if (disabled) return false;

                if (url) {
                    var id = $this.attr('id');
                    $.get(url, function (data, code, xhr) {
                        var $fragment = $(data);
                        var cFragment = _getFragment();

                        _randomize($fragment, cFragment.random);

                        $content.html($fragment);

                        if (sectionInitMap[_cleanId(id)]) {
                            sectionInitMap[_cleanId(id)]();
                        } else {
                            _initPatientDetailsMasonryWgt();
                        }

                        _ajaxify($fragment, cFragment.random);
                    });
                }
            });

            $find("#openAllBtn, #closeAllBtn").on('click', function () {
                $find("#openAllBtn, #closeAllBtn").toggleClass('hidden');
                var operation = $(this).attr('id').indexOf('openAllBtn') >= 0 ? 'show' : 'hide';
                $find('.ccdDetailItem .collapse').collapse(operation);
            });

            /*jump to panel*/
            $find(".jumpLnk").on('click', function () {
                var href = $(this).attr('href');
                $find(_cleanId(href)).collapse('show');
            });

            $find('#downloadPatientRecordBtn').on('click', function () {
                /*ajax options*/
                var residentId = cFragmentUrl.variables.residentId;
                var databaseId = cFragmentUrl.params.databaseId;
                var hashKey = cFragmentUrl.params.hashKey;
                var aggregated = $find('#aggregatedRecord').val();

                var ajaxDownloadOptions = {
                    data: {hashKey: hashKey, databaseId: databaseId, aggregated: aggregated},
                    failCallback: function (error) {
                        _alert({
                            action: 'add',
                            placeSelector: '.patientInfoBody',
                            message: error.responseText,
                            closable: {
                                timer: 45000,
                                btn: true
                            }
                        });
                        window.console.log(error);
                    }
                };

                $.fileDownload($find('#patientInfoUrl').attr('href') + residentId + '/documents/ccd/download', ajaxDownloadOptions);
            });

            var residentId = cFragmentUrl.variables.residentId;
            var databaseId = cFragmentUrl.params.databaseId;
            var hashKey = cFragmentUrl.params.hashKey;
            var divPatientInfoSelector = '[id=' + $(_getFragmentHolder()[0]).attr("id") + ']';  // == "[id=resident-info_" + residentId + "]"
            var basicUrl = $find('#patientInfoUrl').attr('href') + residentId + '/documents/';
            patientTabUtils.initUploadForm(residentId, databaseId, hashKey, divPatientInfoSelector, basicUrl, _updateDocListBadgeValue);

            $find("[data-section-name]").on('click', function (e) {
                initCcdModal.call(this, {
                    sectionName: $(this).attr('data-section-name'),
                    mode: 'add',
                    residentId: residentId,
                    hashKey: hashKey
                });
                if (e) {
                    e.preventDefault();
                }
            });

        },

        reloadNeeded: function() {
            return state.reloadNeeded;
        },
        showDataSourceDetailsPopup: function(element) {
            return showDataSourceDetailsPopup(element);
        }
    };
})();