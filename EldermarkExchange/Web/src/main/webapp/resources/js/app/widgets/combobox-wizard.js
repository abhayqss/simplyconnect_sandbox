$(document).on('ready', function(){

    function filterFirstLettersSecondTab($dropDown) {
        $dropDown.find("#menu2 .first-letter-title").each(function() {
            var $liFirstLetterTitle = $(this);
            var letter = $liFirstLetterTitle.data("first-letter");
            var countOptionsWithLetter = $dropDown.find(".plan-option:visible[data-first-letter='" + letter + "']").length;
            if (countOptionsWithLetter > 0) {
                $liFirstLetterTitle.show();
            } else {
                $liFirstLetterTitle.hide();
            }
        });
    }
    //highlight matches begin
    $.widget( "ui.autocomplete", $.ui.autocomplete, {
        options: {
            renderItem: null,
            renderMenu: null
        },

        _renderItem: function( ul, item ) {
            if ( $.isFunction( this.options.renderItem ) )
                return this.options.renderItem( ul, item );
            else
                return this._super( ul, item );
        },

        _renderMenu: function( ul, items ) {
            if ( $.isFunction( this.options.renderMenu ) ) {
                this.options.renderMenu( ul, items );
            }
            this._super( ul, items );
        }
    });

    var acMenu_Base = function( ul, items ) {
            var containerId = $("input.custom-combobox-input:focus").closest(".autocomplete-tabs").find(".combo-wizard-first-select").data("container-id");
            $( ul ).attr("data-container-id", containerId);
    },
        acItem_Highlight = function( ul, item ) {
            var $input = $("input.custom-combobox-input:focus");
            var currentInputValue = $input.val();
            var textBefore = item.label;
            var textAfter = textBefore.replace(new RegExp('(' + currentInputValue + ')', 'gi'), "<span class='highlight-green'>$1</span>");
            return $( '<li>' )
                .append( $( '<div>' ).html( textAfter ) )
                .appendTo( ul );
        };
    //highlight matches end


    $.widget( "custom.combobox", {
        _create: function() {
            this.wrapper = $( "<span>" )
                .addClass( "custom-combobox" )
                .insertAfter( this.element );

            ////
            this.element.hide();
            this._createAutocomplete();
        },

        _createAutocomplete: function() {
            var $select = $(this.element);
            var selected = this.element.children( ":selected" ),
                value = selected.val() ? selected.text() : "";
            this.input = $( "<input>" )
                .appendTo( this.wrapper )
                .val( value )
                .attr( "title", "" )
                .attr("placeholder", $select.attr("data-input-placeholder"))
                .addClass( "custom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left" )
                .addClass( $select.attr("data-input-cssClass") )
                .autocomplete({
                    delay: 0,
                    minLength: 0,
                    source: $.proxy( this, "_source" ),
                    renderItem: acItem_Highlight,
                    renderMenu: acMenu_Base
                });

            var $input = $(this.input);
            $input.attr("data-container-id", $input.closest(".autocomplete-tabs").attr("id") );
            this._on( this.input, {
                autocompleteselect: function( event, ui ) {

                    //
                    var $this = $(this.input);
                    var $selectOption = $(ui.item.option);
                    var selectedVal = $selectOption.val();
                    var $autoCompleteTabs = $this.closest(".autocomplete-tabs");

                    //define if it is plan or carrier and select that
                    var optionCategory = $selectOption.data("category");
                    if (optionCategory == 'carrier') {
                        var selectedCarrierId = $selectOption.data("carrier-id");
                        $autoCompleteTabs.find(".dropdown-menu #menu1 li[data-carrier-id = '" + selectedCarrierId + "' ]").click();
                        return;
                    } else if (optionCategory == 'auxiliary-option') {
                    } else {//insurance plan
                        var selectedPlanId = $selectOption.val();
                        $autoCompleteTabs.find(".dropdown-menu #menu2 li[data-plan-id = '" + selectedPlanId + "' ]").click();
                    }
                    ui.item.option.selected = true;
                    this._trigger( "select", event, {
                        item: ui.item.option
                    });
                    var $dropDownSelectedPlan  = $autoCompleteTabs.find(".dropdown-selected-plan");
                    $dropDownSelectedPlan.hide();
                    //clear selected plan
                },

                autocompletechange: "_removeIfInvalid",
                autocompletesearch: function(event, ui) {
                    var $input = $(this.input);
                    var $container = $input.closest(".autocomplete-tabs");
                    var $dropDownBody = $container.find(".dropdown-menu");
                    if ($container.data("selected-carrier-id")) {
                        var selectedCarrierId = $container.data("selected-carrier-id");
                        var currentInputValue = $input.val();
                        $dropDownBody.find(".planTabContainer li.plan-option").each(function() {
                            var $this = $(this);
                            if ($this.text().toLowerCase().includes(currentInputValue.toLowerCase())
                                && $this.data("carrier-id") == selectedCarrierId) {
                                $this.show();
                            } else {
                                $this.hide();
                            }
                        });
                        filterFirstLettersSecondTab($dropDownBody);
                        return;
                    }
                    //different behaviour depending on carrier selected or not

                    $dropDownBody.hide();
                }
            });
        },

        _source: function( request, response ) {
            var matcher = new RegExp( $.ui.autocomplete.escapeRegex(request.term), "i" );
            response( this.element.children( "option" ).map(function() {
                var $this = $( this );
                var $container = $this.closest(".autocomplete-tabs");
                if ($container.data("selected-carrier-id")) {
                    return;
                }
                var text = $( this ).text();
                if ( this.value && ( !request.term || matcher.test(text) ) )
                    return {
                        label: text,
                        value: text,
                        option: this
                    };
            }) );
        },

        _removeIfInvalid: function( event, ui ) {

            // Selected an item, nothing to do
            if ( ui.item ) {
                return;
            }

            // Search for a match (case-insensitive)
            var value = this.input.val(),
                valueLowerCase = value.toLowerCase(),
                valid = false;
            this.element.children( "option" ).each(function() {
                if ( $( this ).text().toLowerCase() === valueLowerCase ) {
                    this.selected = valid = true;
                    return false;
                }
            });

            // Found a match, nothing to do
            if ( valid ) {
                return;
            }

            // Remove invalid value
            this.element.val( "" );
            this.input.autocomplete( "instance" ).term = "";
        },

        _destroy: function() {
            this.wrapper.remove();
            this.element.show();
        }
    });


    $(".autocomplete-tabs").each(function () {
       var $dropdownContainer = $(this);
       var $mainSelect = $dropdownContainer.find("select.autocomplete-tabs-select");
       var $firstSelect = $dropdownContainer.find("select.combo-wizard-first-select");
       var $secondSelect = $dropdownContainer.find("select.combo-wizard-second-select");

        var $combobox = $mainSelect.combobox();
        var $autocompleteInput = $dropdownContainer.find(".custom-combobox-input");

        var $dropdownButton = $dropdownContainer.find("button.dropdown-toggle");
        var $dropdownContent = $dropdownContainer.find(".dropdown-menu");
        var $dropdownSelectedPlan = $dropdownContainer.find(".dropdown-selected-plan");

        var $selectedCarrierDivs = $dropdownContainer.find(".selected-carrier-container");
        $selectedCarrierDivs.hide();

        var $carrierTabHeader = $dropdownContainer.find("a[href='#menu1']");
        var $planTabHeader = $dropdownContainer.find("a[href='#menu2']");
        var $liPlan = $planTabHeader.parent();
        var $planTabContent = $dropdownContainer.find(".planTabContainer");

        var $selectedPlanSpans = $(".selected-carrier-id");
        // var $autocompleteInput = $dropdownContainer.find(".autocomplete-tabs-input");

        $dropdownButton.hide();
        $dropdownSelectedPlan.hide();

        $dropdownButton.on("click", function() {
            // console.log("$dropdownButton.click ");
            if ($dropdownContainer.data("selected-plan-option")) {
                dropDownSelectedPlan();
                $dropdownSelectedPlan.show();
                $autocompleteInput.val('');
            } else if ($dropdownContainer.data("selected-carrier-option")) {
                // console.log("$dropdownButton.click $dropdownContainer.data('selected-carrier-option')");
                dropDownSelectedCarrier();
                $dropdownSelectedPlan.show();
                $autocompleteInput.val('');
            }
            else {
                $dropdownContent.show();
                if ($dropdownContent.find(".selected-carrier-container:visible").length > 0) {
                    $autocompleteInput.trigger(jQuery.Event('keydown', { keycode: 32 }));
                    $autocompleteInput.trigger(jQuery.Event('keydown', { keycode: 8 }));
                }
            }
        });
        $autocompleteInput.focus(function () {
            $dropdownButton.trigger("click");
        });
        $(document).mouseup(function(e)
        {
            // $dropdownButton.trigger("click");
            if (!$dropdownContent.is(e.target) && $dropdownContent.has(e.target).length === 0 && !$autocompleteInput.is(e.target))
            {
                hideWizard();
            }
            if (!$dropdownSelectedPlan.is(e.target) && $dropdownSelectedPlan.has(e.target).length === 0 && !$autocompleteInput.is(e.target))
            {
                $dropdownSelectedPlan.hide();
                if ($dropdownContainer.data("selected-plan-option")) {
                    var planDisplayValue = getPlanDisplayed($dropdownContainer.data("selected-plan-option"));
                    $autocompleteInput.val(planDisplayValue);
                    $mainSelect.val($dropdownContainer.data("selected-plan-option").data("plan-id")).trigger('change');
                }
                else if ($firstSelect.val() == "") {
                    // console.log("$dropdownContainer.data('selected-carrier-option')");
                    $autocompleteInput.val('');
                }
            }
        });

        $(".carrier-option").click(function () {
            // console.log(".carrier-option click");
            $dropdownContent.show();
            var $clickedOption = $(this);
            var carrierId = $clickedOption.data("carrier-id");

            if (carrierHasPlans(carrierId)) {
                //go to "select plan" tab
                var selectedCarrierName = getCarrierName(carrierId);

                $selectedCarrierDivs.show();
                $dropdownContainer.data("selected-carrier-id", carrierId);
                $dropdownContainer.data("selected-carrier-name", selectedCarrierName);

                $selectedPlanSpans.text(selectedCarrierName);

                showSelectedIcon($clickedOption);
                filterPlans();
                activatePlanTab();
                $planTabHeader.click();
            } else {
                //select carrier and close dialog
                $mainSelect.val("carrier-" + carrierId).trigger('change');
                $dropdownContainer.data("selected-carrier-option", $clickedOption);
                hideWizard();
            }
        });

        $(".plan-option").click(function () {
            var $clickedOption = $(this);
            selectPlan($clickedOption);
        });

        function carrierHasPlans(carrierId) {
            // console.log("carrierHasPlans " + carrierId + " " + $dropdownContainer.attr("id"));
            // console.log($dropdownContainer.find(".plan-option[data-carrier-id='" + carrierId + "']").length);
            return $dropdownContainer.find(".plan-option[data-carrier-id='" + carrierId + "']").length > 0;
        }

        function showSelectedIcon($clickedOption) {
            $dropdownContainer.find("span.check-mark").remove();
            $clickedOption.append('<span class="glyphicon glyphicon-ok check-mark float-right"></span>');
        }

        function clearSelectedData() {
            $carrierTabHeader.click();
            $dropdownContainer.removeData();
            $autocompleteInput.val('');
            $firstSelect.val('').trigger('change');
            $secondSelect.val('').trigger('change');
            $mainSelect.val('').trigger('change');
            $dropdownContainer.find("span.check-mark").remove();
            $selectedPlanSpans.text('');
            $selectedCarrierDivs.hide();
            deactivatePlanTab();
        }

        $(".choose-different-insurance").click(function (event, notOpenWizard) {
            clearSelectedData();
            $dropdownSelectedPlan.hide();
            if (!notOpenWizard) {
                $autocompleteInput.focus();
            }
        });

        $(".clear-plan").click(function () {
            clearSelectedData();
        });

        $mainSelect.on("change", function() {
            //if carrier selected then select first select, clear the second
            //if plan selected then select second select, clear the first
            var selectedValue = this.value;
            var $selectedOption = $mainSelect.find("option[value='" + selectedValue + "']");
            // console.log("$mainSelect.change " + selectedValue +  $selectedOption + $selectedOption.attr("data-carrier-id"));
            if (selectedValue.toLowerCase().includes("carrier-")) {
                $firstSelect.val($selectedOption.attr("data-carrier-id"));
                $secondSelect.val("");
            } else {
                $firstSelect.val("");
                $secondSelect.val($selectedOption.val());
            }

        });

        if ($firstSelect.val() && $firstSelect.val() != "") {
            $dropdownContainer.find("li.carrier-option[data-carrier-id='" + $firstSelect.val() + "']").click();
        } else if ($secondSelect.val() && $secondSelect.val() != "") {
            $dropdownContainer.find("li.plan-option[data-plan-id='" + $secondSelect.val() + "']").click();
        }

        function hideWizard() {
            // console.log("hideWizard " + $firstSelect.val());
            var firstSelectValue = $firstSelect.val();
            if ($firstSelect.val() != "") {
                var $selectedFirstOption = $firstSelect.find("option[value='" + firstSelectValue + "']");
                // console.log("$selectedFirstOption " + $selectedFirstOption.text());
                $autocompleteInput.val($selectedFirstOption.text());
            }
            $dropdownContent.hide();
        }

        function getCarrierName(carrierId) {
            return $dropdownContent.find("li[data-carrier-id='" + carrierId + "']").first().text();
        }

        function dropDownSelectedPlan() {
            $dropdownContainer.find(".selected-carrier-text").text('');
            $dropdownContainer.find(".selected-plan-text").text(getPlanDisplayed( getSelectedPlanId()));
        }

        function dropDownSelectedCarrier() {
            $dropdownContainer.find(".selected-plan-text").text('');
            $dropdownContainer.find(".selected-carrier-text").text($dropdownContainer.data("selected-carrier-option").text());
        }

        function getPlanDisplayed($clickedOption) {
            var carrierId = $clickedOption.data("carrier-id");
            var $carrierLiOption = $dropdownContainer.find("li.carrier-option[data-carrier-id='" + carrierId + "']").first();
            return $.trim($carrierLiOption.text()) + ", " + $.trim($clickedOption.text());
        }

        function selectPlan($clickedOption) {
            // console.log("selectPlan");
            var planId = $clickedOption.data("plan-id");
            $mainSelect.val(planId).trigger('change');
            var planDisplayValue = getPlanDisplayed($clickedOption);
            $autocompleteInput.val(planDisplayValue);
            $dropdownContainer.data("selected-plan-option", $clickedOption);
            hideWizard();
        }

        function getSelectedPlanId() {
            var result =  $dropdownContainer.data("selected-plan-option");
            if (typeof result === "undefined") {
                return -1;
            } else {
                return result;
            }
        }

        function getSelectedCarrierId() {
            var result =  $dropdownContainer.data("selected-carrier-id");
            if (typeof result === "undefined") {
                return -1;
            } else {
                return result;
            }
        }

        function getSelectedCarrierName() {
            var result =  $dropdownContainer.data("selected-carrier-name");
            if (typeof result === "undefined") {
                return -1;
            } else {
                return result;
            }
        }

        function filterPlans() {
            // console.log("filterPlans getSelectedCarrierId() = " + getSelectedCarrierId());
            $planTabContent.find(".carrier-plans-li-container").each(function() {
                var $this = $(this);
                if ($this.data("carrier-id") == getSelectedCarrierId()) {
                    $this.show();
                } else {
                    $this.hide();
                }
            });
            $planTabContent.find(".plan-option").each(function() {
                var $this = $(this);
                if ($this.data("carrier-id") === getSelectedCarrierId() ) {
                    $this.show();
                } else {
                    $this.hide();
                }
            });
            $planTabContent.find(".first-letter-title").each(function() {
                var $liFirstLetterTitle = $(this);
                var letter = $liFirstLetterTitle.data("first-letter");
                var countOptionsWithLetter = $planTabContent.find(".plan-option[data-first-letter='" + letter + "']").length;
                if (countOptionsWithLetter > 0) {
                    $liFirstLetterTitle.show();
                } else {
                    $liFirstLetterTitle.hide();
                }
            });
        }

        function activatePlanTab() {
            $planTabHeader.attr("data-toggle", "tab");
            $liPlan.removeClass("not-active");
        }

        function deactivatePlanTab() {
            $planTabHeader.removeAttr("data-toggle");
            $liPlan.addClass("not-active");
        }

    });
});
