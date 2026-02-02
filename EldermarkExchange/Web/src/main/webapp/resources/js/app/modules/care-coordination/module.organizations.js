ExchangeApp.modules.CareCoordinationOrganizations = (function () {

    // history of loaded segments
    var fragmentsMap = {};
    // root url
    var cFragmentUrl = {
        template: 'care-coordination/templates/organizations'
    };

    var checkUniquenessUrl = 'care-coordination/organizations/isUnique';

    var holderContentId = "organizationsTabContent";
    // flag if true then reloaded every time
    var state = {
        reloadNeeded: false
    };
    // load root routers (map url to module)
    var router = ExchangeApp.routers.ModuleRouter;
    // Utility
    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('careCoordinationOrganizations');

    var ActionType = Object.freeze({'ADD' : 'create', 'EDIT' : 'edit'});

    var pModule;

    var planSelectedOptions = new Map();

    var marketplaceService = null;
    ServiceProvider
        .getService("MarketplaceService")
        .then(function(service) {
            marketplaceService = service;
        }).fail(function(error) {
            alert(error);
        });

    /*-------------utils----------------*/

    function _prepare() {
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
        _ajaxify();
    }

    function _wizard($container, options) {
        return wgtUtils.wizard($container, '', options);
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


    function _clearAlerts() {
        $find('.alert').remove();
    }

    function _get(id) {
        return $find('[id="' + id + _getFragment().random + '"]');
    }

    function _grid(options) {
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }


    function _addStyler() {
        $find('input').styler();
    }

    // =======================

    function _addCreateOrgValidation($container, orgId, validateHidden) {
        var orgForm = $container.find("#orgForm");
        return orgForm.validate(
            new ExchangeApp.utils.wgt.Validation({
                ignore: validateHidden ? [] : $.validator.defaults.ignore,
                scrollToFirstInvalid: true,
                container: $container.find('#createOrgModal'),
                rules: {
                    'name': {
                        required: true,
                        maxlength: 100,
                        minlength: 3,
                        remote: {
                            url: checkUniquenessUrl,
                            type: 'GET',
                            data: {name: orgForm.find("#name").val(), id: orgId}
                        }
                    },
                    'loginCompanyId': {
                        required: true,
                        maxlength: 10,
                        regexp: /^[^//]+$/,
                        remote: {
                            url: checkUniquenessUrl,
                            type: 'GET',
                            data: {loginCompanyId: orgForm.find("#loginCompanyId").val(), id: orgId}
                        }
                    },
                    'oid': {
                        required: true,
                        maxlength: 30,
                        remote: {
                            url: checkUniquenessUrl,
                            type: 'GET',
                            data: {oid: orgForm.find("#oid").val(), id: orgId}
                        }
                    },
                    'email': {required: true, maxlength: 100, email: true},
                    'phone': {required: true, maxlength: 16, phone: true},
                    'street': {
                        minlength: 2,
                        maxlength: 200,
                        requiredIf: {conditionFunction: isMarketplace.bind(orgForm, orgForm)}
                    },
                    'city': {
                        minlength: 2,
                        maxlength: 100,
                        requiredIf: {conditionFunction: isMarketplace.bind(orgForm, orgForm)}
                    },
                    'stateId': {
                        requiredIf: {conditionFunction: isMarketplace.bind(orgForm, orgForm)}
                    },
                    'postalCode': {
                        zipcodeUS: true,
                        requiredIf: {conditionFunction: isMarketplace.bind(orgForm, orgForm)}
                    },
                    'marketplace.servicesSummaryDescription': {
                        // language=JSRegexp
                        pattern: '[0-9a-zA-Z !"#$%&\'’()*+,\\s\\-./:;<=>?@\\\\[\\]^_`{|}~]*'
                    },
                    'marketplace.appointmentsEmail': {maxlength: 150, email: true},
                    'marketplace.appointmentsSecureEmail': {
                        maxlength: 150,
                        email: true,
                        requiredIf: {conditionFunction: appointmentsAllowed.bind(orgForm, orgForm)}
                    },
                    'logo': {fileIsPicture: true, filesize: 1048576, fileRatio: [1.5, 5]}
                },
                messages: {
                    'name': {
                        required: getErrorMessage("field.empty"),
                        remote: 'Name of Organization should be unique'
                    },
                    'loginCompanyId': {
                        required: getErrorMessage("field.empty"),
                        remote: 'Company ID should be unique',
                        regexp: 'Slash is forbidden'
                    },
                    'oid': {
                        required: getErrorMessage("field.empty"),
                        remote: 'Organization OID should be unique'
                    },
                    'street': {
                        requiredIfNot: 'All address fields should be filled, if at least one of the fields is not empty',
                        requiredIf: 'All address fields should be filled, if organization is discoverable in Marketplace'
                    },
                    'phone': {
                        phone: getErrorMessage("field.phone.format")
                    },
                    'city': {
                        requiredIfNot: 'All address fields should be filled, if at least one of the fields is not empty',
                        requiredIf: 'All address fields should be filled, if organization is discoverable in Marketplace'
                    },
                    'stateId': {
                        requiredIfNot: 'All address fields should be filled, if at least one of the fields is not empty',
                        requiredIf: 'All address fields should be filled, if organization is discoverable in Marketplace'
                    },
                    'postalCode': {
                        requiredIfNot: 'All address fields should be filled, if at least one of the fields is not empty',
                        requiredIf: 'All address fields should be filled, if organization is discoverable in Marketplace'
                    },
                    'marketplace.servicesSummaryDescription': {
                        pattern: 'Invalid format. Allowed: letters, numbers, space and special symbols'
                    },
                    'marketplace.appointmentsSecureEmail': {
                        requiredIf: $.validator.messages.required
                    },
                    'logo': {
                        fileIsPicture: 'File should be of picture format',
                        filesize: 'Maximum file size is 1 MB',
                        fileRatio: 'Image ratio should be between 1.5:1 and 5:1'
                    }
                }
            })
        );
    }

    function isMarketplace(orgForm) {
        return orgForm.find('#orgConfirmVisibilityInMarketplace').is(":checked");
    }

    function appointmentsAllowed(orgForm) {
        return orgForm.find('#allowAppointments').is(":checked");
    }

    function _getAllTextForSelectedOptions(combobox) {
    	var selectedOptionsLength=0;
		var nonSelectedOption=0;
		var option=$(combobox).find('option')[0];
		var previousParentOption=$(option).parent().prev();
        var $dropdown = $(combobox);
		$(previousParentOption).find('ul')
		.children().filter(function (index,li) {
			if(li.outerText =='None' || li.outerText =='All'|| li.outerText.length==0 ){
				nonSelectedOption++;
			}
			 if($(li).attr('class')==='selected')
				 selectedOptionsLength++;
			 })
			 var selectedOptionsLengthFromOptionTag =$(previousParentOption)
			 .find('ul')
			 .children().length - nonSelectedOption;

		 if(selectedOptionsLength === selectedOptionsLengthFromOptionTag){
			 var ed=$(previousParentOption)
			 .prev()
			 .find('span')[0];
			 $(ed).text('All');
			 $(combobox).val(0);
			 $(previousParentOption)
			 .find('ul')
			 .children().filter(function (index,li) {
				$(li).removeClass('selected');
				 if(li.outerText =='All'){
					$(li).addClass('selected');
				 }
			 })
		 }
    }

    function _getMultiSelection($combobox) {
        var selectedValues = $combobox.selectpicker('val') || [];
        // is 'All' selected?
        if (selectedValues.indexOf('0') > -1) {
            var options = $combobox.find('option');
            var allValues = $.map(options, function (option) {
                return option.value;
            }).filter(function (value) {
                return value && parseInt(value, 10) > 0;
            });
            return allValues;
        }
        // is 'None' selected?
        if (selectedValues.indexOf('-1') > -1) {
            return $.map(options, function (option) {
                if (option.innerText.toLowerCase() === 'none')
                    return option.value;
            }).filter(function (value) {
                return value && parseInt(value, 10) > 0;
            });
        }
        return selectedValues;
	}

	function initPrimaryFocusReferencedColumns($primaryFocus) {
    	$("#orgPrimaryFocus").on("changed.bs.select", function(e, clickedIndex, newValue, oldValue) {
          var selectedValues = $primaryFocus.selectpicker('val') || [];
            var options = $primaryFocus.find('option');

            marketplaceService.communityTypesIds(selectedValues)
                .then(function(data) {
                    var $communityTypeSelect = $('#orgCommunityType');
                    var previousSelected = _getMultiSelection($communityTypeSelect);
                    var option = "";
                	data.forEach(function(items) {
                		option +="<optgroup>";
                    	items.forEach(function(item) {
                    		option += "<option class='optionSpace' value='" + item.id + "'>" + item.label + "</option>";
                    	});
                    	option +="</optgroup>";
                    });
                    $communityTypeSelect.html(option);
                    $communityTypeSelect.val(previousSelected);
                    $communityTypeSelect.selectpicker('refresh');
                })
                .fail(function(e) {});

            marketplaceService.serviceTreatmentApproaches(selectedValues)
            .then(function(data) {
                var $servicesTreatmentApproachesSelect = $('#orgServicesTreatmentApproaches');
                var previousSelected = _getMultiSelection($servicesTreatmentApproachesSelect);
                var option = "";
            	data.forEach(function(items) {
            		option +="<optgroup>";
                	items.forEach(function(item) {
                		option += "<option class='optionSpace' value='" + item.id + "'>" + item.label + "</option>";
                	});
                	option +="</optgroup>";
                });
                $servicesTreatmentApproachesSelect.html(option);
                $servicesTreatmentApproachesSelect.val(previousSelected);
                $servicesTreatmentApproachesSelect.selectpicker('refresh');

            })
            .fail(function(e) {});
    	});
    }

    function getSelectedInNetworkInsurancePlanIds () {
        var map = {};
        $('#in-network-insurance-table').find('tr').each(function () {
            var $tr = $(this);

            if($tr.attr('role')) {
                var networkId = $tr.data('network-id');

                if (networkId) {
                    var $select = $tr.find('select.selected-plan');
                    map[networkId] = $select.size() ? _getMultiSelection($select) : [];
                }
            }
        });

        return map;
    }

    function updateMarketPlace(dataObj) {
        dataObj.marketplace = {};
        dataObj.marketplace.confirmVisibility = dataObj["marketplace.confirmVisibility"];
        dataObj.marketplace.servicesSummaryDescription = dataObj["marketplace.servicesSummaryDescription"];
        dataObj.marketplace.prerequisite = dataObj["marketplace.prerequisite"];
        dataObj.marketplace.exclusion = dataObj["marketplace.exclusion"];
        dataObj.marketplace.allowAppointments = dataObj["marketplace.allowAppointments"];
        dataObj.marketplace.appointmentsEmail = dataObj["marketplace.appointmentsEmail"];
        dataObj.marketplace.appointmentsSecureEmail = dataObj["marketplace.appointmentsSecureEmail"];
        dataObj.marketplace.allInsurancesAccepted = dataObj["marketplace.allInsurancesAccepted"];
        dataObj.marketplace.primaryFocusIds = _getMultiSelection($("#orgPrimaryFocus"));
        dataObj.marketplace.communityTypeIds = _getMultiSelection($("#orgCommunityType"));
        dataObj.marketplace.levelOfCareIds = _getMultiSelection($("#orgLevelsOfCare"));
        dataObj.marketplace.ageGroupIds = _getMultiSelection($("#orgAgeGroupsAccepted"));
        dataObj.marketplace.serviceTreatmentApproachIds = _getMultiSelection($("#orgServicesTreatmentApproaches"));
        dataObj.marketplace.emergencyServiceIds = _getMultiSelection($("#orgEmergencyServices"));
        dataObj.marketplace.languageServiceIds = _getMultiSelection($("#orgLanguageServices"));
        dataObj.marketplace.ancillaryServiceIds = _getMultiSelection($("#orgAncillaryServices"));

        dataObj.marketplace.selectedInNetworkInsurancePlanIds = getSelectedInNetworkInsurancePlanIds();

        delete dataObj["marketplace.confirmVisibility"];
        delete dataObj["marketplace.servicesSummaryDescription"];
        delete dataObj["marketplace.prerequisite"];
        delete dataObj["marketplace.exclusion"];
        delete dataObj["marketplace.allowAppointments"];
        delete dataObj["marketplace.appointmentsEmail"];
        delete dataObj["marketplace.appointmentsSecureEmail"];
        delete dataObj["marketplace.primaryFocusIds"];
        delete dataObj["marketplace.communityTypeIds"];
        delete dataObj["marketplace.levelOfCareIds"];
        delete dataObj["marketplace.ageGroupIds"];
        delete dataObj["marketplace.serviceTreatmentApproachIds"];
        delete dataObj["marketplace.emergencyServiceIds"];
        delete dataObj["marketplace.languageServiceIds"];
        delete dataObj["marketplace.ancillaryServiceIds"];
        delete dataObj["marketplace.selectedInNetworkInsuranceIds"];
        delete dataObj["marketplace.selectedInNetworkInsurancePlanIds"];
        delete dataObj["marketplace.acceptedInsurancePlanIds"];
        delete dataObj["marketplace.allInsurancesAccepted"];

        delete dataObj["_marketplace.confirmVisibility"];
        delete dataObj["_marketplace.allowAppointments"];
        delete dataObj["_marketplace.primaryFocusIds"];
        delete dataObj["_marketplace.communityTypeIds"];
        delete dataObj["_marketplace.levelOfCareIds"];
        delete dataObj["_marketplace.ageGroupIds"];
        delete dataObj["_marketplace.serviceTreatmentApproachIds"];
        delete dataObj["_marketplace.emergencyServiceIds"];
        delete dataObj["_marketplace.languageServiceIds"];
        delete dataObj["_marketplace.ancillaryServiceIds"];
        delete dataObj["_marketplace.selectedInNetworkInsuranceIds"];
        delete dataObj["_marketplace.selectedInNetworkInsurancePlanIds"];
        delete dataObj["_marketplace.acceptedInsurancePlanIds"];
        delete dataObj["_marketplace.allInsurancesAccepted"];

        return dataObj;
    }

    function saveOrUpdateOrganization($form, orgId, $modal, $button) {
        $button.addClass("pending");
        var url = 'care-coordination/organizations';

        var requestMethod = 'POST';
        if (orgId) {
            url += '/' + orgId;
            requestMethod = 'PUT';
        }

        $form.find('#orgCompanyCode').removeAttr('disabled');
        $form.find('#orgOid').removeAttr('disabled');

        var dataObj = $form.serializeJSON();

        var logoChanged = dataObj.logoChanged;
        var logoRemoved = dataObj.logoRemoved;
        delete dataObj['logoChanged'];
        delete dataObj['logoRemoved'];

        dataObj.affiliatedDetails = [];
        $(".affiliatedDetails").not('.hidden').each(function () {
            var detailsObj = {};
            detailsObj.communityIds = $(this).find("select.communitySelect").selectpicker('val');
            detailsObj.affOrgId = $(this).find("select.affOrgSelect").val();
            detailsObj.affCommunitiesIds = $(this).find("select.affCommunitySelect").selectpicker('val');
            dataObj.affiliatedDetails.push(detailsObj);
        });
        delete dataObj._affiliatedDetails;

        // jQUery serializeJSON plugin doesn't play well with Spring MVC Forms
        dataObj = updateMarketPlace(dataObj);

        var updateAndClose = function (orgId) {
            _getFragment().widgets.organizationList.api().ajax.reload();
            $modal.modal('hide');
            showOrganizationDetails(orgId);
            ExchangeApp.managers.EventManager.publish('org_list_changed');
        };

        delete dataObj._csrf;

        $.ajax({
            url: url,
            type: requestMethod,
            enctype: 'multipart/form-data',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(dataObj),
            beforeSend: function (xhr) {
                mdlUtils.csrf(xhr);
            }
        }).success(function (data) {
            ExchangeApp.modules.Header.addOptionToOrganizationChooserIfAbsent(data.id, data.name);
            //Update logo
            if (logoRemoved == true) {
                var logoRemoveUrl = 'care-coordination/organizations/' + data.id + '/logo';
                $.ajax({
                    url: logoRemoveUrl,
                    type: 'DELETE',
                    cache: false,
                    dataType: 'json',
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        mdlUtils.csrf(xhr);
                    }
                }).success(function (logoData) {
                    updateAndClose(data.id);
                }).fail(function (e) {
                    mdlUtils.onAjaxError(e, function () {
                        console.log(e);
                        alert('There were problems deleting logo');
                        updateAndClose(data.id);
                    });
                });
            } else if (logoChanged == true) {
                var logoUrl = 'care-coordination/organizations/' + data.id + '/logo';
                var file = $('#logoOrgInput')[0].files[0];
                var fileData = new FormData();
                fileData.append('logo', file);
                $.ajax({
                    url: logoUrl,
                    type: 'POST',
                    data: fileData,
                    cache: false,
                    dataType: 'json',
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        mdlUtils.csrf(xhr);
                    }
                }).success(function (logoData) {
                    updateAndClose(data.id);
                }).fail(function (e, r) {
                    mdlUtils.onAjaxError(e, function () {
                        console.log(e, r);
                        alert('There were problems updating logo');
                        updateAndClose(data.id);
                    });
                });
            } else {
                updateAndClose(data.id);
            }
        }).fail(function (response) {
            mdlUtils.onAjaxError(response, function () {
                alert(response.responseText);
                $button.removeClass("pending");
            });
        });
        return false;
    }

    function validateForm($form) {
        var hasError = false;
        $form.find(".affiliatedDetails").not('.hidden').each(function () {
            var $communitySelect = $(this).find("select.communitySelect");
            if ($communitySelect.selectpicker('val') == null) {
                $communitySelect.parent().parent().addClass("has-warning");
                hasError = true;
            }
            var $affOrgSelect = $(this).find("select.affOrgSelect");
            if ($affOrgSelect.selectpicker('val') === "") {
                $affOrgSelect.parent().parent().addClass("has-warning");
                hasError = true;
            }
            var $affCommunitySelect = $(this).find("select.affCommunitySelect");
            if ($affCommunitySelect.selectpicker('val') == null) {
                $affCommunitySelect.parent().parent().addClass("has-warning");
                hasError = true;
            }
            if (hasError) {
                return false;
            }
        });
        return $form.valid() && !hasError;
    }

    function _initPasswordSettingsDialog($container, orgId) {
        $.ajax({
            url: 'care-coordination/organizations/password-settings/' + orgId,
            beforeSend: function () {
                $('.ajaxLoader').removeClass('hidden');
            },
            complete: function () {
                $('.ajaxLoader').addClass('hidden');
            }
        })
            .success(function (data) {
                $container.empty();
                $container.append(data);
                _addPasswordSettingsValidation($container);

                var $modal = $container.find('#passwordSettingsModal');
                var $saveButton = $container.find("#savePasswordSettings");
                $saveButton.on('click', function () {
                    var $form = $container.find("#passwordSettingsForm");
                    if (!$form.valid()) {
                        return false;
                    }
                    if (!$(this).hasClass("pending")) {
                        updateOrganizationPasswordSettings($form, orgId, $modal, $(this));
                    }
                    return false;
                });

                $('[id^=passwordSettings] :checkbox').styler();
                $('[id^=passwordSettings] :checkbox').change(function () {
                    var inputId = $(this).attr('id') + 'Value';
                    $('#' + inputId).prop('readonly', !this.checked);
                });

                $container.find('#passwordSettingsModal').modal('show');
                $container.find('#passwordSettingsModal').on('hidden.bs.modal', function () {
                    $(this).remove();
                });
            });
    }

    function updateOrganizationPasswordSettings($form, orgId, $modal, $button) {
        $button.addClass("pending");
        $.ajax({
            url: 'care-coordination/organizations/password-settings/' + orgId,
            type: 'POST',
            data: $form.serialize(),
            beforeSend: function (xhr) {
                mdlUtils.csrf(xhr);
            }
        }).success(function (data) {
            $button.removeClass("pending");
            $modal.modal('hide');
        }).fail(function (response) {
            mdlUtils.onAjaxError(response, function () {
                alert(response.responseText);
                $button.removeClass("pending");
            });
        });
    }

    function _addPasswordSettingsValidation($container) {
        var orgForm = $container.find("#passwordSettingsForm");
        return orgForm.validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    'databasePasswordSettingsList[0].value': {positiveInteger: true, max: 999, required: true},
                    'databasePasswordSettingsList[1].value': {positiveInteger: true, max: 999, required: true},
                    'databasePasswordSettingsList[2].value': {
                        positiveInteger: true,
                        max: function () {
                            var maxValue = 99999;
                            if ($('#passwordSettings3').is(':checked') && $.isNumeric($('#passwordSettings3Value').val())) {
                                var lockValue = parseInt($('#passwordSettings3Value').val());
                                if (lockValue > 0) {
                                    maxValue = lockValue;
                                }
                            }
                            return maxValue;
                        },
                        required: true
                    },
                    'databasePasswordSettingsList[3].value': {
                        positiveInteger: true,
                        max: 99999,
                        required: true,
                        min: function () {
                            var minValue = 0;
                            if ($('#passwordSettings2').is(':checked') && $.isNumeric($('#passwordSettings2Value').val())) {
                                var resetValue = parseInt($('#passwordSettings2Value').val());
                                if (resetValue > 0) {
                                    minValue = resetValue;
                                }
                            }
                            return minValue;
                        }
                    },
                    'databasePasswordSettingsList[4].value': {
                        positiveInteger: true,
                        max: 20,
                        required: true,
                        min: function () {
                            var sumOfDependentFields = 0;
                            if ($('#passwordSettings5').is(':checked')) {
                                sumOfDependentFields += $.isNumeric($('#passwordSettings5Value').val()) ? parseInt($('#passwordSettings5Value').val()) : 0;
                            } else {
                                if ($('#passwordSettings6').is(':checked')) {
                                    sumOfDependentFields += $.isNumeric($('#passwordSettings6Value').val()) ? parseInt($('#passwordSettings6Value').val()) : 0;
                                }
                                if ($('#passwordSettings7').is(':checked')) {
                                    sumOfDependentFields += $.isNumeric($('#passwordSettings7Value').val()) ? parseInt($('#passwordSettings7Value').val()) : 0;
                                }
                            }
                            if ($('#passwordSettings8').is(':checked')) {
                                sumOfDependentFields += $.isNumeric($('#passwordSettings8Value').val()) ? parseInt($('#passwordSettings8Value').val()) : 0;
                            }
                            if ($('#passwordSettings9').is(':checked')) {
                                sumOfDependentFields += $.isNumeric($('#passwordSettings9Value').val()) ? parseInt($('#passwordSettings9Value').val()) : 0;
                            }
                            return sumOfDependentFields > 8 ? sumOfDependentFields : 8;
                        }
                    },
                    'databasePasswordSettingsList[5].value': {
                        positiveInteger: true,
                        max: 20,
                        required: true,
                        min: function () {
                            if ($('#passwordSettings5').is(':not(:checked)')) {
                                return 0;
                            }
                            var sumOfDependentFields = 0;
                            if ($('#passwordSettings6').is(':checked')) {
                                sumOfDependentFields += $.isNumeric($('#passwordSettings6Value').val()) ? parseInt($('#passwordSettings6Value').val()) : 0;
                            }
                            if ($('#passwordSettings7').is(':checked')) {
                                sumOfDependentFields += $.isNumeric($('#passwordSettings7Value').val()) ? parseInt($('#passwordSettings7Value').val()) : 0;
                            }
                            return sumOfDependentFields > 1 ? sumOfDependentFields : 1;
                        }
                    },
                    'databasePasswordSettingsList[6].value': {positiveInteger: true, max: 20, min: 1, required: true},
                    'databasePasswordSettingsList[7].value': {positiveInteger: true, max: 20, min: 1, required: true},
                    'databasePasswordSettingsList[8].value': {positiveInteger: true, max: 20, min: 1, required: true},
                    'databasePasswordSettingsList[9].value': {positiveInteger: true, max: 20, required: true},
                    'databasePasswordSettingsList[10].value': {positiveInteger: true, max: 24, required: true}
                },
                messages: {
                    'databasePasswordSettingsList[0].value': getErrorMessage("field.password.settings.999"),
                    'databasePasswordSettingsList[1].value': getErrorMessage("field.password.settings.999"),
                    'databasePasswordSettingsList[2].value': getErrorMessage("field.password.settings.99999.reset"),
                    'databasePasswordSettingsList[3].value': getErrorMessage("field.password.settings.99999.lock"),
                    'databasePasswordSettingsList[4].value': {
                        positiveInteger: getErrorMessage("field.password.settings.length"),
                        max: getErrorMessage("field.password.settings.length"),
                        min: getErrorMessage("field.password.settings.length")
                    },
                    'databasePasswordSettingsList[5].value': {
                        positiveInteger: getErrorMessage("field.password.settings.alphabetic"),
                        max: getErrorMessage("field.password.settings.alphabetic"),
                        min: getErrorMessage("field.password.settings.alphabetic")
                    },
                    'databasePasswordSettingsList[6].value': getErrorMessage("field.password.settings.20.1"),
                    'databasePasswordSettingsList[7].value': getErrorMessage("field.password.settings.20.1"),
                    'databasePasswordSettingsList[8].value': getErrorMessage("field.password.settings.20.1"),
                    'databasePasswordSettingsList[9].value': getErrorMessage("field.password.settings.20"),
                    'databasePasswordSettingsList[10].value': getErrorMessage("field.password.settings.24")
                }
            })
        );
    }

    function _initCreateEditOrgDialog($container, orgId, $button, actionType) {
        if ($button.hasClass("pending")) {
            return;
        }
        $button.addClass("pending");
        $.ajax({
            url: 'care-coordination/templates/organizations/' + orgId,
            beforeSend: function () {
                $('.ajaxLoader').removeClass('hidden');
            },
            complete: function () {
                $('.ajaxLoader').addClass('hidden');
            }
        })
            .success(function (data) {
                $container.empty();
                $container.append(data);
                var validator = _addCreateOrgValidation($container, orgId, true);
                _initWizardWgt($container, actionType);

                var $modal = $container.find('#createOrgModal');
                var $saveButton = $container.find("#createBtn,#createBtn2");
                var $allowAppointmentsCheckbox = $container.find("#allowAppointments");
                var $orgAppointmentsSecureEmailInput = $container.find("#orgAppointmentsSecureEmail");
                var $orgAppointmentsSecureEmailDiv = $container.find("#orgAppointmentsSecureEmailDiv");
                var $orgAppointmentsSecureEmailLabel = $container.find("label[for='orgAppointmentsSecureEmail']");
                var $orgConfirmVisibilityInMarketplaceCheckbox = $container.find('#orgConfirmVisibilityInMarketplace');
                var $addressPart = $modal.find(":input.addressPart");
                var $addressPartLabel = $modal.find("label.addressPart");

                // ============      Save button =========================
                $saveButton.on('click', function () {
                    var $form = $container.find("#orgForm");
                    if (!validateForm($form)) {
                        // jump to tab where validation failed
                        var isAffiliateRelationshipTabValid = $("#affiliateRelationshipTab").has(".has-warning").length <= -[0.0];
                        if (isAffiliateRelationshipTabValid) {
                            var isLegalInfoTabValid = $("#legalInfoTab").has(".has-warning").length <= -(0.0);
                            var isMarketplaceTabValid = $("#marketplaceTab").has(".has-warning").length <= -(0.0);
                            if (!isLegalInfoTabValid) {
                                $('#legalInfoHeadLnk').click(); // so dirty
                            } else if (!isMarketplaceTabValid) {
                                $('#marketplaceHeadLnk').click(); // so dirty
                            }
                        }
                        return false;
                    }
                    if (!$(this).hasClass("pending")) {
                        saveOrUpdateOrganization($form, orgId, $modal, $(this));
                    }
                    return false;
                });

                // ===========      Appointments > Allow appointments checkbox =========================
                var defaultSecureEmailLabelCaption = $orgAppointmentsSecureEmailLabel.text();
                $allowAppointmentsCheckbox.change(function () {
                    if (this.checked) {
                        $orgAppointmentsSecureEmailLabel.text(defaultSecureEmailLabelCaption + '*');
                    } else {
                        $orgAppointmentsSecureEmailLabel.text(defaultSecureEmailLabelCaption);
                        if ('' === $orgAppointmentsSecureEmailInput.val()) {
                            $orgAppointmentsSecureEmailInput.removeClass('error');
                            $orgAppointmentsSecureEmailDiv.removeClass('has-warning');
                        }
                    }
                });
                if ($allowAppointmentsCheckbox.is(":checked")) {
                    $orgAppointmentsSecureEmailLabel.text(defaultSecureEmailLabelCaption + '*');
                }

                // ============      'Confirm that organization will be visible in MarketPlace' checkbox =========================
                $orgConfirmVisibilityInMarketplaceCheckbox.change(function () {
                    if (this.checked) {
                        $addressPartLabel.find("span").show();
                    } else {
                        if ($addressPart.val() === "") {
                            $addressPartLabel.find("span").hide();
                            $addressPartLabel.parent().removeClass("has-warning");
                        }
                    }
                });
                if ($addressPart.val() !== "" || $orgConfirmVisibilityInMarketplaceCheckbox.is(":checked")) {
                    $addressPartLabel.find("span").show();
                }

                // ============      Legal Info > Address > any field =========================
                $addressPart.change(function () {
                    if ($(this).valid()) {
                        var needResetWarnings;
                        if ($(this).val() === "") {
                            // if this input is empty and other inputs are empty too -> reset validation warnings
                            needResetWarnings = $addressPart.not(this).val() === "";
                            var $form = $container.find("#orgForm");
                            if (needResetWarnings && !isMarketplace($form)) {
                                $addressPartLabel.find("span").hide();
                            }
                        } else {
                            // if this input is not empty and other inputs contain valid non-empty values -> reset validation warnings
                            var otherValues = $addressPart.not(this).map(function () {
                                return $(this).val();
                            });
                            needResetWarnings = ($.inArray("", otherValues) === -1) && $addressPart.not(this).valid();
                        }
                        if (needResetWarnings) {
                            $addressPartLabel.parent().removeClass("has-warning");
                        }
                    }
                    if ($(this).val().length > 1) {
                        $addressPartLabel.find("span").show();
                    }
                });

                $container.find('#createOrgModal').modal('show');
                $container.find('#createOrgModal').on('hidden.bs.modal', function () {
                    $(this).remove();
                });


                $("#oidHelp").popover();
                $("#companyIdHelp").popover();
                $("#companyLogoHelp").popover();
                $("#confirmCompanyHelp").popover();
                $("#affiliatedOrgHelp").popover();

                function tabContentPaddingSize($div) {
                    var tableHeight = 0;
                    var startPadding = 25;

                    var tabContent = $(".organizationWizardContent");
                    var table = $(".table-div");

                    if (parseInt($div.css('height'), 10) >= 220) {
                        if (table) {
                            tableHeight = table.height() != null ? table.height() : 0;
                        }
                        tabContent.css('paddingBottom', startPadding + parseInt($div.css('height'), 10) - tableHeight + "px");
                    } else {
                        tabContent.css('paddingBottom', startPadding + parseInt($div.css('height'), 10) + "px");
                    }
                    if ($div.css('display') === 'none') {
                        tabContent.css('paddingBottom', startPadding + "px");
                    }
                }

                function defaultSearchList(liList, upToDefaultList) {
                    var noMatchText = "No networks found";

                    Array.from(liList).forEach(function (li) {
                        var firstLiChildElem = $(li).children(':first').children(':first');

                        if($(li).text() === noMatchText && upToDefaultList) {
                            $(li).remove();
                        }

                        if (firstLiChildElem !== null)
                            firstLiChildElem.html($(li).text().fontcolor("#333333"));
                        $(li).show();
                    });
                }

                function hideSelectedOptions(optionList, $searchIcon) {
                    var $searchInput = $("#searchInput");

                    Array.from(optionList).forEach(function (option) {
                        //hide option 'I'll choose my insurance later'
                        if ($(option).hasClass("selected") || $(option).text() === 'I\'ll choose my insurance later') {
                            $(option).hide();

                            if ($searchInput.val()) {
                                $searchIcon.removeClass("search-icon").addClass("cancel-search-icon");
                            }
                        }
                    });
                    return optionList;
                }

                $("#searchInput").on({
                    "keyup": function () {
                        var input = $("#searchInput");
                        var filter = input.val().toLowerCase();
                        var $div = $("#selectSearch").prev();
                        var liList = $div.find("ul").find("li");
                        var $searchIcon = $("#searchIcon");

                        $div.css("display", "block");
                        $searchIcon.removeClass("cancel-search-icon").addClass("search-icon");

                        defaultSearchList(liList, false);

                        if (filter !== null && filter !== "") {
                            $searchIcon.removeClass("search-icon").addClass("cancel-search-icon");
                            Array.from(liList).forEach(function (li) {
                                var liChild = $(li).children(':first').children(':first');

                                if ($(li).text().toLowerCase().indexOf(filter) < 0) {
                                    $(li).hide();
                                }
                                if (liChild !== null) {
                                    var selection = liChild.text().toLowerCase().indexOf(filter);
                                    liChild.html($(li).text().substring(0, selection)
                                        + $(li).text().substring(selection, selection + filter.length).fontcolor("#454F81")
                                        + $(li).text().substring(selection + filter.length));
                                }
                            });

                            liList = hideSelectedOptions(liList, $searchIcon);

                            var ul = $div.find("ul");
                            var hiddenLi = ul.find("li").filter(":hidden");
                            var noMatch = document.createElement("li");
                            var noMatchText = "No networks found";

                            if (hiddenLi.length === liList.length) {
                                $(noMatch).text(noMatchText);

                                ul.append(noMatch);
                            } else if (ul.children(':last').text() === noMatchText) {
                                Array.from(ul.children()).map(function(li) {
                                    if ($(li).text() === 'No networks found') {
                                        $(li).remove();
                                    }
                                })
                            }
                        } else {
                            defaultSearchList(liList, true);
                            hideSelectedOptions(liList, $searchIcon);
                        }
                        tabContentPaddingSize($div);
                    },
                    "click": function () {
                        var div = $("#selectSearch").prev();
                        var liList = div.find("ul").find("li");

                        tabContentPaddingSize(div);
                        div.css("display", "block");

                        liList = hideSelectedOptions(liList);

                        var handler = function (event) {
                            if (!$(event.target).is('#searchInput')) {
                                onUpdateOrganization(event);
                                $('#createOrgModal').off("click", handler)
                            }
                        }

                        $('#createOrgModal').on("click", handler);
                    },
                    "blur": function () {
                        var $searchSelect = $("#selectSearch");
                        var optionsList = $searchSelect.prev().find("ul").find("li");
                        $searchSelect.change(function () {

                            $("#tableElement").css("display", "none");
                        });
                        hideSelectedOptions(optionsList, $('#searchIcon'));
                    }
                });

                $("#tableElement").on("click", ".remove-button", function () {
                    var networkName = $(this).closest('tr').data('network-name');
                    var search_component = $("#selectSearch");
                    var div = search_component.prev();
                    var liList = div.find("ul").find("li");
                    var $tableElement = $(this);

                    $.each(search_component.find(':selected'), function (index, elem) {
                        if ($(elem).text() === networkName) {
                            $(elem).prop("selected", false);
                        }
                    });

                    $.each(liList, function (index, li) {
                        if ($(li).text() === networkName) {
                            $(li).removeClass("selected");
                            $(li).show();
                        }
                    });

                    $(this).closest('tr').remove();

                    if ($tableElement.find('td').length === 0)
                        $tableElement.empty();
                });

                $("#searchIcon").on("click", function () {
                    var div = $("#selectSearch").prev();

                    if ($(this).hasClass("cancel-search-icon")) {
                        var liList = div.find("ul").find("li");

                        $("#searchInput").val('');

                        defaultSearchList(liList, true);
                        hideSelectedOptions(liList, $(this));

                        $(this).removeClass("cancel-search-icon").addClass("search-icon");
                    }
                    div.css("display", "block");
                    tabContentPaddingSize(div);

                    var handler = function (event) {
                        if (!$(event.target).is('#searchIcon')) {
                            onUpdateOrganization(event);
                            $('#createOrgModal').off("click", handler)
                        }
                    }

                    $('#createOrgModal').on("click", handler);
                });

                function loadNetworkTableList () {
                    var $networkTable = $("#tableElement");
                    var $selectSearch = $("#selectSearch");

                    var networkIds = _getMultiSelection($selectSearch);

                    if (networkIds.length !== 0) {
                        $.ajax({
                            type: "GET",
                            contentType: "application/json",
                            url: "care-coordination/templates/organizations/" + orgId + "/networks/" + networkIds,
                            dataType: "html"
                        }).success(function (data) {
                            $networkTable.empty();
                            $networkTable.append(data);

                            var $tableSelectPickers = $networkTable.find(".spicker");
                            $tableSelectPickers.selectpicker();
                            $tableSelectPickers.on('changed.bs.select', mdlUtils.noneOptionHandler);
                            $tableSelectPickers.on('changed.bs.select', mdlUtils.allOptionHandler);

                            $networkTable.find('select.spicker').each(function () {
                                var $select = $(this);

                                if (!$select.selectpicker('val')) {
                                    $select.selectpicker('val', 0);
                                }
                            });
                        });
                        $networkTable.css("display", "block");
                        $networkTable.find('.selectedPlan').find('.filter-option').addClass('network-table-filter-option');
                    } else {
                        $networkTable.css("display", "none");
                        $networkTable.empty();
                    }
                }

                loadNetworkTableList();

                function onUpdateOrganization (event) {
                    var $target = $(event.target);
                    var $table = $("#tableElement");
                    var $selectSearch = $("#selectSearch");
                    var $selectSearchDropdownMenu = $selectSearch.siblings('.dropdown-menu');
                    var $optionsList = $selectSearch.prev().find("ul").find("li");
                    var $searchIcon = $("#searchIcon");
                    var $searchInput = $("#searchInput");

                    $table.find('.selected-plan').find('.filter-option').addClass('network-table-filter-option');

                    if ($target.closest('button').parent().hasClass('selected-plan')) {
                        var $dropdown_menu = $target.closest('button').next();

                        $dropdown_menu.addClass('plan_select_button_dropdown');
                        $dropdown_menu.find('ul').find('li').find('a').addClass('ellipsis-text');

                        $table.find('.selectedPlan').find('.filter-option').addClass('network-table-filter-option');
                    }

                    else {
                        if (!$target.is($selectSearchDropdownMenu)
                            && !$target.is($searchInput)
                            && !$target.is($searchIcon)
                            && !$target.parent().hasClass('modal-table-row')) {

                            $searchIcon.removeClass("cancel-search-icon").addClass("search-icon");
                            $searchInput.val('');
                            $selectSearchDropdownMenu.css('display', 'none');

                            loadNetworkTableList()
                        }
                        tabContentPaddingSize($selectSearchDropdownMenu);
                        $table.find('.selectedPlan').find('.filter-option').addClass('network-table-filter-option');
                    }
                    $table.find('.selected-plan').find('.filter-option').addClass('network-table-filter-option');
                    hideSelectedOptions($optionsList, $searchIcon)
                }

                $find(":file").each(function (i, e) {
                    var btnText = $(e).attr('data-buttonText');
                    var enableIcon = $(e).attr('data-icon') == 'true';
                    var placeholder = $(e).attr('data-placeholder');
                    $(e).filestyle({icon: enableIcon, buttonText: btnText});
                    $(e).filestyle('placeholder', placeholder ? placeholder : 'File Not Chosen');

                    var cancelBtn = $('<a href="#" class="cancel-icon" id="removeLogo"></a>');

                    $('.input-group input').after(cancelBtn);

                    if (placeholder !== 'File Not Chosen' && placeholder !== "") {
                        $(".cancel-icon").css({
                            paddingTop: '3px',
                            opacity: 1,
                            zIndex: 3
                        });
                    }

                    $(e).on('change', function () {
                        $(".cancel-icon").css({
                            paddingTop: '3px',
                            opacity: 1,
                            zIndex: 3
                        });

                        $("#logoChanged").val(1);
                        $("#logoRemoved").val(0);

                    });
                    cancelBtn.on('click', function () {
                        $("#logoRemoved").val(1);

                        $(e).filestyle('clear');
                        $(e).filestyle('placeholder', 'File Not Chosen');
                        $(e).parents('.form-group').removeClass('has-warning');
                        $(e).siblings('.bootstrap-filestyle').tooltip("destroy");

                        $(this).css({
                            paddingTop: 0,
                            opacity: 0,
                            zIndex: 0
                        });

                        return false;
                    })
                });

                $('input:checkbox').styler();

                // multi selection comboboxes
                initPrimaryFocusReferencedColumns($("#orgPrimaryFocus"));
                var $selectPickers = $container.find("select.spicker");
                initPrimaryFocusReferencedColumns($("#orgPrimaryFocus"));
                $selectPickers.selectpicker();
                $selectPickers.on('changed.bs.select', mdlUtils.noneOptionHandler);
                $selectPickers.on('changed.bs.select', mdlUtils.allOptionHandler);


            	_getAllTextForSelectedOptions('#orgAgeGroupsAccepted');
            	_getAllTextForSelectedOptions('#orgAncillaryServices');
            	_getAllTextForSelectedOptions('#orgEmergencyServices');

                $.each($selectPickers.selectpicker(), function (index, select) {
                    $.each(select, function (optionIndex, option) {
                        if ($(option).val() != -1 && $(option).text().toLowerCase() === 'none') {
                            $(option).parent().prev().find('ul').children().eq(optionIndex).hide();
                            if ($(option).prop('selected')) {
                                $(option).parent().prev().find('ul').children()
                                    .filter(function (index, li) {
                                        return $(li).text().toLowerCase() === 'none' && $(li).css('display') !== 'none';
                                    }).addClass('selected');
                            }
                        } else {
                            $(option).parent().prev().find('li').filter(function (index, li) {
                                return $(li).text().toLowerCase() !== 'none' && $(li).css('display') !== 'none';
                            }).on('click', function (e) {
                                if ($(e.target).text().toLowerCase() !== 'none') {
                                    $(option).parent().find(':selected').filter(function(index, value) {
                                        return $(value).text().toLowerCase() === 'none'
                                    }).prop('selected', false);
                                    $(option).parent().prev().find('li').filter(function (index, li) {
                                        return $(li).text().toLowerCase() === 'none' && $(li).css('display') !== 'none';
                                    }).removeClass('selected');
                                    $(option).parent().prev().prev().find('.filter-option').text().toLowerCase().replace('none, ', '');
                                }
                            });
                        }
                    })
                });

                var $allAffOrgSelectors = $container.find("select.affOrgSelect");
                $allAffOrgSelectors.each(function () {
                    $allAffOrgSelectors.not(this).find("option[value='" + $(this).val() + "']").hide();
                });
                $allAffOrgSelectors.change(onChangeAffOrg);

                $container.find(".affiliatedDetails").not('.hidden').each(function () {
                    var $affiliatedDetails = $(this);
                    $(this).find(".closeAffiliatedOrgsButton").on('click', function () {
                        $affiliatedDetails.remove();
                    });
                });

                // ============      Affiliated Relationship > (+) button      =========================
                var $affiliatedOrgsButton = $container.find(".affiliatedOrgsButton");
                $affiliatedOrgsButton.on('click', function () {
                    var $affiliatedDetails = $container.find(".affiliatedDetails.hidden").clone();
                    $affiliatedDetails.removeClass("hidden");

                    var relationshipHeader = $affiliatedDetails.find('.relationship-header');
                    var relationshipText = relationshipHeader.html() + ' #' +
                        ($container.find(".affiliatedDetails").filter(':visible').size() + 1);
                    relationshipHeader.html(relationshipText);

                    $container.find("#affiliateRelationshipTab").append($affiliatedDetails);

                    var $selectPickers = $affiliatedDetails.find(".spicker-template");
                    $selectPickers.removeClass("spicker-template").addClass("spicker");
                    $selectPickers.selectpicker();
                    $selectPickers.on('changed.bs.select', mdlUtils.allOptionHandler);

                    $affiliatedDetails.find(".closeAffiliatedOrgsButton").on('click', function () {
                        $affiliatedDetails.remove();
                    });
                    var $orgSelector = $affiliatedDetails.find("select.affOrgSelect");
                    $orgSelector.change(onChangeAffOrg);
                });
                $button.removeClass("pending");
            })
            .fail(function (data, data2) {
                console.log(data, data2);
                $button.removeClass("pending");
                alert('Internal server error. Please contact administrator.');
            });

        return false;
    }

    function onChangeAffOrg() {
        var $affiliatedDetails = $(this).closest('.affiliatedDetails');
        var $allAffOrgSelectors = $("select.affOrgSelect");
        $allAffOrgSelectors.find("option").show();
        $allAffOrgSelectors.each(function () {
            $allAffOrgSelectors.not(this).find("option[value='" + $(this).val() + "']").hide();
        });
        $.ajax({
            url: 'care-coordination/communities/byorg/' + $(this).val(),
            type: 'GET'
        }).success(function (data) {
            var $communitySelector = $affiliatedDetails.find("select.affCommunitySelect");
            $communitySelector.find('option').remove();
            $communitySelector.append('<option value="0">All Communities</option>');
            $.each(data.content, function (i, e) {
                $communitySelector.append('<option value="' + e.id + '">' + e.name + '</option>');
            });
            $communitySelector.selectpicker('refresh');
        }).fail(function (response) {
            $("#formError").text(response.responseText);
        });
    }

    function showAffiliatedDetailsPopup(element, affiliatedOrgItems) {
        if (!element || !affiliatedOrgItems) {
            return;
        }
        var $trPopoverContent = $find('#affiliatedOrgsTemplate').clone();

        var popoverId = 'affiliatedTemplate-' + element.id;
        $trPopoverContent.attr('id', popoverId);

        var $popoverContent = $trPopoverContent.children().clone();
        $trPopoverContent.empty();

        $.each(affiliatedOrgItems, function (index, elem) {
            var itemContent = $popoverContent.clone();
            itemContent.find("label").text(elem.name);
            itemContent.find("a").click(function () {
                showOrganizationDetails(elem.id);
            });
            $trPopoverContent.append(itemContent);
        });

        $trPopoverContent.removeClass('hidden');
        $(element).popover({
            html: true,
            content: $trPopoverContent[0],
            trigger: 'hover',
            placement: function () {
                $(element).addClass('hasPopover');
                return 'bottom';
            }
        });

        $(element).on('show.bs.popover',
            function () {
                $find('.popover').trigger('mouseout');
            }
        ).on('shown.bs.popover',
            function () {
                $find('.popover').on('mouseout', function () {
                        if (!$(this).is(':hover')) {
                            $(element).removeClass('hasPopover').popover('hide');
                        }
                    }
                );
            }
        ).on('hide.bs.popover',
            function () {
                if ($(this).hasClass('hasPopover')) return false;
            }
        );

    }

    function showOrganizationDetails(organizationId) {
        var data;
        $.ajax("care-coordination/organizations/details/" + organizationId).success(data, function (data) {
            if ($find("#organizationContent").is(":hidden") && $find("#communityDetailsContent").is(":hidden")) {
                return; // belated response from server -> do nothing
            }

            $find("#organizationContent").hide();
            $find("#organizationDetailsContent").hide();

            $find("#organizationDetailsContent").empty();

            $find("#organizationDetailsContent").append(data);
            $find("#organizationDetailsContent").show();

            mdlUtils.find(_getFragment().$html, '', ".backToOrganizationsList").on('click', function () {
                $find("#organizationDetailsContent").hide();
                $find("#organizationContent").show();
            });

            var $passwordSettingsButton = mdlUtils.find(_getFragment().$html, '', '#passwordSettings');
            $passwordSettingsButton.on('click', function () {
                _initPasswordSettingsDialog($find('#passwordSettingsContainer'), organizationId);
            });

            var $editOrganizationButton = mdlUtils.find(_getFragment().$html, '', '#editOrganization');
            $editOrganizationButton.on('click', function () {
                _initCreateEditOrgDialog($find('#createOrganizationContainer'), organizationId, $(this), 'edit');
            });
            ExchangeApp.modules.Header.setCurrentOrg(organizationId);
            if (!ExchangeApp.modules.CareCoordinationCommunities) {
                var commFrUrl = {
                    template: 'care-coordination/templates/communities'
                };
                ExchangeApp.managers.ModuleManager.invoke({
                    module: {name: 'CareCoordinationCommunities', fragment: {url: commFrUrl}},
                    initParents: false,
                    forceReload: undefined,
                    asyncLoad: true
                });
            }

            $('a[id^=communityDetailsLink]').each(function (i) {
                var $item = $(this);
                var clickedCommunityId = $item.children('input').first().val();
                $item.click(function () {
                    var data;
                    ExchangeApp.modules.Header.setCurrentOrg(organizationId);
                    ExchangeApp.modules.Header.showCurrentOrg();
                    ExchangeApp.modules.CareCoordinationOrganizations.hide();
                    ExchangeApp.modules.CareCoordinationCommunities.show();
                    ExchangeApp.modules.CareCoordinationCommunities.showCommunityDetails(clickedCommunityId, data, false);
                });
            });
        });
    }

    function _initOrganizationList() {
        var $createOrgContainer = $find('#createOrganizationContainer');
        _getFragment().widgets.organizationList = _grid({
            tableId: "organizationList",
            searchFormId: "orgFilterFrom",
            totalDisplayRows: 25,
            colSettings: {
                'createdAutomatically': {
                    customRender: function (data) {
                        if (!data)
                            return '';
                        else {
                            return 'Yes';
                        }
                    }
                },
                'name': {
                    customRender: function (data, type, row) {
                        return "<a href='#' onclick='ExchangeApp.modules.CareCoordinationOrganizations.showOrgDetails(" + row.id + ");'>" + data + "</a>";
                    }
                },
                'communityCount': {
                    customRender: function (data, k, src) {
                        return "<a href='#' onclick='ExchangeApp.modules.Header.setCurrentOrg(" + src.id + "); ExchangeApp.modules.CareCoordination.routeToCommunityList(); return false;'>" + data + "</a>";
                        return data;
                    }
                },
                'affilatedCount': {
                    customRender: function (data, type, row) {
                        if (!data) data = '0';
                        return '<a href="#" id="affiliated-source-' + row.id + '" onclick="return false">' + data + '</a>';
                    }
                }
            },
            order: [[0, 'asc']],
            callbacks: {
                rowCallback: function (row, data, index) {
                    showAffiliatedDetailsPopup($(row).find('#affiliated-source-' + data.id)[0], data.affiliatedOrgItems);
                    var $row = $(row);
                },
                errorCallback: function (error) {
                    alert(error.responseText);
                    // TODO

                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
        _getFragment().widgets.organizationList.api().ajax.reload();
    }

    function updateNetworks(actionType) {
        var selectSearch = $('#selectSearch');
        if (actionType === ActionType.ADD) {
            planSelectedOptions = new Map();
            $.each(selectSearch.find('option'), function (index, option) {
                $(option).prop('selected', false);
            })
        }
    }

    /*-------------widgets----------------*/
    function _initWizardWgt($container, actionType) {
        var cFragment = _getFragment();
        var $organizationWizardContainer = $('#createOrgWzd');

        if (actionType === ActionType.ADD || (actionType === ActionType.EDIT && !_validateLegalInfoStep())) {
            $organizationWizardContainer.tabs({
                disabled: [1, 2]
            });
        }

        updateNetworks(actionType);

        cFragment.widgets.createOrgWizard = _wizard($container,
            {
                wizardId: 'createOrgWzd',
                tabClass: 'nav nav-tabs',
                onNext: function (tab, navigation, index) {
                    switch (index) {
                        case 1:
                            if (_validateLegalInfoStep()) {
                                $organizationWizardContainer.tabs({active: index});
                                $organizationWizardContainer.tabs('enable', index);
                            }
                            return _validateLegalInfoStep();
                        case 2:
                            if (_validateMarketplaceStep()) {
                                $organizationWizardContainer.tabs({active: index});
                                $organizationWizardContainer.tabs('enable', index);
                            }
                            return _validateMarketplaceStep();
                        default:
                            return false;
                    }
                },
                onFinish: function (tab, navigation, index) {
                    return true;
                },
                onPrevious: function (tab, navigation, index) {
                    $organizationWizardContainer.tabs({active: index});
                    return true;
                },
                onTabClick: function (tab, navigation, currentIndex, nextIndex) {
                    if (nextIndex <= currentIndex) {
                        return;
                    }
                    switch (nextIndex) {
                        case 1:
                            if (_validateLegalInfoStep()) {
                                $organizationWizardContainer.tabs({
                                    enable: nextIndex,
                                    active: nextIndex
                                });
                            } else {
                                $organizationWizardContainer.tabs({
                                    disabled: [1, 2],
                                    active: currentIndex
                                });
                            }
                            return _validateLegalInfoStep();
                        case 2:
                            if (_validateMarketplaceStep()) {
                                $organizationWizardContainer.tabs({
                                    enable: nextIndex,
                                    active: nextIndex
                                });
                            } else {
                                $organizationWizardContainer.tabs({
                                    disabled: [2],
                                    active: currentIndex
                                });
                            }
                            return _validateMarketplaceStep();
                        default:
                            return false;
                    }
                },
                onTabShow: function (tab, navigation, index) {
                    index = index < 0 ? 0 : index;
                    $organizationWizardContainer.tabs({active: index});
                    _updateBtns(index);
                    return true;
                },
                nextSelector: $find('.wzBtns .next'),
                previousSelector: $find('.wzBtns .previous'),
                finishSelector: $find('.wzBtns .finish')
            }
        );
        _setActiveStep(cFragment.widgets.createOrgWizard.dataset.activeStep);
        $find('.wzBtns .next').removeClass('disabled');
    }

    function _updateBtns(index) {
        var stepCss = ['.legalInfoStep', '.marketplaceStep', '.affiliateRelationshipStep'];

        _clearAlerts();
        $find('.wzBtns .btn').addClass('hidden');
        $find(stepCss[index]).removeClass('hidden');
    }

    function _setActiveStep(index) {
        $find('.nav>li').removeClass('active');
        if (index !== 3) $find('.nav>li:eq(' + index + ')').addClass('active');
        $find('.nav>li:lt(' + index + ')').addClass('wz-done');

        $find('.tab-pane').removeClass('active');
        $find('.tab-pane:eq(' + index + ')').addClass('active');

        if (index === 3) {
            $find('.tab-pane:eq(2)').addClass('active');
        }
        _updateBtns(index);
    }

    var _clearValidationErrors = function ($tabContainer) {
        var badInputs = $tabContainer.find(".has-warning");
        badInputs.removeClass("has-warning");
        badInputs.find(":input").tooltip('destroy')
            .removeAttr('data-validated');
    };

    function _validateLegalInfoStep() {
        // if inputs at this tab are not valid -> cancel transition to the next tab
        var isFormValid = $("#orgForm").valid();
        var isTabValid = $("#legalInfoTab").has(".has-warning").length <= 0;
        if (!isFormValid) {
            _clearValidationErrors($("#marketplaceTab"));
            _clearValidationErrors($("#affiliateRelationshipTab"));
        }
        return isFormValid ? true : isTabValid;
    }

    function _validateMarketplaceStep() {
        // if inputs at this tab are not valid -> cancel transition to the next tab
        var isFormValid = $("#orgForm").valid();
        var isTabValid = $("#marketplaceTab").has(".has-warning").length <= 0;
        if (!isFormValid) {
            _clearValidationErrors($("#affiliateRelationshipTab"));
        }
        return isFormValid ? true : isTabValid;
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

                    _initOrganizationList();
                    this.setEvents();

                    fragment.inited = true;

                    this.render();
                    this.show();
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

        hide: function () {
            _getFragmentHolder().hide();
            $('#content').addClass('loading');
        },

        show: function () {
            // activate tab
            if ($('#content').find(".tab-pane:visible").length == 0) {
                $.each($('#care-coordination').find('.nav li'), function () {
                    $(this).removeClass('active');
                });
                $('a[data-target^="#' + holderContentId + '"]').parent('li').addClass('active');
                // render content
                _getFragmentHolder().show();
            }
            $('#content').removeClass('loading');
        },

        setEvents: function () {
            $find("#createOrganization").on('click', function () {
                _initCreateEditOrgDialog($find('#createOrganizationContainer'), 0, $(this), 'create');
            });

            $find("#searchOrganization").on('click', function () {
                _getFragment().widgets.organizationList.api().ajax.reload();
                return false;
            });
            return false;
        },

        reloadNeeded: function () {
            return state.reloadNeeded;
        },

        getParentModule: function () {
            return pModule;
        },

        showOrgDetails: function (orgId) {
            showOrganizationDetails(orgId);
        }
    };
})();